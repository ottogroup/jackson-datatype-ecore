package com.ottogroup.jackson.ecore;

import static org.eclipse.emf.ecore.EcorePackage.Literals.EPACKAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;

public class ResourceTest {
  @Test
  public void readResource() {
    final var uri = URI.createURI("testdata/test.json");
    final var resource = TestSetup.resourceSet.getResource(uri, true);

    final var contents = resource.getContents();
    assertEquals(1, contents.size());
    final var head = contents.get(0);
    assertTrue(head instanceof EPackage);
  }

  @Test
  public void writeResource() throws IOException {
    final var uri = URI.createURI("testdata/test.json");
    final var resource = TestSetup.resourceSet.getResource(uri, true);
    assertFalse(resource.getContents().isEmpty());

    final var out = new ByteArrayOutputStream();
    resource.save(out, null);

    final var tree = TestSetup.mapper.readTree(out.toByteArray());
    assertEquals(EcoreUtil.getURI(EPACKAGE).toString(), tree.get("eClass").textValue());
  }
}
