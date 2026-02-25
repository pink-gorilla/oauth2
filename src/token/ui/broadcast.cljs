(ns token.ui.broadcast
  (:require
   [cljs.reader :as reader]
   [taoensso.timbre :refer-macros [info]]
   [promesa.core :as p]))

; https://stackoverflow.com/questions/28230845/communication-between-tabs-or-windows
; https://developer.mozilla.org/en-US/docs/Web/API/Broadcast_Channel_API




(defn process-data [ev]
  (let [json (. ev -data)
        data (js->clj (.parse js/JSON json))
        data-cljs (reader/read-string data)]
    (info "type of data: " (type data))
    (info "chan rcvd: json: " json  "data: " data-cljs)
    data-cljs))

(defn broadcast-result []
  (info "waiting for oauth2 broadcast result..")
  (let [r (p/deferred)
        bc (js/BroadcastChannel. "token_user_channel")]
    (set! (.. bc -onmessage)
          (fn [ev]
            (info "broadcast data received.")
            (let [data (process-data ev)]
              (info "closing broadcast channel.")
              (.close bc)
              (info "sending received broadcast data..")
              (p/resolve! r data))))
    r))

(defn broadcast-subscribe [cb]
  (info "creating broadcast subscription..")
  (let [bc (js/BroadcastChannel. "token_user_channel")]
    (set! (.. bc -onmessage)
          (fn [ev]
            (let [data (process-data ev)]
              (info "broadcast-subscription received data: " data)
              (cb data))))
    bc))