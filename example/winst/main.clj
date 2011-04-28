(ns winst.main
  "Main function for the example program."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.def :only (defvar-)]
        [clojure.contrib.command-line :only (with-command-line)]
        [ring.adapter.jetty :only (run-jetty)]
        [winst.routes :only (main-routes)])
  (:require [winst.simple-account :as simple]
            [winst.rand-account :as rand])
  (:gen-class))

(defvar- accounts
  [simple/account rand/account]
  "The list of available accounts.")

(defvar- app
  (main-routes accounts)
  "The main request handler responding to requests about the given accounts.")

(defn -main
  "The main function. You can start the web server using 'lein run [options]'."
  [& args]
  (with-command-line args
    "winst -- compute your capital gains and serve them as web pages"
    [[port p "start the server on a given port" "8087"]]
    (try
      (run-jetty #'app {:port (Integer/parseInt port 10) :join? false})
      (catch NumberFormatException e
        (binding [*out* *err*]
          (println
           (str "winst: " port ": port number must be a positive integer"))
          (System/exit -1))))))

(comment
  ;; Useful commands for running the server from within emacs.
  (defonce server (run-jetty #'app {:port 8087 :join? false}))
  (.start server)
  (.stop server)
)

