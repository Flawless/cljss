(ns cljss.utils-test
  (:require [cljss.utils :as sut]
            [clojure.test :as t]))

(t/deftest test-build-css
  (t/testing "static"
    (t/is (= ".cls{color:red;size:13em;}"
             (sut/build-css ".cls" nil [[:color "red"] [:size "13em"]]))))
  (t/testing "media"
    (t/is (= "@media all and (max-width: 443px){.cls{color:red;size:13em;}}"
             (sut/build-css ".cls" "@media all and (max-width: 443px)" [[:color "red"] [:size "13em"]])))))
