
-- :name create-author! :i! :raw
-- :doc Create a new author with its full name
-- TODO: We should actually insert only if it doesn't exist in alternative_names either!
insert into authors (full_name) values (:full_name) on conflict do nothing;

-- :name create-publisher! :! :n
-- :doc Create a new publisher with its full name
insert into publishers (full_name) values (:full_name);

-- :name create-book! :! :n
-- :doc Creates a book, to be exact, a specific variation of a book
insert into books (
    title, subtitle, asin, isbn_10, isbn_13, language, cover_image_id, weight, price, edition_name, number_of_pages,
    physical_dimensions, physical_format, publish_country, publish_date, publish_date_precision, description, notes,
    added, last_modified
) values (
    :title, :subtitle, :asin, :isbn_10, :isbn_13, :language, :cover_image_id, :weight, :price, :edition_name,
    :number_of_pages, :physical_dimensions, :physical_format, :publish_country, :publish_date, :publish_date_precision,
    :description, :notes, :added, :last_modified
);

-- :name create-book-publisher! :! :n
-- :doc Link a book with a publisher
insert into books_publishers (fk_book, fk_publisher) values (:book_id, :publisher_id);

-- :name create-book-author! :! :n
-- :doc Link a book with an author
insert into books_authors (fk_book, fk_author) values (:book_id, :author_id);

-- :name create-full-book! :! :n
-- :doc Creates a book together with its authors and publishers
with inserted_author as (
    -- TODO: I think currently this doesn't allow multiple authors
    -- TODO: This doesn't check in alternative_names for existence (SAME for publishers)
    insert into authors (full_name) values (:author_full_name)
    -- TODO: IntelliJ won't allow this. Is this not valid syntax? (Try it once even if IntelliJ will highlight it if uncommented)
--    on conflict do nothing
    returning id as author_id
), inserted_publisher as (
    insert into publishers (full_name) values (:publisher_full_name)
--    on conflict do nothing
    returning id as publisher_id
), inserted_book as (
    insert into books (
    title, subtitle, asin, isbn_10, isbn_13, language, cover_image_id, weight, price, edition_name, number_of_pages,
    physical_dimensions, physical_format, publish_country, publish_date, publish_date_precision, description, notes,
    added, last_modified
    ) values (
    :title, :subtitle, :asin, :isbn_10, :isbn_13, :language, :cover_image_id, :weight, :price, :edition_name,
    :number_of_pages, :physical_dimensions, :physical_format, :publish_country, :publish_date, :publish_date_precision,
    :description, :notes, :added, :last_modified)
    returning id as book_id
), inserted_book_author_connection as (
    insert into books_authors (fk_book, fk_author)
    SELECT book_id, author_id FROM inserted_book, inserted_author
    returning id as book_author_id
), inserted_book_publisher_connection as (
    insert into books_publishers (fk_book, fk_publisher)
    SELECT book_id, publisher_id FROM inserted_book, inserted_publisher
    returning id as book_publisher_id
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