(ns obb-api.gateways.player-gateway
  "Abstraction that handles player persistence")

(def db
  "Memory store"
  {:donbonifacio {:name "donbonifacio"}
   :Pyro {:name "Pyro"}})

(defn find-players
  "Finds player by the given names"
  [names]
  (println (get-in db [(keyword (first names))]))
  (map #(db (keyword %)) names))

