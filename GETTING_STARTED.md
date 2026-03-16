# Digital Wallet System
## Java Spring Boot + React Full Stack Application

---

## 📦 What's Inside

This is a **complete, production-ready digital wallet system** with:

### Backend (Java Spring Boot)
- User authentication with JWT
- Wallet management
- Atomic money transfers
- Double-entry ledger system
- Idempotent payment APIs
- Redis caching
- MySQL database
- Swagger API documentation

### Frontend (React)
- User authentication UI
- Wallet dashboard
- Add money functionality
- Transfer money between wallets
- Transaction history
- Modern UI with Shadcn components

---

## Prerequisites

Install these tools on your local machine:

1. **Java 17 or higher**
   - Download: https://adoptium.net/
   - Verify: `java -version`

2. **Maven 3.6+**
   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **Node.js 16+**
   - Download: https://nodejs.org/
   - Verify: `node -v`

4. **Docker Desktop**
   - Download: https://www.docker.com/products/docker-desktop
   - Verify: `docker -v`

---

## 🚀 Quick Start

### Step 1: Start Infrastructure

```bash
cd /app
docker-compose up -d
```

This starts:
- MySQL on port 3306
- Redis on port 6379

### Step 2: Build & Run Backend

```bash
cd java-backend
mvn clean install
mvn spring-boot:run
```

Backend runs on: **http://localhost:8080**

### Step 3: Run Frontend

```bash
cd /app/frontend
yarn install
yarn start
```

Frontend runs on: **http://localhost:3000**

---

## 🎯 Quick Test

1. Open http://localhost:3000
2. Click "Create Account"
3. Register with your email
4. Create a wallet
5. Add money to wallet
6. Test transfer functionality

---

## 📚 Documentation

- **PROJECT_OVERVIEW.md** - Complete project documentation
- **README.md** - Detailed API documentation and setup
- **SETUP_GUIDE.md** - Step-by-step setup with cURL examples
- **Swagger UI** - http://localhost:8080/api/swagger-ui.html

---

## 💻 Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- MySQL 8.0
- Redis 7
- Maven

**Frontend:**
- React 19
- React Router
- Shadcn UI
- Tailwind CSS
- Axios

---

## 📁 Project Structure

```
/app/
├── java-backend/         # Spring Boot backend
│   ├── src/main/java/
│   └── pom.xml
├── frontend/            # React frontend
│   ├── src/
│   └── package.json
├── docker-compose.yml  # MySQL + Redis
└── README.md
```

---

## 🔑 Key Features

✅ JWT Authentication
✅ Multi-wallet support
✅ Atomic transactions
✅ Idempotent APIs
✅ Double-entry ledger
✅ Audit logging
✅ Modern React UI
✅ Transaction history
✅ Real-time balance updates

---

## 🎯 API Endpoints

**Authentication:**
- `POST /api/auth/register` - Register user
- `POST /api/auth/login` - Login user

**Wallet Management:**
- `POST /api/wallet/create` - Create wallet
- `GET /api/wallet/my-wallets` - Get user wallets
- `GET /api/wallet/{id}/balance` - Get balance

**Payments:**
- `POST /api/payment/add-money` - Add money
- `POST /api/payment/transfer` - Transfer money
- `GET /api/payment/history/{walletId}` - Get history

---

## 🐛 Troubleshooting

**Backend won't start:**
```bash
# Check if port 8080 is available
lsof -i :8080

# Check MySQL is running
docker-compose ps
```

**Frontend can't connect:**
```bash
# Verify backend is running
curl http://localhost:8080/api/auth/login

# Check .env file
cat frontend/.env
```

---

## Support

For detailed documentation, see:
- PROJECT_OVERVIEW.md
- SETUP_GUIDE.md
- Swagger UI (when running)

---
