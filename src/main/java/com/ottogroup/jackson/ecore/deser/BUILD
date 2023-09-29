package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "EDataTypeDeserializer",
    srcs = [":EDataTypeDeserializer.java"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore/type:EDataTypeType",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EDeserializationContext",
    srcs = [":EDeserializationContext.java"],
    deps = [
        ":EDeserializerFactory",
        ":ReadableEObjectId",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_library(
    name = "EDeserializationProblemHandler",
    srcs = [":EDeserializationProblemHandler.java"],
    deps = [
        ":UnknownFeatureDiagnostic",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EDeserializerFactory",
    srcs = [":EDeserializerFactory.java"],
    deps = [
        ":EObjectDeserializer",
        ":SettableFeatureProperty",
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:EClassDescription",
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:FeaturePropertyDefinition",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_library(
    name = "EDeserializers",
    srcs = [":EDeserializers.java"],
    deps = [
        ":EDataTypeDeserializer",
        ":EInstantiators",
        ":EMapDeserializer",
        ":ResourceDeserializer",
        "//src/main/java/com/ottogroup/jackson/ecore/type:EDataTypeType",
        "//src/main/java/com/ottogroup/jackson/ecore/util:StringToURIConverter",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EInstantiators",
    srcs = [":EInstantiators.java"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:EClassDescription",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EMapDeserializer",
    srcs = [":EMapDeserializer.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
    ],
)

java_library(
    name = "EObjectDeserializer",
    srcs = [":EObjectDeserializer.java"],
    deps = [
        ":EObjectIdResolver",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_library(
    name = "EObjectIdResolver",
    srcs = [":EObjectIdResolver.java"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore/deser:EInstantiators",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "RawDeserializer",
    srcs = [":RawDeserializer.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_library(
    name = "ReadableEObjectId",
    srcs = [":ReadableEObjectId.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_library(
    name = "ResourceDeserializer",
    srcs = [":ResourceDeserializer.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "SettableFeatureProperty",
    srcs = [":SettableFeatureProperty.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "UnknownFeatureDiagnostic",
    srcs = [":UnknownFeatureDiagnostic.java"],
    deps = [
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)
