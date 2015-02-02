(ns obb-api.handlers.auth.anonymize
  "Generates an auth token for a given anonymous username"
  (:require [obb-api.response :as response]
            [obb-api.core.auth :as auth]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]))

(defn- validate
  "Validates if the username can be anonymized"
  [username]
  (cond
    (clojure.string/blank? username) ["InvalidUsername" 412]
    (not (re-matches #"anonymous:.*" username)) ["InvalidUsername" 412]))

(defn handler
  "Shows information about the current auth token"
  [request]
  (let [username (get-in request [:params :username])]
    (if-let [[error status] (validate username)]
      (response/json-error {:error error} status)
      (response/json-ok {:token (auth/token-for {:user username})}))))
