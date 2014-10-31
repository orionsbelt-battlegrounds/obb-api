(ns obb-api.interceptors.auth-interceptor
  (:require [io.pedestal.interceptor :as interceptor]
            [obb-api.core.auth :as auth]))

(defn- get-raw-token
  "Gets a token from the request"
  [context]
  "donbonifacio")

(defn- get-token
  "Parses a token from the context"
  [context]
  #_(auth/parse (get-raw-token context))
  "donbonifacio")

(interceptor/defbefore parse
  "Loads and stores the token"
  [context]
  (assoc-in context [:request :auth] (get-token context)))

(interceptor/defbefore verify
  "Verifies a request token"
  [context]
  context)
