(ns ru.nsu.ccfit.g12222.seon.test.sax-test
  (:use clojure.test)
  (:use ru.nsu.ccfit.g12222.seon.core))

(defn- handler-tag
  [tag state]
  (cons tag state))

(defn- handler-value
  [tag state value]
  (concat (list value tag) state))

(deftest seon-sax-test
  (testing "Primitive SAX parser test."
    (is (= :state (seon-sax :state "()")))
    (is (thrown-with-msg?
          RuntimeException #"^EOF while reading$"
          (= :state (seon-sax :state ""))))
    )
  (testing "Binding"
    (binding [
               *seon-list-open* (partial handler-tag "(")
               *seon-list-close* (partial handler-tag ")")
               *seon-map-open* (partial handler-tag "{")
               *seon-map-close* (partial handler-tag "}")
               ]
      (is (=
            `("(" "{" "}" "{" "}" ")")
            (reverse (seon-sax `() "({}{})"))))
      (is (=
            `("(" "{" "}" "{" "}" ")")
            (reverse (seon-sax `() "({:a \"val1\", :b \"val2\"}, {:b \"val3\", :c \"val4\"})"))))
      (binding [
                 *seon-map-key* (partial handler-value :key!)
                 *seon-atom* (partial handler-value :atom!)
                 ]
        (is (=
              `("("
                "{" :key! :a :atom! "val1" :key! :b :atom! "val2" "}"
                "{" :key! :b :atom! "val3" :key! :c :atom! "val4" "}"
                :atom! nil :atom! false :atom! true :atom! 2 :atom! 3.0
                ")")
              (reverse (seon-sax `() "({:a \"val1\", :b \"val2\"}, {:b \"val3\", :c \"val4\"} nil false true 2 3.0)"))))
        )
      ))
  )