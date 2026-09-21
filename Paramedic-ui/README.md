# 🚑 Paramedic UI (Pure Java Web UI)

Complete Pure Java implementation of the Paramedic Dispatch interface built with **Spring Boot 4** and **Vaadin Flow 25**.

## Features
- **100% Pure Java**: Components, layouts, event listeners, and styling written entirely in Java.
- **Direct Dispatch Integration**: Sends emergency dispatch payloads to the Central Dispatch Engine (`http://localhost:8080/api/emergency`) using Java 21's asynchronous `HttpClient`.
- **Real-time Feedback**: Interactive trauma selection, dispatch progress indication, and response modal alerts.

## Running the Application
```powershell
.\mvnw.cmd spring-boot:run
```
Or double-click `run.bat`.

Once launched, access the UI at:
👉 **http://localhost:8081**
