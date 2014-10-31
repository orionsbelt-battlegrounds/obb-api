(ns obb-api.core.auth
  "Handles auth tokens"
  (:require [environ.core :refer [env]]
            [clj-jwt.core  :refer :all]
            [clj-time.core :refer [now plus days]]))

(def ^:private secret (or (env :obb-jwt-secret) "test"))

(defn- build-claim
  "Build a claim to sign"
  [user]
  {:iss user
   :exp (plus (now) (days -1))
   :iat (now)})

(defn token-for
  "Creates a token for a given user"
  [args]
  (-> (build-claim (args :user))
      jwt
      (sign :HS256 secret)
      to-str))

(defn parse
  "Verifies a given token"
  [token]
  (-> token str->jwt))

(defn valid?
  "Checks if a token is valid"
  [token]
  (verify token secret))

(defn username
  "Gets the username of the given token"
  [token]
  (get-in token [:claims :iss]))
