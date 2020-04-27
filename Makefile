
docker-compose-file = ./wishlist-docker/docker-compose.yml


# Note: The && here is important, otherwise it won't execute in that directory
start-frontend:
	cd wishlist-frontend && lein do clean, shadow watch client

start-backend: stop-all uberwar rebuild-jetty-ring
	docker-compose -f $(docker-compose-file) up -d


start-ring-server:
	lein ring server

rebuild-jetty-ring: stop-all uberwar
	docker-compose -f $(docker-compose-file) build jetty-ring


start-database:
	docker-compose -f $(docker-compose-file) up -d database mongo-express


stop-all:
	docker-compose -f $(docker-compose-file) down


uberwar:
	rm target/*-standalone.war || true
	lein ring uberwar
	mv -f target/*-standalone.war wishlist-docker/resources/
	ln -sf -t target/ wishlist-docker/resources/*-standalone.war


clean:
	# Remove all images that are not associated with a container
	docker system prune