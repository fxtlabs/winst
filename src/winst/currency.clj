(ns winst.currency
  "Services for working with different currencies, including parsing
   exchange rate CSV files and finding out the exchange rate for a
   given day."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.java.io :only (reader resource)]
        [clojure.contrib.def :only (defvar-)]
        [clojure.contrib.string :only (lower-case upper-case)]
        [clj-time.core :only (date-time year month day before?)]
        clojure.test))

(defvar- currency-pairs
  [[:usd :cad]]
  "The list of all currency pairs for which exchange rates are available.")

(defvar- re-line
  #"([12][0-9]{3})/(0?[1-9]|1[012])/([0-9]{1,2}),([0-9]+\.[0-9]*).*"
  "The regular expression used to match one date-rate pair in the
   exchange rate CSV files. Those files must have one date-rate pair per
   line and it must be in the form 'yyyy/mm/dd,rate'. Trailing commas are
   tolerated. Lines not matching this format are discarded.")

(defn- valid-record?
  "A predicate that returns true is the regular expression matches
   corresponding to one line of the exchange rate CSV files are successful.
   The test is very basic and only checks that all the expected capturing
   groups have matches."
  [r]
  (and r
       (= 5 (count r))))

(defn- convert-record
  "Converts the regular expression matches corresponding to one date-rate
   pair into a vector representing the date-rate pair as one
   org.joda.time.DateTime object and one float."
  [r]
  (let [year (Integer/parseInt (nth r 1) 10)
        month (Integer/parseInt (nth r 2) 10)
        day (Integer/parseInt (nth r 3) 10)
        rate (Float/parseFloat (nth r 4))]
    [(date-time year month day) rate]))

(defn- parse-exchange-rates
  "Parses the given exchange rate resource (a CSV file) and returns a hash
   map that maps dates to exchange rates."
  [res]
  (with-open [rdr (reader res)]
    (apply sorted-map (->> (line-seq rdr)
                           (map (partial re-matches re-line))
                           (filter valid-record?)
                           (mapcat convert-record)))))

(defn- resource-for
  "Returns the name of the resource corresponding to a given currency pair."
  [[from to]]
  (resource (str (name from) "_" (name to) "_rates.csv")))

(defvar- currencies-rates-map
  (apply hash-map
         (mapcat (fn [pair]
                   [pair (parse-exchange-rates (resource-for pair))])
                 currency-pairs))
  "A hash map that maps a currency pair to the hash map that contains the
   exchange rates corresponding to that pair.")

(defn currency-name
  "Turns a currency keyword into an upper-case string."
  [k]
  (-> k name str upper-case))

(defn currency-keyword
  "Turns a currency string into a keyword."
  [#^String s]
  (-> s lower-case keyword))

(defn- build-exchange-rate-lookup
  "Returns a function that maps a date to an exchange rate.
   The time of day within the given date is ignored. The exchange rate
   is assumed constant throughout the day. If an exchange rate for the
   given date is not available, it returns the exchange rate for the
   closest earlier date.
   The builder takes a pair of currencies and the date-to-rate map
   corresponding to that pair."
  [from to m]
  (fn [dt]
    (if-let [lte-part (rsubseq m <= dt)]
      (val (first lte-part))
      (throw (RuntimeException. (str "No " (currency-name from) " to "
                                     (currency-name to)
                                     " exchange rate for " dt))))))

(defn- reciprocal-lookup
  "Returns a function that always returns the reciprocal of the given function.
   The given function must take exactly one parameter."
  [f]
  (fn [dt] (/ 1.0 (f dt))))

(defn get-exchange-rate-lookup
  "Returns a function that returns the exchange rate at the closing
   of the given time (a org.joda.time.DateTime object) from the first
   currency (keyword) to the second currency (keyword)."
  [from to]
  (if (= from to)
    (constantly 1.0)
    (condp #(get %2 %1) currencies-rates-map
      [from to] :>> #(build-exchange-rate-lookup from to %)
      [to from] :>> #(-> (build-exchange-rate-lookup to from %) reciprocal-lookup)
      nil)))

;; C-c , to run the tests
;; C-c ' to see the error details (you must be on the line of the test)
;; C-c k to clear the errors

(deftest test-currency-name
  (is (= "USD" (currency-name :usd)))
  (is (= "USD" (currency-name :USD))))

(deftest test-build-rate-lookup
  (let [usd-cad-rates (get currencies-rates-map [:usd :cad])]
    (is (< 1.4 ((build-exchange-rate-lookup :usd :cad usd-cad-rates)
                (date-time 2000 1 4 8))))))

(deftest test-reciprocal-lookup
  (let [usd-cad-rates (get currencies-rates-map [:usd :cad])
        lookup-rate (build-exchange-rate-lookup :usd :cad usd-cad-rates)
        dt (date-time 2000 1 4)]
    (is (< 1.4 (lookup-rate dt)))
    (is (> (/ 1.0 1.4) ((reciprocal-lookup lookup-rate) dt)))))

(deftest test-get-exchange-rate-lookup
  (let [dt (date-time 2000 1 4)]
    (let [lookup-rate (get-exchange-rate-lookup :usd :cad)]
      (is (< 1.4 (lookup-rate dt))))
    (let [lookup-rate (get-exchange-rate-lookup :cad :usd)]
      (is (> (/ 1.0 1.4) (lookup-rate dt))))))
