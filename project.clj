(defproject profile "0.1.0"
  :description "Online-Profile"
  :url "http://?hier-URL-eintragen?"
  :license 
  {:name "Eclipse Public License"
   :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"

  :plugins [[lein-cljsbuild "0.3.0"]]

  :cljsbuild 
  {:builds 
   [{:source-paths ["src"]
     :compiler 
     {:output-to "resources/public/js/cljs.js"
      :optimizations :whitespace
      :pretty-print true}}]}

  ;:profiles {:dev {:resource-paths ["test/resources"]}}

  :dependencies 
  [[org.clojure/clojure "1.4.0"]
   [ring "1.1.8"]                 
   ;[ring/ring-jetty-adapter "1.1.8"]
   [compojure "1.1.3"]
   [enlive "1.0.1"]
   [enfocus "1.0.0-SNAPSHOT"]
   [bouncer "0.2.3-SNAPSHOT"]
   [org.mindrot/jbcrypt "0.3m"]]

  :main profile.server)

  