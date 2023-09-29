java_library(
    name = "EAnnotationIntrospector",
    srcs = [":EAnnotationIntrospector.java"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore/annotation:EJsonAlias",
        "//src/main/java/com/ottogroup/jackson/ecore/annotation:EJsonIgnore",
        "//src/main/java/com/ottogroup/jackson/ecore/annotation:EJsonRawValue",
        "//src/main/java/com/ottogroup/jackson/ecore/annotation:EJsonTypeIdResolver",
        "//src/main/java/com/ottogroup/jackson/ecore/annotation:EJsonTypeInfo",
        "//src/main/java/com/ottogroup/jackson/ecore/deser:EObjectIdResolver",
        "//src/main/java/com/ottogroup/jackson/ecore/deser:RawDeserializer",
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:AnnotatedFeature",
        "//src/main/java/com/ottogroup/jackson/ecore/ser:EObjectIdGenerator",
        "//src/main/java/com/ottogroup/jackson/ecore/type:EClassType",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EcoreMapper",
    srcs = [":EcoreMapper.java"],
    visibility = ["//visibility:public"],
    deps = [
        ":EcoreModule",
        "//src/main/java/com/ottogroup/jackson/ecore/deser:EDeserializationContext",
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:EClassIntrospector",
        "//src/main/java/com/ottogroup/jackson/ecore/type:ETypeFactory",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EcoreModule",
    srcs = [":EcoreModule.java"],
    visibility = ["//visibility:public"],
    deps = [
        ":EAnnotationIntrospector",
        "//src/main/java/com/ottogroup/jackson/ecore/deser:EDeserializationProblemHandler",
        "//src/main/java/com/ottogroup/jackson/ecore/deser:EDeserializers",
        "//src/main/java/com/ottogroup/jackson/ecore/deser:EInstantiators",
        "//src/main/java/com/ottogroup/jackson/ecore/introspect:EClassIntrospector",
        "//src/main/java/com/ottogroup/jackson/ecore/ser:ESerializerFactory",
        "//src/main/java/com/ottogroup/jackson/ecore/ser:ESerializers",
        "//src/main/java/com/ottogroup/jackson/ecore/type:ETypeFactory",
        "//src/main/java/com/ottogroup/jackson/ecore/type:ETypeResolverBuilder",
        "//src/main/java/com/ottogroup/jackson/ecore/type:URITypeIdResolver",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "JsonResource",
    srcs = [":JsonResource.java"],
    visibility = ["//visibility:public"],
    deps = [
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "JsonResourceFactory",
    srcs = [":JsonResourceFactory.java"],
    visibility = ["//visibility:public"],
    deps = [
        ":EcoreMapper",
        ":JsonResource",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)
