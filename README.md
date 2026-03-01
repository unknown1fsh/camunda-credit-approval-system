# 🚀 Camunda Credit Approval System (Enterprise Edition)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen)](https://spring.io/projects/spring-boot)
[![Camunda](https://img.shields.io/badge/Camunda-7.21.0-orange)](https://camunda.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18.1-blue)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-purple)](LICENSE)

An enterprise-grade, automated credit approval engine built with **Camunda BPM 7**, **Spring Boot 3**, and **PostgreSQL 18**. This system features advanced workflow automation, complex decision logic (DMN), and a unique compatibility layer for the latest PostgreSQL engines.

---

## 🏛️ System Architecture

The system follows a modern microservices-ready architecture with a clear separation between the process engine, business logic, and external task workers.

```mermaid
graph TD
    User((Customer/Banker)) -->|Submit Application| API[Spring Boot REST API]
    API -->|Start Process| Engine[Camunda Engine]
    
    subgraph "Process Ecosystem"
        Engine -->|Execute| BPMN[BPMN Workflow]
        BPMN -->|Decision| DMN[DMN Tables]
        BPMN -->|External Tasks| Workers[External Workers]
    end
    
    subgraph "Data Layer"
        Engine <-->|Persistence| DB[(PostgreSQL 18)]
        DB <-->|Compatibility Layer| PG18Fix[PG18 Schema Plugin]
    end
    
    Workers -->|Credit Bureau| CB[Credit Bureau API]
    Workers -->|Notifications| Email[Email/SMS Service]
```

---

## 📋 Business Workflow (BPMN)

The core business logic is encapsulated in a high-fidelity BPMN 2.0 process. It handles everything from fraud detection to dynamic manager assignments.

```mermaid
graph LR
    Start((Start)) --> Validate[Validate Application]
    Validate --> Fraud{Fraud Check}
    Fraud -- Fraud Detected --> Reject[Auto Reject]
    Fraud -- Clear --> CB[Credit Bureau Check]
    CB --> Risk[Risk Scoring DMN]
    Risk --> RiskLevel{Risk Level?}
    
    RiskLevel -- CRITICAL --> Reject
    RiskLevel -- Normal --> Approver[Determine Approver DMN]
    
    Approver --> DocReq[Document Requirements DMN]
    DocReq --> Assign[Assign Manager]
    Assign --> Manual[Manager Approval]
    
    Manual --> Approved{Approved?}
    Approved -- Yes --> Notify[Send Approval Notification]
    Approved -- No --> Rollback[Execute Compensation]
    
    Notify --> End((Approved))
    Rollback --> NotifyR[Send Rejection Notification]
    NotifyR --> EndR((Rejected))
```

---

## 🧠 Decision Wisdom (DMN)

We utilize **Decision Model and Notation (DMN)** to keep business rules decoupled from the code.

### 1. Risk Scoring Engine
The system evaluates applications based on:
- **Credit History (1-5)**
- **Debt to Income Ratio (%)**
- **Loan Amount**
- **Employment Status**
- **Age**

| Risk Category | Action | Score Range |
| :--- | :--- | :--- |
| **LOW** | APPROVE | 0 - 30 |
| **MEDIUM** | REVIEW | 31 - 65 |
| **HIGH** | REVIEW | 66 - 85 |
| **CRITICAL** | REJECT | 86 - 100 |

### 2. Approval Authority
Dynamic assignment based on loan amount:
- **Manager:** < $100,000
- **Senior Manager:** $100k - $500k
- **Director:** > $500,000

---

## ⚡ Technical Innovation: PostgreSQL 18 Compatibility

This project solves a critical industry challenge: **Running Camunda 7.21 on PostgreSQL 18**. 

### The Challenge
PostgreSQL 18's metadata API changes caused Camunda's schema detection to break, leading to persistent "Relation already exists" or "Tables are missing" errors.

### The Solution: `PostgreSQL18SchemaPlugin`
We developed a sophisticated `ProcessEnginePlugin` that:
- **Direct SQL Detection:** Uses robust information_schema queries instead of the broken JDBC metadata API.
- **Pipeline Interception:** Dynamically replaces the `CommandExecutorSchemaOperations` with a `NoOp` executor when tables are detected, preventing conflict.
- **Driver Optimization:** Upgrades the JDBC driver to `42.7.10` for native PG18 support.

---

## 🛠️ Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 18

### 1. Configuration (Security First 🔒)
The system is configured to use environment variables for sensitive data. Never commit your passwords!

```bash
# Set your environment variables
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export CAMUNDA_ADMIN_PASSWORD=secure_admin_pass
```

### 2. Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

### 3. Access URLs
- **Main Portal:** [http://localhost:8080/](http://localhost:8080/) (Login: admin / your_pass)
- **REST API Docs:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Metrics (Prometheus):** [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

---

## 📸 Visual Gallery
*(Coming Soon)*
- [ ] Dashboard View
- [ ] BPMN Flow Diagram
- [ ] DMN Table Configuration
- [ ] PostgreSQL Schema View

---

## 📜 License
Distributed under the MIT License. See `LICENSE` for more information.

---
**Developed with ❤️ for high-performance workflow automation.**
