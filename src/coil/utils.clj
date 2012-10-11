(ns coil.utils)

(defn call-method
  [klass method-name params obj & args]
  (-> klass (.getDeclaredMethod (name method-name)
                                (into-array Class params))
      (doto (.setAccessible true))
      (.invoke obj (into-array Object args))))


(defn seperate [pred coll]
  "Use filter to seperate collection in two halves, both a lazy sequence of the items in coll based on result of the predicate function.
   Pred must be free of side-effects."

  {true (filter pred coll) false (filter #(not (pred %)) coll)})

(defrecord Pair [x y])

(defn Pairs [seq]
  "Convert sequence to sequence of pairs (halving the original sequence in length)"
  (lazy-seq
   (if (sequential? seq)
     (cons (Pair. (first seq) (second seq)) (Pairs (nthnext seq 2)))
     nil)))

(defn pairs [seq]
  "Convert sequence to sequence of pairs (halving the original sequence in length)"
  (lazy-seq
   (if (sequential? seq)
     (cons (list (first seq) (second seq)) (pairs (nthnext seq 2)))
     nil)))

        
(defn unpair [seq]
  "Combine sequence of pairs created with pairs or Pairs"
  (mapcat (fn [e]
            (cond
             (sequential? e)    e
             (instance? Pair e) [(:x e) (:y e)]
             :else [e])) seq))

(defn map-in-pairs
  "Map over collection passing each pair to the function. If Pair? is set to true, the pair are instances of Pair, otherwise a
   vector (default"
  ([f col] (map-in-pairs f col false))
  ([f col Pair?]
     (if Pair?
       (unpair (map f (Pairs col)))
       (unpair (map f (pairs col))))))

(defn flatten-1 [seq]
  "Flatten sequence one level deep i.e.
   [1 [2 [3 4] 5] 6] -> [1 2 [3 4] 5 6]"
  (mapcat (fn [e] (if (sequential? e)
                    e
                    [e])) seq))

(defn load-resource
  "Load resource from classpath, using contextclassloader from current thread"
  [name]
  (let [rsc-name name
        thr (Thread/currentThread)
        ldr (.getContextClassLoader thr)]
    (.getResourceAsStream ldr rsc-name)))
