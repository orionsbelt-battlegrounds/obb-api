(ns obb-api.core.turn-processor
  "Logic and validations regarding turn processing"
  (:require [obb-api.response :as response]
            [clj-time.local :as l]
            [obb-api.handlers.show-game :as show-game]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-api.core.turn-history :as history]
            [obb-api.core.hints :as hints]
            [obb-rules.translator :as translator]
            [obb-rules.simplifier :as simplify]
            [obb-rules.turn :as turn]))

(defn process-actions
  "Applies the actions to the game's battle"
  [request game username save?]
  (when game
    (let [actions (get-in request [:json-params :actions])
          player-code (show-game/match-viewer game username)
          translated-actions (translator/actions player-code actions)
          built-game (simplify/build-result game)
          battle (built-game :board)
          process-fn (if save? turn/process-actions turn/simulate-actions)]
      (process-fn battle player-code translated-actions))))

(defn- translate-board
  "Translates the board to the given player focus"
  [focus result]
  (let [board (result :board)
        translated (translator/board focus board)
        translated-result (assoc result :board translated)
        simplified (simplify/clean-result translated-result)]
    (simplified :board)))

(defn- translate-results
  "Translates the results fo the view of the player"
  [viewer action-results]
  (map (fn translate-results [[action info]]
         [(translator/action viewer action) info])
       action-results))

(defn save-game
  "Saves a game after turn processing"
  [request game result username save?]
  (let [sresult (simplify/clean-result result)
        player-code (show-game/match-viewer game username)
        action-results (get-in sresult [:board :action-results])
        new-board (sresult :board)
        new-game (assoc game :board (dissoc new-board :action-results))
        new-game-with-history (history/register new-game action-results)]
    (-> new-game-with-history
        (assoc :updated-at (l/local-now))
        (battle-gateway/update-battle save?)
        (assoc :saved save?)
        (dissoc :viewed-by)
        (assoc :board (translate-board player-code result))
        (assoc-in [:board :action-results] (translate-results player-code action-results))
        (assoc :hints (hints/for-game (simplify/build-result game) player-code))
        (assoc :success (sresult :success)))))

(defn turn-error-response
  "Outputs proper error"
  [error error-status processed]
  (if (= 422 error-status)
    (response/json-error (simplify/clean-result processed) error-status)
    (response/json-error {:error error} error-status)))

