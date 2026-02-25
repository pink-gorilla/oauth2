(ns token.core
  (:require
   [taoensso.timbre :refer-macros [info warn error]]
   [promesa.core :as p]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [token.ui.window :refer [open-window close-window]]
   [token.ui.broadcast :refer [broadcast-result broadcast-subscribe]]
   ))

(defonce user-a (r/atom nil))


(defn set-user! [u]
  (warn "user changed to: " u)
  (info "current username: " (:user u))
  (reset! user-a u))

(broadcast-subscribe
 (fn [u]
   (info "user refresher has received: " u)
   (set-user! u) 
   nil))


(defn open-window-autoclose [url]
  (let [w (open-window {:url url
                        :title ""
                        :height 500
                        :width 500})
        result (broadcast-result)]
    (-> result
        (p/then (fn [r]
                  (info "auto-closing after result: " r)
                  (close-window w))))))


(defn login []
  (info "log in")
  (open-window-autoclose "/login")
  )

(defn logout []
  (info "log out")
  (open-window-autoclose "/logout"))

(defn me []
  (info "me")
  (open-window {:url "/me"
                :title ""
                :height 500
                :width 500}))

(defn authorize [{:keys [provider scope save-as] :as q}]
  (let [params (js/URLSearchParams.)] 
    (doseq [s (or scope [])] 
      (.append params "scope" s))
    (.append params "save-as" save-as)
    (let [provider (name provider)
          query (.toString params)
          url (str "/token/oauth2/start/" provider "?" query)]
      (info "authorize" url)
      (open-window {:url url
                    :title ""
                    :height 600
                    :width 500}))))

(defn init-user!
  "fn to start user (defined in extension)"
  [_config]
  (info "initializing user ..")
  (-> (js/fetch "/token/me")
      (.then (fn [response] (.json response)))
      (.then (fn [data]
               (let [clj-data (js->clj data :keywordize-keys true)]
                 (info "token/me result:" clj-data)
                 (set-user! clj-data)
                 )))
      (.catch (fn [err] (error "token/me error:" err)))))

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
       [:link {:rel "stylesheet"
               :href "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"}]
       [:style user-link-css]
       (if @user-a
         ;; logged in
         [:a (merge link-attrs {:on-click #(me)})
          [:i (merge icon-style {:class "fas fa-user"})
           (:user @user-a)]]
         ;; logged out
         [:a (merge link-attrs {:on-click #(login)})
          [:i (merge icon-style {:class "fas fa-user"})
           "login"]])])))
