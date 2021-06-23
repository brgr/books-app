# Notes

The notes I write here don't necessarily have much to do with my actual project. Sometimes they are just indirectly
linked to it.

## Docker

Some links and small notes:

- Best practices for Dockerfile:
  https://docs.docker.com/develop/develop-images/dockerfile_best-practices/
- Common use cases for Docker compose:
  https://docs.docker.com/compose/#common-use-cases

### Interacting between containers

While I can access a container on my local machine (normally) by using `localhost` or `0.0.0.0`, this can NOT be done
from inside a container. Say for example I want to access the database container from my server (ring) container. For
this to be possible I need to know the real IP address of the database container.

This can be found out with the command `docker inspect $CONTAINER_ID`. However, since this is quite cumbersome to do all
the time, and would not directly allow docker-compose to up all services at the same time, this can be circumvented by
just **using the service name** instead of the IP address of the docker container. Inside the docker container, the
service name is re-mapped to the IP address of the other container name. This is done by docker-compose.

For more info:
https://serverfault.com/questions/758225/cannot-connect-to-mongodb-in-docker
(see: service name instead of IP)

## Servers

Quick note on Jetty: Both Jetty and Tomcat are widely used. I have decided to use Jetty just by chance. I think in the
end it does not matter so much for a small application. Tomcat would have probably also been a good choice.

### WAR vs JAR

