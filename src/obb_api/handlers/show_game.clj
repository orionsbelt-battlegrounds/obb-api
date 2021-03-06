(ns obb-api.handlers.show-game
  "Shows the information about the given game"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-api.core.hints :as hints]
            [obb-rules.game :as game]
            [obb-rules.board :as board]
            [obb-rules.simplifier :as simplify]
            [obb-rules.translator :as translator]
            [obb-rules.privatize :as privatize]))

(defn- prepare-game-deploy
  "Prepares a game in deploy state for JSON"
  [request game viewer]
  (-> game
      (assoc :board (privatize/game (game :board) viewer))
      (dissoc :starting-stash)))

(defn- prepare-game-ongoing
  "Prepares an ongoging game for JSON"
  [request game viewer]
  (assoc game :board (translator/board viewer (game :board))))

(defn- prepare-game
  "Prepares the game for JSON"
  [request game viewer]
  (if (= "deploy" (get-in game [:board :state]))
    (prepare-game-deploy request game viewer)
    (prepare-game-ongoing request game viewer)))

(defn match-viewer
  "Checks if the given username is playing the game"
  [game username]
  (cond
    (= username (get-in game [:p1 :name])) :p1
    (= username (get-in game [:p2 :name])) :p2))

(defn add-username-info
  "Adds username info to the response, is available"
  [game username viewer]
  (if username
    (assoc game :viewed-by {:username username
                            :player-code viewer})
    game))

(defn handler
  "Shows a game's info"
  [request]
  (let [battle-id (get-in request [:path-params :id])
        game (battle-gateway/load-battle battle-id)
        built-game (simplify/build-result game)
        username (auth-interceptor/username request)
        viewer (match-viewer game username)]
    (if game
      (-> (prepare-game request built-game viewer)
          (simplify/clean-result)
          (add-username-info username viewer)
          (assoc :hints (hints/for-game built-game viewer))
          (response/json-ok))
      (response/json-not-found))))
