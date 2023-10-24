package com.ottogroup.jackson.ecore;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.ModelPackage;
import org.eclipse.emfcloud.jackson.junit.model.RawStuff;
import org.junit.Before;
import org.junit.Test;

public class RawTest {

  private static ObjectMapper mapper;

  @Before
  public void setUpOnce() {
    mapper = TestSetup.newMapper(ModelPackage.eINSTANCE);
  }

  @Test
  public void testSerialize() throws IOException {
    final var rawStuff = ModelFactory.eINSTANCE.createRawStuff();
    rawStuff.setJson("{\"foo\":42}");

    final var bytes = mapper.writeValueAsBytes(rawStuff);
    final var actual = mapper.readTree(bytes);
    final var expected =
        mapper.createObjectNode().set("json", mapper.createObjectNode().put("foo", 42));

    assertEquals(expected, actual);
  }

  @Test
  public void testDeserialize() {
    final var tree =
        mapper.createObjectNode().set("json", mapper.createObjectNode().put("foo", 42));

    final var rawStuff = mapper.convertValue(tree, RawStuff.class);
    assertEquals("{\"foo\":42}", rawStuff.getJson());
  }
}
