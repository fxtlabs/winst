(ns winst.securities
  "Services for working with securities. It manages a list of all the
   companies listed on the NASDAQ, NYSE, and AMEX exchanges. The official
   lists are available for download as CSV files from the NASDAQ web site
   at http://www.nasdaq.com/screening/company-list.aspx"
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.java.io :only (resource)]
        [clojure.contrib.string :only (lower-case upper-case)]
        [clojure.contrib.def :only (defvar defvar-)]
        [clj-time.core :only (date-time)]
        [clojure-csv.core :only (parse-csv)]))

(defvar- exchanges
  [:nasdaq :nyse :amex]
  "The market exchanges where the trading activity takes place.")

(defn security-qualified-symbol
  "Returns the qualified symbol for the given security as a string
   formatted as EXCHANGE:SYMBOL (e.g. NASDAQ:AAPL)."
  [security]
  (let [{sym :symbol, exchange :exchange} security]
    (str (upper-case (name exchange)) ":" (upper-case sym))))

(defn- valid-record?
  "A predicate that returns true if the given CSV record is well formed.
   The test is very basic and mainly serves to eliminate blank lines and
   the ending comma (which causes the CSV parser to return an extra record)
   from the parsed CSV file."
  [r]
  (and
   (vector? r)
   (= 9 (count r))
   (< 0 (count (first r)))))

(defn- convert-record
  "Transforms a CSV record into a security map.
   It assumes the CSV record is a sequence of strings corresponding to the
   following fields: symbol, name, last sale, market capitalization, IPO year,
   sector, industry, URL for summary quote.
   The resulting security map retains the exchange, symbol, name, sector,
   and industry for the security."
  [exchange r]
  {:exchange exchange
   :symbol (upper-case (nth r 0))
   :name (nth r 1)
   :sector (nth r 5)
   :industry (nth r 6)
   })

(defn- parse-company-list
  "Parses the resource corresponding to the given exchange keyword and
   returns a sequence of all the securities it finds.
   The resource name is equal to '<exchange>_companies.csv' where
   <exchange> is the lower-case name corresponding to the given keyword."
  [exchange]
  (let [res (resource (str (lower-case (name exchange)) "_companies.csv"))]
    (->> res slurp parse-csv (drop 1) (filter valid-record?)
         (map (partial convert-record exchange)))))

(defn- create-map-by-exchange
  "Creates a hash map that maps each of the exchange keywords to a value
   computed by running the supplied function on that keyword."
  [f]
  (apply hash-map (mapcat (fn [ex] [ex (f ex)]) exchanges)))

(defvar- securities-by-exchange
  (create-map-by-exchange parse-company-list)
  "A hash map that maps an exchange keyword to a sequence of the
   securities listed on that exchange.")

(defn- create-securities-map-by
  "Creates a hash map from a list of securities. The map is keyed by one
   of the fields of the security, corresponding to the provided keyword
   selector."
  [key securities]
  (apply hash-map
         (mapcat (fn [security] [(key security) security]) securities)))

(defvar- securities-by-exchange-by-symbol
  (create-map-by-exchange
   (fn [ex] (create-securities-map-by :symbol (ex securities-by-exchange))))
  "A hash map that maps an exchange keyword to a hash map that maps a
   security symbol to the corresponding security.")

(defn lookup-security-symbol
  "Returns the security corresponding to the given symbol. If an exchange
   keyword is provided, it limits the search to that market exchange;
   otherwise, it will search all available exchanges."
  ([exchange sym]
     (get-in securities-by-exchange-by-symbol [exchange (upper-case sym)]))
  ([sym]
     (let [results (->> exchanges
                        (map #(lookup-security-symbol % sym))
                        (remove #(nil? %)))]
       (case (count results)
             0 nil
             1 (first results)
             (vec results)))))

(defvar security-uid security-qualified-symbol
  "Returns a unique ID for the given security.")

(defvar securities-by-uid
  (apply hash-map
         (mapcat (fn [sec] [(security-uid sec) sec])
                 (apply concat (vals securities-by-exchange))))
  "A hash map that maps unique IDs to securities.")

(defn lookup-security
  "Returns the security corresponding to the given unique ID."
  [uid]
  (get securities-by-uid uid))

