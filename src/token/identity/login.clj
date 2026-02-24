(ns token.identity.login
  (:require
   [hiccup.page :as page]
   [ring.util.response :as response]))

(defn login-page [{:keys [query-params] :as _req}]
  (let [error (get query-params "error")]
    (page/html5
     {:mode :html}
     [:head
      [:meta {:charset "utf-8"}]
      [:title "Login"]]
     [:body
      [:h1 "Login"]
      (when error
        [:div {:style "color: red; margin-bottom: 1em;"}
         "Error: " error])
      [:form {:action "/token/login"
              :method "post"}
       [:div
        [:label {:for "user"} "Username "]
        [:input#user {:type "text" :name "user" :required true}]]
       [:div
        [:label {:for "password"} "Password "]
        [:input#password {:type "password" :name "password" :required true}]]
       [:div
        [:button {:type "submit"} "Login"]]]])))

(defn page-handler [req]
  (response/content-type
   {:status 200
    :body (login-page req)}
   "text/html"))
