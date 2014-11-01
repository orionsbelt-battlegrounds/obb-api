(ns obb-api.interceptors.auth-interceptor
  (:require [io.pedestal.interceptor :as interceptor]
            [io.pedestal.impl.interceptor :as interceptor-impl]
            [ring.util.response :as ring-response]
            [obb-api.core.auth :as auth]))

(defn- get-raw-token
  "Gets a token from the request"
  [context]
  (get-in context [:request :params :token]))

(defn- parse-token
  "Parses a token from the context"
  [context]
  (try
    (if-let [token (get-raw-token context)]
      (auth/parse token))
    (catch Exception e)))

(defn token
  "Gets the request's token"
  [request]
  (request :auth))

(defn username
  "Gets the request's username"
  [request]
  (get-in request [:auth :claims :iss]))

(defn- handle-token
  "Loads and stores the token"
  [context]
  (let [token (parse-token context)]
    (-> context
        (assoc-in [:request :auth] token)
        (assoc-in [:request :auth :valid] (auth/valid? token)))))

(interceptor/defbefore parse
  "Loads and stores the token"
  [context]
  (handle-token context))

(defn- valid-token?
  "Checks if the token is present and valid"
  [context]
  (= true (get-in context [:request :auth :valid])))

(interceptor/defbefore enforce
  "Enforces a request token"
  [context]
  (let [parsed-context (handle-token context)]
    (if (valid-token? parsed-context)
      parsed-context
      (-> context
          (assoc :response {:status  403
                            :body    "{\"error\":\"Forbidden\"}"})
          (interceptor-impl/terminate)))))
