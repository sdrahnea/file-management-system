# File Management System

A lightweight, API-first service for storing file metadata in a database while keeping the actual file content on disk.

Built with Spring Boot, this project is designed for teams that need a simple file storage backend with tenant-aware organization, configurable storage layouts, and straightforward upload/download endpoints.

---

## Why this project exists

Many systems need to keep **structured file metadata** in a database while storing **binary content** on the filesystem for simplicity, portability, and cost control. This project provides that hybrid model behind a REST API.

At a glance, the File Management System helps you:

- upload files through HTTP endpoints
- download files by identifier
- organize stored files by tenant and date-based directory strategies
- keep metadata in a relational database
- run locally with minimal setup using H2
- switch to PostgreSQL when needed

## Key Capabilities

### File upload
The service supports multiple upload flows, including multipart uploads and byte-array-based uploads.

### File download
Files can be retrieved by file ID, with optional tenant-aware validation.

### Flexible storage modes
Stored content can be organized using several folder layout strategies, including tenant-only and date-based structures.

### Tenant-aware organization
Tenants can be configured and optionally validated through application properties.

### API-driven integration
The platform is exposed through HTTP endpoints and documented with Swagger UI for easy exploration and testing.

## Quick Start

### Prerequisites

This project currently targets:

- **Java 8**
- **Maven**
- **A supported database configuration** such as H2 or PostgreSQL

### Default local runtime

The current default configuration in `src/main/resources/application.properties` uses:

- **Port:** `8081`
- **Database:** file-based H2 database at `./data/db`
- **Database user:** `sa`
- **Database password:** `password`
- **File storage root:** `C:/filessssss/file-db`
- **Swagger UI:** `http://localhost:8081/swagger-ui.html`
- **H2 console:** `http://localhost:8081/h2-console/`

> For anything beyond local development, update `file.db.location` to a valid path for your environment.

### Build the project

```bash
mvn clean package
```

### Run the application

```bash
java -jar target/file-management-system-1.0.7.jar
```

After startup, you can open:

- `http://localhost:8081/swagger-ui.html`
- `http://localhost:8081/h2-console/`

## Functional Overview

### Upload endpoints
The application exposes these upload routes:

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/uploadNewFile/{tenant}` | Upload a new file for a tenant |
| `POST` | `/uploadByTenantAndFileId/{tenant}/{fileId}` | Upload a file with a provided file ID |
| `POST` | `/uploadNewFile/{fileId}/{tenant}` | Upload a new file with explicit file ID and tenant |
| `POST` | `/uploadMultipartFile/{fileId}/{tenant}` | Upload multipart content for a file ID |
| `POST` | `/upload/{fileId}/{tenant}` | Upload byte-array content |
| `POST` | `/upload/{fileId}/{directory}/{tenant}` | Upload byte-array content into a specific directory |

### Download endpoints

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/download/{fileId}` | Download a file by ID |
| `GET` | `/download/{fileId}/{tenant}` | Download a file by ID with tenant verification |

## Storage Strategy

The `storage.strategy` property controls how files are organized under `file.db.location`.

| Strategy | Directory layout |
| --- | --- |
| `FILE` | `${file.db.location}/${tenant}/${fileId}` |
| `FILE_PER_DATE` | `${file.db.location}/${tenant}/${date}/${fileId}` |
| `FILE_PER_YEAR_DATE` | `${file.db.location}/${tenant}/${year}/${date}/${fileId}` |
| `FILE_PER_YEAR_MONTH` | `${file.db.location}/${tenant}/${year}/${month}/${fileId}` |
| `FILE_PER_YEAR_MONTH_DAY` | `${file.db.location}/${tenant}/${year}/${month}/${day}/${fileId}` |
| `FILE_PER_YEAR_MONTH_DATE` | `${file.db.location}/${tenant}/${year}/${month}/${date}/${fileId}` |

This makes it easy to adapt storage layout to operational needs such as cleanup, browsing, and filesystem scale.

## Configuration

The main configuration file is `src/main/resources/application.properties`.

### Database options

#### H2
No separate installation is required for local development.

The active default setup uses:

- `spring.datasource.url=jdbc:h2:file:./data/db`
- `spring.datasource.username=sa`
- `spring.datasource.password=password`

Two modes are present in configuration:

- **in-memory** mode (commented out)
- **file-based** mode (active by default)

H2 Console is available at:

```text
http://localhost:8081/h2-console/
```

#### MySQL
The README historically includes MySQL setup guidance, and the configuration file contains commented MySQL properties:

- `spring.datasource.url=jdbc:mysql://localhost:3306/fms`
- `spring.datasource.username=root`
- `spring.datasource.password=root`

Example database creation command:

```sql
CREATE DATABASE fms;
```

