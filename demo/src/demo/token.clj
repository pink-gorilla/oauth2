(ns demo.token
  (:require
   [clojure.pprint :refer [print-table]]
   [taoensso.timbre :as timbre :refer [info error]]
    ; token info
   [modular.oauth2.token.info :as t]
   [demo.token-info :refer [show]]))

(defn token-info [{:keys [provider]}]
  ; cli entry point
  (show provider))

(defn tokens-summary [{:keys [providers]}]
  ; cli entry point
  (info "providers list: " providers)
  (-> (t/tokens-summary providers)
      (print-table))
  (->> (t/tokens-summary-map providers)
       (info "token summary map:")))


