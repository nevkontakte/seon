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