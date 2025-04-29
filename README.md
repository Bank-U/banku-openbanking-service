# BankU OpenBanking Service

OpenBanking integration service for BankU, implemented with Spring Boot.

## Features

- Integration with financial institutions via OpenBanking APIs
- Account information retrieval
- Transaction history access
- Real-time balance updates
- Secure data handling and encryption

## Technologies

- Java 17
- Spring Boot 3.4.4
- Spring Data MongoDB
- Spring Kafka
- OpenBanking API
- Docker
- Docker Compose

## Configuration

### Required Environment Variables

The service requires the following environment variables to be set:

```bash
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://banku:secret@localhost:27017/banku-openbanking?authSource=admin

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092

# JWT Configuration
jwt.secret=xxxxxxx

# Plaid Configuration
plaid.client-id=your-plaid-client-id
plaid.secret=your-plaid-secret
plaid.env=sandbox
```

### Local Development Setup

1. Create a `application-local.properties` file in the project root with the required environment variables
2. Start the required services using Docker Compose:
   ```bash
   docker-compose up -d
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The service will be available at `http://localhost:8082`

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── banku/
│               └── openbankingservice/
│                   ├── config/         # Service configurations
│                   ├── controller/     # REST Controllers
│                   ├── model/          # Data models
│                   ├── repository/     # Data repositories
│                   ├── service/        # Business logic
│                   └── integration/    # External API integrations
└── test/                              # Unit and Integration Tests
```

## API Endpoints

### OpenBanking

- `POST /api/v1/openbanking/connect`: Connect to a financial institution
- `GET /api/v1/openbanking/accounts/{userId}`: Get connected accounts
- `GET /api/v1/openbanking/transactions/{accountId}`: Get account transactions
- `GET /api/v1/openbanking/balance/{accountId}`: Get account balance

## API Documentation

The service provides Swagger UI for API documentation at:
- Swagger UI: `http://localhost:8082/api/v1/openbanking/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8082/api/v1/openbanking/v3/api-docs`

## Development

### Requirements

- Java 17
- Docker
- Docker Compose
- Plaid API credentials

### Local Execution

1. Clone the repository
2. Configure Plaid API credentials in `application-local.properties`
3. Run `docker-compose up -d` to start MongoDB and Kafka
4. Run the application with `./mvnw spring-boot:run`

### Tests

Run tests with:
```bash
./mvnw test
```

## License

This project is private and confidential.
