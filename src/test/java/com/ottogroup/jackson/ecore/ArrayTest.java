/*
 * Copyright (c) 2022 Data In Motion Consulting GmbH and others.
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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emfcloud.jackson.junit.array.ArrayFactory;
import org.eclipse.emfcloud.jackson.junit.array.ArrayHost;
import org.eclipse.emfcloud.jackson.junit.array.ArrayPackage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ArrayTest {

  private static ObjectMapper mapper;
  private static ResourceSet resourceSet;

  @Before
  public void setUpOnce() {
    final var packages = new EPackage[] {ArrayPackage.eINSTANCE};
    mapper = TestSetup.newMapper(packages);
    resourceSet = TestSetup.newResourceSet(packages);
  }

  @Test
  public void testByteArray() {
    final ArrayHost u = ArrayFactory.eINSTANCE.createArrayHost();
    final var bytes = new byte[] {1, 2};
    u.setB(bytes);

    final ObjectNode expected = mapper.createObjectNode();
    expected.put("b", bytes);

    Assert.assertEquals(expected, mapper.valueToTree(u));
  }

  @Test
  public void test1DArray() {
    final ArrayHost u = ArrayFactory.eINSTANCE.createArrayHost();
    u.setD1(new Double[] {1.1, 1.2});

    final ObjectNode expected = mapper.createObjectNode();
    final ArrayNode a = expected.putArray("d1");
    a.add(1.1).add(1.2);

    Assert.assertEquals(expected, mapper.valueToTree(u));
  }

  @Test
  public void test2DArray() {
    final ArrayHost u = ArrayFactory.eINSTANCE.createArrayHost();
    u.setD2(new Double[][] {{1.1, 1.2}, {2.1, 2.2}});

    final ObjectNode expected = mapper.createObjectNode();
    final ArrayNode a = expected.putArray("d2");
    a.addArray().add(1.1).add(1.2);
    a.addArray().add(2.1).add(2.2);

    Assert.assertEquals(expected, mapper.valueToTree(u));
  }

  @Test
  public void test3DArray() {
    final ArrayHost u = ArrayFactory.eINSTANCE.createArrayHost();
    u.setD3(new Double[][][] {{{1.11, 1.12}, {1.21, 1.22}}, {{2.11, 2.12}, {2.21, 2.22}}});

    final ObjectNode expected = mapper.createObjectNode();
    final ArrayNode a = expected.putArray("d3");
    final ArrayNode a1 = a.addArray();
    a1.addArray().add(1.11).add(1.12);
    a1.addArray().add(1.21).add(1.22);
    final ArrayNode a2 = a.addArray();
    a2.addArray().add(2.11).add(2.12);
    a2.addArray().add(2.21).add(2.22);

    Assert.assertEquals(expected, mapper.valueToTree(u));
  }

  @Test
  public void test2DString() {
    final ArrayHost u = ArrayFactory.eINSTANCE.createArrayHost();
    u.setS2(new String[][] {{"1.1", "1.2"}, {"2.1", "2.2"}});

    final ObjectNode expected = mapper.createObjectNode();
    final ArrayNode a = expected.putArray("s2");
    a.addArray().add("1.1").add("1.2");
    a.addArray().add("2.1").add("2.2");

    Assert.assertEquals(expected, mapper.valueToTree(u));
  }

  @Test
  public void testLoad2DDoubleArrayValues() throws IOException {
    final String data =
        """
            {
              "eClass": "http://www.emfjson.org/jackson/array#//ArrayHost",
              "d2": [\s
                [1.1, 1.2],\s
                [2.1, 2.2] ]
            }""";

    final Resource resource = resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(data.getBytes(UTF_8)), null);

    final ArrayHost host = (ArrayHost) resource.getContents().get(0);
    final Double[][] d2 = host.getD2();
    assertEquals(2, d2.length);
    assertArrayEquals(new Double[] {1.1, 1.2}, d2[0]);
    assertArrayEquals(new Double[] {2.1, 2.2}, d2[1]);
  }

  @Test
  public void testLoad2DStringArrayValues() throws IOException {
    final String data =
        """
            {
              "eClass": "http://www.emfjson.org/jackson/array#//ArrayHost",
              "s2": [\s
                ["1.1", "1.2"],\s
                ["2.1", "2.2"] ]
            }""";

    final Resource resource = resourceSet.createResource(URI.createURI("tests/test.json"));
    resource.load(new ByteArrayInputStream(data.getBytes(UTF_8)), null);

    final ArrayHost host = (ArrayHost) resource.getContents().get(0);
    final String[][] s2 = host.getS2();
    assertEquals(2, s2.length);
    assertArrayEquals(new String[] {"1.1", "1.2"}, s2[0]);
    assertArrayEquals(new String[] {"2.1", "2.2"}, s2[1]);
  }
}
