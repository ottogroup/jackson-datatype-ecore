# jackson-datatype-ecore

[Jackson](http://jackson.codehaus.org/) module to support JSON serialization and deserialization
of [Eclipse Ecore](https://wiki.eclipse.org/Ecore) data types.

## Usage

### Using the `Resource` API

```java
// Using the global package registry
var resourceSet = new ResourceSetImpl();
var factory = new JsonResourceFactory(resourceSet.getPackageRegistry());
resourceSet
    .getResourceFactoryRegistry()
    .getExtensionToFactoryMap()
    .put("json",factory);

// Reading
var resource = resourceSet.getResource(URI.createURI("some/file.json"), true);
var head = resource.getContents().get(0);

// Writing
EObject obj = ...
resource.getContents().add(obj);
resource.save(null);
```

### Using the `ObjectMapper` API

```java
// Using the global package registry
var mapper = new EcoreMapper();

EObject obj = mapper.readValue(new File("some/file.json"));
String json = mapper.writeValueAsString(obj);
```

## Limitations

* Containment proxies will never be serialized as a reference but as the actual object instead

## Development

This project is built with [Bazel](https://bazel.build).

You can use the [Bazel plugin for IntelliJ](https://ij.bazel.build/).
After opening the project in IntelliJ, you can import the default settings configured
in `.bazelproject` by putting this line in your `<WORKSPACE>/.ijwb/.bazelproject` file:

```
import .bazelproject
```

After syncing, the dependent libraries should be synced. Furthermore, the test cases should show up
as run configurations. 

Please have a look at the [`Makefile`](./Makefile) for some common tasks like formatting and testing
the code.

### Managing dependencies

External dependencies are configured using [bzlmod](https://bazel.build/external/overview#bzlmod).
External _Maven_ dependencies are managed
using [rules_jvm_external](https://github.com/bazelbuild/rules_jvm_external). You can find the
configuration in [`MODULE.bazel`](./MODULE.bazel).

When updating Maven dependencies, make sure to update the lock file using `make pin` for the changes
to take effect.

Outdated Maven dependencies can be listed using `make outdated`. 
