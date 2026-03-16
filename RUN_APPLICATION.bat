@echo off
title Digital Wallet - Launcher
cd /d "%~dp0"

echo ========================================
echo    Digital Wallet - Application Launcher
echo ========================================
echo.

echo Step 1: Starting Docker (MySQL + Redis)...
docker compose up -d
if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Docker failed. Please:
    echo   1. Open Docker Desktop
    echo   2. Wait for it to fully start
    echo   3. Run this script again
    echo.
    pause
    exit /b 1
)

echo [OK] Docker containers started.
echo.
echo Step 2: Waiting 20 seconds for MySQL to initialize...
timeout /t 20 /nobreak > nul
echo.

echo Step 3: Opening Backend window...
start "Digital Wallet - Backend" cmd /k "cd /d %~dp0\java-backend && echo Starting Spring Boot... && mvn spring-boot:run"

echo.
echo Step 4: Waiting 45 seconds for backend to start...
timeout /t 45 /nobreak > nul

echo.
echo Step 5: Opening Frontend window...
start "Digital Wallet - Frontend" cmd /k "cd /d %~dp0\frontend && echo Starting React... && npm start"

echo.
echo ========================================
echo    Application is starting!
echo ========================================
echo.
echo Two new windows have opened:
echo   - Backend:  Spring Boot (wait for "Started WalletApplication")
echo   - Frontend: React (browser will open automatically)
echo.
echo Access URLs:
echo   Frontend:    http://localhost:3000
echo   Backend API: http://localhost:8080/api
echo   Swagger UI:  http://localhost:8080/api/swagger-ui.html
echo.
echo Press any key to close this launcher (apps keep running)...
pause > nul
