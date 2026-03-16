@echo off
title Digital Wallet - Backend
cd /d "%~dp0"

echo ========================================
echo Digital Wallet - Starting Backend
echo ========================================
echo.

echo Starting MySQL and Redis (Docker)...
docker compose up -d
if %ERRORLEVEL% neq 0 (
    echo.
    echo ERROR: Docker failed. Please ensure Docker Desktop is running.
    echo Start Docker Desktop and try again.
    pause
    exit /b 1
)

echo.
echo Waiting 20 seconds for MySQL to be ready...
timeout /t 20 /nobreak > nul

echo.
echo Starting Spring Boot backend...
cd java-backend
call mvn spring-boot:run

pause
