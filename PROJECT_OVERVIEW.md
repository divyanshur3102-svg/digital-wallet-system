# Digital Wallet System - Complete Project

## 📦 Overview

A **production-grade Digital Wallet / Payment Gateway backend** built with **Java Spring Boot** plus a **React Admin Dashboard** for wallet management.

---

## 📁 Project Structure

```
/app/
├── java-backend/              # Spring Boot Backend
│   ├── src/main/java/com/wallet/
│   │   ├── entity/           # JPA Entities (User, Wallet, Transaction, Ledger)
│   │   ├── repository/       # Data Access Layer
│   │   ├── service/          # Business Logic (Payment, Wallet, Auth)
│   │   ├── controller/       # REST APIs
│   │   ├── security/         # JWT Authentication
│   │   ├── config/           # Security & Redis Config
│   │   └── exception/        # Global Exception Handler
│   ├── src/main/resources/
│   │   └── application.yml   # Application Config
│   └── pom.xml              # Maven Dependencies
│
├── frontend/                 # React Admin Dashboard
│   ├── src/
│   │   ├── pages/            # Login, Register, Dashboard
│   │   ├── api/              # API Client
│   │   └── components/ui/    # Shadcn UI Components
│   └── package.json
│
├── docker-compose.yml       # MySQL + Redis Setup
├── README.md                # Complete Documentation
└── SETUP_GUIDE.md           # Quick Start Guide
```

---

## 🚀 Quick Start (Local Development)

### Prerequisites

