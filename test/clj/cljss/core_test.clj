(ns cljss.core-test
  (:require [cljss.collect :as collect]
            [cljss.font-face :as ff]
            [cljss.inject-global :as ig]
            [clojure.test :refer :all]))

(defn reset-env-fixture [f]
        (collect/reset-env!)
  (f))

(deftest test-font-face
  (testing "build @font-face"
    (is (= '(cljs.core/str
              "@font-face{"
              "font-family:\""
              font-name
              "\";font-variant:normal;font-stretch:unset;font-weight:400;font-style:normal;unicode-range:U+0025-00FF, U+0025-00FF;src:local(\"Arial\")"
              \,
              \space
              "url(\""
              (str "examplefont" ".woff")
              "\")"
              \space
              "format(\"woff\");"
              "}")
           (ff/font-face {:font-family   'font-name
                          :font-variant  "normal"
                          :font-stretch  "unset"
                          :font-weight   400
                          :font-style    "normal"
                          :unicode-range ["U+0025-00FF" "U+0025-00FF"]
                          :src           [{:local "Arial"}
                                          {:url    '(str "examplefont" ".woff")
                                           :format "woff"}]})))))

(deftest test-inject-global
  (testing "build global styles"
    (is (=
          '(["body" "body{margin:0;}"]
            ["ul" "ul{list-style:none;color:red;}"]
            ["body > .app" (cljs.core/str "body > .app" "{" "border:" (str "1px solid" color) ";" "}")])
          (ig/inject-global {:body         {:margin 0}
                             :ul           {:list-style "none"
                                            :color      "red"}
                             "body > .app" {:border '(str "1px solid" color)}})))))
