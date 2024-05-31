# Players Lab Project
Spring lab project with Java Enterprise Stack.  
Main goal is to create a simple REST API for managing players in a game or players in a team,  
using Spring Boot, Spring Data JPA, Spring Security, Spring Batch, Spring Boot Actuator, and Prometheus etc.

# Getting Started

For building and running the application you may need:
- [JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
- [Maven 3](https://maven.apache.org)
- [Docker](https://www.docker.com/)
- [Postman](https://www.postman.com/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) Optional, command line script provided
- [Git](https://git-scm.com/)

### Build and run with CMD
To run application with command-line, need to execute following steps.
Run shell script, included in repository:
```shell
make run
```
or
```shell
sh ./scripts/local-run.sh
```
or
```shell
java $JAVA_OPTS -jar players-lab-0.0.1-SNAPSHOT.jar --spring.config.location=${CONFIG_FILE} --logging.config=${LOG_FILE} --players.config.source=${PLAYERS_CONFIG_SOURCE}
```
NOTE: Make sure, that ```local-run.sh``` configured properly with following variables: `CONFIG_FILE`, `LOG_FILE`, `PLAYERS_CONFIG_SOURCE` (need put custom configs)  
If these variables are not set, **application will use default configuration from resources**.

### Build with Docker
To build Docker Image, there is `Makefile` provided to simplify process.   
To know all function, provided by Makefile, there is command `make help` need to be invoked.

```shell
make help                                                                                             
usage: make [target]

build                          - Compile and Build application
docker-build                   - Build Docker image for  application
docker-delete-containers       - Delete all docker stopped containers
docker-delete-image            - Delete  docker image
docker-delete-images           - Delete all docker unused images
docker-info                    - Docker Info about  docker image
docker-ls                      - List of docker images
docker-run                     - Run in Docker the  docker image
docker-scan                    - Scan for known vulnerabilities the  docker image
env                            - List of Env variables
help                           - Show help message
push-to-aws                    - Push docker image to AWS Elastic Container Registry
scan                           - Scan for known vulnerabilities the  docker image

```

Before build, following resources could be pre-configured or **application will use default configuration from resources**:
```shell
export CONFIG_SOURCE==/Users/some-path/players-lab/players-main/src/main/resources/application.yaml   
export LOG_SOURCE=/Users/some-path/players-lab/players-main/src/main/resources/spring-log4j2.xml
export PLAYERS_CONFIG_SOURCE=/Users/some-path/players-lab/players-main/src/main/resources/player.yaml
```
To build Docker image, following command must be used with `Makefile`:

1. Start Docker build process:
```shell
make docker-build
```

2. Push Docker image to AWS Elastic Container Registry:
```shell
make push-to-aws
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.3.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.3.0/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/index.html#web)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/index.html#web.reactive)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/index.html#web.security)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Batch](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/index.html#howto.batch)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/index.html#actuator)
* [Prometheus](https://docs.spring.io/spring-boot/docs/3.3.0/reference/htmlsingle/index.html#actuator.metrics.export.prometheus)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Creating a Batch Service](https://spring.io/guides/gs/batch-processing/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)


