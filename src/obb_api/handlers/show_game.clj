(ns obb-api.handlers.show-game
  "Shows the information about the given game"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-rules.game :as game]
            [obb-rules.translator :as translator]
            [obb-rules.privatize :as privatize]))

(defn- prepare-game-deploy
  "Prepares a game in deploy state for JSON"
  [request game viewer]
  (-> game
      (assoc :battle (privatize/game (game :board) viewer))
      (dissoc :starting-stash)))

(defn- prepare-game-ongoing
  "Prepares an ongoging game for JSON"
  [request game viewer]
  (assoc game :board (translator/board viewer (game :board))))

(defn- prepare-game
  "Prepares the game for JSON"
  [request game viewer]
  (if (= "deploy" )
    (prepare-game-deploy request game viewer)
    (prepare-game-ongoing request game viewer)))

(defn match-viewer
  "Checks if the given username is playing the game"
  [game username]
  (cond
    (= username (get-in game [:p1 :name])) :p1
    (= username (get-in game [:p2 :name])) :p2))

(defn handler
  "Shows a game's info"
  [request]
  (let [battle-id (get-in request [:path-params :id])
        game (battle-gateway/load-battle battle-id)
        username (auth-interceptor/username request)
        viewer (match-viewer game username)]
    (if game
      (-> (prepare-game request game viewer)
          (assoc :viewed-by username)
          (response/json-ok))
      (response/json-not-found))))
