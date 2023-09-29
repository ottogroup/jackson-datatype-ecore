package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "AnnotatedFeature",
    srcs = [":AnnotatedFeature.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EClassDescription",
    srcs = [":EClassDescription.java"],
    deps = [
        ":FeaturePropertyDefinition",
        "//src/main/java/com/ottogroup/jackson/ecore/type:EClassType",
        "//src/main/java/com/ottogroup/jackson/ecore/type:ETypeFactory",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EClassIntrospector",
    srcs = [":EClassIntrospector.java"],
    deps = [
        ":EClassDescription",
        "//src/main/java/com/ottogroup/jackson/ecore/type:EClassType",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_library(
    name = "FeaturePropertyDefinition",
    srcs = [":FeaturePropertyDefinition.java"],
    deps = [
        ":AnnotatedFeature",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)
