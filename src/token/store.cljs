(ns token.store)


(defn save-token
  [{:keys [path] :as this} id data]
  (let [filename (filename-token this id)]
    (save :edn filename data)))

(defn load-token [this id]
  (let [filename (filename-token this id)]
    ;(println "loading token: " filename)
    (loadr :edn filename)))