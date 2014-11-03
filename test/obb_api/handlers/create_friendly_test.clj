(ns obb-api.handlers.create-friendly-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(defn create-dummy-game
  "Creates a dummy random game"
  [challenger opponent]
  (let [data {:challenger challenger :opponent opponent}
        [response status] (service/post-json "donbonifacio"
                                             "/game/create/friendly"
                                             data)]
    [response status]))

(deftest create-friendly-test
  (let [[response status] (create-dummy-game "donbonifacio" "Pyro")]
    (is (= status 200))
    (is (response :starting_stash))))

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

(deftest create-friendly-fail-no-opponent-exists-test
  (let [data {:challenger "Pyro" :opponent "Waza"}
        [response status] (service/post-json "donbonifacio"
                                             "/game/create/friendly"
                                             data)]
    (is (= "InvalidOpponent" (response :error)))
    (is (= status 412))))

(deftest create-friendly-fail-no-challenger-exists-test
  (let [data {:challenger "Waza" :opponent "Pyro"}
        [response status] (service/post-json "donbonifacio"
                                             "/game/create/friendly"
                                             data)]
    (is (= "InvalidChallenger" (response :error)))
    (is (= status 412))))
