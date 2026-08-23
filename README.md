# Last Mile Delivery Tracker

A backend-driven last-mile delivery management platform for managing orders, delivery agents, pricing, tracking, failed deliveries, rescheduling, notifications, and role-based access.

## Overview

The Last Mile Delivery Tracker automates key logistics operations:

- Customer registration and authentication
- Order creation with automatic charge calculation
- Zone-based pricing
- B2B/B2C rate cards
- COD surcharge calculation
- Delivery-agent assignment
- Order status tracking
- Immutable tracking history
- Failed-delivery handling
- Customer rescheduling
- Automatic agent reassignment
- Email notifications
- Role-based authorization using JWT

The system calculates delivery charges using package dimensions, actual weight, pickup/drop zones, order type, and payment type.

---

## Features

### Customer

- Register and log in
- Create delivery orders
- View orders
- View order tracking history
- Receive status-change notifications
- Reschedule failed deliveries

### Delivery Agent

- View assigned deliveries
- Update delivery status
- Maintain the delivery lifecycle:
  - PICKED_UP
  - IN_TRANSIT
  - OUT_FOR_DELIVERY
  - DELIVERED
  - FAILED

### Admin

- Manage operational data
- Configure delivery zones and areas
- Configure rate cards
- View and filter orders
- Assign delivery agents
- Override delivery status

---

## Technology Stack

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- Maven

### Database

- PostgreSQL

### Email

- Spring Mail
- Gmail SMTP

### Development Tools

- Git
- GitHub
- IntelliJ IDEA / VS Code
- cURL

---

## Project Structure

```text
Last-Mile-Delivery/
│
├── backend/
│   └── delivery-backend/
│       ├── src/
│       │   └── main/
│       │       ├── java/com/lastmile/delivery/
│       │       │
│       │       └── resources/
│       │
│       └── pom.xml
│
├── docs/
│
├── .env.example
├── .gitignore
└── README.md
```

The backend follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL Database
```

Security is implemented through:

```text
Client
  ↓
JWT
  ↓
JwtAuthenticationFilter
  ↓
Spring Security
  ↓
Controller
```

---

# Rate Calculation Engine

The delivery charge is calculated dynamically using the configured rate cards.

## 1. Volumetric Weight

Volumetric weight is calculated using:

```text
Volumetric Weight = (Length × Breadth × Height) / 5000
```

Dimensions are supplied with the order.

## 2. Billable Weight

The system uses the higher of actual weight and volumetric weight:

```text
Billable Weight = max(Actual Weight, Volumetric Weight)
```

## 3. Zone Detection

Pickup and drop areas belong to configured zones.

The system determines:

```text
Pickup Area → Pickup Zone
Drop Area   → Drop Zone
```

The appropriate rate is then selected based on the relationship between the zones.

## 4. Rate Card

The rate card depends on:

- Pickup zone
- Drop zone
- Order type
- Billable weight

Supported order types:

```text
B2B
B2C
```

Rates are stored in the database rather than hardcoded in application logic.

## 5. COD Surcharge

For COD orders, the configured COD surcharge is added to the base delivery charge.

```text
Total Charge =
    Base Delivery Charge
    + COD Surcharge
```

For prepaid orders, the COD surcharge is not applied.

---

# Auto-Assignment

The system supports automatic assignment of delivery agents.

The assignment process considers:

1. Pickup zone
2. Agent availability
3. Agent location
4. Distance from the pickup location

The nearest available suitable delivery agent is selected.

An agent becomes unavailable while handling an assigned delivery and can become available again after the delivery lifecycle is completed.

If no suitable agent is available, the system returns an appropriate error instead of creating an invalid assignment.

---

# Order Status Lifecycle

Orders follow a delivery lifecycle:

```text
CREATED
   ↓
PICKED_UP
   ↓
IN_TRANSIT
   ↓
OUT_FOR_DELIVERY
   ↓
DELIVERED
```

A delivery can also enter:

```text
FAILED
```

Each status transition is stored in tracking history along with:

- Order
- Status
- Actor
- Timestamp

This provides a complete tracking timeline for each order.

---

# Failed Delivery & Rescheduling

When a delivery attempt fails:

```text
Order
  ↓
FAILED
  ↓
Customer notified
  ↓
Customer requests reschedule
  ↓
New delivery date recorded
  ↓
Delivery agent reassigned
  ↓
New delivery attempt
```

The reschedule record stores:

- Order
- Previous attempt date
- New delivery date
- Reason
- Creation timestamp

The system also attempts to automatically assign an available delivery agent for the new attempt.

---

# Notifications

Email notifications are triggered when the order status changes.

The notification service uses:

```text
Spring Mail
      ↓
Gmail SMTP
      ↓
