(ns token.identity.page.me
  (:require
   [clojure.string :as str]
   [hiccup.page :as page]
   [ring.util.response :as response]))

(def me-styles
  [:style (str "
    body { font-family: 'Segoe UI', system-ui, sans-serif; font-size: 16px; line-height: 1.5;
           margin: 0; min-height: 100vh; padding: 2rem; box-sizing: border-box;
           background: linear-gradient(135deg, #f5f0eb 0%, #e8dfd5 50%, #ddd6ce 100%); }
    .me-wrap { max-width: 28rem; margin: 0 auto; }
    .me-card { background: #fff; border: 1px solid #d4ccc4; border-radius: 10px;
               box-shadow: 0 4px 12px rgba(0,0,0,0.06); padding: 1.75rem 2rem; }
    .me-title { font-size: 1.5rem; font-weight: 600; color: #2c2520; margin: 0 0 1rem 0; }
    .me-subtitle { font-size: 0.95rem; color: #5c534c; margin-bottom: 1.25rem; }
    .me-dl { margin: 0; padding: 0; list-style: none; }
    .me-dl .me-dt { font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em;
                    color: #7a7068; margin-top: 1rem; margin-bottom: 0.25rem; }
    .me-dl .me-dt:first-child { margin-top: 0; }
    .me-dl .me-dd { font-size: 1rem; color: #2c2520; margin: 0 0 0 0; }
    .me-actions { margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid #ebe6e1; }
    .btn { display: inline-block; padding: 0.5rem 1.25rem; font-size: 0.9rem; font-weight: 500;
           background: #3d3630; color: #fff; text-decoration: none; border-radius: 6px;
           border: none; cursor: pointer; transition: background 0.15s ease; }
    .btn:hover { background: #2c2520; }
  ")])

(defn me-page-logged-out []
  (let [userdata {:user nil 
                  :roles nil 
                  :email []
                  :provider "anonymous"}]
    (page/html5
     {:mode :html}
     [:head
      [:meta {:charset "utf-8"}]
      [:title "Me"]
      me-styles]
     [:body
      [:div.me-wrap
       [:div.me-card
        [:h1.me-title "Me"]
        [:p.me-subtitle "You are not logged in."]
        [:div.me-actions
         [:a.btn {:href "/login"} "Login"]]
        [:script {:src "/r/oauth2/redirect.js"
                  :type "text/javascript"
                  :onload (str "sendcallback ('" userdata "');")}]
        ]]])))

(defn me-page-logged-in [identity]
  (let [{:keys [user roles email provider]
         :or {user nil roles [] email nil provider nil}
         :as userdata} identity]
    (page/html5
     {:mode :html}
     [:head
      [:meta {:charset "utf-8"}]
      [:title "Me"]
      [:script {:src "/r/oauth2/redirect.js"
                :type "text/javascript"
                :onload (str "sendcallback ('" userdata "');")}]

      me-styles]
     [:body
      [:div.me-wrap
       [:div.me-card
        [:h1.me-title "Profile"]
        [:dl.me-dl
         [:dt.me-dt "User"]
         [:dd.me-dd (or user "—")]
         [:dt.me-dt "Roles"]
         [:dd.me-dd (if (seq roles) (str/join ", " roles) "—")]
         [:dt.me-dt "Email"]
         [:dd.me-dd (if (sequential? email) (str/join ", " email) (or email "—"))]
         [:dt.me-dt "Provider"]
         [:dd.me-dd (or provider "—")]]
        [:div.me-actions
         [:a.btn {:href "/token/logout"} "Logout"]]]]])))

(defn me-page [{:keys [identity] :as _req}]
  (let [logged-in? (and identity (some? (:user identity)))
        body (if logged-in?
               (me-page-logged-in identity)
               (me-page-logged-out))]
    (response/content-type
     {:status 200
      :body body}
     "text/html")))