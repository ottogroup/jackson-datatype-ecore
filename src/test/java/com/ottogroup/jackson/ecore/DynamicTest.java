/*
 * Copyright (c) 2019-2021 Guillaume Hillairet and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 */

package com.ottogroup.jackson.ecore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.Assert;
import org.junit.Test;

public class DynamicTest {
  @Test
  public void testWriteByteArrayJSON() throws IOException {
    final var modelResource =
        TestSetup.resourceSet.getResource(URI.createURI("testdata/test.ecore"), true);
    final var ePackage = (EPackage) modelResource.getContents().get(0);
    final var eClass = (EClass) ePackage.getEClassifier("Test");
    final var eObject = EcoreUtil.create(eClass);
    final var bytes = new byte[] {0, 1, 2, 3};
    final var feature = (EAttribute) eClass.getEStructuralFeature("blob");
    eObject.eSet(feature, bytes);

    final var blobResource = TestSetup.resourceSet.createResource(URI.createURI("tests/blob.json"));
    blobResource.getContents().add(eObject);
    final var out = new ByteArrayOutputStream();
    blobResource.save(out, null);

    final String jsonStr = out.toString(StandardCharsets.UTF_8);
    assertTrue(
        "Base-64 string not found: " + jsonStr,
        jsonStr.contains(Base64.getEncoder().encodeToString(bytes)));
  }

  @Test
  public void testReadDynamic() throws IOException {
    // given
    final var nameAttr = EcoreFactory.eINSTANCE.createEAttribute();
    nameAttr.setName("name");
    nameAttr.setEType(EcorePackage.Literals.ESTRING);

    final var eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Foo");
    eClass.getEStructuralFeatures().add(nameAttr);

    final EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setNsURI("http://foo.org/p");
    ePackage.setName("p");

    ePackage.getEClassifiers().add(eClass);
    new EcoreResourceFactoryImpl()
        .createResource(URI.createURI(ePackage.getNsURI()))
        .getContents()
        .add(ePackage);

    TestSetup.resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

    // when
    final var tree =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://foo.org/p#//Foo")
            .put("name", "Max Mustermann");
    final var object =
        TestSetup.mapper
            .readerFor(EObject.class)
            .withAttribute(ResourceSet.class, TestSetup.resourceSet)
            .<EObject>readValue(tree);

    // then
    final var name = object.eGet(nameAttr);
    assertEquals("Max Mustermann", name);
  }

  @Test
  public void testSaveContainmentWithOpposite() throws IOException {
    final JsonNode expected =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://emfjson/dynamic/model#//A")
            .set("containB", TestSetup.mapper.createObjectNode().put("intValue", 42));

    final EClass classA =
        (EClass)
            TestSetup.resourceSet.getEObject(
                URI.createURI("http://emfjson/dynamic/model#//A"), true);
    final EClass classB =
        (EClass)
            TestSetup.resourceSet.getEObject(
                URI.createURI("http://emfjson/dynamic/model#//B"), true);

    final EObject a1 = EcoreUtil.create(classA);
    final EObject b1 = EcoreUtil.create(classB);

    b1.eSet(classB.getEStructuralFeature("parent"), a1);
    b1.eSet(classB.getEStructuralFeature("intValue"), 42);

    final var writer = TestSetup.mapper.writerFor(EObject.class);
    final var bytes = writer.writeValueAsBytes(a1);
    final var actual = TestSetup.mapper.readTree(bytes);
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testLoadContainmentWithOpposite() throws JsonProcessingException {
    final JsonNode data =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://emfjson/dynamic/model#//A")
            .put("someKind", "e1")
            .set(
                "containB",
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://emfjson/dynamic/model#//B")
                    .put("someKind", "e1"));

    final EObject a1 =
        TestSetup.mapper
            .reader()
            .withAttribute(ResourceSet.class, TestSetup.resourceSet)
            .treeToValue(data, EObject.class);

    final EObject b1 = (EObject) a1.eGet(a1.eClass().getEStructuralFeature("containB"));

    assertNotNull(b1);
    assertSame(a1, b1.eGet(b1.eClass().getEStructuralFeature("parent")));
  }

  @Test
  public void testSaveDynamicEnum() throws IOException {
    final JsonNode expected =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://emfjson/dynamic/model#//A")
            .put("intValue", 0)
            .put("someKind", "e1");

    final EClass a =
        (EClass)
            TestSetup.resourceSet.getEObject(
                URI.createURI("http://emfjson/dynamic/model#//A"), true);
    final EObject a1 = EcoreUtil.create(a);

    final var writer =
        TestSetup.mapper
            .copy()
            .setSerializationInclusion(Include.NON_EMPTY)
            .writerFor(EObject.class);
    final var bytes = writer.writeValueAsBytes(a1);
    final var actual = TestSetup.mapper.readTree(bytes);

    assertEquals(expected, actual);
  }

  @Test
  public void testLoadDynamicEnum() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://emfjson/dynamic/model#//A")
            .put("someKind", "E2");

    final Resource resource =
        TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    assertEquals(1, resource.getContents().size());

    final EObject root = resource.getContents().get(0);

    assertEquals("A", root.eClass().getName());

    final Object literal = root.eGet(root.eClass().getEStructuralFeature("someKind"));

    assertTrue(literal instanceof EEnumLiteral);

    assertEquals("e2", ((EEnumLiteral) literal).getName());
    assertEquals("E2", ((EEnumLiteral) literal).getLiteral());
  }
}
