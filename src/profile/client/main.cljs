(ns profile.client.main
  (:require [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em]))

(em/defsnippet zeugnis-bmw "/templates/zeugnis-bmw.html" ["div.content"] []) 
(em/defsnippet zeugnis-gao "/templates/zeugnis-gao.html" ["div.content"] []) 

(em/at js/document
  ["#bmw"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-bmw)))))             

(em/at js/document
  ["#gao"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-gao)))))             
