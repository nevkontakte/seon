(ns ru.nsu.ccfit.12222.seon.core)

(declare seon?)

(defn seon-atom?
  "Verify that passed expr is an atom in terms of SEON: integer, float, boolean or string."
  [expr]
  (or
    (integer? expr)
    (float? expr)
    (string? expr)
    (true? expr)
    (false? expr)
    ))

(defn seon-list?
  "Verify that passed expr is a list of valid seon expressions."
  [expr]
  (and
    (seq? expr)
    (every? seon? expr)))

(defn seon-map?
  "Verify that passed expr is a map of valid seon expressions."
  [expr]
  (and
    (map? expr)
    (every? seon? (vals expr))))

(defn seon?
  "Verify that passed expr is a valid seon expression."
  [expr]
  (or
    (seon-atom? expr)
    (seon-list? expr)
    (seon-map? expr)))

(defn serialize
  "Serialize SEON structure into string."
  [expr]
  {:pre [(seon? expr)]}
  (pr-str expr))


; TODO May be it's worth writing own parser.
; See http://clojuredocs.org/clojure_core/clojure.core/read#example_803 for explanation.
(defn unserialize
  "Parse SEON string into s-expression."
  [expr]
  {:post [(seon? %)]}
  (binding [*read-eval* false]
    (read-string expr)))


