(ns coil.spring.context
  (:import [org.springframework.context.support AbstractXmlApplicationContext FileSystemXmlApplicationContext ClassPathXmlApplicationContext]
           [java.io File]) 
  (:require [coil.utils :as utils]))


(def ^AbstractXmlApplicationContext ^:dynamic *well*
  "Global binding for Spring context"
  nil)

(defn ^AbstractXmlApplicationContext get-spring [path]
  "Instantiate Spring context from classpath loading Spring's XML bean configuration"
  (let [f (File. path)
        exists (.exists f)]
    (if exists
      (FileSystemXmlApplicationContext. (str "file:" path))
      (ClassPathXmlApplicationContext. path))))


(defn ^AbstractXmlApplicationContext init-spring [path]
    "Initialize global Spring context from classpath loading Spring's XML bean configuration"
    (def *well* (get-spring path))
    *well*)

(defn get-bean [name]
  "Obtain Bean from Spring context"
  (.getBean *well* name))
 
(defn close-spring []
  "Close this application context, destroying all beans in its bean factory."
  (do
    (.close *well*)
    (def *well* nil)))

(defn close-spring-on-exit []
  "Register a shutdown hook with the JVM runtime, closing this context on JVM shutdown unless it has already been closed at that time"
  (.registerShutdownHook *well*))


(defmacro with-spring-context [binding-forms & code]
  "Create local bindings for beans obtained from Spring Application context. The bindings are in the form of
  [ref bean-name ..]"
  (let [conv-forms (vec (utils/map-in-pairs (fn [e] (list (:x e) `(get-bean ~(:y e))))
                                            binding-forms true))]
    `(let ~conv-forms
       ~@code)))

(defmacro with-spring [path binding-forms & code]
  "Create local bindings for beans obtained from Spring Application context

  The applicaction context can be a path-or-resource string/Url/File/InputStream. The bindings are in the form of
  [ref bean-name ..]"
  (do
    (intern 'coil.spring.context '*well*)
    `(binding [*well* (get-spring ~path)]
       (do 
         (with-spring-context ~binding-forms ~@code)
         (close-spring)))))


(defn run-test []
  (with-spring "test/applicationContext.xml"    
  [record-clj "record-clj"
   record-js  "record-js"]
    (do 
      (def bean-clj record-clj)
      (def bean-ks record-js))))


;;(init-spring  "/test/applicationContext.xml")
