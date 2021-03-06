(ns obb-api.handlers.show-game-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.handlers.create-friendly-test :as create-friendly-test]
            [obb-api.core.auth :as auth]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(def token-donbonifacio (auth/token-for {:user "donbonifacio"}))

(deftest show-game-invalid-test
  (let [[response status] (service/get-json "/game/AAA")]
    (is (= status 404))))

(deftest show-game-test
  (let [[dummy-game _] (create-friendly-test/create-dummy-game "donbonifacio" "ShadowKnight")
        game-id (get-in dummy-game [:_id])
        [response status] (service/get-json (str "/game/" game-id))]
    (testing "public view"
      (is (empty? (get-in response [:viewed-by])))
      (is (empty? (get-in response [:battle :elements])))
      (is (empty? (get-in response [:battle :stash :p1])))
      (is (empty? (get-in response [:battle :stash :p2])))
      (is (empty? (get-in response [:starting-stash])))
      (is (= status 200)))
    (testing "donbonifacio's view"
      (let [url (str "/game/" game-id "?token=" token-donbonifacio)
            [response status] (service/get-json url)]
        (is (empty? (get-in response [:board :stash :p2])))
        (is (not (empty? (get-in response [:board :stash :p1]))))
        (is (= "donbonifacio" (get-in response [:viewed-by :username])))
        (is (= "p1" (get-in response [:viewed-by :player-code])))
        (is (= status 200))))))
