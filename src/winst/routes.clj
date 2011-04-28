(ns winst.routes
  "Maps URLs to request handlers to report trading activity, holdings,
   and realized gains/losses for an account over a specified period of time."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.def :only (defvar-)]
        [compojure.core :only (routes GET)]
        [compojure.route :only (resources not-found)]
        [compojure.handler :only (site)]
        [hiccup.middleware :only (wrap-base-url)]
        [winst.handlers :only
         (holdings-handler activities-handler gains-handler)]
        [winst.presentation :only (render-not-found)]))

(defvar- re-account #"[a-zA-Z][a-zA-Z0-9]*"
  "The regular expression used to describe valid account names in a URL.")

(defvar- re-year #"20[0-9]{2}"
  "The regular expression used to describe valid years in a URL.")

(defvar- re-month #"0?[1-9]|1[012]"
  "The regular expression used to describe valid months (1 to 12) in a URL.")

(defn main-routes
  "Returns a handler that can respond to requests related to the given
   sequence of accounts.
   An account is a map with the following keywords:
   :tag         A keyword corresponding to the name used to identify the
                account in a URL.
   :name        The name of the account as it should appear on the reports.
   :description A description of the account.
   :holder      The name of the account holder as it should appear on the reports.
   :type        The type of the account (as a keyword). For example, :cash or
                :margin.
   :currency    The currency used for the account (as a keyword). Currently,
                only :usd (USD) and :cad (CAD) are supported.
   :activities  The sequence of trading activities for the account. See
                winst.dsl/activity for details.
   The URLs this handler responds to must be of the form
   '/accounts/<ACCOUNT>/<ASPECT>/<YEAR>/<MONTH>' where:
   <ACCOUNT>    The short name for the account (see :tag key above).
   <ASPECT>     One of 'holdings', 'activities', or 'gains'.
   <YEAR>       The year for which the report is desired. If omitted, the
                report will be generated for the current date (since account
                inception for activities and realized gains). If present,
                <MONTH> part must also be present.
   <MONTH>      The month (1 to 12) for which the report is desired. If
                omitted, the report will be for a whole year or for the
                lifespan of the account (see <YEAR>).
   The optional query parameter 'currency' can be added to the URL to
   specify the currency to be used for the report. Possible values are USD
   or CAD."
  [accounts]
  (let [accounts (apply hash-map (mapcat (fn [a] [(:tag a) a]) accounts))
        handle-holdings (holdings-handler accounts)
        handle-activities (activities-handler accounts)
        handle-gains (gains-handler accounts)
        rts (routes
             (GET ["/accounts/:account/holdings/:year/:month"
                   :account re-account :year re-year :month re-month] _
                  handle-holdings)

             (GET ["/accounts/:account/holdings/:year"
                   :account re-account :year re-year] _ handle-holdings)

             (GET ["/accounts/:account/holdings" :account re-account] _
                  handle-holdings)

             (GET ["/accounts/:account/activities/:year/:month"
                   :account re-account :year re-year :month re-month] _
                  handle-activities)

             (GET ["/accounts/:account/activities/:year"
                   :account re-account :year re-year] _ handle-activities)

             (GET ["/accounts/:account/activities" :account re-account] _
                  handle-activities)

             (GET ["/accounts/:account/gains/:year/:month"
                   :account re-account :year re-year :month re-month] _
                  handle-gains)

             (GET ["/accounts/:account/gains/:year"
                   :account re-account :year re-year] _ handle-gains)

             (GET ["/accounts/:account/gains" :account re-account] _
                  handle-gains)

             (resources "/")

             (not-found (render-not-found)))]
    (-> rts site wrap-base-url)))

