(ns profile.client.main
	(:require [jayq.core :as jq]
            [fetch.remotes :as remotes]))

(defn delete-account-callback [data]
  (js/window.location.replace "/"))

(defn delete-user-callback [data]
  (case data
        0 (js/alert "The user was NOT deleted")
        1 (js/window.location.reload)
        (js/alert (str "Unknown response: " data))))

(def callbacks {:delete-account-callback delete-account-callback
                :delete-user-callback delete-user-callback})

(jq/bind 
    (jq/$ "a[data-confirm]")
    :click
    (fn [e]
      (.preventDefault e)
      (this-as me
        (let [$me (jq/$ me)
              confirm (jq/data $me :confirm)
              action (jq/data $me :action)
              params (jq/data $me :params)
              params (if (= params js/undefined) nil [params])
              callback (jq/data $me :callback)]
          (when (js/confirm confirm)
                (remotes/remote-callback action
                                         params
                                         ((keyword callback) callbacks)))))))

(jq/delegate
    (jq/$ js/document)
    "a[data-method=\"post\"]"
    :click
    (fn [e]
      (this-as me
        (let [$link (jq/$ me)
              $form (jq/$ (str "<form method=\"post\" action=\""
                               (.attr $link "href")
                               "\"></form>"))]
          (-> (.hide $form) (.appendTo "body"))
          (.submit $form)
          false))))

