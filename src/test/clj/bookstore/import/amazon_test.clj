(ns bookstore.import.amazon-test
  (:require [clojure.test :refer :all]
            [amazon.books.parse.wishlist :refer [load-books-from-amazon-wishlist-url]]
            [bookstore.import.amazon :refer [import-wishlist fully-load-books load-full-book]]))

(deftest ^:amazon import-wishlist-test
  (is (= {:successful [#:books.book{:description             "'Masterful . . . [Thompson] illuminates both the fascinating coders and the bewildering technological forces that are transforming the world in which we live.' - David Grann, author of The Lost City of Z. Facebook’s algorithms shaping the news. Uber’s cars flocking the streets. Revolution on Twitter and romance on Tinder. We live in a world constructed of computer code. Coders – software programmers – are the people who built it for us. And yet their worlds and minds are little known to outsiders. In Coders, Wired columnist Clive Thompson presents a brilliantly original anthropological reckoning with the most influential tribe in today’s world, interrogating who they are, how they think, what they value, what qualifies as greatness in their world, and what should give us pause. One of the most prominent journalists writing on technology today, Clive Thompson takes us into the minds of coders, the most quietly influential people on the planet, in a journey into the heart of the machine – and the men and women who made it.",
                                    :amazon-thumbnail-url    "https://m.media-amazon.com/images/I/516yOG7tZ4L._SS135_.jpg",
                                    :publisher               "Picador (5. März 2020)",
                                    :item-added-date         "Artikel hinzugefügt 9. April 2020",
                                    :isbn-13                 "978-1529019001",
                                    :title                   "Coders: Who They Are, What They Think and How They Are Changing Our World (Englisch) Taschenbuch – 5. März 2020",
                                    :isbn-10                 "1529019001",
                                    :language                "Englisch",
                                    :amazon-id               "I2V9EWL9YVXPTY",
                                    :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/51haIQScwvL._SY344_BO1,204,203,200_.jpg",
                                    :book-length             "448 Seiten",
                                    :authors                 ["Clive Thompson"],
                                    :amazon-url              "https://www.amazon.de/Coders-They-Think-Changing-World/dp/1529019001/ref=tmm_pap_title_0?_encoding=UTF8&coliid=I2V9EWL9YVXPTY&colid=2Y2U31UCNA1ME&qid=&sr=",
                                    :price                   ""}
                       #:books.book{:description             "\"Design is a way to engage with real content, real experience,\" writes celebrated essayist Michael Bierut in this follow-up to his best-selling Seventy-Nine Short Essays on Design (2007). In more than fifty smart and accessible short pieces from the past decade, Bierut engages with a fascinating and diverse array of subjects. Essays range across design history, practice, and process; urban design and architecture; design hoaxes; pop culture; Hydrox cookies, Peggy Noonan, baseball, The Sopranos; and an inside look at his experience creating the \"forward\" logo for Hillary Clinton's 2016 presidential campaign. Other writings celebrate such legendary figures as Jerry della Femina, Alan Fletcher, Charley Harper, and his own mentor, Massimo Vignelli. Bierut's longtime work in the trenches of graphic design informs everything he writes, lending depth, insight, and humor to this important and engrossing collection.",
                                    :amazon-thumbnail-url    "https://m.media-amazon.com/images/I/41vZOzm8+aL._SS135_.jpg",
                                    :publisher               "Princeton Architectural Press (12. März 2019)",
                                    :item-added-date         "Artikel hinzugefügt 9. April 2020",
                                    :title                   "Now You See It and Other Essays on Design (English Edition) Kindle Ausgabe",
                                    :language                "Englisch",
                                    :amazon-id               "IGECR2MGXNLUO",
                                    :amazon-book-image-front "https://m.media-amazon.com/images/I/41vZOzm8+aL._SY346_.jpg",
                                    :book-length             "1616896248",
                                    :authors                 ["Michael Bierut"],
                                    :amazon-url              "https://www.amazon.de/dp/B07P89NHMP/?coliid=IGECR2MGXNLUO&colid=2Y2U31UCNA1ME&psc=0",
                                    :price                   ""}
                       #:books.book{:description             "The similarities between madness and modernism are striking: defiance of convention, nihilism, extreme relativism, distortions of time, strange transformations of self, and much more. In this revised edition of a now classic work, Louis Sass, a clinical psychologist, offers a radically new vision of schizophrenia, comparing it with the works of such artists and writers as Kafka, Beckett, and Duchamp, and considering the ideas of philosophers including Nietzsche, Heidegger, Foucault, and Derrida. Here is a highly original portrait of the world of insanity, along with a provocative commentary on modernist and postmodernist culture.",
                                    :amazon-thumbnail-url    "https://m.media-amazon.com/images/I/418HwSEi9DL._SS135_.jpg",
                                    :publisher               "Oxford University Press; Revised Auflage (31. Oktober 2017)",
                                    :item-added-date         "Artikel hinzugefügt 9. April 2020",
                                    :isbn-13                 "978-0198779292",
                                    :title                   "Madness and Modernism: Insanity in the light of modern art, literature, and thought (revised edition) (International Perspectives in Philosophy and Psychiatry) (Englisch) Taschenbuch – 31. Oktober 2017",
                                    :isbn-10                 "0198779291",
                                    :language                "Englisch",
                                    :amazon-id               "IAKICSUW9R9O7",
                                    :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/418HwSEi9DL._SY344_BO1,204,203,200_.jpg",
                                    :book-length             "556 Seiten",
                                    :authors                 ["Louis Sass"],
                                    :amazon-url              "https://www.amazon.de/dp/0198779291/?coliid=IAKICSUW9R9O7&colid=2Y2U31UCNA1ME&psc=1",
                                    :price                   "42,35 €"}
                       #:books.book{:description             "Als sein Vater stirbt, reist Didier Eribon zum ersten Mal nach Jahrzehnten in seine Heimatstadt. Gemeinsam mit seiner Mutter sieht er sich Fotos an – das ist die Ausgangskonstellation dieses Buchs, das autobiografisches Schreiben mit soziologischer Reflexion verknüpft. Eribon realisiert, wie sehr er unter der Homophobie seines Herkunftsmilieus litt und dass es der Habitus einer armen Arbeiterfamilie war, der es ihm schwer machte, in der Pariser Gesellschaft Fuß zu fassen. Darüber hinaus liefert er eine Analyse des sozialen und intellektuellen Lebens seit den fünfziger Jahren und fragt, warum ein Teil der Arbeiterschaft zum Front National übergelaufen ist. Das Buch sorgt seit seinem Erscheinen international für Aufsehen. So widmete Édouard Louis dem Autor seinen Bestseller »Das Ende von Eddy«.",
                                    :amazon-thumbnail-url    "https://m.media-amazon.com/images/I/41j7s4VLyIL._SS135_.jpg",
                                    :publisher               "Suhrkamp Verlag; 1. Auflage (8. Mai 2016)",
                                    :item-added-date         "Artikel hinzugefügt 9. April 2020",
                                    :title                   "Rückkehr nach Reims (edition suhrkamp) Kindle Ausgabe",
                                    :language                "Deutsch",
                                    :amazon-id               "IO8WXJVOH1KSY",
                                    :amazon-book-image-front "https://m.media-amazon.com/images/I/41j7s4VLyIL._SY346_.jpg",
                                    :book-length             "215 Seiten",
                                    :authors                 ["Didier Eribon"],
                                    :amazon-url              "https://www.amazon.de/dp/B01ELJVAD6/?coliid=IO8WXJVOH1KSY&colid=2Y2U31UCNA1ME&psc=0",
                                    :price                   ""}
                       #:books.book{:amazon-id            "I2TLC3W2GU1TRB",
                                    :title                "Man's Search for Meaning by Frankl, Viktor E. (2006) Taschenbuch",
                                    :amazon-url           nil,
                                    :amazon-thumbnail-url "https://images-na.ssl-images-amazon.com/images/G/01/x-locale/communities/wishlist/no_image_2x._CB485924800_SS135_.png",
                                    :item-added-date      "Artikel hinzugefügt 9. April 2020",
                                    :price                ""}
                       #:books.book{:description             "'A statistical national treasure' Jeremy Vine, BBC Radio 2 'Required reading for all politicians, journalists, medics and anyone who tries to influence people (or is influenced) by statistics. A tour de force' Popular Science Do busier hospitals have higher survival rates? How many trees are there on the planet? Why do old men have big ears? David Spiegelhalter reveals the answers to these and many other questions - questions that can only be addressed using statistical science. Statistics has played a leading role in our scientific understanding of the world for centuries, yet we are all familiar with the way statistical claims can be sensationalised, particularly in the media. In the age of big data, as data science becomes established as a discipline, a basic grasp of statistical literacy is more important than ever. In The Art of Statistics, David Spiegelhalter guides the reader through the essential principles we need in order to derive knowledge from data. Drawing on real world problems to introduce conceptual issues, he shows us how statistics can help us determine the luckiest passenger on the Titanic, whether serial killer Harold Shipman could have been caught earlier, and if screening for ovarian cancer is beneficial. 'Shines a light on how we can use the ever-growing deluge of data to improve our understanding of the world' Nature",
                                    :amazon-thumbnail-url    "https://m.media-amazon.com/images/I/41avZxZMM4L._SS135_.jpg",
                                    :publisher               "Pelican (28. März 2019)",
                                    :item-added-date         "Artikel hinzugefügt 9. April 2020",
                                    :title                   "The Art of Statistics: Learning from Data (Pelican Books) (English Edition) Kindle Ausgabe",
                                    :language                "Englisch",
                                    :amazon-id               "I372TIWCEA46BO",
                                    :amazon-book-image-front "https://m.media-amazon.com/images/I/41avZxZMM4L._SY346_.jpg",
                                    :book-length             "1541618513",
                                    :authors                 ["David Spiegelhalter"],
                                    :amazon-url              "https://www.amazon.de/dp/B07HQDJD99/?coliid=I372TIWCEA46BO&colid=2Y2U31UCNA1ME&psc=0",
                                    :price                   ""}],
          :faulty     nil}
         (import-wishlist "https://www.amazon.de/hz/wishlist/ls/2Y2U31UCNA1ME"))))

(comment
  ; This is the big wishlist. It should have at least 513 elements.
  (def elements-of-big-wishlist
    (load-books-from-amazon-wishlist-url "https://www.amazon.de/hz/wishlist/ls/13XXXLP6RR1X9"))

  ; In the following, we only load a few of the books
  (def loaded-books
    (fully-load-books (take 200 elements-of-big-wishlist)))

  (-> loaded-books :successful count)
  (-> loaded-books :faulty count))

(comment
  (def test-wishlist "https://www.amazon.de/hz/wishlist/ls/2Y2U31UCNA1ME")
  (def test-wishlist-elements
    (load-books-from-amazon-wishlist-url test-wishlist))
  (fully-load-books test-wishlist-elements))