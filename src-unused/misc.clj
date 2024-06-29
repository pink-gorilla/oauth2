
  ;[buddy.sign.jwt :as jwt]

;  [buddy.sign.jwt :as jwt]
;(defn unsign [token]
;  (with-redefs [buddy.core.codecs (fn [url] {:body "Goodbye world"})]
;    (jwt/unsign token "key")
;    
;  )

;(defn validate-token [name]
;   ; (jwt/decrypt incoming-data secret)
;  (let [token (load-token name)]
;    (jwt/unsign token "key")))