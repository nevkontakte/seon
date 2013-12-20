(ns ru.nsu.ccfit.g12222.seon.test.path-test
  (:use [ru.nsu.ccfit.g12222.seon.core])
  (:use [ru.nsu.ccfit.g12222.seon.path])
  (:use [ru.nsu.ccfit.g12222.seon.schema])
  (:use [clojure.test]))

(deftest parser-test
  (testing "Have fun."

    (is (= `({:b "abc", :t {:k {:f "gdk"}}, :u {:d 2, :e 3}} "abc" {:k {:f "gdk"}} {:f "gdk"} "gdk" {:d 2, :e 3} 2 3) (parser  `{:b "abc" :u {:d 2 :e 3} :t {:k {:f "gdk"}}} "..")))

    ))


