load("@rules_jvm_external//:defs.bzl", "java_export")

PROJECT_VERSION = "1.0.0"

java_export(
    name = "maven",
    maven_coordinates = "com.ottogroup:jackson-datatype-ecore:%s" % PROJECT_VERSION,
    pom_template = ":pom_template.xml",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "//src/main/java/com/ottogroup/jackson/ecore:EcoreMapper",
        "//src/main/java/com/ottogroup/jackson/ecore:JsonResourceFactory",
    ],
)

java_binary(
    name = "java_fmt",
    jvm_flags = [
        # Extra flags according to https://github.com/google/google-java-format/#intellij-jre-config
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    ],
    main_class = "com.google.googlejavaformat.java.Main",
    runtime_deps = ["@maven//:com_google_googlejavaformat_google_java_format"],
)
