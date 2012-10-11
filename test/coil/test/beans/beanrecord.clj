(ns coil.test.beans.beanrecord)

(defrecord TestBean [data x y]) 

(TestBean. "Clojure Record" 20 30)
