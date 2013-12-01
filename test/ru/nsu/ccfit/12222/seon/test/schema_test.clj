(ns ru.nsu.ccfit.12222.seon.test.schema_test
  (:use [ru.nsu.ccfit.12222.seon.core])
  (:use [ru.nsu.ccfit.12222.seon.schema])
  (:use [clojure.test])
  )

(deftest validate-test
  (testing "Exception on unknown exception type."
    (is (thrown-with-msg?
          Exception #"^Unknown schema type"
          (valid? {:type "unknown-type"} {})))
    )
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
    ))