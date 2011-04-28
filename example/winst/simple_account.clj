(ns winst.simple-account
  "A simple example account for the example program."
  {:author "Filippo Tampieri <fxt@fxtlabs.com>"}
  (:use [clojure.contrib.def :only (defvar defvar-)]
        [winst.dsl :only (activities)]))

(defvar- activity-list
  (activities (2008 1 24 buy 80 amzn at 888.0)
              (2008 4 9 buy 35 intc at 1010.0)
              (2008 5 22 sell 60 amzn at 700.0)
              (2009 10 22 buy 50 ko at 6000.0)
              (2010 1 4 buy 10 aapl at 1000.0)
              (2010 2 1 buy 20 goog at 3000.0)
              (2010 2 1 buy 30 amzn at 600.0)
              (2010 3 4 buy 10 aapl at 2000.0)
              (2010 4 5 sell 10 aapl at 4500.0)
              (2010 5 6 exchange 20 goog for 2 msft)
              (2010 4 5 split 50 amzn 2 for 1)
              (2010 11 26 sell 1 msft at 500.0)))

(defvar account
  {:tag :simple,
   :name "Simple Example"
   :description "Simple Example Account",
   :holder "John Smith"
   :type :margin
   :currency :usd,
   :activities activity-list}
  "A simple example account made up of a few hand-picked trading activities.")

