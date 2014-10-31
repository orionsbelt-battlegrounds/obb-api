(ns obb-api.core.auth-test
  (:require [clojure.test :refer :all]
            [obb-api.core.auth :as auth]))

(deftest generate-token
  (testing "generation"
    (let [token (auth/token-for {:user "donbonifacio"})]
      (is token)
      (testing "verify"
        (let [result (auth/parse token)]
          (is result)
          (is (auth/valid? result))
          (is (= "donbonifacio" (auth/username result))))))))
