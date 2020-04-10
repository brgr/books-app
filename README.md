# Books

A full stack implementation of a book library. Primarily a book wishlist, but 
I would be happy to include features like a "Read" state of a book, notes on books,
tags on books, etc.

## Frontend

The fronted is currently not in this project and there is no real work done on it so
far. It will probably consist of the following components:

* ClojureScript
* Reagent, a React wrapper
* Reframe, for storing the data in a single place (sort of MVC)

More on this later, when work on it is actually started.

## Backend

The backend currently consists of the following components:

* Clojure
* MongoDB + Docker
* Clojure libraries:
  * `monger` for access to MongoDB
  * `ring` for the REST api server things
    * together with `compojure` and `compojure-api`
  * `selenium` and `Jsoup` for getting some books from Amazon
  
## Deployment

Currently the project is just run locally. There are multiple sites I could run this
on (DigitalOcean, Heroku, Google App Engine, AWS, Azure). I think DigitalOcean would
probably a good start. It would cost a fix amount of 5-15$ per month, I think,
depending on all the things I need - but these would be fairly fix.

## Ideas and Notes

* For unit testing network stuff etc.: [mockery][1]
* Seems to be similar to `testcontainers`: [docker-fixture][2]


[1]: https://github.com/igrishaev/mockery
[2]: https://github.com/brabster/docker-fixture