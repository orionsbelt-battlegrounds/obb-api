(ns obb-api.gateways.battle-gateway
  "Abstraction that handles battle persistence"
  (:require [obb-api.gateways.mongodb.battle-gateway :as mongodb]))

(defn create-battle
  "Persist battle"
  [args]
  (mongodb/create-battle args))

(defn load-open-games
  "Loads the open games for the lobby"
  []
  (mongodb/load-open-games))

(defn load-latest
  "Loads the latest battles from the given username"
  [username]
  (mongodb/load-latest-battles username))

(defn load-battle
  "Loads a persisted battle"
  [id]
  (mongodb/load-battle id))

(defn update-battle
  "Updates a battle given a result"
  ([game]
   (update-battle game true))
  ([game save?]
   (if save?
    (mongodb/update-battle game)
    game)))
