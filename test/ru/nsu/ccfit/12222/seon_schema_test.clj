(ns ru.nsu.ccfit.12222.seon-schema-test
  (:use [ru.nsu.ccfit.12222.seon.core])
  (:use [ru.nsu.ccfit.12222.seon.schema])
  (:use [clojure.test])
  )

(deftest validate-test
  (testing "Exception on unknown exception type."
    (is (thrown-with-msg?
          Exception #"^Unknown schema type"
          (validate {:type "unknown-type"} {})))
    ))