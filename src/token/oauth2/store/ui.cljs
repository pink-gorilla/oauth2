(ns token.oauth2.store.ui
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [reagent.core :as r]
   [promesa.core :as p]
   [frontend.notification :refer [show-notification]]
   [token.oauth2.store.connect :refer [connect get-token-summary]]))

(defn connect-notify [provider scope]
  (let [r (connect provider scope)]
    (-> r
        (p/then (fn [r]
                  (show-notification :info [:span.bg-blue-300.inline (str "token aquired successfully for: " provider)] 3000)))
        (p/catch (fn [r]
                   (show-notification :error [:span.bg-red-300.inline (str "token could not be aquired for: " provider)] 30000))))))

(defn provider-status [{:keys [provider scope available user expires-date]}]
  ;(info "provider: " provider " status: " status)
  [:<>
   [:div (name provider)]
   [:div (str available)]
   [:div {:on-click #(connect-notify provider scope)
          :class "hover:text-blue-700"}
    "connect"]])

(defn providers-ui [provider-table]
  (into
   [:div.grid.grid-cols-3
    [:div "provider"]
    [:div "available?"]
    [:div "c/d"]]
   (map provider-status provider-table)))

(defn summary->map [summary]
  (->> (map (juxt :provider identity) summary)
       (into {})))

(defn add-summary [table summary]
  (info "summary: " summary)
  (let [m (summary->map summary)]
    (info "provider-map:" m)
    (map (fn [{:keys [provider] :as table-row}]
           (merge table-row (or (provider m) {}))) table)))

(defn provider-status-grid [provider-scopes]
  (let [provider-table (r/atom (map (fn [[provider scope]]
                                      {:provider provider
                                       :scope scope
                                       :available false
                                       :user "-"
                                       :expires "-"}) provider-scopes))
        providers (keys provider-scopes)
        summary-p (get-token-summary providers)]
    (-> summary-p
        (p/then  (fn [summary]
                   (info "received provider-summary: " summary)
                   (swap! provider-table add-summary summary)))
        (p/catch  (fn [e]
                    (error "could not get provider-table! error: " e))))
    (fn [_providers]
      [providers-ui @provider-table])))

