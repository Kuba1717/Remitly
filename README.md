# Remitly Home Exercise 2025 for Interns

This project provides a RESTful API for accessing a SWIFT codes database. API documentation is generated via Swagger UI.


## Run
Fill in the database credentials in the .env file.
```bash
MYSQL_DATABASE=database
MYSQL_ROOT_PASSWORD=
MYSQL_USER=
MYSQL_PASSWORD=
```

To build and run the application with Docker, execute:

```bash
docker-compose up -d --build
```
## Swagger UI
Access the interactive API docs at:
```bash
http://localhost:8080/api-docs/swagger-ui/index.html
```

## API Endpoints
### Gets SWIFT code details.
```bash
GET /v1/swift-codes/{swift-code}
```


Headquarter Response:

```bash
{
  "address": "string",
  "bankName": "string",
  "countryISO2": "string",
  "countryName": "string",
  "isHeadquarter": true,
  "swiftCode": "string",
  "branches": [
    {
      "address": "string",
      "bankName": "string",
      "countryISO2": "string",
      "isHeadquarter": false,
      "swiftCode": "string"
    }
  ]
}
```

Branch Response:

```bash
{
  "address": "string",
  "bankName": "string",
  "countryISO2": "string",
  "countryName": "string",
  "isHeadquarter": false,
  "swiftCode": "string"
}
```


### Returns all SWIFT codes for a country:


```bash
GET /v1/swift-codes/country/{countryISO2code}
```
Response:

```bash
{
  "countryISO2": "string",
  "countryName": "string",
  "swiftCodes": [
    {
      "address": "string",
      "bankName": "string",
      "countryISO2": "string",
      "isHeadquarter": true,
      "swiftCode": "string"
    }
  ]
}
```

### Adds a new SWIFT code.

```bash
POST /v1/swift-codes
```


Request:
```bash
{
  "address": "string",
  "bankName": "string",
  "countryISO2": "string",
  "countryName": "string",
  "isHeadquarter": true,
  "swiftCode": "string"
}
```
Response:

```bash
{
  "message": "string"
}
```

###  Deletes a SWIFT code.
```bash
DELETE /v1/swift-codes/{swift-code}
```
Response:

```bash
{
  "message": "string"
}
```
