(ns modular.oauth2.token.info2
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [tick.core :as t]
   [clojure.pprint :refer [print-table]]
   [modular.oauth2.date :refer [epoch-second->datetime now-datetime add-days]]
   [modular.oauth2.token.store :refer [load-token save-token]]
   [modular.oauth2.token.info :refer [get-token]]))

(defn token-info [kw-name]
  (if-let [token (load-token kw-name)] ; check if we have a token on disk
    (let [access-token (get-token kw-name :access-token)
          expiry-epoch (-> access-token :claims :exp) ; 1650389880
          expiry-date (epoch-second->datetime expiry-epoch)]
      (assoc token :available true
             :name kw-name
             :expiry-epoch expiry-epoch
             :expiry-date expiry-date))
    {:name kw-name
     :available false}))

(defn token-info-table [kw-name-seq]
  (let [token-infos (map token-info kw-name-seq)]
    token-infos))

(defn print-token-table [kw-name-seq]
  (->> kw-name-seq
       (token-info-table)
       (print-table [:name :available #_:expiry-epoch :expiry-date :token-type])))

(defn token-available [kw-name]
  (let [{:keys [available]} (token-info kw-name)]
    available))

(defn add-minutes [dt n]
  (t/>> dt (t/new-duration n :minutes)))  ;  :days

;; todo: now needs to be UTC - for xero this is important, 
;; because xero only has 30 minutes valid auth tokens.

(defn access-token-needs-refresh? [kw-name]
  (let [{:keys [available expiry-date]} (token-info kw-name)
        now (t/now)
        now-p1 (add-minutes now 10)]
    (if available
      (do (info "now (+1 day): " now-p1 "token expiry:" expiry-date)
          (t/> now-p1 expiry-date))
      true)))

(comment

  (load-token :bongo)
  (-> (load-token :shiphero)
      keys)
  
  (load-token :google)

  ; now (now-datetime)
  ; now-p1 (add-days now 1)
  ; (t/> now-p1 expiry-date)
  ; (info "now (+1 day): " now-p1 "token expiry:" expiry-date)

  (token-info :shiphero)
  (token-info :xero)
  (token-info :github)
  (token-info :google)

  (print-token-table [:xero :shiphero #_:google #_:github])

  (token-available :shiphero)
  (token-available :xero)

  (access-token-needs-refresh? :shiphero)
  (access-token-needs-refresh? :xero)

   ; 1800 sec = 1800/60 = 180/6 = 90/3 = 30

  ; Runtime.getRuntime().maxMemory()); 
  (-> (Runtime/getRuntime)
      (.maxMemory)
      (/ 1024) ; kB
      (/ 1024) ; MB
      ;(/ 1024) ; GB
      )

  ; https://stackoverflow.com/questions/4667483/how-is-the-default-max-java-heap-size-determined
  ; java -XX:+PrintFlagsFinal -version | grep HeapSize
  ; java -XX:+PrintFlagsFinal -version | grep -iE 'HeapSize|PermSize|ThreadStackSize'

 

;  
  )