# OBB API 
[![Build Status](https://travis-ci.org/orionsbelt-battlegrounds/obb-api.svg)](https://travis-ci.org/orionsbelt-battlegrounds/obb-api) [![Dependency Status](https://www.versioneye.com/user/projects/54524fe330a8fe1239000009/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54524fe330a8fe1239000009) ![Uptime](https://www.statuscake.com/App/button/index.php?Track=PdHw36q4gx&Days=7&Design=5)

This API is used by [Orion's Belt BattleGrounds](https://github.com/orionsbelt-battlegrounds) to persist and process battles and the core game's funcionality. The game has several APIs available:

API | Description
--- | ---
[OBB API](https://github.com/orionsbelt-battlegrounds/obb-api) | Persistent game functionality
OBB Auth API | Allows to trade auth credentials for an auth token, recognized by all services
[OBB Rules API](https://github.com/orionsbelt-battlegrounds/obb-rules-api) | _(Stateless)_ Battle information: units metadata, turn processing and other utilities

## HTTP API

Available at [api.orionsbelt.eu](http://api.orionsbelt.eu).

### `GET /` api version and generic information

```
> curl http://api.orionsbelt.eu/
```
```javascript
{"name":"obb-api"}
```

### `GET /auth/verify?token=:token` verifies the OBB token

Given a token, will analyse it and output information about it, for example if it's considered valid.

```
> curl http://api.orionsbelt.eu/auth/verify?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJkb25ib25pZmFjaW8iLCJleHAiOjE0MTQ2MTYxMTIsImlhdCI6MTQxNDcwMjUxMn0.lisfjmr4ShsYJt2FX8FfJrQ828HVbPGFKF5BL9GMEbw
```
```javascript
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "claims": {
    "iss": "donbonifacio",
    "exp": 1414616112,
    "iat": 1414702512
  },
  "signature": "lisfjmr4ShsYJt2FX8FfJrQ828HVbPGFKF5BL9GMEbw",
  "valid": true
}
```

### `GET /auth/enforce?token=:token` enforces the OBB token

The same interface as `/auth/verify` but will return a *403 Forbidden* if the token is invalid.
