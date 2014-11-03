(ns obb-api.handlers.show-game-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.core.auth :as auth]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(def username "donbonifacio")
(def test-token (auth/token-for {:user username}))

(deftest show-game-invalid-test
  (let [[response status] (service/get-json "/game/AAA")]
    (is (= status 404))))
