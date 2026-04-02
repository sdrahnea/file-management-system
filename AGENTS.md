# AGENTS.md — AI Agent Guide for File Management System

> **Version**: 1.0.7 | **Last Updated**: April 2, 2026 | **Technology**: Java 8 + Spring Boot 2.4.4

---

## Quick Project Overview

**File Management System** is a Spring Boot REST API that splits file storage: **metadata in a relational database** (H2/PostgreSQL/MySQL) and **binary content on the filesystem**. The system is multi-tenant, configurable, and production-ready.

- **Runs on**: Port 8081
- **Default database**: H2 (file-based)
- **Build**: `mvn clean package` → `target/file-management-system-1.0.7.jar`
- **Run**: `java -jar target/file-management-system-1.0.7.jar`
- **Swagger UI**: `http://localhost:8081/swagger-ui.html`

---

## Essential Architecture Patterns

### 1. **Strategy Pattern for Storage & ID Generation**

The system uses **two orthogonal strategy factories** for extensibility:

#### Storage Strategy (`storage.strategy` property)

- **Interface**: `StorageStrategyService` (method: `store(StorageDTO) → Map<String,String>`)
- **Factory**: `FileStorageStrategyFactory` (field: `storageStrategy`, method: `getStorageStrategyMode()`)
- **Six Strategies** (enums in `StorageStrategy.java`):
  1. `FILE` → `{root}/{tenant}/{fileId}`
  2. `FILE_PER_DATE` → `{root}/{tenant}/{YYYY-MM-DD}/{fileId}`
  3. `FILE_PER_YEAR_DATE` → `{root}/{tenant}/{YYYY}/{YYYY-MM-DD}/{fileId}`
  4. `FILE_PER_YEAR_MONTH` → `{root}/{tenant}/{YYYY}/{MM}/{fileId}`
  5. `FILE_PER_YEAR_MONTH_DAY` → `{root}/{tenant}/{YYYY}/{MM}/{DD}/{fileId}`
  6. `FILE_PER_YEAR_MONTH_DATE` → `{root}/{tenant}/{YYYY}/{MM}/{YYYY-MM-DD}/{fileId}`

**To add a storage strategy**:
1. Add enum value to `StorageStrategy`
2. Create `@Service` implementing `StorageStrategyService`
3. Inject in `FileStorageStrategyFactory`
4. Add `case` in factory's `getStorageStrategyMode()` switch

#### File ID Strategy (`file.id.type` property, default: UUID)

- **Interface**: `FileIdStrategyService` (method: `createId() → String`)
- **Factory**: `FileIdServiceFactory` (method: `create()`)
- **Two Strategies**:
  1. `UUID` → `3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e` (safe for all traffic)
  2. `INSTANT` → `1743667428123456789` (nanoseconds, low-traffic only, collision risk)

**To add an ID strategy**:
1. Create `@Service` implementing `FileIdStrategyService`
2. Add case in `FileIdServiceFactory.create()` switch

---

### 2. **Centralized Configuration (`AppConfig.java`)**

All `@Value` injections are **centralized in one class**, never scattered across services. This is the **single source of truth** for application properties.

```java
@Configuration
public class AppConfig {
    private final List<String> tenantList;
    private final String fileDbLocation;
    private final boolean tenantVerification;
    private final boolean apiKeyVerification;
    private final long tenantStorageQuotaBytes;
    private final long tenantUploadLimitPerDay;
    // ... getters
}
```

**Inject this, not @Value directly**:
```java
// ✓ Good
@Autowired
private AppConfig appConfig;
appConfig.getFileDbLocation();

// ✗ Bad
@Value("${file.db.location}")
private String fileDbLocation;
```

---

### 3. **Global Exception Handling**

All exceptions map to HTTP status codes via `GlobalExceptionHandler`:

| Exception | Status | Error Code |
|-----------|--------|-----------|
| `FileNotFoundException` | 404 | `FILE_NOT_FOUND` |
| `ApiKeyUnauthorizedException` | 401 | `UNAUTHORIZED` |
| `TenantIsNotAllowedException` | 403 | `TENANT_NOT_ALLOWED` |
| `StorageQuotaExceededException` | 429 | `STORAGE_QUOTA_EXCEEDED` |
| `RateLimitExceededException` | 429 | `RATE_LIMIT_EXCEEDED` |
| Generic `Exception` | 500 | `INTERNAL_ERROR` |

Response format (always `ErrorDTO`):
```json
{
  "code": "ERROR_CODE",
  "message": "Human readable message",
  "details": "Optional technical details"
}
```

