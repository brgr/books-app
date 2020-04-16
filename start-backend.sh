#!/usr/bin/env bash

lein ring uberjar && \
 mv target/*-standalone.jar wishlist-docker/resources && \
 docker-compose -f wishlist-docker/docker-compose.yml up -d