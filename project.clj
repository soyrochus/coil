(defproject coil "1.0.0-SNAPSHOT"
  :description "Spring with a Clojure"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.mozilla/rhino "1.7R4"]
                 [org.clojure/tools.logging "0.2.4"]
                 [org.springframework/spring-core "3.1.0.RELEASE"]
                 [org.springframework/spring-beans "3.1.0.RELEASE"]
                 [org.springframework/spring-context "3.1.0.RELEASE"]
                 [commons-logging/commons-logging "1.1.1"]
                 [log4j/log4j "1.2.16"]]
  ;; :repositories   {"SpringSource Bundle" "http://repository.springsource.com/maven/bundles/release"
  ;; "SpringSource External" "http://repository.springsource.com/maven/bundles/external"}
  :java-source-paths  ["src/java" "test/java"]
  ;; :jvm-opts ["-Xms1024m" "-Xmx2048m" "-agentlib:jdwp=transport=dt_socket,address=9000,server=y,suspend=n"]     
  :aot [coil.spring.scripting.namespacehandler]
  ;; [coil.scriptfactory 
  ;;  coil.definitionparser
   ;;coil.namespacehandler   
  :main coil.core)



