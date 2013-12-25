(ns ru.nsu.ccfit.g12222.seon.test.sax-schema-test
  (:use clojure.test)
  (:use ru.nsu.ccfit.g12222.seon.sax-schema))

(deftest sax-valid?-atom-test
  (testing "Validation of atomic expressions."
    (testing "Numbers."
      (is (sax-valid?
            {:type "integer"}
            "1"))
      (is (sax-invalid?
            {:type "integer"}
            "1.0"))
      (is (sax-valid?
            {:type "integer" :multipleOf 2}
            "4"))
      (is (sax-invalid?
            {:type "integer" :multipleOf 3}
            "4"))
      (is (sax-valid?
            {:type "integer" :maximum 3}
            "2"))
      (is (sax-invalid?
            {:type "integer" :maximum 3}
            "4"))
      (is (sax-valid?
            {:type "number"}
            "1.0"))
      (is (sax-invalid?
            {:type "number"}
            "\"Not a number\""))
      (is (sax-valid?
            {:type "number" :multipleOf 2.5}
            "5"))
      (is (sax-valid?
            {:type "number" :maximum 3.0}
            "3"))
      (is (sax-invalid?
            {:type "number" :maximum 3.0}
            "4"))
      )
    ))

(deftest sax-valid?-list-test
  (testing "List validation."
    (is (sax-valid?
          {:type "array"}
          "()"))
    (is (sax-valid?
          {:type "array"}
          "(1 2 3)"))
    (is (sax-invalid?
          {:type "array"}
          "1"))
    (is (sax-valid?
          {:type "array" :items {:type "integer"}}
          "()"))
    (is (sax-valid?
          {:type "array" :items {:type "integer"}}
          "(1 2 3)"))
    (is (sax-invalid?
          {:type "array" :items {:type "integer"}}
          "(1 2.0 3)"))
    )
  (testing "Array length validation."
    (is (sax-invalid?
          {:type "array" :minItems 2}
          "(1)"))
    (is (sax-invalid?
          {:type "array" :minItems 2}
          "((1 2 3))"))
    (is (sax-valid?
          {:type "array" :minItems 2}
          "(1 2)"))
    (is (sax-valid?
          {:type "array" :minItems 2}
          "(1 2 3)"))
    (is (sax-valid?
          {:type "array" :maxItems 2}
          "(1)"))
    (is (sax-valid?
          {:type "array" :maxItems 2}
          "(1 2)"))
    (is (sax-valid?
          {:type "array" :maxItems 2}
          "(1 (2 3 4))"))
    (is (sax-invalid?
          {:type "array" :maxItems 2}
          "(1 2 3)"))
    )
  (testing "Array unique items."
    (is (sax-valid?
          {:type "array"}
          "(1 2 3 1 2 3)"))
    (is (sax-valid?
          {:type "array" :uniqueItems false}
          "(1 2 3 1 2 3)"))
    (is (sax-invalid?
          {:type "array" :uniqueItems true}
          "(1 2 3 1 2 3)"))
    (is (sax-valid?
          {:type "array" :uniqueItems true}
          "(1 2 3 4 5 6)"))
    )
  )