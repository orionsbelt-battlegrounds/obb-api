(ns obb-api.handlers.deploy-game-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-rules.stash :as stash]
            [obb-api.handlers.create-friendly-test :as create-friendly-test]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(defn- create-game
  "Creates a dummy sample game"
  []
  (create-friendly-test/create-dummy-game "donbonifacio"
                                          "Pyro"
                                          (stash/create :kamikaze 1)))

(deftest error-if-no-actions-test
  (let [[game _] (create-game)
        data {}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (is (= "EmptyJSON" (response :error)))
    (is (= status 412))))

(deftest error-if-invalid-game-test
  (let [[response status] (service/put-json "donbonifacio"
                                            "/game/waza/deploy"
                                            {})]
    (is (= "InvalidGame" (response :error)))
    (is (= status 404))))

(deftest error-if-invalid-username-test
  (let [[game _] (create-game)
        data {}
        [response status] (service/put-json "ShadowKnight"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (is (= "InvalidPlayer" (response :error)))
    (is (= status 401))))

