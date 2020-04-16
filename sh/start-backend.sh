#!/usr/bin/env bash

./stop.sh && \
  lein ring uberjar && \
  mv ../target/*-standalone.jar wishlist-docker/resources && \
  docker-compose -f ../wishlist-docker/docker-compose.yml up -d