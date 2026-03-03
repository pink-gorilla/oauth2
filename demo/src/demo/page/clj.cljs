(ns demo.page.clj
  (:require
   [promesa.core :as p]
   [reagent.core :as r]
   [goldly.service.core :refer [clj]]
   ))


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



(defn section-clj-service []
  [section "CLJ service requests"
   [:div.oauth2-flex-col
    [link-fn #(exec 'demo.time/time-public) "Request time (permission: public)"]
    [link-fn #(exec 'demo.time/time-debug) "Request user/session info (permission: public)"]
    [link-fn #(exec 'demo.time/time-user) "Request time (permission: logged in)"]
    [:p.oauth2-text "date-local needs supervisor role, which demo does not have!"]
    [:p.oauth2-text "user: boss pwd:1234 has the supervisor role!"]
    [link-fn #(exec 'demo.time/time-supervisor) "Request time (permission: :supervisor (login as boss))"]
    [link-fn #(exec 'demo.time/xxx) "Request time (no function defined = error)"]
    [:p.oauth2-text "Result:"]
    [:pre.oauth2-result (pr-str @result-a)]]])