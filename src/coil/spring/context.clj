(ns coil.spring.context
  (:import [org.springframework.context.support AbstractXmlApplicationContext FileSystemXmlApplicationContext ClassPathXmlApplicationContext]
           [java.io File]) 
  (:require [coil.utils :as utils]))


(def ^AbstractXmlApplicationContext ^:dynamic *well*
  "Global binding for Spring context")


(defn ^AbstractXmlApplicationContext get-spring [path]
  "Instantiate Spring context from classpath loading Spring's XML bean configuration"
  (let [f (File. path)
        exists (.exists f)]
    (if exists
      (FileSystemXmlApplicationContext. (str "file:" path))
      (ClassPathXmlApplicationContext. path))))


(defn ^AbstractXmlApplicationContext init-spring [path]
    "Initialize global Spring context from classpath loading Spring's XML bean configuration"
    (set *well* (get-spring path))
    *well*)

(defn get-bean [name]
  "Obtain Bean from Spring context"
  (.getBean *well* name))
 
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
       (with-spring-context ~binding-forms ~@code))))


(defn run-test []
  (with-spring "applicationContext.xml"
    [bean "bean-counter"
     record "bean-record"
     recordjs "bean-record-js"]    
    (do 
      (def databean bean)
      (def datarecord record)
      (def jsbean recordjs))))


;;(type (.getData (run-test)))
;;(.getData jsbean)
;;(.data datarecord)