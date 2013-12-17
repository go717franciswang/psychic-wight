(ns lesson01.p02.clj)

(def raw-output
  "output #1: 210205973
  output #2: 22795300
  output #3: 58776750
  output #4: 121262470
  output #5: 264731963
  output #6: 140842553
  output #7: 242590528
  output #8: 195244728
  output #9: 86752752")

(def output
  (vec
    (map (fn [s]
           (read-string (second
             (clojure.string/split s #":\s")) ))
         (clojure.string/split raw-output #"\n"))))

(def p 295075153)

(defn next-x [x] 
  (mod (+ (* x 2) 5) p))

(defn next-y [y]
  (mod (+ (* y 3) 7) p))

(defn next-z [x y]
  (bit-xor x y))

(defn validate [x y i]
  (if (= i 9)
    (do
      (println x y (next-z (next-x x) (next-y y)))
      true)
    (let [x (next-x x)
          y (next-y y)]
      (if (= (next-z x y) (get output i))
        (recur x y (inc i))
        false))))

; there must be a better method but it produces the solution under a minute
(loop [x0 0]
  (let [y0 (bit-xor x0 (first output))]
    (if (validate x0 y0 1)
      true
      (recur (inc x0)))))
