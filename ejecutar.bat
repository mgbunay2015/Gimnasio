@echo off
echo ========================================
echo  Gimnasio - Levantando contenedores
echo ========================================
cd /d "%~dp0"
docker compose up -d --build
echo.
echo Esperando a que Keycloak y el microservicio arranquen...
timeout /t 50 /nobreak >nul
echo.
echo Health microservicio:
curl -s http://localhost:8281/api/public/health
echo.
echo Health via Nginx:
curl -s http://localhost:8088/api/public/health
echo.
echo.
echo Keycloak Admin:  http://localhost:8280  (admin / admin)
echo API Bookings:    http://localhost:8281
echo Nginx Gateway:   http://localhost:8088
echo.
echo Usuarios:
echo   member.test  / member123  (MEMBER)
echo   trainer.test / trainer123 (TRAINER)
echo   admin.test   / admin123   (ADMIN)
echo Client: gym-app / Secret: gym-app-secret-2026
echo.
pause
