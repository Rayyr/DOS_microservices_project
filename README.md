# Distributed Operating Systems lab2 : Turning the Bazar into an Amazon: Replication, Caching and Consistency 
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
- Data Higher level validation techniques 
<br></br>

## Main distributed systems concepts

- Service replication(Fault tolerance)
- Load balancing
- Cache management
- Cache invalidation
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
so we create a container (Image) for each tier as follow : FrontTier , BookTier1, BookTier2 , OrderTier1, OrderTier2 using **DOCKERFILE** for each and **docker-compose.yml** for the complete project.
<br></br>

## 🎯 Purpose

This project demonstrates practical skills in backend development, containerization, and database integration, making it suitable for learning, experimentation, and real-world application deployment.
<br></br>

## 🏗️ System Architecture

The project follows a multi-tier (microservices-style) architecture, where each tier is containerized independently using Docker. This design improves scalability, maintainability, and separation of concerns.

The system is composed of three main services:

- FrontTier → Handles client interaction  
- BookTier → Manages book-related operations **(2 replicas)**  
- OrderTier → Handles order processing **(2 replicas)**  

Each service runs in its own container and communicates via HTTP APIs
<p align="left">
<img width="600" height="600" alt="image" src="https://github.com/user-attachments/assets/3b65b455-268e-4ecc-b33a-d32d1687364f" />
</p>

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

## 🐳 Containerization Strategy

Each tier is packaged as a separate Docker image using its own Dockerfile.

🔹 Images
- front-tier-image
- book-tier1-image
- book-tier2-image
- order-tier1-image
- order-tier2-image

🔹 Why Separate Containers?
- Independent deployment
- Better scalability (scale specific services only)
- Fault isolation
- Cleaner architecture
<br></br>

# Ports Configuration


| Service | Port | Description |
|---|---|---|
| Front Tier | `4566` | API Gateway, Load Balancer, and Cache Layer |
| Book Tier 1 | `4560` | Catalog service replica 1 |
| Book Tier 2 | `4564` | Catalog service replica 2 |
| Order Tier 1 | `4561` | Order service replica 1 |
| Order Tier 2 | `4563` | Order service replica 2 |


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
cd DOS_microservices_project-lab2
```


## Build and Run the System

Run the following command from the root project directory from WSl :

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
| Book Tier 1 | `4560` |
| Book Tier 2 | `4564` |
| Order Tier 1 | `4561` |
| Order Tier 2 | `4563` |


## Accessing the System

Clients should communicate only with the **Front Tier**.

Base URL:

```text
http://localhost:4566
```


# Caching System

The system implements an in-memory(using map DS) caching mechanism inside the Front Tier to **reduce response time, minimize database access, and improve overall system performance.**
The Front Tier stores frequently requested responses and returns them directly without contacting backend services whenever possible.
<br></br>

 **Read Strategies (How Data is Retrieved)** we used the following approach :
 Cache-Aside (Lazy Loading): The application checks the cache first. If the data isn't there, it fetches it from the database, writes it to the cache, and returns it. This allows the cache to only hold requested data.
<br></br>
**Write Strategies (How Data is Persisted)** we used the following approach :  
Write-Around: Data is written directly to the database, bypassing the cache. The cache is only populated when the data is subsequently read thats why we have invalidation functionality once the data is n=being modified ( or updated).
 <br></br>
## Implemented Caches
 **Note:**  
 Caching is implemented only for **read data operations** such as:**GET**  as discueed in the previous section . 

```java
private static Map<String,String> bookSearchTopicCache;
private static Map<String,String> bookSearchInfoCache;
private static Map<String,String> orderCache;
```

### Cache Responsibilities

| Cache | Purpose |
|---|---|
| `bookSearchTopicCache` | Stores responses for topic-based book searches |
| `bookSearchInfoCache` | Stores responses for retrieving book information |
| `orderCache` | Stores responses for retrieving orders |


## Cache Workflow

### Cache Hit

When a request already exists in cache:

1. Front Tier checks the cache using `containsKey()`
2. Cached response is returned immediately
3. No communication with backend tiers occurs
4. Response time is significantly reduced
 

### Cache Miss

When requested data does not exist in cache:

1. Front Tier forwards request to backend service
2. Backend processes request and returns response
3. Front Tier stores response in cache
4. Response is returned to client


## Cache Size Management

Each cache has a maximum size of:

```text
10 entries
```

When cache becomes full:

- The oldest cached entry is removed
- New response is inserted

 

The implemented replacement policy(eviction) is:

```text
FIFO (First In First Out) or LRU algorithm
```


## Cache Invalidation

To maintain data consistency, related cache entries are invalidated whenever data is modified(write data operation).

This prevents stale data from being returned to clients.

### Book Update Operations

The following operations invalidate related caches:

- Update cost
- Increase quantity
- Delete book
- Add new book
```java
	private static void invalidateBookCaches(String bookId) {

	    bookSearchInfoCache.remove("/books/info/" + bookId);

	    // simpler approach
	    bookSearchTopicCache.clear();
	}
