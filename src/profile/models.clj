(ns profile.models
  (:require [profile.simpledb :as simpledb]
            [profile.crypt :as crypt]))

(defmacro dbg
  "print debug-infos to console"
  [x] 
  `(let 
     [x# ~x] 
     (println "dbg:" '~x "=" x#) x#)) 

(def dbcontent
  {:user 
   {1 {:id "name1@noc.de" :email "name1@noc.de" :name "name1" :comment "comment name1" :password "$2a$12$TGl2FP/k3hLnlhYH.198q.IuqibqE2EHo2pwTjgiys23K4bCVu.Ti"} ; test1
    2 {:id "name2@noc.de" :email "name2@noc.de" :name "name2" :comment "comment name2" :password "$2a$12$B.Lmz4uCROJRPSAuWugrm.3PErKfFN59FQtRQ6GBO4r2rbp6TbH/2"} ; test2
    3 {:id "admin@noc.de" :email "admin@noc.de" :name "admin" :comment "comment admin" :password "$2a$12$7qKyln6.X2Spz379RWAA5.Ce7N0/kbBZibtOfdwxyfR3n64ehmonO"}}}) ; secret

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
    (when (crypt/compare (:password credentials) (:password user)) 
      user)))