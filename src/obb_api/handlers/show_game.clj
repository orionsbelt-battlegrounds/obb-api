(ns obb-api.handlers.show-game
  "Shows the information about the given game"
  (:require [obb-api.response :as response]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-rules.game :as game]))

(defn- prepare-battle
  "Prepares the battle for JSON"
  [request battle]
  battle)

(defn handler
  "Shows a game's info"
  [request]
  (let [battle-id (get-in request [:params :id])
        battle (battle-gateway/load-battle battle-id)]
    (if battle
      (response/json-ok (prepare-battle request battle))
      (response/json-not-found))))
