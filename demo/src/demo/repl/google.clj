(ns demo.repl.google
  (:require
   [promesa.core :as p]
   [modular.system]
   [token.oauth2.request :refer [GET]]
   [token.oauth2.store :refer [get-auth-header]]))

(def ts (modular.system/system :token-store))

ts
;; => Error printing return value (StackOverflowError) at clojure.lang.RT/assoc (RT.java:827).
;;    null

(get-auth-header ts :google)


(def r 
  (GET  "https://www.googleapis.com/oauth2/v2/userinfo" 
    {:headers (get-auth-header ts :google)})  
  )


@r
r


(def r
  (GET  "https://www.googleapis.com/drive/v3/files"
    {:headers (get-auth-header ts :google)}))



