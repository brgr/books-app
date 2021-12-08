
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
    added, last_modified
) VALUES (
    :title, :subtitle, :asin, :isbn_10, :isbn_13, :language, :cover_image_id, :weight, :price, :edition_name,
    :number_of_pages, :physical_dimensions, :physical_format, :publish_country, :publish_date, :publish_date_precision,
    :description, :notes, :added, :last_modified
);

-- :name create-book-publisher! :! :n
-- :doc Link a book with a publisher
INSERT INTO books_publishers (fk_book, fk_publisher) VALUES (:book_id, :publisher_id);

-- :name create-book-author! :! :n
-- :doc Link a book with an author
INSERT INTO books_authors (fk_book, fk_author) VALUES (:book_id, :author_id);

-- :name create-full-book! :! :n
-- :doc Creates a book together with its authors and publishers
WITH inserted_author AS (
    -- TODO: I think currently this doesn't allow multiple authors
    -- TODO: This doesn't check in alternative_names for existence (SAME for publishers)
    INSERT INTO authors (full_name) VALUES (:author_full_name)
    -- TODO: IntelliJ won't allow this. Is this not valid syntax? (Try it once even if IntelliJ will highlight it if uncommented)
--    on conflict do nothing
    RETURNING id AS author_id
), inserted_publisher AS (
    INSERT INTO publishers (full_name) VALUES (:publisher_full_name)
--    on conflict do nothing
    RETURNING id AS publisher_id
), inserted_book AS (
    INSERT INTO books (
    title, subtitle, asin, isbn_10, isbn_13, language, cover_image_id, weight, price, edition_name, number_of_pages,
    physical_dimensions, physical_format, publish_country, publish_date, publish_date_precision, description, notes,
    added, last_modified
    ) VALUES (
    :title, :subtitle, :asin, :isbn_10, :isbn_13, :language, :cover_image_id, :weight, :price, :edition_name,
    :number_of_pages, :physical_dimensions, :physical_format, :publish_country, :publish_date, :publish_date_precision,
    :description, :notes, :added, :last_modified)
    RETURNING id AS book_id
), inserted_book_author_connection AS (
    INSERT INTO books_authors (fk_book, fk_author)
    SELECT book_id, author_id FROM inserted_book, inserted_author
    RETURNING id AS book_author_id
), inserted_book_publisher_connection AS (
    INSERT INTO books_publishers (fk_book, fk_publisher)
    SELECT book_id, publisher_id FROM inserted_book, inserted_publisher
    RETURNING id AS book_publisher_id
)
SELECT author_id, publisher_id, book_id, book_author_id, book_publisher_id
FROM inserted_author, inserted_publisher, inserted_book,
     inserted_book_author_connection, inserted_book_publisher_connection;

-- TODO: As for what is next:
--
-- The above CTE works in general, but as can be seen in the author notes, that one especially still needs fixing.
-- Another problem is that IntelliJ doesn't work perfectly with CTEs, unfortunately.
-- For this, I think it is best to break the CTE down into multile statements instead, i.e. 5 in total (add authors,
-- add publisher, add book, connect book and authors, connect book and publisher). These should then all be called
-- from a single transaction - which is of course in CLJ code.
-- Connecting these things in CLJ code will simply make everything much easier!
-- Note: If I don't want to rewrite everything immediately, I could also first just start with separating only
-- authors, which I think is the most complex statement in this CTE here.