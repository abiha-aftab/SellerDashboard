# Seller Dashboard — Full Stack Assignment

## Overview
Small seller dashboard that computes weekly metrics and generates alerts.

## Tech
- Backend: Spring Boot, Spring Data JPA, H2 (default), Caffeine caching
- Frontend: Vue 3 + Vite, Axios

## Run backend (H2)
cd backend
./mvnw clean install
./mvnw spring-boot:run

API: GET http://localhost:8080/api/seller/{id}/summary
H2 console: http://localhost:8080/h2-console (JDBC: jdbc:h2:mem:testdb)

## Run frontend
cd frontend
npm install
npm run dev
Open http://localhost:5173

## Caching & Alerts
- Cache: Caffeine cache with TTL=30s for `sellerSummary`.
- Alerts:
  - Sales dropped >30% vs last week (revenue comparison)
  - Return rate >10% (returns/units)
- Optimization: Backend retrieves sales once and computes this-week and last-week metrics in a single-pass loop (O(n)).

## Using PostgreSQL (optional)
- The provided SQL file can be used to test with PostgreSQL.
- This project runs fully using H2 in-memory DB with seeded data.
- Postgres configuration can be added by changing application.properties and running the SQL if desired.

