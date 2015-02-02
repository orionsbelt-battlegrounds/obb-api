(ns obb-api.handlers.auth.anonymize-test
  (:require [clojure.test :refer :all]
            [obb-api.service-test :as service]
            [obb-api.core.auth :as auth]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]))

(deftest anonymize-test
  (let [username "anonymous:123"
        [response status] (service/get-json (str "/auth/anonymize?username=" username))]
    (is (= status 200))
    (let [token-str (:token response)
          token (auth/parse token-str)]
      (is token-str)
      (is (auth/valid? token))
      (is (= username (auth/username token))))))

(deftest anonymize-test-no-username
  (let [username ""
        [response status] (service/get-json (str "/auth/anonymize?username=" username))]
    (is (= status 412))))

(deftest anonymize-test-not-annon-username
  (let [username "donbonifacio"
        [response status] (service/get-json (str "/auth/anonymize?username=" username))]
    (is (= status 412))))
