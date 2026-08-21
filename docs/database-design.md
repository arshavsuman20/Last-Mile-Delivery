# Last Mile Delivery — Database Design

## 1. Overview

The Last Mile Delivery platform uses a relational PostgreSQL database to manage customers, delivery agents, zones, areas, configurable rate cards, orders, assignments, tracking history, rescheduling, and notifications.

The database is designed to support:

* Customer, delivery agent, and admin roles
* Zone and area management
* B2B/B2C rate calculation
* Intra-zone and inter-zone pricing
* COD surcharge
* Delivery agent assignment
* Immutable order tracking history
* Failed delivery and rescheduling
* Customer notifications

## 2. Entities

### User

Stores authentication and role information.

Fields:

* id
* name
* email
* password
* role
* createdAt

Roles:

* CUSTOMER
* DELIVERY_AGENT
* ADMIN

### DeliveryAgent

Stores delivery-agent-specific information.

Fields:

* id
* userId
* available
* latitude
* longitude
* currentZoneId

### Zone

Represents a delivery zone managed by the administrator.

Fields:

* id
* name
* description

### Area

Represents an address/service area belonging to a zone.

Fields:

* id
* name
* zoneId

Relationship:

```text
Zone 1 ──── N Area
```

### RateCard

Stores configurable pricing for B2B and B2C orders.

Fields:

* id
* orderType
* intraZoneRatePerKg
* interZoneRatePerKg
* codSurcharge

The rates are configured by the administrator and are not hardcoded in the application.

### Order

Stores the main delivery order.

Fields:

* id
* customerId
* pickupAddress
* dropAddress
* pickupAreaId
* dropAreaId
* length
* breadth
* height
* actualWeight
* volumetricWeight
* billableWeight
* orderType
* paymentType
* baseCharge
* codSurcharge
* totalCharge
* status
* assignedAgentId
* createdAt
* updatedAt

### TrackingHistory

Stores every order-status change as a separate immutable record.

Fields:

* id
* orderId
* status
* actorId
* timestamp

Example lifecycle:

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

Failed delivery is also recorded in the tracking history.

### Assignment

Stores delivery-agent assignment events.

Fields:

* id
* orderId
* agentId
* assignedAt
* assignmentType

Assignment types:

* MANUAL
* AUTO
* RESCHEDULE

### Reschedule

Stores information about failed-delivery rescheduling.

Fields:

* id
* orderId
* previousAttemptDate
* newDeliveryDate
* reason
* createdAt

### Notification

Stores customer notification records.

Fields:

* id
* orderId
* customerId
* type
* channel
* message
* status
* sentAt

Supported notification channels can include:

* EMAIL
* SMS

## 3. Main Relationships

```text
User
 ├── 1 : 1 ── DeliveryAgent
 └── 1 : N ── Order

Zone
 └── 1 : N ── Area

Order
 ├── N : 1 ── User (Customer)
 ├── N : 1 ── Area (Pickup)
 ├── N : 1 ── Area (Drop)
 ├── N : 1 ── DeliveryAgent
 ├── 1 : N ── TrackingHistory
 ├── 1 : N ── Assignment
 ├── 1 : N ── Reschedule
 └── 1 : N ── Notification

RateCard
 └── configured for B2B/B2C pricing
```

## 4. Rate Calculation

The volumetric weight is calculated as:

```text
Volumetric Weight = (Length × Breadth × Height) / 5000
```

The billable weight is the higher of actual weight and volumetric weight:

```text
Billable Weight = max(Actual Weight, Volumetric Weight)
```

The system detects the pickup and drop zones.

If both addresses belong to the same zone:

```text
Intra-zone rate
```

Otherwise:

```text
Inter-zone rate
```

The appropriate B2B or B2C rate card is selected based on the order type.

For COD orders, the configured COD surcharge is added.

The final charge is therefore based on:

```text
Billable Weight
        ×
Applicable Rate
        +
COD Surcharge (if applicable)
```

The calculated charge is shown before the customer confirms the order.

## 5. Agent Assignment

The system supports:

* Manual assignment by an administrator
* Automatic assignment to the nearest available delivery agent

Agent availability and current location/zone are stored so that the assignment service can select an appropriate available agent.

## 6. Tracking History

Order status changes are never deleted or overwritten from the tracking history.

Each status change creates a new record containing:

* Order
* New status
* Actor
* Timestamp

This provides an immutable tracking timeline for customers and administrators.

## 7. Failed Delivery

When delivery fails:

```text
Failed Delivery
      ↓
Customer Notification
      ↓
Customer Reschedules
      ↓
New Delivery Date Recorded
      ↓
Agent Reassigned
      ↓
New Delivery Attempt
```

The failed attempt and rescheduling information remain available for tracking and auditing.
