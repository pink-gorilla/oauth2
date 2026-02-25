(ns token.identity.page.login
  (:require
   [hiccup.page :as page]
   [ring.util.response :as response]))

(def login-styles
  [:style (str "
    body { font-family: 'Segoe UI', system-ui, sans-serif; font-size: 16px; line-height: 1.5;
           margin: 0; min-height: 100vh; padding: 2rem; box-sizing: border-box;
           background: linear-gradient(135deg, #f5f0eb 0%, #e8dfd5 50%, #ddd6ce 100%); }
    .me-wrap { max-width: 28rem; margin: 0 auto; }
    .me-card { background: #fff; border: 1px solid #d4ccc4; border-radius: 10px;
               box-shadow: 0 4px 12px rgba(0,0,0,0.06); padding: 1.75rem 2rem; }
    .me-title { font-size: 1.5rem; font-weight: 600; color: #2c2520; margin: 0 0 1rem 0; }
    .login-error { font-size: 0.9rem; color: #b54a4a; margin-bottom: 1rem; padding: 0.5rem 0; }
    .login-field { margin-bottom: 1rem; }
    .login-field:last-of-type { margin-bottom: 0; }
    .login-label { font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em;
                  color: #7a7068; display: block; margin-bottom: 0.35rem; }
    .login-input { width: 100%; padding: 0.5rem 0.65rem; font-size: 1rem; color: #2c2520;
                   border: 1px solid #d4ccc4; border-radius: 6px; box-sizing: border-box; }
    .login-input:focus { outline: none; border-color: #3d3630; }
    .login-actions { margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid #ebe6e1; }
    .btn { display: inline-block; padding: 0.5rem 1.25rem; font-size: 0.9rem; font-weight: 500;
           background: #3d3630; color: #fff; text-decoration: none; border-radius: 6px;
           border: none; cursor: pointer; transition: background 0.15s ease; }
    .btn:hover { background: #2c2520; }
  ")])

(defn login-page [{:keys [query-params] :as _req}]
  (let [error (get query-params "error")
        body (page/html5
              {:mode :html}
              [:head
               [:meta {:charset "utf-8"}]
               [:title "Login"]
               login-styles]
              [:body
               [:div.me-wrap
                [:div.me-card
                 [:h1.me-title "Login"]
                 (when error
                   [:div.login-error "Error: " error])
                 [:form {:action "/token/login"
                         :method "post"}
                  [:div.login-field
                   [:label.login-label {:for "user"} "Username"]
                   [:input#user.login-input {:type "text" :name "user" :required true}]]
                  [:div.login-field
                   [:label.login-label {:for "password"} "Password"]
                   [:input#password.login-input {:type "password" :name "password" :required true}]]
                  [:div.login-actions
                   [:button.btn {:type "submit"} "Login"]]]]]])]
    (response/content-type
     {:status 200
      :body body}
     "text/html")))

