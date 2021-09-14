
-- #:books.book{:description             "Schöne neue Welt spielt im Jahr 632 nach FordGemeinschaftlichkeit, Einheitlichkeit,BeständigkeitBrut-und NormzentralengezüchtetEntkorkunggenormtWeisheitendas Problem des Glücksdas Problem, wie man Menschen dahinbringt, ihr Sklaventum zu liebenSomaWildeFabrikationsfehler bei Bernard zieht nach sich,dass er dem Idealbild der ihm zugedachten Kaste nicht entspricht und anachronistischeLiebesgefühle für Lenina Crowne entwickelt. Helmholtz bildet aufgrund seinergeistigen Überlegenheit eine nicht staatskonforme Individualität aus. Savageund Helmholtz versuchen einen Aufstand anzuzetteln, doch sie scheiternHelmholtz und Bernard müssen ins Exil, Savage bleibt schließlich alleinder Se ...",
--              :amazon-thumbnail-url    "https://m.media-amazon.com/images/I/51vGyHGCIML._SS135_.jpg",
--              :publisher               "Fischer; 66. Auflage (1. Januar 1980)",
--              :item-added-date         "Artikel hinzugefügt 21. Oktober 2016",
--              :isbn-13                 "978-3596200269",
--              :title                   "Schöne neue Welt: Ein Roman der Zukunft (Deutsch) Taschenbuch – 1. Januar 1980",
--              :isbn-10                 "3596200261",
--              :language                "Deutsch",
--              :amazon-id               "I17LDHI6F5KX8N",
--              :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/51vGyHGCIML._SY344_BO1,204,203,200_.jpg",
--              :book-length             "256 Seiten",
--              :authors                 ["Aldous Huxley"],
--              :amazon-url              "https://www.amazon.de/dp/3596200261/?coliid=I17LDHI6F5KX8N&colid=13XXXLP6RR1X9&psc=0",
--              :price                   "1,94 €"

insert into authors (full_name) values ('Aldous Huxley');

insert into publishers (full_name) values ('Fischer');

insert into books (
    title,
    subtitle,
    language,
    cover_image_id,
    edition_name,
    number_of_pages,
    added,
    description,
    asin,
    isbn_10,
    isbn_13,
    publish_date,
    publish_date_precision,
    physical_format,
    price
) values (
    'Schöne neue Welt',
    'Ein Roman der Zukunft',
    'Deutsch',
    'c2d29867-3d0b-d497-9191-18a9d8ee7830', -- I have copied this from the web now; effectively, generate it programmatically!
    '66. Auflage',
    256,
    '2016-10-21',
    'Schöne neue Welt spielt im Jahr 632 nach FordGemeinschaftlichkeit, Einheitlichkeit,BeständigkeitBrut-und NormzentralengezüchtetEntkorkunggenormtWeisheitendas Problem des Glücksdas Problem, wie man Menschen dahinbringt, ihr Sklaventum zu liebenSomaWildeFabrikationsfehler bei Bernard zieht nach sich,dass er dem Idealbild der ihm zugedachten Kaste nicht entspricht und anachronistischeLiebesgefühle für Lenina Crowne entwickelt. Helmholtz bildet aufgrund seinergeistigen Überlegenheit eine nicht staatskonforme Individualität aus. Savageund Helmholtz versuchen einen Aufstand anzuzetteln, doch sie scheiternHelmholtz und Bernard müssen ins Exil, Savage bleibt schließlich alleinder Se ...',
    '3596200261',
    '3596200261',
    '978-3596200269',
    '1980-01-01',
    'day',
    'Taschenbuch',
    194
);
