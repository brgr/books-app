
docker-compose-file = ./wishlist-docker/docker-compose.yml

start-backend: stop-all uberjar
	docker-compose -f $(docker-compose-file) up -d

start-database:
	docker-compose -f $(docker-compose-file) up -d database mongo-express

stop-all:
	docker-compose -f $(docker-compose-file) down

uberjar:
	lein ring uberjar
	ln -sf -t wishlist-docker/resources/ ./target/*-standalone.jar