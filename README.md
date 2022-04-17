# cache4k

![CI](https://github.com/ReactiveCircus/cache4k/workflows/CI/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.reactivecircus.cache4k/cache4k/badge.svg)](https://search.maven.org/search?q=g:io.github.reactivecircus.cache4k)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

In-memory Cache for Kotlin Multiplatform.

**Work in progress.**

**cache4k** provides a simple in-memory key-value cache for **Kotlin Multiplatform**, with support for time-based (expiration) and size-based evictions.

**Note that only the new Kotlin Native memory model is supported.**

The following targets are currently supported:

- jvm
- js
- iosX64
- iosArm64
- iosSimulatorArm64
- macosX64
- macosArm64
- tvosArm64
- tvosSimulatorArm64
- tvosX64

## Download

Dependencies are hosted on [Maven Central](https://search.maven.org/artifact/io.github.reactivecircus.cache4k/cache4k).

### Android

```kotlin
dependencies {
    implementation("io.github.reactivecircus.cache4k:cache4k:x.y.z")
}
```

### Multiplatform

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.reactivecircus.cache4k:cache4k:x.y.z")
            }
        }
    }
}
```

## Usage

### Writing and reading cache entries

To create a new `Cache` instance using `Long` for the key and `String` for the value:

```kotlin
val cache = Cache.Builder().build<Long, String>()
```

To start writing entries to the cache:

```kotlin
cache.put(1, "dog")
cache.put(2, "cat")
```

To read a cache entry by key:

```kotlin
cache.get(1) // returns "dog"
cache.get(2) // returns "cat"
cache.get(3) // returns null
```

To overwrite an existing cache entry:

```kotlin
cache.put(1, "dog")
cache.put(1, "bird")
cache.get(1) // returns "bird"
```

### Cache loader

**Cache** provides an API for getting cached value by key and using the provided `loader: suspend () -> Value` lambda to compute and cache the value automatically if none exists.

```kotlin
runBlockingTest {
    val cache = Cache.Builder().build<Long, User>()

    val userId = 1L
    val user = cache.get(userId) {
        fetchUserById(userId) // potentially expensive call (might be a suspend function)
    }

    // value successfully computed by the loader will be cached automatically
    assertThat(user).isEqualTo(cache.get(userId))
}
```

Note that loader is executed on the caller's coroutine context. Concurrent calls from multiple threads using the same key will be blocked. Assuming the 1st call successfully computes a new value, none of the loader from the other calls will be executed and the cached value computed by the first loader will be returned for those calls.

Any exceptions thrown by the `loader` will be propagated to the caller of this function.

### Expirations and evictions

By default, **Cache** has an unlimited number of entries which never expire. But a cache can be configured to support both **time-based expirations** and **size-based evictions**.

#### Time-based expiration

Expiration time can be specified for entries in the cache.

##### Expire after access

To set the maximum time an entry can live in the cache since the last access (also known as **
time-to-idle**), where "access" means **reading the cache**, **adding a new cache entry**, or **
replacing an existing entry with a new one**:

```kotlin
val cache = Cache.Builder()
    .expireAfterAccess(24.hours)
    .build<Long, String>()
```

An entry in this cache will be removed if it has not been read or replaced **after 24 hours** since it's been written into the cache.

##### Expire after write

To set the maximum time an entry can live in the cache since the last write (also known as **
time-to-live**), where "write" means **adding a new cache entry** or **replacing an existing entry with a new one**:

```kotlin
val cache = Cache.Builder()
    .expireAfterWrite(30.minutes)
    .build<Long, String>()
```

An entry in this cache will be removed if it has not been replaced **after 30 minutes** since it's been written into the cache.

_Note that cache entries are **not** removed immediately upon expiration at exact time. Expirations are checked in each interaction with the `cache`._

### Size-based eviction

To set the maximum number of entries to be kept in the cache:

```kotlin
val cache = Cache.Builder()
    .maximumCacheSize(100)
    .build<Long, String>()
```

Once there are more than **100** entries in this cache, the **least recently used one** will be removed, where "used" means **reading the cache**, **adding a new cache entry**, or **replacing an
existing entry with a new one**.

### Getting all cache entries as a Map

To get a copy of the current cache entries as a `Map`:

```kotlin
val cache = Cache.Builder()
    .build<Long, String>()

cache.put(1, "dog")
cache.put(2, "cat")

assertThat(cache.asMap())
    .isEqualTo(mapOf(1L to "dog", 2L to "cat"))
```

_Note that calling `asMap()` has no effect on the access expiry of the cache._

### Deleting cache entries

Cache entries can also be deleted explicitly.

To delete a cache entry for a given key:

```kotlin
val cache = Cache.Builder().build<Long, String>()
cache.put(1, "dog")

cache.invalidate(1)

assertThat(cache.get(1)).isNull()
```

To delete all entries in the cache:

```kotlin
cache.invalidateAll()
```

### Unit testing cache expirations

To test logic that depends on cache expiration, pass in a `FakeTimeSource` when building a `Cache`
so you can programmatically advance the reading of the time source:

```kotlin
@Test
fun cacheEntryEvictedAfterExpiration() {
    private val fakeTimeSource = FakeTimeSource()
    val cache = Cache.Builder()
        .fakeTimeSource(fakeTimeSource)
        .expireAfterWrite(1.minutes)
        .build<Long, String>()

    cache.put(1, "dog")

    // just before expiry
    fakeTimeSource += 1.minutes - 1.nanoseconds

    assertThat(cache.get(1))
        .isEqualTo("dog")

    // now expires
    fakeTimeSource += 1.nanoseconds

    assertThat(cache.get(1))
        .isNull()
}
```

## Credits

The library was ported from a kotlin / JVM cache which I contributed to [dropbox/Store](https://github.com/dropbox/Store) to help unblock Store's multiplatform support (
it was reverted before the 1.0 release as multiplatform wasn't a priority). Many thanks to Store's owners and contributors for reviewing and improving the original implementation.

## License

```
Copyright 2021 Yang Chen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
