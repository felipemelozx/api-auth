# API de Autenticação

Esta é uma API de autenticação de usuários desenvolvida em Java com Spring Boot 3.3.1, que utiliza tokens JWT para segurança. A API permite a criação de contas e login, além de realizar a verificação de e-mail por meio da geração de tokens aleatórios usando UUID. Para o envio de e-mails, a aplicação integra o serviço do Gmail utilizando o `spring-boot-starter-mail`.

Além disso, os tokens de verificação são armazenados no Redis junto com o e-mail do usuário para garantir validade e facilitar a validação durante o processo de confirmação.

---

## Sumário

- [Tecnologias](#tecnologias)
- [Dependências](#dependências)
- [Requisitos](#requisitos)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [CORS](#cors)
- [JWT e Segurança](#jwt-e-segurança)
- [Endpoints](#endpoints)
- [Fluxo de Autenticação](#fluxo-de-autenticação)
- [Como instalar o projeto](#como-instalar-o-projeto)
  - [Pré-requisitos](#pré-requisitos)
  - [Configuração do Banco de Dados MySQL](#configuração-do-banco-de-dados-mysql)
  - [Configuração do Redis](#configuração-do-redis)
  - [Configuração do Email (Gmail)](#configuração-do-email-gmail)
  - [Executando com Docker (Banco e Cache)](#executando-com-docker-banco-e-cache)
  - [Executando com Docker Compose](#executando-com-docker-compose)
  - [Build e execução da aplicação (Dockerfile)](#build-e-execução-da-aplicação-dockerfile)
  - [Download e Execução (Local)](#download-e-execução)
  - [Solução de Problemas Comuns](#solução-de-problemas-comuns)
- [Como usar a API](#como-usar-a-api)
  - [Endpoints Disponíveis](#endpoints-disponíveis)
  - [Fluxo Completo de Teste](#fluxo-completo-de-teste)
  - [Exemplos de Requisição](#exemplos-de-requisição)
- [Erros Comuns](#erros-comuns)
- [Features](#features)
- [Configurações Importantes](#configurações-importantes)
  - [Variáveis de Ambiente](#variáveis-de-ambiente)
  - [Configurações do Banco](#configurações-do-banco)
- [Testes](#testes)
- [Links](#links)
- [Versioning](#versioning)
- [Authors](#authors)

---

## Tecnologias

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

| Dependência                    | Versão | Descrição                                 | Escopo   |
|--------------------------------|--------|-------------------------------------------|----------|
| Spring Boot Starter Data JPA   | 3.3.1  | Persistência de dados com JPA/Hibernate   | (padrão) |
| Spring Boot Starter Web        | 3.3.1  | Framework web para APIs RESTful           | (padrão) |
| Spring Boot Starter Mail       | 3.3.1  | Suporte a envio de e-mails                | (padrão) |
| Spring Boot Starter Thymeleaf  | 3.3.1  | Template engine para views HTML           | (padrão) |
| Java JWT (Auth0) 4.4.0         | 4.4.0  | Geração e validação de JSON Web Tokens    | (padrão) |
| Spring Boot Starter Security   | 3.3.1  | Segurança autenticação e autorização      | (padrão) |
| Spring Boot Starter Data Redis | 3.3.1  | Integração com Redis para cache/mensagens | (padrão) |
| MySQL Connector/J              | -      | Driver JDBC para MySQL                    | runtime  |
| H2 Database                    | -      | Banco em memória para testes              | test     |
| embedded-redis 0.7.3           | 0.7.3  | Redis embarcado para testes               | test     |
| Spring Boot Starter Test       | 3.3.1  | Framework de teste do Spring Boot         | test     |
| Spring Security Test           | 3.3.1  | Testes de segurança                       | test     |

**Requisitos:**
- Java 21
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

---
## Estrutura de Pastas

```plaintext
.github/
├── workflows/
│     └── java-ci.yml              # Workflow para build e test da aplicação
│
docker/
├── docker-compose.yaml           # MySQL e Redis para desenvolvimento
├── Dockerfile                    # Imagem da aplicação (JAR)
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
└── test/
    └── java/ ...                  # Testes unitários e de integração (H2 + Redis embarcado)
```

---
## CORS

- Origem permitida: `http://localhost:4200`
- Métodos: `GET, POST, PUT, DELETE, OPTIONS`
- Credenciais: `false`

Caso precise liberar outras origens, ajuste em `WebConfig` (`dev.felipemlozx.api_auth.infra.config.WebConfig`).

---
## JWT e Segurança

- Context path: `/api/v1`
- Endpoints públicos: `POST /auth/login`, `POST /auth/register`, `GET /auth/verify-email/**`, `GET /auth/refresh`, `POST /auth/resend-verification-email/**`
- Endpoints protegidos: demais rotas exigem `Authorization: Bearer <accessToken>`
- Claims do Access Token: `id`, `name`, `email`, `roles`
- Expiração: Access Token (1h), Refresh Token (7 dias)

A chave secreta do JWT é lida da propriedade `api.secret.key` (pode ser definida via variável de ambiente, ver seção de variáveis).

---
## Endpoints

| Método | Rota                                    | Descrição                                                   | Autenticação |
|--------|-----------------------------------------|-------------------------------------------------------------|--------------|
| POST   | `/api/v1/auth/register`                 | Cadastra um novo usuário                                    | ❌            |
| POST   | `/api/v1/auth/login`                    | Autentica usuário e retorna JWT                             | ❌            |
| GET    | `/api/v1/auth/verify-email/{token}`     | Verifica o e-mail do usuário com o token                    | ❌            |
| GET    | `/api/v1/auth/refresh`                  | Renova o access token usando refresh token                  | ❌            |
| POST    | `/api/v1/auth/resend-verification-email/`                   | Reenvia e-mail de verificação (configurado mas não implementado) | ❌            |
| GET    | `/api/v1/club/secret`                   | Rota protegida só para usuários logados e com e-mail verificado | ✅            |

## Fluxo de Autenticação
1. **Cadastro**: `/api/v1/auth/register` cria o usuário e envia e-mail de verificação.

2. **Verificar e-mail**: `/api/v1/auth/verify-email/{token}` verifica o e-mail com o `token` enviado para o e-mail cadastrado.

3. **Login**: `/api/v1/auth/login` retorna Access + Refresh Tokens.
4. **Renovação de Token**: `/api/v1/auth/refresh` permite renovar o access token usando o refresh token (header `X-Refresh-Token`).
5. **Acesso protegido**: Endpoints protegidos exigem Access Token no header `Authorization: Bearer <token>`.

## Como instalar o projeto

Esta seção te guiará passo a passo para configurar e executar a API de autenticação em sua máquina local.

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java 21** - [Download oficial](https://www.oracle.com/br/java/technologies/downloads/)
- **Maven 3.6+** - [Download oficial](https://maven.apache.org/download.cgi)
- **MySQL 8.0+** - [Download oficial](https://www.mysql.com/downloads/)
- **Redis 6.0+** - [Download oficial](https://redis.io/download)
- **Git** - [Download oficial](https://git-scm.com/downloads)

> Caso prefira, use Docker para executar MySQL e Redis. Veja abaixo.

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

### Executando com Docker (Banco e Cache)

Se preferir usar Docker para MySQL e Redis:

```bash
# MySQL
docker run --name mysql-auth -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=testeDb -e MYSQL_USER=dev -e MYSQL_PASSWORD=teste123 -p 3306:3306 -d mysql:8.0

# Redis
docker run --name redis-auth -p 6379:6379 -d redis:6.2-alpine
```

> Observação: A propriedade `api.secret.key` pode ser definida via variável de ambiente `API_SECRET_KEY` (Spring faz o binding automaticamente).

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

4. **Acesse a aplicação** em `http://localhost:8080/api/v1`

### Solução de Problemas Comuns

| Problema | Solução |
|----------|---------|
| `Connection refused` no MySQL | Verifique se o MySQL está rodando na porta 3306 |
| `Connection refused` no Redis | Verifique se o Redis está rodando na porta 6379 |
| `Email authentication failed` | Verifique a senha de app do Gmail |
| `Port 8080 already in use` | Mude a porta no `application.yaml` ou pare outros serviços |

## Como usar a API

Agora que você tem a aplicação rodando, vamos aprender como usar cada endpoint e testar a funcionalidade.

### Endpoints Disponíveis

| Endpoint | Método | Descrição | Autenticação |
|-----------|--------|-----------|--------------|
| `/api/v1/auth/register` | POST | Cadastra um novo usuário | ❌ Público |
| `/api/v1/auth/login` | POST | Autentica usuário e retorna JWT | ❌ Público |
| `/api/v1/auth/verify-email/{token}` | GET | Verifica e-mail com token | ❌ Público |
| `/api/v1/auth/refresh` | GET | Renova access token (header `X-Refresh-Token`) | ❌ Público |
| `/api/v1/auth/resend-verification-email/{email}` | POST | Reenvia e-mail de verificação | ❌ Público |
| `/api/v1/club/secret` | GET | Rota protegida com frases motivacionais | ✅ JWT obrigatório |


### Fluxo Completo de Teste

1. **Registre um usuário** → Receba confirmação de criação
2. **Verifique seu e-mail** → Clique no link enviado ou use o token
3. **Faça login** → Receba access e refresh tokens
4. **Acesse rota protegida** → Use o access token no header Authorization
5. **Teste renovação** → Use o refresh token quando necessário

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

**Resposta de sucesso:**
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

#### Renovação de Token
```http
GET /api/v1/auth/refresh
X-Refresh-Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
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

#### Reenvio de E-mail de Verificação
```http
POST /api/v1/auth/resend-verification-email/{email}
```

- Sucesso: `204 No Content`
- Possíveis erros: `400` (email não encontrado, tempo de verificação expirado, já verificado), `500` (falha no envio de e-mail)

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
| Código | Mensagem                       | Causa                              |
|--------|--------------------------------|------------------------------------|
| 400    | `Validation errors`            | Dados de entrada inválidos         |
| 400    | `Invalid or expired token`     | Token de verificação inválido/expirado |
| 401    | `REFRESH_TOKEN_INVALID`        | Refresh Token inválido ou revogado |
| 403    | `Email not verified`           | E-mail não foi verificado          |
| 403    | `User or password is incorrect`| Credenciais inválidas              |
| 403    | `User not register`            | Usuário não encontrado             |
| 409    | `User already exists`          | Email já cadastrado                |

## Features

As principais funcionalidades da aplicação são:
- ✅ Registro e autenticação de usuários
- ✅ Autenticação baseada em JWT com access e refresh tokens
- ✅ Sistema de verificação de e-mail
- ✅ Armazenamento de tokens no Redis
- ✅ Renovação automática de tokens
- ✅ Rota protegida com frases motivacionais
- ✅ Validação de dados de entrada
- ✅ Criptografia de senhas com BCrypt
- ✅ Configuração de segurança com Spring Security
- ✅ Suporte a cache com Redis
- ✅ Envio de e-mails com templates HTML

## Configurações Importantes

### Variáveis de Ambiente
```bash
# Senha do Gmail (obrigatória)
export EMAIL_PASSWORD="sua_senha_de_app_aqui"

# URL base para o link de verificação, (Url do front-end onde vai ter a request para o back-end)
export API_URL="http://localhost:4200/verify-email/"

# Chave secreta do JWT (obrigatória em ambientes não locais)
export API_SECRET_KEY="minha_chave_segura"
```

> Dica: O Spring Boot faz binding automático de variáveis de ambiente. `API_SECRET_KEY` substitui `api.secret.key` do `application.yaml` se definida.

### Configurações do Banco
- **MySQL**: Porta 3306, banco `testeDb`
- **Redis**: Porta 6379, cache com TTL de 5 minutos (config Geral) e 15 minutos via `RedisCacheManager`
- **JPA**: DDL auto-update habilitado

## Testes

- Execute: `mvn test`
- Banco de testes: H2 em memória
- Redis de testes: Redis embarcado (`embedded-redis 0.7.3`)

## Links

- Repository: https://github.com/felipemelozx/api-auth

## Versioning

0.0.1-SNAPSHOT

## Authors

* **[Felipe Melo](https://github.com/felipemelozx)**

Please follow GitHub and join us! Thanks for visiting and happy coding!
