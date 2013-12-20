(ns ru.nsu.ccfit.g12222.seon.test.core_test
  (:require [clojure.test :refer :all]
            [ru.nsu.ccfit.g12222.seon.core :refer :all]))

(deftest seon-atom-test
  (testing "SEON atom validation predicate."
    (is (seon-atom? 1))
    (is (seon-atom? 1.0))
    (is (seon-atom? false))
    (is (seon-atom? true))
    (is (seon-atom? "abc"))
    (is (not (seon-atom? `("abc"))))
    ))


(deftest seon-list-test
  (testing "SEON list validation predicate."
    (is (seon-list? `()))
    (is (seon-list? `(1)))
    (is (seon-list? `(1 2)))
    (is (seon-list? `(1 2 "abc")))
    (is (not (seon-list? 1)))
    (is (not (seon-list? {})))
    (is (not (seon-list? :a)))
    ))

(deftest seon-map-test
  (testing "SEON map validation predicate."
    (is (seon-map? {}))
    (is (seon-map? {:a "b"}))
    (is (seon-map? {:a "b" :c "d" :num 1.0}))
    (is (not (seon-map? 1)))
    (is (not (seon-map? `())))
    ))

(deftest seon-predicate-test
  (testing "SEON validation predicate on primitive cases."
    (is (seon? 1))
    (is (seon? 1.0))
    (is (seon? true))
    (is (seon? "abc"))
    (is (seon? `()))
    (is (seon? `(1 2)))
    (is (seon? {:1 1 :2 2}))
    (is (not (seon? #{})))
    (is (not (seon? [])))
    (is (not (seon? list))) ; n.b.: "list" here is a function ref
    )
  (testing "Complex SEON expressions validation."
    (is (seon? `((1 2) (3 4))))
    (is (seon? `((1 2) {:a 3 :b 4})))
    (is (seon? `{:a {:b 1} :c {:d 2 :e 3}}))
    (is (seon? `{:a (1 2) :b {:c 3 :d 4}}))
    (is (not (seon? `{:a (1 2) :b {:c 3 :d []}}))) ; [] (aka array) is not a valid SEON structure
    (is (not (seon? `{:a (#{} 2) :b {:c 3 :d 4}}))) ; #{} (aka set) is not a valid SEON structure
    ))

(deftest serialize-test
  (testing "SEON serialization into string."
    (is (= (serialize 1) "1"))
    (is (= (serialize "1") "\"1\""))
    (is (= (serialize false) "false"))
    (is (= (serialize `()) "()"))
    (is (= (serialize `(1 2 (3 4))) "(1 2 (3 4))"))
    (is (= (serialize {:a "b" :c 2}) "{:a \"b\", :c 2}"))
    ))

(deftest unserialize-test
  (testing "Parsing SEON from string."
    (is (= (unserialize "1") 1))
    (is (= (unserialize "\"1\"") "1"))
    (is (= (unserialize "false") false))
    (is (= (unserialize "()") `()))
    (is (= (unserialize "(1 2 (3 4))") `(1 2 (3 4))))
    (is (= (unserialize "{:a \"b\", :c 2}") {:a "b" :c 2}))
    ))