**To add a new exception type**:
1. Create exception class extending `RuntimeException`
2. Add `@ExceptionHandler` method in `GlobalExceptionHandler`
3. Return `ResponseEntity<ErrorDTO>` with appropriate HTTP status

---

## Key Files to Read First

| File | Purpose | Key Concepts |
|------|---------|--------------|
| `src/main/resources/application.properties` | All configuration (tuneable knobs) | 15+ config properties |
| `src/main/java/com/fms/config/AppConfig.java` | Centralized config injection | Never use `@Value` elsewhere |
| `src/main/java/com/fms/service/storage/FileStorageStrategyFactory.java` | Storage strategy wiring | Switch statement on enum |
| `src/main/java/com/fms/service/id/FileIdServiceFactory.java` | File ID strategy wiring | Default UUID, optional INSTANT |
| `src/main/java/com/fms/model/StorageStrategy.java` | Storage strategy enum | All path patterns documented |
| `src/main/java/com/fms/exception/GlobalExceptionHandler.java` | All error handling | 6 exception types |
| `src/main/java/com/fms/controller/UploadFileController.java` | 6 upload endpoints | Multipart + byte-array variants |
| `src/main/java/com/fms/controller/DownloadFileController.java` | 2 download endpoints | Basic + tenant-aware |
| `src/main/java/com/fms/service/QuotaService.java` | Quota + rate limiting | Checks in correct order |
| `src/main/java/com/fms/scheduler/FileCleanUpScheduler.java` | Auto-delete old files | Runs every 5 minutes |

---

## Critical Workflows

### Upload Workflow

```
Controller.upload()
  → TenantService.checkIfTenantIsAllowed(tenant)          [optional, if configured]
  → QuotaService.assertUploadAllowed(tenant, size)       [checks storage + rate limit]
    ├─ FileRepository.sumStorageByTenant()                [aggregate query]
    └─ UsageEventRepository.countUploadsLast24h()         [rolling window query]
  → FileStorageStrategyFactory.getStorageStrategyMode()   [select strategy]
  → StorageStrategyService.store(StorageDTO)              [write to disk]
    └─ Returns: {FILE_ID, FILE_PATH}
  → FileRepository.save(FileEntity)                       [persist metadata]
  → UsageService.recordUpload()                           [track usage event]
  → Return CreateFileResponseDTO
```

**Transaction scope**: Entire method is `@Transactional` (atomic)

### Download Workflow

```
Controller.download(fileId, tenant?)
  → TenantService.checkIfTenantIsAllowed(tenant)          [optional]
  → FileRepository.findByFileIdAndTenant()                [or just findByFileId()]
  → If empty: throw FileNotFoundException(404)
  → Files.readAllBytes(path)                              [read from disk]
  → UsageService.recordDownload()                         [track usage]
  → Return byte[]
```

### API Key Validation Flow

When `api.key.verification=true`:

```
Extract X-API-Key header
  → ApiKeyService.validateAndGetTenant(apiKey)
    └─ ApiKeyRepository.findActiveByKeyValue(apiKey)
  → If not found or inactive: throw ApiKeyUnauthorizedException(401)
  → Return tenant for this key (or null if feature disabled)
```

---

## Multi-Tenant Isolation

Tenants are **hard boundaries**:

1. **File Organization**: `{file.db.location}/{tenant}/...`
2. **Database Queries**: Always filter by `tenant` column
3. **Quotas**: Per-tenant storage quota + rate limit
4. **API Keys**: Keys belong to one tenant
5. **Cross-Tenant Check**: Download with tenant parameter verifies ownership

**Example tenant queries**:
```java
fileRepository.findByFileIdAndTenant(fileId, tenant)      // Scoped query
fileRepository.sumStorageByTenant(tenant)                 // Quota aggregation
usageEventRepository.findByTenantAndTimestampGreaterThan() // Rate limit check
```

---

## Quota & Rate Limiting

Both enforced before storage via `QuotaService.assertUploadAllowed()`:

### Storage Quota

```
Used Bytes = SUM(FileEntity.fileSizeBytes WHERE tenant = ?)
Allowed = (Used Bytes + Incoming File Size) <= tenant.storage.quota.bytes
```

**Property**: `tenant.storage.quota.bytes` (default: 1 GiB = 1,073,741,824)

### Rate Limiting

```
Uploads Last 24h = COUNT(UsageEvent WHERE tenant = ? AND timestamp > now - 24h AND event_type = 'UPLOAD')
Allowed = Uploads Last 24h < tenant.upload.limit.per.day
```

**Property**: `tenant.upload.limit.per.day` (default: 10,000)

**Rolling Window**: Every 24-hour period, not calendar day

---

## Testing Conventions

