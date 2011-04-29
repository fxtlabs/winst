(defproject com.fxtlabs/winst "1.0.0-SNAPSHOT"
  :description "Winst=gain. Track your trading activities, holdings, and realized gains in multiple currencies."
  :url "https://github.com/fxtlabs/winst"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [clj-time "0.3.0-SNAPSHOT"]
                 [clojure-csv "1.2.4"]
                 [hiccup "0.3.4"]
                 [ring "0.3.7"]
                 [compojure "0.6.2"]]
  :dev-dependencies 
  [[com.fxtlabs/autodoc "0.8.0-SNAPSHOT"
    :exclusions [org.clojure/clojure org.clojure/clojure-contrib]]]
  :dev-resources-path "example"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :autodoc 
    {:name "Winst"
     :description "Track your trading activities, holdings, and realized gains in multiple currencies."
     :copyright "Copyright 2011 Filippo Tampieri"
     :root "."
     :source-path "src"
     :web-src-dir "https://github.com/fxtlabs/winst/blob/"
     :web-home "http://winst.fxtlabs.com"
     :output-path "autodoc"
     :namespaces-to-document ["winst"]
     :load-classpath ["./src"] ; needed because of bug in autodoc 0.8.0-SNAPSHOT
     :load-except-list [#"/test/" #"project.clj"]})

