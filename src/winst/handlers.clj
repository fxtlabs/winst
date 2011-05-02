(ns winst.handlers
  "Request handlers for the trading activity, holdings, and realized
   gains/losses of an account over a specified period of time."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.string :only (lower-case)]
        [winst.core :only (convert-activities
                           dated-before? dated-within?
                           compute-holdings compute-realized-gains)]
        [winst.presentation :only
         (render-error render-holdings render-gains render-activities)]
        [winst.currency :only
         (get-exchange-rate-lookup currency-keyword currency-name)]
        [winst.utils :only (format-date)])
  (:require [clj-time.core :as ct]))

(defn- parse-int
  "Parses a string and returns a non-negative integer.
   It assumes the given string only contains digits."
  [s]
  (when s (Integer/parseInt s 10)))

(defn- get-navigation-data
  "Returns a map with all the info necessary to build navigation menus
   for all report pages."
  [request report]
  {:url-builder (get request :url-builder)
   :accounts (get request :accounts)
   :account (get request :account)
   :report report
   :year (parse-int (get-in request [:route-params :year]))
   :month (parse-int (get-in request [:route-params :month]))
   :currency (get-in request [:query-params "currency"])})


(defn- wrap-account
  "Middleware that fetches the account specified in the request.
   The short name of the account must be provided as the value of the
   :account key in the :route-params of the request. This middleware will
   look up the account in the provided accounts map and pass it to the
   wrapped handler as the value of the :account key in the request map."
  [handler accounts]
  (fn [request]
    (if-let [account-name (get-in request [:route-params :account])]
      (if-let [account (get accounts (keyword (lower-case account-name)))]
        (handler (assoc request :account account, :accounts accounts))
        (render-error nil (str "Account " account-name " does not exist!")))
      (render-error nil "URL is missing account name!"))))


(defn- get-report-currency
  "Returns the currency keyword corresponding to the given currency name.
   If the given currency name is nil, it falls back to the currency of the
   given account."
  [account cname]
  (if cname
    (currency-keyword cname)
    (:currency account)))

(defn- wrap-report-currency
  "Middleware that handles the reporting currency specified by the
   'currency' query parameter. It verifies that an exchange rate
   lookup function from the account currency to the reporting currency
   exists; otherwise it renders an error message.
   If no currency was specified, it uses the account currency.
   It adds the following keys to the request map:
     :report-currency - a currency keyword;
     :exchange-rate-lookup - an exchange rate lookup function."
  [handler]
  (fn [request]
    (let [account (:account request)
          report-currency-name (get-in request [:query-params "currency"])
          report-currency (get-report-currency account report-currency-name)
          exchange-rate-lookup (get-exchange-rate-lookup (:currency account)
                                                         report-currency)
          request (assoc request :report-currency report-currency
                         :exchange-rate-lookup exchange-rate-lookup)]
      (if exchange-rate-lookup
        (handler request)
        (render-error account
                      (str "No " (currency-name (:currency account)) " to "
                           (currency-name report-currency)
                           " exchange rate data available"))))))


(defn- year-month-str [year month]
  (if month (str year "/" month) year))

(defn- build-time
  ([] (ct/now))
  ([year]
     (let [now-dt (ct/now)
           dt (ct/date-time (inc year))]
       (cond (ct/before? dt now-dt) dt
             (= year (ct/year now-dt)) now-dt
             :else nil)))
  ([year month]
     (let [now-dt (ct/now)
           dt (ct/plus (ct/date-time year month) (ct/months 1))]
       (cond (ct/before? dt now-dt) dt
             (and (= year (ct/year now-dt)) (= month (ct/month now-dt))) now-dt
             :else nil))))

(defn- wrap-report-time
  "Middleware that handles the reporting time specified in the request
   (optional :year and :month keys in the :route-params map).
   The reporting time is computed as follows: if both year and month
   are specified, it is the end of the month; only year means end of
   the year; nothing means now. The current year and current month are
   clamped to now; other times in the future will return nil.
   It adds a :report-time key with a org.joda.time.DateTime object
   to the request."
  [handler]
  (fn [request]
    (let [{:keys [year month]} (:route-params request)
          report-time (cond month (build-time (parse-int year) (parse-int month))
                            year (build-time (parse-int year))
                            :else (build-time))]
      (if report-time
        (let [request (assoc request :report-time report-time)]
          (handler request))
        (render-error (:account request)
                      (str "Report time (" (year-month-str year month) ")"
                           " cannot be in the future."))))))



