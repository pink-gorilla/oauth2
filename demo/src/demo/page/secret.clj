(ns demo.page.secret
  (:require
   [hiccup.page :as page]
   [ring.util.response :as response]))

(defn secret-page [req]
  (page/html5
   {:mode :html}
   [:head
    [:meta {:charset "utf-8"}]
    [:title "Secret"]]
   [:body
    [:h1 "this is a very secret page"]]))

(defn page-handler [req]
  (response/content-type
   {:status 200
    :body (secret-page req)}
   "text/html"))

