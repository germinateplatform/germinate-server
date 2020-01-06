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
    restart: unless-stopped
    container_name: mysql

volumes:
  germinate:
  mysql:
```

## Manual setup

