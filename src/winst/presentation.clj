(ns winst.presentation
  "The presentation layer of the winst server. It renders HTML reports
   for the trading activity, holdings, and realized gains/losses of an
   account over a specified period of time."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.def :only (defvar-)]
        hiccup.core
        hiccup.page-helpers
        [clj-time.core :only (now minus millis start end year month)]
        [winst.currency :only (currency-name currency-keyword)]
        [winst.securities :only (security-qualified-symbol lookup-security)]
        [winst.utils :only
         (normalize-activity
          activity-type-name
          format-date format-quantity format-currency format-percentage)]))

(defn- account-inception
  "Returns the date of inception of a given account. It assumes the account
   activities are sorted chronologically and returns the date of the first
   (or today's date if there are no activities)."
  [account]
  (if-let [first-activity (first (:activities account))]
    (:date first-activity)
    (now)))

(defn- page-head
  "Returns the <head> element used by all pages."
  [{:keys [head] :as slots}]
  [:head
   [:title "Winst"]
   ;[:link {:rel "shortcut icon" :type "image/x-icon" :href "/images/favicon.ico"}]
   (include-css "/style.css")
   ;(include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js")
   
   head])

(defn- page-header
  "Returns the <header> element used by all pages."
  [{:keys [title] :as slots}]
  [:header
   [:h1 title]])

(defn- page-footer
  "Returns the <footer> element used by all pages."
  [slots]
  [:footer])

(defn- page-body
  "Returns a <body> element with contents generated by the content
   function corresponding to the :content key in the given slots map."
  [{:keys [content] :as slots}]
  [:body
   [:div#main.container
    (page-header slots)
    (content slots)]
   (page-footer slots)])

(defn- render-page-template
  "Renders a page for the given slots map."
  [slots]
  (html5
   (page-head slots)
   (page-body slots)))

(defn- render-simple-page
  "Renders a simple page with a given tag, title, and text blurb (one
   paragraph)."
  [tag title blurb]
  (letfn [(content [{:keys [blurb]}]
                   [:div#content
                    [:p blurb]])]
    (render-page-template {:tag tag
                           :content content
                           :title title
                           :blurb blurb})))


(defn render-not-found
  "Renders a 404 error (not found) page."
  []
  (render-simple-page :error
                      "404 Not Found"
                      "Sorry, the page you trying to view cannot be found."))

(defn render-error
  "Renders an error page for the given account and error message."
  [_ msg]
  (render-simple-page :error "Error" msg))


(defn- report-header
  "Returns the <header> element used by all report pages."
  [{url-builder :url-builder,
    current-report-type :report, current-currency :currency}]
  [:header
   [:h1 "Winst"]
   [:div.reports
    [:ul
     (for [[report-type report-name] [[:holdings "Holdings"]
                                      [:activities "Activity"]
                                      [:gains "Gain/Loss"]]]
       (if (= report-type current-report-type)
         [:li.selected report-name]
         [:li (link-to (url-builder {:report report-type}) report-name)]))]]
   [:div.currencies
    [:ul
     (let [current-currency (if (nil? current-currency)
                              current-currency
                              (currency-keyword current-currency))]
       (for [[currency label] [[nil "Account"]
                               [:usd "USD"]
                               [:cad "CAD"]]]
         (if (= currency current-currency)
           [:li.selected label]
           [:li (link-to (url-builder {:currency currency}) label)])))]]])

(defn- report-accounts
  "Returns the <div> element used to navigate among the available accounts
   for the current report type, reporting period and currency."
  [{url-builder :url-builder, current-account :account, accounts :accounts}]
  [:div.accounts
   [:ul
    [:li.header "Accounts"]
    (for [[_ account] accounts]
      (if (= (:tag account) (:tag current-account))
        [:li.selected (:name account)]
        [:li (link-to (url-builder {:account (:tag account)})
                      (:name account))]))]])

(defn- format-month
  "Returns the given month as a two-digit string."
  [month]
  (format "%02d" month))

(defn- report-year
  "Returns the elements used to navigate to the reports for the given
   year or available months within that year for the current account,
   report type, and currency."
  [{url-builder :url-builder, account :account,
    current-year :year, current-month :month} yr]
  (let [inception (account-inception account)
        today (now)
        start-month (if (= yr (year inception)) (month inception) 1)
        end-month (if (= yr (year today)) (month today) 12)]
    (list
     (if (and (= yr current-year) (nil? current-month))
       [:li.selected yr]
       [:li (link-to (url-builder {:year yr}) yr)])
     [:li.months
      [:ul
       (for [month (range start-month (inc end-month))]
         (if (and (= yr current-year) (= month current-month))
           [:li.selected (format-month month)]
           [:li (link-to (url-builder {:year yr, :month month})
                         (format-month month))]))]])))

(defn- report-periods
  "Returns the <div> element used to navigate to the reports for all
   available periods for the current account, report type, and currency."
  [{url-builder :url-builder, account :account,
    current-report :report, current-year :year, current-month :month,
    :as navigation}]
  [:div.periods
   [:ul
    [:li.header "Reporting Periods"]
    (let [label (if (= current-report :holdings)
                  "Current day"
                  "Inception - Current day")]
      (if (or current-year current-month)
        [:li (link-to (url-builder {:year nil, :month nil}) label)]
        [:li.selected label]))
    (for [y (range (year (account-inception account)) (inc (year (now))))]
      (report-year navigation y))]])

(defn- account-info
  "Returns the <div> element containing the info for the given account."
  [account]
  [:div.account-info
   [:span (str (:name account) " - "
               (name (:type account)) " - "
               (currency-name (:currency account)))]
   [:br]
   [:span.account-holder (:holder account)]])

(defn- report-info
  "Returns the <div> element containing the info for the required report
   (e.g. date printed and reporting currency)."
  [currency]
  [:div.report-info
   [:span (str "Reporting currency: " (currency-name currency))]
   [:br]
   [:span (str "Printed " (format-date (now)))]])

(defn- report-content
  "Returns the bare report exclusive of header, footer, and
   navigation elements."
  [title account report-currency caption table]
  [:div#content
   [:h1 title]
   (account-info account)
   [:br]
   (report-info report-currency)
   [:h2 caption]
   table])

(defn- render-report
  "Renders a report page with the given pieces."
  [navigation title account report-currency caption table]
  (html5
   (page-head {:title (str "Winst - " title)})
   [:body
    [:div#main.container
     (report-header navigation)
     [:div#sidebar
      (report-accounts navigation)
      (report-periods navigation)]
     (report-content title account report-currency caption table)]
    (page-footer nil)]))

(defn render-holdings
  "Renders a report showing the holdings for the given account at the given
   time and in the given reporting currency."
  [navigation account report-currency report-time holdings]
  (let [title "Account Holdings"
        caption (str "Account Holdings as of "
                     (format-date (minus report-time (millis 1))))
        table [:table
               [:tr
                [:th.number "Quantity"]
                [:th "Symbol"]
                [:th "Description"]
                [:th.number "Book Value"]]
               (for [[security-uid holding] holdings]
                 (let [{:keys [quantity cost]} holding
                       security (lookup-security security-uid)]
                   [:tr
                    [:td.number (format-quantity quantity)]
                    [:td (security-qualified-symbol security)]
                    [:td (:name security)]
                    [:td.number (format-currency cost)]]))
               [:tr
                [:th ""]
                [:th ""]
                [:th ""]
                [:th.number
                 (format-currency (reduce #(+ %1 (:cost %2)) 0 (vals holdings)))]]]]
    (render-report navigation title account report-currency caption table)))

(defn render-gains
  "Renders a report showing the realized gain/loss for the given account over
   the given time interval and in the given reporting currency."
  [navigation account report-currency report-interval gains]
  (let [title "Realized Gain Loss"
        caption (str "Realized Gain Loss from "
                     (format-date (start report-interval))
                     " to "
                     (format-date (minus (end report-interval)
                                         (millis 1))))
        table [:table
               [:tr
                [:th.date "Transaction Date"]
                [:th.activity "Transaction Type"]
                [:th "Symbol"]
                [:th "Description"]
                [:th.number "Quantity"]
                [:th.number "Average Cost"]
                [:th.number "Book Value"]
                [:th.number "Transaction Price"]
                [:th.number "Net Amount"]
                [:th.number "Realized Gain/Loss"]
                [:th.number "Percentage Gain/Loss"]]
               (for [gain gains]
                 (let [{:keys [date security-uid quantity cost proceeds]} gain
                       security (lookup-security security-uid)]
                   [:tr
                    [:td.date (format-date date)]
                    [:td.activity "SELL"]
                    [:td (security-qualified-symbol security)]
                    [:td (:name security)]
                    [:td.number (format-quantity (- quantity))]
                    [:td.number (format-currency (/ cost quantity))]
                    [:td.number (format-currency cost)]
                    [:td.number (format-currency (/ proceeds quantity))]
                    [:td.number (format-currency proceeds)]
                    [:td.number (format-currency (- proceeds cost))]
                    [:td.number (format-percentage (/ (- proceeds cost) cost))]]))
               [:tr
                [:th ""]
                [:th ""]
                [:th ""]
                [:th "Total"]
                [:th ""]
                [:th ""]
                [:th.number
                 (format-currency (reduce #(+ %1 (:cost %2)) 0 gains))]
                [:th ""]
                [:th.number
                 (format-currency (reduce #(+ %1 (:proceeds %2)) 0 gains))]
                [:th.number
                 (format-currency (reduce #(+ %1 (- (:proceeds %2) (:cost %2))) 0 gains))]
                [:th ""]]]]
    (render-report navigation title account report-currency caption table)))

(defn render-activities
  "Renders a report showing the trading activity for the given account over
   the given time interval and in the given reporting currency."
  [navigation account report-currency report-interval activities]
  (let [title "Trading Activity"
        caption (str "Trading Activity from "
                     (format-date (start report-interval))
                     " to "
                     (format-date (minus (end report-interval)
                                         (millis 1))))
        table [:table
               [:tr
                [:th.date "Date"]
                [:th.activity "Activity"]
                [:th.number "Quantity"]
                [:th "Symbol"]
                [:th "Security Description"]
                [:th.number "Price"]
                [:th.number "Credit/(Debit)"]
                ]
               (for [item (mapcat normalize-activity activities)]
                 (let [{:keys [date type quantity security-uid
                               description price credit]} item
                       security (lookup-security security-uid)]
                   [:tr
                    [:td.date (format-date date)]
                    [:td.activity (activity-type-name type)]
                    [:td.number (format-quantity quantity)]
                    [:td (security-qualified-symbol security)]
                    [:td description]
                    [:td.number (if price (format-currency price) "")]
                    [:td.number (if credit (format-currency credit) "")]]))
               ]]
    (render-report navigation title account report-currency caption table)))
