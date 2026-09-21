# 🧭 Driver Navigation HUD (Pure Java Web UI)

Complete Pure Java implementation of the Driver Navigation HUD interface built with **Spring Boot 4** and **Vaadin Flow 25**.

## Features
- **100% Pure Java**: Components, layouts, HUD overlays, and styling written entirely in Java.
- **Real-time STOMP / WebSocket Connectivity**: Connects to the Engine's WebSocket endpoint (`http://localhost:8080/smart-city-ws`), subscribing to `/topic/alerts`.
- **Live Push Updates**: Uses Vaadin server push (`@Push`) to instantly react to HQ dispatch commands.
- **Dynamic Routing Navigation**: Live Leaflet & OSRM routing map automatically reroutes the ambulance to the matched hospital.

## Running the Application
```powershell
.\mvnw.cmd spring-boot:run
```
Or double-click `run.bat`.

Once launched, access the HUD at:
👉 **http://localhost:8082**
