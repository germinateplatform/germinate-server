---
title: Germinate Setup
---

# Germinate Setup

There are many ways in which you can run Germinate.

If you are familiar with Docker and containerization, then the Germinate Docker container is probably the best option.
It requires the least amount of configuration, assuming you already have a working Docker environment. If Docker is an alien concept to you, then a manual build of Germinate will most likely be the best option.

The following page will take you through both scenarios and explain all the necessary steps to get Germinate up and running.

## Docker

Let's start with the simpler case: Docker. We have a working Docker image of Germinate available on DockerHub (**INSERT LINK**) that you can simply pull and run on your machine/server.

Additionally you will need a MySQL database. This can either be another Docker container or an existing database server that you already have.

If you have docker-compose available, things are as simple as defining this `docker-compose.yml` file:

```yaml
version: '3.3'
services:
  tomcat:
    image: sraubach/germinate
    environment:
      - JAVA_OPTS:-Xmx512m
    ports:
      - 9080:8080
    volumes:
      - type: bind
        source: /path/to/your/germinate/config
        target: /data/germinate
      - type: volume
        source: germinate
        target: /usr/local/tomcat/temp
    restart: unless-stopped
    container_name: germinate

  mysql:
    image: mysql:5.7
    ports:
      - 9306:3306
    volumes:
      - type: volume
        source: mysql
        target: /var/lib/mysql/
    environment:
      MYSQL_ROOT_PASSWORD: PASSWORD_HERE
      MYSQL_DATABASE: GERMINATE_DATABASE_NAME
      MYSQL_USER: DATABASE_USER
      MYSQL_PASSWORD: DATABASE_PASSWORD
    restart: unless-stopped
    container_name: mysql

volumes:
  germinate:
  mysql:
```

If you don't use docker-compose, here is an example of those same instructions as basic Docker commands:

```shell script
docker volume create germinate
docker volume create mysql

docker network create germinate

docker run -d \
    --name mysql \
    --network germinate \
    -e MYSQL_ROOT_PASSWORD=ROOT_PASSWORD_HERE \
    -e MYSQL_DATABASE=GERMINATE_DATABASE_NAME \
    -e MYSQL_USER=DATABASE_USER \
    -e MYSQL_PASSWORD=DATABASE_PASSWORD \
    -v mysql:/var/lib/mysql \
    -p 9306:3306 \
    --restart unless-stopped \
    mysql:5.7

docker run -d \
    --name germinate \
    --network germinate \
    -e JAVA_OPTS=-Xmx512m \
    -v germinate:/usr/local/tomcat/temp \
    -v /path/to/your/germinate/config:/data/germinate \
    -p 9080:8080 \
    --restart unless-stopped \
    sraubach/germinate
```

## Manual setup

