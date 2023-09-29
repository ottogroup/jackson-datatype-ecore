def _xcore_java_src(ctx):
    args = ctx.actions.args()

    compile_deps = depset(transitive = [
        dep[JavaInfo].transitive_runtime_jars
        for dep in ctx.attr.deps
    ])
    args.add_joined(
        compile_deps,
        format_joined = "--main_advice_classpath=%s",
        join_with = ctx.configuration.host_path_separator,
    )

    jar_file = ctx.actions.declare_file(ctx.label.name)
    args.add(jar_file.path)
    args.add_all(ctx.files.srcs)

    ctx.actions.run(
        inputs = depset(ctx.files.srcs, transitive = [compile_deps]),
        outputs = [jar_file],
        executable = ctx.executable._xcore_java_generator,
        arguments = [args],
    )

    return [DefaultInfo(files = depset([jar_file]))]

xcore_java_src = rule(
    implementation = _xcore_java_src,
    attrs = {
        "srcs": attr.label_list(
            allow_empty = False,
            allow_files = [".xcore"],
            mandatory = True,
        ),
        "deps": attr.label_list(
            providers = [JavaInfo],
        ),
        "_xcore_java_generator": attr.label(
            executable = True,
            cfg = "exec",
            default = Label("//tools:XcoreJavaGenerator"),
        ),
    },
)

def xcore_library(name, srcs, deps = [], resources = [], **kwargs):
    srcjar = name + ".srcjar"
    xcore_java_src(
        name = srcjar,
        srcs = srcs,
        deps = deps,
    )

    native.java_library(
        name = name,
        srcs = [srcjar],
        resources = srcs + resources,
        deps = deps,
        **kwargs
    )
