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
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.Sex;
import org.eclipse.emfcloud.jackson.junit.model.User;
import org.junit.Test;

public class ReferenceTest {

  @Test
  public void testWrite() throws IOException {
    final var max = ModelFactory.eINSTANCE.createUser();
    max.setUserId("__MAX");
    max.setName("Max");
    max.setSex(Sex.MALE);

    final var erika = ModelFactory.eINSTANCE.createUser();
    erika.setUserId("__ERIKA");
    erika.setName("Erika");
    erika.setSex(Sex.FEMALE);
    erika.setUniqueFriend(max);

    final var resource = TestSetup.resourceSet.createResource(URI.createURI("tests/proxy.json"));
    resource.getContents().add(max);
    resource.getContents().add(erika);

    final var outputStream = new ByteArrayOutputStream();
    resource.save(outputStream, null);

    final var tree = TestSetup.mapper.readTree(outputStream.toByteArray());
    assertTrue(tree instanceof ArrayNode);

    final var contents = (ArrayNode) tree;
    assertEquals(2, contents.size());
    final var erikaNode = (ObjectNode) contents.get(1);
    final var erikasUniqueFriend = erikaNode.required("uniqueFriend");
    assertEquals(erikasUniqueFriend, new TextNode("#__MAX"));
  }

  @Test
  public void testReadBackRef() throws IOException {
    final var maxNode =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .put("userId", "__MAX")
            .put("name", "Max");

    final var erikaNode =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .put("userId", "__ERIKA")
            .put("name", "Erika")
            .put("uniqueFriend", "#__MAX");

    final var tree = TestSetup.mapper.createArrayNode().add(maxNode).add(erikaNode);
    final byte[] bytes = TestSetup.mapper.writeValueAsBytes(tree);

    final var resource = TestSetup.resourceSet.createResource(URI.createURI("tests/testRead.json"));
    resource.load(new ByteArrayInputStream(bytes), null);

    assertEquals(2, resource.getContents().size());

    final var max = (User) resource.getContents().get(0);
    assertEquals("Max", max.getName());

    final var erika = (User) resource.getContents().get(1);
    assertEquals("Erika", erika.getName());
    assertEquals(max, erika.getUniqueFriend());
  }

  @Test
  public void testReadForwardRef() throws IOException {
    final var maxNode =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .put("userId", "__MAX")
            .put("name", "Max")
            .put("uniqueFriend", "#__ERIKA");

    final var erikaNode =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .put("userId", "__ERIKA")
            .put("name", "Erika")
            .put("uniqueFriend", "#__MAX");

    final var tree = TestSetup.mapper.createArrayNode().add(maxNode).add(erikaNode);
    final byte[] bytes = TestSetup.mapper.writeValueAsBytes(tree);

    final var resource = TestSetup.resourceSet.createResource(URI.createURI("tests/testRead.json"));
    resource.load(new ByteArrayInputStream(bytes), null);

    assertEquals(2, resource.getContents().size());

    final var max = (User) resource.getContents().get(0);
    assertEquals("Max", max.getName());

    final var erika = (User) resource.getContents().get(1);
    assertEquals("Erika", erika.getName());
    assertEquals(max, erika.getUniqueFriend());

    assertEquals(erika, max.getUniqueFriend());
  }

  @Test
  public void testWriteForeignResource() throws IOException {

    final var max = ModelFactory.eINSTANCE.createUser();
    max.setUserId("__MAX");
    max.setName("Max");
    max.setSex(Sex.MALE);

    final var erika = ModelFactory.eINSTANCE.createUser();
    erika.setUserId("__ERIKA");
    erika.setName("Erika");
    erika.setSex(Sex.FEMALE);
    erika.setUniqueFriend(max);

    final var maxResource = TestSetup.resourceSet.createResource(URI.createURI("tests/max.json"));
    maxResource.getContents().add(max);
    final var erikaResource = TestSetup.resourceSet.createResource(URI.createURI("tests/erika.json"));
    erikaResource.getContents().add(erika);

    final var outputStream = new ByteArrayOutputStream();
    erikaResource.save(outputStream, null);

    final var tree = TestSetup.mapper.readTree(outputStream.toByteArray());
    assertTrue(tree instanceof ObjectNode);

    final var erikaNode = (ObjectNode) tree;
    final var erikasUniqueFriend = erikaNode.required("uniqueFriend");
    assertEquals(erikasUniqueFriend, new TextNode("tests/max.json#__MAX"));
  }

  @Test
  public void testReadForeignResource() throws IOException {
    final var max = ModelFactory.eINSTANCE.createUser();
    max.setUserId("__MAX");
    max.setName("Max");
    max.setSex(Sex.MALE);
    final var maxResource =
        TestSetup.resourceSet.createResource(URI.createURI("testReadForeignResource/max.json"));
    maxResource.getContents().add(max);

    final var erikaNode =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .put("userId", "__ERIKA")
            .put("name", "Erika")
            .put("uniqueFriend", "testReadForeignResource/max.json#__MAX");

    final byte[] bytes = TestSetup.mapper.writeValueAsBytes(erikaNode);

    final var resource =
        TestSetup.resourceSet.createResource(URI.createURI("testReadForeignResource/erika.json"));
    resource.load(new ByteArrayInputStream(bytes), null);
    assertEquals(1, resource.getContents().size());

    final var erika = (User) resource.getContents().get(0);
    assertEquals("Erika", erika.getName());

    // uniqueFriend has resolveProxies to false, resolve manually
    final var uniqueFriend = EcoreUtil.resolve(erika.getUniqueFriend(), TestSetup.resourceSet);
    assertEquals(max, uniqueFriend);
  }

  @Test
  public void testReadListOfReferences() throws IOException {
    final var maxNode =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .put("userId", "__MAX")
            .put("name", "Max")
            .set("friends", TestSetup.mapper.createArrayNode().add("#__ERIKA"));

    final var erikaNode =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//User")
            .put("userId", "__ERIKA")
            .put("name", "Erika")
            .set("friends", TestSetup.mapper.createArrayNode().add("#__MAX"));

    final var tree = TestSetup.mapper.createArrayNode().add(maxNode).add(erikaNode);
    final byte[] bytes = TestSetup.mapper.writeValueAsBytes(tree);

    final var resource =
        TestSetup.resourceSet.createResource(URI.createURI("tests/testReadListOfReferences.json"));
    resource.load(new ByteArrayInputStream(bytes), null);

    assertEquals(2, resource.getContents().size());

    final var max = (User) resource.getContents().get(0);
    assertEquals("Max", max.getName());
    assertEquals(1, max.getFriends().size());

    final var erika = (User) resource.getContents().get(1);
    assertEquals("Erika", erika.getName());
    assertEquals(1, erika.getFriends().size());
    assertEquals(max, erika.getFriends().get(0));

    assertEquals(erika, max.getFriends().get(0));
  }
}
