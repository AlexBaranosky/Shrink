(ns shrink.shrink)

(defn- long-div [& ns]
  (long (apply / ns)))

(defn- int-div [& ns]
  (int (apply / ns)))

(defprotocol Shrinkable
  (shrink [x]))

(letfn [(remove-chunks [xs]
          (cond
            (empty? xs)       []
            (= 1 (count xs))  [[]]
            :else             (let [length (count xs)
                                    n1 (long-div length 2)
                                    n2 (- length n1)
                                    [xs1 xs2] [(take n1 xs) (drop n1 xs)]
                                    xs3 (for [ys1 (remove-chunks xs1)
                                              :when (seq ys1)]
                                          (concat ys1 xs2))
                                    xs4 (for [ys2 (remove-chunks xs2)
                                              :when (seq ys2)]
                                          (concat xs1 ys2))]
                                (list* xs1 xs2 (interleave xs3 xs4)))))

        (shrink-one [zs]
          (cond
            (empty? zs) []
            :else       (let [[x & xs] zs
                              a (for [y (shrink x)]
                                  (cons y xs))
                              b (for [ys (shrink-one xs)]
                                  (cons x ys))]
                          (concat a b))))
       
        (shrink-num [zero div x]
          (letfn [(halfs [n]
                    (if (> 1 n)
                      []
                      (cons n (halfs (div n 2)))))]
            (if (= x zero)
              []
              (let [ns (map #(- x %) (halfs (div x 2)))
                    negated-ns (map (partial * -1) ns)]
                (cons zero (interleave ns negated-ns))))))]

(extend-protocol Shrinkable
  clojure.lang.IPersistentList
  (shrink [xs] (concat (remove-chunks xs) (shrink-one xs)))

  clojure.lang.ISeq
  (shrink [xs] (concat (remove-chunks xs) (shrink-one xs)))

  clojure.lang.IPersistentVector
  (shrink [xs] (map (partial apply vector) (shrink (seq xs))))

  clojure.lang.PersistentTreeSet
  (shrink [xs] (map (partial apply sorted-set) (shrink (seq xs))))

  clojure.lang.PersistentHashSet
  (shrink [xs] (map set (shrink (seq xs))))

  java.lang.Float
  (shrink [f] (shrink-num 0.0 / f))

  java.lang.Double
  (shrink [d] (shrink-num 0.0 / d))

  java.lang.Long
  (shrink [l] (shrink-num 0 long-div l))

  java.lang.Integer
  (shrink [i] (shrink-num 0 int-div i))

  clojure.lang.Ratio
  (shrink [ratio]
    (let [pos-version-of-ratio (if (neg? ratio)
                                 [(* -1 ratio)]
                                 []) ]
      (concat pos-version-of-ratio [(long ratio)] )))

  String
  (shrink [s] (map (partial apply str) (shrink (seq s))))
               
  clojure.lang.Keyword
  (shrink [kw] (map keyword (shrink (.substring (str kw) 1))))

  clojure.lang.Symbol
  (shrink [sym] (map symbol (shrink (str sym))))

  Object
  (shrink [_] [])

  nil
  (shrink [_] [])))