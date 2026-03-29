# AGENTS.md — AI Agent Guide for File Management System

## Project Overview

Spring Boot 2.4.4 / Java 8 REST service. Files are stored **split**: metadata in a JPA database (H2 default, PostgreSQL optional) and binary content on the local filesystem. Version: **1.0.7**, runs on port **8081**.

Build and run:
```bash
mvn clean package                   # build JAR → target/file-management-system-1.0.7.jar
mvn spring-boot:run                 # run locally (H2, port 8081)
mvn test                            # run all unit tests
java -jar target/file-management-system-1.0.7.jar  # run packaged JAR
```

Swagger UI available at: `http://localhost:8081/swagger-ui.html`

---

## Package Structure

```
com.fms
├── config/         AppConfig (all @Value injection), SpringFoxConfig (Swagger)
├── controller/     UploadFileController, DownloadFileController, ApiKeyController
├── service/        UploadFileService, DownloadFileService, TenantService,
│   │               ApiKeyService, QuotaService, UsageService
│   ├── id/         FileIdServiceFactory + strategies (UUID, INSTANT)
│   └── storage/    FileStorageStrategyFactory + 6 strategy implementations
├── model/          JPA entities (FileEntity, ApiKeyEntity, UsageEvent) + DTOs
├── repository/     FileRepository, ApiKeyRepository, UsageEventRepository
├── scheduler/      FileCleanUpScheduler (cron every 5 min)
├── exception/      GlobalExceptionHandler + typed exceptions
└── util/           DateUtils, FileUtils, MapHelper
```

---

## Dual Strategy Pattern

**All new strategies follow this exact pattern:**

### Storage Strategy (`storage.strategy` property)
- Interface: `StorageStrategyService.store(StorageDTO) → Map<String,String>` returning `FILE_ID` and `FILE_PATH`
- Factory: `FileStorageStrategyFactory` selects at startup via `StorageStrategy` enum
- Values: `FILE | FILE_PER_DATE | FILE_PER_YEAR_DATE | FILE_PER_YEAR_MONTH | FILE_PER_YEAR_MONTH_DAY | FILE_PER_YEAR_MONTH_DATE`
- Path pattern: `{file.db.location}/{tenant}/[{year}/][{month}/][{day}/]{fileId}`

### File ID Strategy (`file.id.type` property, default `UUID`)
- Factory: `FileIdServiceFactory.create()` → delegates to `FileIdStrategyService.createId()`
- Values: `UUID` (safe for any traffic) | `INSTANT` (epoch nanos — only for low traffic)

To add a new strategy: add enum value → create `@Service` implementing the interface → add case in factory switch.

---

## Configuration (all properties in `application.properties`)

| Property | Default | Purpose |
|---|---|---|
| `file.db.location` | `C:/filessssss/file-db` | Root filesystem path for stored files |
| `storage.strategy` | `FILE` | Folder layout strategy |
| `file.id.type` | `UUID` | File identifier generation strategy |
| `tenant.list` | `tenant1,...` | Comma-separated allowed tenants |
| `tenant.verification` | `false` | Enable/disable tenant allowlist check |
| `api.key.verification` | `false` | Enable/disable API key auth on upload/download |
| `tenant.storage.quota.bytes` | `1073741824` (1 GiB) | Max storage per tenant |
| `tenant.upload.limit.per.day` | `10000` | Rate limit: uploads per rolling 24 h |
| `file.cleanup.age` / `file.cleanup.age.type` | `100` / `DAY` | Auto-delete files older than N days/months/years |

All config is centralised in `AppConfig` — always inject `AppConfig`, never use `@Value` in services directly.

---

## Upload / Download Flow

**Upload:** `POST /uploadNewFile/{tenant}` (multipart) or `POST /upload/{fileId}/{tenant}` (byte array)
1. `TenantService.checkIfTenantIsAllowed(tenant)` — optional gate
2. `QuotaService.assertUploadAllowed(tenant, size)` — checks storage quota + rate limit
3. `FileStorageStrategyFactory.getStorageStrategyMode().store(storageDto)` — writes bytes to disk
4. `FileRepository.save(FileEntity)` — persists metadata (fileId, path, tenant, size, contentType, checksum)
5. `UsageService.recordUpload(...)` — appends `UsageEvent` for rate-limit tracking

**Download:** `GET /download/{fileId}` or `GET /download/{fileId}/{tenant}`
- Looks up path in `FileRepository`, reads bytes from disk via `Files.readAllBytes`

---

## Exception Handling

All exceptions extend a base type and are caught by `GlobalExceptionHandler` (`@RestControllerAdvice`), which returns `ErrorDTO { code, message, details }`. HTTP status mappings:
- `FileNotFoundException` → 404
- `ApiKeyUnauthorizedException` → 401
- `TenantIsNotAllowedException` → 403
- `StorageQuotaExceededException` / `RateLimitExceededException` → 429

Add new exceptions by: creating the exception class → adding a handler method in `GlobalExceptionHandler`.

---

## API Key Management

Admin endpoints at `/admin/apikey/{tenant}` (POST generate, GET list, DELETE revoke all).  
Keys have format `fms-{uuid-no-dashes}`. Enabled via `api.key.verification=true`; when disabled, `ApiKeyService.validateAndGetTenant` returns `null` without throwing.

---

## Testing Conventions

Tests live in `src/test/java/com/fms/`. Each service and strategy has a dedicated unit test class. Factory tests (e.g., `FileIdServiceFactoryTest`, `FileStorageStrategyFactoryTest`) verify strategy selection by enum value. Run individual tests with:
```bash
mvn test -Dtest=FileStorageStrategyFactoryTest
```

---

## Key Files to Read First

- `src/main/resources/application.properties` — all tuneable knobs
- `src/main/java/com/fms/config/AppConfig.java` — single source of truth for injected config
- `src/main/java/com/fms/service/storage/FileStorageStrategyFactory.java` — strategy wiring
- `src/main/java/com/fms/model/StorageStrategy.java` — storage strategy enum with path-pattern docs
- `src/main/java/com/fms/exception/GlobalExceptionHandler.java` — all error response shapes

