(ns obb-api.handlers.latest-games-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-api.gateways.battle-gateway :as battle-gateway]
            [obb-api.handlers.create-friendly-test :as create-friendly-test]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest latest-games-smoke
  (let [[_ _] (create-friendly-test/create-dummy-game "donbonifacio" "Pyro")
        [response status] (service/get-json "donbonifacio" (str "/player/latest-games"))]
    (is (= 200 status))))

