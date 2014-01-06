(ns lesson02.p1
  (:import [java.security MessageDigest]))

(defn sha256 [message]
  (let [md (MessageDigest/getInstance "SHA-256")
        _ (. md update (.getBytes message))
        h (.digest md)]
    (apply str (map #(format "%02x" (bit-and % 0xff)) h))))

(defn sha256-truncated [message]
  (let [md (MessageDigest/getInstance "SHA-256")
        _ (. md update (.getBytes message))
        h (map #(bit-and % 0xff) (take-last 7 (.digest md)))
        first-byte (mod (first h) 4)]
    (apply str (map #(format "%02x" %) (conj (rest h) first-byte)))))

(println (sha256-truncated "abc"))
(println (sha256 "abc"))

(defn gen-rand-str []
  (str (rand)))

(defn birth-day-attack []
  (let [iterations (int (* 1.2 (Math/pow 2 25)))
        hash-msg (java.util.HashMap. {})]
    (println "running" iterations "iterations")
    (loop [i 0]
      (when (zero? (mod i 100000)) (println i))
      (when (< i iterations)
        (let [msg (gen-rand-str)
              h (sha256-truncated msg)]
          (if (.containsKey hash-msg h)
            [(.get hash-msg h) msg]
            (do 
              (.put hash-msg h msg)
              (recur (inc i)))))))))

(defn main []
  (time
    (birth-day-attack)))