Apparently both a WAR file can be created for a server (I think WAR files are exclusively for servers), and a JAR file.
Using ring, this looks like this: `lein ring uberwar` (directly integrated in `ring`) or `lein ring uberjar` (this needs
the `lein-ring` plugin). In the execution, the WAR file needs to be placed correctly in Jetty (or Tomcat), and the JAR
file needs to be executed on the server (on the server: `java -jar file.jar`). Besides that, I currently do not know of
any difference (but I haven't really searched).

I have now switched to using the uberwar as this needs slightly fewer dependencies. It would be nice to know the
differences between these two.

### CORS Headers

These headers are needed for AJAX. I had problems because I did not set them for my whole API. It is important to set
the headers for all, not just for some single requests.

This helped me solve it: https://stackoverflow.com/questions/52745107/how-do-i-add-cors-to-a-compojure-api-app

* Update 08 May 2021:

This section was generally created much earlier, for `compojure-api` still. I am currently in the process of migrating
that library to `reitit`. In that process I had a problem indirectly related to CORS that I want to document:

In `reitit`, the allowed origins are now *regexes* - instead of simple strings as before. For a long time, I didn't
realize this. Most of the time (especially in swagger), I would then get an error that was saying something of a
`java.lang.ClassCastException` after executing an API call. I could have never really guessed that this was related to
this, as it was always only happening after sending the API call.

* Update 19 May 2021:

I have now recently had further problems with CORS. Or, actually, CORS has never even worked correctly, and I just have
not seen that because I never really tried it. So far (with the update from 08 May) I only got the code to
"compile" (well, it actually always kind of compiled but just swagger didn't run correctly - what I mean is that the
effective function of CORS was never tested after fixing that error).

Now, I have finally tested the calls on the frontend and quickly saw that I still have errors with CORS. I have
summarized everything [here][15]. What didn't work was basically `reitit`: The framework doesn't really support CORS (
yet - see [this issue on GitHub for more info][16]).

There is a workaround, which I found through googling for Luminus instead of reitit (even though the workaround
basically is about reitit). This workaround [can be found in this StackOverflow question][17], and it is basically what
I am doing now (Except some Luminus-specific stuff that is also in this answer, and that I have stripped away from my
solution. I think that is fine)

## Frontend

### What is it with this hash key (#) in the URL?

In the app URL, the `#` [comes from Reitit][19]. Normally, the hash key always means that we go to the relevant id in
the HTML document. However, in reitit, the URL starts with `#/...`, i.e., with a slash directly after the hash key.
Therefore, this is used to mark the routes for the SPA.

## Environments

I am using `environ` [[5]] for putting environment-specific variables like e.g. the database URL etc. It is from the
same creator(s) as leiningen. Also not the last part on the README from the project: It also takes into account
environment variables, which is important on docker e.g., as I am creating an uberwar on there and this needs to have an
environment variable set to recognize the env variables. This is why I have put that into the docker-compose file.

## SSH Key: With or without passphrase?

When automating access to a server, which means that the private SSH key needs to be written somewhere (and also its
passphrase, if it has one), then there is actually no real advantage in using a passphrase for the key - i.e., the
private key alone is actually enough.

For a discussion on this, see [here][1].

## Github Actions

[Awesome Github Actions][3]

### What's installed on the Github-hosted runners

https://help.github.com/en/actions/reference/software-installed-on-github-hosted-runners

### Testing Actions Locally

Maybe [this][4] can help. Maybe not. So far I have simply created a dummy branch where I push to until I am happy with
the result of the action.

### External Github Actions

Using Github Actions that are not directly from Github is not really safe! For a discussion on this, see [here][2].

### Some small notes on Github Actions

- Breaking bash commands into multiple lines is not allowed:
  https://stackoverflow.com/questions/59954185/github-action-split-long-command-into-multiple-lines

# Quick Notes

- For unit testing network stuff etc.: [mockery][6]
- Seems to be similar to `testcontainers`: [docker-fixture][7]
- Interesting
  discussion: [What technical details should a programmer of a web application consider before making the site public?][8]
- Interesting rite-up on [unit-testing (stubbing and mocking) databases and integration tests][9]
- For deployment:
    - maybe use rsync, see [[10]]
- For web scraping:
    - I think these articles are for higher scale, but they definitely contain some useful information [[11]][[12]]
- I should have a better look at reframe [[13]] and best practices for it.
    - In general, there are some things that are implemented in its example app, TodoMVC [[14]], that I could use.

# Notes for Later

These notes are meant for later, meaning that I'm currently not at the point that these notes are very useful to me, but
it is very likely that they are useful later.

## Pagination in Databases

Pagination should not be done with `limit` in databases. Instead, there are better ways to do it, read more on
it [here][18].


[1]: https://unix.stackexchange.com/questions/90853/how-can-i-run-ssh-add-automatically-without-a-password-prompt

[2]: https://stackoverflow.com/questions/57916983/github-actions-are-there-security-concerns-using-an-external-action-in-a-workfl

[3]: https://github.com/sdras/awesome-actions

[4]: https://github.com/nektos/act

[5]: https://github.com/weavejester/environ

[6]: https://github.com/igrishaev/mockery

[7]: https://github.com/brabster/docker-fixture

[8]: https://softwareengineering.stackexchange.com/questions/46716/what-technical-details-should-a-programmer-of-a-web-application-consider-before

[9]: https://softwareengineering.stackexchange.com/questions/198453/is-there-a-point-to-unit-tests-that-stub-and-mock-everything-public

[10]: https://css-tricks.com/continuous-deployments-for-wordpress-using-github-actions/

[11]: https://dev.to/iankerins/how-to-scrape-amazon-at-scale-with-python-scrapy-and-never-get-banned-44cm

[12]: https://blog.hartleybrody.com/scrape-amazon/

[13]: https://github.com/day8/re-frame

[14]: https://github.com/day8/re-frame/tree/master/examples/todomvc

[15]: https://gitlab.com/berger_/books-app/-/issues/14#note_575263100

[16]: https://github.com/metosin/reitit/issues/236#issuecomment-838301789

[17]: https://stackoverflow.com/questions/56783213/rest-api-cors-error-when-trying-to-access-it-from-a-web-application

[18]: https://use-the-index-luke.com/no-offset

[19]: https://cljdoc.org/d/metosin/reitit/0.5.12/doc/frontend/browser-integration#fragment-router