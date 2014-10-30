(ns obb-api.response
  "Utilities for the service response"
  (:require [io.pedestal.http :as http]))

(defn- set-headers
  "Sets the response headers"
  [data]
  data)

(defn json-ok
  "Returns a json response given the clojure object"
  [obj]
  (set-headers (http/json-response obj)))
