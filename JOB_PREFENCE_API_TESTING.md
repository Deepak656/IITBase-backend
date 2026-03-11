# API Testing Guide

## Prerequisites
- Backend running on http://localhost:8080
- Valid JWT token for authentication

## Get JWT Token
First, authenticate to get your JWT token (adjust based on your auth endpoint):
```bash
# Example authentication (adjust to your auth implementation)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@iitbase.com",
    "password": "password"
  }'
```

## Job Preferences Endpoints

### 1. Get Job Preferences
```bash
curl -X GET http://localhost:8080/api/v1/profile/job-preferences \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 2. Create Job Preferences
```bash
curl -X POST http://localhost:8080/api/v1/profile/job-preferences \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "current_location": "Bangalore",
    "work_location_type": "SPECIFIC_CITIES",
    "preferred_cities": ["Bangalore", "Chennai", "Hyderabad"],
    "previous_salary": 19.0,
    "previous_salary_currency": "INR",
    "notice_period": "IMMEDIATELY"
  }'
```

### 3. Update Job Preferences
```bash
curl -X PUT http://localhost:8080/api/v1/profile/job-preferences \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "current_location": "Mumbai",
    "work_location_type": "ANYWHERE_IN_INDIA",
    "preferred_cities": ["Mumbai", "Pune"],
    "previous_salary": 25.0,
    "previous_salary_currency": "INR",
    "notice_period": "1_MONTH"
  }'
```

### 4. Delete Job Preferences
```bash
curl -X DELETE http://localhost:8080/api/v1/profile/job-preferences \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Expected Responses

### Success Response (GET)
```json
{
  "success": true,
  "message": "Job preferences retrieved successfully",
  "data": {
    "id": 1,
    "current_location": "Bangalore",
    "work_location_type": "SPECIFIC_CITIES",
    "preferred_cities": ["Bangalore", "Chennai", "Hyderabad"],
    "previous_salary": 19.0,
    "previous_salary_currency": "INR",
    "notice_period": "IMMEDIATELY"
  }
}
```

### Success Response (POST/PUT)
```json
{
  "success": true,
  "message": "Job preferences saved successfully",
  "data": {
    "id": 1,
    "current_location": "Bangalore",
    "preferred_cities": ["Bangalore", "Chennai"],
    "previous_salary": 19.0,
    "notice_period": "IMMEDIATELY"
  }
}
```

### No Data Response
```json
{
  "success": true,
  "message": "No job preferences found",
  "data": null
}
```

### Error Response (Unauthorized)
```json
{
  "success": false,
  "message": "Unauthorized access",
  "error": "Invalid or expired token"
}
```

### Error Response (Not Found)
```json
{
  "success": false,
  "message": "Jobseeker not found"
}
```

## Testing Workflow

1. **First Time User**
   ```bash
   # 1. Register/Login to get token
   # 2. GET job preferences (should return null)
   # 3. POST to create preferences
   # 4. GET to verify creation
   ```

2. **Existing User**
   ```bash
   # 1. Login to get token
   # 2. GET current preferences
   # 3. PUT to update preferences
   # 4. GET to verify update
   ```

3. **Delete and Recreate**
   ```bash
   # 1. DELETE existing preferences
   # 2. GET to verify deletion (should return null)
   # 3. POST to create new preferences
   ```

## Common Issues

### 401 Unauthorized
- Token expired: Get a new token
- Token missing: Include Authorization header
- Invalid token: Verify token format

### 404 Not Found
- Jobseeker profile not created
- Wrong endpoint URL
- User ID mismatch

### 500 Internal Server Error
- Database connection issue
- Redis connection issue
- Application error (check logs)

## Testing with Postman

1. Create a new collection "IITBase Profile API"
2. Add environment variables:
   - `base_url`: http://localhost:8080
   - `token`: Your JWT token
3. Create requests for each endpoint
4. Use `{{base_url}}` and `{{token}}` in requests

## Redis Cache Testing

After creating/updating preferences, check Redis:
```bash
redis-cli
> KEYS jobPreferences:*
> GET jobPreferences:user123
```

Cache should invalidate on POST/PUT/DELETE operations.
