(ns ru.nsu.ccfit.12222.seon.schema
  (:use [ru.nsu.ccfit.12222.seon.core]))

(defmulti valid?
          "Validate SEON expression against schema."
          (fn [schema expr]
            (:type schema)))


(defmethod valid? :default
  [schema expr]
  (throw (Exception. (str "Unknown schema type: " (serialize schema)))))

(defmethod valid? "object"
  [schema expr]
  (and
    (map? expr)
    (if (:properties schema)
      (every? true? (map (fn [[property schema]]
                           (if (or (:required schema) (property expr))
                             (valid? schema (property expr))
                             true)
                           ) (:properties schema)))
      true)))