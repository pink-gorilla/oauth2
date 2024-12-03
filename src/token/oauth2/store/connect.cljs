(ns token.oauth2.store.connect
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]
   [token.oauth2.core :as oauth2]))

(defn get-token-summary [providers]
  (clj 'token.oauth2.store/token-summary providers))

(defn save-token [provider token]
  (clj 'token.oauth2.store/save-token provider token))

(defn connect [provider scope]
  (info "connecting to provider: " provider " scope: " scope)
  (let [r-p (p/deferred)
        a-p (oauth2/get-auth-token {:provider provider
                                    :scope scope
                                    :width 500
                                    :height 800
                                    :title (str "authorize " provider)})]
    (-> a-p
        (p/then (fn [token]
                  (info "received token for: " provider)
                  (let [s-p (save-token provider token)]
                    (-> s-p
                        (p/then (fn [result]
                                  (info "token saved!!")
                                  (p/resolve! r-p token)))
                        (p/catch (fn [err]
                                   (info "token save error: " err)
                                   (p/reject! r-p err)))))))
        (p/catch (fn [err]
                   (error "could not get token for: " provider " error: " err)
                   (p/reject! r-p err))))
    r-p))
