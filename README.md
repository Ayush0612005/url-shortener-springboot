# 🔗 URL Shortener API

A production-inspired URL Shortener REST API built with **Java, Spring Boot, Spring Security, JWT Authentication, Redis, and MySQL**.

This project allows users to register, authenticate, shorten URLs, manage them, view analytics, and securely redirect users using unique Base62 short codes.

---

## 🚀 Features

- User Registration & Login
- JWT Authentication
- BCrypt Password Encryption
- URL Shortening using Base62 Encoding
- URL Redirection
- URL Expiration Support
- User-specific URL Management
- Analytics Dashboard
    - Click Count
    - Last Accessed Time
    - Status (Active / Expired)
- Redis Caching
- Global Exception Handling
- Request Validation
- Swagger API Documentation
- Unit Testing with JUnit & Mockito

---

## 🛠 Tech Stack

### Backend
- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate

### Database
- MySQL

### Cache
- Redis (Memurai)

### Authentication
- JWT
- BCrypt

### Documentation
- Swagger / OpenAPI

### Testing
- JUnit 5
- Mockito

---

## 📂 Project Structure

```
src
├── controller
├── service
├── repository
├── entity
├── dto
├── security
├── exception
├── redis
├── util
└── config
```

---

## 🔐 Authentication

Protected APIs require a JWT token.

```
Authorization: Bearer <your-jwt-token>
```

---

## 📌 API Endpoints

### Authentication

| Method | Endpoint |
|---------|----------|
| POST | /api/auth/register |
| POST | /api/auth/login |

### URL Management

| Method | Endpoint |
|---------|----------|
| POST | /api/urls |
| GET | /api/urls/my-urls |
| PUT | /api/urls/{id}/expiration |
| DELETE | /api/urls/{id} |
| GET | /api/urls/{id}/analytics |

### Redirect

| Method | Endpoint |
|---------|----------|
| GET | /{shortCode} |

---

## ⚡ Redis Cache

This project uses Redis to cache URL mappings, reducing database lookups and improving redirect performance.

Cache Strategy:
- Check Redis first
- If cache miss → Query MySQL
- Store result in Redis
- Return URL

---

## 📊 Analytics

Each shortened URL tracks:

- Total Click Count
- Creation Date
- Expiration Date
- Last Accessed Time
- Current Status

---

## 📖 Swagger Documentation

After starting the application:

```
http://localhost:8080/swagger-ui/index.html
```

---

## ⚙️ Running the Project

### Clone

```bash
git clone https://github.com/Ayush0612005/url-shortener-springboot.git
```

### Configure

Update:

```
application.properties
```

with your:

- MySQL credentials
- JWT secret
- Redis configuration

### Run

```bash
mvn spring-boot:run
```

---

## 📸 Screenshots

- Swagger UI
- Postman Testing
- MySQL Database
- Redis CLI

(Add screenshots here)

---

## 🔮 Future Improvements

- Custom aliases
- QR Code generation
- Rate Limiting
- Docker support
- URL statistics dashboard
- Scheduled cleanup of expired URLs

---

## 👨‍💻 Author

**Ayush Kulshreshtha**

- GitHub: https://github.com/Ayush0612005
- LinkedIn: https://www.linkedin.com/in/ayush-kulshreshtha-0066661b9

---

⭐ If you found this project useful, consider giving it a star.
