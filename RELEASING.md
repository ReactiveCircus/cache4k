# Releasing

1. Change the version in top-level `gradle.properties` to a non-SNAPSHOT version.
2. Update the `CHANGELOG.md` for the impending release.
3. Update the `README.md` with the new version.
4. `git commit -am "Prepare for release X.Y.Z."` (where X.Y.Z is the new version).
5. `./gradlew publishAndReleaseToMavenCentral`
6. `git tag -a X.Y.X -m "X.Y.Z"` (where X.Y.Z is the new version)
7. Update the top-level `gradle.properties` to the next SNAPSHOT version.
8. `git commit -am "Prepare next development version."`
9. `git push && git push --tags`
