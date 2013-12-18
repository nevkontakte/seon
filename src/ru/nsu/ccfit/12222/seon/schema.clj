(ns ru.nsu.ccfit.12222.seon.schema
  (:use [ru.nsu.ccfit.12222.seon.core]))

;; General valiadtion functions

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


;; Numbers validation

(defn- valid?-multipleOf
  [schema expr]
  (if (:multipleOf schema)
    (= (float (mod expr (:multipleOf schema)))  0.0)
    true))

(defn- valid?-maximum
  [schema expr]
  (let [pred (if (:exclusiveMaximum schema)
               <
               <=)]
    (if (:maximum schema)
      (pred expr (:maximum schema))
      true))
    )

(defmethod valid?-type "integer"
  [schema expr]
  (and
    (integer? expr)
    (valid?-multipleOf schema expr)
    (valid?-maximum schema expr)))

(defmethod valid?-type "number"
  [schema expr]
  (and
    (or (float? expr) (integer? expr))
    (valid?-multipleOf schema expr)
    (valid?-maximum schema expr)))


;; String validation

(defn- valid?-length
  [schema expr]
  (and
    (if (:minLength schema)
      (>= (count expr) (:minLength schema)) true)
    (if (:maxLength schema)
      (<= (count expr) (:maxLength schema)) true)
    ))

(defmethod valid?-type "string"
           [schema expr]
  (and
    (string? expr)
    (valid?-length schema expr)))


;; Other types

(defn- valid?-nil
  [schema expr]
  (nil? expr))

(defmethod valid?-type "null"
           [schema expr]
  (valid?-nil schema expr))

(defmethod valid?-type "nil"
           [schema expr]
  (valid?-nil schema expr))

(defmethod valid?-type "boolean"
           [schema expr]
  (or (true? expr) (false? expr)))

(defmethod valid?-type "object"
  [schema expr]
  (and
    (map? expr)
    (if (:properties schema)
      (and
        (if (:required schema)
          (every? (fn [property] (not (nil? (property expr)))) (:required schema))
          true)
        (every? true? (map (fn [[property schema]]
                             (if (property expr)
                               (valid?-type schema (property expr))
                               true)
                             ) (:properties schema)))
        )
      true)))

(defmethod valid?-type "array"
           [schema expr]
  (and
    (seq? expr)
    (if (:items schema)
      (every? true? (map (partial valid?-type (:items schema)) expr))
      true)))