1. **Java 17+**: [Download from Adoptium](https://adoptium.net/)
2. **Maven 3.6+**: [Download Maven](https://maven.apache.org/download.cgi)
3. **Node.js 16+**: [Download Node.js](https://nodejs.org/)
4. **Docker Desktop**: [Download Docker](https://www.docker.com/products/docker-desktop)

### Step 1: Start Infrastructure (MySQL + Redis)

```bash
cd /app
docker-compose up -d

# Verify services
docker-compose ps
```

You should see MySQL and Redis running on:
- MySQL: `localhost:3306`
- Redis: `localhost:6379`

### Step 2: Start Backend (Spring Boot)

```bash
cd java-backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

Backend will start on: **http://localhost:8080**

Swagger API Docs: **http://localhost:8080/api/swagger-ui.html**

### Step 3: Start Frontend (React)

```bash
cd /app/frontend

# Install dependencies
yarn install

# Start development server
yarn start
```

Frontend will start on: **http://localhost:3000**

---

## 🎯 Core Features Implemented

### Backend (Spring Boot)

✅ **User Authentication**
- JWT-based authentication
- BCrypt password hashing
- Role-based access control (USER, ADMIN)

✅ **Wallet Management**
- Create multiple wallets per user
- Check balance
- View wallet details
- Wallet status management (ACTIVE, FROZEN, CLOSED)

✅ **Payment System**
- **Add Money**: Deposit funds to wallet
- **Transfer Money**: P2P transfers between wallets
- **Atomic Transactions**: ACID compliance with pessimistic locking
- **Idempotency**: Prevent duplicate payments using Redis

✅ **Ledger System**
- Double-entry bookkeeping
- Append-only ledger entries
- Every transaction creates debit + credit entries
- Maintains balance after each entry

✅ **Security**
- JWT token-based authentication
- Spring Security configuration
- CORS enabled for React frontend
- Input validation

✅ **Audit Logging**
- Async audit log creation
- Tracks all critical operations
- User action history

### Frontend (React)

✅ **Authentication UI**
- Login page
- Registration page
- JWT token management

✅ **Admin Dashboard**
- View all wallets
- Display wallet balance
- Add money to wallet
- Transfer money between wallets
- Transaction history
- Real-time balance updates

✅ **Modern UI**
- Built with Shadcn UI components
- Dark theme design
- Responsive layout
- Toast notifications

---

## 📊 Database Schema

### Tables Created Automatically

1. **users** - User accounts
2. **user_roles** - User role mappings
3. **wallets** - Wallet information with balance
4. **transactions** - Transaction records
5. **ledger_entries** - Double-entry ledger
6. **audit_logs** - Audit trail

---

## 📡 API Endpoints

### Authentication APIs

```
POST /api/auth/register  - Register new user
POST /api/auth/login     - Login user
```

### Wallet APIs (JWT Required)

```
POST /api/wallet/create            - Create new wallet
GET  /api/wallet/{walletId}        - Get wallet details
GET  /api/wallet/my-wallets        - Get all user wallets
GET  /api/wallet/{walletId}/balance - Get wallet balance
```

### Payment APIs (JWT Required)

```
POST /api/payment/add-money             - Add money to wallet
POST /api/payment/transfer              - Transfer between wallets
GET  /api/payment/history/{walletId}    - Get transaction history
```

**Full API Documentation**: http://localhost:8080/api/swagger-ui.html

---

## 🧪 Testing the System

### Option 1: Using the React Dashboard (Recommended)

1. Open http://localhost:3000
2. Click "Create Account"
3. Register with your details
4. Create a wallet
5. Add money to the wallet
6. Create another user and transfer money

### Option 2: Using Swagger UI

1. Open http://localhost:8080/api/swagger-ui.html
2. Register a user via `/auth/register`
3. Login via `/auth/login` and copy the JWT token
4. Click "Authorize" button and paste token
5. Test all endpoints

### Option 3: Using cURL

See `SETUP_GUIDE.md` for cURL examples.

---

## 🔑 Key Technical Implementation

### 1. Atomic Money Transfer

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public TransactionResponse transfer(TransferRequest request) {
    // 1. Lock sender wallet row (pessimistic lock)
    Wallet fromWallet = walletRepository.findByIdWithLock(fromWalletId);
    
    // 2. Lock receiver wallet row
    Wallet toWallet = walletRepository.findByIdWithLock(toWalletId);
    
    // 3. Check sufficient balance
    if (fromWallet.getBalance().compareTo(amount) < 0) {
        throw new InsufficientBalanceException();
    }
    
    // 4. Debit sender
    fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
    
    // 5. Credit receiver
    toWallet.setBalance(toWallet.getBalance().add(amount));
    
    // 6. Save transaction record
    // 7. Create ledger entries (debit + credit)
    // 8. Commit transaction (all or nothing)
}
```

### 2. Idempotency with Redis

```java
if (idempotencyService.isProcessed(idempotencyKey)) {
    return cachedResponse; // Return previous response
}

// Process transaction...

idempotencyService.markAsProcessed(idempotencyKey, response);
```

### 3. Double-Entry Ledger

Every transfer creates TWO ledger entries:
- **Debit Entry**: Records money leaving sender's wallet
- **Credit Entry**: Records money entering receiver's wallet

This ensures:
- Complete audit trail
- Balance reconciliation
- Compliance with accounting standards

---

## 🛠️ Configuration

### Backend Configuration

Edit `java-backend/src/main/resources/application.yml`:

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

### Frontend Configuration

Edit `frontend/.env`:

```env
REACT_APP_API_URL=http://localhost:8080/api
```

---

## 🚀 Production Deployment Checklist

**Before deploying to production:**

- [ ] Change JWT secret to a strong 256-bit key
- [ ] Update database credentials
- [ ] Enable SSL/TLS
- [ ] Configure proper CORS origins
- [ ] Set up database backups
- [ ] Configure Redis persistence
- [ ] Set up monitoring (Prometheus, Grafana)
- [ ] Configure logging (ELK stack)
- [ ] Implement rate limiting
- [ ] Add API versioning
- [ ] Set up CI/CD pipeline

---

## 💻 Technologies Used

**Backend:**
- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Redis 7
- Swagger/OpenAPI
- Maven

**Frontend:**
- React 19
- React Router
- Axios
- Shadcn UI
- Tailwind CSS
- Sonner (Toast notifications)
- Lucide React (Icons)

---

## Technical Highlights

### Architecture & Design
- Microservice-ready architecture
- Repository pattern for data access
- Service layer for business logic
- DTO pattern for API contracts

### Transaction Management
- Pessimistic locking to prevent race conditions
- ACID compliance using Spring @Transactional
- Isolation level: READ_COMMITTED

### Scalability
- Stateless JWT authentication
- Redis for caching and idempotency
- Database indexing on frequently queried fields
- Optimistic locking with @Version for concurrency

### Security
- BCrypt password hashing
- JWT with configurable expiration
- Role-based access control
- Input validation using Bean Validation

### Financial Domain
- Double-entry bookkeeping
- Append-only ledger
- Idempotent payment APIs
- Audit logging for compliance

---

## 🐛 Troubleshooting

### Backend won't start

```bash
# Check if MySQL is running
docker-compose ps

# View backend logs
# Check console output for errors

# Common issue: Port 8080 already in use
lsof -i :8080
kill -9 <PID>
```

### Frontend can't connect to backend

```bash
# Verify backend is running
curl http://localhost:8080/api/auth/login

# Check .env file
cat frontend/.env
```

### Database connection issues

```bash
# Restart MySQL
docker-compose restart mysql

# Check MySQL logs
docker-compose logs mysql
```

---

## 📚 Additional Resources

- **README.md**: Complete API documentation
- **SETUP_GUIDE.md**: Step-by-step setup instructions
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html

---

## Prerequisites

To run this project locally you need:
- Java 17+
- Maven 3.6+
- Node.js 16+
- Docker (for MySQL and Redis)

Install these tools and follow the Quick Start guide in this document.

