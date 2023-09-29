package com.ottogroup.jackson.ecore;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.RawStuff;
import org.junit.Test;

public class RawTest {

  @Test
  public void testSerialize() throws IOException {
    final var rawStuff = ModelFactory.eINSTANCE.createRawStuff();
    rawStuff.setJson("{\"foo\":42}");

    final var bytes = TestSetup.mapper.writeValueAsBytes(rawStuff);
    final var actual = TestSetup.mapper.readTree(bytes);
    final var expected =
        TestSetup.mapper
            .createObjectNode()
            .set("json", TestSetup.mapper.createObjectNode().put("foo", 42));

    assertEquals(expected, actual);
  }

  @Test
  public void testDeserialize() {
    final var tree =
        TestSetup.mapper
            .createObjectNode()
            .set("json", TestSetup.mapper.createObjectNode().put("foo", 42));

    final var rawStuff = TestSetup.mapper.convertValue(tree, RawStuff.class);
    assertEquals("{\"foo\":42}", rawStuff.getJson());
  }
}
