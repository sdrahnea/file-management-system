# File Management System - Comprehensive Technical Documentation

**Version:** 1.0.7  
**Release Date:** 2021-12-31  
**Last Updated:** April 2, 2026  
**Author:** Sergiu Drahnea

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Core Functionalities](#core-functionalities)
4. [API Endpoints](#api-endpoints)
5. [Configuration Guide](#configuration-guide)
6. [Storage Strategies](#storage-strategies)
7. [File ID Strategies](#file-id-strategies)
8. [Database Configurations](#database-configurations)
9. [Quota and Rate Limiting](#quota-and-rate-limiting)
10. [API Key Management](#api-key-management)
11. [Error Handling](#error-handling)
12. [Scheduler and Cleanup](#scheduler-and-cleanup)
13. [Build and Deployment](#build-and-deployment)
14. [Testing](#testing)
15. [Monetization Features](#monetization-features)
16. [Advanced Features](#advanced-features)
17. [Troubleshooting](#troubleshooting)

---

## Project Overview

The **File Management System** is a lightweight, enterprise-grade Spring Boot REST service that implements a hybrid file storage model:

- **Metadata Storage:** Structured file information (ID, path, size, checksum, creation date) is persisted in a relational database (H2, PostgreSQL, or MySQL)
- **Binary Content Storage:** Actual file bytes are stored on the filesystem with configurable directory organization strategies
- **Multi-Tenant:** Full support for tenant-based file isolation and organization
- **RESTful API:** Simple HTTP endpoints for upload and download operations
- **Extensible:** Strategy pattern for both storage layouts and file ID generation

### Key Statistics

- **Technology Stack:** Java 8, Spring Boot 2.4.4, Spring Data JPA, Hibernate
- **Supported Databases:** H2 (default), PostgreSQL, MySQL
- **Default Port:** 8081
- **Max Single File Size:** 100 MB (configurable)
- **Default Storage Quota:** 1 GiB per tenant (configurable)
- **API Documentation:** Swagger UI 2.9.2

---

## Architecture

### High-Level System Design

```
┌─────────────────────────────────────────────────────────────────────┐
│                         HTTP Clients                                 │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                    ┌────────▼────────┐
                    │   Spring Boot   │
                    │   Port 8081     │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
   ┌────▼────┐      ┌───────▼────────┐   ┌──────▼──────┐
   │  Upload │      │  Download      │   │  Admin      │
   │ Endpoint│      │  Endpoint      │   │  Endpoints  │
   └────┬────┘      └────────┬───────┘   └──────┬──────┘
        │                    │                  │
        └────────────────────┼──────────────────┘
                             │
        ┌────────────────────┼──────────────────┐
        │                    │                  │
   ┌────▼────────┐      ┌───▼──────┐    ┌──────▼───────┐
   │Tenant Check │      │Quota &   │    │API Key Auth  │
   │Validation   │      │Rate Limit│    │(Optional)    │
   └────┬────────┘      └───┬──────┘    └──────┬───────┘
        │                   │                  │
        └───────────────────┼──────────────────┘
                            │
        ┌───────────────────┴───────────────────┐
        │                                       │
   ┌────▼──────────────────┐     ┌────────▼────────────────┐
   │  Database Layer       │     │  Filesystem Layer       │
   │  ┌──────────────────┐ │     │  ┌──────────────────┐   │
   │  │ FileEntity       │ │     │  │ file.db.location │   │
   │  │ ApiKeyEntity     │ │     │  │  └─ {tenant}     │   │
   │  │ UsageEvent       │ │     │  │     └─ [{year}]  │   │
   │  └──────────────────┘ │     │  │        [{month}] │   │
   │                       │     │  │        [{day}]   │   │
   │ H2/PostgreSQL/MySQL   │     │  │        └─{fileId}│   │
   └───────────────────────┘     └────────────────────────┘
```

### Package Structure

```
com.fms
├── config/              [Configuration]
│   └── AppConfig.java           - Central config injection
│   └── SpringFoxConfig.java     - Swagger documentation
│
├── controller/          [HTTP Endpoints]
│   ├── UploadFileController.java   - Upload operations
│   ├── DownloadFileController.java - Download operations
│   └── ApiKeyController.java       - API key management
│
├── service/             [Business Logic]
│   ├── UploadFileService.java      - Upload logic
│   ├── DownloadFileService.java    - Download logic
│   ├── TenantService.java          - Tenant validation
│   ├── ApiKeyService.java          - API key operations
│   ├── QuotaService.java           - Storage quota & rate limit checks
│   ├── UsageService.java           - Usage tracking
│   │
│   ├── id/              [File ID Strategy]
│   │   ├── FileIdServiceFactory.java
│   │   ├── FileIdStrategy.java (interface)
│   │   ├── FileIdStrategyService.java (interface)
│   │   ├── UUIDFileIdStrategyService.java
│   │   └── InstantFileIdStrategyService.java
│   │
│   └── storage/         [Storage Strategy]
│       ├── FileStorageStrategyFactory.java
│       ├── StorageStrategyService.java (interface)
│       ├── FileStorageStrategyService.java
│       ├── FilePerDateStorageStrategyService.java
│       ├── FilePerYearDateStorageStrategyService.java
│       ├── FilePerYearMonthStorageStrategyService.java
│       ├── FilePerYearMonthDayStorageStrategyService.java
│       └── FilePerYearMonthDateStorageStrategyService.java
│
├── model/              [Data Models]
│   ├── FileEntity.java             - JPA entity for file metadata
│   ├── ApiKeyEntity.java           - JPA entity for API keys
│   ├── UsageEvent.java             - JPA entity for usage tracking
│   ├── StorageDTO.java             - Transfer object for storage ops
│   ├── StorageStrategy.java        - Strategy enum
│   ├── FileCleanUpAgeType.java    - Cleanup age type enum
│   ├── CreateFileResponseDTO.java  - Upload response DTO
│   ├── CreateFileRequestDTO.java   - Upload request DTO
│   ├── FileResponseDto.java        - File info response DTO
│   ├── FileRequestDTO.java         - File request DTO
│   └── ErrorDTO.java               - Error response DTO
│
├── repository/         [Data Access]
│   ├── FileRepository.java         - File metadata persistence
│   ├── ApiKeyRepository.java       - API key persistence
│   └── UsageEventRepository.java   - Usage event persistence
│
├── exception/          [Exception Handling]
│   ├── GlobalExceptionHandler.java
│   ├── FileNotFoundException.java
│   ├── ApiKeyUnauthorizedException.java
│   ├── TenantIsNotAllowedException.java
│   ├── StorageQuotaExceededException.java
│   ├── RateLimitExceededException.java
│   └── [Other custom exceptions]
│
├── scheduler/          [Scheduled Tasks]
│   └── FileCleanUpScheduler.java  - Auto-delete old files
│
├── util/               [Utilities]
│   ├── DateUtils.java
│   ├── FileUtils.java
│   └── MapHelper.java
│
└── FileManagementSystemApplication.java [Main Entry Point]
```

---

## Core Functionalities

### 1. File Upload

#### Overview
The system provides multiple upload mechanisms to handle various client scenarios:

- **Multipart Upload**: Traditional form-based file uploads
- **Byte-Array Upload**: Raw binary content upload via request body
- **Auto-Generated IDs**: System-generated unique file IDs using configurable strategies
- **Custom IDs**: Client-supplied file IDs for integration scenarios

#### Upload Flow

```
1. HTTP Request → Controller
2. Tenant Validation (optional, configurable)
3. Quota Check:
   - Storage quota enforcement
   - Rate limit enforcement (uploads per 24h)
4. File Storage Strategy Selection (6 strategies available)
5. Write binary to filesystem
6. Persist metadata to database
7. Record usage event for tracking
8. Return response with fileId and metadata
```

#### Key Features

- **Transaction Support**: `@Transactional` ensures atomic database operations
- **Checksum Calculation**: SHA-256 checksums computed for data integrity
- **Content-Type Preservation**: Original MIME type stored with metadata
- **Size Tracking**: File size in bytes tracked for quota management
- **Tenant Isolation**: Files organized per tenant with optional validation

#### File Metadata Stored

```java
FileEntity {
    Long id;                    // Auto-generated database ID
    String fileId;              // Logical file identifier
    String path;                // Filesystem path to content
    String tenant;              // Tenant identifier
    Date createdDate;           // Upload timestamp
    Long fileSizeBytes;         // File size in bytes
    String contentType;         // MIME type
    String checksum;            // SHA-256 hash
}
```

### 2. File Download

#### Overview
Retrieves stored files by identifier with optional tenant verification.

#### Download Flow

```
1. HTTP Request with fileId (and optional tenant)
2. Tenant Validation (if provided, optional)
3. Database Lookup:
   - Query FileRepository by fileId
   - If tenant provided, filter by tenant also
4. If not found → throw FileNotFoundException (404)
5. Read binary content from filesystem path
6. Record download usage event
7. Stream bytes to HTTP response
```

#### Key Features

- **Tenant-Aware Download**: Optionally verify file belongs to tenant
- **Usage Tracking**: Every download recorded for analytics
- **Error Handling**: Returns 404 if file not found
- **Binary Streaming**: Efficient byte array response

### 3. Tenant Management

#### Overview
Full multi-tenant support with optional validation and isolation.

#### Features

- **Tenant List Configuration**: Allowlist of valid tenants in `application.properties`
- **Optional Verification**: Can be disabled for open access or internal use
- **File Isolation**: Files stored under `{file.db.location}/{tenant}/...`
- **Namespace Isolation**: File IDs are per-tenant (not globally unique)

#### Tenant Configuration

```properties
# Comma-separated list of allowed tenants
tenant.list=tenant1,tenant2,tenant3,tenant4

# Enable/disable tenant allowlist verification
# When false: any tenant is accepted
# When true: only tenants in tenant.list are allowed
tenant.verification=false
```

### 4. API Key Management

#### Overview
Optional API key authentication for monetization scenarios (e.g., freemium, per-tenant billing).

#### Features

- **Generate Keys**: POST `/admin/apikey/{tenant}` → generates unique key
- **Key Format**: `fms-{32-char-hex}` (e.g., `fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6`)
- **List Keys**: GET `/admin/apikey/{tenant}` → active keys for tenant
- **Revoke Keys**: DELETE `/admin/apikey/{tenant}` → deactivate all keys
- **Toggle Verification**: `api.key.verification` property enables/disables enforcement
- **Key Status**: Keys can be active or revoked; revoked keys are rejected

#### API Key Features

- **Per-Tenant Keys**: Each tenant has independent set of keys
- **Audit Trail**: Generated keys logged for compliance
- **Soft Revocation**: Keys marked inactive (not deleted) for recovery options
- **On-Demand Generation**: Keys generated on-demand, not pre-allocated

#### When API Key Verification is Enabled

- Every upload/download request must include valid API key
- Key validation happens after tenant checks
- Invalid/missing keys return 401 Unauthorized
- Active keys only; revoked keys rejected

### 5. Quota and Rate Limiting

#### Storage Quota

**Purpose**: Prevent tenants from consuming excessive disk space.

**Configuration**:
```properties
# Default: 1 GiB per tenant
tenant.storage.quota.bytes=1073741824
```

**Check Logic**:
```
allowed = (total_used_bytes + incoming_bytes) <= quota_bytes
```

**Response on Violation**:
```
HTTP 429 Too Many Requests
{
  "code": "STORAGE_QUOTA_EXCEEDED",
  "message": "Storage quota of 1 GB exceeded for tenant 'tenant1'"
}
```

**Calculation**:
```
sum(FileEntity.fileSizeBytes) WHERE FileEntity.tenant = ?
+ incoming_upload_size
> configured_quota
→ REJECTED
```

#### Rate Limiting

**Purpose**: Prevent abuse by limiting uploads per rolling 24-hour window.

**Configuration**:
```properties
# Default: 10,000 uploads per tenant per 24 hours
tenant.upload.limit.per.day=10000
```

**Check Logic**:
```
uploads_in_last_24h >= limit_per_day → REJECTED
```

**Response on Violation**:
```
HTTP 429 Too Many Requests
{
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Rate limit of 10000 uploads exceeded for tenant 'tenant1'"
}
```

**Implementation Details**:
- Uses `UsageEvent` table to track uploads
- Rolling 24-hour window (queries events from last 86,400 seconds)
- Checked before every upload
- Rate limit enforced AFTER quota check

#### Quota Service Logic

```java
QuotaService.assertUploadAllowed(tenant, incomingBytes):
  1. Check storage quota
  2. Check rate limit per day
  3. Throw exception if either fails
  4. Return silently if both pass
```

### 6. File Cleanup / Scheduler

#### Overview
Automatic deletion of old files based on configurable age.

#### Features

- **Scheduled Execution**: Runs every 5 minutes (cron: `0 */5 * * * ?`)
- **Age-Based Deletion**: Configurable delete threshold
- **Three Time Units**: DAY, MONTH, YEAR
- **Dual Cleanup**: Removes both database record and filesystem file
- **Error Resilience**: Continues on individual file failures
- **Logging**: Detailed logs of removed/failed files

#### Configuration

```properties
# Delete files older than this value
file.cleanup.age=100

# Time unit for age calculation: DAY, MONTH, YEAR
file.cleanup.age.type=DAY

# Example interpretations:
# - age=100, type=DAY → delete files > 100 days old
# - age=6, type=MONTH → delete files > 6 months old (~180 days)
# - age=2, type=YEAR → delete files > 2 years old (~730 days)
```

#### Cleanup Process

```
1. Every 5 minutes, scheduler triggers
2. Calculate cutoff date:
   - now - (fileCleanUpAge × timeUnit)
3. Query database for files older than cutoff
4. For each file:
   a. Delete from filesystem (Paths.delete)
   b. Delete database record
   c. Log result (success/failure)
5. Summary log: "Removed X of Y candidates"
```

#### Scheduler Implementation

```java
@Scheduled(cron = "0 */5 * * * ?")
public void cleanFilesByAge() {
    Date cutoffDate = computeCutoffDate();
    List<FileEntity> filesToBeRemoved = fileRepository.getFileForDeleting(cutoffDate);
    
    for (FileEntity entity : filesToBeRemoved) {
        try {
            Files.deleteIfExists(Paths.get(entity.getPath()));
            fileRepository.deleteById(entity.getId());
        } catch (Exception e) {
            log.error("Failed to delete file", e);
        }
    }
}
```

### 7. Usage Tracking

#### Purpose
Track file operations for analytics, rate limiting, and audit purposes.

#### Tracked Events

```java
UsageEvent {
    Long id;
    String tenant;
    String fileId;
    String eventType;          // "UPLOAD" or "DOWNLOAD"
    Long bytes;                // File size involved
    Date timestamp;            // When operation occurred
}
```

#### When Events Are Recorded

1. **On Every Upload**: Records tenant, fileId, bytes, timestamp
2. **On Every Download**: Records tenant, fileId, bytes, timestamp
3. **For Rate Limiting**: Only UPLOAD events counted in 24h check
4. **Retention**: No automatic cleanup of usage events (can be pruned manually)

#### Query Patterns

```sql
-- Count uploads in last 24 hours for tenant
SELECT COUNT(*) 
FROM usage_event 
WHERE tenant = ? 
  AND event_type = 'UPLOAD' 
  AND timestamp > NOW() - INTERVAL 24 HOUR

-- Total bytes uploaded by tenant
SELECT SUM(bytes)
FROM usage_event
WHERE tenant = ?
  AND event_type = 'UPLOAD'
```

---

## API Endpoints

### Upload Endpoints

#### 1. Upload New File (Auto-Generated ID)

```http
POST /uploadNewFile/{tenant}
Content-Type: multipart/form-data

file: <binary>
```

**Parameters**:
- `{tenant}` (path): Tenant identifier
- `file` (form-data): Multipart file

**Response**:
```json
{
  "uuid": "3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e",
  "tenant": "tenant1",
  "filePath": "C:/filessssss/file-db/tenant1/3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e"
}
```

**Status Codes**:
- `200 OK`: Upload successful
- `403 Forbidden`: Tenant not allowed
- `429 Too Many Requests`: Quota/rate limit exceeded
- `401 Unauthorized`: Invalid API key (if enabled)

---

#### 2. Upload File with Specific ID (Multipart)

```http
POST /uploadByTenantAndFileId/{tenant}/{fileId}
Content-Type: multipart/form-data

file: <binary>
```

**Parameters**:
- `{tenant}` (path): Tenant identifier
- `{fileId}` (path): Custom file ID
- `file` (form-data): Multipart file

**Response**:
```json
{
  "uuid": "my-custom-id-001",
  "tenant": "tenant1",
  "filePath": "C:/filessssss/file-db/tenant1/my-custom-id-001"
}
```

**Use Case**: Integrating with existing external ID systems

---

#### 3. Upload New File (Alt ID+Tenant Order)

```http
POST /uploadNewFile/{fileId}/{tenant}
Content-Type: multipart/form-data

file: <binary>
```

**Parameters**:
- `{fileId}` (path): Custom file ID
- `{tenant}` (path): Tenant identifier
- `file` (form-data): Multipart file

**Response**:
```
my-custom-id-001
```

---

#### 4. Upload Multipart File

```http
POST /uploadMultipartFile/{fileId}/{tenant}
Content-Type: multipart/form-data

file: <binary>
```

**Parameters**:
- `{fileId}` (path): File ID
- `{tenant}` (path): Tenant identifier
- `file` (form-data): Multipart file

**Response**:
```
file-id-value
```

---

#### 5. Upload Byte-Array Content

```http
POST /upload/{fileId}/{tenant}
Content-Type: application/octet-stream

<binary body>
```

**Parameters**:
- `{fileId}` (path): File ID
- `{tenant}` (path): Tenant identifier
- Body: Raw binary content

**Response**:
```
file-id-value
```

**Use Case**: Generated PDFs, images, or other pre-computed binary content

---

#### 6. Upload with Custom Directory

```http
POST /upload/{fileId}/{directory}/{tenant}
Content-Type: application/octet-stream

<binary body>
```

**Parameters**:
- `{fileId}` (path): File ID
- `{directory}` (path): Custom directory name
- `{tenant}` (path): Tenant identifier
- Body: Raw binary content

**Response**:
```
file-id-value
```

**Note**: Directory parameter stored but rarely used in modern deployments (legacy feature)

---

### Download Endpoints

#### 1. Download by File ID

```http
GET /download/{fileId}
```

**Parameters**:
- `{fileId}` (path): File ID to retrieve

**Response**:
```
<binary file content>
```

**Headers**:
- `Content-Type`: Original MIME type (e.g., application/pdf)
- `Content-Length`: File size in bytes

**Status Codes**:
- `200 OK`: File found and downloaded
- `404 Not Found`: File ID doesn't exist
- `401 Unauthorized`: Invalid API key (if enabled)

---

#### 2. Download by File ID and Tenant

```http
GET /download/{fileId}/{tenant}
```

**Parameters**:
- `{fileId}` (path): File ID to retrieve
- `{tenant}` (path): Tenant identifier

**Response**:
```
<binary file content>
```

**Status Codes**:
- `200 OK`: File found and downloaded
- `404 Not Found`: File ID not found for this tenant
- `403 Forbidden`: Tenant not allowed
- `401 Unauthorized`: Invalid API key (if enabled)

**Use Case**: Multi-tenant environments ensuring cross-tenant isolation

---

### Admin/API Key Endpoints

#### 1. Generate API Key

```http
POST /admin/apikey/{tenant}
```

**Parameters**:
- `{tenant}` (path): Tenant identifier

**Response**:
```json
{
  "tenant": "tenant1",
  "apiKey": "fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6"
}
```

**Note**: Key is shown only once. Store securely.

---

#### 2. List Active API Keys

```http
GET /admin/apikey/{tenant}
```

**Parameters**:
- `{tenant}` (path): Tenant identifier

**Response**:
```json
[
  {
    "id": 1,
    "keyValue": "fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
    "tenant": "tenant1",
    "active": true,
    "createdDate": "2026-04-02T10:30:00Z"
  },
  {
    "id": 2,
    "keyValue": "fms-e1f2a3b4c5d6a7b8c9d0e1f2a3b4c5d6",
    "tenant": "tenant1",
    "active": false,
    "createdDate": "2026-03-15T14:22:30Z"
  }
]
```

---

#### 3. Revoke All API Keys

```http
DELETE /admin/apikey/{tenant}
```

**Parameters**:
- `{tenant}` (path): Tenant identifier

**Response**:
```json
{
  "tenant": "tenant1",
  "status": "revoked"
}
```

**Effect**: All active keys for tenant marked as inactive

---

## Configuration Guide

### Configuration File Location

```
src/main/resources/application.properties
```

### All Configurable Properties

#### Application Server

| Property | Value | Purpose |
|----------|-------|---------|
| `server.port` | 8081 | HTTP port the application listens on |

#### Database Configuration

**H2 (Default - Development)**
```properties
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.datasource.url=jdbc:h2:file:./data/db
spring.datasource.username=sa
spring.datasource.password=password
```

**H2 (In-Memory - Fast for Testing)**
```properties
spring.datasource.url=jdbc:h2:mem:testdb
```

**PostgreSQL (Production)**
```properties
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL94Dialect
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/test
spring.datasource.username=test
spring.datasource.password=test
```

**MySQL (Alternative)**
```properties
spring.datasource.platform=mysql
spring.datasource.url=jdbc:mysql://localhost:3306/fms
spring.datasource.username=root
spring.datasource.password=root
```

#### File Storage Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `file.db.location` | `C:/filessssss/file-db` | Root directory where files are stored on filesystem |
| `storage.strategy` | `FILE` | Directory organization strategy (see Storage Strategies section) |
| `file.id.type` | (empty, defaults to UUID) | File ID generation strategy (`UUID` or `INSTANT`) |

#### Tenant Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `tenant.list` | `tenant1,tenant2,tenant3,tenant4` | Comma-separated allowed tenant IDs |
| `tenant.verification` | `false` | Enable/disable tenant allowlist validation |

#### File Cleanup Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `file.cleanup.age` | `100` | Threshold for automatic deletion |
| `file.cleanup.age.type` | `DAY` | Time unit: `DAY`, `MONTH`, or `YEAR` |

#### Upload Size Limits

| Property | Default | Purpose |
|----------|---------|---------|
| `spring.servlet.multipart.max-file-size` | `100MB` | Max single file size |
| `spring.servlet.multipart.max-request-size` | `100MB` | Max request body size |
| `server.tomcat.max-http-post-size` | `-1` | Tomcat limit (−1 = unlimited) |

#### Logging Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `logging.level.root` | `info` | Global log level |
| `logging.level.org.springframework.web` | `info` | Spring Web MVC logging |
| `logging.level.org.hibernate` | `error` | Hibernate/JPA logging |

#### Monetization & Rate Limiting

| Property | Default | Purpose |
|----------|---------|---------|
| `api.key.verification` | `false` | Enable/disable API key authentication |
| `tenant.storage.quota.bytes` | `1073741824` (1 GiB) | Storage quota per tenant |
| `tenant.upload.limit.per.day` | `10000` | Max uploads per tenant per 24h |

#### Hibernate/JPA Configuration

| Property | Default | Purpose |
|----------|---------|---------|
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema update behavior (`update`, `create`, `validate`, `none`) |
| `spring.jpa.show-sql` | `false` | Log SQL statements (dev only) |

#### Multipart Configuration Details

```properties
# Enable multipart support
spring.servlet.multipart.enabled=true

# Threshold before temp file creation (0 = always temp)
spring.servlet.multipart.file-size-threshold=0

# Max single file size
spring.servlet.multipart.max-file-size=100MB

# Max request total size
spring.servlet.multipart.max-request-size=100MB

# Alternate properties (legacy support)
spring.servlet.multipart.maxFileSize=100MB
spring.servlet.multipart.maxRequestSize=100MB

# Tomcat HTTP POST size
server.tomcat.max-http-post-size=-1
```

### Changing Configuration Values

#### Method 1: Edit application.properties

```properties
# Example: Change to PostgreSQL
spring.datasource.url=jdbc:postgresql://db.example.com:5432/fms
spring.datasource.username=dbuser
spring.datasource.password=dbpass

# Example: Enable API key verification
api.key.verification=true

# Example: Reduce quota to 500 MB
tenant.storage.quota.bytes=524288000
```

#### Method 2: Command-Line Override at Runtime

```bash
java -jar target/file-management-system-1.0.7.jar \
  --spring.datasource.url=jdbc:postgresql://db.example.com:5432/fms \
  --spring.datasource.username=dbuser \
  --spring.datasource.password=dbpass \
  --api.key.verification=true
```

#### Method 3: External Configuration File

```bash
java -jar target/file-management-system-1.0.7.jar \
  --spring.config.location=/path/to/application.properties
```

#### Method 4: Environment Variables

Spring Boot converts environment variables to properties with `SPRING_` prefix and underscores:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://...
export SPRING_API_KEY_VERIFICATION=true
export SPRING_TENANT_STORAGE_QUOTA_BYTES=524288000
java -jar target/file-management-system-1.0.7.jar
```

---

## Storage Strategies

### Overview

Storage strategies define how files are organized in the filesystem. The `storage.strategy` property selects one of six strategies at startup.

### Strategy Details

#### 1. FILE Strategy

**Configuration**:
```properties
storage.strategy=FILE
```

**Directory Structure**:
```
{file.db.location}/
  └── {tenant}/
      └── {fileId}
          (binary content)
```

**Example**:
```
C:/filessssss/file-db/
  └── tenant1/
      ├── 3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e
      ├── my-custom-id-001
      └── report-2025-001
  └── tenant2/
      ├── user-doc-12345
      └── invoice-9999
```

**Best For**: Simplicity, small deployments, quick tests

**Pros**:
- Simplest directory layout
- Easy to navigate manually
- Fast filesystem lookups
- Minimal path depth

**Cons**:
- Single flat directory can be slow with millions of files
- Harder to locate files by date
- No automatic organization by time

---

#### 2. FILE_PER_DATE Strategy

**Configuration**:
```properties
storage.strategy=FILE_PER_DATE
```

**Directory Structure**:
```
{file.db.location}/
  └── {tenant}/
      └── {YYYY-MM-DD}/
          └── {fileId}
```

**Example**:
```
C:/filessssss/file-db/
  └── tenant1/
      ├── 2026-04-02/
      │   ├── 3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e
      │   └── my-custom-id-001
      ├── 2026-04-01/
      │   └── report-2025-001
      └── 2026-03-31/
          └── invoice-9999
  └── tenant2/
      └── 2026-04-02/
          └── user-doc-12345
```

**Best For**: Moderate-sized deployments, date-based cleanup

**Pros**:
- Natural organization by upload date
- Easy to find files from specific date
- Facilitates daily backups
- Clean separation of date buckets

**Cons**:
- One level deeper than FILE
- Empty dates create unnecessary directories
- Harder to find files across multiple dates

---

#### 3. FILE_PER_YEAR_DATE Strategy

**Configuration**:
```properties
storage.strategy=FILE_PER_YEAR_DATE
```

**Directory Structure**:
```
{file.db.location}/
  └── {tenant}/
      └── {YYYY}/
          └── {YYYY-MM-DD}/
              └── {fileId}
```

**Example**:
```
C:/filessssss/file-db/
  └── tenant1/
      ├── 2026/
      │   ├── 2026-04-02/
      │   │   ├── 3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e
      │   │   └── my-custom-id-001
      │   ├── 2026-03-31/
      │   │   └── invoice-9999
      ├── 2025/
      │   └── 2025-12-31/
      │       └── report-2025-001
```

**Best For**: Multi-year deployments, year-over-year analysis

**Pros**:
- Hierarchical organization by year
- Easy to archive entire year
- Scales well for long-running systems
- Quick year-level filtering

**Cons**:
- Deeper directory structure (3 levels)
- More complex path construction
- Empty years create directory overhead

---

#### 4. FILE_PER_YEAR_MONTH Strategy

**Configuration**:
```properties
storage.strategy=FILE_PER_YEAR_MONTH
```

**Directory Structure**:
```
{file.db.location}/
  └── {tenant}/
      └── {YYYY}/
          └── {MM}/
              └── {fileId}
```

**Example**:
```
C:/filessssss/file-db/
  └── tenant1/
      ├── 2026/
      │   ├── 04/
      │   │   ├── 3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e
      │   │   └── my-custom-id-001
      │   ├── 03/
      │   │   └── invoice-9999
      │   └── 02/
      └── 2025/
          ├── 12/
          └── 11/
```

**Best For**: Monthly billing cycles, moderate-large deployments

**Pros**:
- Good for monthly backups/archiving
- Compact organization (12 months per year)
- Easy month-based retention policies
- Balanced depth and organization

**Cons**:
- Less granular than day-based strategies
- Multiple files per month in same directory
- May need sub-sorting by day still

---

#### 5. FILE_PER_YEAR_MONTH_DAY Strategy

**Configuration**:
```properties
storage.strategy=FILE_PER_YEAR_MONTH_DAY
```

**Directory Structure**:
```
{file.db.location}/
  └── {tenant}/
      └── {YYYY}/
          └── {MM}/
              └── {DD}/
                  └── {fileId}
```

**Example**:
```
C:/filessssss/file-db/
  └── tenant1/
      ├── 2026/
      │   ├── 04/
      │   │   ├── 02/
      │   │   │   ├── 3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e
      │   │   │   └── my-custom-id-001
      │   │   ├── 01/
      │   │   │   └── report-001
      │   ├── 03/
      │   │   └── 31/
      │   │       └── invoice-9999
```

**Best For**: Large-scale deployments, daily cleanup, detailed organization

**Pros**:
- Most organized for day-based operations
- Easy daily backups/cleanup/rotation
- Minimal files per directory (typically < 1000/day)
- Excellent scalability
- Natural cleanup by day

**Cons**:
- Deepest directory structure (5 levels)
- Creates many empty directories
- Path strings longest (performance cost)
- Requires more metadata in paths

---

#### 6. FILE_PER_YEAR_MONTH_DATE Strategy

**Configuration**:
```properties
storage.strategy=FILE_PER_YEAR_MONTH_DATE
```

**Directory Structure**:
```
{file.db.location}/
  └── {tenant}/
      └── {YYYY}/
          └── {MM}/
              └── {YYYY-MM-DD}/
                  └── {fileId}
```

**Example**:
```
C:/filessssss/file-db/
  └── tenant1/
      ├── 2026/
      │   ├── 04/
      │   │   ├── 2026-04-02/
      │   │   │   ├── 3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e
      │   │   │   └── my-custom-id-001
      │   │   ├── 2026-04-01/
      │   │   └── 2026-03-31/
      │   │       └── invoice-9999
      │   ├── 03/
      │   │   └── 2026-03-15/
      │   │       └── report-001
```

**Best For**: Hybrid approach, good organization with date clarity

**Pros**:
- Clear day organization with YYYY-MM-DD naming
- Good scalability
- Easy date parsing from path
- Balanced depth (4 levels)

**Cons**:
- Slightly redundant (year/month + full date)
- Not as clean as pure Y/M/D structure

---

### Choosing a Storage Strategy

| Strategy | Deployment Size | Cleanup Granularity | Best Suited For |
|----------|-----------------|-------------------|-----------------|
| `FILE` | < 100K files | Manual | Development, small tests |
| `FILE_PER_DATE` | < 1M files | Daily | Small-medium production |
| `FILE_PER_YEAR_DATE` | < 10M files | Daily | Multi-year storage |
| `FILE_PER_YEAR_MONTH` | < 100M files | Monthly | Large monthly billing |
| `FILE_PER_YEAR_MONTH_DAY` | > 100M files | Daily | Enterprise, high-volume |
| `FILE_PER_YEAR_MONTH_DATE` | > 100M files | Daily | Enterprise, date clarity |

---

## File ID Strategies

### Overview

File ID strategies determine how unique identifiers are generated for uploaded files when not provided by the client.

### Configuration

```properties
# Leave empty or "UUID" for UUID strategy
# Set to "INSTANT" for epoch-based numeric IDs
file.id.type=UUID
```

### Strategy Details

#### 1. UUID Strategy (Default)

**Configuration**:
```properties
file.id.type=UUID
# OR just leave blank (defaults to UUID)
file.id.type=
```

**ID Format**:
```
3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e
```

**ID Format Details**:
- 36 characters (8-4-4-4-12 hex segments)
- Universally unique (collision probability: 1 in 5 billion)
- RFC 4122 compliant
- Java: `UUID.randomUUID().toString()`

**Example IDs**:
```
3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e
a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d
f6f8a5c9-8b7d-4e2c-9f1a-3b5c7d9e1f2a
```

**Pros**:
- Universally unique
- No collisions in practice
- Safe for any traffic volume
- Standard format (wide tool support)
- Sortable by generation time (v1) or cryptographically unique (v4)

**Cons**:
- 36 characters per ID (larger storage)
- Less human-readable
- Not strictly sequential (hard to guess next ID)

**Best For**:
- High-traffic systems (no collision risk)
- Integration scenarios
- Public APIs
- Default choice for most systems

---

#### 2. INSTANT Strategy

**Configuration**:
```properties
file.id.type=INSTANT
```

**ID Format**:
```
1743667428123456789
```

**ID Format Details**:
- Numeric string (nanoseconds since epoch)
- Typically 18-19 characters
- Java: `System.nanoTime()` wrapped in string
- Roughly sequential (increases monotonically)

**Example IDs**:
```
1743667428123456789
1743667428123456790
1743667428123456791
```

**Pros**:
- Shorter (saves storage)
- Roughly sequential (can deduce upload order)
- Human-readable (numeric)
- Faster generation (no UUID processing)

**Cons**:
- **Collision risk at high traffic**: Multiple uploads in same nanosecond create duplicates
- Only suitable for low-traffic systems
- Less standardized

**Best For**:
- Low-traffic systems (< 100 uploads/second)
- Internal systems where collisions can be handled
- Storage-constrained scenarios
- Scenarios where sequential ordering matters

**⚠️ Warning**: Do NOT use INSTANT for high-concurrency systems. UUID is safer.

---

### Switching Strategies

The strategy is set at startup and cannot be changed without redeployment:

**Current IDs**: Cannot be regenerated (stored in database)

**After Strategy Change**: New uploads use new strategy, old files keep old IDs

**Example Transition**:
```
Before (UUID strategy):
  - file-001 → 3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e

Change config to INSTANT

After (INSTANT strategy):
  - file-002 → 1743667428123456789
  - Old file-001 still has UUID ID
```

---

## Database Configurations

### Supported Databases

The application supports three production-grade databases:

### 1. H2 Database (Default)

#### Configuration

```properties
# Driver
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# File-based (default, data persists)
spring.datasource.url=jdbc:h2:file:./data/db
spring.datasource.username=sa
spring.datasource.password=password

# OR In-memory (for testing, data lost on shutdown)
spring.datasource.url=jdbc:h2:mem:testdb
```

#### Features

- **Zero Installation**: Embedded in application (no separate server needed)
- **File-Based Storage**: Data stored in `./data/db` by default
- **In-Memory Option**: Fast testing without disk I/O
- **H2 Console**: Web UI for querying at `http://localhost:8081/h2-console`
- **SQL Support**: Full SQL-92 standard + some PostgreSQL extensions

#### H2 Console Access

```
URL: http://localhost:8081/h2-console
Driver: org.h2.Driver
JDBC URL: jdbc:h2:file:./data/db
User: sa
Password: password
```

#### Pros
- No setup required
- Perfect for development/testing
- Can be switched to PostgreSQL later
- SQL scripts included in repo

#### Cons
- Not recommended for high-concurrency production
- Limited to one concurrent writer
- Weaker performance than PostgreSQL
- No network access to database

---

### 2. PostgreSQL Database (Recommended for Production)

#### Configuration

```properties
# Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL94Dialect
spring.datasource.driverClassName=org.postgresql.Driver

# Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/fms
spring.datasource.username=postgres
spring.datasource.password=password
```

#### Setup Instructions

**Installation** (Ubuntu/Debian):
```bash
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql
```

**Create Database**:
```sql
sudo -u postgres psql
CREATE DATABASE fms;
CREATE USER fms_user WITH PASSWORD 'secure_password';
ALTER ROLE fms_user SET client_encoding TO 'utf8';
ALTER ROLE fms_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE fms_user SET default_transaction_deferrable TO on;
ALTER ROLE fms_user SET default_transaction_read_uncommitted TO off;
GRANT ALL PRIVILEGES ON DATABASE fms TO fms_user;
```

**Production Configuration**:
```properties
spring.datasource.url=jdbc:postgresql://db.example.com:5432/fms
spring.datasource.username=fms_user
spring.datasource.password=secure_password
spring.jpa.hibernate.ddl-auto=validate
```

#### Features

- **Full ACID Compliance**: Transactional integrity
- **Concurrent Access**: Multiple concurrent connections
- **Advanced Query Optimization**: Query planner
- **Replication Support**: Master-slave setup
- **Backup Tools**: `pg_dump`, `pg_restore`

#### Backup/Restore

```bash
# Backup
pg_dump -U fms_user -h localhost fms > backup.sql

# Restore
psql -U fms_user -h localhost fms < backup.sql

# Binary backup
pg_dump -U fms_user -h localhost -F c fms > backup.dump
pg_restore -U fms_user -h localhost -d fms backup.dump
```

#### Pros
- Enterprise-grade reliability
- Excellent performance at scale
- Advanced features (JSON, Full-Text Search, etc.)
- Active development community
- Free and open-source

#### Cons
- Requires separate installation
- More complex setup
- Requires PostgreSQL knowledge for maintenance

---

### 3. MySQL Database (Alternative)

#### Configuration

```properties
# Driver
spring.datasource.platform=mysql
spring.datasource.url=jdbc:mysql://localhost:3306/fms
spring.datasource.username=root
spring.datasource.password=password
```

#### Setup Instructions

**Installation** (Ubuntu/Debian):
```bash
sudo apt-get install mysql-server
sudo mysql_secure_installation
sudo systemctl start mysql
```

**Create Database**:
```sql
mysql -u root -p
CREATE DATABASE fms;
CREATE USER 'fms_user'@'localhost' IDENTIFIED BY 'secure_password';
GRANT ALL PRIVILEGES ON fms.* TO 'fms_user'@'localhost';
FLUSH PRIVILEGES;
```

#### MySQL 8 Authentication Issues

If using MySQL 8 with older clients:

```sql
ALTER USER 'fms_user'@'localhost' IDENTIFIED WITH mysql_native_password BY 'secure_password';
```

#### Configuration for pom.xml

**Note**: MySQL driver is NOT included in current `pom.xml`. Add:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.28</version>
</dependency>
```

#### Pros
- Widely used
- Simple setup
- Good performance
- Wide hosting support

#### Cons
- Less feature-rich than PostgreSQL
- Not included in pom.xml by default
- Authentication complexity in newer versions

---

### Database Comparison

| Feature | H2 | PostgreSQL | MySQL |
|---------|----|-----------:|-------|
| Setup Complexity | None | Medium | Low |
| Production Ready | No | Yes | Yes |
| Concurrent Writers | 1 | Many | Many |
| ACID Compliance | Yes | Yes | Yes |
| Transaction Support | Yes | Yes | Yes |
| Max Database Size | 2GB | Unlimited | Unlimited |
| Replication | No | Yes | Yes |
| Full-Text Search | Limited | Yes | Yes |
| JSON Support | Limited | Full | Yes |
| Performance (Large) | Poor | Excellent | Good |
| Network Access | No | Yes | Yes |
| Backup Tools | Basic | Advanced | Advanced |
| Cost | Free | Free | Free |
| Recommended For | Dev/Test | Production | Production |

---

### Schema Auto-Creation

The application uses Hibernate's automatic schema management:

```properties
# Options:
spring.jpa.hibernate.ddl-auto=update
```

| Value | Behavior |
|-------|----------|
| `create` | Drop tables, create fresh (⚠️ data loss) |
| `create-drop` | Create on startup, drop on shutdown (⚠️ testing only) |
| `update` | Alter existing tables, never drop (SAFE - default) |
| `validate` | Verify schema matches entities, fail if mismatch |
| `none` | Do nothing (manual schema management) |

#### Production Recommendation

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Create schema manually using SQL scripts, then validate at startup.

---

## Quota and Rate Limiting

### Storage Quota System

#### Configuration

```properties
# Default: 1 GiB per tenant
# 1 GiB = 1,073,741,824 bytes
tenant.storage.quota.bytes=1073741824

# Examples:
# 500 MB = 524,288,000
# 100 MB = 104,857,600
# 10 GB = 10,737,418,240
```

#### How It Works

**Calculation**:
```
Used Bytes = SUM(FileEntity.fileSizeBytes WHERE tenant = {tenant})
Incoming Bytes = New file size being uploaded
Quota Bytes = tenant.storage.quota.bytes

Allowed = (Used Bytes + Incoming Bytes) <= Quota Bytes
```

**Enforcement Point**: Before file storage, in `QuotaService.assertStorageQuota()`

**Error Response** (when exceeded):
```json
HTTP 429 Too Many Requests
{
  "code": "STORAGE_QUOTA_EXCEEDED",
  "message": "Storage quota of 1073741824 bytes exceeded for tenant 'tenant1'"
}
```

#### Example Scenarios

**Scenario 1: Tenant below quota**
```
Tenant: tenant1
Quota: 1 GiB (1,073,741,824 bytes)
Current Usage: 500 MB (524,288,000 bytes)
Incoming File: 300 MB (314,572,800 bytes)

Calculation: 524,288,000 + 314,572,800 = 838,860,800 bytes
838,860,800 < 1,073,741,824 → ALLOWED ✓
```

**Scenario 2: Tenant would exceed quota**
```
Tenant: tenant2
Quota: 1 GiB (1,073,741,824 bytes)
Current Usage: 800 MB (838,860,800 bytes)
Incoming File: 300 MB (314,572,800 bytes)

Calculation: 838,860,800 + 314,572,800 = 1,153,433,600 bytes
1,153,433,600 > 1,073,741,824 → REJECTED ✗
```

#### Per-Tenant Quota Tracking

Each tenant has independent quota:

```
tenant1: 500 MB / 1 GiB used
tenant2: 800 MB / 1 GiB used
tenant3: 100 MB / 1 GiB used
```

Uploading to tenant1 doesn't affect quotas of tenant2 or tenant3.

#### Adjusting Quotas

**Example: Set different quotas per tenant**

Current system: All tenants share `tenant.storage.quota.bytes`

**Future Enhancement Ideas**:
1. Store quota per tenant in database
2. Admin API to set per-tenant quotas
3. Dynamic quota adjustment

---

### Rate Limiting System

#### Configuration

```properties
# Default: 10,000 uploads per 24-hour rolling window per tenant
tenant.upload.limit.per.day=10000

# Examples:
# 100 uploads/day = 100
# 1,000 uploads/day = 1000
# Unlimited = 999999999
```

#### How It Works

**Tracking**:
- Every successful upload records a `UsageEvent`
- `UsageEvent` contains: tenant, fileId, eventType, bytes, timestamp

**Rate Limit Check**:
```
Uploads in Last 24h = COUNT(UsageEvent 
                           WHERE tenant = {tenant}
                           AND eventType = 'UPLOAD'
                           AND timestamp > NOW() - 24 HOURS)

Allowed = Uploads in Last 24h < tenant.upload.limit.per.day
```

**Enforcement Point**: Before quota check, in `QuotaService.assertRateLimit()`

**Error Response** (when exceeded):
```json
HTTP 429 Too Many Requests
{
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Rate limit of 10000 uploads exceeded for tenant 'tenant1'"
}
```

#### Rolling Window Example

Assuming limit = 100 uploads/day:

```
Timeline: April 2, 2026

12:00 AM: 0 uploads (window opens)
...
08:00 AM: 50 uploads in window → ALLOWED (50 < 100)
...
06:00 PM: 99 uploads in window → ALLOWED (99 < 100)
...
06:05 PM: 100th upload in window → ALLOWED (100 = 100, not >)
...
06:10 PM: 101st upload attempt → REJECTED (101 > 100) ✗

Timeline: April 3, 2026

12:00 AM: 1 old upload falls out of 24h window
  → Window now has 100 uploads from Apr 2, 12:01 AM onward
...
12:01 AM: 101st upload from Apr 2 falls out
  → Window now has 99 uploads
  → Next upload allowed ✓
```

#### Per-Tenant Rate Limit Tracking

Each tenant has independent rate limit:

```
tenant1: 9,500 / 10,000 uploads in last 24h
tenant2: 50 / 10,000 uploads in last 24h
tenant3: 10,000 / 10,000 uploads in last 24h (AT LIMIT)
```

tenant3 is at limit and cannot upload until 24h window rolls.

#### Checking Usage Status

**Current System**: No API endpoint to check current usage

**Possible Future Endpoint**:
```http
GET /admin/usage/{tenant}
Response:
{
  "tenant": "tenant1",
  "uploads_last_24h": 9500,
  "limit_per_day": 10000,
  "remaining": 500,
  "storage_used_bytes": 838860800,
  "storage_quota_bytes": 1073741824,
  "storage_remaining_bytes": 234880024
}
```

#### Quota vs Rate Limit

| Aspect | Quota | Rate Limit |
|--------|-------|-----------|
| **What** | Storage space | Upload frequency |
| **Unit** | Bytes | Count |
| **Limit** | 1 GiB | 10,000 per day |
| **When Checked** | At upload | At upload (after quota) |
| **Exceeded Response** | 429 | 429 |
| **Reset** | Never (accumulates) | Every 24h (rolling) |
| **Impact** | Cannot upload (no space) | Cannot upload (too many) |

---

## API Key Management

### Overview

API key management provides optional authentication for monetization scenarios:
- Freemium models (free tier without key, premium with key)
- Per-tenant billing
- Access control
- Audit trails

### Configuration

```properties
# Enable/disable API key verification on all upload/download endpoints
api.key.verification=false

# When false: Keys are ignored, any request allowed
# When true: All requests must include valid X-API-Key header
```

### API Key Format

```
fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6
│
└─ Prefix "fms-" (5 chars) + 32 hex characters = 37 char total
```

#### Example Keys

```
fms-3f7a1b2c9d4e4f6abf120e8d5c6a7f3e
fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6
fms-f6f8a5c98b7d4e2c9f1a3b5c7d9e1f2a
```

### Key Management Lifecycle

#### 1. Generate a Key

```http
POST /admin/apikey/{tenant}
```

**Request**:
```bash
curl -X POST http://localhost:8081/admin/apikey/tenant1
```

**Response**:
```json
{
  "tenant": "tenant1",
  "apiKey": "fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6"
}
```

**Important**: Key shown only once. Store securely (password manager, vault, etc.)

**Example Storage**:
```
Vault Service: HashiCorp Vault
Secret Path: secret/fms/tenant1/api-keys
Value: fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6
```

#### 2. Use the Key

Include key in `X-API-Key` header for uploads/downloads:

```bash
# Upload with API key
curl -X POST http://localhost:8081/uploadNewFile/tenant1 \
  -H "X-API-Key: fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6" \
  -F "file=@document.pdf"

# Download with API key
curl -X GET http://localhost:8081/download/file-id-123 \
  -H "X-API-Key: fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6" \
  --output document.pdf
```

**Header Name**: `X-API-Key` (case-insensitive in HTTP)

#### 3. List Active Keys

```http
GET /admin/apikey/{tenant}
```

**Request**:
```bash
curl -X GET http://localhost:8081/admin/apikey/tenant1
```

**Response**:
```json
[
  {
    "id": 1,
    "keyValue": "fms-a1b2c3d4e5f6a7b8c9d0e1f2a3b4c5d6",
    "tenant": "tenant1",
    "active": true,
    "createdDate": "2026-04-02T10:30:00Z"
  },
  {
    "id": 2,
    "keyValue": "fms-e1f2a3b4c5d6a7b8c9d0e1f2a3b4c5d6",
    "tenant": "tenant1",
    "active": false,
    "createdDate": "2026-03-15T14:22:30Z"
  }
]
```

#### 4. Revoke Keys

```http
DELETE /admin/apikey/{tenant}
```

**Request**:
```bash
curl -X DELETE http://localhost:8081/admin/apikey/tenant1
```

**Response**:
```json
{
  "tenant": "tenant1",
  "status": "revoked"
}
```

**Effect**: All active keys marked as inactive (soft delete)

**Can Reactivate**: No current endpoint, but database allows it

### Key Validation Flow

When `api.key.verification=true`:

```
Request Received
  ↓
Extract X-API-Key header
  ↓
Header missing? → 401 Unauthorized
  ↓
Query ApiKeyRepository.findActiveByKeyValue(keyValue)
  ↓
Key not found? → 401 Unauthorized
  ↓
Key not active? → 401 Unauthorized
  ↓
Key found + active? → Extract tenant from key
  ↓
Proceed with request using key's tenant
```

### ApiKeyEntity JPA Model

```java
@Entity
@Table(name = "api_key_entity")
public class ApiKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @Column(name = "key_value")
    private String keyValue;
    
    @Column(name = "tenant")
    private String tenant;
    
    @Column(name = "active")
    private boolean active;
    
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
}
```

### Security Best Practices

#### For API Consumers

1. **Store keys securely**:
   - Use secrets management (Vault, AWS Secrets Manager, etc.)
   - Never commit keys to version control
   - Never log keys

2. **Rotate keys regularly**:
   - Delete old keys periodically
   - Regenerate annually

3. **Use HTTPS only**:
   - Keys transmitted in headers
   - Must use TLS/HTTPS to prevent interception

4. **Principle of least privilege**:
   - One key per integration
   - Revoke unused keys

#### For API Providers

1. **Hash keys in database** (Future enhancement):
   - Store hash, not plaintext
   - Regenerate if compromised

2. **Key rotation enforcement**:
   - Require periodic key refresh
   - API: POST `/admin/apikey/{tenant}/rotate`

3. **Audit trail** (Future enhancement):
   - Log key generation/revocation
   - Log key usage

4. **Rate limiting per key** (Future enhancement):
   - Different limits per key
   - Detect suspicious usage

---

## Error Handling

### Global Exception Handler

All application exceptions are caught by `GlobalExceptionHandler` and converted to consistent JSON responses.

### Exception Types and HTTP Status Codes

#### 1. FileNotFoundException (404)

**When**:
- Requested fileId doesn't exist in database
- File path not found on filesystem

**Response**:
```json
HTTP 404 Not Found
{
  "code": "FILE_NOT_FOUND",
  "message": "File with ID '3f7a1b2c-9d4e-4f6a-bf12-0e8d5c6a7f3e' not found",
  "details": null
}
```

**Examples**:
```
GET /download/nonexistent-file-id
```

---

#### 2. ApiKeyUnauthorizedException (401)

**When**:
- API key verification enabled (`api.key.verification=true`)
- API key header missing
- API key invalid (not found in database)
- API key revoked (marked inactive)

**Response**:
```json
HTTP 401 Unauthorized
{
  "code": "UNAUTHORIZED",
  "message": "Invalid or missing API key",
  "details": null
}
```

**Examples**:
```bash
# Missing header
curl -X GET http://localhost:8081/download/file-id-123

# Invalid key
curl -X GET http://localhost:8081/download/file-id-123 \
  -H "X-API-Key: fms-invalid"
```

---

#### 3. TenantIsNotAllowedException (403)

**When**:
- Tenant verification enabled (`tenant.verification=true`)
- Provided tenant not in allowed list (`tenant.list`)

**Response**:
```json
HTTP 403 Forbidden
{
  "code": "TENANT_NOT_ALLOWED",
  "message": "Tenant 'unknown-tenant' is not in the allowed list.",
  "details": null
}
```

**Examples**:
```bash
# Config: tenant.list=tenant1,tenant2 and tenant.verification=true
curl -X POST http://localhost:8081/uploadNewFile/tenant3 \
  -F "file=@document.pdf"
# Response: 403 Forbidden
```

---

#### 4. StorageQuotaExceededException (429)

**When**:
- Tenant's total storage would exceed `tenant.storage.quota.bytes`
- Incoming file + used storage > quota

**Response**:
```json
HTTP 429 Too Many Requests
{
  "code": "STORAGE_QUOTA_EXCEEDED",
  "message": "Storage quota of 1073741824 bytes exceeded for tenant 'tenant1'",
  "details": null
}
```

**Example**:
```
Quota: 1 GiB
Used: 900 MB
Incoming: 300 MB
Total: 1.2 GiB > 1 GiB → 429
```

---

#### 5. RateLimitExceededException (429)

**When**:
- Tenant's uploads in last 24h >= `tenant.upload.limit.per.day`
- Checked before quota check

**Response**:
```json
HTTP 429 Too Many Requests
{
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Rate limit of 10000 uploads exceeded for tenant 'tenant1'",
  "details": null
}
```

**Example**:
```
Limit: 10,000 uploads/day
Used: 10,000 in last 24h
Incoming: 1 more upload
→ 429 Rate Limit
```

---

#### 6. Generic Exception (500)

**When**:
- Unhandled exceptions
- Filesystem I/O errors (disk full, permission denied)
- Database connection errors
- Any unexpected error

**Response**:
```json
HTTP 500 Internal Server Error
{
  "code": "INTERNAL_ERROR",
  "message": "An unexpected error occurred.",
  "details": "java.io.IOException"
}
```

---

### Error Response DTO

All error responses follow this structure:

```java
public class ErrorDTO {
    private String code;        // Machine-readable error code
    private String message;     // Human-readable message
    private String details;     // Additional technical details (optional)
}
```

### Exception Handling Order

When an upload request arrives, checks happen in this order:

```
1. Tenant Verification (if enabled)
   → TenantIsNotAllowedException (403)

2. API Key Verification (if enabled)
   → ApiKeyUnauthorizedException (401)

3. Storage Quota Check
   → StorageQuotaExceededException (429)

4. Rate Limit Check
   → RateLimitExceededException (429)

5. File Storage & Database Operations
   → Any unexpected errors → Generic Exception (500)

6. Success
   → 200 OK
```

### Custom Exception Classes

All custom exceptions extend `RuntimeException`:

```java
// Example structure
public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String fileId) {
        super("File with ID '" + fileId + "' not found");
    }
}

public class ApiKeyUnauthorizedException extends RuntimeException {
    public ApiKeyUnauthorizedException() {
        super("Invalid or missing API key");
    }
}
```

---

## Scheduler and Cleanup

### File Cleanup Scheduler Overview

The `FileCleanUpScheduler` automatically removes old files from both filesystem and database based on configurable age.

### Configuration

```properties
# Threshold for deletion (age units)
file.cleanup.age=100

# Time unit: DAY, MONTH, YEAR
file.cleanup.age.type=DAY

# Results in: Delete files > 100 days old
```

### How It Works

**Trigger**: Runs every 5 minutes via Spring `@Scheduled` cron expression

**Schedule Expression**:
```java
@Scheduled(cron = "0 */5 * * * ?")
```

Interpretation:
- `0` = at :00 seconds
- `*/5` = every 5 minutes
- `* * *` = any hour, day, month, day of week

### Cleanup Process

```
1. Scheduler fires (every 5 minutes)
   ↓
2. Calculate cutoff date
   cutoffDate = now - (fileCleanUpAge × timeUnit)
   ↓
3. Query database for old files
   SELECT * FROM file_entity WHERE createdDate < cutoffDate
   ↓
4. For each old file:
   a. Delete file from filesystem
      Files.deleteIfExists(Paths.get(path))
   b. Delete database record
      fileRepository.deleteById(id)
   c. Log result
   ↓
5. Summary log
   "Stop clean-up by age. Candidates=X, Removed=Y"
```

### Example Scenarios

#### Scenario 1: Daily Cleanup

```properties
file.cleanup.age=1
file.cleanup.age.type=DAY
```

**Effect**: Every 5 minutes, delete files > 1 day old

**Timeline** (current time: Apr 2, 2026, 2:00 PM):
```
Cutoff: Apr 1, 2026, 2:00 PM
Delete: Files created before Apr 1, 2:00 PM
Keep: Files created Apr 1, 2:00 PM onward
```

#### Scenario 2: Monthly Cleanup

```properties
file.cleanup.age=1
file.cleanup.age.type=MONTH
```

**Effect**: Every 5 minutes, delete files > 1 month old (~30 days)

**Timeline** (current time: Apr 2, 2026):
```
Cutoff: Mar 3, 2026
Delete: Files created before Mar 3
Keep: Files created Mar 3 onward
```

#### Scenario 3: Yearly Cleanup

```properties
file.cleanup.age=2
file.cleanup.age.type=YEAR
```

**Effect**: Every 5 minutes, delete files > 2 years old

**Timeline** (current time: Apr 2, 2026):
```
Cutoff: Apr 2, 2024
Delete: Files created before Apr 2, 2024
Keep: Files created Apr 2, 2024 onward
```

### Cutoff Date Calculation

```java
private Date computeCutoffDate() {
    Instant now = Instant.now();
    switch (fileCleanUpAgeType) {
        case DAY:
            return Date.from(now.minus(fileCleanUpAge, ChronoUnit.DAYS));
        case MONTH:
            return Date.from(now.minus(fileCleanUpAge * 30L, ChronoUnit.DAYS));
        case YEAR:
        case YAER:  // Note: typo in code (YAER)
            return Date.from(now.minus(fileCleanUpAge * 365L, ChronoUnit.DAYS));
        default:
            return Date.from(now.minus(fileCleanUpAge, ChronoUnit.DAYS));
    }
}
```

### Error Handling

**On Cleanup Failure**:
- File not deleted from filesystem → logged, cleanup continues
- File not deleted from database → logged, cleanup continues
- Invalid path → skipped safely
- Disk full → exception logged, next run retries

**Resilience**:
```java
for (FileEntity entity : filesToBeRemoved) {
    try {
        Files.deleteIfExists(Paths.get(entity.getPath()));
        fileRepository.deleteById(entity.getId());
    } catch (Exception exception) {
        log.error("Failed to remove file id={} path={}", 
                  entity.getFileId(), entity.getPath(), exception);
        // Continue to next file
    }
}
```

### Monitoring Cleanup Activity

**Log Output Example**:
```
[INFO] Start clean-up by age. age=100, type=DAY
[INFO] Removed file id=file-001 tenant=tenant1 path=C:/filessssss/file-db/tenant1/file-001
[INFO] Removed file id=file-002 tenant=tenant2 path=C:/filessssss/file-db/tenant2/file-002
[ERROR] Failed to remove file id=file-003 path=C:/removed-dir/file-003
[INFO] Stop clean-up by age. Candidates=3, Removed=2
```

### Disabling Cleanup

There's no configuration to disable the scheduler. To disable:

1. **Option 1**: Set age to 0
   ```properties
   file.cleanup.age=0
   ```
   (Behavior: Delete nothing, as no files are > 0 days old - incorrect logic)

2. **Option 2**: Set age very high
   ```properties
   file.cleanup.age=9999
   file.cleanup.age.type=YEAR
   ```
   (Files older than ~9,999 years deleted)

3. **Option 3**: Comment out `@Scheduled` annotation (code change)

---

## Build and Deployment

### Build Process

#### Prerequisites

- Java Development Kit (JDK) 8+
- Maven 3.6+
- Git (optional, for cloning)

#### Build Steps

```bash
# 1. Clean previous build
mvn clean

# 2. Compile source code
mvn compile

# 3. Run tests
mvn test

# 4. Package into JAR
mvn package
```

**Single Command**:
```bash
mvn clean package
```

**Output**:
```
target/file-management-system-1.0.7.jar
target/file-management-system-1.0.7.jar.original
```

#### Build Output Details

- `file-management-system-1.0.7.jar`: Executable JAR (runnable)
- `file-management-system-1.0.7.jar.original`: Original JAR (not executable)

**Use the first one for production.**

### Running Locally

#### Command 1: Using Maven

```bash
mvn spring-boot:run
```

**Advantages**: Uses source code, auto-reloads on changes (dev mode)
**Disadvantages**: Requires Maven, slower startup

#### Command 2: Running JAR

```bash
java -jar target/file-management-system-1.0.7.jar
```

**Advantages**: Fast, simple, no Maven required
**Disadvantages**: No auto-reload

#### Command 3: JAR with External Config

```bash
java -jar target/file-management-system-1.0.7.jar \
  --spring.config.location=/path/to/application.properties
```

#### Command 4: JAR with Property Overrides

```bash
java -jar target/file-management-system-1.0.7.jar \
  --server.port=9090 \
  --spring.datasource.url=jdbc:postgresql://db:5432/fms \
  --api.key.verification=true
```

### Startup Verification

After starting, verify the application:

```bash
# Check Swagger UI
curl http://localhost:8081/swagger-ui.html

# Check H2 Console
curl http://localhost:8081/h2-console/

# Check application is running
curl http://localhost:8081/actuator/health
```

### Production Deployment

#### Linux Deployment

**Recommended Directory Structure**:

```
/app/
├── bin/
│   └── start.sh
├── config/
│   └── application.properties
├── file-db/                    # file.db.location
│   └── [file data]
├── lib/
│   └── file-management-system-1.0.7.jar
└── logs/
    └── application.log
```

**start.sh Script**:

```bash
#!/bin/bash

# Start file management system in background

JAR_PATH="/app/lib/file-management-system-1.0.7.jar"
CONFIG_PATH="/app/config/application.properties"
LOG_PATH="/app/logs/application.log"
PID_PATH="/app/file-management-system.pid"

nohup java -jar $JAR_PATH \
  --spring.config.location=$CONFIG_PATH \
  > $LOG_PATH 2>&1 &

echo $! > $PID_PATH
echo "Application started with PID: $(cat $PID_PATH)"
```

**Usage**:
```bash
chmod +x /app/bin/start.sh
/app/bin/start.sh
```

#### Windows Deployment

```cmd
java -jar C:\app\file-management-system-1.0.7.jar ^
  --spring.config.location=C:\app\config\application.properties
```

**Background (using start command)**:
```cmd
start javaw -jar C:\app\file-management-system-1.0.7.jar
```

#### Docker Deployment

**Dockerfile**:

```dockerfile
FROM openjdk:8-jre-slim

WORKDIR /app

COPY ../target/file-management-system-1.0.7.jar /app/app.jar
COPY config/application.properties /app/config/application.properties

ENV FILE_DB_LOCATION=/app/file-db
ENV STORAGE_STRATEGY=FILE_PER_YEAR_MONTH_DAY

EXPOSE 8081

CMD ["java", "-jar", "/app/app.jar", \
     "--spring.config.location=/app/config/application.properties"]
```

**Build and Run**:
```bash
docker build -t fms:latest .
docker run -d \
  -p 8081:8081 \
  -v /data/file-db:/app/file-db \
  -v /data/config:/app/config \
  --name fms \
  fms:latest
```

### Memory Configuration

**Adjust JVM Memory**:

```bash
java -Xmx2G -Xms1G -jar file-management-system-1.0.7.jar
```

**Parameters**:
- `-Xms1G`: Initial heap size (1 GiB)
- `-Xmx2G`: Maximum heap size (2 GiB)

**For Containers** (Docker):
```dockerfile
ENV JAVA_OPTS="-Xmx2G -Xms1G"
CMD ["bash", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
```

### Performance Tuning

**For High-Concurrency Systems**:

```bash
java -Xmx4G -Xms2G \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -jar file-management-system-1.0.7.jar
```

**Flags Explained**:
- `-XX:+UseG1GC`: Use G1 Garbage Collector (good for large heaps)
- `-XX:MaxGCPauseMillis=200`: Target pause time for GC

---

## Testing

### Test Location and Structure

```
src/test/java/com/fms/
├── controller/
│   ├── DownloadFileControllerTest.java
│   └── UploadFileControllerTest.java
├── model/
│   ├── CreateFileRequestDTOTests.java
│   ├── CreateFileResponseDTOTests.java
│   ├── ErrorDTOTests.java
│   └── FileRequestDTOTests.java
├── service/
│   ├── DownloadFileServiceTest.java
│   ├── TenantServiceTest.java
│   ├── id/
│   │   ├── FileIdServiceFactoryTest.java
│   │   ├── FileIdStrategyTest.java
│   │   ├── InstantFileIdStrategyServiceTest.java
│   │   └── UUIDFileIdStrategyServiceTest.java
│   └── storage/
│       ├── FilePerDateStorageStrategyServiceTest.java
│       ├── FilePerYearDateStorageStrategyServiceTest.java
│       ├── FilePerYearMonthDateStorageStrategyServiceTest.java
│       ├── FilePerYearMonthDayStorageStrategyServiceTest.java
│       ├── FilePerYearMonthStorageStrategyServiceTest.java
│       ├── FileStorageStrategyFactoryTest.java
│       └── FileStorageStrategyServiceTest.java
└── util/
    ├── DateUtilsTest.java
    ├── FileUtilsTest.java
    └── MapHelperTest.java
```

### Running Tests

#### All Tests

```bash
mvn test
```

#### Specific Test Class

```bash
mvn test -Dtest=FileStorageStrategyFactoryTest
```

#### Specific Test Method

```bash
mvn test -Dtest=FileStorageStrategyFactoryTest#testStrategySelection
```

#### With Coverage Report

```bash
mvn test jacoco:report
```

**Report Location**: `target/site/jacoco/index.html`

### Test Framework

- **Framework**: JUnit 4
- **Included in**: `spring-boot-starter-test`
- **Additional Dependencies**: Mockito, AssertJ

### Example Test Structure

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class UploadFileServiceTest {

    @MockBean
    private FileRepository fileRepository;

    @InjectMocks
    private UploadFileService uploadFileService;

    @Test
    public void testUploadSuccess() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);

        // Act
        CreateFileResponseDTO response = uploadFileService.upload(file, "tenant1");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getUuid()).isNotNull();
    }
}
```

### Mocking Database for Tests

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class QuotaServiceTest {

    @MockBean
    private FileRepository fileRepository;

    @InjectMocks
    private QuotaService quotaService;

    @Test
    public void testQuotaCheck() {
        // Mock repository to return 800 MB used
        when(fileRepository.sumStorageByTenant("tenant1"))
            .thenReturn(838860800L);

        // Should succeed with 300 MB upload
        quotaService.assertUploadAllowed("tenant1", 314572800L);
    }

    @Test(expected = StorageQuotaExceededException.class)
    public void testQuotaExceeded() {
        // Mock repository to return 900 MB used
        when(fileRepository.sumStorageByTenant("tenant1"))
            .thenReturn(943718400L);

        // Should fail with 300 MB upload
        quotaService.assertUploadAllowed("tenant1", 314572800L);
    }
}
```

---

## Monetization Features

### Overview

The system includes built-in features for monetization models:

1. **API Key Authentication** - Control access
2. **Storage Quotas** - Limit per-tenant storage
3. **Rate Limiting** - Limit uploads per day
4. **Usage Tracking** - Track operations for billing

### Monetization Models

#### Model 1: Freemium

**Setup**:
```properties
api.key.verification=false
tenant.storage.quota.bytes=1073741824
tenant.upload.limit.per.day=1000
```

**Strategy**:
- Public API (no key required)
- 1 GiB free storage
- 1,000 uploads/day free tier
- Premium tier: higher quotas for paying customers

**Implementation**:
```
Tenant: free-user-123
  - Quota: 1 GiB (freemium limit)
  - Rate Limit: 1,000/day (freemium limit)
  - API Key: Not required

Tenant: premium-user-456
  - Quota: 10 GiB (premium limit)
  - Rate Limit: 100,000/day (premium limit)
  - API Key: Required for audit/tracking
```

#### Model 2: Per-Tenant Licensing

**Setup**:
```properties
api.key.verification=true
tenant.verification=true
tenant.list=acme-corp,startup-inc,enterprise-ltd
```

**Strategy**:
- Each enterprise tenant is a separate account
- Each has own API key for authentication
- Each has own quotas (configured differently)
- Usage tracked per tenant for billing

**Implementation**:
```
Tenant: acme-corp
  - API Key: fms-acme001... (generated)
  - Quota: 50 GiB (enterprise)
  - Rate Limit: 500,000/day
  - Monthly fee: $500/month

Tenant: startup-inc
  - API Key: fms-startup01... (generated)
  - Quota: 10 GiB (growth)
  - Rate Limit: 50,000/day
  - Monthly fee: $50/month
```

#### Model 3: Pay-Per-Use

**Setup**:
```properties
api.key.verification=true
tenant.storage.quota.bytes=104857600
tenant.upload.limit.per.day=100
```

**Strategy**:
- Strict quotas to prevent overuse
- API key required for audit trail
- Usage tracked via UsageEvent table
- Billing: count uploads + storage bytes

**Implementation**:
```
Tenant: customer-789
  - API Key: Required (for audit)
  - Quota: 100 MB (hard limit)
  - Rate Limit: 100 uploads/day
  - Billing: $0.001 per upload + $0.01 per GiB/month
```

#### Model 4: Capacity-Based Tiers

**Setup**:
```properties
api.key.verification=true

# Application manages per-tenant quotas dynamically
# (via future database-driven quota feature)
```

**Tiers**:
```
Tier 1 (Basic):
  - Cost: Free
  - Storage: 100 MB
  - Uploads/day: 50
  - Includes: Basic API access

Tier 2 (Professional):
  - Cost: $10/month
  - Storage: 1 GiB
  - Uploads/day: 1,000
  - Includes: API access, webhooks (future)

Tier 3 (Enterprise):
  - Cost: Custom
  - Storage: Unlimited
  - Uploads/day: Unlimited
  - Includes: API access, webhooks, SLA, support
```

### Usage Tracking for Billing

The `UsageEvent` table tracks all operations:

```sql
SELECT 
    tenant,
    COUNT(CASE WHEN event_type = 'UPLOAD' THEN 1 END) as uploads,
    COUNT(CASE WHEN event_type = 'DOWNLOAD' THEN 1 END) as downloads,
    SUM(CASE WHEN event_type = 'UPLOAD' THEN bytes ELSE 0 END) / 1024 / 1024 / 1024 as upload_gb,
    SUM(CASE WHEN event_type = 'DOWNLOAD' THEN bytes ELSE 0 END) / 1024 / 1024 / 1024 as download_gb
FROM usage_event
WHERE timestamp >= DATE_SUB(NOW(), INTERVAL 1 MONTH)
GROUP BY tenant
ORDER BY uploads DESC;
```

**Output Example**:
```
tenant1 | 15000 | 45000 | 45.2 | 120.5
tenant2 | 500   | 1000  | 2.1  | 5.3
tenant3 | 8000  | 25000 | 28.7 | 87.2
```

### Billing Scenarios

**Scenario 1**: $0.001 per upload + $0.01 per stored GiB/month

```
Tenant: customer-001
Month: April 2026
Uploads: 10,000
Downloads: 30,000
Stored at month-end: 2.5 GiB
Total storage-months: 2.5 GiB

Billing:
  Uploads: 10,000 × $0.001 = $10.00
  Storage: 2.5 × $0.01 = $0.025
  Total: $10.025
```

**Scenario 2**: Flat monthly + overages

```
Tenant: customer-002
Plan: Professional ($50/month)
  - Includes: 1 GiB storage + 1,000 uploads
Month: April 2026
Usage: 2 GiB + 2,000 uploads

Billing:
  Base Plan: $50.00
  Overage Storage: 1 GiB × $0.01 = $0.01
  Overage Uploads: 1,000 × $0.001 = $1.00
  Total: $51.01
```

### Future Monetization Features

Recommended enhancements:

1. **Per-Tenant Quota Database Storage**
   ```java
   @Entity
   class TenantQuota {
       String tenant;
       long storageQuotaBytes;
       long uploadLimitPerDay;
   }
   ```

2. **Billing API**
   ```http
   GET /admin/billing/{tenant}/usage?month=2026-04
   GET /admin/billing/{tenant}/invoices
   POST /admin/billing/{tenant}/invoice
   ```

3. **Usage Events Export**
   ```http
   GET /admin/usage/{tenant}/export?format=csv&month=2026-04
   ```

4. **Webhook Notifications**
   ```
   - Quota near limit
   - Rate limit approached
   - Invoice generated
   - File deleted by cleanup
   ```

5. **Subscription Management**
   ```http
   POST /admin/subscription/{tenant}
   PATCH /admin/subscription/{tenant}
   ```

---

## Advanced Features

### File Metadata and Integrity

#### Stored Metadata

Every uploaded file stores comprehensive metadata:

```java
FileEntity {
    Long id;                    // Auto-increment PK
    String fileId;              // Logical identifier
    String directory;           // Custom directory (legacy)
    String path;                // Full filesystem path
    String tenant;              // Tenant identifier
    Date createdDate;           // Upload timestamp
    Long fileSizeBytes;         // Size in bytes
    String contentType;         // MIME type (e.g., application/pdf)
    String checksum;            // SHA-256 hash
}
```

#### Checksum Calculation

Files are checksummed for integrity verification:

```java
private String checksumSafe(MultipartFile file) {
    try {
        byte[] bytes = file.getBytes();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(bytes);
        return Base64.getEncoder().encodeToString(digest);
    } catch (Exception e) {
        log.warn("Failed to calculate checksum", e);
        return null;  // Optional, continues if fails
    }
}
```

**Use Cases**:
- Verify file integrity after download
- Detect file corruption
- Prevent duplicate uploads (same checksum)

#### Content-Type Preservation

Original MIME type stored:

```
Uploaded: document.pdf
MIME Type Stored: application/pdf

Uploaded: image.jpg
MIME Type Stored: image/jpeg

Uploaded: video.mp4
MIME Type Stored: video/mp4
```

**Use Cases**:
- Send correct `Content-Type` header on download
- Filter by type in admin queries
- Type-based cleanup policies

### Multi-Tenant Isolation

#### Tenant Boundaries

Files completely isolated per tenant:

```
Directory: /file-db/tenant1/
  - Can only upload/download tenant1 files
  - Cannot see tenant2 files

Directory: /file-db/tenant2/
  - Completely separate namespace
  - Independent quotas and rate limits
```

#### Tenant-Aware Queries

```java
// Only returns tenant1 files
fileRepository.findByFileIdAndTenant(fileId, "tenant1")

// Quota per tenant
fileRepository.sumStorageByTenant("tenant1")

// Usage per tenant
usageEventRepository.findByTenantAndTimestampGreaterThan("tenant1", past24hours)
```

#### Cross-Tenant Protection

- Download endpoint with tenant parameter verifies ownership
- File cleanup doesn't cross tenant boundaries
- Quotas isolated per tenant
- Rate limits isolated per tenant
- API keys tied to specific tenant

### Concurrent Upload Handling

#### Transaction Safety

Each upload is `@Transactional`:

```java
@Transactional
public CreateFileResponseDTO upload(MultipartFile file, String tenant) {
    // Atomic transaction
    // 1. Filesystem write
    // 2. Database insert
    // 3. Usage tracking
    // All succeed or all rollback
}
```

#### File Naming Collision Handling

With UUID strategy (default), collisions impossible (1 in 5 billion).

With INSTANT strategy, nanosecond collisions possible:

```
File 1: System.nanoTime() = 1743667428123456789
File 2: System.nanoTime() = 1743667428123456789 (same nanosecond)

Result: Both files have same ID
  → Last write wins (overwrites)
  → Not recommended for high-concurrency
```

#### Concurrent Rate Limit Checks

**Issue**: Race condition in rate limit check

```
Thread 1: Check rate limit → 9,999 uploads in 24h (allowed)
          [1ms delay]
Thread 2: Check rate limit → 9,999 uploads in 24h (allowed)
Thread 1: Record upload → 10,000 in database
Thread 2: Record upload → 10,001 in database (OVER LIMIT)

Both threads allowed, but 2nd exceeded limit
```

**Current Mitigation**: Database writes are serialized, so error only affects the very last concurrent request

**Recommendation**: Not a critical issue for most systems (1 request over limit per concurrent spike)

### Large File Handling

#### Multipart Configuration

```properties
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```

#### For Larger Files

**Option 1**: Increase multipart limits

```properties
spring.servlet.multipart.max-file-size=1GB
spring.servlet.multipart.max-request-size=1GB
server.tomcat.max-http-post-size=-1
```

**Option 2**: Use chunked upload client-side

```javascript
// Client-side chunking (not built into FMS)
const chunkSize = 5 * 1024 * 1024;  // 5 MB chunks
for (let i = 0; i < file.size; i += chunkSize) {
    const chunk = file.slice(i, i + chunkSize);
    // Upload each chunk separately
}
```

**Option 3**: Use byte-array upload endpoint for pre-chunked content

### Database Query Performance

#### High-Volume Deployments

**Recommendations**:

1. **Index file_id column**:
   ```sql
   CREATE INDEX idx_file_entity_file_id ON file_entity(file_id);
   ```

2. **Index tenant column**:
   ```sql
   CREATE INDEX idx_file_entity_tenant ON file_entity(tenant);
   ```

3. **Composite index for common queries**:
   ```sql
   CREATE INDEX idx_file_entity_file_id_tenant ON file_entity(file_id, tenant);
   ```

4. **Index usage_event timestamps**:
   ```sql
   CREATE INDEX idx_usage_event_timestamp ON usage_event(timestamp);
   CREATE INDEX idx_usage_event_tenant_timestamp ON usage_event(tenant, timestamp);
   ```

#### Query Optimization

**Current Queries**:
```java
// Fast (indexed by file_id)
fileRepository.findByFileId(fileId)

// Fast (indexed by file_id + tenant)
fileRepository.findByFileIdAndTenant(fileId, tenant)

// Potentially slow (aggregation)
fileRepository.sumStorageByTenant(tenant)

// Slow for large usage tables
usageEventRepository.findByTenantAndTimestampGreaterThan(tenant, date)
```

**Recommendations**:
- Create database indexes
- Add pagination to large result sets
- Archive old UsageEvents regularly
- Use database views for complex aggregations

---

## Troubleshooting

### Common Issues and Solutions

#### Issue 1: "Storage quota exceeded" when storage not full

**Symptoms**:
```
HTTP 429 Too Many Requests
{
  "code": "STORAGE_QUOTA_EXCEEDED",
  "message": "Storage quota of 1073741824 bytes exceeded"
}
```

**Causes**:
1. Tenant's total file size exceeds configured quota
2. Quota configuration is too low for actual usage
3. Deleted files not reflected in database

**Solutions**:

```sql
-- Check actual usage per tenant
SELECT tenant, SUM(file_size_bytes) as total_bytes
FROM file_entity
GROUP BY tenant;

-- Compare with configured quota
SELECT 1073741824 as quota_bytes;

-- If quota is too low, increase it:
-- Edit application.properties
tenant.storage.quota.bytes=5368709120  -- 5 GiB instead of 1 GiB
```

**Solution 2**: Clean up old files

```sql
-- Delete old files older than 30 days
DELETE FROM file_entity 
WHERE created_date < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- Also delete from filesystem
-- (Manual cleanup needed if scheduler is disabled)
```

#### Issue 2: "Rate limit exceeded" with low upload count

**Symptoms**:
```
HTTP 429 Too Many Requests
{
  "code": "RATE_LIMIT_EXCEEDED",
  "message": "Rate limit of 10000 uploads exceeded"
}
```

**Cause**:
- Tenant has many uploads recorded in last 24 hours
- Rolling window includes uploads from past 24h, not just today

**Solution**:

```sql
-- Check uploads in last 24 hours
SELECT COUNT(*) as uploads_24h
FROM usage_event
WHERE tenant = 'tenant1'
AND event_type = 'UPLOAD'
AND timestamp > DATE_SUB(NOW(), INTERVAL 24 HOUR);

-- If this is high, increase limit or wait for window to pass
```

**Solution 2**: Increase rate limit

```properties
# Allow more uploads per day
tenant.upload.limit.per.day=50000
```

**Solution 3**: Clear old usage events

```sql
-- Delete usage events older than 30 days
DELETE FROM usage_event
WHERE timestamp < DATE_SUB(NOW(), INTERVAL 30 DAY);
```

#### Issue 3: "File not found" after upload

**Symptoms**:
- File uploaded successfully (200 response)
- Download returns 404

**Causes**:
1. File deleted from filesystem but database record remains
2. Storage strategy mismatch (changed after upload)
3. File path corrupted

**Solution**:

```sql
-- Check if file exists in database
SELECT file_id, path FROM file_entity WHERE file_id = 'your-file-id';

-- Verify path exists on filesystem
-- ls -la /path/from/above

-- If path incorrect, check storage strategy
-- Edit application.properties
storage.strategy=FILE  -- Ensure this matches deployment

-- If data corruption, delete and re-upload
DELETE FROM file_entity WHERE file_id = 'your-file-id';
```

#### Issue 4: Disk space exceeded

**Symptoms**:
- Upload fails with I/O error
- Cleanup scheduler errors in logs

**Solution**:

```bash
# Check disk usage
df -h /path/to/file-db-location

# Manually run cleanup
# (Scheduler runs every 5 min, but you can do it sooner)
# By restarting the application or using admin endpoint

# Delete oldest files
find /file-db-location -type f -mtime +100 -delete

# Or use FMS scheduler configuration
file.cleanup.age=50
file.cleanup.age.type=DAY
# Restart application
```

#### Issue 5: High memory usage

**Symptoms**:
- Application becomes slow
- OutOfMemoryError in logs
- Heap usage constantly at 100%

**Causes**:
1. JVM heap too small for workload
2. Large file uploads exhausting memory
3. Memory leak in long-running application

**Solutions**:

```bash
# Increase JVM heap size
java -Xmx4G -Xms2G -jar file-management-system-1.0.7.jar

# Monitor heap usage
jmap -heap <PID>

# If memory leak suspected, restart application regularly
# via cron job (daily or weekly)
```

#### Issue 6: Database connection errors

**Symptoms**:
```
com.zaxxer.hikari.pool.HikariPool - Connection is not available
```

**Causes**:
1. Database server down
2. Connection credentials incorrect
3. Network unreachable
4. Database connection pool exhausted

**Solutions**:

```bash
# Test database connectivity
ping db.example.com
telnet db.example.com 5432

# Check credentials
# In application.properties:
spring.datasource.url=jdbc:postgresql://db.example.com:5432/fms
spring.datasource.username=correct_user
spring.datasource.password=correct_password

# Increase connection pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Verify database is running
# PostgreSQL: psql -h db.example.com -U postgres -c "SELECT version();"
```

#### Issue 7: API key not working

**Symptoms**:
```
HTTP 401 Unauthorized
{
  "code": "UNAUTHORIZED",
  "message": "Invalid or missing API key"
}
```

**Causes**:
1. `api.key.verification=false` (feature disabled)
2. Key revoked
3. Wrong header name
4. Key format incorrect

**Solutions**:

```properties
# Enable API key verification
api.key.verification=true
```

```bash
# Verify header format
curl -H "X-API-Key: fms-a1b2c3d4..." \
  http://localhost:8081/download/file-id

# Generate new key
curl -X POST http://localhost:8081/admin/apikey/tenant1

# Reactivate revoked key
# SQL: UPDATE api_key_entity SET active = true WHERE id = ?
```

#### Issue 8: File cleanup not running

**Symptoms**:
- Old files not deleted
- No cleanup logs

**Causes**:
1. Age threshold set too high
2. No files older than threshold
3. Scheduler disabled
4. Permission issues deleting files

**Solutions**:

```properties
# Ensure cleanup age is reasonable
file.cleanup.age=30
file.cleanup.age.type=DAY

# Monitor logs
tail -f /app/logs/application.log | grep "clean-up"

# Check file ages
stat /file-db-location/tenant1/file-id
```

---

## Conclusion

This comprehensive documentation covers all aspects of the File Management System v1.0.7:

- **Architecture & Design**: Hybrid metadata/filesystem model
- **Core Features**: Upload, download, multi-tenancy, quotas, rate limiting
- **Configuration**: 15+ configurable properties for different deployments
- **Storage & ID Strategies**: 6 storage layouts + 2 ID generation methods
- **Databases**: H2 (dev), PostgreSQL (prod recommended), MySQL (alternative)
- **Security**: Tenant isolation, API key authentication
- **Monetization**: Built-in features for freemium, licensing, pay-per-use models
- **Operations**: Build, deploy, monitor, troubleshoot
- **Advanced**: Concurrency, performance tuning, large files, multi-tenancy

---

**For support or questions, refer to the inline code comments or contact the maintainer.**

