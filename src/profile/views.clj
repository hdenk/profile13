(ns profile.views
  (:use [compojure.core :only [defroutes GET POST]])
  (:require [ring.util.response :as response]
            [net.cgrand.enlive-html :as h]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [profile.options :as options] ; TODO ? entfernen ?
            [profile.request :as request]
            [profile.session :as session]
            [profile.models :as models]))

(defmacro dbg
  "print debug-infos to console"
  [x] 
  `(let 
     [x# ~x] 
     (println "dbg:" '~x "=" x#) x#)) 

(declare store-location)

;;
;; security
;;
  
(defmacro secure-if [pred & body]
  `(if (~pred) 
     (do ~@body) 
     (do (store-location) (response/redirect "/login"))))

(defn admin? []
  (session/get :admin))

(defn logged-in? []
  (session/get :user-id))

#_(defn ensure-logged-in []
  (when-not (session/get :user-id)
      (store-location)
      (response/redirect "/login")))

(defn current-user []
  (models/find-user-by-id (session/get :user-id)))
  
(defn base-url []
  (let [r (request/ring-request)
        s (name (r :scheme))
        p (cond (and (= s "http") (= (r :server-port) 80)) ""
                (and (= s "https") (= (r :server-port) 443)) ""
                :else (str ":" (r :server-port)))]
    (str (name (r :scheme)) "://" (r :server-name) p)))

(defn store-location []
  (let [r (request/ring-request)
        query-string (r :query-string)]
    (session/put! :return-to 
                  (str (base-url) (r :uri)
                       (when query-string (str "?" query-string))))))

(defn redirect-back-or-default [default]
  (let [return-to (session/get :return-to)]
    (session/remove! :return-to)
    (response/redirect (or return-to default))))

(defn save-user-info-in-session [{:keys [name email]}]
  (when (= name "admin") 
        (session/put! :admin true))
  (session/put! :user-id email))

;;
;; forms
;;

(defn set-field-value-from-model
  "Returns a transformation function to be used with Enlive to set the value
  of an HTML field element to the corresponding value of the specified model."
  [model]
  (fn [node]
    (let [field-name (get-in node [:attrs :name])]
      ((h/set-attr :value (get model (keyword field-name))) node))))

;;
;; layout
;;

(defn render
  [input]
  (apply str (h/emit* input)))

(h/deftemplate layout "profile/templates/layout.html"
  [{:keys [title style navigation content footer]}]
	 [:title] (h/content title)

	 [:style#inline] (h/substitute style)

     ; convert relative paths for css-links to absolute so that they work  
     ; for any page, whatever its URL path is.
     [[:link (h/attr= :rel "stylesheet")]]
       (fn [node]
         ((h/set-attr :href (str "/" (get-in node [:attrs :href]))) node))

     ; ditto for js-links
     [[:script (h/attr= :type "text/javascript")]]
       (fn [node]
         ((h/set-attr :src (str "/" (get-in node [:attrs :src]))) node))

	 [:navigation] (when navigation (h/substitute navigation))

	 [:content] (h/substitute content)

	 [:footer] (h/content footer)) 

(h/defsnippet navigation "profile/templates/navigation.html" [:navigation :> :*]
  [page-id]
  [(keyword (str "a#" page-id))] (h/add-class "active")
  [:span#userid] (h/content (:name (current-user))))

(h/defsnippet footer "profile/templates/footer.html" [:footer]
  [])

;;
;; content-page
;;

(defn content-page [{:keys [page-id] :as params}]  
  (let [content (h/html-resource (str "profile/templates/" page-id ".html"))] 
	  (layout {:title (h/select content [:title :> h/text-node])
             :style (h/select content [:style])
	           :navigation (navigation page-id)
	           :content (h/select content [:div#content :> :*])
	           :footer (footer)})))

;;
;; login
;;

(defn login-content [credentials]
	  (let [login (h/html-resource "profile/templates/login.html")] 
	    (h/at login
         [:#error-message] (h/clone-for [[field message-list] (:bouncer.core/errors credentials)]
                             (h/html-content (apply str message-list)))
         [[:input (h/attr= :type "text")]] (set-field-value-from-model credentials))))

(defn login-page [& [credentials]]  
	(let [login (login-content credentials)] 
	  (layout {:title (h/select login [:title :> h/text-node])
	           :style (h/select login [:style])
	           :navigation nil
	           :content (h/select login [:div#content :> :*])
	           :footer nil})))

(v/defvalidator login-validator
  {:default-message-format "Leider keine gültige Anmeldung"}
  [_ credentials]
  (models/login credentials))

(v/defvalidatorset credential-validator
  :id (v/required :message "Identifikation darf nicht leer bleiben")
  :password (v/required :message "Passwort darf nicht leer bleiben"))

(defn login [credentials]
  (let [[v-result v-credentials] (b/validate credentials credential-validator)
         user (when (nil? v-result) (models/login credentials))]
    (if user
      (do
        (save-user-info-in-session user)
        (redirect-back-or-default "/content/intro"))
      (let [l-credentials (if (nil? v-result) 
                            (assoc-in v-credentials [:bouncer.core/errors :login] '("Leider keine gültige Anmeldung"))
                            v-credentials)]
            (login-page l-credentials)))))

; The reason why logouts are handled through HTTP POST instead of GET is to
; avoid that someone could log out a user by having him load a page containing
; an image tag like
;   <img src="http://example.com/logout" />
; http://stackoverflow.com/a/3522013/974795
(defn logout []
  (session/clear!)
  (response/redirect "/login"))

;;
;; error
;;

(defn error [{:keys [message]}]
    (let [error (h/html-resource "profile/templates/error.html")] 
      (h/at error 
           [:message] (h/content message))))

(defn error-404 []
  (error 
    {:message 
    "Die angeforderte Seite konnte nicht gefunden werden"}))

(defn error-500 []
  (error 
    {:message 
    "Leider ist ein Problem aufgetreten. Bitte versuchen sie es erneut. Falls das 
    Problem dauerhaft besteht, kontaktieren sie helmut.denk@gmx.de. Sorry und 
    vielen Dank für ihr Verständnis."}))

;;
;; routes
;;

(defroutes routes
  (GET "/" [] (response/redirect "/content/intro"))
  (GET "/login" [] (login-page))
  (POST "/login" [& credentials] (login credentials)) 
  (POST "/logout" [] (logout))
  (GET "/content/:page-id" [page-id] (secure-if logged-in? (content-page {:page-id page-id})))
  (GET "/admin" [] (secure-if admin? "admin"))
  (GET "/options" [] (str "mode: " (options/get :mode)))
  (GET "/throw" [] (throw (Exception. "Exception was thrown ..."))))
