# Books

A full stack implementation of a book library. Primarily a book wishlist, but 
I would be happy to include features like a "Read" state of a book, notes on books,
tags on books, etc.

## Run locally

To run locally, open two terminals and in each run one of the following two make commands:

```
make dev/backend
make frontend
```

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
