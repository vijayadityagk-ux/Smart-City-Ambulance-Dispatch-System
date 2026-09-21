@echo off
title Dispatch Engine
echo Starting Dispatch Engine (Java Spring Boot + Vaadin on port 8080)...
call .\mvnw.cmd spring-boot:run
pause
