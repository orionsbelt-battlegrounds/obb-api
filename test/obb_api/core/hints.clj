(ns obb-api.core.hints
  "Gathers hints for a player to build some actions"
  (:require [obb-rules.game :as game]
            [obb-rules.element :as element]
            [obb-rules.simplifier :as simplify]
            [obb-rules.translator :as translator]
            [obb-rules.actions.move :as move]
            [obb-rules.board :as board]))

(defn- all-possible-moves
  "Gets all possible moves, with the coordinate translated"
  [board viewer element]
  (->> (move/find-all-possible-destinations-with-cost board element)
       (reduce (fn [col [coord cost]] (assoc col (translator/coordinate viewer coord) cost)) {})))

(defn- hints-for-element
  "Gathers the hints for a specific element"
  [board viewer element]
  {:coord (translator/coordinate viewer (element/element-coordinate element))
   :goto (all-possible-moves board viewer element)})

(defn for-game
  "Returns to the given hash hints to use to move the elements"
  [game viewer]
  (when (and viewer (simplify/name= viewer (get-in game [:board :state])))
    (let [board (:board game)
          elements (board/board-elements board viewer)
          hints-for-element (partial hints-for-element board viewer)
          hints (map hints-for-element elements)]
      hints)))

