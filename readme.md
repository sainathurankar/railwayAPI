# Railway Station Search API

This API provides information about railway stations and train availability based on user queries. It supports autocomplete for station names and allows users to search for available trains between two stations on a specific date.

# AutoComplete for Railway Station Names

## Overview
The `AutoCompleteController` is responsible for handling auto-complete requests for railway station names. It provides a RESTful API endpoint `/autocomplete` that accepts a GET request with a search query and returns a list of matching station names.

## API Endpoint
- **Endpoint:** `/autocomplete`
- **Method:** GET

## Request Parameters
- `query` (required): The search query entered by the user.

### Endpoint
`GET /api/autocomplete?query=<search_query>`

### Example
`GET http://localhost:8080/api/autocomplete?query=hub`
```json
{
    "start": 0,
    "numFoundExact": true,
    "numFound": 20,
    "results": [
        {
          "stationName": "Hubli Jn",
          "stationCode": "UBL",
          "state": "Karnataka",
          "search_tag": "EXACT_SEARCH",
          "region": "South",
          "rbStationId": 35819,
          "rank": 3,
          "locationId": 222,
          "cityName": "Hubli",
          "alias2": "Hubballi",
          "alias1": "HBX"
        },
        {
          "stationName": "Unkal",
          "stationCode": "UNK",
          "state": "Karnataka",
          "search_tag": "EXACT_SEARCH",
          "region": "South",
          "rbStationId": 35901,
          "rank": 4,
          "locationId": 222,
          "cityName": "Unkal",
          "alias3": "Hubballi",
          "alias2": "HBX",
          "alias1": "Hubli"
        }
    ]
}
```

# Search for Trains

## Overview
The `SearchController` handles search requests for train information. It provides a RESTful API endpoint `/search` that accepts a POST request with a `SearchInput` object and optional parameters for train number, class and update.

## API Endpoint
- **Endpoint:** `/search`
- **Method:** POST
- **Request Body:**
    - `searchInput`: A JSON object containing search criteria.
    - `trainNumber` (optional): The train number for more specific searches.
    - `class` (optional): The class of travel for more refined results.
    - `update` (optional): default = `false` To fetch the latest availability of seats

# Example Usage

### Search for Trains

**Endpoint:** `POST http://localhost:8080/api/search`

**Request Body:**
```json
{
  "src": "UBL",
  "dst": "SBC",
  "doj": "20231229"
}
```

**Response:**
```json
{
  "error": null,
  "response": null,
  "status": {
    "StatusCode": 200,
    "StatusMsg": "OK"
  },
  "trainList": [
    "13:40 Sbc Vande Bharat 19:45 "
  ],
  "trains": [
    {
      "trainName": "Sbc Vande Bharat",
      "trainNumber": "20662",
      "departureTime": "13:40",
      "arrivalTime": "19:45",
      "availablitiesList": [
        {
          "quota": "GN",
          "className": "CC",
          "status": "Available",
          "seats": "335",
          "fare": "1320"
        },
        {
          "quota": "GN",
          "className": "EC",
          "status": "GNWL",
          "seats": "1",
          "fare": "2395"
        }
      ]
    }
  ]
}
```

#### Search by Train Number Endpoint

**Endpoint:** `POST http://localhost:8080/api/search?trainNumber=16590`

**Request Body:**
```json
{
  "src": "UBL",
  "dst": "SBC",
  "doj": "20231229"
}
```

#### Search by Train Number and Class Endpoint

**Endpoint:** `POST http://localhost:8080/api/search?trainNumber=16590&class=SL`

**Request Body:**
```json
{
  "src": "UBL",
  "dst": "SBC",
  "doj": "20231229"
}
```

#### Search and get updated availability of seats

**Endpoint:** `POST http://localhost:8080/api/search?update=true`

**Request Body:**
```json
{
  "src": "UBL",
  "dst": "SBC",
  "doj": "20231229"
}
```

# Local Setup Steps

## Build Docker Image

To build the Docker image, use the following command:

```bash
docker build -t railway-api-spring-boot:latest .
```

## Run Docker Container

To run the Docker container and expose the Spring Boot application on port 8080, use the following command:

```bash
docker run -p 8080:8080 railway-api-spring-boot:latest
```


## Credits

This project utilizes the RedBus API for train information. We acknowledge and appreciate the services provided by RedBus for making this integration possible.