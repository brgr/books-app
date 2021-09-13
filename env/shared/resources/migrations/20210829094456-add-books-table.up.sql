
/******************
 *   DATE UTILS   *
 ******************/

create table date_precision (
    precision text primary key
);
--;;

/*
 * Note that this table is "built" like an enum, without needing to use that
 * specifically. Enums cannot super easily be changed, which is why we
 * preferred this way. As there is no number id saved (only the text itself),
 * a join will also not be needed, as the text is directly saved at the row
 * that needs it. This will of course need more storage, as each row saves
 * its value (instead of just an ID number that links to it), but I think
 * that will for now not be a problem.
 * I have this idea from here: https://stackoverflow.com/a/41655273/1564218
 */
insert into date_precision values
    ('year'),
    ('month'),
    ('day');
--;;


/************************
 *   MAIN BOOKS TABLE   *
 ************************/

/*
 * This table is meant for several book *variations*.
 *
 * The hierarchy goes like this: book / edition / variation.
 * As an example: Don Quijote / German Translation from 2010 by some person / paperback
 * Editions are the different "releases" of the book, variations are the different "channels" of this "release", for
 * example paperbacks or ebooks.
 *
 * Currently, we don't save "editions" or "books", even though we could, and then we could link them together. This is
 * at the moment not in scope though. (This is more in scope of a project like OpenLibrary, which really wants a great
 * and open library - this project however wants a personal books library, and personally we will most likely only have
 * a specific variation of a book)
 */
create table books (
    id int primary key generated always as identity,
    title text not null,
    subtitle text,

    -- An ISBN is given to each variation
    isbn_13 text,
    isbn_10 text,
    -- ASIN = Amazon Standard Identification Number; for books, this should be equal to ISBN10, but for example for
    -- Kindle books this seems to not be the case (which is why this is a separate column here)
    asin text,

    -- For now, we will keep "language" very easy, i.e. just a text for each language
    language text,

    -- We use a UUIDv4 to make the cover images URL unguessable
    cover_image_id uuid,

    -- For now, comparing weight or similar is no goal of this application, so we will start out by storing weight as
    -- simply text
    weight text,

    -- For example, "1st ed.", "2000 edition"
    edition_name text,
    number_of_pages integer,
    physical_dimensions text, -- e.g., "21 x 14.8 x 0.8 centimeters"
    physical_format text,
    publish_country text,

    -- To be able to handle for example "1999", i.e. where we only know a year or only year/month, we further add a
    -- field date_precision, which basically stores this information. If for example a date is "2020-08-01" and
    -- publish_date_precision is set to "month", then we know that this book was published in August 2020.
    publish_date date,
    publish_date_precision text references date_precision (precision)
        on delete restrict
        on update cascade,

    description text,
    notes text,
    added timestamp,
    last_modified timestamp
);
--;;

/***************
 *   AUTHORS   *
 ***************/

create table authors (
    id int primary key generated always as identity,
    full_name text not null unique,
    alternate_names text[]
);
--;;
create table books_authors (
    id int primary key generated always as identity,
    fk_book integer not null,
    fk_author integer not null,

    foreign key (fk_book) references books (id)
        on delete cascade
        on update cascade,
    foreign key (fk_author) references authors (id)
        on delete restrict
        on update cascade
);
--;;

/******************
 *   PUBLISHERS   *
 ******************/

create table publishers (
    id int primary key generated always as identity,
    full_name text not null unique,
    -- For example: full_name = Penguin, alternate_names = {Penguin Books, Penguin Science, ...}
    -- Note that I won't distinguish sub-publishers (e.g. Penguin Science) for now
    alternate_names text[]
);
--;;
create table books_publishers (
    id int primary key generated always as identity,
    fk_book integer not null,
    fk_publisher integer not null,

    foreign key (fk_book) references books (id)
        on delete cascade
        on update cascade,
    foreign key (fk_publisher) references publishers (id)
        on delete cascade -- We might have a publisher duplicated, in which case we allow deletion
        on update restrict
);
--;;

-- Todo: a table for the import sources, e.g. original (full) amazon link, original front cover link
-- This is different than e.g. saving the amazon link, as that one can simply be generated from the ASIN, but here
-- we would like to know the full source link, with all the parameters etc.
-- --> Make a revisions table! The first revision is for example an automated import, after that we may make
--     manual revisions, or scrape some other site via which we add data. We can have a notes column which logs the
--     changes. The question would be if we need an exact diff of the objects, but I would say we don't. That remains
--     to be thought about though.

-- Todo: Genres table
-- Todo: Maybe: create a links table
-- --> this is problematic because the ASIN can already generate the amazon link, for example (therefore this would not be normalized)
-- Todo: Tags
