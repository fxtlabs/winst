(ns winst.core
  "The engine that processes trading activities to compute resulting
   holdings and realized gains/losses."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.def :only (defvar)]
        [clj-time.core :only (before? within? date-time)]
        [clj-time.coerce :only (to-long)]))

(defn dated-before?
  "True if the given item dates before the specified time.
   The item must be a map with a :date key."
  [dt x]
  (before? (:date x) dt))

(defn dated-within?
  "True if the given item dates within the specified time interval.
   The item must be a map with a :date key."
  [interval x]
  (within? interval (:date x)))

(defn sort-by-date
  "Sorts the given items in chronological order.
   The items must be maps with a :date key."
  [as]
  (sort-by #(to-long (:date %)) as))

(defvar merge-by-date
  (comp sort-by-date concat)
  "Concatenates the given sequences of items and returns them
   as a single, chronologically-sorted, sequence.
   The items must be maps with a :date key.")


(defn convert-activity
  "Converts an activity using the given exchange rate lookup function."
  [exchange-rate-lookup activity]
  (let [rate (-> activity :date exchange-rate-lookup)]
    (case (:type activity)
          :buy (merge-with * activity {:cost rate})
          :sell (merge-with * activity {:proceeds rate})
          (:exchange :split) activity
          (throw (RuntimeException. (str "Activity type " (-> activity :type :name) " does not exist!"))))))

(defn convert-activities
  "Converts a sequence of activities using the given exchange rate
   lookup function."
  [exchange-rate-lookup activities]
  (map (partial convert-activity exchange-rate-lookup) activities))


(defn- subtract-from-holdings
  "Subtracts some quantity of the given security from the given holdings
   and returns a vector with the remaining holdings and the original cost
   of the quantity just subtracted. The latter value is used to implement
   security exchanges as a sequence of a subtraction and an addition to
   the holdings."
  [holdings {:keys [security-uid quantity]}]
  (if-let [holding (get holdings security-uid)]
    (cond (< quantity (:quantity holding))
          (let [new-holding
                (assoc holding
                  :quantity (- (:quantity holding) quantity)
                  :cost (* (:cost holding) (/ (- (:quantity holding) quantity) (:quantity holding))))]
            [(assoc holdings security-uid new-holding)
             (- (:cost holding) (:cost new-holding))])

          (= quantity (:quantity holding))
          [(dissoc holdings security-uid) (:cost holding)]

          :else
          (throw (RuntimeException. "cannot sell more that is there!")))
    (throw (RuntimeException.
            (str "Cannot find " security-uid " in holdings.")))))

(defn- add-to-holdings
  "Adds some quantity of the given security to the given holdings
   and returns the new positions."
  [holdings {:keys [date security-uid quantity cost]}]
  (let [holding (get holdings security-uid)]
    (assoc holdings security-uid
           (if holding
             (assoc holding
               ;; NOTE: keeps the most recent date; consider keeping a list
               ;; of dates
               :date date
               :quantity (+ quantity (:quantity holding))
               :cost (+ cost (:cost holding)))
             {:date date, :quantity quantity, :cost cost}))))

(defmulti apply-activity
  "Applies a trading activity to a given holdings and returns the updated
   holdings."
  {:arglists '([holdings activity])}
  (fn [_ activity] (:type activity)))

(defmethod apply-activity :buy [holdings activity]
  (add-to-holdings holdings activity))

(defmethod apply-activity :sell [holdings activity]
  (first (subtract-from-holdings holdings activity)))

(defmethod apply-activity :split [holdings activity]
  (let [{:keys [security-uid quantity split-ratio]} activity]
    (if-let [holding (get holdings security-uid)]
      (if (= quantity (:quantity holding))
        (assoc holdings security-uid
               (assoc holding :quantity (* split-ratio quantity)))
        (throw (Throwable. "quantity must match!")))
      (throw (Throwable. "cannot find holding to split!")))))

(defmethod apply-activity :exchange [holdings activity]
  (let [[new-holdings cost] (subtract-from-holdings holdings activity)]
    (add-to-holdings new-holdings {:date (:date activity)
                                   :security-uid (:new-security-uid activity)
                                   :quantity (:new-quantity activity)
                                   :cost cost})))

(defn compute-holdings
  "Returns the final state of the holdings after applying the given
   sequence of trading activities."
  ([as] (compute-holdings {} as))
  ([initial-holdings as] (reduce apply-activity initial-holdings as)))

(defn compute-holdings-seq
  "Returns a sequence of all the intermediate states of the holdings
   after applying the given sequence of trading activities. The first
   item in the result is the state of the holdings immediately prior to
   the application of any of the given activities."
  ([as] (compute-holdings-seq {} as))
  ([initial-holdings as] (reductions apply-activity initial-holdings as)))


(defn- compute-realized-gain
  "Returns the realized gain resulting from the application of the given
   activity (which must be a SELL activity) to the given holdings."
  [holdings activity]
  (if-let [holding (get holdings (:security-uid activity))]
    {:security-uid (:security-uid activity),
     :quantity (:quantity activity),
     :date (:date activity),
     :cost (* (:cost holding) (/ (:quantity activity) (:quantity holding))),
     :proceeds (:proceeds activity)}
    (throw (Throwable. "cannot find matching holding for capital gain!"))))

(defn compute-realized-gains
  "Returns a sequence of the realized gains resulting from the application
   of the given trading activities to the given initial holdings."
  ([as] (compute-realized-gains {} as))
  ([initial-holdings as]
     (->> (map vector (compute-holdings-seq initial-holdings as) as)
          (filter (fn [[_ activity]] (= (:type activity) :sell)))
          (map (fn [[holdings sale]] (compute-realized-gain holdings sale))))))

