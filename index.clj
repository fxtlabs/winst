{:namespaces
 ({:source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url "http://winst.fxtlabs.com/winst.core-api.html",
   :name "winst.core",
   :author "Filippo Tampieri <fxt@fxtlabs.com>",
   :doc
   "The engine that processes trading activities to compute resulting\nholdings and realized gains/losses."}
  {:source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/currency.clj",
   :wiki-url "http://winst.fxtlabs.com/winst.currency-api.html",
   :name "winst.currency",
   :author "Filippo Tampieri <fxt@fxtlabs.com>",
   :doc
   "Services for working with different currencies, including parsing\nexchange rate CSV files and finding out the exchange rate for a\ngiven day."}
  {:source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/dsl.clj",
   :wiki-url "http://winst.fxtlabs.com/winst.dsl-api.html",
   :name "winst.dsl",
   :author "Filippo Tampieri <fxt@fxtlabs.com>",
   :doc
   "An embedded domain specific language to easily describe trading activities."}
  {:source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/handlers.clj",
   :wiki-url "http://winst.fxtlabs.com/winst.handlers-api.html",
   :name "winst.handlers",
   :author "Filippo Tampieri <fxt@fxtlabs.com>",
   :doc
   "Request handlers for the trading activity, holdings, and realized\ngains/losses of an account over a specified period of time."}
  {:source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj",
   :wiki-url "http://winst.fxtlabs.com/winst.presentation-api.html",
   :name "winst.presentation",
   :author "Filippo Tampieri <fxt@fxtlabs.com>",
   :doc
   "The presentation layer of the winst server. It renders HTML reports\nfor the trading activity, holdings, and realized gains/losses of an\naccount over a specified period of time."}
  {:source-url
   "https://github.com/fxtlabs/winst/blob/f90e2d0409e0fa18650f22bb5b397187c4dc3bff/src/winst/routes.clj",
   :wiki-url "http://winst.fxtlabs.com/winst.routes-api.html",
   :name "winst.routes",
   :author "Filippo Tampieri <fxt@fxtlabs.com>",
   :doc
   "Maps URLs to request handlers to report trading activity, holdings,\nand realized gains/losses for an account over a specified period of time."}
  {:source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj",
   :wiki-url "http://winst.fxtlabs.com/winst.securities-api.html",
   :name "winst.securities",
   :author "Filippo Tampieri <fxt@fxtlabs.com>",
   :doc
   "Services for working with securities. It manages a list of all the\ncompanies listed on the NASDAQ, NYSE, and AMEX exchanges. The official\nlists are available for download as CSV files from the NASDAQ web site\nat http://www.nasdaq.com/screening/company-list.aspx"}
  {:source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj",
   :wiki-url "http://winst.fxtlabs.com/winst.utils-api.html",
   :name "winst.utils",
   :author "Filippo Tampieri <fxt@fxtlabs.com>",
   :doc "Utility functions used to prepare reports."}),
 :vars
 ({:arglists ([holdings activity]),
   :name "apply-activity",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L90",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/apply-activity",
   :doc
   "Applies a trading activity to a given holdings and returns the updated\nholdings.",
   :var-type "multimethod",
   :line 90,
   :file "/Users/fxt/Projects/winst/./src/winst/core.clj"}
  {:arglists ([as] [initial-holdings as]),
   :name "compute-holdings",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L118",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/compute-holdings",
   :doc
   "Returns the final state of the holdings after applying the given\nsequence of trading activities.",
   :var-type "function",
   :line 118,
   :file "winst/core.clj"}
  {:arglists ([as] [initial-holdings as]),
   :name "compute-holdings-seq",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L124",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/compute-holdings-seq",
   :doc
   "Returns a sequence of all the intermediate states of the holdings\nafter applying the given sequence of trading activities. The first\nitem in the result is the state of the holdings immediately prior to\nthe application of any of the given activities.",
   :var-type "function",
   :line 124,
   :file "winst/core.clj"}
  {:arglists ([as] [initial-holdings as]),
   :name "compute-realized-gains",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L145",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/compute-realized-gains",
   :doc
   "Returns a sequence of the realized gains resulting from the application\nof the given trading activities to the given initial holdings.",
   :var-type "function",
   :line 145,
   :file "winst/core.clj"}
  {:arglists ([exchange-rate-lookup activities]),
   :name "convert-activities",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L44",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/convert-activities",
   :doc
   "Converts a sequence of activities using the given exchange rate\nlookup function.",
   :var-type "function",
   :line 44,
   :file "winst/core.clj"}
  {:arglists ([exchange-rate-lookup activity]),
   :name "convert-activity",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L34",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/convert-activity",
   :doc
   "Converts an activity using the given exchange rate lookup function.",
   :var-type "function",
   :line 34,
   :file "winst/core.clj"}
  {:arglists ([dt x]),
   :name "dated-before?",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L9",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/dated-before?",
   :doc
   "True if the given item dates before the specified time.\nThe item must be a map with a :date key.",
   :var-type "function",
   :line 9,
   :file "winst/core.clj"}
  {:arglists ([interval x]),
   :name "dated-within?",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L15",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/dated-within?",
   :doc
   "True if the given item dates within the specified time interval.\nThe item must be a map with a :date key.",
   :var-type "function",
   :line 15,
   :file "winst/core.clj"}
  {:raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L27",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/merge-by-date",
   :namespace "winst.core",
   :line 27,
   :file "winst/core.clj",
   :var-type "var",
   :doc
   "Concatenates the given sequences of items and returns them\nas a single, chronologically-sorted, sequence.\nThe items must be maps with a :date key.",
   :name "merge-by-date"}
  {:arglists ([as]),
   :name "sort-by-date",
   :namespace "winst.core",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj#L21",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/core.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.core-api.html#winst.core/sort-by-date",
   :doc
   "Sorts the given items in chronological order.\nThe items must be maps with a :date key.",
   :var-type "function",
   :line 21,
   :file "winst/core.clj"}
  {:arglists ([s]),
   :name "currency-keyword",
   :namespace "winst.currency",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/currency.clj#L71",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/currency.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.currency-api.html#winst.currency/currency-keyword",
   :doc "Turns a currency string into a keyword.",
   :var-type "function",
   :line 71,
   :file "winst/currency.clj"}
  {:arglists ([k]),
   :name "currency-name",
   :namespace "winst.currency",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/currency.clj#L66",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/currency.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.currency-api.html#winst.currency/currency-name",
   :doc "Turns a currency keyword into an upper-case string.",
   :var-type "function",
   :line 66,
   :file "winst/currency.clj"}
  {:arglists ([from to]),
   :name "get-exchange-rate-lookup",
   :namespace "winst.currency",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/currency.clj#L98",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/currency.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.currency-api.html#winst.currency/get-exchange-rate-lookup",
   :doc
   "Returns a function that returns the exchange rate at the closing\nof the given time (a org.joda.time.DateTime object) from the first\ncurrency (keyword) to the second currency (keyword).",
   :var-type "function",
   :line 98,
   :file "winst/currency.clj"}
  {:arglists ([& as]),
   :name "activities",
   :namespace "winst.dsl",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/dsl.clj#L62",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/dsl.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.dsl-api.html#winst.dsl/activities",
   :doc
   "Produces a chronologically sorted sequence of account activities using\nthe same shorthand language defined by the 'activity' macro. For example:\n(activities (2008 1 24 buy 80 amzn at 888.0)\n            (2008 4 9 buy 35 intc at 1010.0)\n            (2008 5 22 sell 60 amzn at 700.0))",
   :var-type "macro",
   :line 62,
   :file "/Users/fxt/Projects/winst/./src/winst/dsl.clj"}
  {:arglists ([year month day action quantity ticker & rest]),
   :name "activity",
   :namespace "winst.dsl",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/dsl.clj#L29",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/dsl.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.dsl-api.html#winst.dsl/activity",
   :doc
   "Builds an account activity map for one operation using a shorthand language,\nbest illustrated with an example:\n(activity 2010 3 4 buy 10 aapl at 2000.0)\n(activity 2010 4 5 sell 10 aapl at 4500.0)\n(activity 2010 5 6 exchange 20 goog for 2 msft)\n(activity 2010 4 5 split 30 amzn 2 for 1)\nThe first three integers specify the year, month, and day of the activity;\nthen the name of the activity (buy, sell, exchange or split);\nthen the quantity of the security as a number;\nthen the symbol of the security as a symbol or a string.\nThe rest of the parameters depends on the specific activity and can be\neasily inferred from the example above. Note that the cost/proceeds of\na buy/sell activity is given for the full quantity and not for a single\nshare.",
   :var-type "macro",
   :line 29,
   :file "/Users/fxt/Projects/winst/./src/winst/dsl.clj"}
  {:arglists ([sym]),
   :name "get-security-uid",
   :namespace "winst.dsl",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/dsl.clj#L20",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/dsl.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.dsl-api.html#winst.dsl/get-security-uid",
   :doc
   "Returns the security corresponding to the given symbol. If the same\nsymbol is used by more than one market exchange, it will select one\nof them; which one is undetermined.",
   :var-type "function",
   :line 20,
   :file "/Users/fxt/Projects/winst/./src/winst/dsl.clj"}
  {:arglists ([account]),
   :name "activities-handler",
   :namespace "winst.handlers",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/handlers.clj#L197",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/handlers.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.handlers-api.html#winst.handlers/activities-handler",
   :doc
   "Returns a handler capable of responding to activity requests for\nthe given account.",
   :var-type "function",
   :line 197,
   :file "winst/handlers.clj"}
  {:arglists ([account]),
   :name "gains-handler",
   :namespace "winst.handlers",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/handlers.clj#L223",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/handlers.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.handlers-api.html#winst.handlers/gains-handler",
   :doc
   "Returns a handler capable of responding to realized gain/loss requests\nfor the given account.",
   :var-type "function",
   :line 223,
   :file "winst/handlers.clj"}
  {:arglists ([account]),
   :name "holdings-handler",
   :namespace "winst.handlers",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/handlers.clj#L176",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/handlers.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.handlers-api.html#winst.handlers/holdings-handler",
   :doc
   "Returns a handler capable of responding to holdings requests for the\ngiven account.",
   :var-type "function",
   :line 176,
   :file "winst/handlers.clj"}
  {:arglists ([account report-currency report-interval activities]),
   :name "render-activities",
   :namespace "winst.presentation",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj#L201",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.presentation-api.html#winst.presentation/render-activities",
   :doc
   "Renders a report showing the trading activity for the given account over\nthe given time interval and in the given reporting currency.",
   :var-type "function",
   :line 201,
   :file "/Users/fxt/Projects/winst/./src/winst/presentation.clj"}
  {:arglists ([_ msg]),
   :name "render-error",
   :namespace "winst.presentation",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj#L87",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.presentation-api.html#winst.presentation/render-error",
   :doc
   "Renders an error page for the given account and error message.",
   :var-type "function",
   :line 87,
   :file "/Users/fxt/Projects/winst/./src/winst/presentation.clj"}
  {:arglists ([account report-currency report-interval gains]),
   :name "render-gains",
   :namespace "winst.presentation",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj#L145",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.presentation-api.html#winst.presentation/render-gains",
   :doc
   "Renders a report showing the realized gain/loss for the given account over\nthe given time interval and in the given reporting currency.",
   :var-type "function",
   :line 145,
   :file "/Users/fxt/Projects/winst/./src/winst/presentation.clj"}
  {:arglists ([account report-currency report-time holdings]),
   :name "render-holdings",
   :namespace "winst.presentation",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj#L115",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.presentation-api.html#winst.presentation/render-holdings",
   :doc
   "Renders a report showing the holdings for the given account at the given\ntime and in the given reporting currency.",
   :var-type "function",
   :line 115,
   :file "/Users/fxt/Projects/winst/./src/winst/presentation.clj"}
  {:arglists ([]),
   :name "render-not-found",
   :namespace "winst.presentation",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj#L80",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/presentation.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.presentation-api.html#winst.presentation/render-not-found",
   :doc "Renders a 404 error (not found) page.",
   :var-type "function",
   :line 80,
   :file "/Users/fxt/Projects/winst/./src/winst/presentation.clj"}
  {:arglists ([accounts]),
   :name "main-routes",
   :namespace "winst.routes",
   :source-url
   "https://github.com/fxtlabs/winst/blob/f90e2d0409e0fa18650f22bb5b397187c4dc3bff/src/winst/routes.clj#L23",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/f90e2d0409e0fa18650f22bb5b397187c4dc3bff/src/winst/routes.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.routes-api.html#winst.routes/main-routes",
   :doc
   "Returns a handler that can respond to requests related to the given\nsequence of accounts.\nAn account is a map with the following keywords:\n:tag         A keyword corresponding to the name used to identify the\n             account in a URL.\n:name        The name of the account as it should appear on the reports.\n:description A description of the account.\n:holder      The name of the account holder as it should appear on the reports.\n:type        The type of the account (as a keyword). For example, :cash or\n             :margin.\n:currency    The currency used for the account (as a keyword). Currently,\n             only :usd (USD) and :cad (CAD) are supported.\n:activities  The sequence of trading activities for the account. See\n             winst.dsl/activity for details.\nThe URLs this handler responds to must be of the form\n'/accounts/<ACCOUNT>/<ASPECT>/<YEAR>/<MONTH>' where:\n<ACCOUNT>    The short name for the account (see :tag key above).\n<ASPECT>     One of 'holdings', 'activities', or 'gains'.\n<YEAR>       The year for which the report is desired. If omitted, the\n             report will be generated for the current date (since account\n             inception for activities and realized gains). If present,\n             <MONTH> part must also be present.\n<MONTH>      The month (1 to 12) for which the report is desired. If\n             omitted, the report will be for a whole year or for the\n             lifespan of the account (see <YEAR>).\nThe optional query parameter 'currency' can be added to the URL to\nspecify the currency to be used for the report. Possible values are USD\nor CAD.",
   :var-type "function",
   :line 23,
   :file "/Users/fxt/Projects/winst/./src/winst/routes.clj"}
  {:arglists ([uid]),
   :name "lookup-security",
   :namespace "winst.securities",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj#L109",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.securities-api.html#winst.securities/lookup-security",
   :doc "Returns the security corresponding to the given unique ID.",
   :var-type "function",
   :line 109,
   :file "/Users/fxt/Projects/winst/./src/winst/securities.clj"}
  {:arglists ([exchange sym] [sym]),
   :name "lookup-security-symbol",
   :namespace "winst.securities",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj#L85",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.securities-api.html#winst.securities/lookup-security-symbol",
   :doc
   "Returns the security corresponding to the given symbol. If an exchange\nkeyword is provided, it limits the search to that market exchange;\notherwise, it will search all available exchanges.",
   :var-type "function",
   :line 85,
   :file "/Users/fxt/Projects/winst/./src/winst/securities.clj"}
  {:raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj#L103",
   :wiki-url
   "http://winst.fxtlabs.com//winst.securities-api.html#winst.securities/securities-by-uid",
   :namespace "winst.securities",
   :line 103,
   :file "/Users/fxt/Projects/winst/./src/winst/securities.clj",
   :var-type "var",
   :doc "A hash map that maps unique IDs to securities.",
   :name "securities-by-uid"}
  {:arglists ([security]),
   :name "security-qualified-symbol",
   :namespace "winst.securities",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj#L17",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.securities-api.html#winst.securities/security-qualified-symbol",
   :doc
   "Returns the qualified symbol for the given security as a string\nformatted as EXCHANGE:SYMBOL (e.g. NASDAQ:AAPL).",
   :var-type "function",
   :line 17,
   :file "/Users/fxt/Projects/winst/./src/winst/securities.clj"}
  {:raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/securities.clj#L100",
   :wiki-url
   "http://winst.fxtlabs.com//winst.securities-api.html#winst.securities/security-uid",
   :namespace "winst.securities",
   :line 100,
   :file "/Users/fxt/Projects/winst/./src/winst/securities.clj",
   :var-type "var",
   :doc "Returns a unique ID for the given security.",
   :name "security-uid"}
  {:arglists ([t]),
   :name "activity-type-name",
   :namespace "winst.utils",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj#L32",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.utils-api.html#winst.utils/activity-type-name",
   :doc
   "Returns the string corresponding to a trading activity type\n(e.g. :buy -> BUY).",
   :var-type "function",
   :line 32,
   :file "/Users/fxt/Projects/winst/./src/winst/utils.clj"}
  {:arglists ([f]),
   :name "format-currency",
   :namespace "winst.utils",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj#L21",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.utils-api.html#winst.utils/format-currency",
   :doc "Formats currency using parenthesis for negative values.",
   :var-type "function",
   :line 21,
   :file "/Users/fxt/Projects/winst/./src/winst/utils.clj"}
  {:arglists ([dt]),
   :name "format-date",
   :namespace "winst.utils",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj#L11",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.utils-api.html#winst.utils/format-date",
   :doc "Formats a date as yyyy-mm-dd.",
   :var-type "function",
   :line 11,
   :file "/Users/fxt/Projects/winst/./src/winst/utils.clj"}
  {:arglists ([f]),
   :name "format-percentage",
   :namespace "winst.utils",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj#L26",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.utils-api.html#winst.utils/format-percentage",
   :doc
   "Formats percentages (given as a fraction) using parenthesis for\nnegative values.",
   :var-type "function",
   :line 26,
   :file "/Users/fxt/Projects/winst/./src/winst/utils.clj"}
  {:arglists ([f]),
   :name "format-quantity",
   :namespace "winst.utils",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj#L16",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.utils-api.html#winst.utils/format-quantity",
   :doc "Formats a quantity using parenthesis for negative values.",
   :var-type "function",
   :line 16,
   :file "/Users/fxt/Projects/winst/./src/winst/utils.clj"}
  {:arglists
   ([{:keys [date type quantity security-uid], :as activity}]),
   :name "normalize-activity",
   :namespace "winst.utils",
   :source-url
   "https://github.com/fxtlabs/winst/blob/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj#L38",
   :raw-source-url
   "https://github.com/fxtlabs/winst/raw/c0e74fb09e43d0c304f7cb739f17d46ddf971fc8/src/winst/utils.clj",
   :wiki-url
   "http://winst.fxtlabs.com//winst.utils-api.html#winst.utils/normalize-activity",
   :doc
   "Normalizes an activity so that it can be described in terms of securities\nadded or subtracted from the holdings. A buy or sell activity\nwill be represented by one entry each, but an exchange will generate two\nentries. It returns a vector of one or two entries.",
   :var-type "function",
   :line 38,
   :file "/Users/fxt/Projects/winst/./src/winst/utils.clj"})}
