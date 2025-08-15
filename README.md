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
│   │   └── dev/felipemlozx/api_auth/
│   │        ├── controller/       # Endpoints da API (REST Controllers)
│   │        ├── core/             # Classes para transição de dados entre módulos
│   │        ├── dto/              # Objetos de transferência de dados (Request/Response)
│   │        ├── entity/           # Entidades JPA (Mapeadas para o banco de dados)
│   │        ├── repository/       # Interfaces de acesso ao banco (JPA)
│   │        ├── infra/            # Configuração de infraestrutura
│   │               └── config/    # Configuração de dependências gerais ou de dependências externas (Redis, CORS)
│   │               └── security/  # Configuração de segurança da aplicação (filtro de request, jwt, etc)
│   │        ├── services/         # Regras de negócio e lógica da aplicação
│   │        └── utils/            # Classes utilitárias
│   └── resources/
│       └── application.yaml       # Configurações (Porta, banco, JWT, etc)
│       └── templates/             # Templates para o envio de email
```

---
## Endpoints

| Método | Rota                                    | Descrição                                                   | Autenticação |
|--------|-----------------------------------------|-------------------------------------------------------------|--------------|
| POST   | `/api/v1/auth/register`                 | Cadastra um novo usuário                                    | ❌            |
| POST   | `/api/v1/auth/login`                    | Autentica usuário e retorna JWT                             | ❌            |
| GET    | `/api/v1/auth/verify-email/{token}`     | Verifica o e-mail do usuário com o token                    | ❌            |
| GET    | `/api/v1/club/secret`                   | Rota protegida só para usuários logados e com e-mail verificado | ✅            |


## Fluxo de Autenticação
1. Cadastro: `/api/v1/auth/register` cria o usuário.

2. Verificar e-mail: `/api/v1/auth/verify-email/{token}` verifica o e-mail com o `token` enviado para o e-mail cadastrado.

3. Login: `/api/v1/auth/login` retorna Access + Refresh Tokens.

4. Acesso protegido: Endpoints protegidos exigem Access Token no header Authorization: Bearer <token>.

## Como instalar o projeto

Esta seção te guiará passo a passo para configurar e executar a API de autenticação em sua máquina local.

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 21** - [Download oficial](https://adoptium.net/)
- **Maven 3.6+** - [Download oficial](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** - [Download oficial](https://dev.mysql.com/downloads/)
- **Redis 6.0+** - [Download oficial](https://redis.io/download)
- **Git** - [Download oficial](https://git-scm.com/)

> **Dica**: Se preferir usar Docker, você pode executar MySQL e Redis em containers. Veja a seção [Executando com Docker](#executando-com-docker) abaixo.

### Configuração do Banco de Dados MySQL

1. **Inicie o servidor MySQL** em sua máquina
2. **Crie um novo banco de dados**:
   ```sql
   CREATE DATABASE testeDb;
   ```
3. **Crie um usuário** (opcional, mas recomendado):
   ```sql
   CREATE USER 'dev'@'localhost' IDENTIFIED BY 'teste123';
   GRANT ALL PRIVILEGES ON testeDb.* TO 'dev'@'localhost';
   FLUSH PRIVILEGES;
   ```
4. **Verifique se as configurações** no arquivo `application.yaml` estão corretas:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/testeDb
       username: dev
       password: teste123
   ```

### Configuração do Redis

1. **Inicie o servidor Redis** em sua máquina:
   ```bash
   redis-server
   ```
2. **Verifique se está rodando** na porta padrão 6379:
   ```bash
   redis-cli ping
   # Deve retornar: PONG
   ```
3. **Ou ajuste a configuração** no `application.yaml` se necessário:
   ```yaml
   spring:
     data:
       redis:
         host: localhost
         port: 6379
   ```

### Configuração do Email (Gmail)

Para enviar e-mails de verificação, você precisa configurar o Gmail:

