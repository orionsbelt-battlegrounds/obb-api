(ns obb-api.handlers.play-game-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-rules.stash :as stash]
            [obb-api.handlers.deploy-game-test :as deploy-game-test]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(defn- create-game
  "Creates a game where p1 is the first to play"
  []
  (let [[game status] (deploy-game-test/create-deployed-game)
        p1-game (assoc-in game [:board :state] "p1")
        updated (battle-gateway/update-battle p1-game)]
    updated))

(defn- make-move
  "Makes a move on a game"
  ([game data]
   (make-move game data "donbonifacio"))
  ([game data username]
   (service/put-json username
                     (str "/game/" (game :_id) "/turn")
                     data)))

(deftest play-game-smoke
  (let [game (create-game)
        [response status] (make-move game nil)]
    (is (not= 404 status))))

(deftest error-if-no-actions-test
  (let [game (create-game)
        [response status] (make-move game nil)]
    (is (= "EmptyJSON" (response :error)))
    (is (= 412 status))))

(deftest error-if-invalid-game-test
  (let [game {:_id "waza"}
        [response status] (make-move game {})]
    (is (= "InvalidGame" (response :error)))
    (is (= 404 status))))

(deftest error-if-invalid-player-test
  (let [game (create-game)
        [response status] (make-move game {} "ShadowKnight")]
    (is (= "InvalidPlayer" (response :error)))
    (is (= 401 status))))

(defn- get-waiting-player
  "Gets the player of the game that's waiting to play"
  [game]
  (let [current (get-in game [:board :state])
        other-code (first (disj #{"p1" "p2"} current))]
    (get-in game [(keyword other-code) :name])))

(deftest error-if-not-player-turn-test
  (let [game (create-game)
        other-player (get-waiting-player game)
        [response status] (make-move game {:actions []} other-player)]
    (is (= "InvalidPlayer" (response :error)))
    (is (= 401 status))))

(deftest allow-zero-actions-test
  (let [game (create-game)
        [response status] (make-move game {:actions []} "donbonifacio")]
    (is (= true (response :success)))
    (is (= 200 status))))

(deftest make-move-toggles-player
  (let [game (create-game)
        [response status] (make-move game {:actions []} "donbonifacio")]
    (is (= "p2" (get-in response [:board :state])))))

(deftest make-single-action-invalid
  (let [game (create-game)
        [response status] (make-move game
                                     {:actions [[:move [4 4] [4 5] 1]]}
                                     "donbonifacio")]
    (is (= false (response :success)))
    (is (= 422 status))))

#_(deftest make-single-action-success
  (let [game (create-game)
        [response status] (make-move game
                                     {:actions [[:move [2 7] [1 7] 1]]}
                                     "donbonifacio")]
    (println response)
    (is (= true (response :success)))
    (is (= 200 status))))
