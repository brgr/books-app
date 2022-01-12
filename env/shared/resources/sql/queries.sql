
-- :name create-author! :insert :raw
-- :doc Create a new author with its full name
INSERT INTO authors (full_name)
SELECT :full_name
WHERE NOT EXISTS (
    SELECT id FROM authors
    WHERE full_name = :full_name OR :full_name = ANY(alternate_names)
);

-- :name get-author-by-name :? :1
-- :doc Retrieves an author by its name
SELECT * FROM authors
WHERE :full_name = full_name
   OR :full_name = ANY(alternate_names);

-- :name create-publisher! :insert :raw
-- :doc Create a new publisher with its full name
INSERT INTO publishers (full_name)
SELECT :full_name
WHERE NOT EXISTS (
    SELECT id FROM publishers
    WHERE full_name = :full_name OR :full_name = ANY(alternate_names)
);

-- :name get-publisher-by-name :? :1
-- :doc Retrieves a publisher by its name
SELECT * FROM publishers
WHERE :full_name = full_name
   OR :full_name = ANY(alternate_names);

-- :name create-book! :insert :raw
-- :doc Creates a book, to be exact, a specific variation of a book
INSERT INTO books (
    title, subtitle, asin, isbn_10, isbn_13, language, cover_image_id, weight, price, edition_name, number_of_pages,
    physical_dimensions, physical_format, publish_country, publish_date, publish_date_precision, description, notes,
    added, last_modified, fk_publisher
) VALUES (
    :title, :subtitle, :asin, :isbn_10, :isbn_13, :language, :cover_image_id, :weight, :price, :edition_name,
    :number_of_pages, :physical_dimensions, :physical_format, :publish_country, :publish_date, :publish_date_precision,
    :description, :notes, :added, :last_modified, :fk_publisher
);

-- :name create-book-author! :! :n
-- :doc Link a book with an author
INSERT INTO books_authors (fk_book, fk_author) VALUES (:book_id, :author_id);

-- :name get-first-n-books :? :*
-- :doc Gets the first n books, where n is given by the user of this SELECT clause.
--      This is a temporary SELECT clause that we have until we get pagination.
--      As for the book that is returned, it is already JOINed with its publisher, and also with
--      its authors. The authors are hereby returned as a single array per book column, containing
--      all authors of that book.
--      For both authors and publisher only their respective full names are returned. Their alternate
--      names are not returned.
SELECT
    b.id AS book_id,
    p.id AS publisher_id,
    array_agg(a.id) AS author_ids,
    b.title,
    array_agg(a.full_name) AS author_names,
    p.full_name AS publisher_name,
    b.subtitle,
    b.asin,
    b.isbn_10,
    b.isbn_13,
    b.language,
    b.cover_image_id,
    b.weight,
    b.price,
    b.edition_name,
    b.number_of_pages,
    b.physical_dimensions,
    b.physical_format,
    b.publish_country,
    b.publish_date,
    b.publish_date_precision,
    b.description,
    b.notes,
    b.added,
    b.last_modified
FROM
    books b, publishers p, authors a, books_authors
WHERE
    b.fk_publisher = p.id
        AND books_authors.fk_book = b.id
        AND books_authors.fk_author = a.id
GROUP BY
    b.id, p.id
LIMIT :n;
