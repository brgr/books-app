
create table date_precision (
    precision text primary key
);
--;;

-- Note that this table is "built" like an enum, without needing to use that specifically. Enums cannot super easily
-- be changed, which is why we preferred this way. As there is no number id saved (only the text itself), a join will
-- also not be needed, as the text is directly saved at the row that needs it. This will of course need more storage,
-- as each row saves its value (instead of just an ID number that links to it), but I think that will for now not be
-- a problem.
-- I have this idea from here: https://stackoverflow.com/a/41655273/1564218
insert into date_precision values
    ('year'),
    ('month'),
    ('day');
--;;

-- This table is meant for several book *editions*. Currently, we won't link them to a single book. This can however be
-- added as a feature in the future.
create table books (
    id serial primary key,
    title text not null,
    subtitle text,

    -- An ISBN is given to each (edition, variation) tuple. The hierarchy goes like this: book / edition / variation.
    -- As an example: Don Quijote / German Translation from 2010 by some person / paperback
    -- Editions are the different "releases" of the book, variations are the different "channels" of this "release", for
    -- example paperbacks or ebooks.
    isbn_13 text[],
    isbn_10 text[],

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
create table authors (
    id serial primary key,
    full_name text not null unique,
    alternate_names text[]
);
--;;
create table books_authors (
    id serial primary key,
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
create table publishers (
    id serial primary key,
    full_name text not null unique,
    -- For example: full_name = Penguin, alternate_names = {Penguin Books, Penguin Science, ...}
    -- Note that I won't distinguish sub-publishers (e.g. Penguin Science) for now
    alternate_names text[]
)
--;;
create table books_publishers (
    id serial primary key,
    fk_book integer not null,
    fk_publisher integer not null,

    foreign key (fk_book) references books (id)
        on delete cascade
        on update cascade,
    foreign key (fk_publisher) references publishers (id)
        on delete cascade -- We might have a publisher duplicated, in which case we allow deletion
        on update restrict
)

-- todo: genres table
-- todo: maybe: create a links table
-- todo: tags