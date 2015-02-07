(ns obb-api.handlers.open-games
  "Returns the open games on the lobby"
  (:require [obb-api.response :as response]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.handlers.show-game :as show-game]
            [obb-api.gateways.battle-gateway :as battle-gateway]))

(defn handler
  "Processes turn actions"
  [request]
  (-> (battle-gateway/load-open-games)
      (response/json-ok)))
