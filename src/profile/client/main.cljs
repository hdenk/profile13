(ns profile.client.main
  (:require [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em]))

(defn start [] 
  (em/at js/document
    ["#clojurescript"] (em/content "Hello world!")))

(set! (.-onload js/window) start)