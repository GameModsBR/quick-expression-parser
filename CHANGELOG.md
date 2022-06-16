# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
- Upgraded to Kotlin 1.6.21
- Added support to locales for internationalization

## [0.0.2] - 2018-08-20
### Added
- This changelog file
- A license file

### Changed
- Improved the code quality
- Removed 5 unnecessary classes
- Renamed Java's API class to `QuickExpressionParser`
- Added KDocs and examples to the public methods and classes.

## 0.0.1 - 2018-08-19
### Added
- Support for: `Expression with #direct identifier`
- Support for: `Expression with #{curlyBraces} identifier`
- Support for: `Expression with #{paramized.object.value} identifiers`
- Support for: `Expression with #{object.isValid ? 'Conditional' : "Identifiers" }`
- Support for: `Expression with #{condition ? "Escaped \" \v\a\l\u\e\s" }`
- Support for: `Expression with #{multiple ? condition ? and : also ? multiple : chained : elses  }`
- Support for: `Expression with #{strings ? "containing #{nested ? "expressions" : 'and strings'}"}`

[0.0.2]: https://github.com/GameModsBr/quick-expression-parser/compare/v0.0.1...v0.0.2
[Unreleased]: https://github.com/GameModsBr/quick-expression-parser/compare/v0.0.2...HEAD
