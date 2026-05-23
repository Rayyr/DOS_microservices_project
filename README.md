# Distributed Operating Systems Lab 1: Bazar.com: A Multi-tier Online Book Store
<br></br>
**Note** : we have an indvisual branch for each lab named as ***lab1 & lab2*** .
<br></br>
## 📖 Project Overview
This project is a containerized backend application built using **Java and Maven**, designed to provide a scalable and **modular API-based system**. The application follows a **multi-tier architecture and integrates with an SQLite database** for lightweight and efficient data storage.
**Docker** is used to simplify deployment and ensure consistency across environments by packaging the application and its dependencies into isolated containers for each tier which they communicate among each others via HTTP calls .
The system handles **HTTP requests**, processes data, and returns responses in **JSON or plain text format**, making it suitable for web services and microservice-based architectures.

<br></br>
## 🚀 Key Features

- RESTful API implementation
- Multi-tier architecture design
- SQLite database integration
- Docker-based containerization
- Spark Java framework for API
- Maven build and dependency management
- JSON and text response handling

<br></br>
## 🛠️ Technologies Used

- Java (JDK 17)
- Maven
- SQLite
- Docker Desktop
- Windows Subsystem Linux with Ubuntu(24.04.4 LTS) distribution 
- Spark Java REST APIs
- Postman for API testing

<br></br>

## 📦 Deployment

The application is built using Maven and deployed inside Docker containers using a multi-stage Dockerfile for optimized image size and performance.
so we create a container (Image) for each tier as follow : FrontTier , BookTier , OrderTier using **DOCKERFILE** for each and **docker-compose.yml** for the complete project.

<br></br>

## 🎯 Purpose

This project demonstrates practical skills in backend development, containerization, and database integration, making it suitable for learning, experimentation, and real-world application deployment.


<br></br>
## 🏗️ System Architecture

The project follows a multi-tier (microservices-style) architecture, where each tier is containerized independently using Docker. This design improves scalability, maintainability, and separation of concerns.

The system is composed of three main services:

FrontTier → Handles client interaction
BookTier → Manages book-related operations
OrderTier → Handles order processing

Each service runs in its own container and communicates via HTTP APIs.
<p align="left">
<img width="600" height="600" alt="597119458-3b65b455-268e-4ecc-b33a-d32d1687364f" src="https://github.com/user-attachments/assets/09694f7a-a5c2-4355-b8b0-b30f2bea6166" /></p>

**🔹 FrontTier (Client Interface Layer)**

Purpose:
Acts as the entry point for users. It receives client requests and forwards them to the appropriate backend service(tier).

Responsibilities:

Handle incoming HTTP requests
Route requests to BookTier or OrderTier
Format and return responses (JSON / plain text)
Basic validation and error handling 
<br></br>
**APIs :** 
- `GET     /books/search/{topic}`                   → Fetch all books related to the assigned topic  
- `GET     /books/info/{id}`                 → Fetch book details based to ID 
- `GET     /orders/getOrders`                → Get all orders
- `POST    /books/addBook`                   → Create a new Book 
- `POST    /orders/makeOrder/{id}/{req_q}`                → Create a new Order 
- `PATCH   /books/updateCost/{id}`           → Update the book cost based to the ID 
- `PATCH   /books/increaseQuantity/{id}`     → Update the book quantity based to the ID 
- `DELETE  /books/deleteBook/{id}`           → Delete book based to ID
<br></br>

**📚 BookTier (Book Management Service)**

Purpose:
Responsible for managing book data and interacting with the SQLite database.

Responsibilities:

Store and retrieve book information
Handle database operations (CRUD)
Provide book-related APIs
<br></br>
**APIs :** 
- `GET     /search/{topic}`                   → Fetch all books related to the assigned topic  
- `GET     /info/{id}`                 → Fetch book details based to ID 
- `POST    /addBook`                   → Create a new Book 
- `PATCH   /updateCost/{id}`           → Update the book cost based to the ID 
- `PATCH   /increaseQuantity/{id}`     → Update the book quantity based to the ID 
- `DELETE  /deleteBook/{id}`           → Delete book based to ID

<br></br>
**🛒 OrderTier (Order Processing Service)**

Purpose:
Handles customer orders and business logic related to purchasing.

Responsibilities:

Create and manage orders
Validate book availability (via BookTier)
Track order status
<br></br>
**APIs :** 
- `GET     /getOrders`                → Get all orders
- `POST    /makeOrder/{id}/{req_q}`                → Create a new Order 

<br></br>



# How to Run the Program

The project is containerized using Docker and Docker Compose.


## Prerequisites

Before running the system, ensure the following are installed:

- Docker
- Docker Compose
- Java JDK
- Maven
- WSL


## Clone the Repository

```bash
git clone https://github.com/Rayyr/DOS_microservices_project.git
cd DOS_microservices_project-lab1
```


## Build and Run the System

Run the following command from the root project directory from WSL :

```bash
docker-compose up --build
```

This command will:

- Build all services
- Create Docker containers
- Start all backend tiers
- Start the Front Tier
- Create the Docker network automatically


## Running Services

After startup, the following services will be available:

| Service | Port |
|---|---|
| Front Tier | `4566` |
| Book Tier  | `4560` |
| Order Tier | `4561` |


## Accessing the System

Clients should communicate only with the **Front Tier**.

Base URL:

```text
http://localhost:4566
```


## 🐳 Containerization Strategy

Each tier is packaged as a separate Docker image using its own Dockerfile.

🔹 Images
- front-tier-image
- book-tier-image
- order-tier-image

🔹 Why Separate Containers?
- Independent deployment
- Better scalability 
- Fault isolation
- Cleaner architecture
<br></br>

## ⚙️ Docker Compose

The entire system is orchestrated using **docker-compose** `docker-compose up --build`, which:

- Builds all service images
- Runs containers together
- Handles networking between services
- Simplifies startup with a single command
<br></br>
## 🔗 Service Communication
- FrontTier → BookTier (for book data)
- FrontTier → OrderTier (for order operations)

All communication is done via REST APIs over HTTP inside the Docker network.
<br></br>
## 🎥 Demo
https://drive.google.com/file/d/1Oe0Fk751SyQG4rRz2JxA_8PW6chphIIe/view?usp=sharing
<br></br>
## 🔗 API Documentation
You can find there a set of cases for each request with the corrosponding response . https://documenter.getpostman.com/view/53199814/2sBXiomA5e
<br></br>
## 👨‍🏫 Supervised By
- Dr Samer Arandi
  <br></br>
## ✨ Team members
- Raya Khasati
- Anoud Abdulsalam 
