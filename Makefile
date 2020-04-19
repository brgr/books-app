
docker-compose-file = ./wishlist-docker/docker-compose.yml


# Note: The && here is important, otherwise it won't execute in that directory
start-frontend:
	cd wishlist-frontend && lein do clean, shadow watch client

start-backend: stop-all uberjar
	docker-compose -f $(docker-compose-file) up -d

start-ring-server:
	lein ring server

start-database:
	docker-compose -f $(docker-compose-file) up -d database mongo-express

stop-all:
	docker-compose -f $(docker-compose-file) down

uberjar:
	lein ring uberjar
	ln -sf -t wishlist-docker/resources/ ./target/*-standalone.jar