# log-searcher-indexer
A amateur log searcher and indexer application

The aim of this application is to demonstrate the capability of writing application log content into a database (SQL/NoSQL, though NoSQL is best suited for write intensive applications) and reading it back when required.

This application is basically a simple web application that has a very simple front-end to interact with. The Services are configured to return application/json data. 

Components used

  It utilizes an Oracle database to persist/read the log contents at the backend. Service layer uses JDK 8 (yeah, Lambda expressions, Date/Time API are utilized!) to process stuffs. Services are exposed using Spring MVC 4 framework. Spring is pretty much used for all of IoC work. Spring JDBC is also used to manage communication with the Database. If required, a new layer could be written to include a suitable ORM tool. The front-end is basically barebones HTML with JQuery to do the AJAX processing.
