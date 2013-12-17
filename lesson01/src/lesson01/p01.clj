(ns lesson01.p01)

(def ciphers
  (vec 
    (map #(vec (hex2ints (second %)))
      (partition-all 3
        (clojure.string/split (slurp (clojure.java.io/resource "ciphers.txt")) #"\n")))))

(defn hex2ints [h]
  (map #(read-string (apply str "0x" %))(partition-all 2 h)))

; cipher-i xor cipher-j == msg-i xor msg-j
; since msg can only come from space, a-z, and A-Z
; compute map xor-result to valid pairs that produce this result
; xor each cipher with each other to find valid pairs for each cipher in the following order
; 1st xor last
; 2nd xor last, 1st xor 2nd
; 3rd xor last, 1st xor 3rd, 2nd xor 3rd
; 4th xor last, 1st xor 4th, 2nd xor 4th, 3rd xor 4th
; ...
; until we determine a single valid char for the last cipher

(def valid-chars 
  (concat 
    [(int \space) (int \:) (int \.)]
    (range (int \a) (inc (int \z)))
    (range (int \A) (inc (int \Z)))))

(def xor2char-pairs
  (reduce (fn [m [k v]]
            (update-in m [k] conj v)) 
          {}
          (for [i valid-chars 
                j valid-chars] 
            [(bit-xor i j) [i j]])))

#_(apply str
  (for [pos (range (count (get ciphers 10)))]
    (loop [i 0
           valid-sets #{}]
      (let [cipher (get-in ciphers [i pos])
            target (get-in ciphers [10 pos])
            new-sets (set (map first (get xor2char-pairs (bit-xor cipher target) [])))
            filtered-sets (if (empty? valid-sets)
                            new-sets
                            (clojure.set/intersection new-sets valid-sets))]
        (if (or (= 1 (count filtered-sets)) (= i 9))
          (char (first filtered-sets))
          (recur (inc i) filtered-sets))))))

(apply str
  (for [pos (range (count (get ciphers 10)))]
    (loop [i 0
           valid-sets {}]
      (if (= i 9)
        (char (first (last (sort-by second valid-sets))))
        (let [cipher (get-in ciphers [i pos])
              target (get-in ciphers [10 pos])
              new-sets (map first (get xor2char-pairs (bit-xor cipher target)))
              filtered-sets (reduce (fn [m k]
                                      (update-in m [k] (fnil inc 0)))
                                      valid-sets
                                      new-sets)]
          (recur (inc i) filtered-sets))))))

