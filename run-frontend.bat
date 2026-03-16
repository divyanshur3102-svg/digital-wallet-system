@echo off
title Digital Wallet - Frontend
cd /d "%~dp0\frontend"

echo ========================================
echo Digital Wallet - Starting Frontend
echo ========================================
echo.

echo Starting React development server...
call npm start

pause
