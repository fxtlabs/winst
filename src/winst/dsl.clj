(ns winst.dsl
  "An embedded domain specific language to easily describe trading activities."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.string :only (lower-case)]
        [clj-time.core :only (date-time)]
        [winst.core :only (sort-by-date)]
        [winst.securities :only (security-uid lookup-security-symbol)]))

(defn- symbol-to-keyword
  "Turns a (Clojure) symbol into a keyword."
  [s]
  (-> s name lower-case keyword))

(defn- to-string
  "Turns a (Clojure) symbol into a string. If the given parameter is not
   a symbol, it assumes it is a string and returns it unchanged."
  [x]
  (if (symbol? x) (name x) x))

(defn get-security-uid
  "Returns the security corresponding to the given symbol. If the same
   symbol is used by more than one market exchange, it will select one
   of them; which one is undetermined."
  [sym]
  (if-let [security (lookup-security-symbol sym)]
    (security-uid (if (vector? security) (first security) security))
    (throw (RuntimeException. (str "Cannot find security for symbol " sym)))))

(defmacro activity
  "Builds an account activity map for one operation using a shorthand language,
   best illustrated with an example:
   (activity 2010 3 4 buy 10 aapl at 2000.0)
   (activity 2010 4 5 sell 10 aapl at 4500.0)
   (activity 2010 5 6 exchange 20 goog for 2 msft)
   (activity 2010 4 5 split 30 amzn 2 for 1)
   The first three integers specify the year, month, and day of the activity;
   then the name of the activity (buy, sell, exchange or split);
   then the quantity of the security as a number;
   then the symbol of the security as a symbol or a string.
   The rest of the parameters depends on the specific activity and can be
   easily inferred from the example above. Note that the cost/proceeds of
   a buy/sell activity is given for the full quantity and not for a single
   share."
  [year month day action quantity ticker & rest]
  (let [action# (symbol-to-keyword action)]
    `(merge {:date (date-time ~year ~month ~day),
             :type ~action#,
             :quantity ~quantity,
             :security-uid (get-security-uid ~(to-string ticker))}
            ~(case action#
                   :buy (let [[_ cost] rest]
                          {:cost cost})
                   :sell (let [[_ proceeds] rest]
                           {:proceeds proceeds})
                   :split (let [[end _ start] rest]
                            {:split-ratio (/ end start)})
                   :exchange (let [[_ new-quantity new-ticker] rest]
                               {:new-quantity new-quantity,
                                :new-security-uid `(get-security-uid ~(to-string new-ticker))})
                   (throw (Error. (str "Activity type " action " does not exist!")))))))

(defmacro activities
  "Produces a chronologically sorted sequence of account activities using
   the same shorthand language defined by the 'activity' macro. For example:
   (activities (2008 1 24 buy 80 amzn at 888.0)
               (2008 4 9 buy 35 intc at 1010.0)
               (2008 5 22 sell 60 amzn at 700.0))"
  [& as]
  `(sort-by-date ~(vec (for [a# as] `(activity ~@a#)))))

