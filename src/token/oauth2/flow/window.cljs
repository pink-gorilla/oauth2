(ns token.oauth2.flow.window
  (:require
   [taoensso.timbre :refer-macros [info error]]))

; featrues options:
; fullscreen=yes|no|1|0	Whether or not to display the browser in full-screen mode. Default is no. A window in full-screen mode must also be in theater mode. IE only
; height=pixels	The height of the window. Min. value is 100
; left=pixels	The left position of the window. Negative values not allowed
; location=yes|no|1|0	Whether or not to display the address field. Opera only
; menubar=yes|no|1|0	Whether or not to display the menu bar
; resizable=yes|no|1|0	Whether or not the window is resizable. IE only
; scrollbars=yes|no|1|0	Whether or not to display scroll bars. IE, Firefox & Opera only
; status=yes|no|1|0	Whether or not to add a status bar
; titlebar=yes|no|1|0	Whether or not to display the title bar. Ignored unless the calling application is an HTML Application or a trusted dialog box
; toolbar=yes|no|1|0	Whether or not to display the browser toolbar. IE and Firefox only
; top=pixels	The top position of the window. Negative values not allowed
; width=pixels	The width of the window. Min. value is 100


(defn open-window [{:keys [url title height width]
                    :or {width 500
                         height 600}}]
  (info "opening window: " url)
  (let [features (str "width=" width ",height=" height) ; A comma-separated list of items, no whitespaces.
        ;features "width=500,height=600"
        w (.open js/window url title features)]
    (println "window features: " features)
    w))

(defn close-window [window]
  (info "closing window..")
  (when window
    (.close window)))