# File Management System

File Management System is a hybrid file storing system. The file information is saved into database.
The file content is located into logical disk. The system is exposed via API.

## Summary
* Getting Started (Prerequisites, Installing)
* Running the tests
* Deployment
* Built With
* Do you have any issue?
* Contributing
* Versioning
* Authors
* License
* Donation

## Getting Started

Clone or download a copy of this project.

### Prerequisites

This project requires Java 1.8, PostgreSQL and Maven.

### Installing

Install PostgreSQL. it is required to create a database:

```
CREATE DATABSE fms;
```

Note: in case that you run the application starting with MySQL 8.0.4, please execute the following query:
```
ALTER USER '${USER}'@'localhost' IDENTIFIED WITH mysql_native_password BY '${PASSWORD}';
-- where ${USER} and ${PASSWORD} should be provided. 
```

Please, run the following commands if it is the case:
```
createuser -U postgres -s Progress
```

Please, run the following command to import a database (if it is the case):
```
pg_restore -d DATABASE_NAME <  PATH/BACKUP_FILE_NAME.sql
```

To create the JAR file please use the following command:
```
mvn clean package
```

## Running the tests

This project does not have any kind of tests :).

## Deployment

If the build (the jar file) is ready then the application can be run. Please, use the following command to run the application:
```
XXX:file-management-system xxx java -jar target/file-management-system-X.Y.Z-SNAPSHOT.jar
```
### Deployment & run on LINUX environment
In case if application is run in a linux based instance, please create the following folders:
1. /app         - folder where the JAR is located;
2. /app/log     - folder which will contains the logs;
3. /app/config  - folder which will contains the application configuration files;
4. /app/file-db - production or working folder where service will save physically the content;
5. /app/test-db - test folder where service will save physically the content;

To run the application we have the following options:
```
nohub java -jar file-management-system-1.0.0.jar --spring.config.location=/app/config/application.properties &
```

To show last NUM_OF_RECORDS from a FILE_NAME linux command:
```
tail -n NUM_OF_RECORDS FILE_NAME
```

### Deployment & run on WINDOWS environment
The application was developed on Windows environment. 
We did not test how the application works on PRODUCTION on WINDOWS environment.

To run application, please run this command:
```
java -jar D:\projects\app\file-management-system-1.0.0.jar --spring.config.location=D:/projects/app/config/application.properties
```

## Swagger usage
You can use swagger for testing proposes:
```
URL: APP_HOST/swagger-ui.html  . For example:  http://localhost:8081/swagger-ui.html
```

## Built With

* [Java](https://www.java.com/en/download/) - Java technology allows you to work and play in a secure computing environment. Java allows you to play online games, chat with people around the world, calculate your mortgage interest, and view images in 3D, just to name a few.
* [Spring Security](https://spring.io/projects/spring-security) - Spring Security is a powerful and highly customizable authentication and access-control framework. It is the de-facto standard for securing Spring-based applications.
* [Spring Boot](https://spring.io/projects/spring-boot) - Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".
* [Spring Data](https://spring.io/projects/spring-data) - Spring Data’s mission is to provide a familiar and consistent, Spring-based programming model for data access while still retaining the special traits of the underlying data store.
* [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Spring Data JPA, part of the larger Spring Data family, makes it easy to easily implement JPA based repositories. This module deals with enhanced support for JPA based data access layers. It makes it easier to build Spring-powered applications that use data access technologies.
* [PostgreSQL](https://www.postgresql.org/) - PostgreSQL, also known as Postgres, is a free and open-source relational database management system (RDBMS) emphasizing extensibility and technical standards compliance. It is designed to handle a range of workloads, from single machines to data warehouses or Web services with many concurrent users. It is the default database for macOS Server, and is also available for Linux, FreeBSD, OpenBSD, and Windows. 
* [Maven](https://maven.apache.org/) - Apache Maven is a software project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, reporting and documentation from a central piece of information. 

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Donation
* [PayPal](https://www.paypal.me/sdrahnea) - any donation is welcomed in case that you was pleased with this work :p