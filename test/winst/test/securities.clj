(ns winst.test.securities
  (:use [clojure.test]
        [winst.securities :only (security-qualified-symbol
                                 lookup-security-symbol
                                 security-uid
                                 securities-by-uid
                                 lookup-security)]))

(deftest lookup
  (testing "Security lookup functions"
    (let [uid "NASDAQ:AAPL"
          sym "AAPL"
          security {:exchange :nasdaq
                    :symbol sym
                    :name "Apple Inc."
                    :sector "Technology"
                    :industry "Computer Manufacturing"}]
      (is (= (lookup-security uid) (get securities-by-uid uid)))
      (is (= (lookup-security uid) security))
      (is (= (lookup-security-symbol sym) security)))
    (let [uid "NYSE:IBM"
          sym "IBM"
          security{:exchange :nyse,
                   :symbol "IBM",
                   :name "International Business Machines Corporation",
                   :sector "Technology",
                   :industry "Computer Manufacturing"}]
      (is (= (lookup-security uid) (get securities-by-uid uid)))
      (is (= (lookup-security uid) security))
      (is (= (lookup-security-symbol sym) security)))
    (is (not (lookup-security "NOT:THERE")))
    (is (not (lookup-security-symbol "NOTTHERE")))))

(deftest naming
  (testing "Security naming functions"
    (is (= (security-qualified-symbol (lookup-security-symbol :nasdaq "aapl"))
           "NASDAQ:AAPL"))
    (is (= (security-qualified-symbol (lookup-security-symbol :nyse "ibm"))
           "NYSE:IBM"))))

;; C-c , to run the tests
;; C-c ' to see the error details (you must be on the line of the test)
;; C-c k to clear the errors

