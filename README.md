🧠 String Analyzer API (Kotlin + Spring Boot)
📘 Overview

String Analyzer API is a RESTful service built with Kotlin and Spring Boot.
It analyzes strings and stores their computed properties in an in-memory database (using ConcurrentHashMap).

This project focuses on five REST endpoints that allow you to analyze, retrieve, filter, and delete strings — as well as interpret natural language queries for filtering.


⚙️ Core Features

When a string is analyzed, the API computes and stores the following properties:

Property	Description
length	Number of characters in the string
is_palindrome	Whether the string reads the same forwards and backwards (case-insensitive)
unique_characters	Number of distinct characters in the string
word_count	Number of words (split by whitespace)
sha256_hash	SHA-256 hash value for unique identification
character_frequency_map	Map showing frequency of each character

All strings are stored in memory, so the data resets when the server restarts.

🧩 API Endpoints
1️⃣ Create / Analyze String

POST /strings
Analyzes and stores a new string.

Request
`{
"value": "string to analyze"
}`

Success Response – 201 Created
`{
"id": "sha256_hash_value",
"value": "string to analyze",
"properties": {
"length": 17,
"is_palindrome": false,
"unique_characters": 12,
"word_count": 3,
"sha256_hash": "abc123...",
"character_frequency_map": {
"s": 2,
"t": 3,
"r": 2
}
},
"created_at": "2025-08-27T10:00:00Z"
}`

2️⃣ Get Specific String

GET /strings/{string_value}
Retrieves the analysis result for a given string.

Example

GET /strings/madam

Success Response – 200 OK
`{
"id": "3e25960a79dbc69b674cd4ec67a72c62",
"value": "madam",
"properties": {
"length": 5,
"is_palindrome": true,
"unique_characters": 3,
"word_count": 1,
"sha256_hash": "3e25960a79dbc69b674cd4ec67a72c62",
"character_frequency_map": { "m": 2, "a": 2, "d": 1 }
},
"created_at": "2025-08-27T10:00:00Z"
}`

Error Response
`{
"404 Not Found": "String does not exist in the system"
}`

3️⃣ Get All Strings (with Filters)

GET /strings
Returns all stored strings or filters them based on query parameters.

Supported Filters
Parameter	Type	Description
is_palindrome	Boolean	Filter by palindrome status
min_length	Int	Minimum string length
max_length	Int	Maximum string length
word_count	Int	Exact number of words
contains_character	String	Must contain this character
Example Request
GET /strings?is_palindrome=true&min_length=5&max_length=20&word_count=2&contains_character=a

Success Response – 200 OK
`{
"data": [
{
"id": "hash1",
"value": "string1",
"properties": { /* ... */ },
"created_at": "2025-08-27T10:00:00Z"
}
],
"count": 15,
"filters_applied": {
"is_palindrome": true,
"min_length": 5,
"max_length": 20,
"word_count": 2,
"contains_character": "a"
}
}`

Error Response
`{
"400 Bad Request": "Invalid query parameter values or types"
}`

4️⃣ Delete String

DELETE /strings/{string_value}
Removes an analyzed string from storage.

Example

DELETE /strings/madam

Success Response – 204 No Content
(no response body)

Error Response
`{
"404 Not Found": "String does not exist in the system"
}`

5️⃣ Filter by Natural Language

GET /strings/filter-by-natural-language?query={text}
Interprets a natural-language query and applies filters automatically.

Example Requests
Query	Parsed Filters
all single word palindromic strings	{ "word_count": 1, "is_palindrome": true }
strings longer than 10 characters	{ "min_length": 11 }
palindromic strings that contain the first vowel	{ "is_palindrome": true, "contains_character": "a" }
strings containing the letter z	{ "contains_character": "z" }
Example Success Response – 200 OK
`{
"data": [
{
"id": "hash1",
"value": "madam",
"properties": { /* ... */ },
"created_at": "2025-08-27T10:00:00Z"
}
],
"count": 3,
"interpreted_query": {
"original": "all single word palindromic strings",
"parsed_filters": {
"word_count": 1,
"is_palindrome": true
}
}
}`

Error Responses
Status	Description
400 Bad Request	Unable to parse natural language query
422 Unprocessable Entity	Query parsed but resulted in conflicting filters
🧪 Example cURL Commands
# Create
curl -X POST http://localhost:8080/strings \
-H "Content-Type: application/json" \
-d '{"value": "madam"}'

# Get one
curl http://localhost:8080/strings/madam

# Filtered list
curl "http://localhost:8080/strings?is_palindrome=true&min_length=3"

# Delete
curl -X DELETE http://localhost:8080/strings/madam

# Natural language query
curl "http://localhost:8080/strings/filter-by-natural-language?query=all%20single%20word%20palindromic%20strings"

⚙️ Setup Instructions
1️⃣ Clone the Repository
git clone https://github.com/<your-username>/string-analyzer-api.git
cd string-analyzer-api

2️⃣ Run Locally
./gradlew bootRun

3️⃣ Access API
http://localhost:8080

🧩 Dependencies
Library	Purpose
spring-boot-starter-web	REST API support
spring-boot-starter	Core Spring Boot dependencies
kotlin-reflect	Kotlin reflection utilities
reactor-core	Reactive programming (used by WebClient)


