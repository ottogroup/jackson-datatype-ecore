package(default_testonly = True)

java_test(
    name = "AnnotationTest",
    size = "small",
    srcs = [":AnnotationTest.java"],
    deps = [
        ":TestSetup",
        "//src/test/resources/model",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_test(
    name = "ArrayTest",
    size = "small",
    srcs = [":ArrayTest.java"],
    deps = [
        ":TestSetup",
        "//src/test/resources/model:array",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_test(
    name = "ContainmentTest",
    size = "small",
    srcs = [":ContainmentTest.java"],
    data = [
        "//testdata:test-proxy-5.json",
        "//testdata:test-proxy-5b.json",
        "//testdata:test-proxy-6.json",
        "//testdata:test-proxy-6-target.json",
        "//testdata:test-proxy-7b.json",
    ],
    deps = [
        ":TestSetup",
        "//src/main/java/com/ottogroup/jackson/ecore:JsonResource",
        "//src/test/resources/model",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_test(
    name = "DynamicTest",
    size = "small",
    srcs = [":DynamicTest.java"],
    data = ["//testdata:test.ecore"],
    deps = [
        ":TestSetup",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_core",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore_xmi",
    ],
)

java_test(
    name = "EcoreTest",
    size = "small",
    srcs = [":EcoreTest.java"],
    data = ["//testdata:test.json"],
    deps = [
        ":TestSetup",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_test(
    name = "EnumTest",
    size = "small",
    srcs = [":EnumTest.java"],
    deps = [
        ":TestSetup",
        "//src/test/resources/model",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "EventTypeIdResolver",
    srcs = [":EventTypeIdResolver.java"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore/type:ETypeFactory",
        "//src/test/resources/model",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
    ],
)

java_test(
    name = "MapTest",
    size = "small",
    srcs = [":MapTest.java"],
    data = [
        "//testdata:test-map-1.json",
        "//testdata:test-map-2.json",
    ],
    deps = [
        ":TestSetup",
        "//src/test/resources/model",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_test(
    name = "PolymorphicTest",
    size = "small",
    srcs = [":PolymorphicTest.java"],
    data = ["//testdata:test-load-types.json"],
    deps = [
        ":TestSetup",
        "//src/test/resources/model",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_test(
    name = "RawTest",
    size = "small",
    srcs = [":RawTest.java"],
    deps = [
        ":TestSetup",
        "//src/test/resources/model",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "ReferenceTest",
    size = "small",
    srcs = [":ReferenceTest.java"],
    deps = [
        ":TestSetup",
        "//src/test/resources/model",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_test(
    name = "ResourceTest",
    size = "small",
    srcs = [":ResourceTest.java"],
    data = ["//testdata:test.json"],
    deps = [
        ":TestSetup",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_test(
    name = "TypeResolverTest",
    size = "small",
    srcs = [":TypeResolverTest.java"],
    runtime_deps = [":EventTypeIdResolver"],
    deps = [
        ":TestSetup",
        "//src/test/resources/model",
        "@maven//:junit_junit",
    ],
)

java_test(
    name = "ValueTest",
    size = "small",
    srcs = [":ValueTest.java"],
    deps = [
        ":TestSetup",
        "//src/test/resources/model",
        "@maven//:com_fasterxml_jackson_core_jackson_annotations",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:junit_junit",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
    ],
)

java_library(
    name = "TestSetup",
    srcs = [":TestSetup.java"],
    data = ["//testdata:dynamic.ecore"],
    deps = [
        "//src/main/java/com/ottogroup/jackson/ecore:EcoreMapper",
        "//src/main/java/com/ottogroup/jackson/ecore:JsonResourceFactory",
        "//src/test/resources/model",
        "//src/test/resources/model:array",
        "@maven//:com_fasterxml_jackson_core_jackson_databind",
        "@maven//:org_eclipse_emf_org_eclipse_emf_common",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore",
        "@maven//:org_eclipse_emf_org_eclipse_emf_ecore_xmi",
    ],
)
