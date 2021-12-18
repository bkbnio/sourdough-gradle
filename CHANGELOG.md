# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.0] - December 18th, 2021
### Question
- Was it all a dream?

## [0.2.3] - 12/17/2021
### Changes
- Going to try moving signing to after-evaluate block

## [0.2.2] - 12/17/2021
### Changed
- Honestly no idea if this will work, but switched the signing extension to sign the publication rather than the name

## [0.2.1] - 12/17/2021
### Changed
- Sonatype now applied directly, fixes bug where `publishToSonatype` task was not found on root

## [0.2.0] - 12/17/2021
### Changed
- Back to Properties (aka i'm a lazy boi)

## [0.1.2] - 12/17/2021
### Changed
- Modified Action Pipelines to use Gradle Wrapper with built-in caching

## [0.1.1] - 12/17/2021
### Changed
- Allow for JVM Target to be specified separate from JVM Version
- `javaVersion` is now `toolChainJavaVersion`

## [0.1.0] - 12/13/2021
### Changed
- Fixed bug in Dokka setup causing failure if missing folder

## [0.0.5] - 12/12/2021
### Added
- The beginnings of an actual README
### Changed
- Library extension changed to open class with normal strings

## [0.0.4] - 12/12/2021
### Added
- Snapshot Publishing to GitHub packages
### Changed
- Safer Dokka Configuration (will generate necessary setup if not present)
- Bumped gradle to 7.3.1

## [0.0.3] - 12/04/2021
### Changed
- Fixed bug in gradle file for application plugin

## [0.0.2] - 12/04/2021
### Changed
- Added `tags` to plugin to enable push to gradle repository

## [0.0.1] - 12/02/2021

- Started my project using [sourdough](https://github.com/bkbnio/sourdough-kt) ❤️
- Setup initial Gradle plugins with library and root configurations