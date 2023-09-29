package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "EClassifierType",
    srcs = [":EClassifierType.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EClassType",
    srcs = [":EClassType.java"],
    deps = [
        ":EClassifierType",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EDataTypeType",
    srcs = [":EDataTypeType.java"],
    deps = [
        ":EClassifierType",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "ETypeFactory",
    srcs = [":ETypeFactory.java"],
    deps = [
        ":EClassType",
        ":EDataTypeType",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "ETypeResolverBuilder",
    srcs = [":ETypeResolverBuilder.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "URITypeIdResolver",
    srcs = [":URITypeIdResolver.java"],
    deps = [
        ":ETypeFactory",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)
