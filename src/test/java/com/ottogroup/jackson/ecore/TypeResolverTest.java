package com.ottogroup.jackson.ecore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.eclipse.emfcloud.jackson.junit.model.Event;
import org.eclipse.emfcloud.jackson.junit.model.FooEvent;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.eclipse.emfcloud.jackson.junit.model.ModelPackage;
import org.junit.Before;
import org.junit.Test;

public class TypeResolverTest {

  private static ObjectMapper mapper;

  @Before
  public void setUpOnce() {
    mapper = TestSetup.newMapper(ModelPackage.eINSTANCE);
  }

  @Test
  public void readFooEvent() {
    final var tree = mapper.createObjectNode().put("type", "foo").put("foo", 42);

    final var event = mapper.convertValue(tree, Event.class);
    assertTrue(event instanceof FooEvent);
    final var fooEvent = (FooEvent) event;
    assertEquals(42, fooEvent.getFoo());
  }

  @Test
  public void writeFooEvent() throws IOException {
    final var fooEvent = ModelFactory.eINSTANCE.createFooEvent();
    fooEvent.setType("foo");
    fooEvent.setFoo(42);

    final var expected = mapper.createObjectNode().put("type", "foo").put("foo", 42);

    final var writer = mapper.writerFor(Event.class);
    final var written = writer.writeValueAsBytes(fooEvent);
    final var actual = mapper.readTree(written);
    assertEquals(expected, actual);
  }
}
