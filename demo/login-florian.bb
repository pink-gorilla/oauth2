#!/usr/bin/env bb

(require '[clojure.string :as str])

(def base "http://localhost:8000")
(def login-url (str base "/token/login"))
(def me-url (str base "/token/me"))

(defn extract-cookie-header [response]
  (let [headers (.headers response)
        set-cookies (when headers (.allValues headers "set-cookie"))]
    (when (seq set-cookies)
      (str/join "; " set-cookies))))

(def body-handler (java.net.http.HttpResponse$BodyHandlers/ofString))

(defn post-login []
  (let [client (java.net.http.HttpClient/newHttpClient)
        body (java.net.http.HttpRequest$BodyPublishers/ofString
              "{\"user\":\"florian\",\"password\":\"1234\"}")
        request (-> (java.net.http.HttpRequest/newBuilder)
                    (.uri (java.net.URI/create login-url))
                    (.header "Content-Type" "application/json")
                    (.POST body)
                    (.build))
        response (.send client request body-handler)]
    (println "POST" login-url "->" (.statusCode response))
    response))

(defn get-me [cookie-header]
  (let [client (java.net.http.HttpClient/newHttpClient)
        builder (-> (java.net.http.HttpRequest/newBuilder)
                   (.uri (java.net.URI/create me-url))
                   (.GET))]
    (when cookie-header
      (.header builder "Cookie" cookie-header))
    (let [request (.build builder)
          response (.send client request body-handler)]
      (println "GET" me-url "->" (.statusCode response))
      (println (.body response))
      response)))

(let [login-response (post-login)
      cookies (extract-cookie-header login-response)]
  (println "Cookies:" (or cookies "(none)"))
  (get-me cookies))
