# Change Log

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
