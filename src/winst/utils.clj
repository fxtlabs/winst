(ns winst.utils
  "Utility functions used to prepare reports."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.def :only (defvar-)]
        [clojure.contrib.string :only (upper-case)]
        [clj-time.format :only (formatters unparse)]
        [winst.securities :only (lookup-security)]))

(defvar- date-formatter (formatters :year-month-day))

(defn format-date
  "Formats a date as yyyy-mm-dd."
  [dt]
  (unparse date-formatter dt))

(defn format-quantity
  "Formats a quantity using parenthesis for negative values."
  [f]
  (if (integer? f) (format "%(,d" f) (format "%(,.4f" (float f))))

(defn format-currency
  "Formats currency using parenthesis for negative values."
  [f]
  (format "%(,.2f" (float f)))

(defn format-percentage
  "Formats percentages (given as a fraction) using parenthesis for
   negative values."
  [f]
  (format "%(,.2f" (* 100 f)))

(defn activity-type-name
  "Returns the string corresponding to a trading activity type
   (e.g. :buy -> BUY)."
  [t]
  (upper-case (name t)))

(defn normalize-activity
  "Normalizes an activity so that it can be described in terms of securities
   added or subtracted from the holdings. A buy or sell activity
   will be represented by one entry each, but an exchange will generate two
   entries. It returns a vector of one or two entries."
  [{:keys [date type quantity security-uid] :as activity}]
  (let [security (lookup-security security-uid)]
    (case type
          :buy [{:date date
                 :type type
                 :quantity quantity
                 :security-uid security-uid
                 :description (:name security)
                 :price (/ (:cost activity) quantity)
                 :credit (- (:cost activity))}]
          :sell [{:date date
                  :type type
                  :quantity (- quantity)
                  :security-uid security-uid
                  :description (:name security)
                  :price (/ (:proceeds activity) quantity)
                  :credit (:proceeds activity)
                  }]
          :split (let [r (:split-ratio activity)
                       [to from] (if (ratio? r)
                                   [(numerator r) (denominator r)] [r 1])]
                   [{:date date
                     :type type
                     :quantity (* (- r 1) quantity)
                     :security-uid security-uid
                     :description (str "Split "
                                       (format-quantity quantity) " "
                                       (:name security) " "
                                       to " for " from)}])
          :exchange (let [{:keys [new-quantity new-security-uid]} activity
                          new-security (lookup-security new-security-uid)]
                      [{:date date
                        :type type
                        :quantity (- quantity)
                        :security-uid security-uid
                        :description (str (:name security)
                                          "; exchanged for "
                                          (format-quantity new-quantity)
                                          " " (:name new-security))}
                       {:date date
                        :type type
                        :quantity new-quantity
                        :security-uid new-security-uid
                        :description (str (:name new-security)
                                          "; exchanged for "
                                          (format-quantity quantity)
                                          " " (:name security))}])
          (throw (RuntimeException. "Unexpected activity type!")))))

