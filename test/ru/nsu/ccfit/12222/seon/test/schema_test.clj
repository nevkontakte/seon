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
                 {:type "object" :properties {
                                               :nested {:type "object" :required true}
                                               }}
                 {})))
      (is (valid?
            {:type "object" :properties {
                                          :nested {:type "object"}
                                          }}
            {:nested {}}))
      (is (valid?
            {:type "object" :properties {
                                          :nested {:type "object" :required true}
                                          }}
            {:nested {}}))
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