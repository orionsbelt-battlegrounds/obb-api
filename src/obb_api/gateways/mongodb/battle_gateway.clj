(ns obb-api.gateways.mongodb.battle-gateway
  "Persists battles on mongodb"
  (:require [monger.core :as mg]
            [environ.core :refer [env]]
            [monger.result :refer [ok? has-error?]]
            [monger.joda-time]
            [monger.collection :as mc]
            [monger.query :as mq])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

(def mongodb-uri (or (env :mongosoup-url) "mongodb://127.0.0.1/obb?maxPoolSize=128&waitQueueMultiple=5;waitQueueTimeoutMS=150;socketTimeoutMS=5500&autoConnectRetry=true;safe=false&w=1;wtimeout=2500;fsync=true"))
(def collection-name "battles")
(def master-conn (mg/connect-via-uri mongodb-uri))

(defn- db
  "Gets the current DB to handle"
  []
  (let [{:keys [conn db]} master-conn]
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

(defn load-latest-battles
  "Loads the latest battles from the given username"
  [username]
  (-> (db)
      (mq/with-collection collection-name
          (mq/find {:$or [{:p1 {:name username}} {:p2 {:name username}}] })
          (mq/sort (sorted-map :_id -1))
          (mq/skip 00)
          (mq/limit 50))))

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
