(ns token.oauth2.flow.broadcast
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [promesa.core :as p]
   [cemerick.url :as url]))

; https://stackoverflow.com/questions/28230845/communication-between-tabs-or-windows
; https://developer.mozilla.org/en-US/docs/Web/API/Broadcast_Channel_API

(defn keywordize [my-map]
  (into {}
        (for [[k v] my-map]
          [(keyword k) v])))

(defn url-data [wurl]
  (let [url (url/url wurl)]
    {:anchor (keywordize (url/query->map (:anchor url)))
     :query (keywordize (:query url))}))

(defn process-data [ev]
      (let [json (. ev -data)
            data (js->clj (.parse js/JSON json))
            wurl (get data "url")
            provider (get data "provider")
            cbdata (merge  (url-data wurl) {:provider (keyword provider)})]
        (info "oauth chan rcvd: json: " json  "data: " data)
        (info "oauth cb data: " cbdata)
        cbdata))

(defn oauth2-broadcast-result []
  (info "waiting for oauth2 broadcast result..")
  (let [r (p/deferred)
        bc (js/BroadcastChannel. "webly_oauth2_redirect_channel")] 
    (set! (.. bc -onmessage)
        (fn [ev]
          (info "broadcast data received.")
          (let [data (process-data ev)]
            (info "closing broadcast channel.")
            (.close bc)
            (info "sending received broadcast data..")
            (p/resolve! r data))))
    r
    ))

