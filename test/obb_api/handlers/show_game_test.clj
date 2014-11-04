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
      (is (nil? (get-in response [:viewed-by])))
      (is (nil? (get-in response [:battle :elements])))
      (is (nil? (get-in response [:battle :stash])))
      (is (nil? (get-in response [:starting-stash])))
      (is (= status 200)))
    (testing "donbonifacio's view"
      (let [[response status] (service/get-json (str "/game/" game-id "?token=" token-donbonifacio))]
        (is (= "donbonifacio" (response :viewed-by)))
        (is (= status 200))))))
