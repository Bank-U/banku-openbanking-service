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

## Configuration

### MongoDB

```properties
spring.data.mongodb.uri=mongodb://banku:secret@localhost:27017/banku-openbanking?authSource=admin
```

### Kafka

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=banku-openbanking-group
spring.kafka.consumer.auto-offset-reset=earliest
```

### OpenBanking

```properties
openbanking.api.url=https://api.openbanking.com
openbanking.api.key=your-api-key
openbanking.api.secret=your-api-secret
```

## Development

### Requirements

- Java 17
- Docker
- Docker Compose
- OpenBanking API credentials

### Local Execution

1. Clone the repository
2. Configure OpenBanking API credentials in `application.properties`
3. Run `docker-compose up -d` to start MongoDB and Kafka
4. Run the application with `./mvnw spring-boot:run`

### Tests

Run tests with:
```bash
./mvnw test
```

## Docker

The service can be run using Docker:

```bash
docker-compose up -d
```

The Docker Compose file includes:
- MongoDB for data storage
- Kafka for event streaming

## Security

- All API calls are encrypted using TLS
- Sensitive data is encrypted at rest
- API keys and secrets are stored securely
- Regular security audits and updates

## License

This project is private and confidential.
