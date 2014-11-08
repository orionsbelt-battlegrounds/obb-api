(ns obb-api.handlers.deploy-game
  "Processes deploys on battles"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.handlers.show-game :as show-game]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
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

(defn- validate
  "Validates data for deploying"
  [args]
  (cond
    (nil? (args :game)) ["InvalidGame" 404]
    (not (valid-player? args)) ["InvalidPlayer" 401]
    (nil? (get-in args [:data])) ["EmptyJSON" 412]
    (nil? (get-in args [:data :actions])) ["NoActions" 412]
    (= false ((args :processed) :success)) ["TurnFailed" 422]))

(defn- process-actions
  "Applies the actions to the battle"
  [request game username]
  (when game
    (let [actions (get-in request [:json-params :actions])
          player-code (show-game/match-viewer game username)
          translated-actions (translator/actions player-code actions)
          battle (game :battle)]
      (apply turn/process battle player-code translated-actions))))

(defn- dump-error
  "Outputs proper error"
  [error error-status processed]
  (if (= 422 error-status)
    (response/json-error processed error-status)
    (response/json-error {:error error} error-status)))

(defn- save
  "Saves the game"
  [game result]
  (let [sresult (simplify/clean-result result)
        action-results (get-in sresult [:board :action-results])
        new-board (sresult :board)
        new-game (assoc game :board (dissoc new-board :action-results))]
    (battle-gateway/update-battle new-game)
    (-> new-game
        (assoc :success (sresult :success)))))

(defn handler
  "Processes deploy actions"
  [request]
  (let [data (request :json-params)
        battle-id (get-in request [:path-params :id])
        game (battle-gateway/load-battle battle-id)
        username (auth-interceptor/username request)
        processed (process-actions request game username)]
    (if-let [[error error-status] (validate {:request request
                                             :data data
                                             :username username
                                             :game game
                                             :processed processed})]
      (dump-error error error-status processed)
      (response/json-ok (save game processed)))))
