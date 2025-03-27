1.1.4 / 2025-03-31
==================

Minor coverage updates and code analysis improvements.

1.1.3 / 2024-10-31
==================

Improvement:
* [OIS-48](https://openlmis.atlassian.net/browse/OIS-48): Update service base images to versions without known vulnerabilities

1.1.2 / 2022-04-21
==================

Improvement:
* [OLMIS-7568](https://openlmis.atlassian.net/browse/OLMIS-7568): Use openlmis/dev:7 and openlmis/service-base:6.1

1.1.1 / 2021-10-29
==================

Improvement:
* [OLMIS-6983](https://openlmis.atlassian.net/browse/OLMIS-6983): Sonar analysis and contract tests runs only for snapshots

1.1.0 / 2020-04-14
==================

New functionality added in a backwards-compatible manner:
* [OLMIS-6771](https://openlmis.atlassian.net/browse/OLMIS-6771): Update Spring Boot version to 2.x:
  * Spring Boot version is 2.2.2.
  * New mechanism for loading Spring Security for OAuth2 (matching Spring Boot version), new versions for RAML tester, RAML parser.
  * Fix repository method signatures (findOne is now findById, etc.); additionally they return Optional.
  * Fix unit tests.
  * Fix integration tests.
  * API definitions require "Keep-Alive" header for web integration tests.

1.0.3 / 2019-05-27
==================

Improvements:
* [OLMIS-4531](https://openlmis.atlassian.net/browse/OLMIS-4531): Added compressing HTTP POST responses.
* [OLMIS-6129](https://openlmis.atlassian.net/browse/OLMIS-6129): Added package-lock.json file.

1.0.2 / 2018-12-12
==================

Improvements:
* [OLMIS-4295](https://openlmis.atlassian.net/browse/OLMIS-4295): Updated checkstyle to use newest google style.

1.0.1 / 2018-08-16
==================

Improvements:
* [OLMIS-4650](https://openlmis.atlassian.net/browse/OLMIS-4650): Added Jenkinsfile

1.0.0 / 2018-04-24
==================

Released openlmis-diagnostics 1.0.0 as part of openlmis-ref-distro 3.3. This was the first stable release of openlmis-diagnostics.

Features
* [OLMIS-1652](https://openlmis.atlassian.net/browse/OLMIS-1652): Retrieve application's health status
