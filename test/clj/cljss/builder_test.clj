(ns cljss.builder-test
  (:require [cljss.builder :as sut]
            [cljss.core-test :as core-test]
            [clojure.test :refer [use-fixtures deftest is testing]]
            [cljss.collect :as collect]))

(use-fixtures :each core-test/reset-env-fixture)

(deftest test-styles-builder
  (testing "static"
    (is (= ["cls" [".cls{color:#fff;size:1px;}"] []]
           (sut/build-styles "cls" {:color "#fff" :size "1px"}))))

  (testing "pseudos"
    (is (= ["cls" [".cls{}" ".cls:hover{color:red;}"] []]
           (sut/build-styles "cls" {:&:hover {:color "red"}}))))

  (testing "keyword selector"
    (is (= ["cls" [".cls{}" ".cls a{color:blue;}"] []]
           (sut/build-styles "cls" {:a {:color "blue"}}))))

  (testing "dynamic"
    (is (= '["cls" [".cls{color:var(--var-0);}"] [["--var-0" x]]]
           (sut/build-styles "cls" {:color 'x}))))

  (testing "dynamic nested"
    (collect/reset-env!)
    (is (= '["cls" [".cls{}" ".cls.cls2{color:var(--var-0);}"] [["--var-0" x]]]
           (sut/build-styles "cls" {:.cls2 {:color 'x}}))))

  (testing "dynamic pseudos"
    (collect/reset-env!)
    (is (= '["cls" [".cls{}" ".cls:hover{color:var(--var-0);}"] [["--var-0" x]]]
           (sut/build-styles "cls" {:&:hover {:color 'x}}))))

  (testing "media"
    (is (= ["cls" [".cls{}" "@media (max-width:850px){.cls{color:red;}}"] []]
           (sut/build-styles "cls" {[[:max-width "850px"]] {:color "red"}}))))

  (testing "nested media"
    (is (= ["cls" [".cls{}" ".cls a{}" "@media (max-width:850px){.cls a{color:red;}}"] []]
           (sut/build-styles "cls" {:a {[[:max-width "850px"]] {:color "red"}}}))))

  (testing "more than one level of nesting"
    (is (= ["cls" [".cls{}" ".cls.cls2{}" ".cls.cls2 a{color:red;}"] []]
           (sut/build-styles "cls" {:.cls2 {:a {:color "red"}}})))))

(deftest test-build-name
  (testing "test selectors"
    (is (= "cls a" (sut/build-name "cls" :a)))
    (is (= "cls.cls2" (sut/build-name "cls" :.cls2)))
    (is (= "cls:hover" (sut/build-name "cls" :&:hover))))
  (testing "test media queries"
    (is (= "cls" (sut/build-name "cls" [[:max-width "850px"]])))))
