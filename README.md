# Books

A full stack implementation of a book library. Primarily a book wishlist, but 
I would be happy to include features like a "Read" state of a book, notes on books,
tags on books, etc.

## Frontend

The frontend consists mainly of the following:

* ClojureScript
* Reagent, a React wrapper
* Reframe, for storing the data in a single place (sort of MVC)

For more information regarding the frontend, see the Readme in the respective directory.

## Backend

The backend currently consists mainly of the following components:

* Clojure
* MongoDB + Docker
* Clojure libraries:
* `ring` for the REST api server things
  * together with `compojure` and `compojure-api`

## Ideas and Notes

* For unit testing network stuff etc.: [mockery][1]
* Seems to be similar to `testcontainers`: [docker-fixture][2]
* Interesting Discussion: [What technical details should a programmer of a web application consider before making the site public?][3]
* Write-up on [unit-testing (stubbing and mocking) databases and integration tests][4]
* For parsing stuff (like e.g. the wishlist HTML from Amazon) parser combinators could eventually be used


[1]: https://github.com/igrishaev/mockery
[2]: https://github.com/brabster/docker-fixture
[3]: https://softwareengineering.stackexchange.com/questions/46716/what-technical-details-should-a-programmer-of-a-web-application-consider-before
[4]: https://softwareengineering.stackexchange.com/questions/198453/is-there-a-point-to-unit-tests-that-stub-and-mock-everything-public
