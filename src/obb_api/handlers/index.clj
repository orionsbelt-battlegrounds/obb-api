(ns obb-api.handlers.index
  "Handles the root URL"
  (:require [obb-api.response :as response]))

(defn handler
  "Returns the JSON of the root URL"
  [request]
  (println request)
  (response/json-ok {:name "obb-api"}))
