(ns token.oauth2.request2)


(defn authorized-post [provider
                       uri {:keys [format timeout response-format params]
                            :or {format (ajax/url-request-format) ; xero
                                        ; (ajax/json-request-format) ; {:keywords? true}
                                 timeout 5000
                                 response-format (ajax/json-response-format {:keywords? true})
                                 params {}}}]
  (let [r (p/deferred)
        header {}]
    (ajax/POST uri
      :headers header
       ; grant_type=refresh_token
         ;refresh_token=Your refresh token
      :params params
      :format  format
      :timeout  timeout                            ;; optional see API docs
      :response-format response-format
      :handler (fn [res]
                 (info "authorized-post success: " res)
                 (p/resolve! r res)))
    :error-handler (fn [res]
                     (error  "authorized-post error: " res)
                     (p/reject! r res))
    r))


;'authorization: Bearer YOUR_ACCESS_TOKEN' 
;$ curl -H "Authorization: token OAUTH-TOKEN" https://api.github.com

(defn store-refreshed-token [provider token-old token-new]
  (debug provider "/refresh-token success: " token-new)
  (let [token-new (sanitize-token token-new)
        token-new (merge token-old token-new)
        ; some providers include a new refresh token, some dont
        ; so we merge the tokens so we use all fields from the new token,
        ; and carry forward the old token (in case we do not get a new refresh-token)
        ;(assoc token-new :refresh-token refresh-token)
        ]
    (debug "new access-token: " (:access-token token-new))
    (save-token provider token-new)))


(defn token-renew-request [provider]
  (let [params {:client_id	 client-id
                :client_secret client-secret
                :refresh_token refresh-token
                :grant_type "refresh_token"}]))




(rf/reg-event-fx
 :request
 (fn [{:keys [db]} [_ provider uri success]]
   (let [token (get-in db [:token provider])
         at (:access-token token)
         r (merge {:headers (get-provider-auth-header provider at)}
                  {:uri uri
                   :method          :get
                   :timeout         5000                     ;; optional see API docs
                   :response-format  (ajax/json-response-format {:keywords? true}) ;; IMPORTANT!: You must provide this.
                   :on-success      [success provider]
                   :on-failure      [:oauth2/request-error provider]})]
     (info "making web request:" provider " token: " at " r: " r)
     {:http-xhrio r})))

  ;:format (ajax/json-request-format) ;  {:keywords? false})
  ;:response-format; {:keywords? true})

(defn err-msg [res]
  (or (get-in res [:response :error :message])
      (get-in res [:response :message])))

(rf/reg-event-fx
 :oauth2/request-error
 (fn [{:keys [db]} [_ provider res]]
   (errorf "oauth2 provider: %s error: %s" provider res)
   (show-notification :error (str "request error " provider ": " (err-msg res)))
   {}))


