(ns token.oauth2.flow.success
  (:require
   [taoensso.timbre :refer-macros [info error]]
   ;[modular.oauth2.token.save-handler] ; side effects
   [token.sanitize :refer [sanitize-token]]))

 (defn authorize-success [provider token]
   (let [token (sanitize-token token)
         access-token (:access-token token)]
     (info "oauth2 authorize success: " provider token)
     (when access-token
         ;(rf/dispatch [:oauth2/get-user provider])
         (println "to implement: get user from provider")
         )
       ))