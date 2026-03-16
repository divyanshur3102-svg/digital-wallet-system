# Digital Wallet / Payment Gateway Backend

## Spring Boot - Fintech Project

### Overview
A production-grade digital wallet system with secure payment gateway features including:
- User authentication with JWT
- Wallet management
- Atomic money transfers
- Ledger-based accounting
- Idempotent payment APIs
- Redis caching
- Comprehensive audit logging

### Tech Stack
- **Backend**: Java 17, Spring Boot 3.2
- **Database**: MySQL 8.0
- **Cache**: Redis 7
- **Security**: Spring Security + JWT
- **API Documentation**: Swagger/OpenAPI
- **Build Tool**: Maven

---

## Quick Start Guide

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (for MySQL & Redis)

### Step 1: Start Infrastructure Services

```bash
# Start MySQL and Redis
cd /app
docker-compose up -d

# Verify services are running
docker-compose ps
```

### Step 2: Build the Application

```bash
cd java-backend
mvn clean install
```

### Step 3: Run the Application

```bash
# Run from IDE (IntelliJ IDEA / Eclipse)
# OR
mvn spring-boot:run

# OR build and run JAR
mvn package
java -jar target/digital-wallet-1.0.0.jar
```

The application will start on **http://localhost:8080**

### Step 4: Access API Documentation

Swagger UI: **http://localhost:8080/api/swagger-ui.html**

---

## API Endpoints

### Authentication APIs

#### Register User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "1234567890"
}
```

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
}
```

### Wallet APIs (Requires JWT Token)

#### Create Wallet
```bash
POST /api/wallet/create?currency=USD
Authorization: Bearer <JWT_TOKEN>
```

#### Get Wallet Details
```bash
GET /api/wallet/{walletId}
Authorization: Bearer <JWT_TOKEN>
```

#### Get My Wallets
```bash
GET /api/wallet/my-wallets
Authorization: Bearer <JWT_TOKEN>
```

#### Get Balance
```bash
GET /api/wallet/{walletId}/balance
Authorization: Bearer <JWT_TOKEN>
```

### Payment APIs (Requires JWT Token)

#### Add Money to Wallet
```bash
POST /api/payment/add-money
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "walletId": 1,
  "amount": 1000.00,
  "description": "Initial deposit",
  "idempotencyKey": "unique-key-123"
}
```

#### Transfer Money
```bash
POST /api/payment/transfer
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "fromWalletId": 1,
  "toWalletId": 2,
  "amount": 500.00,
  "description": "Payment for services",
  "idempotencyKey": "unique-key-456"
}
```

#### Get Transaction History
```bash
GET /api/payment/history/{walletId}?page=0&size=10
Authorization: Bearer <JWT_TOKEN>
```

---

## Key Features

### 1. Atomic Transactions
- Uses `@Transactional` with proper isolation levels
- Pessimistic locking on wallet rows during transfers
- Ensures ACID compliance
- Rollback on any failure

### 2. Idempotency
- Prevents duplicate payments
- Redis-based idempotency key storage
- 24-hour expiry on keys

### 3. Ledger System
- Append-only ledger entries
- Every transaction creates debit/credit entries
- Maintains balance after each entry
- Used for reconciliation and audits

### 4. Security
- JWT-based authentication
- BCrypt password hashing
- Role-based access control (USER, ADMIN)
- CORS configuration for frontend

### 5. Audit Logging
- Async audit log creation
- Tracks all critical operations
- User action history

---

## Database Schema

The application automatically creates tables on startup:

- **users** - User accounts
- **user_roles** - User role mappings
- **wallets** - Wallet information
- **transactions** - Transaction records
- **ledger_entries** - Double-entry ledger
- **audit_logs** - Audit trail

---

## Configuration

### Application Configuration
Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wallet_db
    username: wallet_user
    password: wallet_password
  
  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your-256-bit-secret-key-change-this-in-production
  expiration: 86400000  # 24 hours
```

### Production Deployment

**Important**: Before deploying to production:

1. Change JWT secret to a strong random key
2. Update database credentials
3. Enable SSL/TLS
4. Configure proper CORS origins
5. Set up application monitoring
6. Configure backup strategy

---

## Testing

### Using Postman

1. Import the Swagger spec into Postman
2. Create an environment with `baseUrl` = `http://localhost:8080/api`
3. Register a user and save the JWT token
4. Use the token in Authorization header for protected endpoints

### Using cURL

See examples in the API Endpoints section above.

---

## Project Structure

```
java-backend/
├── src/main/java/com/wallet/
│   ├── WalletApplication.java          # Main application class
│   ├── entity/                         # JPA entities
│   │   ├── User.java
│   │   ├── Wallet.java
│   │   ├── Transaction.java
│   │   ├── LedgerEntry.java
│   │   └── AuditLog.java
│   ├── repository/                     # Data access layer
│   ├── service/                        # Business logic
│   │   ├── AuthService.java
│   │   ├── WalletService.java
│   │   ├── PaymentService.java
│   │   ├── IdempotencyService.java
│   │   └── AuditService.java
│   ├── controller/                     # REST controllers
│   ├── dto/                           # Request/Response DTOs
│   ├── security/                      # Security components
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CustomUserDetailsService.java
│   ├── config/                        # Configuration classes
│   └── exception/                     # Exception handling
└── src/main/resources/
    └── application.yml                # Application configuration
```

---

## Troubleshooting

### MySQL Connection Issues
```bash
# Check if MySQL is running
docker-compose ps

# View MySQL logs
docker-compose logs mysql

# Restart MySQL
docker-compose restart mysql
```

### Redis Connection Issues
```bash
# Check if Redis is running
docker-compose ps

# Test Redis connection
redis-cli ping
```

### Application Logs
Check console output or configure logging in `application.yml`

---

## Key Features Summary

- Digital Wallet & Payment Gateway backend using Spring Boot
- Atomic fund transfers with pessimistic locking
- Ledger-based accounting system (double-entry)
- Idempotent payment APIs using Redis
- JWT-based authentication with Spring Security
- ACID compliance with proper transaction management
- Audit logging for financial operations
- RESTful APIs with Swagger documentation

---

## License
MIT License - Free to use for learning and portfolio purposes

---

## Support
For issues or questions, please create an issue in the repository.
