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
      (is (nil? (get game :p2))))))

(comment

(deftest latest-games-smoke-p2
  (let [[_ _] (create-friendly-test/create-dummy-game "donbonifacio" "Pyro")
        [response status] (service/get-json "Pyro" (str "/player/latest-games"))]
    (is (not (empty? response)))
    (is (= 200 status))))

(deftest latest-games-game-uri
  (let [[_ _] (create-friendly-test/create-dummy-game "donbonifacio" "Pyro")
        [response status] (service/get-json "donbonifacio" (str "/player/latest-games"))]
    (is (not (empty? response)))
    (is (= 200 status))
    (let [sample-game (first response)
          uri (sample-game :uri)]
      (let [[response status] (service/get-json uri)]
        (is (= 200 status))))))
)
