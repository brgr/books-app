# Books Frontend

The frontend of the books wishlist, written using the following technologies / libraries:

- ClojureScript
- Reagent (React wrapper)
- Reframe (for state management)
- shadow-cljs
- `cljs-ajax`
- `reitit` for routing (i.e. URL routing) [[1]]
    - for an example where `reitit` works together with `reframe`, see [[2]]
        - this is not really trivial, and once I want to introduce book-specific routes I will likely need to have a
          look at this again

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

[2]: https://github.com/metosin/reitit/blob/master/examples/frontend-re-frame/src/cljs/frontend_re_frame/core.cljs

[3]: https://github.com/mozilla/geckodriver