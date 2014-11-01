(ns obb-api.handlers.auth.verify-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-api.core.auth :as auth]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(def test-token (auth/token-for {:user "donbonifacio"}))

(deftest verify-no-token-test
  (is (=
       (service/get-json "/auth/verify")
       {:info "NoTokenFound"})))

(deftest verify-token-test
  (let [response (service/get-json (str "/auth/verify?token=" test-token))]
    (is (get-in response [:header :alg]))
    (is (get-in response [:claims :iss]))))


