(ns demo.token-refresh
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [modular.oauth2.token.refresh :refer [refresh-access-token]]))

(defn refresh-token [{:keys [provider]}]
  (info "refreshing token provider: " provider)
  (refresh-access-token provider)
;
  )
(comment

;  
  )