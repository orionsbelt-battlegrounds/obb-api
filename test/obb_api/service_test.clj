(ns obb-api.service-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [obb-api.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))

(defn get-raw
  "Gets a response"
  [url]
  (response-for service :get url))

(defn- parse-json
  "Parses the response json"
  [raw]
  (try
    (json/read-str raw :key-fn keyword)
    (catch Exception e (str "Error parsing JSON: " raw))))

(defn get-json
  "Gets a json response"
  [url]
  (-> (get-raw url)
      :body
      (parse-json)))

(defn get-headers
  "Gets the response headers"
  [url]
  (-> (get-raw url)
      :headers))

