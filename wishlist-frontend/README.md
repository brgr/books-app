# wishlist-frontend

The frontend of the books wishlist, written using the following technologies / libraries:
 - ClojureScript
 - Reagent (React wrapper)
 - Reframe (for state management)
 - shadow-cljs
 - `cljs-ajax`
 - `reitit` for routing (i.e. URL routing) [1]
   - (Note: While I am writing this, I am just at the point of introducing `reitit` to the project, so some things
     will probably change still)
   - `reitit` can be used for the backend also (instead of `compojure-api`). It is also from the same maintainer! Maybe
     at some point I'd like to remove `compojur-api` and use `reitit` instead.
 
To run app:

```bash
lein do clean, shadow watch client
```

or, better: 

```bash
make frontend
```

Generally, watch in the Makefile for how to start the frontend, backend etc.

[1]: https://github.com/metosin/reitit