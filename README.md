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

### `GET /game/:id`

Gets the game with the given `id`. If the game is in _deploy_ state, then the _stash_ and _elements_ will be removed, to prevent a player to have information about the opponent's deploy before time. However, if you pass
the player's token, the information specific to the given player will be present.

```
> curl http://api.orionsbelt.eu/game/545790d6e4b0406f6fc19315
```
```javascript
{  
   "viewed-by":null,
   "_id":"545790d6e4b0406f6fc19315",
   "battle":{  
      "state":"deploy",
      "stash":{
          "p2":{},
          "p1":{}
      },
      "width":8,
      "height":8,
      "terrain":"rock",
      "elements":{}
   },
   "p2":{ "name":"Pyro" },
   "p1":{ "name":"donbonifacio" }
}
```

### `POST /game/create/friendly` creates a friendly match

Creates a match bettween two players. Returns the board ready to be deployed.

```
curl -XPOST http://api.orionsbelt.eu/game/create/friendly?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJkb25ib25pZmFjaW8iLCJleHAiOjE0MTQ2MTYxMTIsImlhdCI6MTQxNDcwMjUxMn0.lisfjmr4ShsYJt2FX8FfJrQ828HVbPGFKF5BL9GMEbw \
     -H "Content-Type: application/json" \
     -d '{"challenger" : "donbonifacio", "opponent" : "Pyro"}'
```
```javascript
{  
   "battle":{  
      "state":"deploy",
      "stash":{  
         "p2":{  
            "anubis":100,
            "heavy-seeker":25,
            "nova":25,
            "kamikaze":50,
            "rain":100,
            "scarab":50,
            "worm":50,
            "crusader":25
         },
         "p1":{  
            "anubis":100,
            "heavy-seeker":25,
            "nova":25,
            "kamikaze":50,
            "rain":100,
            "scarab":50,
            "worm":50,
            "crusader":25
         }
      },
      "width":8,
      "height":8,
      "terrain":"terrest",
      "elements":{}
   },
   "p2":{ "name":"Pyro" },
   "p1":{ "name":"donbonifacio" },
   "_id":"54565621300418dc8ed15cf1"
}
```

### `PUT /game/:id/deploy` performs deploy actions

Performs the deploy actions for the game with the given `:id`. It's mandatory that the auth token be given and that belongs to one of the players. When both players have both deployed, the game will auto start and the first one will
be randomly selected.

```
curl -XPUT http://api.orionsbelt.eu/game/some_id/deploy?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJkb25ib25pZmFjaW8iLCJleHAiOjE0MTQ2MTYxMTIsImlhdCI6MTQxNDcwMjUxMn0.lisfjmr4ShsYJt2FX8FfJrQ828HVbPGFKF5BL9GMEbw \
     -H "Content-Type: application/json" \
     -d '{ actions : [ ["deploy", 10, "rain", [8, 8]] ]}'
```
```javascript
{  
   "battle":{  
      "state":"deploy",
      "stash":{  
         "p2":{  
            "anubis":100,
            "heavy-seeker":25,
            "nova":25,
            "kamikaze":50,
            "rain":100,
            "scarab":50,
            "worm":50,
            "crusader":25
         },
         "p1":{}
      },
      "width":8,
      "height":8,
      "terrain":"terrest",
      "elements":{
         "[8 8" : {
            "unit" : "rain",
            "coordinate" : [8, 8],
            "player" : "p1",
            "quantity" : 10
         }
      }
   },
   "p2":{ "name":"Pyro" },
   "p1":{ "name":"donbonifacio" },
   "_id":"54565621300418dc8ed15cf1"
}
```

### `GET /auth/verify` verifies the OBB token

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

### `GET /auth/enforce` enforces the OBB token

The same interface as `/auth/verify` but will return a *401 Unauthorized* if the token is invalid.
