(ns obb-api.handlers.auth.verify
  "Checks the auth token on the current request"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]))

(defn handler
  "Shows information about the current auth token"
  [request]
  (if-let [token (auth-interceptor/token request)]
    (response/json-ok token)
    (response/json-ok {:info "NoTokenFound"})))

(defn enforce
  "Does the same as index, but it's not expected to reach this if
  token in invalid"
  [request]
  (handler request))

