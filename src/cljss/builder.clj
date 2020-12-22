(ns cljss.builder
  (:require [cljss.collect :as c]
            [cljss.media :as media]
            [cljss.utils :as utils]))

(defn status? [[rule value]]
  (and (re-matches #"^.*\?$" (name rule))
       (map? value)))

(defn build-name [cls rule]
  (cond
    (vector? rule) (str cls)
    (keyword? rule) (let [rule (name rule)]
                      (cond
                        (re-matches #"[a-z].*" rule) (str cls " " rule)
                        (re-matches #"&:.*" rule) (str cls (subs rule 1))
                        :else (str cls rule)))))

(defn- build-styles-recursive [cls media styles]
  "Recursively translates map `styles` into vector of vectors, `cls` must be a
   on input takes string with css selector and map with styles, styles may be
   nested
   Returns vector contains processed styles and vals"
  (let [nested          (filterv utils/nested? styles)
        styles          (filterv #(and (not (utils/nested? %))) styles)
        nstyles         (->> nested
                             (mapv
                              (fn [[rule styles]]
                                (build-styles-recursive (build-name cls rule)
                                                        (if (vector? rule)
                                                          (media/-compile-media-query rule))
                                                        styles))))
        [static vals]   (c/collect-styles styles)
        static          (utils/build-css cls media static)
        vals            (into [] vals)
        vals            (into vals (mapcat second nstyles))
        static          (concat [static] (mapcat first nstyles))]
    [static vals]))

(defn build-styles [cls styles]
  (concat [cls] (build-styles-recursive (str \. cls) nil styles)))

(comment
  (build-styles
   "hello"
   {"&:first-child" {:color "red"}
    "a" {:color "blue"
         :cljss.core/media {[:screen :and [:max-width "850px"]]
                            {:color "yellow"}}}})
  (build-styles
   "hello"
   {"&:first-child" {:color "red"}
    "div" {:color "blue"
           "a" {:color "green"}}
    "h1" {:color "#321"}}))
