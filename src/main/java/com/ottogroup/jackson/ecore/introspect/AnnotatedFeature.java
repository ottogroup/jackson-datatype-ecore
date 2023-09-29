package com.ottogroup.jackson.ecore.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import java.io.Serial;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Objects;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class AnnotatedFeature extends AnnotatedMember {

  @Serial private static final long serialVersionUID = 1L;

  final JavaType type;
  final EStructuralFeature feature;

  public AnnotatedFeature(final JavaType type, final EStructuralFeature feature) {
    super(null, null);
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(feature, "feature");

    this.type = type;
    this.feature = feature;
  }

  public EStructuralFeature getFeature() {
    return feature;
  }

  @Override
  public Annotated withAnnotations(final AnnotationMap fallback) {
    return null;
  }

  @Override
  public Class<?> getDeclaringClass() {
    return feature.getContainerClass();
  }

  @Override
  public Member getMember() {
    return null;
  }

  @Override
  public void setValue(final Object pojo, final Object value)
      throws UnsupportedOperationException, IllegalArgumentException {
    if (pojo instanceof final EObject eObject) {
      eObject.eSet(feature, value);
    }
    throw new IllegalArgumentException("pojo must be an EObject");
  }

  @Override
  public Object getValue(final Object pojo)
      throws UnsupportedOperationException, IllegalArgumentException {
    if (pojo instanceof final EObject eObject) {
      return eObject.eGet(feature);
    }
    throw new IllegalArgumentException("pojo must be an EObject");
  }

  @Override
  public AnnotatedElement getAnnotated() {
    return null;
  }

  @Override
  protected int getModifiers() {
    int value = Modifier.PUBLIC;
    if (feature.isTransient()) {
      value |= Modifier.TRANSIENT;
    }
    if (feature.isVolatile()) {
      value |= Modifier.VOLATILE;
    }

    return value;
  }

  @Override
  public String getName() {
    return feature.getName();
  }

  @Override
  public JavaType getType() {
    return type;
  }

  @Override
  public Class<?> getRawType() {
    return getType().getRawClass();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof final AnnotatedFeature that)) {
      return false;
    }
    return type.equals(that.type) && feature.equals(that.feature);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, feature);
  }

  @Override
  public String toString() {
    return "AnnotatedFeature{" + "type=" + type + ", feature=" + feature + '}';
  }
}
