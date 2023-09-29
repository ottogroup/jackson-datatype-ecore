package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * A {@link DeserializationProblemHandler} that adds a warning to the {@link Resource} stored in an
 * attribute {@link DeserializationContext} when an unknown property is encountered.
 */
public class EDeserializationProblemHandler extends DeserializationProblemHandler {

  @Override
  public boolean handleUnknownProperty(
      final DeserializationContext ctxt,
      final JsonParser p,
      final JsonDeserializer<?> deserializer,
      final Object beanOrClass,
      final String propertyName) {
    if (beanOrClass instanceof final EObject eObject) {
      // Ignore type hint property
      final var typePropertyName = getTypePropertyName(ctxt.getConfig());
      if (propertyName.equals(typePropertyName)) {
        return true;
      }

      final var resourceAttr = ctxt.getAttribute(Resource.class);
      if (resourceAttr instanceof final Resource resource) {
        final var currentLocation = p.getCurrentLocation();
        final var diagnostic =
            new UnknownFeatureDiagnostic(
                propertyName,
                eObject.eClass(),
                resource.getURI().toString(),
                currentLocation.getLineNr(),
                currentLocation.getColumnNr());
        return resource.getWarnings().add(diagnostic);
      }
    }

    return false;
  }

  protected static String getTypePropertyName(final DeserializationConfig config) {
    final var baseType = config.constructType(EObject.class);
    final var typeResolver = config.getDefaultTyper(baseType);
    if (typeResolver instanceof final StdTypeResolverBuilder stdTypeResolverBuilder) {
      return stdTypeResolverBuilder.getTypeProperty();
    }
    return null;
  }
}
