(ns obb-api.core.turn-history-test
  (:require [clojure.test :refer :all]
            [obb-api.core.turn-history :as history]))

(deftest history-proper-initializes
  (let [data (history/register {} [])]
    (is (data :history))))

(deftest history-regists-new
  (let [data (history/register {} [[[:move [1 2] [1 3]] :dummy]
                                   [[:attack [2 2] [3 3]] :dummy]])]
    (is (= 1 (count data)))))
