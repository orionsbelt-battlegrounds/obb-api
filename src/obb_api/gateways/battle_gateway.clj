(ns obb-api.gateways.battle-gateway
  "Abstraction that handles battle persistence"
  (:require [obb-api.gateways.mongodb.battle-gateway :as mongodb]))

(defn create-battle
  "Persist battle"
  [args]
  (mongodb/create-battle args))
