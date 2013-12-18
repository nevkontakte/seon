(ns ru.nsu.ccfit.12222.seon.schema
  (:use [ru.nsu.ccfit.12222.seon.core]))

(defmulti valid?-type
          "Validate SEON expression against schema."
          (fn [schema expr]
            (:type schema)))

(defn valid?
  "Validate SEON expression common for all types."
  [schema expr]
  (and
    (if (:type schema) (valid?-type schema expr) true))
  )

(defn invalid?
  "Shortcut for (not (valid schema expr))."
  [schema expr]
  (not (valid? schema expr)))


(defmethod valid?-type :default
  [schema expr]
  (throw (Exception. (str "Unknown schema type: " (serialize schema)))))

(defmethod valid?-type "integer"
  [schema expr]
  (integer? expr))

(defmethod valid?-type "number"
  [schema expr]
  (or (float? expr) (integer? expr)))

(defn- valid?-nil
  [schema expr]
  (nil? expr))

(defmethod valid?-type "null"
           [schema expr]
  (valid?-nil schema expr))

(defmethod valid?-type "nil"
           [schema expr]
  (valid?-nil schema expr))

(defmethod valid?-type "string"
           [schema expr]
  (string? expr))

(defmethod valid?-type "boolean"
           [schema expr]
  (or (true? expr) (false? expr)))

(defmethod valid?-type "object"
  [schema expr]
  (and
    (map? expr)
    (if (:properties schema)
      (every? true? (map (fn [[property schema]]
                           (if (or (:required schema) (property expr))
                             (valid?-type schema (property expr))
                             true)
                           ) (:properties schema)))
      true)))

(defmethod valid?-type "array"
           [schema expr]
  (and
    (seq? expr)
    (if (:items schema)
      (every? true? (map (partial valid?-type (:items schema)) expr))
      true)))