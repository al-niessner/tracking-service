# Tracking Service
Provides functionality for tracking status and other aspects pertaining to PDS products that are not captured in the Registry Service.

# Documentation
The documentation for the latest release of the Tracking Service, including release notes, installation and operation of the software are online at https://nasa-pds.github.io/tracking-service/.

If you would like to get the latest documentation, including any updates since the last release, you can execute the "mvn site:run" command and view the documentation locally at http://localhost:8080.

# Build
The software can be compiled and built with the "mvn compile" command but in order 
to create the JAR file, you must execute the "mvn compile jar:jar" command. 

In order to create a complete distribution package, execute the 
following commands: 

```
% mvn site
% mvn package
```

# Operational Release

A release candidate should be created after the community has determined that a release should occur. These steps should be followed when generating a release candidate and when completing the release.

## Clone fresh repo
```
git clone git@github.com:NASA-PDS/tracking-service.git
```

## Release with ConMan

For internal JPL use, the ConMan software package can be used for releasing software, otherwise the following sections outline how to complete these steps manually.

## Manual Release

### Update Version Numbers

Update pom.xml for the release version or use the Maven Versions Plugin, e.g.:

```
# Skip this step if this is a RELEASE CANDIDATE, we will deploy as SNAPSHOT version for testing
VERSION=1.15.0
mvn versions:set -DnewVersion=$VERSION
git add pom.xml
```

### Build and Publish Software to [Sonatype Maven Repo](https://repo.maven.apache.org/maven2/gov/nasa/pds/).

```
# For operational release
mvn clean site deploy -P release

# For release candidate
mvn clean site deploy
```

Note: If you have issues with GPG, be sure to make sure you've created your GPG key, sent to server, and have the following in your `~/.m2/settings.xml`:
```
<profiles>
  <profile>
    <activation>
      <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
      <gpg.executable>gpg</gpg.executable>
      <gpg.keyname>KEY_NAME</gpg.keyname>
      <gpg.passphrase>KEY_PASSPHRASE</gpg.passphrase>
    </properties>
  </profile>
</profiles>

```

### Push Tagged Release
```
# For Release Candidate, you may need to delete old SNAPSHOT tag
git push origin :$VERSION

# Now tag and push
git tag ${VERSION}
git push --tags

```

### Deploy Site to Github Pages

From cloned repo:
```
git checkout gh-pages

# Create specific version site
mkdir -p $VERSION

# Copy the over to version-specific and default sites
rsync -av target/site/ $VERSION
rsync -av $VERSION/* .

git add .

# For operational release
git commit -m "Deploy $VERSION docs"

git push origin gh-pages
```

### Update Versions For Development

Update `pom.xml` with the next SNAPSHOT version either manually or using Github Versions Plugin.

For RELEASE CANDIDATE, ignore this step.

```
git checkout master

# For release candidates, skip to push changes to master
VERSION=1.16.0-SNAPSHOT
mvn versions:set -DnewVersion=$VERSION
git add pom.xml
git commit -m "Update version for $VERSION development"

# Push changes to master
git push -u origin master
```

