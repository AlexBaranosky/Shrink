(ns shrink.test.t_core
  (:use shrink.core
        midje.sweet))

(tabular "shrinking ints"
  (fact
    (shrink n) => result)
  n         result
  0         []
;    1         []
)