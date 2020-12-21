(ns cljss.collect-test
  (:require [cljss.collect :as sut]
            [cljss.core-test :as core-test]
            [clojure.test :as t]))

(t/use-fixtures :each core-test/reset-env-fixture)

(t/deftest test-collect-styles
  (t/testing "static"
    (t/is (= '[([:color "red"] [:size "13em"]) []] (sut/collect-styles {:color "red" :size "13em"}))))
  (t/testing "dynamic"
    (t/is (= '[([:color "var(--var-0)"]) [["--var-0" x]]]
             (sut/collect-styles {:color 'x})))))
