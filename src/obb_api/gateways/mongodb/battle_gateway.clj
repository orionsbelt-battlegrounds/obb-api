(ns obb-api.gateways.mongodb.battle-gateway
  "Persists battles on mongodb"
  (:require [monger.core :as mg]
            [environ.core :refer [env]]
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
   :battle (args :battle)
   :starting-stash (get-in args [:battle :stash])})

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