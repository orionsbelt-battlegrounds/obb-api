(ns obb-api.handlers.latest-games
  "Returns the player's last games"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.handlers.show-game :as show-game]
            [obb-api.gateways.battle-gateway :as battle-gateway]))

(defn- trim-game
  "Simplifies a specific game"
  [game]
  {:_id (get game :_id)
   :state (get-in game [:board :state])
   :p1 (get game :p1)
   :p2 (get game :p2)})

(defn- trim-games
  "Returns a simple game info collection"
  [games]
  (map trim-game games))

(defn handler
  "Processes turn actions"
  [request]
  (-> (auth-interceptor/username request)
      (battle-gateway/load-latest)
      (trim-games)
      (response/json-ok)))
