
docker-compose-file = ./wishlist-docker/docker-compose.yml


# Note: The && here is important, otherwise it won't execute in that directory (the Makefile does not keep track of its
# environment)
frontend:
	cd wishlist-frontend && lein do clean, shadow watch client

start-backend: docker/down uberwar docker/rebuild
	docker-compose -f $(docker-compose-file) up -d

dev/backend: docker/database ring-server

ring-server:
	lein ring server

docker/rebuild: docker/down uberwar
	docker-compose -f $(docker-compose-file) build jetty-ring

docker/database:
	docker-compose -f $(docker-compose-file) up -d database mongo-express

docker/down:
	docker-compose -f $(docker-compose-file) down

uberwar:
	rm target/*-standalone.war || true
	lein ring uberwar
	mv -f target/*-standalone.war wishlist-docker/resources/
	ln -sf -t target/ wishlist-docker/resources/*-standalone.war

clean:
	# Remove all images that are not associated with a container
	docker system prune