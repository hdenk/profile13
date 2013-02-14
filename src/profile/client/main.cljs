(ns profile.client.main
  (:require [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em]))

(em/defsnippet zeugnis-zeppelin "/templates/zeugnis-zeppelin.html" ["div.content > *"] []) 
(em/defsnippet zeugnis-gao "/templates/zeugnis-gao.html" ["div.content > *"] [])
(em/defsnippet zeugnis-mbb "/templates/zeugnis-mbb.html" ["div.content > *"] []) 
(em/defsnippet zeugnis-bmw "/templates/zeugnis-bmw.html" ["div.content > *"] []) 
(em/defsnippet zeugnis-fhnoten "/templates/zeugnis-fhnoten.html" ["div.content > *"] []) 
(em/defsnippet zeugnis-vordiplom "/templates/zeugnis-vordiplom.html" ["div.content > *"] []) 
(em/defsnippet zeugnis-jtg "/templates/zeugnis-jtg.html" ["div.content > *"] []) 

(em/at js/document
  ["#zeppelin"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-zeppelin)))))             

(em/at js/document
  ["#gao"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-gao)))))             

(em/at js/document
  ["#mbb"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-mbb)))))             

(em/at js/document
  ["#bmw"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-bmw)))))             

(em/at js/document
  ["#fhnoten"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-fhnoten)))))             

(em/at js/document
  ["#vordiplom"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-vordiplom)))))             

(em/at js/document
  ["#jtg"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-jtg)))))