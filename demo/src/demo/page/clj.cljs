(ns demo.page.clj
  (:require
   [promesa.core :as p]
   [reagent.core :as r]
   [goldly.service.core :refer [clj]]))


(def ^:private page-css
  (str
   ".oauth2-page { font-family: \"Segoe UI\", system-ui, -apple-system, sans-serif; background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%); min-height: 100%; padding: 1.5rem; box-sizing: border-box; } "
   ".oauth2-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem; } "
   ".oauth2-section { background: #fff; border: 1px solid #d1d9e0; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); padding: 1.25rem 1.5rem; } "
   ".oauth2-section-title { font-size: 1.25rem; font-weight: 600; color: #2c3e50; margin: 0 0 1rem 0; padding-bottom: 0.5rem; border-bottom: 2px solid #3498db; } "
   ".oauth2-link { display: inline-block; padding: 0.4rem 0.75rem; margin: 0.25rem 0.25rem 0.25rem 0; background: #3498db; color: #fff; text-decoration: none; border-radius: 6px; cursor: pointer; border: none; font-size: 0.9rem; transition: background 0.15s ease; } "
   ".oauth2-link:hover { background: #2980b9; } "
   ".oauth2-text { margin: 0.5rem 0; color: #34495e; line-height: 1.5; } "
   ".oauth2-heading { font-size: 1rem; font-weight: 600; color: #2c3e50; margin: 1rem 0 0.5rem 0; } "
   ".oauth2-heading:first-child { margin-top: 0; } "
   ".oauth2-result { margin-top: 0.75rem; padding: 0.75rem; background: #f8f9fa; border-radius: 6px; border: 1px solid #e9ecef; font-size: 0.85rem; color: #495057; overflow-x: auto; } "
   ".oauth2-flex-col { display: flex; flex-direction: column; gap: 0.25rem; } "))


(defn section [title & children]
  (into [:div.oauth2-section
         [:h2.oauth2-section-title title]]
        children))

(defn link-fn [fun text]
  [:a.oauth2-link {:on-click fun} text])

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


(defn clj-page [& opts]
  [:div.oauth2-page
   [:style page-css]
   [:div.oauth2-grid
    [section-clj-service]]])