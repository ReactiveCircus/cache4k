# Change Log

## Unreleased

### Changed

- Kotlin 2.1.20
- Migrate to Sonatype Central Portal. 

## 0.14.0

### Changed

- Kotlin 2.1.0
- Coroutines 1.10.1
- AtomicFU 0.27.0
- Stately 2.1.0

## 0.13.0

### Added

Added new targets:
- `wasmJs` - [#45](https://github.com/ReactiveCircus/cache4k/pull/45)

### Changed

- Switch internal map implementation to `ConcurrentHashMap` for JVM targets - [#39](https://github.com/ReactiveCircus/cache4k/pull/39)
- Kotlin 1.9.22
- Coroutines 1.8.0
- AtomicFU 0.23.2
- Stately 2.0.6

## 0.12.0

### Added

Added new targets:
- `linuxArm64`

### Changed

- Kotlin 1.9.10
- Coroutines 1.7.2
- AtomicFU 0.21.0
- Stately 2.0.5

## 0.11.0

### Added

- New event listener APIs (@darkxanter) - [#35](https://github.com/ReactiveCircus/cache4k/pull/35)

### Fixed

- Downgrade stately to 1.2.5 to fix duplicate class error - [#36](https://github.com/ReactiveCircus/cache4k/pull/36)

## 0.10.0

### Added

Added new targets:
- `watchosArm64`
- `watchosSimulatorArm64`
- `watchosX64`

`FakeTimeSource` is now public - [#30](https://github.com/ReactiveCircus/cache4k/pull/30)

### Changed

- Kotlin 1.8.20.
- AtomicFU 0.20.2.
- Stately 2.0.0-rc1.

## 0.9.0

### Fixed

- Revert to using `state-collections` to fix JVM concurrency issue - [#23](https://github.com/ReactiveCircus/cache4k/issues/23)

### Changed

- Kotlin 1.7.20.
- AtomicFU 0.18.5.

## 0.8.0

### Added

Added new targets:
- `linuxX64`
- `macosArm64`

### Changed

- Kotlin 1.7.10.
- Coroutines 1.6.4.
- AtomicFU 0.18.3.

## 0.7.0

### Changed

- Kotlin 1.7.0.
- Coroutines 1.6.3.
- AtomicFU 0.18.2.

## 0.6.0

### Changed

- `fakeTimeSource` in `Cache.Builder` has been renamed to `timeSource`, and the `FakeTimeSource` implementation has been removed (still available in our tests) - [#20](https://github.com/ReactiveCircus/cache4k/pull/20)
- Kotlin 1.6.21.

## 0.5.0

This release adds support for new native memory model and drops support for the old one. Many thanks to @dcvz for the contribution. 

### Added

- Synchronize cache loader by key - [#15](https://github.com/ReactiveCircus/cache4k/pull/15)
- Support new native memory model and drop support for old memory model - [#17](https://github.com/ReactiveCircus/cache4k/pull/17)

### Changed

- Kotlin 1.6.20.
- Coroutines 1.6.1.

## 0.4.0

### Added

Added new targets:
- `iosSimulatorArm64`
- `macosArm64`
- `tvosArm64` 
- `tvosSimulatorArm64`
- `tvosX64`

### Changed

- Kotlin 1.6.10.
- Stately 1.2.1.
- Target Java 11.

## 0.3.0

### Changed

- Change `fun newBuilder()` to `operator fun invoke()` - [#7](https://github.com/ReactiveCircus/cache4k/pull/7).
- Compile with Kotlin 1.5.21.

## 0.2.0

### Changed

- Support Kotlin 1.5.0.
- Stately to 1.1.7.

## 0.1.1

### Changed

- Disable IR.

## 0.1.0

Initial release.
