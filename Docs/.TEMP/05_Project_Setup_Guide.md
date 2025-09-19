# 📚 Library Management System – Spring Boot Project Setup Guide

## Overview
Step-by-step guide to create a **Spring Boot web application** in IntelliJ.  
Sets up foundation with **REST API, security, docs, and pagination**【100†source】.

## Prerequisites
- IntelliJ IDEA (Community or Ultimate)  
- JDK 17+  
- Internet connection (for dependencies)  

Verify JDK in IntelliJ: File → Project Structure → SDKs.

## Project Creation
1. **Initialize Project**  
   - Type: Maven, Java 17, Spring Boot 3.x  
   - Group: `com.example`, Artifact: `library-management-system`  
   - Packaging: Jar

2. **Dependencies**  
   - Spring Web, Spring Security, Validation, DevTools

## Configuration
- Add OpenAPI dependency for Swagger UI.  
- `application.properties` includes server port, session, logging, Swagger paths.  

## Core Structure
- **Application.java**: main entry point.  
- **SecurityConfig.java**: authentication + authorization.  
- **HealthController.java**: health endpoint.  
- **PagedResponse.java**: pagination wrapper.

## Running the App
- Run via IntelliJ or Maven wrapper (`./mvnw spring-boot:run`).  
- Swagger: `http://localhost:8085/swagger-ui.html`.  
- Health check: `http://localhost:8085/health`.

## Project Structure (after setup)
```
library-management-system/
 ├── src/main/java/com/example/librarymanagementsystem/
 │    ├── Application.java
 │    ├── config/SecurityConfig.java
 │    ├── controller/HealthController.java
 │    └── dto/PagedResponse.java
 ├── src/main/resources/application.properties
 └── pom.xml
```

---
✅ Provides a working foundation ready for entities, repos, services, controllers, and auth【100†source】.
