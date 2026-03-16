# Digital Wallet - Setup & Run Guide

Full-stack setup for **backend (Spring Boot)** and **frontend (React)**.

---

## Run application completely (quick start)

### Method 1: One-click (Windows)

1. **Start Docker Desktop** and wait until it is fully running.
2. **Double-click `RUN_APPLICATION.bat`** in the project root.
3. Two CMD windows will open (backend and frontend). Wait until:
   - Backend shows: `Started WalletApplication in X seconds`
   - Frontend opens your browser at http://localhost:3000
4. Use the app at **http://localhost:3000** (register, login, create wallet, add money, transfer).

### Method 2: Manual (any OS)

| Step | Action |
|------|--------|
| 1 | Start **Docker Desktop** (Windows/Mac) or ensure Docker is running. |
| 2 | In a terminal, from **project root**: `docker compose up -d` → wait **20 seconds**. |
| 3 | **Terminal 1** – Backend: `cd java-backend` then `mvn spring-boot:run` → wait for "Started WalletApplication". |
| 4 | **Terminal 2** – Frontend: `cd frontend` then `npm start` (or `yarn start`). |
| 5 | Open **http://localhost:3000** in your browser. |

**URLs:** Frontend: http://localhost:3000 | Backend API: http://localhost:8080/api | Swagger: http://localhost:8080/api/swagger-ui.html

---

## Prerequisites

| Tool        | Version  | Download |
|------------|----------|----------|
| Java       | 17+      | https://adoptium.net/ |
| Maven      | 3.6+     | https://maven.apache.org/download.cgi |
| Node.js    | 16+      | https://nodejs.org/ |
| Docker     | Desktop  | https://www.docker.com/products/docker-desktop |

**Windows:** Add Java, Maven, and Node to your system PATH after installing.

---

## Option A: One-time setup (recommended)

### Windows (PowerShell)

From the **project root** (`Digital-Payment`):

```powershell
.\start.ps1
```

This will:
1. Check Java, Maven, Node, Docker
2. Start MySQL and Redis (`docker compose up -d`)
3. Build the Java backend
4. Install frontend dependencies (npm or yarn)

### Linux / macOS (Bash)

```bash
chmod +x start.sh
./start.sh
```

---

## Option B: Manual steps

### 1. Start infrastructure (MySQL + Redis)

From **project root**:

```bash
# Windows PowerShell / Linux / macOS
docker compose up -d
```

Wait ~15 seconds for MySQL to be ready.

### 2. Build & run backend

```bash
cd java-backend
mvn clean install
mvn spring-boot:run
```

Leave this terminal open. Backend runs at **http://localhost:8080**.

### 3. Run frontend (second terminal)

From **project root**:

```bash
cd frontend
npm install
npm start
```

Or with Yarn:

```bash
yarn install
yarn start
```

Frontend runs at **http://localhost:3000**.

---

## Access points

| What        | URL |
|------------|-----|
| **Frontend (React)** | http://localhost:3000 |
| **Backend API**      | http://localhost:8080/api |
| **Swagger UI**       | http://localhost:8080/api/swagger-ui.html |

---

## Quick run (after first setup)

If you already ran `start.ps1` or `start.sh` once:

1. **Start Docker** (if not running):  
   From project root: `docker compose up -d`

2. **Terminal 1 – Backend:**
   ```bash
   cd java-backend
   mvn spring-boot:run
   ```

3. **Terminal 2 – Frontend:**
   ```bash
   cd frontend
   npm start
   ```
   (or `yarn start`)

4. Open **http://localhost:3000** in your browser.

---

## API testing (optional)

### Register user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'
```

Save the `token` from the response.

### Create wallet

```bash
curl -X POST "http://localhost:8080/api/wallet/create?currency=USD" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Add money

```bash
curl -X POST http://localhost:8080/api/payment/add-money \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"walletId":1,"amount":1000,"description":"Initial deposit","idempotencyKey":"key-001"}'
```

### Transfer money

```bash
curl -X POST http://localhost:8080/api/payment/transfer \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"fromWalletId":1,"toWalletId":2,"amount":500,"description":"Payment","idempotencyKey":"key-002"}'
```

---

## Troubleshooting

- **Docker not found**  
  Install Docker Desktop and ensure it’s running. On Windows, you may need to restart the terminal after install.

- **Maven not found**  
  Install Maven and add its `bin` folder to your system PATH.

- **Backend fails to start (e.g. MySQL connection)**  
  Ensure `docker compose up -d` was run and wait 15–20 seconds before starting the backend.

- **Frontend can’t reach API**  
  Backend must be running on port 8080. Check `frontend/.env`:  
  `REACT_APP_API_URL=http://localhost:8080/api`
