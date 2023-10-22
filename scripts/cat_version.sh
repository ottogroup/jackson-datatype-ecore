#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

if [[ $# -ne 1 ]]; then
  printf 'Usage: %s FILE\n' "$0" >&2
  exit 1
fi

REGEX='^PROJECT_VERSION = "(.+)"$'
readonly REGEX

while read -r line; do
  if [[ "$line" =~ $REGEX ]]; then
    printf '%s\n' "${BASH_REMATCH[1]}"
    exit 0
  fi
done < "$1"

exit 1
