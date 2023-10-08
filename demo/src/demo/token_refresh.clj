(ns demo.token-refresh
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.oauth2.token.refresh :refer [refresh-auth-token]]))


(defn refresh-token [{:keys [provider]}]
  (info "refreshing token provider: " provider)
  (refresh-auth-token provider)
;
    )


(comment 
 
  
  
;  
  )