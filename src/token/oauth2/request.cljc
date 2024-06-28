(ns token.oauth2.request
  (:require
   [taoensso.timbre :refer [debug info error errorf]]
   [promesa.core :as p]
   [ajax.core :as ajax]))


(defn GET [uri {:keys [format timeout response-format params headers]
                :or {format ;(ajax/url-request-format) ; xero
                     (ajax/json-request-format) ; {:keywords? true}
                     timeout 5000
                     response-format (ajax/json-response-format {:keywords? true})
                     params {}
                     }}]
  (let [r (p/deferred)]
    (ajax/GET uri {:headers headers
                   :params params
                   :format  format
                   :timeout  timeout ;; optional see API docs
                   :response-format response-format
                   :handler (fn [res]
                              (info "http-get success: " res)
                              (p/resolve! r res))
                   :error-handler (fn [res]
                                    (error  "http-get error: " res
                                            "url: " uri 
                                            " params: " params 
                                            " headers: " headers)
                                    
                                    (p/reject! r (ex-info "http-get error!" res) ))})

    r))




