package com.advann.order_service.service.serviceImpl;

import com.advann.order_service.client.CartClient;
import com.advann.order_service.client.ProductClient;
import com.advann.order_service.dto.*;
import com.advann.order_service.entity.Order;
import com.advann.order_service.entity.OrderItem;
import com.advann.order_service.enums.OrderStatus;
import com.advann.order_service.enums.PaymentStatus;
import com.advann.order_service.exception.ResourceNotFoundException;
import com.advann.order_service.payload.ApiResponse;
import com.advann.order_service.repository.OrderItemRepository;
import com.advann.order_service.repository.OrderRepository;
import com.advann.order_service.service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final ModelMapper modelMapper;

    @Override
    public OrderResponseDto placeOrder(Long userId) {

        ApiResponse<CartResponseDto> cartResponse = cartClient.getCartByUserId(userId);

        if (cartResponse == null || cartResponse.getData() == null || cartResponse.getData().getItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty for userId: " + userId);
        }

        CartResponseDto cart = cartResponse.getData();

        // Create Order
        Order order = Order.builder()
                .userId(userId)
                .orderStatus(OrderStatus.PLACED)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(cart.getGrandTotal())
                .build();

        order = orderRepository.save(order);

        // Save Order Items
        for (CartItemResponseDto cartItem : cart.getItems()) {

            ApiResponse<ProductResponseDto> productResponse =
                    productClient.getProductById(cartItem.getProductId());

            if (productResponse == null || productResponse.getData() == null) {
                throw new ResourceNotFoundException("Product not found with id: " + cartItem.getProductId());
            }

            ProductResponseDto product = productResponse.getData();

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            // Reduce Stock in product-service
            productClient.reduceStock(cartItem.getProductId(), cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(cartItem.getProductId())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();

            orderItemRepository.save(orderItem);
        }

        // Clear cart after order placed
        cartClient.clearCart(userId);

        return getOrderById(order.getId());
    }

    @Override
    public OrderResponseDto getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        List<OrderItemResponseDto> responseItems = orderItems.stream()
                .map(item -> {

                    ApiResponse<ProductResponseDto> productResponse =
                            productClient.getProductById(item.getProductId());

                    ProductResponseDto product = productResponse.getData();

                    OrderItemResponseDto dto = OrderItemResponseDto.builder()
                            .productId(item.getProductId())
                            .productName(product != null ? product.getName() : null)
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .totalPrice(item.getTotalPrice())
                            .build();

                    return dto;
                })
                .toList();

        return OrderResponseDto.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderStatus(order.getOrderStatus())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .items(responseItems)
                .createdAt(order.getCreatedAt())
                .build();
    }

    @Override
    public List<OrderResponseDto> getOrdersByUserId(Long userId) {

        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(order -> getOrderById(order.getId()))
                .toList();
    }

    @Override
    public OrderResponseDto cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setOrderStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);

        return getOrderById(orderId);
    }
}