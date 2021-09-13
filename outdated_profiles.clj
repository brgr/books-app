{:dev
 {:env {:database-url     "0.0.0.0"
        :thumbnails-dir   "img/thumbnails/"
        :front-matter-dir "img/front_matters/"

        :headless-scraping "true"

        :user-agents-file "src/main/resources/useragents.txt"
        ; the following need to be set up like this on the machine the code is running on
        :firefox-profile-prefs-template-file "src/main/resources/prefs-template.js"
        :firefox-profile-directory "/home/dominik/.mozilla/firefox/djhmwgxs.useragent-switcher/"
        ; this is just a normal firefox browser, but since it is a separate installation it was renamed
        :firefox-browser-name "firefox-gecko"}}
 }