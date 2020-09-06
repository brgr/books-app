(ns amazon.books.single-book-parser-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as spec]
            [amazon.books.single-book-parser :refer [load-book parse-html]]))

; todo: maybe change the ns name, and move this spec to that namespace then
(spec/def :amazon.books/book
  (spec/keys :req [:amazon.books/title
                   :amazon.books/authors
                   :amazon.books/amazon-book-image-front]
             :opt [:amazon.books/isbn-10
                   :amazon.books/isbn-13
                   :amazon.books/publisher
                   :amazon.books/language
                   :amazon.books/book-length
                   :amazon.books/amazon-format
                   :amazon.books/description]))

(def single-book-html-01
  {:url          "https://www.amazon.de/dp/0198779291/?coliid=IVUEOO0EISPOI&colid=13XXXLP6RR1X9&psc=1&ref_=lv_ov_lig_dp_it"
   :html-content (slurp "src/test/resources/amazon/single_books/01-madness-and-modernism.html")})
(def single-book-html-02
  {:url          "https://www.amazon.de/Now-You-Other-Essays-Design/dp/1616896248/ref=tmm_hrd_swatch_0?_encoding=UTF8&qid=1599046696&sr=8-34"
   :html-content (slurp "src/test/resources/amazon/single_books/02-now-you-see-it.html")})
(def single-book-html-03
  {:url          "https://www.amazon.de/dp/0262534800/?coliid=I1NB2M0WEXUPTC&colid=13XXXLP6RR1X9&psc=1&ref_=lv_ov_lig_dp_it"
   :html-content (slurp "src/test/resources/amazon/single_books/03-how-to-design-programs.html")})

; todo: add tests also for the direct amazon links
(deftest single-book-parse-test
  (testing "book 01"
    (let [book-01 (parse-html (:html-content single-book-html-01))]
      (is (spec/valid? :amazon.books/book book-01))
      (is (= #:amazon.books {:title                   "Madness and Modernism: Insanity in the light of modern art, literature, and thought (revised edition) (International Perspectives in Philosophy and Psychiatry) Taschenbuch – 31. Oktober 2017",
                             :authors                 ["Louis Sass"],
                             :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/418HwSEi9DL._SY344_BO1,204,203,200_.jpg",
                             :book-length             "556 Seiten",
                             :isbn-10                 "0198779291",
                             :isbn-13                 "978-0198779292",
                             :publisher               "Oxford University Press; Revised Auflage (31. Oktober 2017)",
                             :language                "Englisch"}
             book-01))))
  (testing "book 02"
    (let [book-02 (parse-html (:html-content single-book-html-02))]
      (is (spec/valid? :amazon.books/book book-02))
      (is (= #:amazon.books {:title                   "Now You See It and Other Essays on Design (Englisch) Gebundene Ausgabe – 7. November 2017",
                             :authors                 ["Michael Bierut"],
                             :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/51rj-8gPSOL._SY344_BO1,204,203,200_.jpg",
                             :book-length             "240 Seiten",
                             :isbn-10                 "1616896248",
                             :isbn-13                 "978-1616896249",
                             :publisher               "PRINCETON ARCHITECTURAL PR; 01 Auflage (7. November 2017)",
                             :language                "Englisch"}
             book-02))))
  (testing "book 03"
    (let [book-03 (parse-html (:html-content single-book-html-03))]
      (is (spec/valid? :amazon.books/book book-03))
      (= #:amazon.books {:title                   "How to Design Programs, second edition: An Introduction to Programming and Computing (Mit Press) (Englisch) Taschenbuch – 4. Mai 2018",
                         :authors                 ["Matthias Felleisen" "Matthew Flatt" "Shriram Krishnamurthi"],
                         :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/515CMMS6DML._SX258_BO1,204,203,200_.jpg",
                         :book-length             "792 Seiten",
                         :isbn-10                 "0262534800",
                         :isbn-13                 "978-0262534802",
                         :publisher               "The MIT Press; second edition Auflage (4. Mai 2018)",
                         :language                "Englisch"}
         (load-book (:url single-book-html-03))))))

