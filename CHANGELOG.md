# Changelog
All notable changes to this project will be documented in this file.

## [1.0.2] - 2021-05-XX [Unreleased]
### Added
- file storage strategy

### Fixed
- 

## [1.0.1] - 2021-05-XX [Unreleased]
### Added
- storage to H2 database

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