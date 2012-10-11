Coil - support for Clojure & Javascript (Rhino) in Spring projects
==================================================================

Introduction
------------

Coil is a Java library supporting the integration of Clojure and Rhino, the Javascript
implementation for the JVM by Mozilla, with the Spring Framework.

In principle Coil should work both ways: allow the use of Clojure & Javscript source files as
"beans" in Spring applications using Spring's Dependency Injection infrastructure as well as allow
the integration of Spring from within stand-alone Clojure and Javascript applications.

The library should be considered to be very much an alpha release and to be extremely hazardous for
your health, sanity and reputation. However, basic usage of both Clojure and Javascript in Spring
works and a minimal Clojure library (coil.spring.context) supporting the usage of Spring
Application-Context from within Clojure applications is usable (see the unit test).

Future versions should support re-loadable beans (allowing changes in the script source files without
needing to restart the Spring application), annotation support (allowing the usage of Clojure and
Javascript without XML configuration hell) and support a more elaborate Clojure library (as well as,
where reasonable, a Javascript equivalent).


Dependency injection: configuration by XML
------------------------------------------

In the Spring configuration file(s), i.e. applicationContext.xml and such, the Coil name space &
schema declarations need to be incorporated, like the following example:

    <?xml version="1.0" encoding="UTF-8"?>
       <beans xmlns="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:coil="http://skitr.com/schema/coil"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd 
                               http://skitr.com/schema/coil schemas/coil.xsd"
    default-autowire="byName">
    
    
Within this file you can define one or multiple beans by using the "clojure" or "javascript" tags,
referring to the corresponding source file (using a Spring resource locator)
    
    
    <coil:clojure id="record-clj" script-source="classpath:coil/test/beans/beanrecord.clj" />

    <coil:javascript id="record-js" script-source="classpath:coil/test/beans/beanrecord.js" />


Instantiating beans
-------------------

The last expression within the source file is considered to represent the "bean". In case of a
Clojure script it can be either a Class or Clojure value. In case of the former it will be
instantiated using the default constructor.

In case of a Javascript file, the expression must be an object instance. In order for it to be
usefull it means that the last expression really should be an implemenation of an existing Java
interface. In the test the following example is used:


```javascript

    var BeanRecord = Packages.coil.test.beans.BeanRecord; ;; interface within test package

    var initImpl = function(data, x, y){
    
      return  {
	
           getData: function(){
               return data;
           },
           getX: function(){
               return x;
           },
           getY: function(){
               return y;
           }
      };
    };
    

    var beanrecord = new BeanRecord(initImpl("Bean from Javascript", 10, 10.001));

    //return from script
    beanrecord;
    
```


Usage of Spring from within Clojure
------------------------------------

The library implements the Clojure name space `clojure.spring.context`. Please refer to the component
documentation for advanced usage. Typical usage would focus on the following functions:


*`(init-spring [path])`*
    
Initialize global Spring context from class path loading Spring's XML bean configuration

*`(get-bean [name])`*

Obtain bean from Spring context

*`(close-spring)`*

Close & clean-up the Spring application context.

```clojure

    (do 
        (init-spring "/test/applicationContext.xml")
        (let [bean1 (get-bean "bean-name1")
              bean2 (get-bean "bean-name2")]        
            (.process bean1 (.getData bean2))
            (close-spring)))

```

A utility to help cleanup the process of obtaining multiple beans from a Spring context is the function

*`(with-spring-context [binding-forms & code])`*


which creates local bindings for beans obtained from Spring Application context. The bindings are in the
  form of [ref bean-name ..]


As these these functions create a reference to a Spring application context in the dynamic Var
`*well*` within the `coil.spring.context` name space, in order to safeguard safe use they could be
used within a `binding` context. The following function can be used in order to facilitate the safe
creation, usage & cleanup of a Spring application context:

(with-spring [path binding-forms & code)

This creates local bindings for beans obtained from Spring Application context. The application context can be a path-or-resource string/Url/File/InputStream.  
The bindings are in the form of [ref bean-name ..]

```clojure

    (with-spring "/test/applicationContext.xml"
        [bean "bean-counter"
         record "bean-record"
         recordjs "bean-record-js"]    
      (.process bean1 (.getData bean2)))
      
```


License
-------

> Copyright (C) 2012 Iwan van der Kleijn
> All rights reserved.

> Redistribution and use in source and binary forms, with or without
> modification, are permitted provided that the following conditions are met:
>    * Redistributions of source code must retain the above copyright
>      notice, this list of conditions and the following disclaimer.
>    * Redistributions in binary form must reproduce the above copyright
>      notice, this list of conditions and the following disclaimer in the
>      documentation and/or other materials provided with the distribution.
>    * Neither the name of the <organization> nor the
>      names of its contributors may be used to endorse or promote products
>      derived from this software without specific prior written permission.

> THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
> ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
> WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
> DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
> DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
> (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
> LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
> ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
> (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
> SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
