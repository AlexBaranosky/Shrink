(ns shrink.test.t-shrink
  (:use shrink.shrink
        midje.sweet))

(tabular "shrinking ints"
  (fact (shrink n) => result)
                n     result
                0     []
                1     [0]
                2     [0 1 -1]
                3     [0 2 -2]
                4     [0 2 -2 3 -3])
