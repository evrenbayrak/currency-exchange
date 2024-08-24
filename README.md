# Currency Exchange Service

## Overview

The Currency Exchange Service is a Spring Boot application that provides currency conversion functionalities, including retrieving exchange rates, converting amounts between currencies, and querying conversion history. The application uses the CurrencyLayer API for obtaining real-time exchange rates.

## Table of Contents

1. [Live Demo](#live-demo)
2. [Installation](#installation)
    - [Prerequisites](#prerequisites)
    - [Building the Docker Image](#building-the-docker-image)
    - [Running the Docker Container](#running-the-docker-container)
3. [Configuration](#configuration)
4. [API Endpoints](#api-endpoints)
    - [Get Exchange Rate](#get-exchange-rate)
    - [Convert Amount](#convert-amount)
    - [Get Conversion History by Transaction ID](#get-conversion-history-by-transaction-id)
    - [Get Conversion History by Date](#get-conversion-history-by-date)
    - [Get Supported Currencies](#get-supported-currencies)
5. [Swagger UI](#swagger-ui)
6. [H2 Console](#h2-console)
7. [License](#license)

## Live Demo

You can access a live demo of the application at:

- **Application URL**: [http://173.249.13.172:8092](http://173.249.13.172:8092)
- **Swagger UI**: [http://173.249.13.172:8092/currency/swagger-ui/index.html](http://173.249.13.172:8092/currency/swagger-ui/index.html)
- **H2 Console**: [http://173.249.13.172:8092/currency/h2-console](http://173.249.13.172:8092/currency/h2-console)


## Installation

### Prerequisites

Before you begin, ensure you have the following installed on your machine:

- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)
- **Java 21**: Make sure Java 21 is installed and set as your default JDK.
- **Gradle**: [Install Gradle](https://gradle.org/install/) if you want to build the project manually without Docker.

### Building the Docker Image

To build the Docker image for the Currency Exchange Service, use the following command:

```sh
docker build -t currency-exchange .
```

### Running the Docker Container

To run the Docker container with the necessary environment variables:

```sh
docker run -d -e API_KEY=your_api_key -p 8092:8092 currency-exchange
```

- Replace `your_api_key` with your CurrencyLayer API key.
- The application will be accessible at `http://localhost:8092`.

## Configuration

The application uses environment variables for configuration:

- **API_KEY**: Your CurrencyLayer API key. If not provided, the service will failed to retrieve exchange rates.
- **H2 Database**: The application uses an in-memory H2 database for testing and development purposes.

## API Endpoints

### Get Exchange Rate

**Description**: Retrieve the exchange rate between two currencies.

- **URL**: `/api/v1/exchange-rate/{baseCurrency}/{targetCurrency}`
- **Method**: `GET`
- **Path Variables**:
    - `baseCurrency`: The currency to convert from (e.g., `USD`).
    - `targetCurrency`: The currency to convert to (e.g., `EUR`).
- **Response**: `200 OK` with exchange rate.

**Example Request**:

```sh
curl http://localhost:8092/currency/api/v1/exchange-rate/USD/EUR
```

**Example Response**:

```json
{
"rate": 0.893
}
```

### Convert Amount

**Description**: Convert a specified amount from one currency to another.

- **URL**: `/api/v1/conversion`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
  "baseCurrency": "USD",
  "targetCurrency": "EUR",
  "amount": 100.0
  }
  ```
- **Response**: `200 OK` with conversion result.

**Example Request**:

```sh
curl -X POST http://localhost:8092/currency/api/v1/conversion \\
-H "Content-Type: application/json" \\
-d '{"baseCurrency": "USD", "targetCurrency": "EUR", "amount": 100.0}'
```

**Example Response**:

```json
{
"transactionId": "4f6d68e2-b9c5-4f53-8397-d4728b41b2d1",
"conversionAmount": 89.3
}
```

### Get Conversion History by Transaction Id

**Description**: Retrieve conversion details using a transaction Id.

- **URL**: `/api/v1/conversion-history/{transactionId}`
- **Method**: `GET`
- **Path Variables**:
    - `transactionId`: The ID of the transaction.
- **Response**: `200 OK` with transaction details.

**Example Request**:

```sh
curl http://localhost:8092/currency/api/v1/conversion-history/4f6d68e2-b9c5-4f53-8397-d4728b41b2d1
```

**Example Response**:

```json
{
"transactionId": "4f6d68e2-b9c5-4f53-8397-d4728b41b2d1",
"baseCurrency": "USD",
"targetCurrency": "EUR",
"baseAmount": 100.0,
"conversionAmount": 89.3,
"conversionDate": "2024-08-24T14:45:00"
}
```

### Get Conversion History by Date

**Description**: Retrieve a paginated list of conversions that occurred on a requested date.

- **URL**: `/api/v1/conversion-history`
- **Method**: `GET`
- **Query Parameters**:
    - `date`: The date of the conversions (format: `YYYY-MM-DD`).
    - `page`: The page number (default: 0).
    - `size`: The number of records per page (default: 10).
- **Response**: `200 OK` with a list of conversion history.

**Example Request**:

```sh
curl http://localhost:8092/currency/api/v1/conversion-history?date=2024-08-24&page=0&size=2
```

**Example Response**:

```json
{
  "totalPages": 1,
  "totalElements": 2,
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": false,
      "empty": true,
      "unsorted": true
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "size": 10,
  "content": [
    {
      "transactionId": "4f6d68e2-b9c5-4f53-8397-d4728b41b2d1",
      "baseCurrency": "USD",
      "targetCurrency": "EUR",
      "baseAmount": 100.0,
      "conversionAmount": 89.3,
      "conversionDate": "2024-08-24T21:20:48.456271"
    },
    {
      "transactionId": "4e1d34b6-28b1-40b3-92f4-0a9c4fbb14b8",
      "baseCurrency": "TRY",
      "targetCurrency": "EUR",
      "baseAmount": 10,
      "conversionAmount": 0.26,
      "conversionDate": "2024-08-24T21:21:48.456271"
    }
  ],
  "number": 0,
  "sort": {
    "sorted": false,
    "empty": true,
    "unsorted": true
  },
  "numberOfElements": 2,
  "first": true,
  "last": true,
  "empty": false
}
```

### Get Supported Currencies

**Description**: Retrieve a list of all supported currencies.

- **URL**: `/api/v1/supported-currencies`
- **Method**: `GET`
- **Response**: `200 OK` with a list of supported currencies.

**Example Request**:

```sh
curl http://localhost:8092/currency/api/v1/supported-currencies
```

**Example Response**:

```json
{
"USD": "United States Dollar",
"EUR": "Euro",
"GBP": "British Pound Sterling"
}
```

## Swagger UI

The application includes Swagger UI for exploring and testing the API endpoints.

- **URL**: `http://localhost:8092/currency/swagger-ui/index.html`
- **Description**: Access the Swagger UI to interact with and explore the available API endpoints.

## H2 Console

The application uses an in-memory H2 database for development and testing. To access the H2 console:

- **URL**: `http://localhost:8092/currency/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.