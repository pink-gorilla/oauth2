(ns build
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
   [deps-deploy.deps-deploy :as dd]))


(def lib 'org.pinkgorilla/oauth2)
(def version (format "0.0.%s" (b/git-count-revs nil)))

(defn jar "build the JAR" [opts]
  (println "Building the JAR")
  #_(spit (doto (fs/file "resources/META-INF/pink-gorilla/webly3/meta.edn")
          (-> fs/parent fs/create-dirs)) {:module-name "rest"
                                          :version version})
  (-> opts
      (assoc :lib lib
             :version version
             :src-pom "template/pom.xml"
             :transitive true)
      ;(bb/run-tests)
      ;(bb/clean)
      (bb/jar)))


(defn deploy "Deploy the JAR to Clojars." [opts]
  (println "Deploying to Clojars.")
  (-> opts
      (assoc :lib lib 
             :version version)
      (bb/deploy)))
