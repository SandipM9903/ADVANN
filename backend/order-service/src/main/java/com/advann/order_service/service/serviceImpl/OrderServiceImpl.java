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
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public OrderResponseDto placeOrder(Long userId) {

        ApiResponse<CartResponseDto> cartResponse = cartClient.getCartByUserId(userId);

        if (cartResponse == null || cartResponse.getData() == null || cartResponse.getData().getItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty for userId: " + userId);
        }

        CartResponseDto cart = cartResponse.getData();

        // Create Order
        Order order = Order.builder()
                .userId(userId)
                .orderStatus(OrderStatus.CREATED)
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

            if (product.getStock() < cartItem.getQuantity()) {
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
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Order cannot be cancelled at this stage");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        // Restore stock
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : orderItems) {
            productClient.increaseStock(item.getProductId(), item.getQuantity());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return getOrderById(orderId);
    }

    @Override
    public OrderResponseDto updatePaymentStatus(Long orderId, PaymentStatusUpdateRequestDto requestDto) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        order.setPaymentStatus(requestDto.getPaymentStatus());

        if (requestDto.getPaymentStatus() == PaymentStatus.PAID) {
            order.setOrderStatus(OrderStatus.PAID);
        }

        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Payment already processed or invalid state");
        }

        orderRepository.save(order);

        return getOrderById(orderId);
    }

    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id: " + orderId));

        // Prevent invalid transitions
        if (order.getOrderStatus() == OrderStatus.CANCELLED ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new RuntimeException("Order status cannot be changed");
        }

        if (order.getOrderStatus() == OrderStatus.CREATED && newStatus != OrderStatus.PAID) {
            throw new RuntimeException("Order must be PAID first");
        }

        if (order.getOrderStatus() == OrderStatus.PAID && newStatus != OrderStatus.SHIPPED) {
            throw new RuntimeException("Order must be SHIPPED after PAID");
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPED && newStatus != OrderStatus.DELIVERED) {
            throw new RuntimeException("Order must be DELIVERED after SHIPPED");
        }

        order.setOrderStatus(newStatus);
        orderRepository.save(order);

        return getOrderById(orderId);
    }
}