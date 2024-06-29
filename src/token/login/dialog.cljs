(ns token.login.dialog
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [promesa.core :as p]
   [reagent.core :as r]
   [frontend.notification :refer [show-notification]]
   [frontend.dialog :refer [dialog-show dialog-close]]
   [token.user :as user]
   [token.identity.local :as local]
   [token.identity.oidc :as oidc]
   [token.oauth2.core :as oauth2]))

(defn- login-local [username password]
  (info "logging in locally..")
  (let [r-p (local/get-token username password)]
    (-> r-p
        (p/then (fn [{:keys [user token] :as usermap}]
                  (println "login local token success! user: " user " token: " token)
                  (show-notification :info [:span.bg-blue-300.inline "logged in successfully"] 1000)
                  (user/set-user! usermap)
                  (dialog-close)))
        (p/catch (fn [err]
                   (println "login local error: " err)
                   (show-notification :error [:span.bg-red-300.inline "login error!"] 1000)
                   (dialog-close))))))

(defn- login-oauth2 [provider]
  (info "logging in oauth2 provider: " provider " ..")
  (let [r-p (oauth2/get-auth-token {:provider provider
                          ;:width
                          ;:height 
                                    })]
    (-> r-p
        (p/then (fn [token]
                  (println "login oauth2 token success! token: " token)
                  (show-notification :info [:span.bg-blue-300.inline "logged in successfully"] 1000)
                  (let [user-p (oidc/login provider token)]
                    (-> user-p 
                       (p/then (fn [login-result]
                                 (println "oauth2 login success: " login-result)))    
                       (p/catch (fn [login-err]
                                  (println "oauth2 login error: " login-err))))
                       ;(user/set-user! usermap)
                    )
                  (dialog-close)))
        (p/catch (fn [err]
                   (println "login local error: " err)
                   (show-notification :error [:span.bg-red-300.inline "login error!"] 1000)
                   (dialog-close))))))



(defn- login-ui []
  (let [username (r/atom "")
        password (r/atom "")
        on-change (fn [a]
                    (fn [e]
                      (let [v (-> e .-target .-value)]
                        ;(info "changed: " v)
                        (reset! a v))))]
    (fn []
      [:div {:class "w-full flex items-center justify-center bg-blue-800"}
       [:div {:class "bg-gray-200 w-96 h-auto rounded-lg pt-8 pb-8 px-8 flex flex-col items-center"}

      ;; header
        [:label {:class "font-light text-4xl mb-4"} ""
         [:span {:class "font-bold"} "login"]]

      ; username
        [:input {:type "text"
                 :class "w-full h-12 rounded-lg px-4 text-lg focus:ring-blue-600 mb-4"
                 :placeholder "Email"
                 :value @username
                 :on-change (on-change username)}]
        [:input {:type "password"
                 :class "w-full h-12 rounded-lg px-4 text-lg focus:ring-blue-600 mb-4"
                 :placeholder "Password"
                 :value @password
                 :on-change (on-change password)}]
        [:button {:class "w-full h-12 rounded-lg bg-blue-600 text-gray-200 uppercase font-semibold hover:bg-blue-700 text-gray-100 transition mb-4"
                  :on-click #(login-local @username @password)} "Login"]

        [:label {:class "text-gray-800 mb-4"} "or"]

        [:button {:class "w-full h-12 rounded-lg bg-red-600 text-gray-200 uppercase font-semibold hover:bg-red-700 text-gray-100 transition mb-4"
                  :on-click #(login-oauth2 :google)}
         "Sign with Google"]
        ;[:button {:class "w-full h-12 rounded-lg bg-blue-600 text-gray-200 uppercase font-semibold hover:bg-blue-700 text-gray-100 transition mb-4"}
        ; "Sign with Facebook"]
        ;[:button {:class "w-full h-12 rounded-lg bg-gray-800 text-gray-200 uppercase font-semibold hover:bg-gray-900 text-gray-100 transition mb-4"}
        ;"Sign with Github"]
        ]])))

(defn show-login-dialog []
  (dialog-show
   [:div [login-ui]]
   :medium))



#_(rf/reg-event-fx
   :oauth2/login-oauth-success
   (fn [{:keys [db]} [_ provider token]]
     (info "oauth2 login success via oidc for provider " provider "token: " (pr-str token))
     (rf/dispatch [:ws/send [:login/oidc token]])
     nil))
