(ns coil.spring.scripting.namespacehandler
  (:import [org.springframework.beans.factory.xml NamespaceHandlerSupport BeanDefinitionParser]
            [coil.spring.scripting ClojureBeanDefinitionParser RhinoBeanDefinitionParser])
  (:require [coil.utils :as utils])
  (:gen-class
	   :name coil.spring.scripting.CoilNamespaceHandler
	   :extends org.springframework.beans.factory.xml.NamespaceHandlerSupport 
	   :init initialize))

(defn -initialize []
  [[] nil])

(defn- regParser [obj element bean-parser]
  (utils/call-method NamespaceHandlerSupport :registerBeanDefinitionParser
                     [String BeanDefinitionParser] obj element bean-parser))

(defn -init [this]
  (do
    (regParser this "clojure"  (ClojureBeanDefinitionParser.))
    (regParser this "javascript"  (RhinoBeanDefinitionParser.))))


;;(compile 'coil.namespacehandler)

;;  (defn -this-init [this]
;;   (.regBeanDefParser this "clojure" ^BeanDefinitionParser (ClojureBeanDefinitionParser.)))

;; (defn -regBeanDefParser [this ^String element ^BeanDefinitionParser parser]
;;   (.registerBeanDefinitionParser element parser))
 
;; (defn -regBeanDefParser [this ^String element ^BeanDefinitionParser parser]
;;   (.registerBeanDefinitionParser element parser))


;; package coil;
;; import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

;; /**
;;  * Namespace handler for custom scripting.
;;  * 
;;  * @author jason
;;  */
;; public class CoilNamespaceHandler extends NamespaceHandlerSupport {
;;     public void init() {
;;         registerBeanDefinitionParser("clojure", new ClojureBeanDefinitionParser());
;;         registerBeanDefinitionParser("javascript", new RhinoBeanDefinitionParser());
;;     }
;; }
