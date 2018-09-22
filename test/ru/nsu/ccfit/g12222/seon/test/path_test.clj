(ns ru.nsu.ccfit.g12222.seon.test.path-test
  (:use [ru.nsu.ccfit.g12222.seon.path])
  (:use [ru.nsu.ccfit.g12222.seon.core])
  
  (:use [clojure.test]))


(deftest parser-test1
  (testing "Have fun."

    (is (= `({:b "abc", :t {:k {:f "gdk"}}, :u {:d 2, :e 3}} {:k {:f "gdk"}} {:f "gdk"} {:d 2, :e 3})
           (query  `{:b "abc" :u {:d 2 :e 3} :t {:k {:f "gdk"}}} "..")))

    ))

(deftest parser-test2
  (testing "Have fun2."
    (is (= `("frt") (query  `{:u {:f {:t "frt"}} :r {:e "abc", :x "cde"}} "$*.*.t") ))
    ))

(deftest parser-test2
  (testing "Have fun5."
    (is (= `("abc") (query  `{:a {:b "cde" :c "abc"  :d ()} :b {:k 1}} "*.*{abc}") ))
    ))

(deftest parser-test2
  (testing "Have fun6."
    (is (= `("cde") (query  `("abc" "cde") "1") ))
    ))