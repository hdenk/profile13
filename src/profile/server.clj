(ns profile.server
  (:use [compojure.core :only [defroutes]])
  (:require [ring.middleware.reload :as ring-reload]
            [ring.middleware.stacktrace :as ring-stacktrace]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [profile.options :as options]
            [profile.request :as request]
            [profile.session :as session]
            [profile.models :as models]
            [profile.views :as views]
            [profile.simpledb :as simpledb]))

(defmacro dbg
  "print debug-infos to console"
  [x] 
  `(let 
     [x# ~x] 
     (println "dbg:" '~x "=" x#) x#)) 

; http://mmcgrana.github.com/2010/03/clojure-web-development-ring.html
(defn wrap-development-middleware [handler]
  (if (options/dev-mode?)
    (-> handler 
        (ring-reload/wrap-reload ["src"])
        (ring-stacktrace/wrap-stacktrace))
    handler))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
      (catch Exception e
        (dbg "@error-page@")
        
            {:status 500
            :headers {"Content-Type" "text/html"}
            :body (views/render (views/error-500))}))))
        
        ;(views/render (views/error-500))))))

(defn wrap-middleware
  "Wrap a base handler in all of middleware"
  [handler opts]
  (binding [options/*options* (options/compile-options opts)]
    (-> handler
        (request/wrap-request-map)
        (session/wrap-noir-flash)
        (session/wrap-noir-session)
        (wrap-development-middleware)
        (options/wrap-options opts)
        (wrap-error-page))))

(defroutes routes
  views/routes
  (route/resources "/")
  (route/not-found (views/render (views/error-404))))

(defn gen-handler [& [opts]] 
  (-> (handler/site routes)
      (wrap-middleware opts)))

(defn start [port & [opts]]
  ;; to allow for jetty to be excluded as a dependency, it is included
  ;; here inline.
  (require 'ring.adapter.jetty)
  (println "Starting server...")
  (let [run-fn (resolve 'ring.adapter.jetty/run-jetty) ;; force runtime resolution of jetty
        jetty-opts (merge {:port port :join? false} (:jetty-options opts))
        server (run-fn (gen-handler opts) jetty-opts)]
    (println (str "Server started on port [" port "]."))
    (println (str "You can view the site at http://localhost:" port))
    server))

(defn -main [& m]
  ; init db
  (simpledb/init! models/dbcontent)

  ; start server
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (start port {:mode mode :ns "profile"})))

;; For interactive development:
;; (.stop server)
;; (def server (-main))
