(ns obb-api.interceptors.auth-interceptor
  (:require [io.pedestal.interceptor :as interceptor]
            [obb-api.core.auth :as auth]))

(defn- get-raw-token
  "Gets a token from the request"
  [context]
  nil)

(defn- parse-token
  "Parses a token from the context"
  [context]
  (if-let [token (get-raw-token context)]
    (auth/parse token)))

(defn token
  "Gets the request's token"
  [request]
  (request :auth))

(interceptor/defbefore parse
  "Loads and stores the token"
  [context]
  (assoc-in context [:request :auth] (parse-token context)))

(interceptor/defbefore verify
  "Verifies a request token"
  [context]
  context)