```

### Order Operations

When a new order is created the order cache will be invalidate,this ensures that future requests will retrieve the new added orders .
```java
private static void invalidateOrderCache() {

	    orderCache.clear();
	}
```

## Response Time Measurement

Response time is measured using:

```java
long start = System.nanoTime();

/* request processing */

long end = System.nanoTime();

double responseTime =
    (end - start) / 1000000.0;
```

## Performance Improvement
Caching significantly improves system performance.

***Note***: **all details(tables+discussions) can be found here:** **lab2/docs/** 

Benefits of caching include:

- Faster response times
- Reduced database access
- Reduced backend workload
- Reduced network communication
- Improved scalability
- Better user experience


## Data Consistency Strategy

The project uses a:

```text
Write Invalidate Strategy
```

Whenever data changes:

- Related cache entries are removed
- Future requests fetch updated data from backend services

This ensures consistency between:

- Cache
- Backend services
- Database

<br></br>
# Data validation

The Front Tier performs input validation before forwarding requests to backend services.  
This helps prevent invalid data, improves system reliability, and reduces unnecessary database operations.


# Load Balancing

The system implements a simple load balancing mechanism inside the Front Tier to distribute incoming requests across replicated backend services.

Load balancing improves:

- Scalability
- Fault tolerance
- Resource utilization
- System availability


## Replicated Services

The system contains replicated backend tiers:

| Service | Replicas |
|---|---|
| Book Tier | `booktier1` and `booktier2` |
| Order Tier | `ordertier1` and `ordertier2` |


## Load Balancing Strategy

The Front Tier uses a:

```text
Round Robin Load Balancing Strategy
```

Requests are alternated between replicas sequentially.

Example:

```text
Request 1 -> BookTier1
Request 2 -> BookTier2
Request 3 -> BookTier1
Request 4 -> BookTier2
```

## Book Tier Load Balancing

The Front Tier distributes book-related requests between:

- `booktier1`
- `booktier2`

Example:

```java
if(whichBookTier == false) {

    url = new URL(
        "http://booktier:4560/search/" +
        encodedTopic
    );

    whichBookTier = true;
}
else {

    url = new URL(
        "http://booktier2:4564/search/" +
        encodedTopic
    );

    whichBookTier = false;
}
```

## Order Tier Load Balancing

Order-related requests are distributed between:

- `ordertier1`
- `ordertier2`

Example:

```java
if(whichOrderTier == false) {

    url = new URL(
        "http://ordertier:4561/getOrders"
    );

    whichOrderTier = true;
}
else {

    url = new URL(
        "http://ordertier2:4563/getOrders"
    );

    whichOrderTier = false;
}
```

## Front Tier Responsibility

The Front Tier acts as:

- API Gateway
- Request Router
- Load Balancer

Responsibilities include:

- Receiving client requests
- Selecting backend replica
- Forwarding requests
- Returning responses to clients


## ⚙️ Docker Compose

The entire system is orchestrated using **docker-compose** `docker-compose up --build`, which:

- Builds all service images
- Runs containers together
- Handles networking between services
- Simplifies startup with a single command
<br></br>

## 🔗 Service Communication
- FrontTier → BookTier1/2 (for book data)
- FrontTier → OrderTier1/2 (for order operations)

All communication is done via REST APIs over HTTP inside the Docker network.
<br></br>

## 🔗 API Documentation
You can find there a set of cases for each request with the corrosponding response . 
https://documenter.getpostman.com/view/53199814/2sBXiomA5e
<br></br>

## 👨‍🏫 Supervised By
- Dr Samer Arandi
  <br></br>
  
## ✨ Team members
- Raya Khasati
- Anoud Abdusalam 
