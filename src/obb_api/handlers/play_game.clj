(ns obb-api.handlers.play-game
  "Plays turns on battles"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.handlers.show-game :as show-game]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-api.core.turn-processor :as turn-processor]
            [obb-rules.game :as game]
            [obb-rules.simplifier :as simplify]
            [obb-rules.translator :as translator]
            [obb-rules.privatize :as privatize]
            [obb-rules.turn :as turn]))

(defn- valid-player?
  "Checks if a given player belongs to this battle"
  [args]
  (let [current (get-in args [:game :board :state])
        current-username (get-in args [:game (keyword current) :name])
        auth-username (args :username)]
    (= auth-username current-username)))

(defn- validate
  "Validates data for playing the turn"
  [args]
  (cond
    (nil? (args :game)) ["InvalidGame" 404]
    (nil? (get-in args [:data])) ["EmptyJSON" 412]
    (not (valid-player? args)) ["InvalidPlayer" 401]
    (nil? (get-in args [:data :actions])) ["NoActions" 412]
    (= false ((args :processed) :success)) ["TurnFailed" 422]))

(defn- privatize
  "Privatizes the response"
  [response viewer]
  (assoc response :board (privatize/game (:board response) viewer)))

(defn- build-and-privatize
  "Builds the response to be privatized"
  [response viewer]
  (-> response
      (dissoc :starting-stash)
      (simplify/build-result)
      (privatize viewer)
      (simplify/clean-result)))

(defn handler
  "Processes turn actions"
  ([request]
   (handler request {}))
  ([request {:keys [:validator :save?] :or {:validator validate :save? true}}]
   (let [data (request :json-params)
         battle-id (get-in request [:path-params :id])
         game (battle-gateway/load-battle battle-id)
         username (auth-interceptor/username request)
         viewer (show-game/match-viewer game username)
         processed (turn-processor/process-actions request game username save?)]
     (if-let [[error error-status] (validator {:request request
                                              :data data
                                              :username username
                                              :game game
                                              :processed processed})]
       (turn-processor/turn-error-response error error-status processed)
       (response/json-ok (-> (turn-processor/save-game request
                                                       game
                                                       processed
                                                       username
                                                       save?)
                             (build-and-privatize viewer)
                             (show-game/add-username-info username viewer)))))))

(defn simulator
  "Simulates turn actions"
  [request]
  (handler request {:save? false}))
