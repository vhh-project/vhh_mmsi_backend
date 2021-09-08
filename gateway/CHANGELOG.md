# Changelog

All notable changes to the gateway project since version 1.0.0
are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/),

The changelog has an "Unreleased" section at the top where all
notable changes should be documented. There are five different categories:

  * **Added** for new features.
  * **Changed** for changes in existing functionality.
  * **Deprecated** for soon-to-be removed features.
  * **Removed** for now removed features.
  * **Fixed** for any bug fixes.
  * **Security** in case of vulnerabilities.

The JIRA ticket number should be recorded for each change. The 
description of the change can be the ticket title, but can also 
be another (possibly clearer/more elaborate) statement.

For example:

    ## [9.9.9] - 9999-09-09
    ### Fixed
    - MMSI-9999 NullPointerException when the user is not found

See [Keep a Changelog](https://keepachangelog.com/) for further inspiration.

## [Unreleased]

### Added

  - MMSI-730 Extended startup information with svn revision and build time
  - MMSI-727 Added configuration for post logout redirect url 
  - MMSI-665 Added banner and version information on startup

### Removed

  - MMSI-897 Removed thymeleaf

## [1.0.0] - 2020-09-18

  - Initial Release
