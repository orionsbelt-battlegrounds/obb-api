(ns obb-api.handlers.play-game-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-rules.stash :as stash]
            [obb-api.handlers.deploy-game-test :as deploy-game-test]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(defn- create-game
  "Creates a game where p1 is the first to play"
  []
  (-> (deploy-game-test/create-deployed-game)))

(defn- make-move
  "Makes a move on a game"
  ([game data]
   (make-move game data "donbonifacio"))
  ([game data username]
   (service/put-json username
                     (str "/game/" (game :_id) "/turn")
                     data)))

(deftest play-game-smoke
  (let [[game _] (create-game)
        [response status] (make-move game nil)]
    (is (not= 404 status))))

(deftest error-if-no-actions-test
  (let [[game _] (create-game)
        [response status] (make-move game nil)]
    (is (= "EmptyJSON" (response :error)))
    (is (= 412 status))))

(deftest error-if-invalid-game-test
  (let [game {:_id "waza"}
        [response status] (make-move game {})]
    (is (= "InvalidGame" (response :error)))
    (is (= 404 status))))

(deftest error-if-invalid-player-test
  (let [[game _] (create-game)
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
  (let [[game _] (create-game)
        other-player (get-waiting-player game)
        [response status] (make-move game {} other-player)]
    (is (= "InvalidPlayer" (response :error)))
    (is (= 401 status))))