package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import com.ottogroup.jackson.ecore.introspect.EClassDescription;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/** Responsible for discovery of {@link ValueInstantiator}s based on a given type. */
public class EInstantiators implements ValueInstantiators {

  @Override
  public ValueInstantiator findValueInstantiator(
      final DeserializationConfig config,
      final BeanDescription beanDesc,
      final ValueInstantiator defaultInstantiator) {

    if (beanDesc instanceof final EClassDescription eClassDescription) {
      return new EClassInstantiator(eClassDescription.getType(), eClassDescription.getEClass());
    }

    return defaultInstantiator;
  }

  public static class EClassInstantiator extends ValueInstantiator.Base {

    @Serial private static final long serialVersionUID = 1L;

    final EClass eClass;

    public EClassInstantiator(final JavaType valueType, final EClass eClass) {
      super(valueType);
      this.eClass = eClass;
    }

    @Override
    public boolean canCreateUsingDefault() {
      return !eClass.isInterface() && !eClass.isAbstract();
    }

    @Override
    public boolean canCreateFromString() {
      return canCreateUsingDefault();
    }

    @Override
    public EObject createUsingDefault(final DeserializationContext ctxt) {
      return EcoreUtil.create(eClass);
    }

    @Override
    public EObject createFromString(final DeserializationContext ctxt, final String value)
        throws IOException {
      final URI uri;
      try {
        uri = URI.createURI(value);
      } catch (final IllegalArgumentException e) {
        return ctxt.reportInputMismatch(_valueType, "Invalid proxy URI: %s", value);
      }
      return createFromURI(ctxt, uri);
    }

    public EObject createFromURI(final DeserializationContext ctxt, final URI uri) {
      final var object = createUsingDefault(ctxt);
      ((InternalEObject) object).eSetProxyURI(uri);
      return object;
    }
  }

  public static class EListInstantiator extends ValueInstantiator.Base implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    public static final EListInstantiator INSTANCE = new EListInstantiator();

    public EListInstantiator() {
      super(BasicEList.class);
    }

    @Override
    public boolean canCreateUsingDefault() {
      return true;
    }

    @Override
    public BasicEList<?> createUsingDefault(final DeserializationContext ctxt) {
      return new BasicEList<>();
    }
  }
}
