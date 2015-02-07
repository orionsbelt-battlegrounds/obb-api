(ns obb-api.handlers.open-games-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-api.handlers.create-friendly-test :as create-friendly-test]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest latest-games-smoke
  (let [[_ _] (create-friendly-test/create-dummy-game "donbonifacio" "")
        [response status] (service/get-json "donbonifacio" (str "/lobby/open-games"))]
    (is (not (empty? response)))
    (is (= 200 status))
    (let [game (first response)]
      (is game)
      (is (get game :starting-stash))
      (is (get-in game [:p1 :name]))
      (is (nil? (get game :p2)))
      (testing "if joining the game clears it from the lobby"
        (let [game-id (game :_id)
              join-url (str "/game/" game-id  "/join")
              [response status] (service/put-json "Pyro" join-url "")]
          (is (= 200 status))
          (let [[response status] (service/get-json "donbonifacio" (str "/lobby/open-games"))]
            (is (= 200 status))
            (let [new-game (first response)]
              (is new-game)
              (is (not= (:_id game) (:_id new-game))))))))))
