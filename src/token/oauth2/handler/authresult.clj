(ns token.oauth2.handler.authresult
  (:require
   [hiccup.page :as page]
   [ring.util.response :as response]))

(def authresult-styles
  [:style (str "
    body { font-family: 'Segoe UI', system-ui, sans-serif; font-size: 16px; line-height: 1.5;
           margin: 0; min-height: 100vh; padding: 2rem; box-sizing: border-box;
           background: linear-gradient(135deg, #f5f0eb 0%, #e8dfd5 50%, #ddd6ce 100%); }
    .me-wrap { max-width: 28rem; margin: 0 auto; }
    .me-card { background: #fff; border: 1px solid #d4ccc4; border-radius: 10px;
               box-shadow: 0 4px 12px rgba(0,0,0,0.06); padding: 1.75rem 2rem; }
    .me-title { font-size: 1.5rem; font-weight: 600; color: #2c2520; margin: 0 0 1rem 0; }
    .me-subtitle { font-size: 0.95rem; color: #5c534c; margin-bottom: 1.25rem; }
    .me-dl { margin: 0; padding: 0; list-style: none; }
    .me-dl .me-dt { font-size: 0.75rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.04em;
                    color: #7a7068; margin-top: 1rem; margin-bottom: 0.25rem; }
    .me-dl .me-dt:first-child { margin-top: 0; }
    .me-dl .me-dd { font-size: 1rem; color: #2c2520; margin: 0 0 0 0; word-break: break-all; }
    .me-actions { margin-top: 1.5rem; padding-top: 1.25rem; border-top: 1px solid #ebe6e1; }
    .btn { display: inline-block; padding: 0.5rem 1.25rem; font-size: 0.9rem; font-weight: 500;
           background: #3d3630; color: #fff; text-decoration: none; border-radius: 6px;
           border: none; cursor: pointer; transition: background 0.15s ease; }
    .btn:hover { background: #2c2520; }
  ")])

(defn page-authresult [{:keys [params] :as _req}]
  (let [state (get params "state" (get params :state "—"))
        scope (get params "scope" (get params :scope "—"))]
    (response/content-type
     {:status 200
      :body (page/html5
             {:mode :html}
             [:head
              [:meta {:charset "utf-8"}]
              [:title "OAuth2 authorization"]
              authresult-styles]
             [:body
              [:div.me-wrap
               [:div.me-card
                [:h1.me-title "Authorization complete"]
                [:p.me-subtitle "Token has been stored. You can close this window."]
                [:dl.me-dl
                 [:dt.me-dt "Saved as"]
                 [:dd.me-dd (str state)]
                 [:dt.me-dt "Scope"]
                 [:dd.me-dd (if (string? scope) scope (str scope))]]
                [:div.me-actions
                 [:button.btn {:type "button"
                               :onclick "window.close();"}
                  "Close window"]
                 " "
                 [:a.btn {:href "/me"} "Go to profile"]]]]])}
     "text/html")))
