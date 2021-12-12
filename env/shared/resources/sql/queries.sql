
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
