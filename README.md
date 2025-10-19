# hng  Profile API

A production-ready RESTful API built with Go and Gin that returns user profile information along with random cat facts from an external API.

**Live Demo:** 

---

## ✨ Features

- ✅ **Dynamic Profile Endpoint** - Returns user profile with fresh cat facts on every request
- ✅ **Real-time Timestamps** - ISO 8601 formatted timestamps updated for each request
- ✅ **External API Integration** - Fetches random cat facts from https://catfact.ninja/fact
- ✅ **Error Handling** - Graceful fallbacks if external API fails
- ✅ **CORS Support** - Ready for cross-origin requests
- ✅ **Environment Configuration** - Uses springboot for easy configuration
- ✅ **Production Ready** - Deployed on railway with proper error handling

- ## 🔧Stack
- KOTLIN/SPRINGBOOT

## 🛣️ API Endpoints

### GET /me

Returns your profile with a random cat fact.

**Request:**
```bash
curl http://localhost:8080/me
```

**Response:**
```json
{
  "status": "success",
  "user": {
    "email": "okezi003@gmail.com",
    "name": "Joseph Okeh Simon",
    "stack": "Kotlin/SpringBoot"
  },
  "timestamp": "2024-10-18T14:37:45Z",
  "fact": "A cat joke won't hurt."
}
```

**Status:** 200 OK