### Complete Release in Github
Currently the process to create more formal release notes and attach Assets is done manually through the [Github UI](https://github.com/NASA-PDS/tracking-service/releases/new) but should eventually be automated via script.

*NOTE: Be sure to add the `tar.gz` and `zip` from the `target/` directory to the release assets, and use the CHANGELOG generated above to create the RELEASE NOTES.*

# Snapshot Release

Deploy software to Sonatype SNAPSHOTS Maven repo:

```
# Operational release
mvn clean site deploy
```

# Maven JAR Dependency Reference

## Operational Releases
https://search.maven.org/search?q=g:gov.nasa.pds%20AND%20a:tracking-service&core=gav

## Snapshots
https://oss.sonatype.org/content/repositories/snapshots/gov/nasa/pds/tracking-service/

If you want to access snapshots, add the following to your `~/.m2/settings.xml`:
```
<profiles>
  <profile>
     <id>allow-snapshots</id>
     <activation><activeByDefault>true</activeByDefault></activation>
     <repositories>
       <repository>
         <id>snapshots-repo</id>
         <url>https://oss.sonatype.org/content/repositories/snapshots</url>
         <releases><enabled>false</enabled></releases>
         <snapshots><enabled>true</enabled></snapshots>
       </repository>
     </repositories>
   </profile>
</profiles>
```

# Docker

## Building

### Local Repository

`docker build --network=host -t tracking_service:$(git rev-parse HEAD) -f Dockerfile.local .`

### Release

`docker build --build-arg VERSION=${VERSION} --network=host -t tracking_service:${VERSION} - < Dockerfile.release`

## Running

The image is built to run tracking within tomcat. To verify the build and run the equivalent of `mvn site:run` as describe above, add `/tmp/run.sh` to the end of the docker run command.

All of the run examples below use the host network for simplicity but this can be very insecure. These are just simple examples are not intented to be how to securely run an exposed service. Use the appropriate network in your operations to meet your security needs.

Lastly, using the --publish (-p) switch of docker run, the 8080 port can be moved without having to rebuild the container.

### Preparation

Need to have a mysql up and running. How that is done is entirely up to the reader but using the mysql docker image from dockerhub.com is covered here.

1. Create space for mysql (not doing this will make your data ephemeral)
1. Create a network for connnecting mysql, tomcat, and the reset of the world
1. Get the mysql container
1. Run the server
1. Create the database tables for tracking_service
1. _Optional_ Load the database with some test data
1. Create a jdbc.properties file for to connect between your DB and tracking_service
1. run tomcat as described in sections below

```
mkdir -p /path/to/your/persistent/data/directory
docker network create -d bridge pds_ts
docker pull mysql:8.0.22
docker run --detach --rm \
           --env MYSQL_DATABASE=tracking \
           --env MYSQL_PASSWORD=your_password \
           --env MYSQL_ROOT_PASSWORD=not_overly_secure \
           --env MYSQL_USER=tracking \
           --name mysql-server \
           --network=pds_ts \
           --publish 3306:3306 \
           --volume /path/to/your/persistent/data/directory:/var/lib/mysql \
           mysql:8.0.22
docker run --interactive \
           --network=pds_ts \
           --rm mysql:8.0.22 \
           mysql --database=tracking \
                 --host=mysql-server \
                 --password='your_password' \
                 --user=tracking < /path/to/your/tracking-service/src/main/resources/schema/create-schema.sql
docker run --interactive \
           --network=pds_ts \
           --rm mysql:8.0.22 \
           mysql --database=tracking \
                 --host=mysql-server \
                 --password='your_password' \
                 --user=tracking < /path/to/your/tracking-service/src/main/resources/schema/load-schema.sql
```

__NOTE__: The `create-schema.sql` and `load-schema.sql` are in the tracking-service repository.

Place in `/path/to/your/jdbc.properties` the following content:
```
javax.persistence.jdbc.url=jdbc:mysql://mysql-server:3306/tracking?allowPublicKeyRetrieval=true&useSSL=false
javax.persistence.jdbc.user=tracking
javax.persistence.jdbc.password=your_password
javax.persistence.jdbc.driver=com.mysql.jdbc.Driver

achive.status.table.name=archive_status
certification.status.table.name=certification_status
nssdca.status.table.name=nssdca_status
```

__NOTE__: Make `your_password` anything you want. In fact any of the values can change, like 'tracking' for database as long as you do it consistently in all places.

### Local Repository

```
docker run --detach --rm \
           --network=pds_ts \
           --publish 8080:8080 \
           --read-only --volume /path/to/your/jdbc.properties:/etc/tracking_service/jdbc.properties \
           tracking_service:$(git rev-parse HEAD)
```

### Release

```
docker run --detach --rm \
           --network=pds_ts \
           --publish 8080:8080 \
           --read-only --volume /path/to/your/jdbc.properties:/etc/tracking_service/jdbc.properties \
           tracking_service:${VERSION}
```
