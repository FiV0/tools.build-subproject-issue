(ns build
  (:require [clojure.tools.build.api :as b]))

(def version "0.1.0")
(def class-dir "target/classes")
(def subprojects #{"subproject"})

(defn- check-subproject [subproject]
  (when-not (subprojects subproject)
    (throw (IllegalArgumentException. (str "No such subproject " subproject)))))

(defn uber-file [subproject]
  (format "target/%s-%s-standalone.jar" subproject version))

(defn clean-subproject [subproject]
  (binding [b/*project-root* subproject]
    (b/delete {:path "target"})))

(defn clean [{:keys [subproject]}]
  (if subproject
    (clean-subproject (str subproject))
    (doseq [subproject subprojects]
      (clean-subproject subproject))))

(defn- uber-subproject [subproject]
  (check-subproject subproject)
  (clean-subproject subproject)
  (binding [b/*project-root* subproject]
    (let [basis (b/create-basis {:project "deps.edn"})]
      (b/copy-dir {:src-dirs ["src"]
                   :target-dir class-dir})
      (b/compile-clj {:basis basis
                      :src-dirs ["src"]
                      :class-dir class-dir})
      (b/uber {:class-dir class-dir
               :uber-file (uber-file subproject)
               :basis basis
               :main (symbol subproject)}))))

(defn uber [{:keys [subproject]}]
  (if subproject
    (uber-subproject (str subproject))
    (doseq [subproject subprojects]
      (uber-subproject subproject))))
