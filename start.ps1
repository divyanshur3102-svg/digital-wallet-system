# Digital Wallet System - Build & Start (Windows PowerShell)
# Run from project root: .\start.ps1

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Green
Write-Host "Digital Wallet System - Build & Start" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Check prerequisites
Write-Host "Checking prerequisites..." -ForegroundColor Yellow

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Host "Java is not installed or not in PATH. Please install Java 17+ from https://adoptium.net/" -ForegroundColor Red
    exit 1
}
Write-Host "OK Java found" -ForegroundColor Green

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Host "Maven is not installed or not in PATH. Install from https://maven.apache.org/download.cgi" -ForegroundColor Red
    exit 1
}
Write-Host "OK Maven found" -ForegroundColor Green

if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    Write-Host "Node.js is not installed. Install from https://nodejs.org/" -ForegroundColor Red
    exit 1
}
Write-Host "OK Node.js found" -ForegroundColor Green

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "Docker is not installed or not in PATH. Install Docker Desktop from https://www.docker.com/products/docker-desktop" -ForegroundColor Red
    Write-Host "After installing, start Docker Desktop and run this script again." -ForegroundColor Yellow
    exit 1
}
Write-Host "OK Docker found" -ForegroundColor Green

Write-Host ""
Write-Host "All prerequisites satisfied!" -ForegroundColor Green
Write-Host ""

# Start infrastructure
Write-Host "Starting MySQL and Redis..." -ForegroundColor Yellow
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

docker compose up -d
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to start Docker containers." -ForegroundColor Red
    exit 1
}
Write-Host "OK Infrastructure started" -ForegroundColor Green

Write-Host ""
Write-Host "Waiting for MySQL to be ready (15 seconds)..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# Build backend
Write-Host ""
Write-Host "Building backend..." -ForegroundColor Yellow
Set-Location "$projectRoot\java-backend"
mvn clean install -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build backend." -ForegroundColor Red
    exit 1
}
Write-Host "OK Backend built" -ForegroundColor Green

# Install frontend dependencies
Write-Host ""
Write-Host "Installing frontend dependencies..." -ForegroundColor Yellow
Set-Location "$projectRoot\frontend"
if (Get-Command yarn -ErrorAction SilentlyContinue) {
    yarn install
} else {
    npm install
}
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to install frontend dependencies." -ForegroundColor Red
    exit 1
}
Write-Host "OK Frontend dependencies installed" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "To run the application, open TWO terminals:" -ForegroundColor Yellow
Write-Host ""
Write-Host "Terminal 1 - Backend:" -ForegroundColor Cyan
Write-Host "  cd java-backend" -ForegroundColor White
Write-Host "  mvn spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "Terminal 2 - Frontend (after backend is up):" -ForegroundColor Cyan
Write-Host "  cd frontend" -ForegroundColor White
Write-Host "  npm start" -ForegroundColor White
Write-Host "  (or: yarn start)" -ForegroundColor White
Write-Host ""
Write-Host "Access:" -ForegroundColor Yellow
Write-Host "  Frontend:    http://localhost:3000" -ForegroundColor White
Write-Host "  Backend API: http://localhost:8080/api" -ForegroundColor White
Write-Host "  Swagger UI:  http://localhost:8080/api/swagger-ui.html" -ForegroundColor White
Write-Host ""
