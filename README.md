Shrink
---------

An implementation of shrinking, for use in finding the simplest possible failure case in generative test failures. Based off of Haskell's QuickCheck, and Scala's ScalaCheck


by Alex Baranosky

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