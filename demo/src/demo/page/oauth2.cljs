(ns demo.page.oauth2
  (:require
   [re-frame.core :as rf]
   [modular.oauth2.user.view :refer [user-login]]
   [modular.oauth2.token.ui :refer [provider-status-grid]]
   [demo.helper.ui :refer [link-dispatch link-href link-fn block2]]
   [demo.helper.oauth2] ; side-effects
   ))

;; OAUTH
(defn demo-oauth []
  [block2 "oauth2"
   [user-login]
   [:div.border.border-blue-500.border-2.border-round ; .overflow-scroll
    [provider-status-grid [:google :github :xero :woo :wordpress
                           :wunderbar]]] ; not available. does not have token.
   ])

(defn page-oauth2 [_]
  [:div.w-full.h-full
   [link-fn #(rf/dispatch [:login/dialog]) "login"]
   [demo-oauth]])