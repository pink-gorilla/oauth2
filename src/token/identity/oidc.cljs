(ns token.identity.oidc
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]))

(defn login
  "input: the result of get-token (or the saved token in localstorage)"
  [provider token]
  (info "login (oidc) provider:" provider " token: " token)
  (let [r-p (p/deferred)
        data-p (clj 'token.identity.oidc/login {:provider provider :token token})]
    (-> data-p
        (p/then (fn [{:keys [error error-message] :as result}]
                  (info "local oidc success: " result)
                  (p/resolve! r-p result)))
        (p/catch (fn [err]
                   (error "local oidc error: " err)
                   (p/reject! r-p err))))
    r-p))
