(ns obb-api.handlers.create-friendly-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest create-friendly-smoke-test
  (let [data {:challenger "donbonifacio" :opponent "Pyro"}
        [response status] (service/post-json "donbonifacio"
                                             "/game/create/friendly"
                                             data)]
    (is (= status 200))))

(deftest create-friendly-fail-no-challenger-test
  (let [data {:opponent "Pyro"}
        [response status] (service/post-json "donbonifacio"
                                             "/game/create/friendly"
                                             data)]
    (is (= "EmptyChallenger" (response :error)))
    (is (= status 412))))

(deftest create-friendly-fail-no-opponent-test
  (let [data {:challenger "Pyro"}
        [response status] (service/post-json "donbonifacio"
                                             "/game/create/friendly"
                                             data)]
    (is (= "EmptyOpponent" (response :error)))
    (is (= status 412))))
