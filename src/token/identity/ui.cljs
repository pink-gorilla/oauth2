(ns token.identity.ui
  (:require
   [frontend.tooltip :refer [with-tooltip]]
   [token.identity.user :refer [user-a]]
   [token.identity.dialog :as dialog]))

(defn header-ico [fa-icon]
  [:a {:on-click #(dialog/show-login-dialog)
       :class "hover:text-blue-700"}
   [:i {:class (str "fa-lg pl-1 " fa-icon)}]])

(defn header-icon [fa-icon rf-dispatch text]
  [with-tooltip text
   [header-ico fa-icon rf-dispatch]])

(defn user-icon-with-login []
 (if @user-a
   [:span.text-green-800 (str "user: " @user-a)]
   [:span.text-red-500
     [header-icon "fas fa-user" "l"]]))

(defn show-login-dialog []
  (dialog/show-login-dialog))