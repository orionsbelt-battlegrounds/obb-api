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

(deftest play-game-smoke
  (let [[game _] (create-game)
        data {}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id) "/turn")
                                            data)]
    (is (not= 404 status))))
