(ns token.oauth2.handler.save
  (:require
   [taoensso.timbre :as timbre :refer [info error]]
   [ring.util.response :as res]
   [token.sanitize :refer [sanitize-token]]
   [modular.oauth2.token.store :refer [save-token]]))

(defn handler-oauth2-save [req]
  (let [{:keys [token provider]} (get-in req [:body-params])]
    (info "saving token for provider: " provider)
    (let [token (sanitize-token token)]
      (save-token provider token))
    (res/response {:message "Token Saved to store!"})))
