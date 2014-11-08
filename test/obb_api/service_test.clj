(ns obb-api.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [obb-api.core.auth :as auth]
            [cheshire.core :as json]
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
    (json/parse-string raw (fn [k] (keyword k)))
    (catch Exception e (str "Error parsing JSON: " raw))))

(defn get-json
  "Gets a json response"
  [url]
  (let [response (get-raw url)]
    [(-> response :body parse-json) (response :status)]))

(defn get-headers
  "Gets the response headers"
  [url]
  (-> (get-raw url)
      :headers))

(defn add-token
  "Adds a valid auth token to a given URL"
  [username url]
  (str url "?token=" (auth/token-for {:user username})))

(defn post-json
  "Posts a json request"
  [username url obj]
  (let [authed-url (add-token username url)
        data (json/generate-string obj)
        response (response-for service :post authed-url
                                       :body data
                                       :headers {"Content-Type" "application/json"})]
    [(-> response :body parse-json) (response :status)]))


(defn put-json
  "PUTs a json request"
  [username url obj]
  (let [authed-url (add-token username url)
        data (json/generate-string obj)
        response (response-for service :put authed-url
                                       :body data
                                       :headers {"Content-Type" "application/json"})]
    [(-> response :body parse-json) (response :status)]))

