# Books Frontend

The frontend of the books wishlist, written using the following technologies / libraries:
 - ClojureScript
 - Reagent (React wrapper)
 - Reframe (for state management)
 - shadow-cljs
 - `cljs-ajax`
 - `reitit` for routing (i.e. URL routing) [[1]]
   - for an example where `reitit` works together with `reframe`, see [[2]]
     - this is not really trivial, and once I want to introduce book-specific routes I will likely need to have a look
       at this again
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

## Notes

- I should have a better look at reframe [[3]] and best practices for it.
  - In general, there are some things that are implemented in its example app, TodoMVC [[4]], that I could use.

[1]: https://github.com/metosin/reitit
[2]: https://github.com/metosin/reitit/blob/master/examples/frontend-re-frame/src/cljs/frontend_re_frame/core.cljs
[3]: https://github.com/day8/re-frame
[4]: https://github.com/day8/re-frame/tree/master/examples/todomvc