If you run into MySQL 8 authentication issues, the legacy compatibility command below may help depending on your environment:

```sql
ALTER USER '${USER}'@'localhost' IDENTIFIED WITH mysql_native_password BY '${PASSWORD}';
```

> Note: the current `pom.xml` does not include a MySQL connector dependency, so MySQL support is not ready out of the box without adding the corresponding driver.

#### PostgreSQL
A PostgreSQL driver is already included in the build.

The configuration file contains commented PostgreSQL settings for a local instance:

- `spring.datasource.url=jdbc:postgresql://localhost:5432/test`
- `spring.datasource.username=test`
- `spring.datasource.password=test`

Legacy setup commands preserved from the original README:

```bash
createuser -U postgres -s Progress
```

Optional restore example:

```bash
pg_restore -d DATABASE_NAME < PATH/BACKUP_FILE_NAME.sql
```

### Tenant configuration

The service supports tenant allow-list configuration through:

- `tenant.list`
- `tenant.verification`

When tenant verification is disabled, the application can operate without enforcing the configured allow-list.

### File cleanup configuration

The current properties file also includes cleanup-related settings:

- `file.cleanup.age=100`
- `file.cleanup.age.type=DAY`

### Multipart configuration

The service is configured for large file uploads with properties such as:

- `spring.servlet.multipart.max-file-size=100MB`
- `spring.servlet.multipart.max-request-size=100MB`
- `server.tomcat.max-http-post-size=-1`

## Testing

All available unit and integration tests are located in `src/test/java`.

Run the test suite with:

```bash
mvn test
```

## Deployment

Once the JAR is built, the application can be started with Java.

### Standard run command

```bash
java -jar target/file-management-system-1.0.7.jar
```

### External configuration

You can also run the application with an external properties file:

```bash
java -jar target/file-management-system-1.0.7.jar --spring.config.location=/path/to/application.properties
```

### Linux deployment notes

If you deploy on Linux, a practical folder layout can be:

1. `/app` - application home
2. `/app/log` - log files
3. `/app/config` - configuration files
4. `/app/file-db` - physical file storage
5. `/app/test-db` - test or temporary storage

Example background start command:

```bash
nohup java -jar file-management-system-1.0.7.jar --spring.config.location=/app/config/application.properties &
```

Example command to inspect recent log lines:

```bash
tail -n NUM_OF_RECORDS FILE_NAME
```

### Windows deployment notes

The project was developed on Windows and can also be started with an external config file.

Example:

```bash
java -jar /projects/app/file-management-system-1.0.7.jar --spring.config.location=/projects/app/config/application.properties
```

## API Documentation

Swagger UI is available for testing and exploration:

```text
http://localhost:8081/swagger-ui.html
```

## Built With

This project currently uses the following core technologies from the codebase and build file:

- [Java](https://www.java.com/en/download/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Web](https://spring.io/projects/spring-framework)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [H2](https://www.h2database.com/)
- [PostgreSQL](https://www.postgresql.org/)
- [Swagger / Springfox](https://springfox.github.io/springfox/)
- [Maven](https://maven.apache.org/)
- [Log4j2](https://logging.apache.org/log4j/2.x/)

## Contributing

Please read `CONTRIBUTING.md` for guidelines on contributing, pull requests, and collaboration.

## Versioning

The project follows Semantic Versioning where practical. The current version declared in `pom.xml` is `1.0.7`.

## Maintainer

This project was authored by **Sergiu Drahnea**.

LinkedIn:

- https://www.linkedin.com/in/sergiu-drahnea/

## License

This project is released under the **MIT License**. See `LICENSE` for details.

## Support the Project

If this project has been useful to you, you can support it through the following channels preserved from the original README:

- [PayPal](https://www.paypal.me/sdrahnea)
- [EGLD](http://elrond.com/) - `erd1t3t5m8v7862asdh48nq820shsmlmuw9jpm87qw25cvch7djpkapskgq4es`
- [TROY](https://troytrade.com/) - Address: `bnb136ns6lfw4zs5hg4n85vdthaad7hq5m4gtkgf23`, Memo: `100079140`
- [PHB](https://phoenix.global/) - Address: `bnb136ns6lfw4zs5hg4n85vdthaad7hq5m4gtkgf23`, Memo: `100079140`
- [HOT](https://holochain.org/) - Address: `0x1ebfc62e2510f0a0558568223d1d101d0cf074b2`
- [VET](https://www.vechain.org/) - Address: `0x1ebfc62e2510f0a0558568223d1d101d0cf074b2`
- [TRX](https://tron.network/) - Address: `TRe8xSkGqpS73Nhk6bnvW34aiJoRTmZs8N`
- [BTT](https://www.bittorrent.com/token/btt/) - Address: `TRe8xSkGqpS73Nhk6bnvW34aiJoRTmZs8N`
