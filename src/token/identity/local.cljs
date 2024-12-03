(ns token.identity.local
  (:require
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]))

(defn get-token
  "returns a promise with the token or an error"
  [user password]
  (println "local get-token user: " user "password: " password)
  (let [r-p (p/deferred)
        data-p (clj 'token.identity.local/get-token user password)]
    (-> data-p
        (p/then (fn [{:keys [error error-message] :as token}]
                  (if error
                    (p/reject! r-p error-message)
                    (p/resolve! r-p token))))
        (p/catch (fn [err]
                   (println "get-token error: " err)
                   (p/reject! r-p err))))
    r-p))

(defn login
  "input: the result of get-token (or the saved token in localstorage)"
  [user]
  (println "login (local) user: " user)
  (let [r-p (p/deferred)
        data-p (clj 'token.identity.local/login user)]
    (-> data-p
        (p/then (fn [{:keys [error error-message] :as result}]
                  (println "local login success: " result)
                  (p/resolve! r-p result)))
        (p/catch (fn [err]
                   (println "local login error: " err)
                   (p/reject! r-p err))))
    r-p))
