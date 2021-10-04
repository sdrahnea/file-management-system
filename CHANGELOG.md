# Changelog
All notable changes to this project will be documented in this file.

## [1.0.7] - 2021-MM-DD [Unreleased]
### Added
- add clients for interaction with the APIs

### Fixed

## [1.0.6] - 2021-10-04
### Added
- add file id strategy for generating file names:
  -- UUID: will generate an uuid value;
  -- INSTANT: will generate equivalent epoch value for now (numbers)

### Fixed
- small refactors

## [1.0.5] - 2021-09-21
### Added
- add file metadata (size, creation date, file extension, etc)
- add file version
- add unit tests 
- add performance tests (related to h2 database)
- add possibility to communicate over queue 

### Fixed
- fix error log text
- small refactoring

## [1.0.4] - 2021-08-29
### Added
- constraint file retrieve / store by tenant
- add new end-point: download functionality by tenant

### Fixed
- creation file path in FileStorageStrategyService
- small refactors

## [1.0.3] - 2021-07-12
### Added
- file storage strategies:
  -- ${file.db.location} / ${tenant} / ${file_id}
  -- ${file.db.location} / ${tenant} / ${date} / ${file_id}
  -- ${file.db.location} / ${tenant} / ${year} / ${date} / ${file_id}
  -- ${file.db.location} / ${tenant} / ${year} / ${month} / ${date} / ${file_id}
  -- ${file.db.location} / ${tenant} / ${year} / ${month} / ${day} / ${file_id}
  -- ${file.db.location} / ${tenant} / ${year} / ${month} / ${file_id}
- constraint file retrieve / store by tenant

### Fixed
- split controller and services into dedicated ones

## [1.0.2] - 2021-07-02
### Added
- new end-point: upload file by tenant only

### Fixed
- fix data sql file 
- small refactors

## [1.0.1] - 2021-05-07
### Added
- storage to H2 database
- use tenant instead of environment
- add multi-tenant possibility

### Fixed
- bug related to multipart file config

## [1.0.0] - 2021-05-04
### Added
- add logging (please see and update log4j2.xml file)
- option to run application with specific configuration file
- update and write documentation
- fix the query which returns the multiple records in case of download request
- allow uploading files more than 100 Mb size 
- fix upload end-points by adding request body
- README file contains main information about project
- initial application commit
- Swagger UI
- uploadMultipartFile/{documentId} REST end-point to upload document like multipart file.
  For swagger testing proposes.
- upload/{documentId} REST end-point to upload file from other clients
- upload/{documentId}/{directory} REST end-point to upload file from other clients which contains
  a directory. This is TEMPORARY use case.
- download/{documentId} REST end-point return saved content by document id.
