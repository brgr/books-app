
docker-directory = ./docker
docker-compose-file = $(docker-directory)/docker-compose.yml


# Note: The && here is important, otherwise it won't execute in that directory (the Makefile does not keep track of its
# environment)
frontend:
	cd wishlist-frontend && lein do clean, shadow watch client

start-backend: docker/down uberwar docker/rebuild
	docker-compose -f $(docker-compose-file) up -d

dev/setup-images:
	mkdir -p public/img/thumbnails || true
	mkdir -p public/img/front_matters || true
	ln -srf env/dev/resources/front_matters/*.jpg public/img/front_matters/

dev/backend: docker/down docker/database dev/setup-images ring-server

# Runs a local server on port 3000
# This has the advantage of updating changes made to it in real-time, while Docker would need to be restarted all the
# time.
ring-server:
	lein run

docker/rebuild: docker/down uberwar
	docker-compose -f $(docker-compose-file) build jetty-ring

docker/database:
	docker-compose -f $(docker-compose-file) up -d database adminer

docker/down:
	docker-compose -f $(docker-compose-file) down

uberwar:
	rm target/*-standalone.war || true
	lein ring uberwar
	mv -f target/*-standalone.war $(docker-directory)/resources/
	ln -sf -t target/ $(docker-directory)/resources/*-standalone.war

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
