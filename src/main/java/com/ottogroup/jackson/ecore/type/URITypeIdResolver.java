package com.ottogroup.jackson.ecore.type;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import java.io.IOException;
import java.util.Optional;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class URITypeIdResolver extends TypeIdResolverBase {

  final ETypeFactory typeFactory;

  public URITypeIdResolver(final JavaType baseType, final ETypeFactory typeFactory) {
    super(baseType, typeFactory);
    this.typeFactory = typeFactory;
  }

  @Override
  public String idFromValue(final Object value) {
    if (value instanceof final EObject eObject) {
      final EClass eClass = eObject.eClass();
      final var eClassURI = EcoreUtil.getURI(eClass);
      return eClassURI.toString();
    }

    return null;
  }

  @Override
  public String idFromValueAndType(final Object value, final Class<?> suggestedType) {
    return idFromValue(value);
  }

  @Override
  public JavaType typeFromId(final DatabindContext context, final String id) throws IOException {
    // This method is called during deserialization only, so this cast should be safe
    final var dCtxt = (DeserializationContext) context;
    final var resourceSet =
        Optional.ofNullable((ResourceSet) context.getAttribute(ResourceSet.class))
            .orElseGet(ResourceSetImpl::new);

    final URI uri;
    try {
      uri = URI.createURI(id);
    } catch (final IllegalArgumentException e) {
      throw dCtxt.invalidTypeIdException(_baseType, id, e.getMessage());
    }

    final var eObject = resourceSet.getEObject(uri, true);
    if (eObject == null) return null;
    if (!(eObject instanceof final EClass eClass)) {
      throw dCtxt.invalidTypeIdException(
          _baseType, id, "Expected EClass, got " + eObject.eClass().getName());
    }

    return typeFactory.constructEClassType(eClass);
  }

  @Override
  public JsonTypeInfo.Id getMechanism() {
    return JsonTypeInfo.Id.CUSTOM;
  }
}
