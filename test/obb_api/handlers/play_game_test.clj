(ns obb-api.handlers.play-game-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-rules.stash :as stash]
            [obb-api.handlers.deploy-game-test :as deploy-game-test]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest play-game-smoke
  (let [[game _] (deploy-game-test/create-deployed-game)
        data {}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id) "/turn")
                                            data)]
    (is (not= 404 status))
    (is (#{"p1" "p2"} (get-in game [:board :state])))))
