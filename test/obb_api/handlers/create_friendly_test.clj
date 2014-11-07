(ns obb-api.handlers.create-friendly-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-rules.stash :as stash]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(defn- build-data
  "Creates the data for creating games"
  [challenger opponent stash]
  (let [base {:challenger challenger :opponent opponent}]
    (if (empty? stash)
      base
      (-> base
          (assoc :stash {})
          (assoc-in [:stash :challenger] (first stash))
          (assoc-in [:stash :opponent] (first stash))))))

(defn create-dummy-game
  "Creates a dummy random game"
  [challenger opponent & stash]
  (let [data (build-data challenger opponent stash)
        [response status] (service/post-json "donbonifacio"
                                             "/game/create/friendly"
                                            data)]
    [response status]))

(deftest create-friendly-test
  (let [[response status] (create-dummy-game "donbonifacio" "Pyro")]
    (is (= status 200))
    (is (response :starting-stash))))

(deftest create-friendly-custom-stash-test
  (let [stash (stash/create :rain 1)
        [response status] (create-dummy-game "donbonifacio" "Pyro" stash)]
    (is (= status 200))
    (is (= 1 (get-in response [:starting-stash :p1 :rain])))
    (is (= 1 (get-in response [:starting-stash :p2 :rain])))
    (is (response :starting-stash))))

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
