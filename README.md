A RESTful API that fetches countries from restcountries.com, matches currencies with exchange rates from open.er-api.com, computes an estimated_gdp, caches data in a MySQL database, and provides CRUD + image summary.

Requirements
Kotlin SpringBoot
MySQL server

🚀 Features
Fetches all countries from restcountries.com

Retrieves live exchange rates from open.er-api.com

Computes estimated_gdp = population × random(1000–2000) ÷ exchange_rate

Stores and updates data in MySQL as cached records

Generates an image summary (cache/summary.png) after refresh

Provides filters, sorting, and CRUD endpoints

Handles API and DB errors gracefully

Deployed easily on Railway

💻 Tech Stack
Backend: SpringBoot

Language: Kotlin

HTTP Client: requests

Deployment: Render

🧩 API Endpoints
Method	Endpoint	Description
POST	/countries/refresh	fetch external data & cache to DB (and generate cache/summary.png)
GET	/countries	list cached countries (filters: region, currency; sort: gdp_desc or gdp_asc)
GET	/countries/{name}	get one country
GET	/status	total countries + last refresh time
GET	/countries/image	serve generated summary image
DELETE	/countries/{name}	delete a country
✅ Example Request 
POST /countries/refresh

Example Response
{
"total_countries": 250,
"last_refreshed_at": "2025-10-26T15:56:10.495906+00:00"
}

### ✅ Example Request — Filter Countries

**GET** `/countries?region=Africa`


### Example Response
```json
[
  {
    "id": 1,
    "name": "Nigeria",
    "capital": "Abuja",
    "region": "Africa",
    "population": 206139589,
    "currency_code": "NGN",
    "exchange_rate": 1600.23,
    "estimated_gdp": 25767448125.2,
    "flag_url": "https://flagcdn.com/ng.svg",
    "last_refreshed_at": "2025-10-22T18:00:00Z"
  }
]

