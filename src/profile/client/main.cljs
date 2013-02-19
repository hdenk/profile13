(ns profile.client.main
  (:require [enfocus.core :as ef])
  (:require-macros [enfocus.macros :as em]))

(em/defsnippet zeugnis-zeppelin "/templates/zeugnis-zeppelin.html" ["div.content > *"] []) 
(em/defsnippet zeugnis-gao "/templates/zeugnis-gao.html" ["div.content > *"] [])
(em/defsnippet zeugnis-mbb "/templates/zeugnis-mbb.html" ["div.content > *"] []) 
(em/defsnippet zeugnis-bmw "/templates/zeugnis-bmw.html" ["div.content > *"] []) 
(em/defsnippet zeugnis-fh "/templates/zeugnis-fh.html" ["div.content > *"] []) 
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
  ["#fh"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-fh)))))             

(em/at js/document
  ["#jtg"] (em/listen :click #(em/at js/document 
                                    ["#zeugnis-info"] (em/content (zeugnis-jtg)))))