(ns lesson02.p1-sqlite
  (:require [lesson02.p1 :as p1]
            [java-jdbc.ddl :as ddl]
            [clojure.java.jdbc :as j])
  (:import com.jolbox.bonecp.BoneCPDataSource))

(def db-spec
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "db/database.db"
   :init-pool-size 4
   :max-pool-size 20
   :partitions 2})

(defn pooled-datasource [db-spec]
  (let [{:keys [classname subprotocol subname user password
                init-pool-size max-pool-size idle-time partitions]} db-spec
        cpds (doto (BoneCPDataSource.)
                   (.setDriverClass classname)
                   (.setJdbcUrl (str "jdbc:" subprotocol ":" subname))
                   (.setUsername user)
                   (.setPassword password)
                   (.setMinConnectionsPerPartition (inc (int (/ init-pool-size partitions))))
                   (.setMaxConnectionsPerPartition (inc (int (/ max-pool-size partitions))))
                   (.setPartitionCount partitions)
                   (.setStatisticsEnabled true)
                   (.setIdleMaxAgeInMinutes (or idle-time 60)))]
    {:datasource cpds}))

(def pooled-db-spec (pooled-datasource db-spec))

(defn create-table []
  (try (j/db-do-commands 
         pooled-db-spec false
         "create table if not exists hash_msg
         (hash text primary key, msg text);")
       (catch Exception e (println e))))

(defn insert [records]
  (try (apply j/insert! pooled-db-spec :hash_msg (conj (seq records) [:hash :msg]))
       (catch Exception e (println e))))

(defn query [hashes]
  (try (let [item-count (count hashes)
             placeholders (apply str "?" (repeat (dec item-count) ",?"))
             query (str "select * from hash_msg where hash in (" placeholders ")")]
         (j/query pooled-db-spec (conj (seq hashes) query)))
       (catch Exception e (println "error with select" e))))

(defn batch [n i]
  (println "batch" i)
  (let [hash-msg (loop [i 0
                        hash-msg (transient {})]
                   (if (< i n)
                     (let [msg (p1/gen-rand-str)
                           h (p1/sha256-truncated msg)]
                       (recur (inc i) 
                              (assoc! hash-msg h msg)))
                     (persistent! hash-msg)))
        hashes (keys hash-msg)
        found-records (query hashes)]
    (if (empty? found-records)
      (do
        (insert (seq hash-msg))
        false)
      (do (println "match found!")
          (let [record (first found-records)
                h (:hash record)
                m1 (:msg record)
                m2 (get hash-msg h)]
            (println "hash:" h)
            (println "m1:" m1)
            (println "m2:" m2)
            true)))))
                
(defn main []
  (create-table)
  (loop [i 1]
    (when (< i 480000)
      (let [found (batch 100 i)]
        (if found
          (println "done")
          (do
            (recur (inc i))))))))

(main)
