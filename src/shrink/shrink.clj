(ns shrink.shrink
  (:use [clojure.core.match :only [match]]))

(defmacro defmatch [name args & cases]
  `(defn ~name ~args
     (match ~args
       ~@cases)))

(defmacro defmethod-match [name dispatch-val args & cases]
  `(defmethod ~name ~dispatch-val ~args
     (match ~args
       ~@cases)))

(defmulti shrink (fn [x] 
                   (if (integer? x) :int)))

(defmatch halfs [n] 
  [(_ :when (partial > 1))]  []
  [_]                        (cons n (halfs (long (/ n 2)))))

(defmethod shrink :default [_] 
  [])

(defmethod-match shrink :int [x]
  [0]  []
  [_]  (let [ns (map #(- x %) (halfs (long (/ x 2))))
             negated-ns (map (partial * -1) ns)]
         (cons 0 (interleave ns negated-ns))))