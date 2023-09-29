load("//tools:defs.bzl", "xcore_library")

package(
    default_testonly = True,
    default_visibility = ["//src/test:__subpackages__"],
)

xcore_library(
    name = "array",
    srcs = [":array.xcore"],
    javacopts = [
        "-Xep:ArrayEquals:OFF",
        "-Xep:ArrayToString:OFF",
    ],
    deps = [
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

xcore_library(
    name = "model",
    srcs = [":model.xcore"],
    javacopts = [
        "-Xep:ArrayEquals:OFF",
        "-Xep:ArrayToString:OFF",
    ],
    deps = [
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore_xmi",
    ],
)
