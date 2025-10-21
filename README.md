🧠 String Analyzer API (Kotlin + Spring Boot)
Overview

String Analyzer API is a RESTful service built with Kotlin and Spring Boot, designed to analyze and store strings in memory.
Each string is analyzed for multiple textual properties such as palindrome status, length, unique characters, and more.

This API is fully self-contained and uses an in-memory storage (ConcurrentHashMap) meaning no external database setup is required.

🚀 Features

For each analyzed string, the API computes and stores:

Property	Description
length	Number of characters in the string
is_palindrome	Whether the string reads the same forwards and backwards (case-insensitive)
unique_characters	Count of distinct characters in the string
word_count	Number of words separated by whitespace
sha256_hash	SHA-256 hash of the string for unique identification
character_frequency_map	Object mapping each character to its occurrence count

The analyzed strings are stored in memory using a ConcurrentHashMap, simulating an in-memory database.

⚙️ Tech Stack

Language: Kotlin

Framework: Spring Boot

HTTP Client: Spring WebClient (used to fetch random facts)

Storage: In-memory (ConcurrentHashMap)

Build Tool: Gradle

🧩 Project Structure
src/
├─ main/
│   ├─ kotlin/
│   │   └─ com.example.stringanalyzer/
│   │        ├─ controller/
│   │        │   └─ ProfileController.kt
│   │        ├─ model/
│   │        │   ├─ AnalyzedString.kt
│   │        │   ├─ CreateStringRequest.kt
│   │        │   ├─ Profile.kt
│   │        │   ├─ User.kt
│   │        │   └─ CatFactResponse.kt
│   │        ├─ service/
│   │        │   ├─ GetAnalyzedString.kt
│   │        │   └─ GetFilterList.kt
│   │        └─ StringAnalyzerApplication.kt
│   └─ resources/
│       └─ application.yml
└─ test/

⚡️ Endpoints
1️⃣ Create / Analyze String

POST /strings
Creates and analyzes a new string.

Request
{
"value": "string to analyze"
}

Success Response – 201 Created
{
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
}

Error Responses
Code	Message
409 Conflict	String already exists in the system
400 Bad Request	Invalid request body or missing "value"
422 Unprocessable Entity	Invalid data type for "value" (must be string)
2️⃣ Get Specific String

GET /strings/{string_value}

Retrieves a specific analyzed string by its original value.

Success Response – 200 OK
{
"id": "sha256_hash_value",
"value": "requested string",
"properties": {
"length": 10,
"is_palindrome": true,
"unique_characters": 8,
"word_count": 2,
"sha256_hash": "efg456...",
"character_frequency_map": { "a": 2, "b": 1, "c": 2 }
},
"created_at": "2025-08-27T10:00:00Z"
}

Error Response
{
"404 Not Found": "String does not exist in the system"
}

3️⃣ Get All Strings (with Filtering)

GET /strings?is_palindrome=true&min_length=5&max_length=20&word_count=2&contains_character=a

Filters stored strings using query parameters.

Supported Filters
Parameter	Type	Description
is_palindrome	Boolean	Whether the string is a palindrome
min_length	Int	Minimum length
max_length	Int	Maximum length
word_count	Int	Exact word count
contains_character	String	Must contain this character
Success Response – 200 OK
{
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
}

Error Response
{
"400 Bad Request": "Invalid query parameter values or types"
}

4️⃣ Delete String

DELETE /strings/{string_value}

Deletes a string from in-memory storage.

Success Response – 204 No Content

(empty response)

Error Response
{
"404 Not Found": "String does not exist in the system"
}

5️⃣ (Optional) Profile Endpoint



🧮 Example Requests (cURL)
# Create a new string
curl -X POST http://localhost:8080/strings \
-H "Content-Type: application/json" \
-d '{"value": "madam"}'

# Get a specific string
curl http://localhost:8080/strings/madam

# Filter strings
curl "http://localhost:8080/strings?is_palindrome=true&min_length=3"

# Delete a string
curl -X DELETE http://localhost:8080/strings/madam

🧑‍💻 Local Setup Instructions
1️⃣ Clone Repository
git clone https://github.com/<your-username>/string-analyzer-api.git
cd string-analyzer-api

2️⃣ Run the Application
Using Gradle
./gradlew bootRun

Or using IntelliJ / Spring Tools Suite

Run StringAnalyzerApplication.kt directly.

3️⃣ Access the API
http://localhost:8080

🧩 Dependencies

org.springframework.boot:spring-boot-starter-web

org.springframework.boot:spring-boot-starter

org.jetbrains.kotlin:kotlin-reflect

org.jetbrains.kotlin:kotlin-stdlib-jdk8

io.projectreactor:reactor-core

(Gradle automatically installs these during build.)
