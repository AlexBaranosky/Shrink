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

    ;; lists
    '()     '()
    '(0)     '(())
    '(1 2)   '( (1) (2) (0 2) (1 0) (1 1) (1 -1))

    ;; seqs                                            
    (seq "a")     [[]]
    (seq "ab")    [[\a] [\b]]
    (seq "abc")   [[\a] [\b \c]]
                                                         
    ;; chars                                              
    \c []                                                 
                                                          
    ;; strings
    "a"      [""]
    "ab"     ["a" "b"]
    "abc"    ["a" "bc"]
    
    ;; keywords
    :a      [(keyword "")]  ;; TODO - consider if we'd prefer this to be []
    :ab     [:a :b]
    :abc    [:a :bc]

    ;; symbols
    'a      [(symbol "")]   ;; TODO - consider if we'd prefer this to be []
    'ab     ['a 'b]
    'abc    ['a 'bc]
    
    ;; TODO
     #{1 2 3}         []
    {:a 1 :b 2}       []
    (sorted-set #{})  []
    (sorted-map :a 1) []
    ;; (/ 79 77)      [] ;; ratios
    ;; 4.5            [] ;; floats

    ;; can't shrink things it doesn't understand
    (Foo.) []
  )
