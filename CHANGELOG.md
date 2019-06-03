# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
## [1.1.5]
### Added
- parameter to switch off script cache usage
- parameter for script cache location
- if there is ivy.xml files in the same location as config all dependencies will be added to classpath
- log information in idea log
- create gradle task to generate build info class
- [LM-32](https://camsoft.myjetbrains.com/youtrack/issue/LM-32) plugin context now has script file info
### Changed
- only two exception can be used AppConfigException and AppDataException
- change classifier parameters and output. Now classifier gets just ID of entity
- command line parameters: now 3 commands: --help, --version and --process 
### Fixed
- [LM-27](https://camsoft.myjetbrains.com/youtrack/issue/LM-27)
    Script hash calculation now includes import directives
- [LM-30](https://camsoft.myjetbrains.com/youtrack/issue/LM307)
    Some documentation for API added
## [1.1.2]
### Added
- [LM-4](https://camsoft.myjetbrains.com/youtrack/issue/LM-4)
    Configuration script caching
### Fixed
- [LM-17](https://camsoft.myjetbrains.com/youtrack/issue/LM-17)
    Dependency jars removed from lomout.jar
## [1.1.0] - 2019-03-27
### Added
- [LM-14](https://camsoft.myjetbrains.com/youtrack/issue/LM-14)
    Implemented support for XLSM (Excel 2007 with macro) files
- [LM-13](https://camsoft.myjetbrains.com/youtrack/issue/LM-13)
    Plugin context now has current logger property
- Function(lambda) that is used instead of plugins noe has access to plugin context as receiver
### Changed
- Version number change according to [Semantic Versioning](https://semver.org/spec/v2.0.0.html)
