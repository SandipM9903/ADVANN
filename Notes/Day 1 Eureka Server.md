# **ADVANN**





**\*\*\*\* Why we need Eureka Server?**

**Ans: In microservices, you may have many services like:**



* **user-service**
* **order-service**
* **payment-service**
* **notification-service**



**Each service runs on different ports and IPs, and they can change anytime (because of scaling, deployment, docker, kubernetes).**

**So the main problem is:**

**❓ How will one service know where another service is running?**

**✅ Eureka Server solves this problem**

**Eureka Server works like a Service Registry / Phonebook**

* **All microservices register themselves in Eureka Server**
* **Eureka stores their service name + IP + port**
* **When one service wants to call another, it asks Eureka.**



**🔥 Example**

**Instead of calling like this:**



**http://localhost:8082/payment**



**We can call like:**



**http://PAYMENT-SERVICE/payment**



**Eureka will automatically map PAYMENT-SERVICE to the correct running instance.**



**✅ Small Note:**

**Eureka Server is used for service discovery in microservices. It acts like a registry/phonebook where all microservices register their name, IP, and port, so other services can find and communicate with them without hardcoding URLs.**

================================================================================



#### **🚀 DAY 1 TASKS (Agile Scrum Style)**



###### **✅ User Story (Day 1)**



**As a developer, I want a Service Registry (Eureka Server) so that all microservices can register and communicate dynamically.**





###### **✅ Step-by-Step Day 1 Work**



🟦 Step 1: Create Main Project Folder



For Example :

ShopSphere-Microservices/

   service-registry/

   api-gateway/

   auth-service/

   product-service/

   order-service/

   payment-service/

   notification-service/

================================================================================



🟦 Step 2: Create Eureka Server Project (Spring Initializr)



Go to Spring Initializr and select:



Project Setup



Project: Maven



Language: Java



Spring Boot: latest stable



Group: com.shopsphere



Artifact: service-registry



Name: service-registry



Packaging: Jar



Java: 17



Add Dependencies:



✅ Spring Web

✅ Eureka Server

================================================================================



🟦 Step 3: Add Eureka Server Annotation



Open your main class and update like this:

package com.shopsphere.serviceregistry;



import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;



@SpringBootApplication

**@EnableEurekaServer**

public class ServiceRegistryApplication {



    public static void main(String\[] args) {

        SpringApplication.run(ServiceRegistryApplication.class, args);

    }

}

================================================================================



🟦 Step 4: Configure application.yml



server:

  port: 8761



spring:

  application:

    name: eureka-service-registry

eureka:

  client:

    register-with-eureka: false

    fetch-registry: false

Why register-with-eureka: false and fetch-registry: false are false?

Because register-with-eureka=false means Eureka Server will not register itself as a client (so it will not appear as a service in the dashboard).

fetch-registry=false means Eureka Server will not fetch the service list because it is the main registry itself.

================================================================================



🟦 Step 5: Run the Project

🟦 Step 6: Verify in Browser

 	[http://localhost:8761](http://localhost:8761)

You should see Eureka Dashboard.

