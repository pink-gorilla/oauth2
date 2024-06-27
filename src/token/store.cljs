(ns token.store
  (:require
    [taoensso.timbre :refer-macros [info error]]
    [promesa.core :as p]
    [goldly.service.core :refer [clj]]))

(defn  current-url []
  (-> js/window .-location .-href))

(defn save-token
  "returns a promise with save result"
  [{:keys [provider token]}]
  (info "save-token provider: " provider " token: " token)
  (let [r-p (p/deferred)
        data-p (clj 'token.store/save-token provider token)]
    (-> data-p
        (p/then (fn [token]
                  (info "save-token success: " provider " token: " token)
                  (p/resolve! r-p token)))
        (p/catch (fn [err]
                   (info "save-token error: " err)
                   (p/reject! r-p err))))
    r-p))
 
