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

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emfcloud.jackson.junit.model.AbstractType;
import org.eclipse.emfcloud.jackson.junit.model.ConcreteTypeOne;
import org.eclipse.emfcloud.jackson.junit.model.ConcreteTypeTwo;
import org.eclipse.emfcloud.jackson.junit.model.Container;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.junit.Assert;
import org.junit.Test;

public class PolymorphicTest {

  @Test
  public void testSaveTwoObjectsWithTypeInformation() {
    final JsonNode expected =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//Container")
            .set(
                "elements",
                TestSetup.mapper
                    .createArrayNode()
                    .add(
                        TestSetup.mapper
                            .createObjectNode()
                            .put("eClass", "http://www.emfjson.org/jackson/model#//ConcreteTypeOne")
                            .put("name", "First"))
                    .add(
                        TestSetup.mapper
                            .createObjectNode()
                            .put("eClass", "http://www.emfjson.org/jackson/model#//ConcreteTypeTwo")
                            .put("name", "Two")));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/types.json"));

    final Container c = ModelFactory.eINSTANCE.createContainer();
    final ConcreteTypeOne one = ModelFactory.eINSTANCE.createConcreteTypeOne();
    one.setName("First");
    final ConcreteTypeTwo two = ModelFactory.eINSTANCE.createConcreteTypeTwo();
    two.setName("Two");
    c.getElements().add(one);
    c.getElements().add(two);
    resource.getContents().add(c);

    Assert.assertEquals(expected, TestSetup.mapper.valueToTree(resource));
  }

  @Test
  public void testLoadTwoObjectsWithTypeInformation() throws IOException {
    final Resource resource =
        TestSetup.resourceSet.createResource(URI.createURI("testdata/test-load-types.json"));
    final Map<Object, Object> options = new HashMap<>();
    resource.load(options);

    assertEquals(1, resource.getContents().size());

    final EObject root = resource.getContents().get(0);
    assertTrue(root instanceof Container);

    final Container container = (Container) root;

    assertEquals(2, container.getElements().size());
    final AbstractType first = container.getElements().get(0);
    final AbstractType second = container.getElements().get(1);

    assertTrue(first instanceof ConcreteTypeOne);
    assertTrue(second instanceof ConcreteTypeTwo);

    assertEquals("First", first.getName());
    assertEquals("one", ((ConcreteTypeOne) first).getPropTypeOne());
    assertEquals("Two", second.getName());
    assertEquals("two", ((ConcreteTypeTwo) second).getPropTypeTwo());
  }
}
