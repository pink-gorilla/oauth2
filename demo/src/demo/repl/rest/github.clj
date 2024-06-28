(ns demo.repl.rest.github
  (:require
   [modular.system]
   [token.oauth2.request :refer [GET]]
   [token.oauth2.core :refer [get-auth-header]]))

(def t (modular.system/system :oauth2))

@(get-auth-header t :github)

@(GET "https://api.github.com/user" 
   {:headers @(get-auth-header t :github)})  


;; => {:html_url "https://github.com/awb99",
;;     :disk_usage 22527,
;;     :gravatar_id "",
;;     :followers_url "https://api.github.com/users/awb99/followers",
;;     :subscriptions_url "https://api.github.com/users/awb99/subscriptions",
;;     :owned_private_repos 0,
;;     :site_admin false,
;;     :email nil,
;;     :following_url "https://api.github.com/users/awb99/following{/other_user}",
;;     :hireable nil,
;;     :name "awb99",
;;     :node_id "MDQ6VXNlcjEwODU0Njgy",
;;     :type "User",
;;     :twitter_username nil,
;;     :received_events_url "https://api.github.com/users/awb99/received_events",
;;     :private_gists 2,
;;     :login "awb99",
;;     :following 5,
;;     :updated_at "2024-06-18T15:41:49Z",
;;     :collaborators 0,
;;     :bio nil,
;;     :organizations_url "https://api.github.com/users/awb99/orgs",
;;     :id 10854682,
;;     :events_url "https://api.github.com/users/awb99/events{/privacy}",
;;     :url "https://api.github.com/users/awb99",
;;     :public_gists 23,
;;     :repos_url "https://api.github.com/users/awb99/repos",
;;     :two_factor_authentication true,
;;     :public_repos 2,
;;     :total_private_repos 1,
;;     :starred_url "https://api.github.com/users/awb99/starred{/owner}{/repo}",
;;     :plan {:name "free", :space 976562499, :collaborators 0, :private_repos 10000},
;;     :location nil,
;;     :blog "",
;;     :followers 11,
;;     :company nil,
;;     :gists_url "https://api.github.com/users/awb99/gists{/gist_id}",
;;     :created_at "2015-02-04T19:59:51Z",
;;     :avatar_url "https://avatars.githubusercontent.com/u/10854682?v=4"}



