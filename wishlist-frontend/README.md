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

## Notes

- For the scraping to work, Firefox and geckodriver [[3]] need to be installed Furthermore, firefox needs to be linked
  as `firefox-gecko` (see `profiles.clj`, where it can be also changed to just
  `firefox`). This is because if Firefox is the main browser that is used on the system that this is run on, it would
  not work well, because the settings of firefox are changed every run to change the user agent.

[1]: https://github.com/metosin/reitit

[2]: https://github.com/metosin/reitit/blob/master/examples/frontend-re-frame/src/cljs/frontend_re_frame/core.cljs

[3]: https://github.com/mozilla/geckodriver