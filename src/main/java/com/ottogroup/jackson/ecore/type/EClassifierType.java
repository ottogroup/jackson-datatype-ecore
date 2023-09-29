package com.ottogroup.jackson.ecore.type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeBase;
import java.io.Serial;
import org.eclipse.emf.ecore.EClassifier;

public class EClassifierType extends SimpleType {

  @Serial private static final long serialVersionUID = 1L;

  protected EClassifier eClassifier;

  protected EClassifierType(final EClassifier eClassifier, final TypeBase base) {
    super(
        base.getRawClass(),
        base.getBindings(),
        base.getSuperClass(),
        base.getInterfaces().toArray(new JavaType[0]),
        eClassifier.hashCode(),
        base.getValueHandler(),
        base.getTypeHandler(),
        base.useStaticType());
    this.eClassifier = eClassifier;
  }

  public EClassifier getEClassifier() {
    return eClassifier;
  }

  @Override
  public SimpleType withStaticTyping() {
    return this;
  }

  /*
  /**********************************************************
  /* Standard methods
  /**********************************************************
   */

  @Override
  public String toString() {
    return "[%s, classifier %s]".formatted(getClass().getSimpleName(), eClassifier.getName());
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o)
        && (o instanceof final EClassifierType other)
        && eClassifier.equals(other.eClassifier);
  }
}
