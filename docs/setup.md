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

```shell
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

Setting up Germinate manually involves a few steps that have to be done for every new release.

### Requirements

Server:
- Java 8 or above
- Tomcat 8.5.12 or above
- MySQL 5.7.17 or above
- Ant 1.10.1 or above

Client:
- Node.js 10.15.1 or above
- NPM 6.4.1 or above

We are going to assume that you have a running Tomcat and a running MySQL database that we can link into.

### Client

The steps involved to build the client of Germinate are as follows:

1. Download the Germinate Client code from GitHub
2. Set up the configuration file
3. Build Germinate

#### Download Germinate Client
You can either download the code from GitHub directly via the [releases](https://github.com/sebastian-raubach/germinate-vue/releases) or you can check out the latest release via the command line: 

```shell
git clone -b '3.7.0' --depth 1 https://github.com/sebastian-raubach/germinate-vue.git
```

#### Configure Germinate Client
Run this from within the root directory of the source code to install all dependencies:

```shell
npm i
```


Create a file called `.env` and add this to it:

```ini
VUE_APP_BASE_URL=/<project.name>/v<api.version>/api/
```

Where `project.name` and `api.version` come from the Germinate Server configuration below.

#### Build Germinate

Run this to build Germinate:

```shell
npm run build
```

This will generate HTML and JS files inside the `dist` directory. We will now move on to the build process of the server code.

### Server

Setting up the server involves the following steps:

1. Download the Germinate Server code from GitHub
2. Set up the configuration file and the external configuration directory
3. Include the client code in the deploy
4. Build and deploy Germinate Server to Tomcat

Let's go through these steps one at a time.

#### Download Germinate Server
You can either download the code from GitHub directly via the [releases](https://github.com/sebastian-raubach/germinate-server/releases) or you can check out the latest release via the command line: 

```shell
git clone -b '3.7.0' --depth 1 https://github.com/sebastian-raubach/germinate-server.git
```

#### Configure Germinate Server

Rename `config.template.properties` to `config.properties` and `build.template.properties` to `build.properties`.

Change `build.properties` like this:

```ini
tomcat.manager.url      = <your tomcat host>/manager/text
tomcat.manager.username = <username defined in tomcat's config/tomcat-users.xml>
tomcat.manager.password = <password defined in tomcat's config/tomcat-users.xml>

project.name = <the relative path inside tomcat, e.g. 'germinate' -> http://localhost:8080/germinate/v<api.version>>

api.version = 3.7.0
```

Change `config.properties` like this:

```ini
data.directory.external = <path to the configuration directory>
```

The configuration directory and its content are described in the <a href="config.html">configuration options</a>.

#### Include Germinate Client

Copy the whole `dist` directory within the Germinate Client source into the `web` directory within the Germinate Server source. 

#### Build Germinate Server

Once all previous steps are complete, building Germinate Server is as simple as calling:

```shell
ant deploy
```

After the build process is complete, the Germinate API will be available at the specified location (`<tomcat-url>/<project.name>/v<api.version>`).