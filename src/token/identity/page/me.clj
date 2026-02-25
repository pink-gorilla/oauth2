(ns token.identity.page.me
  (:require
   [clojure.string :as str]
   [hiccup.page :as page]
   [ring.util.response :as response]))

(def button-style "display: inline-block; padding: 0.5em 1em; background: #333; color: white; text-decoration: none; border-radius: 4px;")

(defn me-page-logged-out []
  (page/html5
   {:mode :html}
   [:head
    [:meta {:charset "utf-8"}]
    [:title "Me"]]
   [:body
    [:h1 "Me"]
    [:p "You are not logged in."]
    [:p
     [:a.btn {:href "/login" :style button-style}
      "Login"]]]))

(defn me-page-logged-in [identity]
  (let [{:keys [user roles email provider]
         :or {user nil roles [] email nil provider nil}} identity]
    (page/html5
     {:mode :html}
     [:head
      [:meta {:charset "utf-8"}]
      [:title "Me"]]
     [:body
      [:h1 "Profile"]
      [:dl
       [:dt "User"]
       [:dd (or user "—")]
       [:dt "Roles"]
       [:dd (if (seq roles) (str/join ", " roles) "—")]
       [:dt "Email"]
       [:dd (if (sequential? email) (str/join ", " email) (or email "—"))]
       [:dt "Provider"]
       [:dd (or provider "—")]]
      [:p
       [:a.btn {:href "/token/logout" :style button-style}
        "Logout"]]])))

(defn me-page [{:keys [identity] :as _req}]
  (let [logged-in? (and identity (some? (:user identity)))
        body (if logged-in?
               (me-page-logged-in identity)
               (me-page-logged-out))]
    (response/content-type
     {:status 200
      :body body}
     "text/html")))