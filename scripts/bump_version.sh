#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

if [[ $# -ne 2 ]]; then
  printf 'Usage: %s FILE NEW_VERSION\n' "$0" >&2
  exit 1
fi

tmpfile=$(mktemp)
awk -v new_version="$2" '/^PROJECT_VERSION =/ { gsub(/".+"/, "\"" new_version "\"") } ; { print }' "$1" > "$tmpfile"
mv "$tmpfile" "$1"
