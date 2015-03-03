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
                                          (stash/create "kamikaze" 2)))
(defn create-deployed-game
  "Creates a dummy deployed game"
  []
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [7 7]]]}
        [response1 status1] (service/put-json "Pyro"
                                              (str "/game/" (game :_id)  "/deploy")
                                              data)
        data {:actions [[:deploy 2 :kamikaze [7 7]]]}
        [response2 status2] (service/put-json "donbonifacio"
                                             (str "/game/" (game :_id)  "/deploy")
                                             data)]
    [response2 status2]))

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

(deftest simulate-deploy-incomplete-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 1 :kamikaze [8 8]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy/simulate")
                                            data)]
    (is (= status 200))))

(deftest deploy-success-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [8 8]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)]
    (is (= true (response :success)))
    (is (= true (response :saved)))
    (is (response :viewed-by))
    (is (= "donbonifacio" (get-in response [:viewed-by :username])))
    (is (= "p1" (get-in response [:viewed-by :player-code])))
    (is (= status 200))
    (testing "get the game and check that the stash cleared for p1"
      (let [game-id (:_id game)
            [response status] (service/get-json "donbonifacio" (str "/game/" game-id))]
        (is (= status 200))
        (is (empty? (get-in response [:board :stash :p1])))))))

(deftest simulate-deploy-success-test
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [8 8]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy/simulate")
                                            data)]
    (is (= true (response :success)))
    (is (= false (response :saved)))
    (is (response :viewed-by))
    (is (= "donbonifacio" (get-in response [:viewed-by :username])))
    (is (= "p1" (get-in response [:viewed-by :player-code])))
    (is (= status 200))
    (testing "get the game and check that the stash is still present"
      (let [game-id (:_id game)
            [response status] (service/get-json "donbonifacio" (str "/game/" game-id))]
        (is (= status 200))
        (is (= 2 (get-in response [:board :stash :p1 :kamikaze])))))))

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
  (let [[response status] (create-deployed-game)
        state (get-in response [:board :state])]
    (is (or (= "p1" state) (= "p2" state)))
    (is (empty? (get-in response [:board :stash :p1])))
    (is (empty? (get-in response [:board :stash :p2])))
    (is (= true (response :success)))
    (is (= status 200))))

(deftest deploy-success-p1-viewed-by-p2
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [8 8]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)
        [show show-status] (service/get-json "Pyro" (str "/game/" (game :_id)))]
    (is (empty? (get-in show [:board :elements])))
    (is (show :viewed-by))
    (is (= show-status 200))))

(deftest deploy-success-p1-viewed-by-p1
  (let [[game _] (create-game)
        data {:actions [[:deploy 2 :kamikaze [8 8]]]}
        [response status] (service/put-json "donbonifacio"
                                            (str "/game/" (game :_id)  "/deploy")
                                            data)
        [show show-status] (service/get-json "donbonifacio" (str "/game/" (game :_id)))]
    (is (get-in show [:board :elements]))
    (testing "game was cleaned"
      (is (= "kamikaze" (get-in show [:board :elements (keyword "[8 8]") :unit]))))
    (is (show :viewed-by))
    (is (= show-status 200))))

