(ns obb-api.handlers.create-friendly
  "Creates a friendly matches bettwen two players"
  (:require [obb-api.response :as response]))

(defn- find-request-error
  "Tries to find errors on the request"
  [request]
  (cond
    (nil? (request :json-params)) "InvalidJSON"
    (nil? (get-in request [:json-params :opponent])) "EmptyOpponent"
    (nil? (get-in request [:json-params :challenger])) "EmptyChallenger"))

(defn handler
  "Creates a friendly match"
  [request]
  (if-let [error (find-request-error request)]
    (response/json-error {:error error})
    (response/json-ok {:name "obb-api"})))
