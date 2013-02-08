(ns profile.models
  (:require [profile.simpledb :as simpledb]))

(defmacro dbg
  "print debug-infos to console"
  [x] 
  `(let 
     [x# ~x] 
     (println "dbg:" '~x "=" x#) x#)) 

(def dbcontent 
  {:user 
   {1 {:id "name1@noc.de" :email "name1@noc.de" :password "test1" :name "name1" :comment "comment name1"}
    2 {:id "name2@noc.de" :email "name2@noc.de" :password "test2" :name "name2" :comment "comment name2"}
    3 {:id "admin@noc.de" :email "admin@noc.de" :password "secret" :name "admin" :comment "comment admin"}}})

(defn- reduce-by-id [id]
  (fn [found key]
    (if-not found
      (let [user (simpledb/get-in :user [key])]
        (when (= (:id user) id)
          user))
      found)))
      
(defn find-user-by-id [id] 
  (dbg (str "find-user-by-id" id))
  (reduce (reduce-by-id id) nil (simpledb/keys :user)))

(defn login [credentials]
  (if-let [user (find-user-by-id (:id credentials))]
    (when (= (:password user) (:password credentials)) ; TODO use encrypted !
      user)))    
