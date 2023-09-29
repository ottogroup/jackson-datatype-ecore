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

import static org.eclipse.emfcloud.jackson.junit.model.ModelPackage.Literals.PRIMARY_OBJECT;
import static org.eclipse.emfcloud.jackson.junit.model.ModelPackage.Literals.USER;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.PrimaryObject;
import org.eclipse.emfcloud.jackson.junit.model.Sex;
import org.eclipse.emfcloud.jackson.junit.model.SomeKind;
import org.eclipse.emfcloud.jackson.junit.model.User;
import org.junit.Assert;
import org.junit.Test;

public class EnumTest {
  @Test
  public void testEnums() {
    final JsonNode expected =
        TestSetup.mapper
            .createArrayNode()
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//User"))
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//User")
                    .put("sex", "FEMALE"));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));

    final User u1 = ModelFactory.eINSTANCE.createUser();
    u1.setSex(Sex.MALE);

    final User u2 = ModelFactory.eINSTANCE.createUser();
    u2.setSex(Sex.FEMALE);

    resource.getContents().add(u1);
    resource.getContents().add(u2);

    Assert.assertEquals(expected, TestSetup.mapper.valueToTree(resource));
  }

  @Test
  public void testLoadEnums() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createArrayNode()
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//User")
                    .put("name", "A")
                    .put("sex", "MALE"))
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//User")
                    .put("name", "B")
                    .put("sex", "FEMALE"));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    assertEquals(2, resource.getContents().size());

    assertEquals(USER, resource.getContents().get(0).eClass());
    assertEquals(USER, resource.getContents().get(1).eClass());

    final User u1 = (User) resource.getContents().get(0);
    final User u2 = (User) resource.getContents().get(1);

    assertEquals("A", u1.getName());
    assertEquals(Sex.MALE, u1.getSex());
    assertEquals("B", u2.getName());
    assertEquals(Sex.FEMALE, u2.getSex());
  }

  @Test
  public void testSaveEnumDifferentCases() {
    final JsonNode expected =
        TestSetup.mapper
            .createArrayNode()
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//PrimaryObject")
                    .put("unsettableAttributeWithNonNullDefault", "junit")
                    .put("kind", "one"))
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//PrimaryObject")
                    .put("unsettableAttributeWithNonNullDefault", "junit")
                    .put("kind", "two"))
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//PrimaryObject")
                    .put("unsettableAttributeWithNonNullDefault", "junit")
                    .put("kind", "Three-is-Three"));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    {
      final PrimaryObject p = ModelFactory.eINSTANCE.createPrimaryObject();
      p.setKind(SomeKind.ONE);
      resource.getContents().add(p);
    }
    {
      final PrimaryObject p = ModelFactory.eINSTANCE.createPrimaryObject();
      p.setKind(SomeKind.TWO);
      resource.getContents().add(p);
    }
    {
      final PrimaryObject p = ModelFactory.eINSTANCE.createPrimaryObject();
      p.setKind(SomeKind.THREE);
      resource.getContents().add(p);
    }

    final var reconfiguredMapper = TestSetup.mapper.copy().setSerializationInclusion(Include.NON_EMPTY);
    Assert.assertEquals(expected, reconfiguredMapper.valueToTree(resource));
  }

  @Test
  public void testLoadEnumDifferentCases() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createArrayNode()
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//PrimaryObject")
                    .put("kind", "one"))
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//PrimaryObject")
                    .put("kind", "two"))
            .add(
                TestSetup.mapper
                    .createObjectNode()
                    .put("eClass", "http://www.emfjson.org/jackson/model#//PrimaryObject")
                    .put("kind", "Three-is-Three"));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    assertEquals(3, resource.getContents().size());

    final EObject one = resource.getContents().get(0);
    final EObject two = resource.getContents().get(1);
    final EObject three = resource.getContents().get(2);

    assertEquals(PRIMARY_OBJECT, one.eClass());
    assertEquals(PRIMARY_OBJECT, two.eClass());
    assertEquals(PRIMARY_OBJECT, three.eClass());

    assertEquals(SomeKind.ONE, ((PrimaryObject) one).getKind());
    assertEquals(SomeKind.TWO, ((PrimaryObject) two).getKind());
    assertEquals(SomeKind.THREE, ((PrimaryObject) three).getKind());
  }
}
