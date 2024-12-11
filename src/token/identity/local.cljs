(ns token.identity.local
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]))

(defn get-token
  "returns a promise with the token or an error"
  [user password]
  (info "local get-token user: " user "password: " password)
  (let [r-p (p/deferred)
        data-p (clj 'token.identity.local/get-token user password)]
    (-> data-p
        (p/then (fn [{:keys [error error-message] :as token}]
                  (if error
                    (p/reject! r-p error-message)
                    (p/resolve! r-p token))))
        (p/catch (fn [err]
                   (error "get-token error: " err)
                   (p/reject! r-p err))))
    r-p))

(defn login
  "input: the result of get-token (or the saved token in localstorage)"
  [user]
  (info "login (local) user: " user)
  (let [r-p (p/deferred)
        data-p (clj 'token.identity.local/login user)]
    (-> data-p
        (p/then (fn [{:keys [error error-message] :as result}]
                  (info "local login success: " result)
                  (p/resolve! r-p result)))
        (p/catch (fn [err]
                   (error "local login error: " err)
                   (p/reject! r-p err))))
    r-p))
