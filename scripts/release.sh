#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"/..

declare release_version proposed_release_version dev_version proposed_dev_version \
  current_version major minor patch

current_version=$(scripts/cat_version.sh BUILD)
readonly current_version
printf 'The current version is: %s\n' "$current_version"

# Propose to release the current version without the '-SNAPSHOT' suffix
if [[ "${current_version: -9}" == '-SNAPSHOT' ]]; then
  proposed_release_version=${current_version:0:-9}
  readonly proposed_release_version
else
  proposed_release_version=
fi

read -er -p "What should the RELEASE version be? [$proposed_release_version] " release_version
if [[ -z "$release_version" ]]; then
  if [[ -n "$proposed_release_version" ]]; then
    release_version=$proposed_release_version
  else
    exit 1
  fi
fi
readonly release_version


# Propose to use next patch version for SNAPSHOT
if [[ "$release_version" =~ ([[:digit:]]+)\.([[:digit:]]+)\.([[:digit:]]+) ]]; then
  major=${BASH_REMATCH[1]}
  minor=${BASH_REMATCH[2]}
  patch=${BASH_REMATCH[3]}
  proposed_dev_version="$major.$minor.$((patch + 1))-SNAPSHOT"
  readonly major minor patch proposed_dev_version
else
  proposed_dev_version=
fi

read -er -p "What should the next DEVELOPMENT version be? [$proposed_dev_version] " dev_version
if [[ -z "$dev_version" ]]; then
  if [[ -n "$proposed_dev_version" ]]; then
    dev_version=$proposed_dev_version
  else
    exit 1
  fi
fi
readonly dev_version

# Prepare release
scripts/bump_version.sh BUILD "$release_version"
git add BUILD
release_msg="Prepare release v$release_version"
git commit --message="$release_msg"
git tag --annotate --message="$release_msg" "v$release_version"

# Prepare for next development
scripts/bump_version.sh BUILD "$dev_version"
git add BUILD
git commit --message="Prepare development of $dev_version"
