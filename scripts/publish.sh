#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

print_usage() {
  printf 'Usage: %s local|github\n' "$0" >&2
}

if [[ $# -ne 1 ]]; then
  print_usage
  exit 1
fi

declare -a run_args
case "$1" in
  local)
    run_args+=(--define maven_repo="file://$HOME/.m2/repository")
    ;;
  github)
    run_args+=(
      --define maven_repo="https://maven.pkg.github.com/$GITHUB_REPOSITORY"
      --define maven_user="$GITHUB_ACTOR"
      --define maven_password="$GITHUB_TOKEN"
    )
    ;;
  *)
    print_usage
    exit 1
    ;;
esac

exec bazel run --stamp "${run_args[@]}" //:maven.publish
