(ns obb-api.handlers.auth.verify-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest verify-test
  (is (=
       (service/get-json "/auth/verify")
       {:info "NoTokenFound"})))


