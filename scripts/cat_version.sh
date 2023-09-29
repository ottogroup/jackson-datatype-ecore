#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

if [[ $# -ne 1 ]]; then
  printf 'Usage: %s FILE\n' "$0" >&2
  exit 1
fi

exec awk 'match($0, /^PROJECT_VERSION = "(.*)"$/, matches) { print matches[1] }' "$1"
