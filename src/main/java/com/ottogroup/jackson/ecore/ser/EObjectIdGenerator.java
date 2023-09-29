package com.ottogroup.jackson.ecore.ser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.databind.DatabindContext;
import java.io.Serial;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class EObjectIdGenerator extends ObjectIdGenerator<URI> {

  @Serial private static final long serialVersionUID = 1L;

  public static final String REF_FIELD_NAME = "$ref";

  protected final Class<?> scope;
  protected final DatabindContext context;

  public EObjectIdGenerator() {
    this(EObject.class, null);
  }

  public EObjectIdGenerator(final Class<?> scope, final DatabindContext context) {
    this.scope = scope;
    this.context = context;
  }

  /*
  /**********************************************************
  /* Accessors
  /**********************************************************
   */

  @Override
  public Class<?> getScope() {
    return scope;
  }

  @Override
  public boolean canUseFor(final ObjectIdGenerator<?> gen) {
    return gen instanceof final EObjectIdGenerator other && scope.equals(other.scope);
  }

  @Override
  public boolean maySerializeAsObject() {
    return true;
  }

  @Override
  public boolean isValidReferencePropertyName(final String name, final Object parserObj) {
    return REF_FIELD_NAME.equals(name);
  }

  /*
  /**********************************************************
  /* Factory methods
  /**********************************************************
   */

  @Override
  public EObjectIdGenerator forScope(final Class<?> scope) {
    return new EObjectIdGenerator(scope, context);
  }

  @Override
  public EObjectIdGenerator newForSerialization(final Object context) {
    return new EObjectIdGenerator(scope, (DatabindContext) context);
  }

  @Override
  public IdKey key(final Object key) {
    if (key == null) return null;
    return new IdKey(getClass(), getScope(), key);
  }

  /*
  /**********************************************************
  /* Methods for serialization
  /**********************************************************
   */

  @Override
  public URI generateId(final Object forPojo) {
    if (!(forPojo instanceof final EObject eObject)) {
      throw new IllegalArgumentException("Only EObjects are supported, got: " + forPojo.getClass());
    }

    final var resourceAttr = context.getAttribute(Resource.class);
    if (resourceAttr instanceof final Resource resource && resource == eObject.eResource()) {
      final var fragment = resource.getURIFragment(eObject);
      if (!"/-1".equals(fragment)) {
        return URI.createHierarchicalURI(null, null, fragment);
      }
    }

    return EcoreUtil.getURI(eObject);
  }
}
