Shrink
---------

An implementation of shrinking, for use in finding the simplest possible failure case in generative test failures. Based off of Haskell's QuickCheck, and Scala's ScalaCheck

Usage
---------------

Leiningen:

```clojure
   [shrink "0.1.0"]
```

Example
-----------

```clojure
(shrink [1 2]) 
  ;=> [[1] [2] [0 2] [1 0] [1 1] [1 -1]] 
```

Versions
------------

Tested against Clojure versions 1.2.0, 1.2.1, 1.3.0, and 1.4.0-beta1