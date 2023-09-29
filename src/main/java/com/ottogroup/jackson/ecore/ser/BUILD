package(default_visibility = ["//:__subpackages__"])

java_library(
    name = "DynamicEObjectDeserializer",
    srcs = [":DynamicEObjectSerializer.java"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore/type:ETypeFactory",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EDataTypeSerializer",
    srcs = [":EDataTypeSerializer.java"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore/type:EDataTypeType",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EObjectIdGenerator",
    srcs = [":EObjectIdGenerator.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "ESerializerFactory",
    srcs = [":ESerializerFactory.java"],
    deps = [
        ":FeaturePropertyBuilder",
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:EClassDescription",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_library(
    name = "ESerializers",
    srcs = [":ESerializers.java"],
    deps = [
        ":DynamicEObjectDeserializer",
        ":EDataTypeSerializer",
        ":ResourceSerializer",
        "//src/main/java/com/ottogroup/jackson/ecore/type:EClassType",
        "//src/main/java/com/ottogroup/jackson/ecore/type:EDataTypeType",
        "//src/main/java/com/ottogroup/jackson/ecore/util:EMapToMapConverter",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "FeaturePropertyBuilder",
    srcs = [":FeaturePropertyBuilder.java"],
    deps = [
        ":FeaturePropertyWriter",
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:FeaturePropertyDefinition",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_library(
    name = "FeaturePropertyWriter",
    srcs = [":FeaturePropertyWriter.java"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:FeaturePropertyDefinition",
        "//src/main/java/com/ottogroup/jackson/ecore/type:ETypeFactory",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "ResourceSerializer",
    srcs = [":ResourceSerializer.java"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)
