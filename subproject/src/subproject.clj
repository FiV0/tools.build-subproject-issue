(ns subproject
  (:require [cheshire.core :as json])
  (:gen-class))

(defn -main [& args]
  (println "Hello from Clojure!!! " (json/encode {:foo :bar})))
