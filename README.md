
# Forex Rate Aggregator Service

**Wiremit Backend Developer Technical Interview – August 2025**

This project is a Spring Boot backend service that aggregates forex rates from multiple public APIs, calculates an average, applies a customer-facing markup, and serves authenticated endpoints for current and historical rates.

---

## Table of Contents

1. [Features](#features)
2. [How to Run the API](#how-to-run-the-api)
3. [Authentication Flow](#authentication-flow)
4. [Rate Aggregation Logic](#rate-aggregation-logic)
5. [Endpoints](#endpoints)
6. [Database Schema](#database-schema)
7. [Testing](#testing)
8. [Design Decisions & Trade-offs](#design-decisions--trade-offs)
9. [Future Improvements](#future-improvements)

---

## Features

- **User Authentication & Authorization:** JWT-based access and refresh tokens.
- **Rate Aggregation:** Fetches rates from three public APIs, calculates averages, applies a 0.10 markup.
- **Outlier Filtering:** Discards rates that deviate more than 5% from the median to improve reliability.
- **Historical Storage:** Persists rates with timestamps for analytics.
- **Hourly Refresh:** Scheduler automatically refreshes rates every hour.
- **Dynamic Currencies:** Admin can add/remove supported currency pairs.
- **Resilience:** Retries API failures with exponential backoff using Resilience4j.
- **Unit & Integration Tests:** Covers service, controllers, and aggregation logic.
- **Docker & Postgres Ready:** Easily deployable in containerized environments.

---

## How to Run the API

### 1. Clone the repo

```bash
git clone https://github.com/<your-username>/forex-aggregator.git
cd forex-aggregator
```

### 2. Run with Maven (H2 in-memory DB for dev)

```bash
mvn spring-boot:run
```

### 3. Run with Docker & Postgres (production-like)

```bash
docker compose up --build
```

### 4. Run Tests

```bash
mvn clean test
```

---

## Authentication Flow

1. **Signup**: `POST /signup`

```json
{
  "username": "user1",
  "password": "Password123!"
}
```

2. **Login**: `POST /login`

```json
{
  "username": "user1",
  "password": "Password123!"
}
```

- Returns **Access Token** (JWT, valid for 15 min)
- Returns **Refresh Token** (valid for 7 days)

3. **Access Secured Endpoints**:

```http
GET /rates
Authorization: Bearer <access_token>
```

4. **Token Refresh**: `POST /token/refresh`

```json
{
  "refreshToken": "<refresh_token>"
}
```

- Returns a new access token.

---

## Rate Aggregation Logic

1. **Fetch rates from 3 APIs**:
    - `exchangerate.host`
    - `frankfurter.app`
    - `open.er-api.com`

2. **Filter outliers**:
    - Remove rates deviating >5% from the median.

3. **Compute average**:

\[
ext{average} = rac{	ext{sum of valid rates}}{	ext{count of valid rates}}
\]

4. **Apply markup**:

\[
ext{customer rate} = 	ext{average} + 0.10
\]

5. **Persist results**:
    - Store rates with timestamp in DB (`rates` table)
    - Historical rates are stored for analytics endpoints.

---

## Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/signup` | POST | Register a new user |
| `/login` | POST | Authenticate user & get JWT |
| `/token/refresh` | POST | Refresh access token |
| `/rates` | GET | Get latest aggregated rates (auth required) |
| `/rates/{currency}` | GET | Get latest rate for a currency |
| `/rates/compare/{currency}` | GET | Compare rate with previous hour/day |
| `/historical/rates` | GET | Retrieve historical rates |
| `/admin/currencies` | POST/DELETE | Add/remove supported currencies |

---

## Database Schema

**Tables:**

- `users` – stores username, hashed password, roles
- `rates` – currency pair, average rate, markup applied, timestamp
- `currencies` – dynamically managed supported currency codes
- `audit_logs` – logs user actions (future extensibility)

---

## Testing

- **Unit Tests:**
    - `AggregationServiceTest` → validates aggregation, outlier filtering, and markup logic.
    - `AuthServiceTest` → validates signup/login behavior and token generation.

- **Controller Tests:**
    - `AuthControllerTest` → tests `/signup` and `/login` endpoints.
    - `RateControllerIntegrationTest` → tests `/rates`, `/rates/{currency}` with authentication.

- **Run All Tests:**

```bash
mvn clean test
```

- **Coverage:** All core logic paths and error handling scenarios are tested, including API failures and empty responses.

---

## Design Decisions & Trade-offs

- **JWT Auth** → stateless, scalable, supports refresh tokens
- **Resilience4j retries** → handle unreliable external APIs gracefully
- **Outlier filtering** → improves accuracy, slightly increases computation
- **Dynamic currency pairs** → supports scaling without code changes
- **H2 dev / Postgres prod** → simplifies dev while remaining production-ready

---

## Future Improvements

- **WebSocket Updates** → push rate changes to clients in real-time
- **Multi-region caching** → reduce latency for global users
- **Rate limit monitoring** → avoid hitting API provider limits
- **Analytics dashboard** → visualize rate trends, markups, and comparisons
- **Container orchestration** → Kubernetes for scaling with multiple instances  
