(ns shrink.shrink)

(defn- long-div [& ns]
  (long (apply / ns)))

(defmulti shrink (fn [x] 
                   (cond
                     (vector? x) :vector
                     (list? x)   :list
                     (seq? x)    :seq
                     (number? x) :int
                     (string? x) :string
                     (keyword? x) :keyword
                     (symbol? x) :symbol
                     )))

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
                        (concat a b))))]

  (defmethod shrink :vector [xs]
    (concat (remove-chunks xs) (shrink-one xs)))
  
  (defmethod shrink :list [xs]
    (concat (remove-chunks xs) (shrink-one xs)))
  
  (defmethod shrink :seq [xs]
    (concat (remove-chunks xs) (shrink-one xs))))

(defmethod shrink :string [s]
  (map (partial apply str) (shrink (seq s))))

(defmethod shrink :keyword [kw]
  (map keyword (shrink (.substring (str kw) 1))))

(defmethod shrink :symbol [sym]
  (map symbol (shrink (str sym))))

(letfn [(halfs [n] 
          (if (> 1 n) 
            []
            (cons n (halfs (long-div n 2)))))]

  (defmethod shrink :int [x]
    (if (= x 0)
      []
      (let [ns (map #(- x %) (halfs (long-div x 2)))
            negated-ns (map (partial * -1) ns)]
        (cons 0 (interleave ns negated-ns))))))

(defmethod shrink :default [_] [])