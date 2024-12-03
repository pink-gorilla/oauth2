(ns token.identity.ui
  (:require
   [token.identity.user :refer [user-a logout]]
   [token.identity.dialog :as dialog]))

(defn user-icon-with-login []
  (if @user-a
   ; logged in
    [:a {:on-click #(logout)
         :class "hover:text-blue-700"}
     [:i {:class "fa-lg pl-1 fas fa-user"}
      (:user @user-a)]]
   ; logged out
    [:a {:on-click #(dialog/show-login-dialog)
         :class "hover:text-blue-700"}
     [:i {:class "fa-lg pl-1 fas fa-user"}
      "login"]]))

(defn show-login-dialog []
  (dialog/show-login-dialog))