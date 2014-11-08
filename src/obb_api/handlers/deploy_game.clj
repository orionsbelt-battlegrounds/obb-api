(ns obb-api.handlers.deploy-game
  "Processes deploys on battles"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-rules.game :as game]
            [obb-rules.privatize :as privatize]))

(defn- valid-player?
  "Checks if a given player belongs to this battle"
  [args]
  (let [battle (args :game)
        username (args :username)]
    (or
      (= username (get-in battle [:p1 :name]))
      (= username (get-in battle [:p2 :name])))))

(defn- validate
  "Validates data for deploying"
  [args]
  (cond
    (nil? (args :game)) ["InvalidGame" 404]
    (not (valid-player? args)) ["InvalidPlayer" 401]
    (nil? (get-in args [:data])) ["EmptyJSON" 412]
    (nil? (get-in args [:data :actions])) ["NoActions" 412]))

(defn handler
  "Processes deploy actions"
  [request]
  (let [data (request :json-params)
        battle-id (get-in request [:path-params :id])
        game (battle-gateway/load-battle battle-id)
        username (auth-interceptor/username request)]
    (if-let [[error error-status] (validate {:request request
                                             :data data
                                             :username username
                                             :game game})]
      (response/json-error {:error error} error-status)
      (response/json-ok {}))))
