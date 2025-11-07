# Seller Dashboard

## Overview
A small seller dashboard that computes weekly metrics and generates alerts based on sales and return rates. The application consists of a **Spring Boot** backend for computing metrics and a **Vue.js 3 + Vite** frontend for displaying data.

## Tech Stack
- **Backend**: Spring Boot, Spring Data JPA, H2 (default), Caffeine caching
- **Frontend**: Vue 3, Vite, Axios

## Setup Instructions

### Backend Setup
1. **Navigate to the backend folder**:
   <pre><code>cd backend</code></pre>

2. **Install dependencies and run the backend**:
   - Run the following commands to build and run the backend:
     <pre><code>./mvnw clean install</code></pre>
     <pre><code>./mvnw spring-boot:run</code></pre>

3. **Access the API**:
   - API endpoint to get seller summary:
     <pre><code>GET http://localhost:8080/api/seller/{id}/summary</code></pre>
   - Example request: 
     <pre><code>GET http://localhost:8080/api/seller/1/summary</code></pre>

4. **H2 Console**:
   - Access the H2 console for querying the in-memory database:
     <pre><code>http://localhost:8080/h2-console</code></pre>
   - JDBC URL: <pre><code>jdbc:h2:mem:testdb</code></pre>

### Frontend Setup
1. **Navigate to the frontend folder**:
   <pre><code>cd frontend</code></pre>

2. **Install frontend dependencies**:
   <pre><code>npm install</code></pre>

3. **Run the frontend**:
   <pre><code>npm run dev</code></pre>
   - Visit the dashboard at <pre><code>http://localhost:5173</code></pre>.

---

## Caching and Alerts

### Caching:
- The **Caffeine cache** is used to cache the seller's summary data for 30 seconds to improve performance.
- **Cache TTL**: 30 seconds (`sellerSummary` cache)
- **How it works**: The first request to fetch the seller's summary will query the database, while subsequent requests within the next 30 seconds will retrieve the data from the cache.

### Alerts:
- The dashboard generates alerts based on the following conditions:
  1. **Sales dropped by more than 30% vs last week**: This compares the current week's revenue to last week's. If the current week's sales are more than 30% lower than the previous week, an alert is generated.
  2. **Return rate > 10%**: If the return rate (i.e., the percentage of returned items) exceeds 10% of the total sales for the current week, an alert is triggered.

### Example Request and Response

#### Request:
<pre><code>
GET http://localhost:8080/api/seller/1/summary
</code></pre>

#### Response:
```json
{
  "totalSales": 120,
  "totalRevenue": 4500.34,
  "returnRate": 0.085,
  "alerts": [
    "Sales dropped by more than 30% vs last week"
  ]
}



## Using PostgreSQL (optional)
- The provided SQL file can be used to test with PostgreSQL.
- This project runs fully using H2 in-memory DB with seeded data.
- Postgres configuration can be added by changing application.properties and running the SQL if desired.

