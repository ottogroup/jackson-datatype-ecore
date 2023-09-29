load("@maven//:defs.bzl", "maven_artifacts")
load("@rules_jvm_external//:defs.bzl", "artifact")
load("@rules_jvm_external//:specs.bzl", "parse")

exports_files(["defs.bzl"])

# Depends on the full transitive clojure of artifacts.
# This causes all dependencies to be listed in IntelliJ's external libraries, which is useful
# for debugging and browsing the libraries' code
java_binary(
    name = "Dummy",
    srcs = [":Dummy.java"],
    main_class = "tools.Dummy",
    deps = [
        artifact(jar_coord)
        for jar_coord in maven_artifacts
        if parse.parse_maven_coordinate(jar_coord).get("packaging", "jar") == "jar" and
           "classifier" not in parse.parse_maven_coordinate(jar_coord)
    ],
)

java_library(
    name = "VanillaXcoreGenerator",
    srcs = ["VanillaXcoreGenerator.java"],
    deps = [
        "@maven//:org_eclipse_emf_org_eclipse_emf_codegen_ecore",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore_xcore",
        "@maven//:org_eclipse_xtext_org_eclipse_xtext",
    ],
)

java_binary(
    name = "XcoreJavaGenerator",
    srcs = [":XcoreJavaGenerator.java"],
    main_class = "tools.XcoreJavaGenerator",
    visibility = ["//visibility:public"],
    deps = [
        ":VanillaXcoreGenerator",
        "@maven//:jakarta_inject_jakarta_inject_api",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore_xcore",
        "@maven//:org_eclipse_xtext_org_eclipse_xtext",
        "@maven//:org_fusesource_jansi_jansi",
    ],
)
