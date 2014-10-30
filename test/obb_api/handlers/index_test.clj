(ns obb-api.handlers.index-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest index-test
  (is (=
       (service/get-json "/")
       {:name "obb-api"})))
