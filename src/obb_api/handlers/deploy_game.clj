(ns obb-api.handlers.deploy-game
  "Processes deploys on battles"
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
  "Checks if a given player belongs to this battle"
  [args]
  (let [battle (args :game)
        username (args :username)]
    (or
      (= username (get-in battle [:p1 :name]))
      (= username (get-in battle [:p2 :name])))))

(defn- stash-still-has-units?
  "Checks if the player's stash still has units"
  [args]
  (let [player-code (show-game/match-viewer (args :game) (args :username))]
    (not (empty? (get-in args [:processed :board :stash player-code])))))

(defn- validate
  "Validates data for deploying"
  [args]
  (cond
    (nil? (args :game)) ["InvalidGame" 404]
    (not (valid-player? args)) ["InvalidPlayer" 401]
    (nil? (get-in args [:data])) ["EmptyJSON" 412]
    (nil? (get-in args [:data :actions])) ["NoActions" 412]
    (= false ((args :processed) :success)) ["TurnFailed" 422]
    (stash-still-has-units? args) ["StashNotCleared" 412]))

(defn handler
  "Processes deploy actions"
  [request]
  (let [data (request :json-params)
        battle-id (get-in request [:path-params :id])
        game (battle-gateway/load-battle battle-id)
        username (auth-interceptor/username request)
        processed (turn-processor/process-actions request game username)]
    (if-let [[error error-status] (validate {:request request
                                             :data data
                                             :username username
                                             :game game
                                             :processed processed})]
      (turn-processor/turn-error-response error error-status processed)
      (response/json-ok (turn-processor/save-game request
                                                  game
                                                  processed
                                                  username)))))
