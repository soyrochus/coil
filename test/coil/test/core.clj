(ns coil.test.core
  (:use [coil.spring.context])
  (:use [clojure.test]))

(with-spring "test/applicationContext.xml"
  [record-clj "record-clj"
   record-js  "record-js"]
  (deftest test-spring
    (is (= (:data record-clj) "Clojure Record"))
    (is (= (.getData record-js) "Bean from Javascript"))))

