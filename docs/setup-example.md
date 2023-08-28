---
title: Germinate Setup Example
---

# Germinate Setup Example

In this section we are going to show you a complete example of a full Germinate setup including Gatekeeper.

If you don't want to use Gatekeeper, simply remove the two Docker containers relating to Gatekeeper from the `docker-compose.yml` file.

In this setup, both Germinate and Gatekeeper will be running inside the same Docker compose setup each with their own MySQL Docker container.

```yaml
version: '3.3'
services:
  # This is Germinate's MySQL database.
  germinate_mysql:
    image: mysql:5.7
    # We're exposing the MySQL port for remote access at 9306
    ports:
      - 9306:3306
    volumes:
      - type: volume
        source: germinate_mysql
        target: /var/lib/mysql/
    environment:
      # The root password. This is not used by Germinate, but can be used to access the database externally
      MYSQL_ROOT_PASSWORD: germinate
      # The name of the Germinate database, e.g. "germinate". Use this for 'database.name'.
      MYSQL_DATABASE: germinate
      # The username Germinate will use to connect to this database. Use this for 'database.username'.
      MYSQL_USER: germinate-username
      # The password Germinate will use to connect to this database. Use this for 'database.password'.
      MYSQL_PASSWORD: germinate-password
    restart: unless-stopped
    # This is the name of the container. It's also what has to be used when trying to access the database from Germinate, e.g. 'database.server' is this value.
    container_name: germinate_mysql

  # This is Germinate itself
  germinate:
      image: cropgeeks/germinate:<VERSION>
      environment:
        - JAVA_OPTS:-Xmx4g
      # We're exposing port 8080 at 9080 to access the interface
      ports:
        - 9080:8080
      volumes:
        - type: bind
          # This points to where your Germinate configuration folder is outside the container
          source: ./config/germinate
          target: /data/germinate
        - type: volume
          source: germinate
          target: /usr/local/tomcat/temp
      restart: unless-stopped
      container_name: germinate
      depends_on:
        - "germinate_mysql"

  # This is Gatekeeper's MySQL container
  gatekeeper_mysql:
    image: mysql:5.7
    # We're also exposing the port, this time on 9307
    ports:
      - 9307:3306
    volumes:
      - type: volume
        source: gatekeeper_mysql
        target: /var/lib/mysql/
    environment:
      # The root password. This is not used by Gatekeeper, but can be used to access the database externally
      MYSQL_ROOT_PASSWORD: gatekeeper
      # The name of the Gatekeeper database, e.g. "gatekeeper". Use this for 'database.name'.
      MYSQL_DATABASE: gatekeeper
      # The username Gatekeeper will use to connect to this database. Use this for 'database.username'.
      MYSQL_USER: gatekeeper-username
      # The password Gatekeeper will use to connect to this database. Use this for 'database.password'.
      MYSQL_PASSWORD: gatekeeper-password
    restart: unless-stopped
    # This is the name of the container. It's used in the 'config.properties' of Gatekeeper for 'database.server'
    container_name: gatekeeper_mysql

  # This is Gatekeeper itself.
  gatekeeper:
      image: cropgeeks/gatekeeper:<VERSION>
      environment:
        - JAVA_OPTS:-Xmx512m
      # Expose port 8080 at 9081
      ports:
        - 9081:8080
      volumes:
        - type: bind
          # This points to where your Gatekeeper configuration folder is outside the container
          source: ./config/gatekeeper
          target: /data/gatekeeper
        - type: volume
          source: gatekeeper
          target: /usr/local/tomcat/temp
      restart: unless-stopped
      container_name: gatekeeper
      depends_on:
        - "gatekeeper_mysql"

volumes:
  germinate:
  germinate_mysql:
  gatekeeper:
  gatekeeper_mysql:
```

This Docker compose setup dictates some of the properties in the `config.properties` files of Germinate and Gatekeeper respectively.

For Germinate it'll look like this:

```ini
# This is the Germinate MySQL Docker container name
database.server=germinate_mysql
database.name=germinate
database.username=germinate-username
database.password=germinate-password
# Note, we're not using the 9306 port here. That's only for remote access.
database.port=3306

# We're using the Docker container name of Gatekeeper here.
gatekeeper.url=http://gatekeeper:8080
gatekeeper.username=gatekeeper-username
gatekeeper.password=gatekeeper-password

# This is only true if trying to access it from the machine running Docker.
# If you want the setup to be available from the outside world, use whatever your Proxy setup is configured as.
germinate.client.url=http://localhost:9080

# This just tells Germinate where to find this config file from inside the Docker container
data.directory.external=/data/germinate

authentication.mode=SELECTIVE
```

For Gatekeeper, the configuration should look something like this:

```ini
# This is the Gatekeeper MySQL Docker container name
database.server=gatekeeper_mysql
database.name=gatekeeper
database.username=gatekeeper-username
database.password=gatekeeper-password
# Note, we're not using the 9307 port here. That's only for remote access.
database.port=3306

email.address=
email.username=
email.password=
email.server=
email.port=

# This is only true if trying to access it from the machine running Docker.
# If you want the setup to be available from the outside world, use whatever your Proxy setup is configured as. 
web.base=http://localhost:9081/
```

Please consult the [Gatekeeper specific configuration page](https://germinateplatform.github.io/germinate-server/config.html#gatekeeper-configuration) to ensure the `gatekeeper.username` and `gatekeeper.password` properties of the Germinate configuration are properly configured.