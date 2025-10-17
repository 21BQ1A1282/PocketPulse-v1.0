<h2 align="center">
   💰 PocketPulse Backend
</h2>

<h3>A complete Spring Boot backend for personal finance management with secure authentication, real-time analytics, and automated reporting features.</h3>

## 🚀 Quick Start
### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Installation
1. **Clone the repository**
    ```bash
    git clone https://github.com/21BQ1A1282/PocketPulse-v1.0-Server.git
    cd PocketPulse-v1.0-Server
    ```

2. **Clean or run application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## ⚙️ Configuration
1. Configure or Edit application.properties
   ```properties
   # Database
   spring.datasource.url=jdbc:postgresql://localhost:5432/pocketpulse
   spring.jpa.hibernate.ddl-auto=update

   # JWT Security
   jwt.secret=your-256-bit-secret-key
   jwt.expiration.ms=86400000

   # Email
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587

   ...
   ```

## 🛠️ Technology Stack
- **Backend Framework**: Spring Boot 3.1.0
- **Language**: Java 17
- **Database**: PostgreSQL with JPA/Hibernate
- **Authentication**: JWT + Spring Security
- **Build Tool**: Maven
- **Containerization**: Docker


## 💡 Core Features
### Security & Authentication
- JWT-based stateless authentication
- BCrypt password encryption
- Role-based access control
- CORS configuration for frontend

### Financial Management
- Complete CRUD operations for transactions
- Category-based organization
- Real-time balance calculations
- Multi-tenant data isolation

### Analytics & Reporting
- Dashboard with financial insights
- Advanced filtering and search
- Excel export capabilities
- Automated email reporting

### Automation
- Scheduled daily reminders
- Expense summary emails
- Background job processing


## 📊 Features in Detail
### Real-time Analytics
- Total balance, income, and expense calculations
- Recent transaction tracking
- Category-wise spending analysis

### Advanced Filtering
- Date range filtering
- Keyword search across transactions
- Multiple sorting options
- Type-based filtering (income/expense)

### Automated Reporting
- Daily expense summaries via email
- Excel export functionality
- Scheduled financial reminders

## 🔒 Security Features
- Password hashing with BCrypt
- JWT token expiration and validation
- SQL injection prevention
- Cross-origin resource sharing (CORS) configuration
- Data isolation between users


Frontend required for full functionality: [PocketPulse Frontend](https://github.com/21BQ1A1282/PocketPulse-v1.0-Client)