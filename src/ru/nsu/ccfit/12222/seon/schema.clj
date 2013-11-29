(ns ru.nsu.ccfit.12222.seon.schema
  (:use [ru.nsu.ccfit.12222.seon.core]))

(defmulti validate
          "Validate SEON expression against schema."
          (fn [schema expr]
            (:type schema)))


(defmethod validate :default
  [schema expr]
  (throw (Exception. (str "Unknown schema type: " (serialize schema)))))