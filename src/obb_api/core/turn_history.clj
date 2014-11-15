(ns obb-api.core.turn-history
  (:require [clj-time.core :refer [now plus days]]))

(defn- init-container
  "Inits a hash to receive the turn history"
  [container]
  (if (container :history)
    container
    (assoc container :history [])))

(defn- add-action-results
  "Adds action results to the history"
  [container action-results]
  (let [commands (into [] (map #(first %) action-results))
        history (get container :history)
        added-history (conj history {:at (now)
                                     :actions commands})]
  (assoc container :history added-history)))

(defn register
  "Register action turns as game history"
  [container action-results]
  (-> container
      (init-container)
      (add-action-results action-results)))
