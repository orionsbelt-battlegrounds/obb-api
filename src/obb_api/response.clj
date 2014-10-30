(ns obb-api.response
  "Utilities for the service response"
  (:require [io.pedestal.http :as http]))

(defn json-ok
  "Returns a json response given the clojure object"
  [obj]
  (http/json-response obj))
