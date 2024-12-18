#!/bin/bash

set -e

REPO="git@github.com:ReactiveCircus/cache4k.git"
REMOTE_NAME="origin"
DIR=temp-clone

if [ -n "${CI}" ]; then
  REPO="https://github.com/${GITHUB_REPOSITORY}.git"
fi

# Clone project into a temp directory
rm -rf $DIR
git clone "$REPO" $DIR
cd $DIR

# Generate API docs
./gradlew :dokkaGenerate

# Copy *.md files into docs directory
cp README.md docs/index.md
cp CHANGELOG.md docs/changelog.md

# If on CI, configure git remote with access token
if [ -n "${CI}" ]; then
  REMOTE_NAME="https://x-access-token:${DEPLOY_TOKEN}@github.com/${GITHUB_REPOSITORY}.git"
  git config --global user.name "${GITHUB_ACTOR}"
  git config --global user.email "${GITHUB_ACTOR}@users.noreply.github.com"
  git remote add deploy "$REMOTE_NAME"
  git fetch deploy && git fetch deploy gh-pages:gh-pages
fi

# Build the website and deploy to GitHub Pages
mkdocs gh-deploy --remote-name "$REMOTE_NAME"

# Delete temp directory
cd ..
rm -rf $DIR
