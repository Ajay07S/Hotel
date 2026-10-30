# Restaurant Management System

Restaurant operations dashboard with a React/TypeScript frontend, a Spring Boot REST API, and PostgreSQL persistence. The original Java CLI and business-domain sample remain in `src/` and `test/`; the web backend is a separate Maven module under `backend/`.

## Architecture

```text
React + TypeScript (Vite) → HTTP / JSON → Spring Boot REST API → Spring Data JPA → PostgreSQL
```

The browser only talks to the API. Order totals, status transitions, table occupancy, and payment validation are enforced by backend services. Database entities and API DTOs are separate from the retained CLI models.

## Requirements

- Java 21 or newer
- Maven 3.9+
- Node.js 20+ and npm
- Docker Desktop with Docker Compose (or a local PostgreSQL 16 server)

## Local setup

1. Create a local environment file from the example and set a local database password:

   ```powershell
   Copy-Item .env.example .env
   ```

   `.env` is ignored by Git. Do not commit database credentials.

2. Start PostgreSQL:

   ```powershell
   docker compose up -d postgres
   docker compose ps
   ```

   Docker creates the `restaurant` database and a persistent local volume. Hibernate creates/updates the application tables. On an empty database the backend loads example menu items and tables once; it does not overwrite existing records.

3. Start the backend in a terminal:

   ```powershell
   cd backend
   $settings = Get-Content ..\.env | ConvertFrom-StringData
   $env:DB_URL = $settings.DB_URL
   $env:DB_USERNAME = $settings.DB_USERNAME
   $env:DB_PASSWORD = $settings.DB_PASSWORD
   $env:SERVER_PORT = $settings.SERVER_PORT
   $env:FRONTEND_ORIGIN = $settings.FRONTEND_ORIGIN
   mvn spring-boot:run
   ```

   API base URL: `http://localhost:8080/api`. Override `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SERVER_PORT`, and `FRONTEND_ORIGIN` with environment variables as needed.

   If using an already-installed local PostgreSQL instead of Docker, create a
   database with `psql -U postgres -d postgres -c "CREATE DATABASE restaurant;"`
   (or use an existing dedicated database), then set the DB variables to match
   that server and its authentication settings.

4. In another terminal, install frontend dependencies and run Vite:

   ```powershell
   cd frontend
   Copy-Item .env.example .env
   npm install
   npm run dev
   ```

   Open `http://localhost:5173`. `VITE_API_BASE_URL` configures the API endpoint. Vite listens on loopback for local development; the API's CORS origin is configurable with `FRONTEND_ORIGIN`.

5. Production frontend build:

   ```powershell
   cd frontend
   npm run build
   ```

## Tests

Run the backend unit and PostgreSQL HTTP integration tests from the backend
directory after PostgreSQL is available and its environment variables are set:

```powershell
cd backend
$settings = Get-Content ..\.env | ConvertFrom-StringData
$env:DB_URL = $settings.DB_URL
$env:DB_USERNAME = $settings.DB_USERNAME
$env:DB_PASSWORD = $settings.DB_PASSWORD
mvn test
```

On the current machine only, `javac` lacks release-signature metadata despite
supporting Java 21 source/target levels; use
`mvn -Drestaurant.jdk.no-release-signatures=true test`. A normal JDK 21+
installation should use the standard `mvn test` command.

## API overview

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET / POST | `/api/menu` | List / create menu items |
| GET / PUT / DELETE | `/api/menu/{id}` | Read / edit / remove an unused menu item |
| GET / POST | `/api/tables` | List / add tables |
| PUT | `/api/tables/{id}` | Reserve or release a non-occupied table |
| GET / POST | `/api/orders` | List / open an order for an available table |
| GET | `/api/orders/{id}` | Read order details |
| POST | `/api/orders/{id}/items` | Add a menu item and quantity |
| POST | `/api/orders/{id}/cancel` | Cancel an eligible order and release its table |
| POST | `/api/orders/{id}/preparing` | Start preparation |
| POST | `/api/orders/{id}/ready` | Mark a preparing order ready |
| POST | `/api/orders/{id}/complete` | Complete a paid order and release its table |
| GET / POST | `/api/payments` | List receipts / make a simulated payment |
| GET | `/api/payments/{id}` | Read a receipt |
| GET | `/api/dashboard` | Dashboard counts and successful-payment sales |

Payment request example:

```json
{ "orderId": 1, "method": "CASH" }
```

All payments are simulated; no real money is processed. One payment record is permitted per order. API errors include a message and field validation details where applicable.

## Database tables

The API persists `menu_items`, `restaurant_tables`, `restaurant_orders`, `order_items`, and `payments`. `restaurant_orders` avoids using the SQL keyword `order`. Items retain their unit price at the time they were added. Payment-to-order uniqueness and transactional table/order updates protect core business rules.

## Original Java CLI

The original console app is retained and can still be compiled from the workspace root:

```powershell
javac -d out src\com\restaurant\models\*.java src\com\restaurant\repository\*.java src\com\restaurant\service\*.java src\com\restaurant\CLI\*.java src\com\restaurant\Main.java
java -cp out com.restaurant.Main
```

Its dependency-free regression checks:

```powershell
javac -d out src\com\restaurant\models\*.java src\com\restaurant\repository\*.java src\com\restaurant\service\*.java src\com\restaurant\CLI\*.java src\com\restaurant\Main.java test\com\restaurant\RestaurantSystemTest.java
java -cp out com.restaurant.RestaurantSystemTest
```

## Limitations

This is a local single-restaurant application. It has no authentication, authorization, multi-tenant separation, real payment gateway, or production database migration tool. Hibernate schema update and sample seeding are intended for local development; use versioned migrations and production secret management before deployment.
