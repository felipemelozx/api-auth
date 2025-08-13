## API de Autenticação
Esta é uma API de autenticação de usuários desenvolvida em Java com Spring Boot, que utiliza tokens JWT para segurança. A API permite a criação de contas e login, além de realizar a verificação de e-mail por meio da geração de tokens aleatórios usando UUID. Para o envio de e-mails, a aplicação integra o serviço do Gmail utilizando o `spring-boot-starter-mail`. 

Além disso, os tokens de verificação são armazenados no Redis junto com o e-mail do usuário para garantir validade e facilitar a validação durante o processo de confirmação.

---

## Technology

<div>
  <img src="https://skillicons.dev/icons?i=java" height="40" alt="java logo"/>
  <img src="https://skillicons.dev/icons?i=mysql" height="40" alt="mysql logo"/>
  <img src="https://skillicons.dev/icons?i=spring" height="40" alt="spring logo"/>
  <img src="https://skillicons.dev/icons?i=hibernate" height="40" alt="hibernate logo"/>
  <img src="https://skillicons.dev/icons?i=redis" height="40" alt="redis logo"/>
  <img src="https://skillicons.dev/icons?i=githubactions" height="40" alt="github actions logo"/>
  <img src="https://skillicons.dev/icons?i=docker" height="40" alt="docker logo"/>
</div>

---

### Dependências

| Dependência                    | Descrição                                 | Escopo   |
|--------------------------------|-------------------------------------------|----------|
| Spring Boot Starter Data JPA   | Persistência de dados com JPA/Hibernate   | (padrão) |
| Spring Boot Starter Web        | Framework web para APIs RESTful           | (padrão) |
| Spring Boot Starter Mail       | Suporte a envio de e-mails                | (padrão) |
| Spring Boot Starter Thymeleaf  | Template engine para views HTML           | (padrão) |
| Java JWT (Auth0) 4.4.0         | Geração e validação de JSON Web Tokens    | (padrão) |
| Spring Boot Starter Security   | Segurança autenticação e autorização      | (padrão) |
| Spring Boot Starter Data Redis | Integração com Redis para cache/mensagens | (padrão) |
| MySQL Connector/J              | Driver JDBC para MySQL                    | runtime  |
| H2 Database                    | Banco em memória para testes              | test     |
| embedded-redis 0.7.3           | Redis embarcado para testes               | test     |
| Spring Boot Starter Test       | Framework de teste do Spring Boot         | test     |
| Spring Security Test           | Testes de segurança                       | test     |

---
## Estrutura de Pastas

```plaintext
.github/
├── workflows/
│     └── java-ci.yml              # Workflow para build e test da aplicação
│
src/
├── main/
│   ├── java/
│   │   └── dev/felipemelozx/api_auth/
│   │        ├── controller/       # Endpoints da API (REST Controllers)
│   │        ├── dto/              # Objetos de transferência de dados (Request/Response)
│   │        ├── entity/           # Entidades JPA (Mapeadas para o banco de dados)
│   │        ├── repository/       # Interfaces de acesso ao banco (JPA)
│   │        ├── infra/            # Configuração de segurança (Autenticação JWT)
│   │               └── config/    # Configuração de dependências gerais ou de dependências externas. (Redis, CROS)
│   │               └── security/  #Configuração de segurança da aplicação (filtro de request, jwt, etc)
│   │        └── service/          # Regras de negócio e lógica da aplicação
│   │        └── utils/            # classes utilitárias
│   └── resources/
│       └── application.yaml       # Configurações (Porta, banco, JWT, etc)
│       └── templates/             # Templates para o envio de email
```

---
## Endpoints

| Método | Rota                    | Descrição                                                   | Autenticação |
|--------|-------------------------|-------------------------------------------------------------|--------------|
| POST   | `/api/v1/auth/register` | Cadastra um novo usuário                                    | ❌            |
| POST   | `/api/v1/auth/login`    | Autentica usuário e retorna JWT                             | ❌            |
| GET    | `/api/v1/club/secret`   | Rota protegida so para user logados e com e-mail verificado | ✅            |


## Fluxo de Autenticação
1. Cadastro: `/api/v1/auth/register` cria o usuário.

2. Verificar e-mail:`/api/v1/auth/verify-email{token}` verifica o e-mail com o `token` enviado para o e-mail cadastrado.

3. Login: `/api/v1/auth/login` retorna Access + Refresh Tokens.

4. Acesso protegido: Endpoints de `/api/v1/club/secret` exigem Access Token no header Authorization: Bearer <token>.

5. Renovação: `/auth/v1/refresh` gera novo Access `Token`.

6. Logout: Invalida Refresh Token.




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
