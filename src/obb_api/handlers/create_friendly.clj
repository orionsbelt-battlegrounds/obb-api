(ns obb-api.handlers.create-friendly
  "Creates a friendly matches bettwen two players"
  (:require [obb-api.response :as response]))

(defn handler
  "Creates a friendly match"
  [request]
  (response/json-ok {:name "obb-api"}))
