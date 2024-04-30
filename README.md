# Spring security demo service

Spring security demo service implement authentication and authorization using Spring Security and JWT.

API documentation is available by clicking here: [http://host:port/swagger-ui.html]().

### How to use

#### <u>Register new user</u>

Request example:

```
POST http://localhost:8081/api/v1/auth/register
```

BODY:
```json
{
  "firstName": "foo",
  "lastName": "bar",
  "username": "foobar@gmail.com",
  "password": "123"
}
```

Response example:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29iYXJAZ21haWwuY29tIiwiaWF0IjoxNzE0NDc2ODk0LCJleHAiOjE3MTQ0Nzc0OTR9.Mp96LdbnOzMAHiiWM4dUyo9FfFbqbn7MaRyJEhlaztY",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29iYXJAZ21haWwuY29tIiwiaWF0IjoxNzE0NDc2ODk0LCJleHAiOjE3MTQ0Nzg2OTR9.UHJJJpelReD8gvyj9A8OD73Jm1RhFt0RGzDvZ0wm-24"
}
```

#### <u>Authenticate user</u>

Request example:

```
POST http://localhost:8081/api/v1/auth/authenticate
```

BODY:
```json
{
  "username": "foobar@gmail.com",
  "password": "123"
}
```

Response example:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29iYXJAZ21haWwuY29tIiwiaWF0IjoxNzE0NDc3MTAwLCJleHAiOjE3MTQ0Nzc3MDB9.48W0B_VaW7U-MPfrXcP8ZHVsepIQ0vatFgOXrjUfuz8",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29iYXJAZ21haWwuY29tIiwiaWF0IjoxNzE0NDc3MTAwLCJleHAiOjE3MTQ0Nzg5MDB9.7FvAUmCbYfb69dujdir_PFnU2MLpLH80h7jScM0hif4"
}
```

#### <u>Refresh token</u>

Request example:

HEADERS:
```
Authorization: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29iYXJAZ21haWwuY29tIiwiaWF0IjoxNzE0NDc3MTAwLCJleHAiOjE3MTQ0Nzg5MDB9.7FvAUmCbYfb69dujdir_PFnU2MLpLH80h7jScM0hif4" 
```

```
POST http://localhost:8081/api/v1/auth/refresh-token
```

Response example:

```json
{
  "accessToken":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29iYXJAZ21haWwuY29tIiwiaWF0IjoxNzE0NDc3MzQ5LCJleHAiOjE3MTQ0Nzc5NDl9.SrObt4fNxYyxGCMj_8PiGDtg5zd4pHQ86uhiZVoUPFI",
  "refreshToken":" eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29iYXJAZ21haWwuY29tIiwiaWF0IjoxNzE0NDc3MTAwLCJleHAiOjE3MTQ0Nzc3MDB9.48W0B_VaW7U-MPfrXcP8ZHVsepIQ0vatFgOXrjUfuz8"
}
```

#### <u>Get user by id</u>

CONDITIONS: Only current user or admin role

Request example:

HEADERS:
```
Authorization: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmb29iYXJAZ21haWwuY29tIiwiaWF0IjoxNzE0NDc3MzQ5LCJleHAiOjE3MTQ0Nzc5NDl9.SrObt4fNxYyxGCMj_8PiGDtg5zd4pHQ86uhiZVoUPFI" 
```

```
GET http://localhost:8081/api/v1/users/12
```

Response example:

```json
{
    "id": 12,
    "firstName": "foo",
    "lastName": "bar",
    "username": "foobar@gmail.com",
    "role": "USER",
    "createdAt": "2024-04-30T14:34:54.492544",
    "enabled": true,
    "accountNonExpired": true,
    "credentialsNonExpired": true,
    "authorities": [
        {
            "authority": "ROLE_USER"
        }
    ],
    "accountNonLocked": true
}
```

#### <u>Get all users</u> 

CONDITIONS: Only admin role

Request example:

HEADERS:
```
Authorization: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhLmcuc2Fta2luQGdtYWlsLmNvbSIsImlhdCI6MTcxNDQ3ODQzMiwiZXhwIjoxNzE0NDc5MDMyfQ.NbhdtCfsspw7irGe2giWoebAt4oY5IYmL3rW8vjEeQc" 
```

```
GET http://localhost:8081/api/v1/users
```

Response example:
```json
[
    {
        "id": 11,
        "firstName": null,
        "lastName": null,
        "username": "test@gmail.com",
        "role": "USER",
        "createdAt": "2024-04-28T13:02:40.094401",
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "authorities": [
            {
                "authority": "ADMIN_USER"
            }
        ],
        "accountNonLocked": true
    },
    {
        "id": 12,
        "firstName": "foo",
        "lastName": "bar",
        "username": "foobar@gmail.com",
        "role": "USER",
        "createdAt": "2024-04-30T14:34:54.492544",
        "enabled": true,
        "accountNonExpired": true,
        "credentialsNonExpired": true,
        "authorities": [
            {
                "authority": "ROLE_USER"
            }
        ],
        "accountNonLocked": true
    }
]
```

#### <u>Change password</u>   

CONDITIONS: Only current user or admin role

Request example:

HEADERS:
```
Authorization: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhLmcuc2Fta2luQGdtYWlsLmNvbSIsImlhdCI6MTcxNDQ3ODQzMiwiZXhwIjoxNzE0NDc5MDMyfQ.NbhdtCfsspw7irGe2giWoebAt4oY5IYmL3rW8vjEeQc" 
```

```
PATCH http://localhost:8081/api/v1/users/12/change-password
```

BODY:
```json
{
  "currentPassword": "123",
  "newPassword": "456",
  "confirmationPassword": "456"
}
```

#### <u>Delete user</u>

CONDITIONS: Only current user or admin role

Request example:

HEADERS:
```
Authorization: "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhLmcuc2Fta2luQGdtYWlsLmNvbSIsImlhdCI6MTcxNDQ3ODQzMiwiZXhwIjoxNzE0NDc5MDMyfQ.NbhdtCfsspw7irGe2giWoebAt4oY5IYmL3rW8vjEeQc" 
```

```
DELETE http://localhost:8081/api/v1/users/12
```

