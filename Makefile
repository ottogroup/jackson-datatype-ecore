SHELL := /usr/bin/env bash

.DEFAULT_TARGET: build
.PHONY: build
build:
	bazel build //...

.PHONY: lint
lint:
	bazel run //:java_fmt -- --dry-run --set-exit-if-changed @<(find $$PWD/src $$PWD/tools -type f -name *.java)

.PHONY: fmt
fmt:
	bazel run //:java_fmt -- --replace @<(find $$PWD/src $$PWD/tools -type f -name *.java)

.PHONY: test
test:
	bazel test //...

.PHONY: deploy-artifacts
deploy-artifacts:
	scripts/publish.sh github

.PHONY: release
release:
	scripts/release.sh
	git push --follow-tags --atomic

.PHONY: ci
ci: lint test

.PHONY: cd
cd: deploy-artifacts

.PHONY: outdated
outdated:
	bazel run @maven//:outdated

.PHONY: pin
pin:
	bazel run @unpinned_maven//:pin
