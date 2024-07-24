## API de Autenticação

This is a user authentication API.

## Technology

Here are the technologies used in this project:

* Java version 21.0.0
* Spring Boot 3.3.1
* MySQL
* Docker

## Services Used

* GitHub
* Render

## Dependencies

* Spring Boot Starters
  - spring-boot-starter-data-jpa
  - spring-boot-starter-web
  - spring-boot-starter-security
  - spring-boot-starter-test (scope test)
* MySQL Connector
* java-jwt (version 4.4.0)
* spring-security-test (scope test)

## Getting Started

### Pre-requisites

- Java 21
- Maven

### Build and Run

1. Clone the repository:
    ```sh
    git clone https://github.com/felipemelozx/api-auth.git
    cd api-auth
    ```

2. Build the project:
    ```sh
    mvn clean install
    ```

3. Run the project:
    ```sh
    mvn spring-boot:run
    ```

4. Access the application at `http://localhost:8080`

## How to Use

### Endpoints

- `/auth/login` - Authenticate user and get JWT token.
- `/auth/register` - Register a new user.

## Features

The main features of the application are:
 - User registration and authentication
 - JWT token-based authentication

## Links

- Repository: https://github.com/felipemelozx/api-auth

## Versioning

0.0.1-SNAPSHOT

## Authors

* **Felipe**

Please follow GitHub and join us! Thanks for visiting and happy coding!
