# File Management System

![Version](https://img.shields.io/badge/artifact-1.0.7-blue?style=flat-square)
![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=flat-square&logo=springboot)
![License](https://img.shields.io/badge/license-MIT-green?style=flat-square)
![API Docs](https://img.shields.io/badge/OpenAPI-springdoc-85EA2D?style=flat-square)

A production-ready REST API for file storage where metadata lives in a relational database and binary content is stored on disk.

This project is built for teams that need a simple, extensible file platform with tenant isolation, configurable storage layouts, usage tracking, and built-in controls for API-key access, quotas, and rate limits.

## Product Overview

File Management System helps external integrators and platform teams:

- ingest and retrieve files through a clean HTTP API
- isolate tenants at both filesystem and database query level
- choose storage path strategies without changing API contracts
- enforce tenant-level quotas and upload limits
- expose usage events for billing and analytics
- run locally with H2 or move to PostgreSQL for larger environments

## Architecture at a Glance

```text
Client Apps
   |
   v
Spring Boot API (port 8081)
  - UploadFileController
  - DownloadFileController
  - ApiKeyController
   |
   +--> Database (H2/PostgreSQL): metadata, API keys, usage events
   |
   +--> Filesystem: binary content under {file.db.location}/{tenant}/...
```

Core design choices:

- strategy pattern for storage layout (`StorageStrategyService` + `FileStorageStrategyFactory`)
- strategy pattern for file ID generation (`FileIdStrategyService` + `FileIdServiceFactory`)
- centralized configuration through `AppConfig` (avoid scattered `@Value`)
- global exception mapping via `GlobalExceptionHandler` for predictable API errors

## Key Features

### 1) Upload and download APIs

- multipart upload and raw byte-array upload variants
- download by file ID, with optional tenant check
- file metadata persisted in DB, content on disk

### 2) Multi-tenant isolation

- tenant-aware folder structure under `file.db.location`
- tenant-scoped repository queries for storage and retrieval
- optional tenant allow-list enforcement (`tenant.verification`)

### 3) Pluggable storage strategies

`storage.strategy` supports:

- `FILE`
- `FILE_PER_DATE`
- `FILE_PER_YEAR_DATE`
- `FILE_PER_YEAR_MONTH`
- `FILE_PER_YEAR_MONTH_DAY`
- `FILE_PER_YEAR_MONTH_DATE`

### 4) Monetization-ready controls

- optional API key enforcement (`api.key.verification=true`)
- per-tenant storage quota (`tenant.storage.quota.bytes`)
- rolling 24-hour upload rate limit (`tenant.upload.limit.per.day`)
- usage event tracking for upload/download activity (`UsageEvent`)

## Technology Stack

- Java 21
- Spring Boot 4.0.5
- Spring Web + Spring Data JPA
- H2 (default local DB) and PostgreSQL driver
- springdoc OpenAPI UI
- Log4j2
- Maven

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+

### Build

```bash
mvn clean package
```

### Run

```bash
java -jar target/file-management-system-1.0.7.jar
```

### Local endpoints

- API docs (springdoc): `http://localhost:8081/swagger-ui/index.html`
- API docs (compat redirect in many setups): `http://localhost:8081/swagger-ui.html`
- H2 console: `http://localhost:8081/h2-console/`

## Default Runtime Configuration

From `src/main/resources/application.properties`:

- `server.port=8081`
- `spring.datasource.url=jdbc:h2:file:./data/db-v2;AUTO_SERVER=TRUE`
- `spring.datasource.username=sa`
- `spring.datasource.password=password`
- `file.db.location=C:/filessssss/file-db`
- `storage.strategy=FILE`
- `file.id.type=` (empty means UUID default)
- `api.key.verification=false`
- `tenant.storage.quota.bytes=1073741824`
- `tenant.upload.limit.per.day=10000`

## API Surface

### Upload endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/uploadNewFile/{tenant}` | Upload new file with generated ID |
| `POST` | `/uploadByTenantAndFileId/{tenant}/{fileId}` | Upload with client-provided ID |
| `POST` | `/uploadNewFile/{fileId}/{tenant}` | Upload with explicit file ID and tenant |
| `POST` | `/uploadMultipartFile/{fileId}/{tenant}` | Multipart upload variant |
| `POST` | `/upload/{fileId}/{tenant}` | Raw byte-array upload |
| `POST` | `/upload/{fileId}/{directory}/{tenant}` | Raw upload with custom directory segment |

### Download endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/download/{fileId}` | Download by file ID |
| `GET` | `/download/{fileId}/{tenant}` | Download with tenant validation |

### API key management endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/admin/apikey/{tenant}` | Generate API key |
| `GET` | `/admin/apikey/{tenant}` | List active API keys |
| `DELETE` | `/admin/apikey/{tenant}` | Revoke all tenant keys |

## Example Requests

```bash
curl -X POST http://localhost:8081/uploadNewFile/tenant1 -F "file=@document.pdf"
curl -X GET http://localhost:8081/download/my-id --output downloaded.pdf
curl -X POST http://localhost:8081/admin/apikey/tenant1
curl -X GET http://localhost:8081/download/my-id -H "X-API-Key: fms-..." --output downloaded.pdf
```

## Logging

The project uses Log4j2 with `src/main/resources/log4j2-spring.xml`.

- console logging enabled
- rolling file appender enabled
- base log folder property: `APP_LOG_ROOT` (default in config: `/root/app/logs`)
- active levels in `application.properties`:
  - `logging.level.root=info`
  - `logging.level.org.springframework.web=info`
  - `logging.level.org.hibernate=error`

## Testing

```bash
mvn test
```

Run one test class:

```bash
mvn test -Dtest=FileStorageStrategyFactoryTest
```

## Deployment Notes

Run with external configuration:

```bash
java -jar target/file-management-system-1.0.7.jar --spring.config.location=/path/to/application.properties
```

For production-like deployments, typically:

- use PostgreSQL
- set a valid writable `file.db.location`
- configure a writable log path in `log4j2-spring.xml`
- keep `api.key.verification`, quotas, and limits enabled as required by your pricing model

## Contributing

Please read `CONTRIBUTING.md`.

## Changelog

See `CHANGELOG.md` for release history.

## License

MIT License - see `LICENSE`.

## Funding

- GitHub Sponsors: https://github.com/sponsors/sdrahnea
- PayPal: https://www.paypal.me/sdrahnea
