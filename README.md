# Eazi Wallet

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com)

Eazi Wallet is a simple, modern, and extensible open-source wallet application built with Java and Spring Boot. It provides a solid foundation for managing digital wallets, handling transactions, and can be extended for various financial services.

## What is Eazi-Wallet?

Eazi Wallet is a core banking system that provides a robust, scalable, and secure infrastructure for financial applications. It allows for the management of wallets and the transactions associated with them. It is designed to be easily deployed and managed, providing a RESTful API for integration with other services.

## Common use cases

*   Creating and managing user wallets.
*   Funding wallets.
*   Transferring funds between wallets.

## Requirements
*   Java 21
*   Maven 3.x
*   PostgreSQL 12+

## Getting Started

### Prerequisites

Ensure you have the following installed on your system:
- Java 21 Development Kit (JDK)
- Apache Maven
- PostgreSQL

### Database Setup

The application connects to a PostgreSQL database. The connection details are configured using the following environment variables:

```bash
export DATABASE_URL=<your_database_url>
export DATABASE_USERNAME=<your_database_username>
export DATABASE_PASSWORD=<your_database_password>
```
For example:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/eazi_wallet
export DATABASE_USERNAME=user
export DATABASE_PASSWORD=password
```

The application uses Flyway to manage database schemas. The migrations will be applied automatically on startup.

### Build

To build the application, run the following command from the root directory:

```bash
./mvnw clean install
```
This will compile the code, run tests, and package the application into a JAR file in the `target/` directory.

### Run

You can run the application using the following command:

```bash
java -jar target/eazi_wallet-0.0.1-SNAPSHOT.jar
```
Alternatively, you can run it directly with Maven:

```bash
./mvnw spring-boot:run
```
The application will start and be accessible at `http://localhost:8080`.

## API Documentation

The Eazi Wallet API is documented using Swagger/OpenAPI. Once the application is running, you can access the Swagger UI at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

This interface provides detailed information about all the available endpoints, their parameters, and allows you to test them directly from your browser.

## How to contribute?

Contributions are welcome! If you would like to contribute to the project, please follow these steps:

1.  Fork the repository.
2.  Create a new branch for your feature or bug fix.
3.  Make your changes and commit them with clear messages.
4.  Push your changes to your fork.
5.  Create a pull request to the main repository.

## Community Support

If you need help or have any questions, feel free to open an issue on the GitHub repository.
