#!/bin/bash

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Digital Wallet System - Build & Start${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Check prerequisites
echo -e "${YELLOW}Checking prerequisites...${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}Java is not installed. Please install Java 17 or higher.${NC}"
    echo "Download from: https://adoptium.net/"
    exit 1
fi
echo -e "${GREEN}✓ Java found: $(java -version 2>&1 | head -n 1)${NC}"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}Maven is not installed. Please install Maven 3.6 or higher.${NC}"
    echo "Download from: https://maven.apache.org/download.cgi"
    exit 1
fi
echo -e "${GREEN}✓ Maven found: $(mvn -version | head -n 1)${NC}"

# Check Node.js
if ! command -v node &> /dev/null; then
    echo -e "${RED}Node.js is not installed. Please install Node.js 16 or higher.${NC}"
    echo "Download from: https://nodejs.org/"
    exit 1
fi
echo -e "${GREEN}✓ Node.js found: $(node -v)${NC}"

# Check Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}Docker is not installed. Please install Docker Desktop.${NC}"
    echo "Download from: https://www.docker.com/products/docker-desktop"
    exit 1
fi
echo -e "${GREEN}✓ Docker found: $(docker -v)${NC}"

echo ""
echo -e "${GREEN}All prerequisites satisfied!${NC}"
echo ""

# Start infrastructure
echo -e "${YELLOW}Starting MySQL and Redis...${NC}"
docker-compose up -d

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Infrastructure started successfully${NC}"
else
    echo -e "${RED}Failed to start infrastructure${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}Waiting for MySQL to be ready...${NC}"
sleep 10

echo ""
echo -e "${YELLOW}Building backend...${NC}"
cd java-backend
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Backend built successfully${NC}"
else
    echo -e "${RED}Failed to build backend${NC}"
    exit 1
fi

echo ""
echo -e "${YELLOW}Installing frontend dependencies...${NC}"
cd ../frontend
yarn install

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Frontend dependencies installed${NC}"
else
    echo -e "${RED}Failed to install frontend dependencies${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Setup Complete!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "${YELLOW}To start the application:${NC}"
echo ""
echo "1. Start Backend:"
echo "   cd java-backend"
echo "   mvn spring-boot:run"
echo ""
echo "2. Start Frontend (in another terminal):"
echo "   cd frontend"
echo "   yarn start"
echo ""
echo -e "${YELLOW}Access Points:${NC}"
echo "  - Frontend: http://localhost:3000"
echo "  - Backend API: http://localhost:8080/api"
echo "  - Swagger UI: http://localhost:8080/api/swagger-ui.html"
echo ""
echo -e "${GREEN}Happy Coding! 🚀${NC}"
echo ""