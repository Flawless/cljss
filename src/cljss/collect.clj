(ns cljss.collect
  (:require [cljss.utils :refer [build-css]]))

(def env* (atom 0))

(defn reset-env! []
  (reset! env* 0))

(defn dynamic? [[_ value]]
  (not (or (string? value)
           (number? value))))

(defn varid [idx [rule]]
  [rule (str "--var-" idx)])

(defn collect-styles [styles]
  (let [dynamic (filterv dynamic? styles)
        static  (filterv (comp not dynamic?) styles)
        vars
                (reduce
                  (fn [vars ds]
                    (let [ret (conj vars (varid @env* ds))]
                      (swap! env* inc)
                      ret))
                  []
                  dynamic)
        vals    (mapv (fn [[_ var] [_ exp]] [var exp]) vars dynamic)
        static  (->> vars
                     (map (fn [[rule var]] [rule (str "var(" var ")")]))
                     (concat static))]
    [static vals]))
