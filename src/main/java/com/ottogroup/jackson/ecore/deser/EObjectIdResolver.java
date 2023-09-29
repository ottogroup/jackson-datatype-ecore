package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.ottogroup.jackson.ecore.deser.EInstantiators.EClassInstantiator;
import java.io.IOException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

public class EObjectIdResolver implements ObjectIdResolver {

  public static final Object RESOLVED_TYPE_ATTR = new Object();

  protected final DeserializationContext context;

  public EObjectIdResolver() {
    this(null);
  }

  public EObjectIdResolver(final DeserializationContext context) {
    this.context = context;
  }

  @Override
  public void bindItem(final IdKey id, final Object pojo) {}

  @Override
  public EObject resolveId(final IdKey id) {
    EObject result = null;
    if (!(id.key instanceof final URI uri)) {
      throw new IllegalArgumentException("Expected URI key, got " + id.key.getClass());
    }

    final var resourceAttr = context.getAttribute(Resource.class);
    if (resourceAttr instanceof final Resource resource
        && (uri.trimFragment().isEmpty() || resource.getURI().equals(uri.trimFragment()))
        && uri.hasFragment()) {
      result = resource.getEObject(uri.fragment());
    }

    if (result == null && !uri.trimFragment().isEmpty()) {
      try {
        final var beanDesc = context.getConfig().introspect(getActualType(id));
        final var valueInstantiator = context.getFactory().findValueInstantiator(context, beanDesc);
        result = ((EClassInstantiator) valueInstantiator).createFromURI(context, uri);
      } catch (final IOException e) {
        throw new RuntimeException("Error creating proxy for: " + id.scope, e);
      }
    }

    return result;
  }

  @Override
  public EObjectIdResolver newForDeserialization(final Object context) {
    return new EObjectIdResolver((DeserializationContext) context);
  }

  @Override
  public boolean canUseFor(final ObjectIdResolver resolverType) {
    return resolverType instanceof EObjectIdResolver;
  }

  protected JavaType getActualType(final IdKey id) {
    final var typeAttr = context.getAttribute(RESOLVED_TYPE_ATTR);
    if (typeAttr instanceof final JavaType eClassType) {
      return eClassType;
    } else {
      return context.constructType(id.scope);
    }
  }
}
