(ns demo.repl.rest.google
  (:require
   [modular.system]
   [token.oauth2.request :refer [GET]]
   [token.oauth2.core :refer [get-auth-header]]))

(def t (modular.system/system :oauth2))

t
;; => Error printing return value (StackOverflowError) at clojure.lang.RT/assoc (RT.java:827).
;;    null

(keys t)
(dissoc t :clj :store)

@(get-auth-header t :google)
 
@(GET  "https://www.googleapis.com/oauth2/v2/userinfo" 
   {:headers @(get-auth-header t :google)})  
  
@(GET  "https://www.googleapis.com/drive/v3/files"
    {:headers @(get-auth-header t :google)})



