# Fryzjer Backend

Backend dla zakładu fryzjerskiego - Spring Boot + PostgreSQL

## Wymagania
- Java 21
- Maven
- PostgreSQL (uruchomiony na localhost:5432)

## Konfiguracja

Domyślna konfiguracja w application.properties:
- URL: jdbc:postgresql://localhost:5432/postgres
- User: postgres
- Password: postgres

Zmień hasło w application.properties jeśli masz inne.

## Uruchomienie

```bash
mvn clean spring-boot:run
```

Tabele i dane testowe utworzą się automatycznie!

## Linki
- Aplikacja: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Endpointy API

| Zasób | Metoda | Endpoint |
|-------|--------|----------|
| Klienci | GET, POST | /api/klienci |
| Klienci | GET, PUT, DELETE | /api/klienci/{id} |
| Fryzjerzy | GET, POST | /api/fryzjerzy |
| Fryzjerzy | GET, PUT, DELETE | /api/fryzjerzy/{id} |
| Usługi | GET, POST | /api/uslugi |
| Usługi | GET, PUT, DELETE | /api/uslugi/{id} |
| Wizyty | GET, POST | /api/wizyty |
| Wizyty | GET, PUT, DELETE | /api/wizyty/{id} |
| Wizyty klienta | GET | /api/wizyty/klient/{id} |
| Wizyty fryzjera | GET | /api/wizyty/fryzjer/{id} |