(deftest ^:amazon single-book-load-test
  (testing "book 01"
    (is (= #:amazon.books{:description             "The similarities between madness and modernism are striking: defiance of convention, nihilism, extreme relativism, distortions of time, strange transformations of self, and much more. In this revised edition of a now classic work, Louis Sass, a clinical psychologist, offers a radically new vision of schizophrenia, comparing it with the works of such artists and writers as Kafka, Beckett, and Duchamp, and considering the ideas of philosophers including Nietzsche, Heidegger, Foucault, and Derrida. Here is a highly original portrait of the world of insanity, along with a provocative commentary on modernist and postmodernist culture.",
                          :publisher               "Oxford University Press; Revised Auflage (31. Oktober 2017)",
                          :isbn-13                 "978-0198779292",
                          :title                   "Madness and Modernism: Insanity in the light of modern art, literature, and thought (revised edition) (International Perspectives in Philosophy and Psychiatry) (Englisch) Taschenbuch – 31. Oktober 2017",
                          :isbn-10                 "0198779291",
                          :language                "Englisch",
                          :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/418HwSEi9DL._SY344_BO1,204,203,200_.jpg",
                          :book-length             "556 Seiten",
                          :authors                 ["Louis Sass"]})
        (load-book (:url single-book-html-01))))
  (testing "book 02"
    (is (= #:amazon.books{:description             "\"Design is a way to engage with real content, real experience,\" writes celebrated essayist Michael Bierut in this follow-up to his best-selling Seventy-Nine Short Essays on Design (2007). In more than fifty smart and accessible short pieces from the past decade, Bierut engages with a fascinating and diverse array of subjects. Essays range across design history, practice, and process; urban design and architecture; design hoaxes; pop culture; Hydrox cookies, Peggy Noonan, baseball, The Sopranos; and an inside look at his experience creating the \"forward\" logo for Hillary Clinton's 2016 presidential campaign. Other writings celebrate such legendary figures as Jerry della Femina, Alan Fletcher, Charley Harper, and his own mentor, Massimo Vignelli. Bierut's longtime work in the trenches of graphic design informs everything he writes, lending depth, insight, and humor to this important and engrossing collection.",
                          :publisher               "PRINCETON ARCHITECTURAL PR; 01 Auflage (7. November 2017)",
                          :isbn-13                 "978-1616896249",
                          :title                   "Now You See It and Other Essays on Design (Englisch) Gebundene Ausgabe – 7. November 2017",
                          :isbn-10                 "1616896248",
                          :language                "Englisch",
                          :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/51rj-8gPSOL._SY344_BO1,204,203,200_.jpg",
                          :book-length             "240 Seiten",
                          :authors                 ["Michael Bierut"]})
        (load-book (:url single-book-html-02))))
  (testing "book 03"
    (is (= #:amazon.books {:description             "A completely revised edition, offering new design recipes for interactive programs and support for images as plain values, testing, event-driven programming, and even distributed programming. This introduction to programming places computer science at the core of a liberal arts education. Unlike other introductory books, it focuses on the program design process, presenting program design guidelines that show the reader how to analyze a problem statement, how to formulate concise goals, how to make up examples, how to develop an outline of the solution, how to finish the program, and how to test it. Because learning to design programs is about the study of principles and the acquisition of transferable skills, the text does not use an off-the-shelf industrial language but presents a tailor-made teaching language. For the same reason, it offers DrRacket, a programming environment for novices that supports playful, feedback-oriented learning. The environment grows with readers as they master the material in the book until it supports a full-fledged language for the whole spectrum of programming tasks. This second edition has been completely revised. While the book continues to teach a systematic approach to program design, the second edition introduces different design recipes for interactive programs with graphical interfaces and batch programs. It also enriches its design recipes for functions with numerous new hints. Finally, the teaching languages and their IDE now come with support for images as plain values, testing, event-driven programming, and even distributed programming.",
                           :publisher               "The MIT Press; second edition Auflage (4. Mai 2018)",
                           :isbn-13                 "978-0262534802",
                           :title                   "How to Design Programs, second edition: An Introduction to Programming and Computing (Mit Press) (Englisch) Taschenbuch – 4. Mai 2018",
                           :isbn-10                 "0262534800",
                           :language                "Englisch",
                           :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/515CMMS6DML._SX258_BO1,204,203,200_.jpg",
                           :book-length             "792 Seiten",
                           :authors                 ["Matthias Felleisen" "Matthew Flatt" "Shriram Krishnamurthi"]}
           (load-book (:url single-book-html-03))))))