package com.ottogroup.jackson.ecore;

import static org.eclipse.emf.ecore.resource.Resource.Factory.Registry.DEFAULT_EXTENSION;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emfcloud.jackson.junit.array.ArrayPackage;
import org.eclipse.emfcloud.jackson.junit.model.ModelPackage;

public class TestSetup {

  static EcoreMapper mapper;
  static ResourceSet resourceSet;

  static {
    resourceSet = new ResourceSetImpl();
    resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
    resourceSet.getPackageRegistry().put(ArrayPackage.eNS_URI, ArrayPackage.eINSTANCE);
    resourceSet.getPackageRegistry().put(ModelPackage.eNS_URI, ModelPackage.eINSTANCE);

    final var uriMap = resourceSet.getURIConverter().getURIMap();
    uriMap.put(
        URI.createURI("http://emfjson/dynamic/model"), URI.createURI("testdata/dynamic.ecore"));

    mapper = new EcoreMapper(resourceSet.getPackageRegistry());
    final var jsonResourceFactory = new JsonResourceFactory(mapper);
    final var extensionToFactoryMap =
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    extensionToFactoryMap.put("json", jsonResourceFactory);
    extensionToFactoryMap.put(DEFAULT_EXTENSION, new EcoreResourceFactoryImpl());
  }
}
