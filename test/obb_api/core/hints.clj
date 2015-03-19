(ns obb-api.core.hints
  "Gathers hints for a player to build some actions"
  (:require [obb-rules.game :as game]
            [obb-rules.element :as element]
            [obb-rules.simplifier :as simplify]
            [obb-rules.actions.move :as move]
            [obb-rules.board :as board]))

(defn- hints-for-element
  "Gathers the hints for a specific element"
  [board element]
  {:coord (element/element-coordinate element)
   ;:goto (move/find-all-possible-destinations-with-cost board element)
   })

(defn for-game
  "Returns to the given hash hints to use to move the elements"
  [game viewer]
  (when (and viewer (simplify/name= viewer (get-in game [:board :state])))
    (let [board (:board game)
          elements (board/board-elements board viewer)
          hints-for-element (partial hints-for-element board)]
      (println elements)
      (map hints-for-element elements))))

