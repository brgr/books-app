# Data Definitions

## Useful Other Schemas

***Open Library***:

I think I will (at first, at least) have a heavy look at the schemas by Open Library [[6]][6] [[7]][7].

I will probably not use all the keys of their schemata, but I will try to use most which are useful *right now*.

## Book

Open questions:

- What compromises a book? For example, is Don Quijote one book?
    - It has 2 parts, actually
    - It has many many different editions
    - (These would again have reprints, but I'm sure that I would count those into the same edition)

### Interesting

I have found several things which I will just link here, as they could be interesting:

- [OPAC][2]
- [International Cataloging Principles][3] and [its newest version][4]
- [Book Design (Wikipedia)][5]

  Writes a lot about what things a book consists of, e.g. what is put in the front matter, what types of texts exists (
  e.g. preface, acknowledgments, introduction, ...)

### Data Points

We will discuss several data points of a book here, whether they are needed or what alternative ways there are to store
them.

- How big / long can a book title be? For example: VARCHAR(100)?

  Note that for example ISBN wouldn't write something like that down. They just give a unique identifier to a book.

  In the end, I have found out [that it is anyways best to use 'text' in Postgres][1], which has no constraints on
  strings anyways!
- I have quickly stopped discussing / writing up about this in this document. Some further information can be found in
  the migration scripts themselves!

The most useful resource has definitely been the [Open Library Schemata][7]. I have used them a lot do define my own
schemas. I have put a copy of them in this folder, as they are so useful.

---

[1]: https://stackoverflow.com/a/20334221/1564218

[2]: https://de.wikipedia.org/wiki/OPAC

[3]: https://en.wikipedia.org/wiki/International_Cataloguing_Principles

[4]: https://repository.ifla.org/handle/123456789/81

[5]: https://en.wikipedia.org/wiki/Book_design

[6]: https://github.com/internetarchive/openlibrary/wiki/Library-Metadata-Standards

[7]: https://github.com/internetarchive/openlibrary-client/tree/master/olclient/schemata