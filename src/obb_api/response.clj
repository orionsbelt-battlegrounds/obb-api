(ns obb-api.response
  "Utilities for the service response"
  (:require [io.pedestal.http :as http]
            [cheshire.generate :refer [add-encoder encode-str]]))

(add-encoder org.bson.types.ObjectId encode-str)

(defn- set-headers
  "Sets the response headers"
  [data]
  data)

(defn json-ok
  "Returns a json response given the clojure object"
  [obj]
  (-> (http/json-response obj)
      (set-headers)))

(defn json-error
  "Returns a json response error given the clojure object"
  [obj]
  (-> (http/json-response obj)
      (set-headers)
      (assoc :status 412)))

(defn json-not-found
  "Returns a not tound error"
  []
  (-> (http/json-response {:error "NotFound"})
      (set-headers)
      (assoc :status 404)))
