(ns citadel.district-test
  (:require [citadel.district :as district]
            [clojure.spec.alpha :as s]
            [clojure.test.check.generators :as gen]
            [clojure.test :refer [deftest are is testing]]
            [clojure.data.json :as json])
  (:import
   (clojure.lang ExceptionInfo)))

(deftest valid-json-districts-read-correctly?
  (doseq [d (gen/sample (s/gen ::district/spec))]
    (is
     (= (district/map->District d)
        (-> d json/json-str district/from-json-str)))))

(deftest invalid-json-district-throws?
  (testing "validate colors"
    (let [bad-color "{\"title\":\"title\", \"cost\":5, \"color\":\"azure\"}"]
      (is (thrown? ExceptionInfo (district/from-json-str bad-color)))))
  (testing "validate costs"
    (let [bad-cost "{\"title\":\"title\", \"cost\":0, \"color\":\"azure\"}"]
      (is (thrown? ExceptionInfo (district/from-json-str bad-cost)))))
  (testing "validate missing title"
    (let [bad-title "{\"cost\":0, \"color\":\"azure\"}"]
      (is (thrown? ExceptionInfo (district/from-json-str bad-title))))))
