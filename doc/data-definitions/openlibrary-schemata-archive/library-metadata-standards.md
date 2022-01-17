### Schemas

- [Authors](https://github.com/internetarchive/openlibrary-client/blob/master/olclient/schemata/author.schema.json)
- [Editions](https://github.com/internetarchive/openlibrary-client/blob/master/olclient/schemata/edition.schema.json)
- [Works](https://github.com/internetarchive/openlibrary-client/blob/master/olclient/schemata/work.schema.json)

### Location to publish standards

Contribute schemas to https://github.com/internetarchive/openlibrary-client/tree/master/olclient/schemata

See also:

* Wiki for technical / internal
* OL Help -> FAQ
* OL Developers -> Developer Center

### Things to standardise

* Titles (Title Case / other?)
  ** need to capture original-language titles and edition-language titles
  ** the usual English rules for Title Case are often not applicable in other languages. Storing Sentence Case is
  preferred, as it is much easier to read and can be automatically converted to Title Case if necessary.
* Subtitle rules and field splitting

*Author name (OL uses "natural order") How to handle initials / full name vs. commonly know as, aliases and pen-names.
Books of an author published under different names is meaningful in many (most?) cases.
** rather than reinvent the wheel, adopt either ISNI or VIAF identifier once found and provide URLs
** prefer the ISNI author name, disinverted, list any others under alternative names

* Roles, Multiple Authors, Editors, Illustrators etc

* Subjects: authority, case, special tags

* Formats: Hardcover / Softcover -- many synonyms

* Dimensions / weight etc... link to external librarianship standards?
  ** Source metadata may simply give 8vo, or inch-measure. Should auto-convert to approximate cm for consistency, i18n.
  ** Dimensions harvested from amazon.com are inches, while the other national amazon sites (e.g. amazon.de) use cm.

* ISBNs, fields (10, 13 + other), strip non-digits, do the checksum, validate that the target was actually published
  **A huge number of bogus ISBNs were created from bad amazon records in 2008, these should be stripped or at least
  downgraded to ASINs if they cannot be validated as extant on Worldcat

* Dates (currently free text, I have found at least one example of Asian dates with kanji month and day). Approximate
  dates?
  **It's the year that matters for copyright, that has to be consistent. Month and day are rarely useful for
  disambiguation of editions.

* External links:  VIAF, Wikidata, ISNI, perhaps wikipedia
  **confusion over ASIN and OCAID where users sometimes paste URLs instead of ids.
  **Many of the other ids are also mishandled in the current UI (e.g. BNF). A preview would help.
  **Whether the URL or just the basic id is the input, the full URL ought to be resolved and validated at edit time.
  **For external links from a OLnnnnA author record, full URLs should be stored for the titles "VIAF", "ISNI", and "
  Wikidata"
  **External links should not be stored for undifferentiated records

* Allowable fields in OL records, currently there can be free fields on records, do we want this / how to manage it if
  we do?

* Which things are arrays, should they be? i.e. an edition's 'works' is an array, but no edition has more than one(?)
  Same with 'publishers'
  **Allowing more than one work record per edition might be a helpful step towards merging them. Alternately allow the
  various work records to link back to the oldest (or per WorldCat's algorithm to the smallest-valued identifier)

* Field standards, some keys are `named-key: {type/value}` [last_modified, created],
  others `named-key: value [isbn_13, publish_date]`, and others `named-key: {key: value}` [type]

* Clarity of why and which, document and explain for users + check consistency when adding new fields.

* Unicode -- NFC.. what are the implications for search? Define expectations and create some tests for Solr behaviour?
  ** for searches and type-ahead behaviours must use case-insensitive, diacritic-insensitive matching

* Other -- please add

