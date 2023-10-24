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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.jackson.junit.model.Address;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.ModelPackage;
import org.eclipse.emfcloud.jackson.junit.model.Node;
import org.eclipse.emfcloud.jackson.junit.model.PrimaryObject;
import org.eclipse.emfcloud.jackson.junit.model.Sex;
import org.eclipse.emfcloud.jackson.junit.model.TargetObject;
import org.eclipse.emfcloud.jackson.junit.model.User;
import org.eclipse.emfcloud.jackson.junit.model.impl.PhysicalNodeImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ContainmentTest {

  private static ObjectMapper mapper;
  private static ResourceSet resourceSet;

  @Before
  public void setUpOnce() {
    final var packages = new EPackage[] {ModelPackage.eINSTANCE};
    mapper = TestSetup.newMapper(packages);
    resourceSet = TestSetup.newResourceSet(packages);
  }

  @Test
  public void testSaveOneRootObjectWithAttributes() {
    final JsonNode expected =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .put("userId", "1")
            .put("name", "John");

    final User user = ModelFactory.eINSTANCE.createUser();
    user.setUserId("1");
    user.setName("John");

    final Resource resource = resourceSet.createResource(URI.createURI("tests/test-save-1.json"));
    assertNotNull(resource);

    resource.getContents().add(user);

    Assert.assertEquals(expected, mapper.valueToTree(resource));
  }

  @Test
  public void testSaveTwoRootObjectsWithAttributesNoReferences() {
    final JsonNode expected =
        mapper
            .createArrayNode()
            .add(
                mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//User")
                    .put("userId", "1")
                    .put("name", "John"))
            .add(
                mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//User")
                    .put("userId", "2")
                    .put("name", "Mary")
                    .put("sex", "FEMALE"));

    final User user1 = ModelFactory.eINSTANCE.createUser();
    user1.setUserId("1");
    user1.setName("John");

    final User user2 = ModelFactory.eINSTANCE.createUser();
    user2.setUserId("2");
    user2.setName("Mary");
    user2.setSex(Sex.FEMALE);

    final Resource resource = resourceSet.createResource(URI.createURI("tests/test-save-2.json"));
    resource.getContents().add(user1);
    resource.getContents().add(user2);

    Assert.assertEquals(expected, mapper.valueToTree(resource));
  }

  @Test
  public void testSaveOneObjectWithOneChild() {
    final JsonNode expected =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .set("address", mapper.createObjectNode());

    final Resource resource = resourceSet.createResource(URI.createURI("test"));

    final User user = ModelFactory.eINSTANCE.createUser();
    final Address address = ModelFactory.eINSTANCE.createAddress();
    user.setAddress(address);
    resource.getContents().add(user);

    Assert.assertEquals(expected, mapper.valueToTree(resource));
  }

  @Test
  public void testLoadOneObjectWithOneChild() throws IOException {
    final JsonNode data =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .set(
                "address",
                mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//Address"));

    final var resource =
        (JsonResource)
            resourceSet.createResource(URI.createURI("testLoadOneObjectWithOneChild.json"));
    resource.load(data, null);

    assertNotNull(resource);
    assertEquals(1, resource.getContents().size());

    final User user = (User) resource.getContents().get(0);

    assertNotNull(user.getAddress());
  }

  @Test
  public void testSaveOneObjectWithManyChildren() {
    final JsonNode expected =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//Node")
            .set(
                "child",
                mapper
                    .createArrayNode()
                    .add(mapper.createObjectNode())
                    .add(mapper.createObjectNode())
                    .add(mapper.createObjectNode()));

    final Resource resource =
        resourceSet.createResource(URI.createURI("testSaveOneObjectWithManyChildren.json"));
    final Node root = ModelFactory.eINSTANCE.createNode();
    root.getChild().add(ModelFactory.eINSTANCE.createNode());
    root.getChild().add(ModelFactory.eINSTANCE.createNode());
    root.getChild().add(ModelFactory.eINSTANCE.createNode());
    resource.getContents().add(root);

    Assert.assertEquals(expected, mapper.valueToTree(resource));
  }

  @Test
  public void testLoadOneObjectWithManyChildren() throws IOException {
    final JsonNode data =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//Node")
            .set(
                "child",
                mapper
                    .createArrayNode()
                    .add(mapper.createObjectNode())
                    .add(mapper.createObjectNode())
                    .add(mapper.createObjectNode()));

    final var resource =
        (JsonResource)
            resourceSet.createResource(URI.createURI("testLoadOneObjectWithManyChildren.json"));
    resource.load(data, null);

    assertNotNull(resource);
    assertEquals(1, resource.getContents().size());

    final Node node = (Node) resource.getContents().get(0);

    assertEquals(3, node.getChild().size());
  }

  @Test
  public void testLoadProxyRootContainmentWithOppositeReference() {
    final Resource resource =
        resourceSet.getResource(URI.createURI("testdata/test-proxy-6.json"), true);

    assertEquals(1, resource.getContents().size());
    assertTrue(resource.getContents().get(0) instanceof PrimaryObject);

    final PrimaryObject source = (PrimaryObject) resource.getContents().get(0);

    assertEquals("TheSource", source.getName());
    assertTrue(
        source.eIsSet(ModelPackage.Literals.PRIMARY_OBJECT__SINGLE_CONTAINMENT_REFERENCE_PROXIES));

    final TargetObject target = source.getSingleContainmentReferenceProxies();
    assertFalse(target.eIsProxy());

    assertSame(source, target.getSingleReferenceNotResolveProxies());
    assertSame(source, target.eContainer());
    assertSame(
        ModelPackage.Literals.PRIMARY_OBJECT__SINGLE_CONTAINMENT_REFERENCE_PROXIES,
        target.eContainingFeature());
    assertNotSame(source.eResource(), target.eResource());
  }

  @Test
  public void testLoadResolvingProxyContainment() {
    final Resource resource =
        resourceSet.getResource(URI.createURI("testdata/test-proxy-5b.json"), true);

    assertFalse(resource.getContents().isEmpty());
    assertEquals(1, resource.getContents().size());

    final Node root = (Node) resource.getContents().get(0);

    assertEquals("2", root.getLabel());
    assertEquals(1, root.getChild().size());

    final Node child = root.getChild().get(0);
    // Proxy is resolved because GenModel.ContainmentProxy is true
    assertFalse(child.eIsProxy());

    assertNotSame(root.eResource(), child.eResource());
    assertEquals("1", child.getLabel());
  }

  @Test
  public void testLoadResolvingProxyContainmentWithAbstract() {
    final Resource resource =
        resourceSet.getResource(URI.createURI("testdata/test-proxy-7b.json"), true);

    assertFalse(resource.getContents().isEmpty());
    assertEquals(1, resource.getContents().size());

    final PhysicalNodeImpl root = (PhysicalNodeImpl) resource.getContents().get(0);
    assertEquals("2", root.getLabel());

    final var siblings = root.getSiblings();
    assertEquals(1, siblings.size());
    final var sibling = siblings.get(0);
    assertNotNull(sibling);

    assertTrue(sibling.eIsProxy());
    assertNotSame(root.eResource(), sibling.eResource());

    final EObject resolve = EcoreUtil.resolve(sibling, resourceSet);
    assertNotSame(resolve.eResource(), root.eResource());
  }
}
