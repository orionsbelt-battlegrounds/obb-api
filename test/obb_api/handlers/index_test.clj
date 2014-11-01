(ns obb-api.handlers.index-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest index-test
  (let [[response status] (service/get-json "/")]
    (is (= status 200))
    (is (= response {:name "obb-api"}))))
