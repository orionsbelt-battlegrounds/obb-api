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
                                          (stash/create :kamikaze 2)))

(deftest error-if-no-actions-test
  (let [[game _] (create-game)
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            nil)]
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

(deftest deploy-invalid-action-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [4 4]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (is (= false (response :success)))
    (is (= status 422))))

(deftest deploy-incomplete-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 1 :kamikaze [8 8]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (is (= "StashNotCleared" (response :error)))
    (is (= status 412))))

(deftest deploy-success-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [8 8]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (is (= true (response :success)))
    (is (= status 200))))

(deftest deploy-success-2-actions-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 1 :kamikaze [8 8]]
                        [:deploy 1 :kamikaze [7 7]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (is (= true (response :success)))
    (is (= status 200))))

(deftest deploy-success-p2-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [8 8]]]}
        [response status] (service/put-json "Pyro"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (is (= true (response :success)))
    (is (= status 200))))

(deftest deploy-complete-success-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [8 8]]]}
        [response status] (service/put-json "Pyro"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)
        data {:actions [[:deploy 2 :kamikaze [1 8]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (let [state (get-in response [:board :state])]
      (is (or (= "p1" state) (= "p2" state))))
    (is (empty? (get-in response [:board :stash :p1])))
    (is (empty? (get-in response [:board :stash :p2])))
    (is (= true (response :success)))
    (is (= status 200))))
