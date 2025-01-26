User Service Project

This project is a User Service built with Spring Boot. It allows users to register themselves, fetch their details by ID, update their details, and delete their account. Additionally, it provides APIs to fetch products from an external API and place orders.

Table of Contents

1.Prerequisites

2.Setup Instructions

3.Project Structure

4.API Endpoints

5.Sample Requests

------------------------------------------------------------------------------------------------------------------------------------------------------------------

Prerequisites:

Before running the project, ensure you have the following installed on your system:

-> MySQL Workbench: For database management.

-> Redis: For caching user and product data.

-> Java Development Kit (JDK): Version 17 or higher.

-> Maven: For building the project.

------------------------------------------------------------------------------------------------------------------------------------------------------------------
Setup Instructions:

1. Clone the Repository

2. Set Up MySQL

   -> Open MySQL Workbench and create a database named "users"

   -> Update the application.properties file with your MySQL username and password:

       spring.datasource.url=jdbc:mysql://localhost:3306/users

       spring.datasource.username=your-username

       spring.datasource.password=your-password

3. Set Up Redis:

   -> Install Redis on your system.

   -> Start the Redis server:

   commands : 

          sudo service redis-server start

          redis-cli

          monitor

4. Run the Project

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Code architechture :


    src

    ── main

    ├── java
    │   └── com
    │       └── userservice
    │           ├── config
    │           │   ├── AppConfig.java
    │           │   └── RedisConfig.java
    │           ├── controller
    │           │   └── UserController.java
    │           ├── exception
    │           │   ├── ProductException.java
    │           │   ├── UserException.java
    │           │   └── UserServiceException.java
    │           ├── repository
    │           │   ├── OrderRepository.java
    │           │   └── UserRepository.java
    │           ├── service
    │           │   ├── OrderService.java
    │           │   ├── ProductService.java
    │           │   ├── RedisService.java
    │           │   └── UserService.java
    │           ├── user
    │           │   └── UserServiceApplication.java
    └── resources
        ├── static
        ├── templates
        └── application.properties

------------------------------------------------------------------------------------------------------------------------------------------------------------------

Features Implemented

1. MySQL Database
   
   -> Used to store user and order data.

   -> User table stores user details like id, name, and email.

   -> Order table stores order details like id, userId, productId, quantity, and orderAmount.

3. Redis Caching
 
   -> Used to cache frequently accessed data (e.g., user details and product details).

   ->Improves performance by reducing database calls.

   ->Cached data has a TTL (Time-to-Live) of 3000 seconds.


3. Resilience4j (Retry and Circuit Breaker)
   
   -> Retry Mechanism: Retries failed API calls to the external product API.


   -> Circuit Breaker: Prevents cascading failures by opening the circuit when the external API is unavailable.


   -> Fallback methods are implemented to handle failures gracefully.


4. ExecutorService for Asynchronous Processing

   -> Used in the OrderService to process orders asynchronously.


   -> Improves performance by offloading order processing to a separate thread.


5. External API Integration
   
   -> Fetches product data from the open-source API: https://dummyjson.com/products.


   -> Filters products based on rating > 3.5 and stock > 0.


6. Order Flow
   
   -> When a user places an order:


    -> The system checks if the user exists in the database.


   -> Fetches the product details from the external API.


   -> Validates the product stock.


   -> If the stock is sufficient, the order is processed asynchronously using ExecutorService.


    -> The order details are saved in the database and returned to the user.

