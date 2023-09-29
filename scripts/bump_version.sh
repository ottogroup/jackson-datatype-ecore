#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

if [[ $# -ne 2 ]]; then
  printf 'Usage: %s FILE NEW_VERSION\n' "$0" >&2
  exit 1
fi

exec sed --in-place "/^PROJECT_VERSION =/c\\PROJECT_VERSION = \"$2\"" "$1"
