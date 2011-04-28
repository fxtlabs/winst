(ns winst.rand-account
  "A generator of random trading activities and an example account
   generated using it; used for the example program."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.def :only (defvar defvar-)]
        [clj-time.coerce :only (to-long)]
        [clj-time.core :only
         (date-time interval minutes in-minutes start plus day-of-week)]
        [winst.core :only (apply-activity)]
        [winst.currency :only (get-exchange-rate-lookup)]
        [winst.securities :only (lookup-security securities-by-uid)]
        [winst.dsl :only (activities)]))

;;;
;;; A generator of random trading activities
;;;

(defn- choose-activity-type
  "Randomly choose an activity among BUY, SELL, SPLIT, and EXCHANGE with
   relative probabilities of 50%, 40%, 5%, and 5% respectively."
  [holdings]
  (if (empty? holdings)
    :buy
    (let [p (rand)]
      (cond (< p 0.5) :buy
            (< p 0.9) :sell
            (< p 0.95) :split
            :else :exchange))))

(defn- accumulate?
  "Randomly choose whether a BUY activity should buy more of a security
   already held in the account or start a new position.
   It favors accumulation if the account already has a large number of
   position, but tries not to let them grow to an unreasonable number.
   More formally, the probability of accumulation decreses exponentially
   as the number of held positions increases.
   The probability of adding a new position to an account that already
   holds 30 is only 40%."
  [holdings]
  (if (empty? holdings)
    false
    (let [n (count holdings)
          c (/ (Math/log 0.4) 30.0)
          p (- 1.0 (Math/exp (* n c)))]
      (< (rand) p))))

(defn- choose-held-security
  "Randomly choose one of the securities that is represented in the holdings."
  [holdings]
  (rand-nth (keys holdings)))

(defn- choose-new-security
  "Randomly choose a securities that is not already represented in
   the holdings."
  [holdings]
  (let [security-uids (keys securities-by-uid)]
    (->> (repeatedly (fn [] (rand-nth security-uids)))
         (drop-while (partial find holdings))
         first)))

(defn- choose-any-security
  "Randomly choose a security traded on the market."
  []
  (rand-nth (keys securities-by-uid)))

(defn- get-market-price [security-uid]
  (+ 10 (rand-int 91)))

(defn- gen-buy-activity
  "Randomly generate a BUY activity for the given holdings and date."
  [holdings dt]
  (let [security-uid (if (accumulate? holdings)
                       (choose-held-security holdings)
                       (choose-new-security holdings))
        price (get-market-price security-uid)
        quantity (+ 10 (* 5 (rand-int 100)))]
    {:date dt
     :type :buy
     :security-uid security-uid
     :quantity quantity
     :cost (* quantity price)}))

(defn- close-position?
  "Randomly decides whether the given position should be closed.
   Small positions (less than 5 shares) are always closed; other
   positions are closed with a 70% probability."
  [holding]
  (or
   (< (:quantity holding) 5)
   (< (rand) 0.7)))

(defn- gen-sell-activity
  "Randomly generate a SELL activity for the given holdings and date.
   It randomly picks one of the securities in the holdings and either
   closes it (70% probability) or sells some random portion of it."
  [holdings dt]
  (let [security-uid (choose-held-security holdings)
        holding (get holdings security-uid)
        current-quantity (:quantity holding)
        quantity (if (close-position? holding)
                   current-quantity
                   (+ 1 (rand-int (int current-quantity))))
        price (get-market-price security-uid)]
    {:date dt
     :type :sell
     :security-uid security-uid
     :quantity quantity
     :proceeds (* quantity price)}))

(defn- gen-split-activity
  "Randomly generate a SPLIT activity for the given holdings and date.
   It randomly picks one of the securities in the holdings and split
   it using a random split ratio ([1-4] for [1-3])."  
  [holdings dt]
  (let [security-uid (choose-held-security holdings)
        holding (get holdings security-uid)
        quantity (:quantity holding)
        split-ratio (/ (+ 1 (rand-int 4)) (+ 1 (rand-int 3)))]
    {:date dt
     :type :split
     :security-uid security-uid
     :quantity quantity
     :split-ratio split-ratio}))

(defn- gen-exchange-activity
  "Randomly generate an EXCHANGE activity for the given holdings and date.
   It randomly picks one of the securities in the holdings and replaces the
   entire position with one of corresponding cost in a different security."
  [holdings dt]
  (let [security-uid (choose-held-security holdings)
        holding (get holdings security-uid)
        quantity (:quantity holding)
        cost (:cost holding)
        new-security-uid (choose-any-security)
        new-quantity (/ cost (get-market-price new-security-uid))]
    {:date dt
     :type :exchange
     :security-uid security-uid
     :quantity quantity
     :new-security-uid new-security-uid
     :new-quantity new-quantity}))

(defn- gen-activity
  "Randomly generate a trading activity for the given holdings and date."  
  [holdings dt]
  (let [activity-type (choose-activity-type holdings)
        activity (case activity-type
                       :buy (gen-buy-activity holdings dt)
                       :sell (gen-sell-activity holdings dt)
                       :split (gen-split-activity holdings dt)
                       :exchange (gen-exchange-activity holdings dt))]
    [(apply-activity holdings activity) activity]))

(defn- rand-date
  "Return a random date in the given interval."
  [interval]
  (let [duration (in-minutes interval)]
    (plus (start interval) (minutes (rand-int (int duration))))))

(defn- valid-date?
  "A predicate that returns true if the given date does not falls on a week-end."
  [dt]
  (contains? #{1 2 3 4 5} (day-of-week dt)))

(defn- gen-activities*
  "Generate a lazy sequence of activities starting with the given holdings
   and adding one random trading activity for each of the given dates."
  [holdings dates]
  (lazy-seq
   (when-let [dates (seq dates)]
     (let [[holdings activity] (gen-activity holdings (first dates))]
       (cons activity (gen-activities* holdings (rest dates)))))))

(defn gen-activities
  "Generate a lazy sequence of 'n' random trading activities at random times
   during the given time interval."
  [n interval]
  (let [dates (->> (repeatedly (partial rand-date interval))
                   (filter valid-date?)
                   (take n)
                   (sort-by (fn [dt] (to-long dt))))]
    (gen-activities* {} dates)))


(defvar account
  {:tag :rand,
   :name "Rand Example"
   :description "Randomly Generated Example Account",
   :holder "John Smith"
   :type :margin
   :currency :usd,
   :activities (gen-activities 300
                               (interval (date-time 2006 1 1)
                                         (date-time 2010 12 31)))}
  "An example account made up of several random trading activities.")

