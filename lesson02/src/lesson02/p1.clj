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

(defn gen-rand-str []
  (str (rand)))

(defn birth-day-attack []
  (let [iterations 10000000
        hash-msg (java.util.HashMap. {})]
    (println "running" iterations "iterations")
    (loop [i 0]
      (if (< i iterations)
        (let [msg (gen-rand-str)
              h (sha256-truncated msg)]
          (if (.containsKey hash-msg h)
            (do 
              (println [(.get hash-msg h) msg])
              true)
            (do 
              (.put hash-msg h msg)
              (recur (inc i)))))
        false))))

(defn main []
  (time
    (do
      (println (sha256-truncated "0.08622480981330949"))
      (println (sha256-truncated "0.3486294390628166"))

      (println (sha256 "0.08622480981330949"))
      (println (sha256 "0.3486294390628166"))

      (while (not (birth-day-attack))
        (println "attempt failed after 10mil tries")))))

