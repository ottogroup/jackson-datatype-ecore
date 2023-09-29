package com.ottogroup.jackson.ecore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.eclipse.emfcloud.jackson.junit.model.Event;
import org.eclipse.emfcloud.jackson.junit.model.FooEvent;
import org.eclipse.emfcloud.jackson.junit.model.ModelFactory;
import org.junit.Test;

public class TypeResolverTest {

  @Test
  public void readFooEvent() {
    final var tree = TestSetup.mapper.createObjectNode().put("type", "foo").put("foo", 42);

    final var event = TestSetup.mapper.convertValue(tree, Event.class);
    assertTrue(event instanceof FooEvent);
    final var fooEvent = (FooEvent) event;
    assertEquals(42, fooEvent.getFoo());
  }

  @Test
  public void writeFooEvent() throws IOException {
    final var fooEvent = ModelFactory.eINSTANCE.createFooEvent();
    fooEvent.setType("foo");
    fooEvent.setFoo(42);

    final var expected = TestSetup.mapper.createObjectNode().put("type", "foo").put("foo", 42);

    final var writer = TestSetup.mapper.writerFor(Event.class);
    final var written = writer.writeValueAsBytes(fooEvent);
    final var actual = TestSetup.mapper.readTree(written);
    assertEquals(expected, actual);
  }
}
