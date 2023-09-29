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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.jackson.junit.model.ETypes;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.ModelPackage;
import org.eclipse.emfcloud.jackson.junit.model.Sex;
import org.eclipse.emfcloud.jackson.junit.model.User;
import org.junit.Assert;
import org.junit.Test;

public class ValueTest {

  static class Foo {
    Object id;

    public Foo setId(final Object id) {
      this.id = id;
      return this;
    }

    public Object getId() {
      return id;
    }
  }

  @Test
  public void pojoObject() {
    final var foo = new Foo();
    foo.setId(42);
    final var tree = TestSetup.mapper.valueToTree(foo);
    assertTrue(tree.get("id").isNumber());
  }

  @Test
  public void testOptionSaveDefaultValues() {
    final var includeDefaultsMapper = TestSetup.mapper.copy().setSerializationInclusion(Include.NON_EMPTY);
    {
      final JsonNode expected = TestSetup.mapper.createObjectNode().put("name", "u1");

      final User u = ModelFactory.eINSTANCE.createUser();
      u.setName("u1");

      Assert.assertEquals(expected, TestSetup.mapper.valueToTree(u));
    }

    {
      final JsonNode expected = TestSetup.mapper.createObjectNode().put("name", "u1").put("sex", "FEMALE");

      final User u = ModelFactory.eINSTANCE.createUser();
      u.setName("u1");
      u.setSex(Sex.FEMALE);

      Assert.assertEquals(expected, TestSetup.mapper.valueToTree(u));
    }

    {
      final JsonNode expected = TestSetup.mapper.createObjectNode().put("name", "u1").put("sex", "MALE");

      final User u = ModelFactory.eINSTANCE.createUser();
      u.setName("u1");

      assertEquals(expected, includeDefaultsMapper.valueToTree(u));
    }
    {
      final JsonNode expected = TestSetup.mapper.createObjectNode().put("name", "u1").put("sex", "FEMALE");

      final User u = ModelFactory.eINSTANCE.createUser();
      u.setName("u1");
      u.setSex(Sex.FEMALE);

      assertEquals(expected, includeDefaultsMapper.valueToTree(u));
    }
  }

  @Test
  public void testStringValues() {
    final JsonNode expected =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eString", "Hello")
            .set("eStrings", TestSetup.mapper.createArrayNode().add("Hello").add("The").add("World"));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    final ETypes valueObject = ModelFactory.eINSTANCE.createETypes();
    valueObject.setEString("Hello");

    valueObject.getEStrings().add("Hello");
    valueObject.getEStrings().add("The");
    valueObject.getEStrings().add("World");

    resource.getContents().add(valueObject);

    Assert.assertEquals(expected, TestSetup.mapper.valueToTree(resource));
  }

  @Test
  public void testEmptyString() throws IOException {
    final JsonNode expected = TestSetup.mapper.createObjectNode().put("name", "");

    final var user = ModelFactory.eINSTANCE.createUser();
    user.setName("");

    final var actual = TestSetup.mapper.valueToTree(user);
    assertEquals(expected, actual);

    final var read = TestSetup.mapper.treeToValue(actual, User.class);
    assertEquals("", read.getName());
  }

  @Test
  public void testLoadStringValues() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eString", "Hello")
            .set("eStrings", TestSetup.mapper.createArrayNode().add("Hello").add("The").add("World"));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    final EObject root = resource.getContents().get(0);
    assertEquals(ModelPackage.Literals.ETYPES, root.eClass());

    assertEquals("Hello", ((ETypes) root).getEString());

