(ns obb-api.handlers.create-friendly-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest create-friendly-smoke-test
  (let [data {:challenger "donbonifacio" :other "Pyro"}
        [response status] (service/post-json "donbonifacio"
                                             "/game/create/friendly"
                                             data)]
    (is (= status 200))))
