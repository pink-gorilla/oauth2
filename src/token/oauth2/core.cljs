(ns token.oauth2.core
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [promesa.core :as p]
   [frontend.notification :refer [show-notification]]
   ; helpers
   [token.oauth2.flow.window :refer [open-window close-window]]
   [token.oauth2.flow.broadcast :refer [oauth2-broadcast-result]]
   [token.oauth2.token :refer [sanitize-token]]
   ; provider specifics
   [token.oauth2.provider :refer [oauth2-flow-response-parse]]
   ; flow
   [token.oauth2.flow.url :refer [url-authorize]]
   [token.oauth2.flow.code :refer [exchange-code-to-token]]
   ; side effects - make sure our default providers are compiled into the bundle.
   [token.oauth2.provider.default]))

(defn process-authorize-response [w provider auth-result]
  #_{:provider :google
     :anchor {},
     :query {:scope "email profile openid https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email",
             :prompt "consent",
             :authuser "0",
             :code "4/0ATx3LY4lnqT4ouMOPf7JIkIjFcXjnxu6Y6aL47n1J6ZcIF950eCI4WXmnI_rFXafYNuzAw"}}
  (info "process authorize-response: " auth-result)
  (let [{:keys [scope code]} (oauth2-flow-response-parse auth-result)]
     ;(rf/dispatch [:oauth2/code->token p auth-result])
     ;(rf/dispatch [:oauth2/authorize-success p auth-result])
    (if code
      (let [code-p (exchange-code-to-token {:provider (:provider auth-result)
                                            :code code})]
        (info "authorize-response is a code .. exchanging code to token: " code)
        code-p)
      (let [r-p (p/deferred)]
        (info "authorize-response is already a token.. returning token: " auth-result)
        (p/resolve! r-p auth-result)
        r-p))))

(defn get-token-from-url [{:keys [provider width height url]}]
  (let [broadcast-p (oauth2-broadcast-result)
        title (str "oauth2 for: " provider)
        r-p (p/deferred)
        w (open-window {:url url
                        :title title
                        :width width
                        :height height})]
    (-> broadcast-p
        (p/then (fn [auth-result]
                  (let [token-p (process-authorize-response w provider auth-result)]
                    (-> token-p
                        (p/then (fn [token]
                                  (close-window w)
                                  (p/resolve! r-p token)))
                        (p/catch (fn [err]
                                   (close-window w)
                                   (p/reject! r-p err))))))))
    r-p))

(defn get-auth-token
  "gets a auth-token for {:provider :scope}
   returns a promise"
  [{:keys [provider scope
           title width height]
    :or {width 500
         height 600} :as opts}]
  (let [r-p (p/deferred)
        url-p (url-authorize provider scope)]
    (-> url-p
        (p/then (fn [url]
                  (let [token-p (get-token-from-url (assoc opts :url url))]
                    (-> token-p
                        (p/then (fn [token]
                                  (info "auth-result token: " token)
                                  (let [sane-token (sanitize-token token)]
                                    (info "auth-result sane-token: " sane-token)
                                    (p/resolve! r-p sane-token))))
                        (p/catch (fn [err]
                                   (error "could not get oauth2-authorize-url for provider: " provider " error: " err)
                                   (show-notification :error (str "could not get authorize-url for:  " provider))
                                   (p/reject! r-p err)))))))
        (p/catch (fn [err]
                   (error "could not get oauth2-authorize-url for provider: " provider)
                   (show-notification :error (str "could not get authorize-url for:  " provider))
                   (p/reject! r-p err))))

    r-p))
