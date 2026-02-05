# 				**ADVANN**

#### **🚀 DAY 1 TASKS (Agile Scrum Style)**



###### **✅ User Story (Day 1)**



**As a developer, I want a Service Registry (Eureka Server) so that all microservices can register and communicate dynamically.**





###### 							**✅ Step-by-Step Day 1 Work**



🟦 Step 1: Create Main Project Folder



For Example :

ShopSphere-Microservices/

&nbsp;  service-registry/

&nbsp;  api-gateway/

&nbsp;  auth-service/

&nbsp;  product-service/

&nbsp;  order-service/

&nbsp;  payment-service/

&nbsp;  notification-service/

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



&nbsp;   public static void main(String\[] args) {

&nbsp;       SpringApplication.run(ServiceRegistryApplication.class, args);

&nbsp;   }

}

================================================================================



🟦 Step 4: Configure application.yml



server:

&nbsp; port: 8761



spring:

&nbsp; application:

&nbsp;   name: eureka-service-registry

eureka:

&nbsp; client:

&nbsp;   register-with-eureka: false

&nbsp;   fetch-registry: false

Why register-with-eureka: false and fetch-registry: false are false? 

Because register-with-eureka=false means Eureka Server will not register itself as a client (so it will not appear as a service in the dashboard).

fetch-registry=false means Eureka Server will not fetch the service list because it is the main registry itself.

================================================================================



🟦 Step 5: Run the Project

🟦 Step 6: Verify in Browser

&nbsp;	[http://localhost:8761](http://localhost:8761)

You should see Eureka Dashboard.

