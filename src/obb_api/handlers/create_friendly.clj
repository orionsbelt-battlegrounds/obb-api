(ns obb-api.handlers.create-friendly
  "Creates a friendly matches bettwen two players"
  (:require [obb-api.response :as response]
            [obb-api.gateways.player-gateway :as player-gateway]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-rules.stash :as stash]
            [obb-rules.game :as game]))

(defn- challenger-name
  "Gets the challenger's name"
  [request]
  (get-in request [:json-params :challenger]))

(defn- opponent-name
  "Gets the opponent's name"
  [request]
  (get-in request [:json-params :opponent]))

(defn- find-request-error
  "Tries to find errors on the request"
  [request]
  (cond
    (nil? (request :json-params)) "InvalidJSON"
    (nil? (opponent-name request)) "EmptyOpponent"
    (nil? (challenger-name request)) "EmptyChallenger"))

(defn- validate
  "Validates request data"
  [request challenger opponent]
  (cond
    (nil? challenger) "InvalidChallenger"
    (nil? opponent) "InvalidOpponent"))

(defn- save-game
  "Saves a game"
  [challenger opponent battle]
  (let [saved (battle-gateway/create-battle {:p1 challenger
                                             :p2 opponent
                                             :board battle})]
    (response/json-created saved)))

(defn- prepare-stash
  "Prepares the stash for the engine, transforming keyword keys in strings"
  [stash]
  (->> stash
       (map (fn [[k v]] [(name k) v]))
       (into {})))

(defn- create-battle
  "Creates a battle for a given request"
  [request]
  (if-let [stash (get-in request [:json-params :stash :challenger])]
    (-> stash
        (prepare-stash)
        (stash/create-from-hash)
        (game/create))
    (game/random)))

(defn- create-game
  "Creates the game"
  [request]
  (let [p1 (challenger-name request)
        p2 (opponent-name request)
        [challenger opponent] (player-gateway/find-players [p1 p2])
        battle (create-battle request)]
    (println "--" battle)
    (if-let [error (validate request challenger opponent)]
      (response/json-error {:error error})
      (save-game challenger opponent battle))))

(defn handler
  "Creates a friendly match"
  [request]
  (if-let [error (find-request-error request)]
    (response/json-error {:error error})
    (create-game request)))
