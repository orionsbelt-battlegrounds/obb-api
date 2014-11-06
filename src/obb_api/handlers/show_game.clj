(ns obb-api.handlers.show-game
  "Shows the information about the given game"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-rules.game :as game]
            [obb-rules.privatize :as privatize]))

(defn- prepare-game-deploy
  "Prepares a game in deploy state for JSON"
  [request game]
  (-> game
      (assoc :battle (privatize/game (game :battle)))
      (dissoc :starting-stash)))

(defn- prepare-game-ongoing
  "Prepares an ongoging game for JSON"
  [request game]
  game)

(defn- prepare-game
  "Prepares the game for JSON"
  [request game]
  (if (= "deploy" )
    (prepare-game-deploy request game)
    (prepare-game-ongoing request game)))

(defn handler
  "Shows a game's info"
  [request]
  (let [battle-id (get-in request [:path-params :id])
        game (battle-gateway/load-battle battle-id)
        username (auth-interceptor/username request)]
    (if game
      (-> (prepare-game request game)
          (assoc :viewed-by username)
          (response/json-ok))
      (response/json-not-found))))