    assertEquals(List.of("Hello", "The", "World"), ((ETypes) root).getEStrings());
  }

  @Test
  public void testLoadNullValue() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .putNull("eString");

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    final EObject root = resource.getContents().get(0);

    assertEquals(ModelPackage.Literals.ETYPES, root.eClass());
    assertNull(((ETypes) root).getEString());
  }

  @Test
  public void testIntValues() {
    final JsonNode expected =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eInt", 2)
            .set("eInts", TestSetup.mapper.createArrayNode().add(2).add(4).add(7));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));

    final ETypes valueObject = ModelFactory.eINSTANCE.createETypes();
    valueObject.setEInt(2);

    valueObject.getEInts().add(2);
    valueObject.getEInts().add(4);
    valueObject.getEInts().add(7);

    resource.getContents().add(valueObject);

    Assert.assertEquals(expected, TestSetup.mapper.valueToTree(resource));
  }

  @Test
  public void testLoadIntValues() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eInt", 2)
            .set("eInts", TestSetup.mapper.createArrayNode().add(2).add(4).add(7));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    assertEquals(1, resource.getContents().size());

    final EObject root = resource.getContents().get(0);
    assertEquals(ModelPackage.Literals.ETYPES, root.eClass());

    assertEquals(2, ((ETypes) root).getEInt());

    final List<Integer> ints = ((ETypes) root).getEInts();
    assertEquals(3, ints.size());
    assertEquals(2, (int) ints.get(0));
    assertEquals(4, (int) ints.get(1));
    assertEquals(7, (int) ints.get(2));
  }

  @Test
  public void testBooleanValues() {
    final JsonNode expected =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eBoolean", true)
            .set("eBooleans", TestSetup.mapper.createArrayNode().add(false).add(true));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));

    final ETypes valueObject = ModelFactory.eINSTANCE.createETypes();
    valueObject.setEBoolean(true);

    valueObject.getEBooleans().add(false);
    valueObject.getEBooleans().add(true);

    resource.getContents().add(valueObject);

    Assert.assertEquals(expected, TestSetup.mapper.valueToTree(resource));
  }

  @Test
  public void testDateValue() {
    final Calendar calendar = Calendar.getInstance();
    calendar.set(2020, Calendar.OCTOBER, 10);

    assertNotNull(TestSetup.mapper.getDateFormat());

    final JsonNode expected =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put(
                "eDate",
                EcoreUtil.convertToString(EcorePackage.Literals.EDATE, calendar.getTime()));

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    final ETypes valueObject = ModelFactory.eINSTANCE.createETypes();

    final Date value = calendar.getTime();
    valueObject.setEDate(value);

    resource.getContents().add(valueObject);

    Assert.assertEquals(expected, TestSetup.mapper.valueToTree(resource));
  }

  @Test
  @SuppressWarnings("JavaUtilDate")
  public void testLoadDateValue() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eDate", "2011-10-10T00:00:00");

    final Date value =
        (Date) EcoreUtil.createFromString(EcorePackage.eINSTANCE.getEDate(), "2011-10-10T00:00:00");

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    assertEquals(1, resource.getContents().size());

    final ETypes root = (ETypes) resource.getContents().get(0);

    assertEquals(value.getTime(), root.getEDate().getTime());
  }

  @Test
  public void testBigIntegerValue() {
    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));

    final ETypes valueObject = ModelFactory.eINSTANCE.createETypes();
    valueObject.setEBigInteger(new BigInteger("15"));
    resource.getContents().add(valueObject);

    final JsonNode result = TestSetup.mapper.valueToTree(resource);

    assertEquals(new BigInteger("15"), result.get("eBigInteger").bigIntegerValue());
  }

  @Test
  public void testLoadBigIntegerValue() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eBigInteger", 15);

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    assertEquals(1, resource.getContents().size());

    final EObject root = resource.getContents().get(0);
    assertEquals(ModelPackage.Literals.ETYPES, root.eClass());

    final BigInteger value = ((ETypes) root).getEBigInteger();

    assertEquals(new BigInteger("15"), value);
  }

  @Test
  public void testByteValue() {
    final JsonNode expected =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eByte", 101);

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));

    final ETypes valueObject = ModelFactory.eINSTANCE.createETypes();
    final byte b = 101;
    valueObject.setEByte(b);
    resource.getContents().add(valueObject);

    Assert.assertEquals(expected, TestSetup.mapper.valueToTree(resource));
  }

  @Test
  public void testBigDecimalValue() {
    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));

    final ETypes valueObject = ModelFactory.eINSTANCE.createETypes();
    valueObject.setEBigDecimal(new BigDecimal("1.5"));
    resource.getContents().add(valueObject);

    final JsonNode result = TestSetup.mapper.valueToTree(resource);

    assertEquals(new BigDecimal("1.5"), result.get("eBigDecimal").decimalValue());
  }

  @Test
  public void testLoadBigDecimalValue() throws IOException {
    final JsonNode data =
        TestSetup.mapper
            .createObjectNode()
            .put("eClass", "http://www.emfjson.org/jackson/model#//ETypes")
            .put("eBigDecimal", 1.5);

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(TestSetup.mapper.writeValueAsBytes(data)), null);

    assertEquals(1, resource.getContents().size());

    final EObject root = resource.getContents().get(0);
    assertEquals(ModelPackage.Literals.ETYPES, root.eClass());

    final BigDecimal value = ((ETypes) root).getEBigDecimal();

    assertEquals(new BigDecimal("1.5"), value);
  }

  @Test
  public void testLoadObjectArrayValue() throws IOException {
    final String data =
        """
          {
            "eClass": "http://www.emfjson.org/jackson/model#//ETypes",
            "objectArrayType": [\s
              [201707250000, 1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 2, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],\s
              [201707250100, 1, 0, 0, 74, 0, 13, 0, 0, 0, 2, 0, 116, 88, 2, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0],\s
              [201707260000] ]
          }""";

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(data.getBytes(UTF_8)), null);

    final ETypes object = (ETypes) resource.getContents().get(0);
    assertEquals(3, object.getObjectArrayType().length);
  }

  @Test
  public void testLoadObjectTypeValue() throws IOException {
    final String data =
        """
          {
            "eClass": "http://www.emfjson.org/jackson/model#//ETypes",
            "objectType": 1}""";

    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(data.getBytes(UTF_8)), null);

    final ETypes object = (ETypes) resource.getContents().get(0);
    assertEquals(1, object.getObjectType());
  }

  @Test
  public void testSaveObjectTypeValue() {
    final Resource resource = TestSetup.resourceSet.createResource(URI.createURI("tests/test.json"));
    final ETypes object = ModelFactory.eINSTANCE.createETypes();
    object.setObjectType(1);
    resource.getContents().add(object);

    final JsonNode result = TestSetup.mapper.valueToTree(resource);
    assertTrue(result.get("objectType").isNumber());
  }
}
