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

## Web Scraping

- For the scraping to work, Firefox and geckodriver [[3]] need to be installed Furthermore, firefox needs to be linked
  as `firefox-gecko` (see `profiles.clj`, where it can be also changed to just
  `firefox`). This is because if Firefox is the main browser that is used on the system that this is run on, it would
  not work well, because the settings of firefox are changed every run to change the user agent.