(defn- earlier-time
  "Returns the earlier of the two given times."
  [dt1 dt2]
  (if (ct/before? dt1 dt2) dt1 dt2))

(defn- build-interval
  ([account] (let [start-dt (if-let [first-activity (first (:activities account))]
                              (:date first-activity)
                              (ct/date-time 2000))
                   end-dt (ct/now)]
               (ct/interval start-dt end-dt)))
  ([account year] (build-interval account year 1 12))
  ([account year month] (build-interval account year month 1))
  ([_ year month n]
     (let [start-dt (ct/date-time year month)
           end-dt (earlier-time (ct/plus start-dt (ct/months n)) (ct/now))]
       (ct/interval start-dt end-dt))))

(defn- wrap-report-interval
  "Middleware that handles the reporting interval specified in the request
   (optional :year and :month keys in the :route-params map).
   The reporting interval is computed as follows: if both year and month
   are specified, it is the full month; only year means the full year;
   nothing means from account inception until now. The current year and
   current month are clamped to now; other intervals in the future will
   return nil.
   It adds a :report-interval key with a org.joda.time.Interval object
   to the request."
  [handler]
  (fn [request]
    (let [{:keys [year month]} (:route-params request)
          account (:account request)]
      (try
        (let [report-interval (cond month (build-interval account
                                                          (parse-int year)
                                                          (parse-int month))
                                    year (build-interval account
                                                         (parse-int year))
                                    :else (build-interval account))
              request (assoc request :report-interval report-interval)]
          (handler request))
        (catch IllegalArgumentException e
          (render-error (:account request)
                        (str "Report interval (" (year-month-str year month) ")"
                             " cannot be in the future.")))))))


(defn- handle-holdings
  "Handler for a request for account holdings.
   It computes the holdings for a given account, reporting currency
   and time and produces a report.
   It expect to find its parameters in the request map under the
   :account :report-currency, :exchange-rate-lookup, and :report-time keys."
  [{:keys [account report-currency
           exchange-rate-lookup report-time] :as request}]
  (let [activities (->> account :activities
                        (take-while (partial dated-before? report-time))
                        (convert-activities exchange-rate-lookup))
        holdings (compute-holdings activities)]
    (render-holdings (get-navigation-data request :holdings)
                     account report-currency report-time holdings)))

(defn holdings-handler
  "Returns a handler capable of responding to holdings requests for the
   given account."
  [accounts]
  (-> handle-holdings
      wrap-report-time wrap-report-currency (wrap-account accounts)))


(defn- handle-activities
  "Handler for a request for account activities.
   It computes the activities for a given account, reporting currency
   and interval and produces a report.
   It expect to find its parameters in the request map under the
   :account :report-currency, :exchange-rate-lookup, and :report-interval keys."
  [{:keys [account report-currency
           exchange-rate-lookup report-interval] :as request}]
  (let [activities (->> account :activities
                        (filter (partial dated-within? report-interval))
                        (convert-activities exchange-rate-lookup))]
    (render-activities (get-navigation-data request :activities)
                       account report-currency report-interval activities)))

(defn activities-handler
  "Returns a handler capable of responding to activity requests for
   the given account."
  [accounts]
  (-> handle-activities
      wrap-report-interval wrap-report-currency (wrap-account accounts)))


(defn- handle-gains
  "Handler for a request for account gains/losses.
   It computes the realized gains/losses for a given account,
   reporting currency and interval and produces a report.
   It expect to find its parameters in the request map under the
   :account :report-currency, :exchange-rate-lookup, and :report-interval keys."
  [{:keys [account report-currency
           exchange-rate-lookup report-interval] :as request}]
  (let [activities (->> account :activities
                        (take-while
                         (partial dated-before? (ct/end report-interval)))
                        (convert-activities exchange-rate-lookup))
        gains (->> activities
                   compute-realized-gains
                   (drop-while
                    (partial dated-before? (ct/start report-interval))))]
    (render-gains (get-navigation-data request :gains)
                  account report-currency report-interval gains)))

(defn gains-handler
  "Returns a handler capable of responding to realized gain/loss requests
   for the given account."
  [accounts]
  (-> handle-gains
      wrap-report-interval wrap-report-currency (wrap-account accounts)))

