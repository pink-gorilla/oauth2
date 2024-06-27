(ns token.oauth2.handler.start
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [ring.util.response :as response]
   [token.oauth2.core :refer [url-authorize]]))

(defn req->url [{:keys [scheme server-name server-port uri] :as req}]
   ; {:scheme :http :server-name "localhost" :server-port 8080
   ;  :uri "/api/oauth2/start/github" :protocol "HTTP/1.1"}
   ; :headers {"host" "localhost:8080"}, 
  (or (get-in req [:query-params "current-url"])
      (str (name scheme) "://" server-name ":" server-port uri)))

(defn create-handler-oauth2-start
  "this handler is opened in a browser-popup window.
   It redirects to the oauth2 start url of the provider"
  [this]
  (fn [req]
    #_(info "oauth2/authorize-start: "
            "params:" (:params req)
            "query-params:" (:query-params req))
  ;(info "oauth2/authorize-start: req:" req)
    (let [current-url (req->url req)
          provider (get-in req [:route-params :provider])
          provider-kw (keyword provider)
          scope (get-in req [:route-params :scope])
          _  (info "oauth2 start for provider: " provider-kw)
          url-auth (url-authorize this provider-kw current-url scope)]
      (info "current url query-param: " current-url)
      (info "url auth: " url-auth)
      (-> (response/redirect url-auth)
        ;(assoc :headers {"Content-Type" "text/html"})
          ))))