1. **Ative a verificação em duas etapas** na sua conta Google
2. **Gere uma senha de app**:
   - Acesse [Conta Google](https://myaccount.google.com/)
   - Segurança → Verificação em duas etapas → Senhas de app
   - Gere uma senha para "Email"
3. **Configure a variável de ambiente**:
   ```bash
   export EMAIL_PASSWORD="sua_senha_de_app_aqui"
   ```
4. **Ou adicione no arquivo** `application.yaml` (não recomendado para produção):
   ```yaml
   spring:
     mail:
       username: seu_email@gmail.com
       password: ${EMAIL_PASSWORD}
   ```

### Executando com Docker (Opcional)

Se preferir usar Docker para MySQL e Redis:

```bash
# MySQL
docker run --name mysql-auth -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=testeDb -e MYSQL_USER=dev -e MYSQL_PASSWORD=teste123 -p 3306:3306 -d mysql:8.0

# Redis
docker run --name redis-auth -p 6379:6379 -d redis:6.2-alpine
```

### Download e Execução

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/felipemelozx/api-auth.git
   cd api-auth
   ```

2. **Compile o projeto**:
   ```bash
   mvn clean install
   ```

3. **Execute a aplicação**:
   ```bash
   mvn spring-boot:run
   ```

4. **Acesse a aplicação** em `http://localhost:8080`

### Solução de Problemas Comuns

| Problema | Solução |
|----------|---------|
| `Connection refused` no MySQL | Verifique se o MySQL está rodando na porta 3306 |
| `Connection refused` no Redis | Verifique se o Redis está rodando na porta 6379 |
| `Email authentication failed` | Verifique a senha de app do Gmail |
| `Port 8080 already in use` | Mude a porta no `application.yaml` ou pare outros serviços |

## Como usar a API

Agora que você tem a aplicação rodando, vamos aprender como usar cada endpoint e testar a funcionalidade.

### 🔗 Endpoints Disponíveis

| Endpoint | Método | Descrição | Autenticação |
|-----------|--------|-----------|--------------|
| `/api/v1/auth/register` | POST | Cadastra um novo usuário | ❌ Público |
| `/api/v1/auth/login` | POST | Autentica usuário e retorna JWT | ❌ Público |
| `/api/v1/auth/verify-email/{token}` | GET | Verifica e-mail com token | ❌ Público |
| `/api/v1/club/secret` | GET | Rota protegida com frases motivacionais | ✅ JWT obrigatório |

#### **Opção 2: Usando Postman/Insomnia**
1. Importe os endpoints acima
2. Configure as variáveis de ambiente para o token JWT
3. Teste o fluxo completo de autenticação

### Fluxo Completo de Teste

1. **Registre um usuário** → Receba confirmação de criação
2. **Verifique seu e-mail** → Clique no link enviado ou use o token
3. **Faça login** → Receba access e refresh tokens
4. **Acesse rota protegida** → Use o access token no header Authorization
5. **Teste renovação** → Use o refresh token quando necessário


## Como usar

### Endpoints

- `/api/v1/auth/register` - Register a new user
- `/api/v1/auth/login` - Authenticate user and get JWT token
- `/api/v1/auth/verify-email/{token}` - Verify user email with token
- `/api/v1/club/secret` - Protected route for authenticated users
## Exemplos de Requisição

#### Registro
```http
POST /api/v1/auth/register
Content-Type: application/json
{
  "username": "UserNameTest",
  "email": "Test@email.com",
  "password": "Strong#123"
}
```
##### Resposta

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "success": true,
  "message": "User created. Verify your email.",
  "data": null
}
```

**Resposta com erros de validação:**
```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "success": false,
  "message": "Validation errors",
  "data": [
    "Username must be at least 3 characters long",
    "Email must be valid",
    "Password must contain at least 8 characters, one uppercase, one lowercase, one number and one special character"
  ]
}
```

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "UserNameTest",
  "password": "Strong#123"
}
```

**Resposta de sucesso:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Resposta com erro - Email não verificado:**
```http
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
  "success": false,
  "message": "Email not verified",
  "data": null
}
```

**Resposta com erro - Credenciais inválidas:**
```http
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
  "success": false,
  "message": "User or password is incorrect",
  "data": null
}
```

#### Verificação de Email
```http
GET /api/v1/auth/verify-email/{token}
```

**Resposta de sucesso:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Email verified",
  "data": null
}
```

**Resposta com erro - Token inválido/expirado:**
```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
  "success": false,
  "message": "Invalid or expired token",
  "data": null
}
```

#### Acesso à Rota Protegida
```http
GET /api/v1/club/secret
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Resposta de sucesso:**
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": true,
  "message": "Success",
  "data": "UserNameTest are authorization user@email.com Cada desafio é uma oportunidade de crescimento."
}
```

## Erros Comuns
| Código | Mensagem                | Causa                              |
|--------|-------------------------|------------------------------------|
| 400    | `Invalid credentials`   | Email ou senha inválidos           |
| 401    | `Token expired`         | Access Token expirou               |
| 403    | `Invalid refresh token` | Refresh Token inválido ou revogado |
| 409    | `User already exists`   | Email já cadastrado                |


## Features

The main features of the application are:
 - User registration and authentication
 - JWT token-based authentication
 - Email verification system
 - Redis-based token storage
 - Protected routes with role-based access
 - Comprehensive validation and error handling

## Links

- Repository: https://github.com/felipemelozx/api-auth

## Versioning

0.0.1-SNAPSHOT

## Authors

* **Felipe**

Please follow GitHub and join us! Thanks for visiting and happy coding!
