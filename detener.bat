@echo off
echo Deteniendo contenedores del Gimnasio...
cd /d "%~dp0"
docker compose down
echo Listo.
pause
