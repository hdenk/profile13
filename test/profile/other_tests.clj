(ns profile.other-tests
  (:use [clojure.test :only [deftest is testing]])
  (:require [bouncer [core :as b] [validators :as v]]))

(v/defvalidatorset credential-validator
  :id (v/required :message "Identifikation darf nicht leer bleiben")
  :password (v/required :message "Passwort darf nicht leer bleiben"))

(deftest bouncer 
  (testing "validatorset"
    (is 
      (=
        '[{:password ("Passwort darf nicht leer bleiben"), :id ("Identifikation darf nicht leer bleiben")} 
         {:bouncer.core/errors {:password ("Passwort darf nicht leer bleiben"), :id ("Identifikation darf nicht leer bleiben")}}]
        (b/validate {} credential-validator)))
    (is 
      (=
        '[nil {:id "1", :password "test1"}]
        (b/validate {:id "1" :password "test1"} credential-validator)))))
