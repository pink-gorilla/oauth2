(ns demo.page.oauth2
  (:require
   [promesa.core :as p]
   [reagent.core :as r]
   [goldly.service.core :refer [clj]]
   [token.core :refer [user-a user-icon-with-login login me]]
   [demo.helper.ui :refer [link-fn block2]]))

(defn demo-oauth []
  [block2 "token user oauth2"
   ; available users   
   [:h1 "users (that are configured in the demo app)"]
   [:p "user: florian pwd: 1234"]
   [:p "user: boss pwd: 1234"]
   ; login button
   [:h1 "login"]
   [link-fn #(login) "show login dialog2"]
   [:h1 "user button"]
   [user-icon-with-login]
   
   [:a {:on-click #(me)}
    [:h1 "ME:"]]
   
   ])

(defn user-details []
  (fn []
    [block2 "user details (for debugging): "
     [:p (pr-str @user-a)]]))



(defonce result-a (r/atom "--- no request made yet ---"))

(defn exec [& args]
  (println "executing clj: " args)
  (let [data-p (apply clj args)]
    (-> data-p
        (p/then (fn [result]
                  (println "exec " args " success: " result)
                  (reset! result-a result)))
        (p/catch (fn [err]
                   (println "exec " args " error: " err)
                   (reset! result-a {:error err}))))
    nil))

(defn demo-clj []
  [block2 "clj service"
   [:div.flex.flex-col
    [link-fn #(exec 'demo.time/time-public) " request time (permission: public)"]
    [link-fn #(exec 'demo.time/time-debug) "request user/session info (permission: public)"]
    [link-fn #(exec 'demo.time/time-user) " request time (permission: logged in)"]
    [:p "date-local needs supervisor role, which demo does not have!"]
    [:p "user: boss pwd:1234  does have the supervisor role!"]
    [link-fn #(exec 'demo.time/time-supervisor) " request time (permission: :supervisor (login as boss))"]
    [link-fn #(exec 'demo.time/xxx) "request time (no function defined = error)"]
    [:p "result:"
     (pr-str @result-a)]]])

(defn page-oauth2 [_]
  [:div.w-full.h-full
   [demo-oauth]
   [user-details] 
   [demo-clj]])


