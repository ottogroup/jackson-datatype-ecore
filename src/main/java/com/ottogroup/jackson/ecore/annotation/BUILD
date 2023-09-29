package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "EAnnotationBridge",
    srcs = [":EAnnotationBridge.java"],
    deps = [
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EJsonAlias",
    srcs = [":EJsonAlias.java"],
    deps = [
        ":EAnnotationBridge",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EJsonIgnore",
    srcs = [":EJsonIgnore.java"],
    deps = [
        ":EAnnotationBridge",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EJsonRawValue",
    srcs = [":EJsonRawValue.java"],
    deps = [
        ":EAnnotationBridge",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EJsonTypeIdResolver",
    srcs = [":EJsonTypeIdResolver.java"],
    deps = [
        ":EAnnotationBridge",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EJsonTypeInfo",
    srcs = [":EJsonTypeInfo.java"],
    deps = [
        ":EAnnotationBridge",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)
