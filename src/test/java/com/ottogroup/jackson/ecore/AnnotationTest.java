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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.ModelPackage;
import org.eclipse.emfcloud.jackson.junit.model.User;
import org.junit.Before;
import org.junit.Test;

public class AnnotationTest {

  private static ObjectMapper mapper;
  private static ResourceSet resourceSet;

  @Before
  public void setUpOnce() {
    final var packages = new EPackage[] {ModelPackage.eINSTANCE};
    mapper = TestSetup.newMapper(packages);
    resourceSet = TestSetup.newResourceSet(packages);
  }

  @Test
  public void testSaveAnnotation() {
    final JsonNode expected =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EClass")
            .<ObjectNode>set(
                "eAnnotations",
                mapper
                    .createArrayNode()
                    .add(
                        mapper
                            .createObjectNode()
                            .put("source", "source")
                            .set("details", mapper.createObjectNode().put("displayName", "value"))))
            .put("name", "Foo");

    final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Foo");

    final EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
    eAnnotation.setEModelElement(eClass);
    eAnnotation.setSource("source");
    eAnnotation.getDetails().put("displayName", "value");
    eClass.getEAnnotations().add(eAnnotation);

    final Resource resource = resourceSet.createResource(URI.createURI("test.json"));
    resource.getContents().add(eClass);

    final JsonNode result = mapper.valueToTree(resource);

    assertEquals(expected, result);
  }

  @Test
  public void testLoadAnnotation() throws IOException {
    final JsonNode data =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EClass")
            .put("name", "Foo")
            .set(
                "eAnnotations",
                mapper
                    .createArrayNode()
                    .add(
                        mapper
                            .createObjectNode()
                            .put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EAnnotation")
                            .put("source", "source")
                            .set(
                                "details", mapper.createObjectNode().put("displayName", "value"))));

    final Resource resource = resourceSet.createResource(URI.createURI("test.json"));
    resource.load(new ByteArrayInputStream(mapper.writeValueAsBytes(data)), null);

    assertEquals(1, resource.getContents().size());

    final EObject root = resource.getContents().get(0);

    assertEquals(EcorePackage.Literals.ECLASS, root.eClass());

    final EList<EAnnotation> annotations = ((EClass) root).getEAnnotations();

    assertEquals(1, annotations.size());

    final EAnnotation annotation = annotations.get(0);
    assertEquals(1, annotation.getDetails().size());
    assertEquals("displayName", annotation.getDetails().get(0).getKey());
    assertEquals("value", annotation.getDetails().get(0).getValue());
  }

  @Test
  public void testSnakeCaseNaming() {
    final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName("Foo");

    final EAnnotation eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
    eAnnotation.setEModelElement(eClass);
    eAnnotation.setSource("source");
    eAnnotation.getDetails().put("displayName", "value");
    eClass.getEAnnotations().add(eAnnotation);

    final var actual =
        mapper
            .copy()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .valueToTree(eClass);
    final JsonNode expected =
        mapper
            .createObjectNode()
            .<ObjectNode>set(
                "e_annotations",
                mapper
                    .createArrayNode()
                    .add(
                        mapper
                            .createObjectNode()
                            .put("source", "source")
                            .set("details", mapper.createObjectNode().put("displayName", "value"))))
            .put("name", "Foo");

    assertEquals(expected, actual);
  }

  @Test
  public void jsonAliasUserId() throws IOException {
    final String id = "4711";
    final String name = "Max Mustermann";
    final var object = mapper.createObjectNode().put("id", id).put("name", name);
    final var user = mapper.readValue(object.traverse(), User.class);
    assertEquals(id, user.getUserId());
    assertEquals(name, user.getName());
  }

  @Test
  public void jsonAliasUserUId() throws IOException {
    final String id = "4711";
    final String name = "Max Mustermann";
    final var object = mapper.createObjectNode().put("uid", id).put("name", name);
    final var user = mapper.readValue(object.traverse(), User.class);
    assertEquals(id, user.getUserId());
    assertEquals(name, user.getName());
  }

  @Test
  public void ignored() {
    final var user = ModelFactory.eINSTANCE.createUser();
    user.setIgnored(42);
    final var tree = mapper.valueToTree(user);
    assertFalse("The ignored field should not be serialized", tree.has("ignored"));
  }
}
