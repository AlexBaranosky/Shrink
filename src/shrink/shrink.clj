(ns shrink.shrink
  (:use [clojure.core.match :only [match]]))

(defmacro defmatch [name args & cases]
  `(defn ~name ~args
     (match ~args
     ~@cases)))

(defmatch halfs [n] 
  [(x :when (partial > 1))]  []
  [_]  (cons n (halfs (long (/ n 2)))))

(defmatch shrink [x]
  [0]  []
  [_]  (let [ns (map #(- x %) (halfs (long (/ x 2))))
             negated-ns (map (partial * -1) ns)]
         (cons 0 (interleave ns negated-ns))))