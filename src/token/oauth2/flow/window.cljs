(ns token.oauth2.flow.window
  (:require
   [taoensso.timbre :refer-macros [info error]]))

(defn open-window [{:keys [url title height width]
                    :or {width 500
                         height 600}}]
  (info "opening window: " url)
  (let [opts (str "width=" width ",height=" height)
        ;opts "width=500,height=600"
        w (.open js/window
                 url
                 title
                 opts)]
    w))

(defn close-window [window]
  (info "closing window..")
  (when window
    (.close window)))