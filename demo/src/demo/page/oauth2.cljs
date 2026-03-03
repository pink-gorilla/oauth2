(ns demo.page.oauth2
  (:require
   [promesa.core :as p]
   [reagent.core :as r]
   [token.core :refer [user-a user-icon-with-login login me authorize]]))

(def ^:private page-css
  (str
   ".oauth2-page { font-family: \"Segoe UI\", system-ui, -apple-system, sans-serif; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%); min-height: 100%; padding: 1.5rem; box-sizing: border-box; } "
   ".oauth2-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; } "
   ".oauth2-section { background: #fff; border: 1px solid #d1d9e0; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); padding: 1.25rem 1.5rem; } "
   ".oauth2-section-title { font-size: 1.25rem; font-weight: 600; color: #2c3e50; margin: 0 0 1rem 0; padding-bottom: 0.5rem; border-bottom: 2px solid #3498db; } "
   ".oauth2-link { display: inline-block; padding: 0.4rem 0.75rem; margin: 0.25rem 0.25rem 0.25rem 0; background: #3498db; color: #fff; text-decoration: none; border-radius: 6px; cursor: pointer; border: none; font-size: 0.9rem; transition: background 0.15s ease; } "
   ".oauth2-link:hover { background: #2980b9; } "
   ".oauth2-text { margin: 0.5rem 0; color: #34495e; line-height: 1.5; } "
   ".oauth2-heading { font-size: 1rem; font-weight: 600; color: #2c3e50; margin: 1rem 0 0.5rem 0; } "
   ".oauth2-heading:first-child { margin-top: 0; } "
   ".oauth2-result { margin-top: 0.75rem; padding: 0.75rem; background: #f8f9fa; border-radius: 6px; border: 1px solid #e9ecef; font-size: 0.85rem; color: #495057; overflow-x: auto; } "
   ".oauth2-flex-col { display: flex; flex-direction: column; gap: 0.25rem; } "))

(defn section [title & children]
  (into [:div.oauth2-section
         [:h2.oauth2-section-title title]]
        children))

(defn link-fn [fun text]
  [:a.oauth2-link {:on-click fun} text])

(defn section-available-users []
  [section "Available users"
   [:p.oauth2-text "Users configured in the demo app:"]
   [:p.oauth2-text "user: florian — pwd: 1234"]
   [:p.oauth2-text "user: boss — pwd: 1234"]])

(defn section-user-management []
  (fn []
    [section "User management"
     [:h3.oauth2-heading "Login"]
     [link-fn #(login) "Show login dialog"]
     [:h3.oauth2-heading "User button"]
     [user-icon-with-login]
     [:h3.oauth2-heading "Me"]
     [:a.oauth2-link {:on-click #(me)} "Open ME"]
     [:h3.oauth2-heading "User details (debug)"]
     [:p.oauth2-text (pr-str @user-a)]]))

(defn section-oauth2-authorize []
  [section "OAuth2 authorize requests"
   [link-fn #(authorize {:provider :google
                         :scope [;"offline_access" ; refresh token
                                 "https://www.googleapis.com/auth/spreadsheets.readonly"
                                 "https://www.googleapis.com/auth/drive.readonly"]
                         :save-as "demo-gmail-drive"})
    "Authorize Gmail / Drive creds"]
   [link-fn #(authorize {:provider :xero
                         :scope ["offline_access" ; refresh_token
                                 ;"openid"
                                 ;"profile"
                                 ;"email"
                                 "accounting.settings"
                                 "accounting.reports.read"
                                 "accounting.journals.read"
                                 "accounting.contacts"
                                 "accounting.attachments"
                                 "accounting.transactions"
                                 "accounting.transactions.read"]
                         :save-as "demo-xero-accounting"})
    "Authorize Xero / accounting"]
   ])


(defn page-oauth2 [_]
  [:div.oauth2-page
   [:style page-css]
   [:div.oauth2-grid
    [section-available-users]
    [section-user-management]
    [section-oauth2-authorize]
    ;[section-clj-service]
    ]])