Customer Email
```

Required SMTP credentials are supplied through environment variables and are not stored directly in source code.

---

# Authentication & Authorization

Authentication uses JWT tokens.

After successful login, the API returns a JWT containing the authenticated user's identity and role.

Example roles:

```text
CUSTOMER
DELIVERY_AGENT
ADMIN
```

Spring Security uses the JWT authentication filter to validate the token and establish the user's security context.

Role-based endpoint protection includes:

| Endpoint | CUSTOMER | DELIVERY_AGENT | ADMIN |
|---|---:|---:|---:|
| `/api/orders/**` | ✓ | ✓ | ✓ |
| `/api/tracking/**` | ✓ | ✓ | ✓ |
| `/api/reschedules/**` | ✓ | - | ✓ |
| `/api/assignments/**` | - | ✓ | ✓ |
| `/api/rates/**` | ✓ | - | ✓ |

---

# API Documentation

Base URL:

```text
http://localhost:8080
```

## Authentication

### Login

```http
POST /api/auth/login
```

Example:

```bash
curl -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d "{\"email\":\"customer@test.com\",\"password\":\"password123\"}"
```

Response:

```json
{
  "token": "<JWT_TOKEN>",
  "role": "CUSTOMER"
}
```

Use the returned token for protected endpoints:

```text
Authorization: Bearer <JWT_TOKEN>
```

---

## Orders

### Get All Orders

```http
GET /api/orders
```

Example:

```bash
curl -i http://localhost:8080/api/orders \
-H "Authorization: Bearer <JWT_TOKEN>"
```

### Filter Orders

```http
GET /api/orders/filter
```

Optional parameters:

```text
status
zoneId
agentId
```

Example:

```text
/api/orders/filter?status=FAILED
```

### Create Order

```http
POST /api/orders
```

The request contains:

- Customer ID
- Pickup area
- Drop area
- Pickup address
- Drop address
- Package dimensions
- Actual weight
- Order type
- Payment type

The backend calculates the delivery charge automatically.

---

# Tracking APIs

### Get Tracking History

```http
GET /api/tracking/{orderId}
```

Example:

```bash
curl -i http://localhost:8080/api/tracking/1 \
-H "Authorization: Bearer <JWT_TOKEN>"
```

The response contains the complete tracking timeline including status, actor, and timestamp.

---

# Rescheduling API

### Reschedule a Failed Order

```http
POST /api/reschedules/{orderId}
```

Parameters:

```text
newDeliveryDate
reason
actorId
```

Example:

```bash
curl -i -X POST \
"http://localhost:8080/api/reschedules/1?newDeliveryDate=2026-08-27&reason=Customer%20requested%20reschedule&actorId=4" \
-H "Authorization: Bearer <JWT_TOKEN>"
```

---

# Database Schema

The application uses PostgreSQL.

The main entities include:

```text
users
delivery_agents
zones
areas
orders
rate_cards
tracking_history
reschedules
notifications
assignments
```

## Main Relationships

```text
User
 ├── Customer
 └── Delivery Agent

Zone
 └── Areas

Order
 ├── Customer
 ├── Pickup Area
 ├── Drop Area
 └── Delivery Agent

Order
 ├── Tracking History
 └── Reschedules

Rate Card
 ├── Pickup Zone
 └── Drop Zone
```

The database design keeps pricing configuration, operational entities, order history, and delivery assignment data separated.

---

# Environment Variables

Create environment variables before starting the backend.

Example `.env.example`:

```env
DB_URL=jdbc:postgresql://localhost:5432/lastmile_delivery
DB_USERNAME=postgres
DB_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret

MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```

---

# Local Setup

## 1. Clone the repository

```bash
git clone https://github.com/arshavsuman20/Last-Mile-Delivery.git
cd Last-Mile-Delivery
```

## 2. Configure PostgreSQL

Create the database:

```sql
CREATE DATABASE lastmile_delivery;
```

Make sure PostgreSQL is running.

## 3. Configure environment variables

Set:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
MAIL_USERNAME
MAIL_PASSWORD
```

The Gmail password should be a Gmail App Password when SMTP authentication is enabled.

## 4. Enter the backend

```bash
cd backend/delivery-backend
```

## 5. Build the project

```bash
mvn clean package
```

## 6. Run the application

```bash
mvn spring-boot:run
```

The backend starts on:

```text
http://localhost:8080
```

---

# Testing

The APIs were tested using cURL.

Important verified flows include:

### Authentication

```text
Login → JWT token generated
```

### Order retrieval

```text
JWT CUSTOMER → GET /api/orders → 200 OK
```

### Tracking

```text
JWT CUSTOMER → GET /api/tracking/{orderId} → 200 OK
```

### Rescheduling

```text
Failed Order
    ↓
POST /api/reschedules/{orderId}
    ↓
Reschedule record created
    ↓
Agent reassignment attempted
```

### Email Notification

Status changes trigger customer email notifications through Gmail SMTP.

---

# System Design

The platform uses a layered Spring Boot architecture with PostgreSQL as the persistence layer. Controllers expose REST APIs, services contain business logic, and repositories provide database access through Spring Data JPA.

The rate calculation engine first resolves pickup and drop areas to their configured zones. It calculates volumetric weight using `(L × B × H) / 5000` and selects the higher value between actual and volumetric weight as the billable weight. The appropriate rate card is then selected using the pickup zone, drop zone, order type, and billable weight. COD surcharge is added only when the payment type is COD. This keeps pricing configurable through database rate cards instead of hardcoding values.

Auto-assignment uses the pickup zone, agent availability, and agent location to identify a suitable delivery agent. The nearest available agent is selected for an order. Agent availability is updated according to the delivery assignment lifecycle. If no suitable agent is available, the operation fails safely rather than creating an invalid assignment.

Order tracking is represented as an append-only history of status transitions. Every status update records the order, status, actor, and timestamp. This provides a complete timeline while preserving historical delivery events.

Failed deliveries transition the order to `FAILED`, trigger customer notification, and allow the customer to request a new delivery date with a reason. A reschedule record preserves the previous attempt and new delivery date. The system then attempts to assign an available delivery agent for the new attempt.

JWT authentication protects the REST API and Spring Security enforces role-based access for customers, delivery agents, and administrators.

---

# Future Improvements

Potential extensions include:

- SMS notification integration
- Real-time GPS tracking
- Map-based distance calculation
- Redis caching for rate cards
- Asynchronous notification processing
- Docker deployment
- Cloud deployment
- Frontend dashboard
- Automated integration testing
- CI/CD pipeline

---

# Author

**Arshav Suman**

B.Tech Computer Science & Engineering

GitHub:

https://github.com/arshavsuman20