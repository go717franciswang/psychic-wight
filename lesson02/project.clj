(defproject lesson02 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [com.jolbox/bonecp "0.8.0.RELEASE"]
                 [java-jdbc/dsl "0.1.0"]
                 [org.clojure/java.jdbc "0.3.2"]
                 [org.xerial/sqlite-jdbc "3.7.2"]]
  :jvm-opts ["-Xmx2G" "-Xms2G"]
  :main lesson02.p1/main
  )

  ;:main [lesson02.p1-sqlite/main])
