define({ "api": [
  {
    "type": "get",
    "url": "/auth/loginRequest",
    "title": "Login",
    "name": "doLogin",
    "group": "User",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "Content-Type",
            "description": "<p>should be application/json</p>"
          },
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "X-Requested-With",
            "description": "<p>should be XMLHttpRequest</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Header-Example:",
          "content": "{\n  \"Content-Type\" : \"application/json\",\n  \"X-Requested-With\" : \"XMLHttpRequest\"\n}",
          "type": "json"
        }
      ]
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "username",
            "description": "<p>User Login Email.</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "password",
            "description": "<p>User account password.</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Request-Example:",
          "content": "{\n  \"username\" : \"buyer@procurehere.com\",\n  \"password\" : \"Password@1\"\n}",
          "type": "json"
        }
      ]
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "firstname",
            "description": "<p>Firstname of the User.</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "lastname",
            "description": "<p>Lastname of the User.</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"token\" : \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w\",\n  \"refreshToken\" : \"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9SRUZSRVNIX1RPS0VOIl0sInRlbmFudElkIjoiMmM5Zjk2Nzk1OGUwZTk3ZTAxNThlMGViMmEyNDFiODEiLCJ0ZW5hbnRUeXBlIjoiQlVZRVIiLCJpZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhNmMxYjgzIiwiaXNzIjoicHJvY3VyZWhlcmUuY29tIiwianRpIjoiN2Y0NGQ0NDctOWMyYi00NGJlLWI4ZTItMzIxM2E1ODBhZmVhIiwiaWF0IjoxNDk3MzQzODk4LCJleHAiOjE0OTczNDc0OTh9.nsqMBPZbiqgUUDqidDKkkNOCZdA2LIKSCv6UGMujQ0p2Ti5gdhJxzcD6aeMefFgJPzZso9h-Pe811yERp2M3-A\"\n}",
          "type": "json"
        }
      ]
    },
    "examples": [
      {
        "title": "Example usage:",
        "content": "curl -X POST -H \"X-Requested-With: XMLHttpRequest\" -H \"Content-Type: application/json\" -H \"Cache-Control: no-cache\" -d '{  \"username\": \"admin@procurehere.com\", \"password\": \"admin123\" }' \"http://uat.procurehere.com/procurehere/api/auth/login\"",
        "type": "curl"
      }
    ],
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "InvalidUser",
            "description": "<p>The id or password of the User mismatch.</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 403 Forbidden\n{\n  \"status\" : 401,\n  \"message\" : \"Invalid username or password\",\n  \"errorCode\" : 10,\n  \"timestamp\" : 1497343691661\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "src/main/java/com/privasia/procurehere/rest/controller/MobileApiController.java",
    "groupTitle": "User"
  },
  {
    "type": "get",
    "url": "/me",
    "title": "Request User information",
    "name": "getUserProfile",
    "group": "User",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "Content-Type",
            "description": "<p>should be application/json</p>"
          },
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "X-Requested-With",
            "description": "<p>should be XMLHttpRequest</p>"
          },
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "X-Authorization",
            "description": "<p>Bearer [jwtToken]</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Header-Example:",
          "content": "{\n  \"Content-Type\" : \"application/json\",\n  \"X-Requested-With\" : \"XMLHttpRequest\",\n  \"X-Authorization\" : \"Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w\"\n}",
          "type": "json"
        }
      ]
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "id",
            "description": "<p>Internal Id of the User.</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "username",
            "description": "<p>User name.</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "loginId",
            "description": "<p>Login email of the User.</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "tenantId",
            "description": "<p>User tenant id.</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "tenantType",
            "description": "<p>User tenant type. BUYER, SUPPLIER or OWNER.</p>"
          },
          {
            "group": "Success 200",
            "type": "Boolean",
            "optional": false,
            "field": "locked",
            "description": "<p>User account lock status. true = locked, false = unlocked</p>"
          },
          {
            "group": "Success 200",
            "type": "Boolean",
            "optional": false,
            "field": "deleted",
            "description": "<p>User account delete status. true = deleted, false = existing</p>"
          },
          {
            "group": "Success 200",
            "type": "Boolean",
            "optional": false,
            "field": "active",
            "description": "<p>User account active status. true = active, false = inactive</p>"
          },
          {
            "group": "Success 200",
            "type": "Boolean",
            "optional": false,
            "field": "checkControl",
            "description": "<p>[ignore this param]</p>"
          },
          {
            "group": "Success 200",
            "type": "String[]",
            "optional": false,
            "field": "grantedAuthorities",
            "description": "<p>List containing granted access rights to this user</p>"
          },
          {
            "group": "Success 200",
            "type": "Boolean",
            "optional": false,
            "field": "passwordExpired",
            "description": "<p>Password expiry status. true = expired. false = active</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n  \"id\" : \"2c9f967958e0e97e0158e0eb2a6c1b83\",\n  \"loginId\" : \"BUYER@PROCUREHERE.COM\",\n  \"tenantId\" : \"2c9f967958e0e97e0158e0eb2a241b81\",\n  \"tenantType\" : \"BUYER\",\n  \"locked\" : false,\n  \"deleted\" : false,\n  \"active\" : true,\n  \"checkControl\" : true,\n  \"grantedAuthorities\" : [ \"ROLE_BUYER\" ],\n  \"passwordExpired\" : true,\n  \"isAdmin\" : false,\n  \"accountNonExpired\" : true,\n  \"accountNonLocked\" : true,\n  \"credentialsNonExpired\" : true,\n  \"authorities\" : [ {\n    \"authority\" : \"ROLE_BUYER\"\n  } ],\n  \"enabled\" : true,\n  \"username\" : \"BUYER@PROCUREHERE.COM\"\n}",
          "type": "json"
        }
      ]
    },
    "examples": [
      {
        "title": "Example usage:",
        "content": "curl -X GET -H \"X-Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJCVVlFUkBQUk9DVVJFSEVSRS5DT00iLCJzY29wZXMiOlsiUk9MRV9CVVlFUiJdLCJ0ZW5hbnRJZCI6IjJjOWY5Njc5NThlMGU5N2UwMTU4ZTBlYjJhMjQxYjgxIiwidGVuYW50VHlwZSI6IkJVWUVSIiwiaWQiOiIyYzlmOTY3OTU4ZTBlOTdlMDE1OGUwZWIyYTZjMWI4MyIsImlzcyI6InByb2N1cmVoZXJlLmNvbSIsImlhdCI6MTQ5NzM0Mzg5OCwiZXhwIjoxNDk3MzQ0Nzk4fQ.DcyZ8hq93y37xBBdiWS_jnST6FLCprD-kfnm3hvi1e1YG9re1rCD0MkCc1angh0xP-3RhIz2Z7h67xU4ODSw6w\" -H \"Cache-Control: no-cache\" \"http://uat.procurehere.com/procurehere/api/me\"",
        "type": "curl"
      }
    ],
    "version": "0.0.0",
    "filename": "src/main/java/com/privasia/procurehere/rest/controller/MobileApiController.java",
    "groupTitle": "User",
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "401",
            "description": "<p>Unauthorized access to protected resource. Requires login first.</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n  \"status\" : 401,\n  \"message\" : \"Token has expired\",\n  \"errorCode\" : 11,\n  \"timestamp\" : 1497346221994\n}",
          "type": "json"
        }
      ]
    }
  }
] });
