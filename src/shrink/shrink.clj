(ns shrink.shrink)

(defn- long-div [& ns]
  (long (apply / ns)))

(defn- int-div [& ns]
  (int (apply / ns)))

(defprotocol Shrinkable
  (shrink [x]))

(defn- halve [xs]
  (let [length (count xs)
        n1 (long-div length 2)]
    [(take n1 xs) (drop n1 xs)]))

(letfn [(remove-chunks [xs]
          (cond
            (empty? xs)       []
            (= 1 (count xs))  [[]]
            :else             (let [[fst-half scd-half] (halve xs)
                                    xs3 (for [x (remove-chunks fst-half)
                                              :when (seq x)]
                                          (concat x scd-half))
                                    xs4 (for [x (remove-chunks scd-half)
                                              :when (seq x)]
                                          (concat fst-half x))]
                                (list* fst-half scd-half (interleave xs3 xs4)))))

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

  Float
  (shrink [f] (shrink-num 0.0 / f))

  Double
  (shrink [d] (shrink-num 0.0 / d))

  Long
  (shrink [l] (shrink-num 0 long-div l))

  Integer
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