.DEFAULT_GOAL := build

build:
	mvn package -DskipTests

test:
	mvn test

docker-compose-build:
	cd docker && docker-compose build

docker-compose-up:
	cd docker && docker-compose up -d

.PHONY: build