- **Location**: `src/test/java/com/fms/`
- **Framework**: JUnit 4 (via `spring-boot-starter-test`)
- **Annotations**: `@RunWith(SpringRunner.class)`, `@SpringBootTest`, `@MockBean`, `@InjectMocks`
- **Mocking**: Mockito for repository mocks

**Run tests**:
```bash
mvn test                                    # All tests
mvn test -Dtest=FileStorageStrategyFactoryTest  # Specific class
mvn test -Dtest=FileStorageStrategyFactoryTest#testMethodName  # Specific method
```

---

## Database Configuration

**Default**: H2 file-based (`./data/db`)

**Supported**: H2, PostgreSQL, MySQL

**Key Property**: `spring.jpa.hibernate.ddl-auto=update` (safe for prod, but see schema notes)

**Entities**:
- `FileEntity` (file metadata)
- `ApiKeyEntity` (API keys)
- `UsageEvent` (upload/download tracking)

---

## Configuration Properties (All Tuneable)

| Property | Default | Scope | Notes |
|----------|---------|-------|-------|
| `server.port` | 8081 | Server | Port application listens on |
| `file.db.location` | `C:/filessssss/file-db` | File Storage | Root directory for files |
| `storage.strategy` | `FILE` | File Storage | 6 options (see StorageStrategy enum) |
| `file.id.type` | (empty→UUID) | File ID | `UUID` or `INSTANT` |
| `tenant.list` | `tenant1,...` | Tenant | Comma-separated allowed tenants |
| `tenant.verification` | `false` | Tenant | Enable allowlist check |
| `api.key.verification` | `false` | Security | Enable API key auth |
| `tenant.storage.quota.bytes` | 1073741824 | Quota | Per-tenant storage limit |
| `tenant.upload.limit.per.day` | 10000 | Rate Limit | Max uploads per tenant per 24h |
| `file.cleanup.age` | 100 | Cleanup | Delete threshold |
| `file.cleanup.age.type` | DAY | Cleanup | Time unit: DAY/MONTH/YEAR |
| `spring.servlet.multipart.max-file-size` | 100MB | Upload | Max single file size |
| `spring.datasource.url` | (H2 file) | Database | JDBC connection URL |

---

## Common Development Tasks

### Add a New Upload Endpoint

1. Add method to `UploadFileController`
2. Inject `TenantService` and `UploadFileService`
3. Call `tenantService.checkIfTenantIsAllowed(tenant)`
4. Call appropriate `uploadFileService.upload...()` method
5. Let exception handler manage errors

### Add a New Storage Strategy

1. Add enum value to `StorageStrategy`
2. Create `@Service` implementing `StorageStrategyService`
   - Method: `store(StorageDTO) → Map<String,String>` (returns `FILE_ID` and `FILE_PATH`)
3. Inject service in `FileStorageStrategyFactory`
4. Add `case` in factory's `getStorageStrategyMode()` switch
5. Add unit test extending `FileStorageStrategyServiceTest`

### Add a New Exception Type

1. Create exception class extending `RuntimeException`
2. Add `@ExceptionHandler` method in `GlobalExceptionHandler`
3. Return `ResponseEntity<ErrorDTO>` with HTTP status
4. Document in error response table

### Toggle Features at Runtime

Edit `application.properties`:
- `api.key.verification`: Enable/disable API key auth
- `tenant.verification`: Enable/disable tenant allowlist
- `storage.strategy`: Change storage layout (requires restart)
- `file.id.type`: Change ID generation (requires restart)
- Quotas/rate limits: Immediate effect (no restart)

---

## Monetization Built-In Features

The system has native support for:

1. **API Key Authentication**: Per-tenant keys for access control
2. **Storage Quotas**: Limit per-tenant storage (configurable per tenant in future)
3. **Rate Limiting**: Limit uploads per day (rolling 24h window)
4. **Usage Tracking**: Every operation recorded in `UsageEvent` for billing

**Billing Queries**:
```sql
SELECT tenant, COUNT(*) as uploads, SUM(bytes) as total_bytes
FROM usage_event
WHERE event_type = 'UPLOAD' AND timestamp > DATE_SUB(NOW(), INTERVAL 1 MONTH)
GROUP BY tenant;
```

---

## Performance & Scalability

### Recommended Indexes (PostgreSQL)

```sql
CREATE INDEX idx_file_entity_file_id ON file_entity(file_id);
CREATE INDEX idx_file_entity_tenant ON file_entity(tenant);
CREATE INDEX idx_file_entity_file_id_tenant ON file_entity(file_id, tenant);
CREATE INDEX idx_usage_event_timestamp ON usage_event(timestamp);
CREATE INDEX idx_usage_event_tenant_timestamp ON usage_event(tenant, timestamp);
```

