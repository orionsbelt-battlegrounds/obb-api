(ns obb-api.handlers.create-friendly
  "Creates a friendly matches bettwen two players"
  (:require [obb-api.response :as response]))

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

(defn handler
  "Creates a friendly match"
  [request]
  (if-let [error (find-request-error request)]
    (response/json-error {:error error})
    (response/json-ok {:name "obb-api"})))
