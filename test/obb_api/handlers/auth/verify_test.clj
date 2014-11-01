(ns obb-api.handlers.auth.verify-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]
            [obb-api.core.auth :as auth]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(def username "donbonifacio")
(def test-token (auth/token-for {:user username}))

(deftest verify-no-token-test
  (let [[response status] (service/get-json "/auth/verify")]
    (is (= status 200))
    (is (= response {:valid false}))))

(deftest verify-token-test
  (let [[response status] (service/get-json (str "/auth/verify?token=" test-token))]
    (is (= status 200))
    (is (get-in response [:header :alg]))
    (is (get-in response [:claims :iss]))))

(deftest enforce-no-token-test
  (let [[response status] (service/get-json "/auth/enforce")]
    (is (= status 403))
    (is (= response {:error "Forbidden"}))))

(deftest enforce-token-test
  (let [[response status] (service/get-json (str "/auth/enforce?token=" test-token))]
    (is (= status 200))
    (is (get-in response [:header :alg]))
    (is (get-in response [:claims :iss]))))

