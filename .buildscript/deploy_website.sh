#!/usr/bin/env bash

set -euo pipefail

REPO="git@github.com:ReactiveCircus/cache4k.git"
DIR=temp-clone
OUTPUT_DIR="$(pwd)/site"

if [ -n "${CI:-}" ]; then
  REPO="https://github.com/${GITHUB_REPOSITORY}.git"
fi

# Clone project into a temp directory
rm -rf "$DIR" "$OUTPUT_DIR"
git clone "$REPO" "$DIR"
cd "$DIR"

# Generate API docs
./gradlew :dokkaGenerate

# Copy *.md files into docs directory
cp README.md docs/index.md
cp CHANGELOG.md docs/changelog.md

# Build the website for the GitHub Pages artifact
mkdocs build --site-dir "$OUTPUT_DIR"

# Delete temp directory
cd ..
rm -rf "$DIR"
