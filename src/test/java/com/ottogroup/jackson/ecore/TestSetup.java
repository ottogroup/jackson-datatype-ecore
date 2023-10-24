package com.ottogroup.jackson.ecore;

import static org.eclipse.emf.ecore.resource.Resource.Factory.Registry.DEFAULT_EXTENSION;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

public class TestSetup {

  private static EPackage.Registry newPackageRegistry(final EPackage... packages) {
    final var packageRegistry = new EPackageRegistryImpl();
    packageRegistry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

    for (final EPackage ePackage : packages) {
      packageRegistry.put(ePackage.getNsURI(), ePackage);
    }

    return packageRegistry;
  }

  static EcoreMapper newMapper(final EPackage... packages) {
    return new EcoreMapper(newPackageRegistry(packages));
  }

  static ResourceSet newResourceSet(final EPackage... packages) {
    final var resourceSet = new ResourceSetImpl();
    final var packageRegistry = newPackageRegistry(packages);
    resourceSet.getPackageRegistry().putAll(packageRegistry);
    final var extensionToFactoryMap =
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();

    // Register the JsonResourceFactory for *.json resources
    final var jsonResourceFactory = new JsonResourceFactory(resourceSet.getPackageRegistry());
    extensionToFactoryMap.put("json", jsonResourceFactory);

    // Register the EcoreResourceFactory as a fallback
    extensionToFactoryMap.put(DEFAULT_EXTENSION, new EcoreResourceFactoryImpl());

    return resourceSet;
  }
}
