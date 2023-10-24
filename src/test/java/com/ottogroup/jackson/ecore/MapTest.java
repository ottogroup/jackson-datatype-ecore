/*
 * Copyright (c) 2019-2022 Guillaume Hillairet and others.
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
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emfcloud.jackson.junit.model.ETypes;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.ModelPackage;
import org.eclipse.emfcloud.jackson.junit.model.Type;
import org.eclipse.emfcloud.jackson.junit.model.Value;
import org.junit.Before;
import org.junit.Test;

public class MapTest {

  private static ObjectMapper mapper;
  private static ResourceSet resourceSet;

  @Before
  public void setUpOnce() {
    final var packages = new EPackage[] {ModelPackage.eINSTANCE};
    mapper = TestSetup.newMapper(packages);
    resourceSet = TestSetup.newResourceSet(packages);
  }

  @Test
  public void testSaveMap() throws IOException {
    final JsonNode expected = mapper.readTree(Paths.get("testdata/test-map-1.json").toFile());

    final Resource resource = resourceSet.createResource(URI.createURI("tests/test-map-1.json"));

    final ETypes types = ModelFactory.eINSTANCE.createETypes();
    {
      final Type t = ModelFactory.eINSTANCE.createType();
      t.setName("t1");
      final Value v = ModelFactory.eINSTANCE.createValue();
      v.setValue(1);
      types.getValues().put(t, v);
    }
    {
      final Type t = ModelFactory.eINSTANCE.createType();
      t.setName("t2");
      final Value v = ModelFactory.eINSTANCE.createValue();
      v.setValue(2);
      types.getValues().put(t, v);
    }
    resource.getContents().add(types);

    final JsonNode actual = mapper.valueToTree(resource);
    assertEquals(expected, actual);
  }

  @Test
  public void testLoadMap() {
    final Resource resource =
        resourceSet.getResource(URI.createURI("testdata/test-map-1.json"), true);

    assertEquals(1, resource.getContents().size());
    assertTrue(resource.getContents().get(0) instanceof ETypes);

    final ETypes types = (ETypes) resource.getContents().get(0);

    final EMap<Type, Value> values = types.getValues();

    assertEquals(2, values.size());

    Map.Entry<Type, Value> e = values.get(0);

    assertEquals("t1", e.getKey().getName());
    assertEquals(1, e.getValue().getValue());

    e = values.get(1);

    assertEquals("t2", e.getKey().getName());
    assertEquals(2, e.getValue().getValue());
  }

  @Test
  public void testSaveMapWithStringKey() throws IOException {
    final JsonNode expected = mapper.readTree(Paths.get("testdata/test-map-2.json").toFile());

    final Resource resource = resourceSet.createResource(URI.createURI("tests/test-map-2.json"));

    final ETypes types = ModelFactory.eINSTANCE.createETypes();

    final Value v1 = ModelFactory.eINSTANCE.createValue();
    v1.setValue(1);

    final Value v2 = ModelFactory.eINSTANCE.createValue();
    v2.setValue(2);

    types.getStringMapValues().put("Hello", v1);

    types.getStringMapValues().put("World", v2);

    resource.getContents().add(types);

    final JsonNode actual = mapper.valueToTree(resource);
    assertEquals(expected, actual);
  }

  @Test
  public void testLoadMapWithStringKey() {
    final Resource resource =
        resourceSet.getResource(URI.createURI("testdata/test-map-2.json"), true);

    assertEquals(1, resource.getContents().size());
    assertTrue(resource.getContents().get(0) instanceof ETypes);

    final ETypes types = (ETypes) resource.getContents().get(0);

    assertEquals(2, types.getStringMapValues().size());

    final EMap<String, Value> mapValues = types.getStringMapValues();

    assertEquals(1, mapValues.get("Hello").getValue());
    assertEquals(2, mapValues.get("World").getValue());
  }

  @Test
  public void testSaveMapWithDataTypeKey() {
    final JsonNode expected =
        mapper
            .createObjectNode()
            .set("dataTypeMapValues", mapper.createObjectNode().put("test.json", "hello"));

    final ETypes types = ModelFactory.eINSTANCE.createETypes();
    types.getDataTypeMapValues().put("test.json", "hello");

    final JsonNode actual = mapper.valueToTree(types);
    assertEquals(expected, actual);
  }

  @Test
  public void testSaveLoadWithDataTypeKey() throws IOException {
    final JsonNode data =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .set("dataTypeMapValues", mapper.createObjectNode().put("test.json", "hello"));

    final ETypes types = mapper.reader().forType(ETypes.class).readValue(data);

    assertNotNull(types);
    assertEquals("hello", types.getDataTypeMapValues().map().get("test.json"));
  }
}
