(ns demo.app
  (:require
   [reitit.frontend.easy :as rfe]
   [frontend.css :refer [css-loader]]
   [frontend.notification :refer [notification-container]]
   [frontend.dialog :refer [modal-container]]
   [webly.spa.env :refer [get-resource-path]]
   [demo.page.clj]
   ))

(defn wrap-page [page match]
  [:div
   [modal-container]
   [notification-container]
   [css-loader (get-resource-path)]
   [page match]])

(defn main-page [& opts]
  [:div
   [:p "main page."]
   [:a {:href (rfe/href :about)}
    [:p  "about"]]
   [:a {:href "/secret"} [:p  "secure static page"]]
   [:a {:href "/me"} [:p  "my current user"]]
   [:a {:href "/login"} [:p  "force login"]]
   [:a {:href (rfe/href 'demo.page.oauth2/page-oauth2)}
    [:p "oauth demo"]]
   [:a {:href (rfe/href :clj)}
    [:p "clj demo"]]
   ])

(def routes
  [["/" {:name :main :view main-page}]
   ["/about" {:name :about :view (fn [] [:h1 "About"])}]
   ["/oauth2" {:name 'demo.page.oauth2/page-oauth2}]
   ["/clj" {:name :clj :view demo.page.clj/clj-page}]
   
   ])
