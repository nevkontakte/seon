(ns ru.nsu.ccfit.g12222.seon.path
  (:use [ru.nsu.ccfit.g12222.seon.core]))


(defn walk-expression
  [expr mas stack]
  (if (nil? mas)

    (list expr)
    (cond
      (= (first mas) "{") (if (= (second mas) expr) (list expr) `())

      (seon-map? expr)
      (cond (= "*" (first mas))
            (reduce concat (map
                             (fn [subExp] (walk-expression subExp (next mas) stack))
                             (vals expr)))



            (= ".." (first mas))
            (concat
              (walk-expression expr (next mas) stack)
              (reduce concat (map (fn [subExp]
                                    (walk-expression subExp mas stack))
                                  (vals expr)))
              )





            :else
            (walk-expression ((keyword (first mas)) expr) (next mas) stack))

      (seon-list? expr)
      (cond (= ".." (first mas))
            (reduce concat (map
                             (fn [subExp] (walk-expression subExp (next mas)))
                             expr))

            (= "*" (first mas))
            (concat
              (walk-expression expr (next mas))
              (reduce concat (map (fn [subExp]
                                    (walk-expression subExp mas))
                                  expr))
              )

            :else
            (let [index (Integer/parseInt (first mas))]
              (cond
                (>= index (count expr)) `()
                :else (walk-expression (nth expr index) (next mas) stack)
                )))


      (and (not= mas nil) (seon-atom? expr)) `()
      :else (list expr)
      ))

  )

(defn query
  [expr string]
  {:pre [(seon? expr)]}
  (walk-expression expr (re-seq #"\w+|[.]{2}|(?<=\[)[0-9]+(?=\])|[\*]|[\{]+|[\w+]+(?=\})" string) ())

  )





