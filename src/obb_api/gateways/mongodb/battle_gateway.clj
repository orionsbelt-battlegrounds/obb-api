(ns obb-api.gateways.mongodb.battle-gateway
  "Persists battles on mongodb"
  (:require [monger.core :as mg]
            [environ.core :refer [env]]
            [monger.result :refer [ok? has-error?]]
            [monger.collection :as mc])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(def mongodb-uri (or (env :mongosoup-url) "mongodb://127.0.0.1/obb"))
(def collection-name "battles")

(defn- db
  "Gets the current DB to handle"
  []
  (let [{:keys [conn db]} (mg/connect-via-uri mongodb-uri)]
    db))

(defn- build
  "Builds a mongodb object based on the given hash"
  [args]
  {:p1 (args :p1)
   :p2 (args :p2)
   :board (args :board)
   :starting-stash (get-in args [:board :stash])})

(defn create-battle
  "Creates a battle"
  [args]
  (-> (db)
      (mc/insert-and-return collection-name (build args))))

(defn load-battle
  "Loads a persisted battle"
  [id]
  (when (ObjectId/isValid id)
    (-> (db)
        (mc/find-one-as-map collection-name {:_id (ObjectId. id)}))))

(defn- resolve-id
  "Verifies the id of the game"
  [game]
  (if (string? (game :_id))
    (assoc game :_id (ObjectId. (game :_id)))
    game))

(defn update-battle
  "Updates a battle given a result"
  [game]
  (let [result (-> (db)
                   (mc/save-and-return collection-name (resolve-id game)))]
    result))
