Shrink
---------

An implementation of shrinking, for use in finding the simplest possible failure case in generative test failures. Based off of Haskell's QuickCheck, and Scala's ScalaCheck

Say you have found a test case fails for the inputted value of 4. The shrink function will return a sequence of integers ([0 2 -2 3 -3]) that you can try as inputs to that failing test case.  The aim being to identify the smallest possible failure case, which you will then report to the user.

Exs.
  (shrink 4) => [0 2 -2 3 -3]
  (shrink [1 2]) => [[1] [2] [0 2] [1 0] [1 1] [1 -1]]
  (shrink "abc") => ["a" "bc"] 

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