
docker-directory = ./docker
docker-compose-file = $(docker-directory)/docker-compose.yml


frontend:
	# Note: The && here is important, otherwise it won't execute in that directory, because the Makefile does not keep
	# track of its environment
	cd wishlist-frontend && lein do clean, shadow watch client

start-backend: docker/down docker/rebuild
	docker-compose -f $(docker-compose-file) up -d

dev/setup-images:
	mkdir -p env/dev/resources/public/img/front_matters || true
	ln -srf env/dev/resources/front_matters/*.jpg env/dev/resources/public/img/front_matters/

dev/backend: docker/down docker/database dev/setup-images ring-server

# Runs a local server on port 3000
# This has the advantage of updating changes made to it in real-time, while Docker would need to be restarted all the
# time.
ring-server:
	lein run

docker/rebuild: docker/down
	docker-compose -f $(docker-compose-file) build jetty-ring

docker/database:
	docker-compose -f $(docker-compose-file) up -d database adminer

docker/down:
	docker-compose -f $(docker-compose-file) down

prod/build:
	lein uberjar

# This runs the "prod" environment "locally". By locallly, we mean that it's run on this PC with *some* settings /
# environment variables set like it is done for dev.
# Concretely, the database is set up s.t. it accesses the local one. Other things are however not set up, e.g. the
# front_matters dir.
prod/run-locally: prod/build
	chmod +x ./target/uberjar/bookstore.jar
	DATABASE_URL='jdbc:postgresql://localhost:5432/books_dev?user=pguser&password=3ZmW9M38mX8AQGqBP' ./target/uberjar/bookstore.jar


clean:
	# Remove all images that are not associated with a container
	docker system prune


### Tests ###

# This runs only the tests that are neither :amazon, nor :integration, as is defined in project.clj
test:
	lein test

test-amazon:
	lein test :amazon

test-integration:
	lein test :integration

test-all: test test-amazon test-integration
