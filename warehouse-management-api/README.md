# Warehouse Management Application



[`Architectural Choice Document`](./documents/Warehouse%20presentation.pptx) and [`HELP resource Document`](./HELP.md)

# README

This README would normally document whatever steps are necessary to get the application up and running.

## What is this repository for?

- REST APIs to efficiently manages inventory, products, and transactions within a warehouse environment.

## Dependencies

- Java 17
- Maven
- PostgreSQL

### Prerequisites

Ensure you have the following tools installed:

| Name                        | Download                                              |                                             |
|-----------------------------|-------------------------------------------------------|---------------------------------------------|
| Java                        | https://adoptium.net/temurin/releases/                | JDK 17 or newer                             |
| Maven                       | https://maven.apache.org/download.cgi                 | Download the Binary and extract as needed   |
| Docker                      | https://www.docker.com/                               |                                             |
| IntelliJ IDEA OR Other IDEA | https://www.jetbrains.com/idea/download/other.html    | IntelliJ The community edition is also fine |
| WSL                         | https://learn.microsoft.com/en-us/windows/wsl/install | If using Windows and Docker+WSL             |
| PostgreSQL                  | https://www.postgresql.org/                           | Needed to run local DB                      |
| Git                         | https://git-scm.com/                                  | Needed to clone repository                  |

### Environment Variables

Ensure that you have added your Java and Maven installations to your `PATH` and that the `JAVA_HOME` and `MAVEN_HOME`
environment variables are set appropriately

## Getting Started

To clone repository, run the following commands on a terminal:

`git clone https://github.com/msundlana/warehouse-software.git` this will clone the repository onto your local machine.

### Running the API

To run the api you will need to do the following

1. Setting up the database
    1. Setup and Run the Database using Docker
        - Follow the instruction in [`database-setup.md`](./docker/database/database-setup.md)
2. Setting up the application
    1. Install dependencies using `mvn clean install` or `mvn clean install -P ${PROFILE_NAME}`
       `${PROFILE_NAME} = dev|prod|test`
    2. If running through the terminal
        1. Build the application using `mvn package`
        2. Start the app
           using `mvn spring-boot:run` ([Other Spring Boot Scripts here](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/#goals))
    3. If running with your IDE just hit the play button on `WarehouseManagementApplication.java`
    4. If running using docker 
       - Update [`.env`](.env) file with environmental variables used to configure the database 
        connection settings in the [`application-prod.properties`](./src/main/resources/application-prod.properties) directory:
        `DATABASE_USERNAME=your_username
         DATABASE_PASSWORD=your_password
         DATABASE_HOST=localhost:5432`
       - Replace `your_username` and `your_password`with your actual database credentials and
       - Update the `localhost:5432` with the correct host address. Also, ensure that the PostgreSQL server is running.
       - To build the API docker image, run  `mvn spring-boot:build-image -P ${PROFILE_NAME}`
         `${PROFILE_NAME} = dev|prod|test`
       - To run the docker image, run `docker-compose up -d`
   5. The application will start
   6. Once the App is running, you can access it by navigating to `http://localhost:8080` in your web browser. 
   7. Once the App is running you can view the OpenAPI Doc at `http://localhost:8080/swagger-ui/index.html`
   8. Once the App is running you can also view `http://localhost:8080/actuator`. Monitoring our app, gathering
       metrics, and understanding traffic or the state of our database is trivial.
       The actuator mainly exposes operational information about the running application — health, metrics, info, dump,
       env, etc. It uses HTTP endpoints or JMX beans to enable us to interact with it.

### Database Model Changes

Database modelling is handled using Liquibase automatic migrations. This works by generating a diff between a database
instance and the compiled application code

> Important: the process outlined below should be followed when making any changes to the application Entity/Database
> Models

To generate a diff, first ensure that you have a running version of the database that is configured at the latest state
of the application. Then, run the following command to build the application:

```shell
mvn clean install -DskipTests
```

And thereafter, create a database changeset:

```shell
mvn liquibase:diff
```

The above command will create a new changeset file. Rename this file to contain the description of what the changeset
contains. Do not remove the date and time as this is necessary to ensure that changes to the database are applied in the
correct order

To check the current status of your database you can use the following command:

```shell
mvn liquibase:status
```

Once you have verified the changeset is correct, you can apply it to your database using:

```shell
mvn clean install -DskipTests
mvn liquibase:update
```

> Note that if the last step is not done but the application is stared, the application will automatically apply the
> changeset. So depending on what you're doing it's okay to skip this step

### Running the tests

This project uses the [spring-boot-starter-test](https://docs.spring.io/spring-boot/docs/1.5.7.RELEASE/reference/html/boot-features-testing.html) framework with the
[Maven Artifact](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test) plugin to run the full suite of tests.
Tests are annotation with @Test .

To run all tests simply execute `mvn test` from the project root. Alternatively if you are using Intellij right-click on the respective xml file and select run.

### How do I get set up? (Sections to be completed)

- Summary of set up
- Configuration
- Dependencies
- Database configuration
- How to run tests
- Deployment instructions

## Contribution guidelines

- Writing tests
- Code review
- Other guidelines





