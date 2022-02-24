(ns build
  (:require [clojure.tools.build.api :as b]))

(def version "0.1.0")
(def class-dir "target/classes")
(def subprojects #{"subproject"})

(defn- check-subproject [subproject]
  (when-not (subprojects subproject)
    (throw (IllegalArgumentException. (str "No such subproject " subproject)))))

(defn uber-file [subproject]
  (format "%s/target/%s-%s-standalone.jar" subproject subproject version))

(defn clean-subproject [subproject]
  (binding [b/*project-root* subproject]
    (b/delete {:path "target"})))

(defn clean [{:keys [subproject]}]
  (if subproject
    (clean-subproject (str subproject))
    (doseq [subproject subprojects]
      (clean-subproject subproject))))

(defn create-basis [subproject]
  (binding [b/*project-root* subproject]
    (b/create-basis {:project "deps.edn"})))

(defn- uber-subproject [subproject]
  (check-subproject subproject)
  (clean-subproject subproject)
  (let [basis (create-basis subproject)
        src (str subproject "/src")
        class-dir (str subproject "/" class-dir)]
    (b/copy-dir {:src-dirs [src]
                 :target-dir class-dir})
    (b/compile-clj {:basis basis
                    :src-dirs [src]
                    :class-dir class-dir})
    (b/uber {:class-dir class-dir
             :uber-file (uber-file subproject)
             :basis basis
             :main (symbol subproject)})))

(defn uber [{:keys [subproject]}]
  (if subproject
    (uber-subproject (str subproject))
    (doseq [subproject subprojects]
      (uber-subproject subproject))))
