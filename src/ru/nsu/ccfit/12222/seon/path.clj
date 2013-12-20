(ns ru.nsu.ccfit.12222.seon.path
  (:use [ru.nsu.ccfit.12222.seon.core]))

(defn function
  [expr mas]
  (if (nil? mas)
    (list expr)
    (cond (seon-map? expr)
          (cond (= "*" (first mas))
                (map
                  (fn [subExp] (function subExp (next mas)))
                  (vals expr))

                (= ".." (first mas))
                (concat
                  (function expr (next mas))
                  (reduce concat (map (fn [subExp]
                                        (function subExp mas))
                                      (vals expr)))
                  )


                :else
                (function ((keyword (first mas)) expr) (next mas)))
          :else (list expr)
          ))

  )


(defn parser
  [expr string]
  {:pre [(seon? expr)]}
  (function expr (re-seq #"\w+|[.]{2}|(?<=\[)[0-9]+(?=\])" string))

  )

