(ns obb-api.handlers.join-game
  "Allows a player to join a game"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.handlers.show-game :as show-game]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-api.core.turn-processor :as turn-processor]
            [obb-rules.game :as game]
            [obb-rules.simplifier :as simplify]
            [obb-rules.translator :as translator]
            [obb-rules.turn :as turn]))

(defn- valid-player?
  "Checks if the player can join"
  [args]
  (let [battle (args :game)
        username (args :username)]
    (not= username (get-in battle [:p1 :name]))))

(defn- open-game?
  "True if the player can join the game"
  [args]
  (clojure.string/blank? (get-in (args :game) [:p2 :name])))

(defn- validate
  "Validates data for deploying"
  [args]
  (cond
    (nil? (args :game)) ["InvalidGame" 404]
    (not (open-game? args)) ["CantJoin" 412]
    (not (valid-player? args)) ["InvalidPlayer" 401]))

(defn handler
  "Allows a player to join a game"
  [request]
  (let [battle-id (get-in request [:path-params :id])
        game (battle-gateway/load-battle battle-id)
        username (auth-interceptor/username request)]
    (if-let [[error error-status] (validate {:request request
                                             :username username
                                             :game game})]
      (response/json-error {:error error} error-status)
      (response/json-ok (-> game
                            (assoc :success true)
                            (assoc :p2 {:name username})
                            (battle-gateway/update-battle)
                            (show-game/add-username-info username :p2))))))
