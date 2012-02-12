(ns shrink.test.t-shrink
  (:use shrink.shrink
        midje.sweet))

(defrecord Foo [])

(tabular "shrinking ints"
  (fact (shrink x) => result)

    x     result
    ;; ints
    0     []
    1     [0]
    2     [0 1 -1]
    3     [0 2 -2]
    4     [0 2 -2 3 -3]
  
    ;; can't shrink things it doesn't understand
    (Foo.) []  )
