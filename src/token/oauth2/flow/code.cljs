(ns token.oauth2.flow.code
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]))

(defn  current-url []
  (-> js/window .-location .-href))

(defn exchange-code-to-token
  "returns a promise with the token or an error"
  [{:keys [provider code]}]
  (info "oauth2 exchange-code-to-token provider: " provider " code: " code)
  (let [r-p (p/deferred)
        data-p (clj 'token.oauth2.core/exchange-code-to-token
                    {:provider provider
                     :current-url (current-url)
                     :code code})]
    (-> data-p
        (p/then (fn [token]
                  (info "exchange-code-to-token success: " provider " token: " token)
                  (p/resolve! r-p token)))
        (p/catch (fn [err]
                   (info "exchange-code-to-token error: " err)
                   (p/reject! r-p err))))
    r-p))
 




