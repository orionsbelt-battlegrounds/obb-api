(ns obb-api.gateways.player-gateway
  "Abstraction that handles player persistence")

(def db
  "Memory store"
  {:donbonifacio {:name "donbonifacio"}
   :ShadowKnight {:name "ShadowKnight"}
   :Pyro {:name "Pyro"}})

(defn find-players
  "Finds player by the given names"
  [names]
  (map #(db (keyword %)) names))

