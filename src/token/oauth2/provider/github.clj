(ns token.oauth2.provider.github
  (:require
   [token.oauth2.provider :refer [oauth2-authorize 
                                  oauth2-auth-header 
                                  oauth2-auth-response-parse
                                  user-info-map
                                  ]]))

(defmethod oauth2-authorize :github [_]
  {; authorize
   :uri "https://github.com/login/oauth/authorize"
   :query-params {:response_type "code" ;  "token"
                  }
   })

(defmethod oauth2-auth-response-parse :github [{:keys [query]}]
  ; :query {code 27aefeb35395d63c34}}
  (let [{:keys [scope code prompt authuser]} query]
     {:code (:code query)}))

(defmethod oauth2-auth-header :github [{:keys [token]}]
  {"Authorization" (str "token " token)})

 

(defmethod user-info-map :github [{:keys [token]}]
   ; {:email "name@domain.com"
   ;  :login "masterbuilder99"
   ;  :id 6767676
   ;  :public_gists 4
   ;  :public_repos 1
   ;  :created_at "2015-05-27T14:46:29Z"
   ;  :avatar_url "https://avatars.githubusercontent.com/u/82429483?v=4"}
  {:uri  "https://api.github.com/user"
   :parse-user-info-fn (fn [data]
                         {:user (:login data)
                          :email (:email data)})})


(def config
  {   ; token
   :token-uri "https://github.com/login/oauth/access_token"
   :parse-dispatch [:github/code->token]
   :accessTokenResponseKey "id_token"
   :icon  "fab fa-github-square"})




