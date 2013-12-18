(ns ru.nsu.ccfit.12222.seon.test.schema_test
  (:use [ru.nsu.ccfit.12222.seon.core])
  (:use [ru.nsu.ccfit.12222.seon.schema])
  (:use [clojure.test])
  )

(deftest valid?-test
  (testing "Exception on unknown exception type."
    (is (thrown-with-msg?
          Exception #"^Unknown schema type"
          (valid? {:type "unknown-type"} {})))
    )
  )

(deftest valid?-integer-test
  (testing "Integer type validation."
    (is (valid?
          {:type "integer"}
          1))
    (is (invalid?
          {:type "integer"}
          1.0))
    (is (invalid?
          {:type "integer"}
          nil))
    (is (valid?
          {:type "integer" :multipleOf 2}
          4))
    (is (invalid?
          {:type "integer" :multipleOf 3}
          4))
    (is (valid?
          {:type "integer" :maximum 3}
          2))
    (is (valid?
          {:type "integer" :maximum 3}
          3))
    (is (invalid?
          {:type "integer" :maximum 3}
          4))
    (is (valid?
          {:type "integer" :maximum 3 :exclusiveMaximum true}
          2))
    (is (invalid?
          {:type "integer" :maximum 3 :exclusiveMaximum true}
          3))
    (is (invalid?
          {:type "integer" :maximum 3 :exclusiveMaximum true}
          4))
    (is (valid?
          {:type "integer" :maximum 3 :exclusiveMaximum false}
          3))
    ))

(deftest valid?-number-test
  (testing "Integer type validation."
    (is (valid?
          {:type "number"}
          1))
    (is (valid?
          {:type "number"}
          1.0))
    (is (invalid?
          {:type "number"}
          "Not a number"))
    (is (invalid?
          {:type "number"}
          nil))
    (is (valid?
          {:type "number" :multipleOf 2.5}
          5))
    (is (invalid?
          {:type "number" :multipleOf 3.5}
          5))
    (is (valid?
          {:type "number" :maximum 3.0}
          2))
    (is (valid?
          {:type "number" :maximum 3.0}
          3))
    (is (invalid?
          {:type "number" :maximum 3.0}
          4))
    (is (valid?
          {:type "number" :maximum 3.0 :exclusiveMaximum true}
          2.0))
    (is (invalid?
          {:type "number" :maximum 3.0 :exclusiveMaximum true}
          3.0))
    (is (invalid?
          {:type "number" :maximum 3.0 :exclusiveMaximum true}
          4.0))
    (is (valid?
          {:type "number" :maximum 3.0 :exclusiveMaximum false}
          3.0))
    ))

(deftest valid?-string-test
  (testing "String type validation."
    (is (valid?
          {:type "string"}
          "This is a string"))
    (is (invalid?
          {:type "string"}
          1))
    (is (invalid?
          {:type "string"}
          nil))
    (is (valid?
          {:type "string" :minLength 2}
          "ab"))
    (is (invalid?
          {:type "string" :minLength 2}
          "a"))
    (is (valid?
          {:type "string" :maxLength 2}
          "ab"))
    (is (invalid?
          {:type "string" :maxLength 2}
          "abc"))
    ))

(deftest valid?-nil-test
  (testing "Nil type validation."
    (is (valid?
          {:type "nil"}
          nil))
    (is (invalid?
          {:type "nil"}
          0))
    (is (valid?
          {:type "null"}
          nil))
    (is (invalid?
          {:type "null"}
          0))
    ))

(deftest valid?-boolean-test
  (testing "Boolean type validation."
    (is (valid?
          {:type "boolean"}
          true))
    (is (valid?
          {:type "boolean"}
          false))
    (is (invalid?
          {:type "boolean"}
          1))
    (is (invalid?
          {:type "boolean"}
          0))
    ))

(deftest valid?-object-test
  (testing "Object type validation."
    (testing "Object validation."
      (is (valid?
            {:type "object"}
            {}))
      (is (valid?
            {:type "object" :properties {
                                          :nested {:type "object"}
                                          }}
            {}))
      (is (not (valid?
                 {:type       "object"
                  :properties {
                                :nested {:type "object"}
                                }
                  :required   [:nested]}
                 {})))
      (is (valid?
            {:type "object" :properties {
                                          :nested {:type "object"}
                                          }}
            {:nested {}}))
      (is (valid?
            {:type       "object"
             :properties {
                           :nested {:type "object"}
                           }
             :required   [:nested]}
            {:nested {}}))
      (is (invalid?
            {:type       "object"
             :properties {
                           :nested {:type "object"}
                           }
             :required   [:nested]}
            {:nested 0}))
      )
    ))

(deftest valid?-array-test
  (testing "Array type validation."
    (is (valid?
          {:type "array"}
          `()))
    (is (valid?
          {:type "array"}
          `(1 2 3)))
    (is (invalid?
          {:type "array"}
          {}))
    (is (valid?
          {:type "array" :items {:type "integer"}}
          `()))
    (is (valid?
          {:type "array" :items {:type "integer"}}
          `(1 2 3)))
    (is (invalid?
          {:type "array" :items {:type "integer"}}
          `(1 2.0 3)))
    ))