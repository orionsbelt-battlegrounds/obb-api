(ns obb-api.handlers.create-friendly
  "Creates a friendly matches bettwen two players"
  (:require [obb-api.response :as response]
            [obb-api.gateways.player-gateway :as player-gateway]))

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

(defn- create-game
  "Creates the game"
  [request]
  (let [p1 (challenger-name request)
        p2 (opponent-name request)
        [challenger opponent] (player-gateway/find [p1 p2])]
    (if-let [error (validate request challenger opponent)]
      (response/json-error {:error error})
      (response/json-ok {} ))))

(defn handler
  "Creates a friendly match"
  [request]
  (if-let [error (find-request-error request)]
    (response/json-error {:error error})
    (create-game request)))
