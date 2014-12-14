(ns obb-api.core.turn-processor
  "Logic and validations regarding turn processing"
  (:require [obb-api.response :as response]
            [obb-api.handlers.show-game :as show-game]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-api.core.turn-history :as history]
            [obb-rules.translator :as translator]
            [obb-rules.simplifier :as simplify]
            [obb-rules.turn :as turn]))

(defn process-actions
  "Applies the actions to the game's battle"
  [request game username]
  (when game
    (let [actions (get-in request [:json-params :actions])
          player-code (show-game/match-viewer game username)
          translated-actions (translator/actions player-code actions)
          built-game (simplify/build-result game)
          battle (built-game :board)]
      (apply turn/process battle player-code translated-actions))))

(defn- translate-board
  "Translates the board to the given player focus"
  [focus result]
  (let [board (result :board)
        translated (translator/board focus board)
        translated-result (assoc result :board translated)
        simplified (simplify/clean-result translated-result)]
    (simplified :board)))

(defn save-game
  "Saves a game after turn processing"
  [request game result username]
  (let [sresult (simplify/clean-result result)
        player-code (show-game/match-viewer game username)
        action-results (get-in sresult [:board :action-results])
        new-board (sresult :board)
        new-game (assoc game :board (dissoc new-board :action-results))
        new-game-with-history (history/register new-game action-results)]
    (battle-gateway/update-battle new-game-with-history)
    (-> new-game-with-history
        (dissoc :viewed-by)
        (assoc :board (translate-board player-code result))
        (assoc-in [:board :action-results] action-results)
        (assoc :success (sresult :success)))))

(defn turn-error-response
  "Outputs proper error"
  [error error-status processed]
  (if (= 422 error-status)
    (response/json-error (simplify/clean-result processed) error-status)
    (response/json-error {:error error} error-status)))

