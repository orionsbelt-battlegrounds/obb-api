(ns obb-api.handlers.latest-games
  "Returns the player's last games"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.handlers.show-game :as show-game]
            [obb-api.gateways.battle-gateway :as battle-gateway]))

(defn- prepare-game
  "Prepares a game to be returned"
  [game]
  (assoc game :uri (str "/game/" (get game :_id))))

(defn- prepare-games
  "Prepares the games to be returned"
  [games]
  (map prepare-game games))

(defn handler
  "Processes turn actions"
  [request]
  (-> (auth-interceptor/username request)
      (battle-gateway/load-latest)
      (prepare-games)
      (response/json-ok)))
