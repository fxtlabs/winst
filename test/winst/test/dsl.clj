(ns winst.test.dsl
  (:use clojure.test
        [clj-time.core :only (date-time)]
        [winst.dsl :only (activity activities)]))

(deftest expansion
  (testing "Macro expansion"
    (is (= (activity 2008 4 9 buy 35 intc at 1010.0)
           {:security-uid "NASDAQ:INTC"
            :date (date-time 2008 4 9)
            :type :buy
            :quantity 35
            :cost 1010.0}))
    (is (= (activity 2008 5 22 sell 60 amzn at 700.0)
           {:security-uid "NASDAQ:AMZN"
            :date (date-time 2008 5 22)
            :type :sell
            :quantity 60
            :proceeds 700.0}))
    (is (= (activity 2010 4 5 split 50 abcd 3 for 2)
           {:security-uid "NASDAQ:ABCD"
            :date (date-time 2010 4 5)
            :type :split
            :quantity 50
            :split-ratio 3/2}))
    (is (= (activity 2010 5 6 exchange 20 goog for 2 msft)
           {:security-uid "NASDAQ:GOOG"
            :date (date-time 2010 5 6)
            :quantity 20
            :type :exchange
            :new-security-uid "NASDAQ:MSFT"
            :new-quantity 2}))
    (is (= (map :security-uid (activities (2009 10 22 buy 50 ko at 6000.0)
                                          (2008 1 24 buy 80 amzn at 888.0)
                                          (2008 5 22 sell 60 goog at 700.0)
                                          (2008 4 9 buy 35 intc at 1010.0)))
           ["NASDAQ:AMZN" "NASDAQ:INTC" "NASDAQ:GOOG" "NYSE:KO"]))
))

