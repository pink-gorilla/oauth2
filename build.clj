(ns build
  (:require
   [clojure.tools.build.api :as b]
   [org.corfield.build :as bb] ; https://github.com/seancorfield/build-clj
))


(def lib 'org.pinkgorilla/oauth2)
(def version (format "0.2.%s" (b/git-count-revs nil)))

(defn jar "build the JAR" [opts]
  (println "Building the JAR")
  (-> opts
      (assoc :lib lib
             :version version
             :src-pom "pom-template.xml"
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
