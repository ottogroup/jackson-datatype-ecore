package com.ottogroup.jackson.ecore;

import static com.ottogroup.jackson.ecore.TestSetup.mapper;
import static org.eclipse.emf.ecore.util.EcoreUtil.GEN_MODEL_ANNOTATION_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.junit.Assert;
import org.junit.Test;

public class EcoreTest {

  @Test
  public void attribute() throws IOException {
    final var json =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EAttribute")
            .put("name", "count");

    final var value = mapper.readValue(json.traverse(), EObject.class);
    assertNotNull("Deserialized value is null", value);

    if (!(value instanceof final EAttribute attribute)) {
      Assert.fail("Expected EAttribute, got %s".formatted(value.getClass()));
      return;
    }

    assertEquals("count", attribute.getName());
  }

  @Test(expected = UnrecognizedPropertyException.class)
  public void unknownFeature() throws IOException {
    final var json =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EAttribute")
            .put("name", "count")
            .put("foo", "bar");

    final var value =
        mapper
            .reader()
            .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue(json.traverse(), EObject.class);
    assertNotNull("Deserialized value is null", value);

    if (!(value instanceof final EAttribute attribute)) {
      Assert.fail("Expected EAttribute, got %s".formatted(value.getClass()));
      return;
    }

    assertEquals("count", attribute.getName());
  }

  @Test
  public void ignoreUnknownFeature() throws IOException {
    final var json =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EAttribute")
            .put("name", "count")
            .put("foo", "bar");

    final var value =
        mapper
            .reader()
            .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue(json.traverse(), EObject.class);
    assertNotNull("Deserialized value is null", value);

    if (!(value instanceof final EAttribute attribute)) {
      Assert.fail("Expected EAttribute, got %s".formatted(value.getClass()));
      return;
    }

    assertEquals("count", attribute.getName());
  }

  @Test
  public void eClass() throws IOException {
    final var attrJson =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EAttribute")
            .put("name", "count");
    final var classJson =
        mapper
            .createObjectNode()
            .put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EClass")
            .put("name", "Foo");
    classJson.putArray("eStructuralFeatures").add(attrJson);

    final var value = mapper.readValue(classJson.traverse(), EObject.class);
    assertNotNull("Deserialized value is null", value);

    if (!(value instanceof final EClass eClass)) {
      Assert.fail("Expected EClass, got %s".formatted(value.getClass()));
      return;
    }

    assertEquals("Foo", eClass.getName());
    assertEquals(1, eClass.getEAttributes().size());
    final var attr = eClass.getEAttributes().get(0);
    assertEquals("count", attr.getName());
  }

  @Test
  public void readTestJson() throws IOException {
    final var jsonFile = new File("testdata/test.json");
    final var ePackage =
        mapper
            .reader()
            .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue(jsonFile, EPackage.class);

    assertEquals("test", ePackage.getName());

    assertEquals(1, ePackage.getEAnnotations().size());
    final var genModelAnnotation = ePackage.getEAnnotations().get(0);
    assertEquals(GEN_MODEL_ANNOTATION_URI, genModelAnnotation.getSource());
    final var details = genModelAnnotation.getDetails();
    assertEquals("false", details.get("bundleManifest"));
    assertEquals("11.0", details.get("complianceLevel"));
    assertEquals("Otto Group Test", details.get("modelName"));
    assertEquals("false", details.get("suppressGenModelAnnotations"));
    assertEquals("com.ottogroup.test", details.get("basePackage"));

    assertEquals(1, ePackage.getEClassifiers().size());
    final var testClass = (EClass) ePackage.getEClassifiers().get(0);
    assertEquals("Test", testClass.getName());

    assertEquals(1, testClass.getEStructuralFeatures().size());
    assertEquals(1, testClass.getEAttributes().size());
    final var fooAttr = testClass.getEAttributes().get(0);
    assertEquals("foo", fooAttr.getName());
  }

  @Test
  public void writeTestJson() throws IOException {
    final var jsonFile = new File("testdata/test.json");
    final var ePackage =
        mapper
            .reader()
            .without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue(jsonFile, EPackage.class);
    final var tree = mapper.valueToTree(ePackage);

    final var annotationsNode = (ArrayNode) tree.required("eAnnotations");
    final var genModelAnnNode = annotationsNode.get(0);
    assertEquals(GEN_MODEL_ANNOTATION_URI, genModelAnnNode.required("source").textValue());
    final var detailsNode = (ObjectNode) genModelAnnNode.required("details");
    assertEquals("Otto Group Test", detailsNode.required("modelName").textValue());

    final var classifiersNode = tree.required("eClassifiers");
    assertTrue(classifiersNode.isArray());
    final var classifiers = (ArrayNode) classifiersNode;
    assertEquals(ePackage.getEClassifiers().size(), classifiers.size());

    final var testClass = (EClass) ePackage.getEClassifier("Test");
    final var testClassNode = classifiers.get(0);
    assertEquals(testClass.getName(), testClassNode.required("name").textValue());

    final var testFeaturesNode = (ArrayNode) testClassNode.required("eStructuralFeatures");
    assertEquals(testClass.getEStructuralFeatures().size(), testFeaturesNode.size());

    final var testFooAttrNode = testFeaturesNode.get(0);
    assertEquals("foo", testFooAttrNode.required("name").textValue());
  }
}
