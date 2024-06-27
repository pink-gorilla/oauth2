(ns token.oauth2.flow.url
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]
    ))

(defn  current-url []
  (-> js/window .-location .-href))


(defn url-authorize
  "returns a promise with the token or an error"
  [provider scope]
  (info "oauth2 url-authorize provider: " provider )
  (let [r-p (p/deferred)
        data-p (clj 'token.oauth2.core/url-authorize
                    provider
                    (current-url)
                    scope)]
    (-> data-p
        (p/then (fn [{:keys [error error-message] :as url}]
                  (info "url-authorize success: " provider " url: " url)
                  (p/resolve! r-p url)))
        (p/catch (fn [err]
                   (info "url-authorize error: " err)
                   (p/reject! r-p err))))
    r-p))
 