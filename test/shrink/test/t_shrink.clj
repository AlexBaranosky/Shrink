(ns shrink.test.t-shrink
  (:use shrink.shrink
        midje.sweet))

(defrecord Foo [])

(tabular "shrinking"
  (fact (shrink x) => result)

    x     result
    ;; ints
    0     []
    1     [0]
    2     [0 1 -1]
    3     [0 2 -2]
    4     [0 2 -2 3 -3]

    ;; vectors
    []      []
    [0]     [[]]
    [1 2]   [[1] [2] [0 2] [1 0] [1 1] [1 -1]]

  
    ;; can't shrink things it doesn't understand
    (Foo.) []  )