### High-Concurrency Setup

1. Use PostgreSQL (not H2)
2. Add indexes above
3. Increase JVM heap: `java -Xmx4G -Xms2G -jar ...`
4. Use G1GC: `-XX:+UseG1GC -XX:MaxGCPauseMillis=200`
5. Increase connection pool: `spring.datasource.hikari.maximum-pool-size=20`

---

## Scheduler

**File Cleanup Scheduler** runs every **5 minutes** via `@Scheduled(cron = "0 */5 * * * ?")`:

```
1. Calculate cutoff: now - (age × timeUnit)
2. Query: DELETE FROM file_entity WHERE createdDate < cutoff
3. For each file: Delete from filesystem, then from database
4. Log summary: "Candidates=X, Removed=Y"
```

Error-resilient: continues on per-file failures.

---

## Key Metrics to Monitor

For production deployments:

1. **API Response Times**: UploadFileController, DownloadFileController endpoints
2. **Disk Usage**: Monitor `file.db.location` disk space
3. **Database Connections**: Connection pool utilization
4. **Error Rates**: Monitor `GlobalExceptionHandler` exception counts
5. **Usage Events**: Monitor cleanup scheduler frequency/success
6. **JVM Memory**: Heap usage, GC pause times

---

## Troubleshooting Checklist

1. **403 Forbidden**: Check `tenant.verification` and `tenant.list`
2. **401 Unauthorized**: Check `api.key.verification` and API key validity
3. **429 Too Many Requests**: Check `tenant.storage.quota.bytes` or `tenant.upload.limit.per.day`
4. **404 Not Found**: Verify file exists in database and on filesystem
5. **500 Internal Error**: Check logs, verify database connectivity, disk space
6. **High Memory**: Increase JVM heap or restart application
7. **Slow Downloads**: Check disk I/O, database query performance, network bandwidth

---

## Example Curl Commands

```bash
# Upload file (auto-generated ID)
curl -X POST http://localhost:8081/uploadNewFile/tenant1 \
  -F "file=@document.pdf"

# Upload with custom ID
curl -X POST http://localhost:8081/uploadByTenantAndFileId/tenant1/my-id \
  -F "file=@document.pdf"

# Download by ID
curl -X GET http://localhost:8081/download/my-id \
  --output downloaded.pdf

# Download with tenant check
curl -X GET http://localhost:8081/download/my-id/tenant1 \
  --output downloaded.pdf

# Generate API key
curl -X POST http://localhost:8081/admin/apikey/tenant1

# List active keys
curl -X GET http://localhost:8081/admin/apikey/tenant1

# Revoke all keys
curl -X DELETE http://localhost:8081/admin/apikey/tenant1

# With API key header
curl -X GET http://localhost:8081/download/my-id \
  -H "X-API-Key: fms-a1b2c3d4..." \
  --output downloaded.pdf
```

---

## Package Responsibilities

| Package | Responsibility | Key Classes |
|---------|-----------------|------------|
| `config` | Centralized configuration | `AppConfig`, `SpringFoxConfig` |
| `controller` | HTTP endpoints | `UploadFileController`, `DownloadFileController`, `ApiKeyController` |
| `service` | Business logic | `UploadFileService`, `DownloadFileService`, `TenantService`, `ApiKeyService`, `QuotaService` |
| `service.storage` | Storage strategy implementations | 6 implementations + factory |
| `service.id` | File ID strategy implementations | `UUIDFileIdStrategyService`, `InstantFileIdStrategyService` + factory |
| `model` | Data models | JPA entities + DTOs |
| `repository` | Database access | `FileRepository`, `ApiKeyRepository`, `UsageEventRepository` |
| `exception` | Exception types | 6 custom exceptions + global handler |
| `scheduler` | Scheduled tasks | `FileCleanUpScheduler` |
| `util` | Utilities | `DateUtils`, `FileUtils`, `MapHelper` |

---

## When to Use run_subagent

This codebase does NOT require delegation for typical tasks. However, consider delegating:

- **Complex planning**: Use `Plan` agent to outline multi-step refactorings
- **Documentation updates**: Multiple documentation files that need coordination

---

## Version Notes

- **Current**: 1.0.7 (2021-12-31)
- **Next planned**: 1.0.8+ (see CHANGELOG.md)
- **Java**: 8 (target)
- **Spring Boot**: 2.4.4
- **Database**: H2 default, PostgreSQL/MySQL supported

---

**This guide is designed for AI agents to be immediately productive. Refer to inline code comments and `DETAILED_DOCUMENTATION.md` for comprehensive information.**

