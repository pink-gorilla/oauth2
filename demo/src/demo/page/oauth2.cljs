(ns demo.page.oauth2
  (:require
   [promesa.core :as p]
   [reagent.core :as r]
   [goldly.service.core :refer [clj]]
   [token.identity.dialog :refer [show-login-dialog]]
   [token.identity.user :refer [user-a]]
   [token.identity.ui :refer [user-icon-with-login]]
   [token.oauth2.store.ui :refer [provider-status-grid]]
   [demo.helper.ui :refer [link-fn block2]]))

(defn demo-oauth []
  [block2 "token user oauth2"
   ; available users   
   [:h1 "users (that are configured in the demo app)"]
   [:p "user: florian pwd: 1234"]
   [:p "user: boss pwd: 1234"]
   ; login button
   [:h1 "login"]
   [link-fn #(show-login-dialog) "show login dialog2"]
   [:h1 "user button"]
   [user-icon-with-login]])

(defn user-details []
  (fn []
      [block2 "user details (for debugging): "
       [:p (pr-str @user-a)]]))

(def provider-scopes 
  {:github [;https://docs.github.com/en/developers/apps/scopes-for-oauth-apps
             "user"
             ;"user:email" ; this includes ONLY the user email
             "gist"
             "repo"
             "openid"
            ]
    :google [;https://developers.google.com/identity/protocols/oauth2/scopes
                                            ;"offline_access" ; does not work!
             "openid" ; The scope parameter must begin with the openid value and then include the profile value, the email value, or both.
             "email" ; If the email scope value is present, the ID token includes email and email_verified claims.
             "profile" ; he profile scope value is present, the ID token might (but is not guaranteed to) include the user's default profile claims.
             "https://www.googleapis.com/auth/userinfo.email"
                                            ; readonly 
             "https://www.googleapis.com/auth/spreadsheets.readonly"
             "https://www.googleapis.com/auth/drive.readonly"
             "https://www.googleapis.com/auth/gmail.readonly"
             "https://www.googleapis.com/auth/drive.photos.readonly"
                                            ; edit
             "https://www.googleapis.com/auth/spreadsheets"
             "https://www.googleapis.com/auth/calendar"
             "https://www.googleapis.com/auth/drive"
             "https://www.googleapis.com/auth/drive.appdata"
             "https://www.googleapis.com/auth/drive.file"
             "https://www.googleapis.com/auth/drive.metadata"
             "https://www.googleapis.com/auth/drive.metadata.readonly"
             "https://www.googleapis.com/auth/cloud-platform"
   
             "https://docs.google.com/feeds/"
             "https://spreadsheets.google.com/feeds"
   
             "https://www.googleapis.com/auth/cloud_search"]
    :xero ["offline_access" ; refresh_token
           "openid"
           "profile"
           "email"
           "accounting.settings"
           "accounting.reports.read"
           "accounting.journals.read"
           "accounting.contacts"
           "accounting.attachments"
           "accounting.transactions"
           "accounting.transactions.read"]
    ; :woo ["read" "write"  "read_write"]
    })

(defn store-connections []
 [block2 "store connections:"
   [:div.border.border-blue-500.border-2.border-round ; .overflow-scroll
     [provider-status-grid provider-scopes]]]) 
      



(defonce result-a (r/atom "--- no request made yet ---"))

(defn exec [& args]
  (println "executing clj: " args)
  (let [data-p (apply clj args)]
    (-> data-p
        (p/then (fn [result]
                  (println "exec " args " success: " result)
                  (reset! result-a result)))
        (p/catch (fn [err]
                   (println "exec " args " error: " err)
                   (reset! result-a {:error err}))))
    nil))

(defn demo-clj []
  [block2 "clj service"
   [:div.flex.flex-col
    [link-fn #(exec 'demo.time/time-public) " request time (permission: public)"]
    [link-fn #(exec 'demo.time/time-debug) "request user/session info (permission: public)"]
    [link-fn #(exec 'demo.time/time-user) " request time (permission: logged in)"]
    [:p "date-local needs supervisor role, which demo does not have!"]
    [:p "user: boss pwd:1234  does have the supervisor role!"]
    [link-fn #(exec 'demo.time/time-supervisor) " request time (permission: :supervisor (login as boss))"]
    [link-fn #(exec 'demo.time/xxx) "request time (no function defined = error)"]
    [:p "result:"
       (pr-str @result-a)]]])

(defn page-oauth2 [_]
  [:div.w-full.h-full
   [demo-oauth]
   [user-details]
   [store-connections]
   [demo-clj]])


