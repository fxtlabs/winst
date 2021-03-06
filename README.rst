winst
=====

Track your trading activities, holdings, and realized gains in
multiple currencies.

Usage
-----

The winst library is hosted on clojars.org.

Add ``[com.fxtlabs/winst "1.0.0-SNAPSHOT"]`` to the dependencies
list in your ``project.clj`` file and run ``lein deps`` to download the
library from the Clojars archives.

See the example files in the ``example`` directory under the project
root to learn how to use the library.

License
-------

Copyright (C) 2011 Filippo Tampieri

Distributed under the Eclipse Public License, the same as Clojure.

Documentation
-------------

Autodoc generated documentation can be found at
http://winst.fxtlabs.com.

You can generate a local copy with ``lein autodoc``; it will be saved
in the ``autodoc`` directory under the project root.


Notes
-----

NASDAQ keeps lists of the companies traded on the NASDAQ, NYSE, and
AMEX exchanges at http://www.nasdaq.com/screening/company-list.aspx.

These lists were downloaded as CSV files and stored in the
``resources`` directory under the project root.
They are used to look up the security name from its symbol and find
out on which exchange they are traded.

