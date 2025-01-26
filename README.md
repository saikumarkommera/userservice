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

------------------------------------------------------------------------------------------------------------------------------------------------------------------

