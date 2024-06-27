(ns token.store.ui
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [reagent.core :as r]
   [promesa.core :as p]
   [goldly.service.core :refer [clj]]
   [token.oauth2.core :as oauth2]))

(defn get-token-summary [providers]
  (clj 'token.store/token-summary providers))

(defn save-token [provider token]
  (clj 'token.store/save-token provider token))

(defn connect [provider]
  (let [r-p (p/deferred)
        a-p (oauth2/get-auth-token {:provider provider
                                     ;:width
                                     ;:height
                                    })]
    (-> a-p
        (p/then (fn [token]
                  (info "received token for: " provider)
                  (let [s-p (save-token provider token)]
                    (-> s-p
                        (p/then (fn [result]
                                  (info "token saved!!")
                                  (p/resolve! r-p token)))
                        (p/catch (fn [err]
                                   (info "token save error: " err)
                                   (p/reject! r-p err)))))))
        (p/catch (fn [err]
                   (error "could not get token for: " provider " error: " err)
                   (p/reject! r-p err))))
    r-p))

(defn provider-status [{:keys [provider available user expires-date]}]
  ;(info "provider: " provider " status: " status)
  [:<>
   [:div (name provider)]
   [:div (str available)]
   [:div {:on-click #(connect provider)
          :class "hover:text-blue-700"}
    "connect"]])

(defn providers-ui [provider-table]
  (into
   [:div.grid.grid-cols-3
    [:div "provider"]
    [:div "available?"]
    [:div "c/d"]]
   (map provider-status provider-table)))


(defn provider-status-grid [providers]
  (let [provider-table (r/atom [])
        table-p (get-token-summary providers)]
    (-> table-p
        (p/then  (fn [t]
                   (info "received provider-table: " t)
                   (reset! provider-table t)))
        (p/catch  (fn [e]
                    (error "could not get provider-table! error: " e)
                    (reset! table-p []))))
    (fn [_providers]
      [providers-ui @provider-table])))

