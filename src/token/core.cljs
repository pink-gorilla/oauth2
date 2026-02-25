(ns token.core
  (:require
   [taoensso.timbre :refer-macros [info warn error]]
   [promesa.core :as p]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [token.ui.window :refer [open-window close-window]]
   [token.ui.broadcast :refer [oauth2-broadcast-result]]
   ))

(defonce user-a (r/atom nil))


(defn set-user! [{:keys [token user] :as usermap}]
  (info "setting user to: " user)
  (reset! user-a usermap))

(defn login []
  (info "log in")
  (let [w (open-window {:url "/login"
                        :title ""
                        :height 500
                        :width 500})
        broadcast-result (oauth2-broadcast-result)
        ]
    (-> broadcast-result
        (p/then (fn [r]
                  (info "broadcast result: " r)
                  (close-window w)
                  )))

    )
  )

(defn logout []
  (info "log out")
  (open-window {:url "/logout"
                :title ""
                :height 500
                :width 500}))

(defn me []
  (info "me")
  (open-window {:url "/me"
                :title ""
                :height 500
                :width 500}))

(defn init-user!
  "fn to start user (defined in extension)"
  [_config]
  (info "initializing user .."))

(defn get-user []
  @user-a)

(rf/reg-event-db
 :ws/connected
 (fn [db _]
   (when-let [usermap (get-user)]
     (let [{:keys [token user]} usermap]
       (warn "ws connected - auto login: " user)
       ;(local/login token)
       ))
   db))


(def ^:private user-link-css
  ".token-user-link { cursor: pointer; color: inherit; text-decoration: none; transition: color 0.15s ease; }
   .token-user-link:hover { color: #1d4ed8; }")

(defn user-icon-with-login []
  (let [icon-style {:font-size "1.33em"
                    :paddingleft "0.25rem"
                    :marginright "0.25rem"}
        link-attrs {:class "token-user-link"}]
    (fn []
      [:<>
       [:style user-link-css]
       (if @user-a
         ;; logged in
         [:a (merge link-attrs {:on-click #(logout)})
          [:i (merge icon-style {:class "fas fa-user"})
           (:user @user-a)]]
         ;; logged out
         [:a (merge link-attrs {:on-click #(login)})
          [:i (merge icon-style {:class "fas fa-user"})
           "login"]])])))
