package com.ottogroup.jackson.ecore.introspect;

import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.eclipse.emf.ecore.EStructuralFeature;

public class FeaturePropertyDefinition extends BeanPropertyDefinition {

  final AnnotationIntrospector annotationIntrospector;
  final EStructuralFeature feature;
  final AnnotatedFeature annotated;
  final PropertyName name;

  public FeaturePropertyDefinition(
      final AnnotationIntrospector annotationIntrospector,
      final JavaType type,
      final EStructuralFeature feature) {
    this(annotationIntrospector, type, feature, PropertyName.construct(feature.getName()));
  }

  public FeaturePropertyDefinition(
      final AnnotationIntrospector annotationIntrospector,
      final JavaType type,
      final EStructuralFeature feature,
      final PropertyName name) {
    this.annotationIntrospector = annotationIntrospector;
    this.feature = feature;
    this.name = name;
    this.annotated = new AnnotatedFeature(type, feature);
  }

  public EStructuralFeature getFeature() {
    return feature;
  }

  @Override
  public BeanPropertyDefinition withName(final PropertyName newName) {
    return new FeaturePropertyDefinition(
        annotationIntrospector, annotated.getType(), feature, newName);
  }

  @Override
  public BeanPropertyDefinition withSimpleName(final String newSimpleName) {
    return new FeaturePropertyDefinition(
        annotationIntrospector,
        annotated.getType(),
        feature,
        PropertyName.construct(newSimpleName));
  }

  @Override
  public String getName() {
    return name.getSimpleName();
  }

  @Override
  public PropertyName getFullName() {
    return name;
  }

  @Override
  public String getInternalName() {
    return feature.getName();
  }

  @Override
  public PropertyName getWrapperName() {
    return null;
  }

  @Override
  public boolean isExplicitlyIncluded() {
    return false;
  }

  @Override
  public JavaType getPrimaryType() {
    return annotated.getType();
  }

  @Override
  public Class<?> getRawPrimaryType() {
    return getPrimaryType().getRawClass();
  }

  @Override
  public PropertyMetadata getMetadata() {
    return PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
  }

  @Override
  public boolean hasGetter() {
    return false;
  }

  @Override
  public boolean hasSetter() {
    return false;
  }

  @Override
  public boolean hasField() {
    return false;
  }

  @Override
  public boolean hasConstructorParameter() {
    return false;
  }

  @Override
  public AnnotatedMethod getGetter() {
    return null;
  }

  @Override
  public AnnotatedMethod getSetter() {
    return null;
  }

  @Override
  public AnnotatedField getField() {
    return null;
  }

  @Override
  public AnnotatedParameter getConstructorParameter() {
    return null;
  }

  @Override
  public AnnotatedMember getAccessor() {
    return annotated;
  }

  @Override
  public AnnotatedMember getPrimaryMember() {
    return annotated;
  }

  @Override
  public Value findInclusion() {
    final var value = annotationIntrospector.findPropertyInclusion(annotated);
    return (value == null) ? Value.empty() : value;
  }
}
