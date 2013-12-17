(ns lesson01.q07)

(def m "attack at dawn")
(def m-int (map int m))

(def c "6c73d5240a948c86981bc294814d")
(def c-int (map #(read-string (str "0x" (apply str %))) (partition-all 2 c)))

(def k-int (map bit-xor c-int m-int))

(def m2 "attack at dusk")
(apply str (map #(format "%02x" %) (map bit-xor k-int (map int m2))))
