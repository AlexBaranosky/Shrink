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

    ;; ratios
    (/ 1 2)     [0]
    (/ 15 8)    [1]
    (/ -15 8)   [(/ 15 8) -1]
    (/ 99 22)   [4]
    (/ -99 22)  [(/ 99 22) -4]
    
    ;; vectors
    []      []
    [0]     [[]]
    [1 2]   [[1] [2] [0 2] [1 0] [1 1] [1 -1]]
    [1 2]   (has every? vector?)

    ;; lists
    '()      []
    '(0)     ['()]
    '(1 2)   ['(1) '(2) '(0 2) '(1 0) '(1 1) '(1 -1)]
    '(1 2)   (has every? list?)

    ;; sets
    #{}      []
    #{0}     [#{}]
    #{1 2}   [#{1} #{2} #{0 2} #{1 0} #{1} #{1 -1} ]  ;; TODO - decide if we want to de-duplicate these results

    ;; sorted-sets
    (sorted-set)  []
    (sorted-set 0)  [(sorted-set)]
    (sorted-set 1 2)  [(sorted-set 1) (sorted-set 2) (sorted-set 0 2) (sorted-set 1 0) (sorted-set 1) (sorted-set 1 -1)]
    (sorted-set 1 2 3)  (has every? (every-pred sorted? set?))

    ;; seqs                                            
    (seq "a")     [[]]
    (seq "ab")    [[\a] [\b]]
    (seq "abc")   [[\a] [\b \c]]
    (seq "abc")   (has every? seq?)
                                                         
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
    (sorted-map :a 1) []
    {:a 1 :b 2}       []
    ;; 4.5            [] ;; floats

    ;; can't shrink things it doesn't understand
    (Foo.) []
  )
