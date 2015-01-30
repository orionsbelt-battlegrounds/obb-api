(ns obb-api.handlers.join-game-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-rules.stash :as stash]
            [obb-api.handlers.create-friendly-test :as create-friendly-test]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(defn- create-game
  "Creates a dummy sample game"
  [opponent]
  (create-friendly-test/create-dummy-game "donbonifacio"
                                          opponent
                                          (stash/create "kamikaze" 2)))
(deftest error-if-invalid-game-test
  (let [[response status] (service/put-json "donbonifacio"
                                            "/game/waza/join"
                                            {})]
    (is (= "InvalidGame" (response :error)))
    (is (= status 404))))

(deftest error-if-invalid-cant-join
  (let [[game _] (create-game "Pyro")
        [response status] (service/put-json "Pyro"
                                            (str "/game/" (game :_id)  "/join") "")]
    (is (= "CantJoin" (response :error)))
    (is (= status 412))))

(deftest join-success-test
  (let [[game _] (create-game "")
        game-id (game :_id)
        [response status] (service/put-json "Pyro"
                                            (str "/game/" game-id  "/join") "")]
    (is (= true (response :success)))
    (is (= status 200))
    (let [[response status] (service/get-json "Pyro" (str "/game/" game-id))]
      (is (= "Pyro" (get-in response [:p2 :name])))
      (is (= 2 (get-in response [:board :stash :p2 :kamikaze])))
      (is (= status 200)))))

