# Railway Station Search API

This API provides information about railway stations and train availability based on user queries. It supports autocomplete for station names and allows users to search for available trains between two stations on a specific date.

## Autocomplete Station Names

### Endpoint
`GET /api/autocomplete?query=<search_query>`

### Example
`GET /api/autocomplete?query=hub`
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