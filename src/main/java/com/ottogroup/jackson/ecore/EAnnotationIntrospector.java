package com.ottogroup.jackson.ecore;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import java.io.Serial;
import java.lang.annotation.Annotation;
import com.ottogroup.jackson.ecore.annotation.EJsonAlias;
import com.ottogroup.jackson.ecore.annotation.EJsonIgnore;
import com.ottogroup.jackson.ecore.annotation.EJsonRawValue;
import com.ottogroup.jackson.ecore.annotation.EJsonTypeIdResolver;
import com.ottogroup.jackson.ecore.annotation.EJsonTypeInfo;
import com.ottogroup.jackson.ecore.deser.EObjectIdResolver;
import com.ottogroup.jackson.ecore.deser.RawDeserializer;
import com.ottogroup.jackson.ecore.introspect.AnnotatedFeature;
import com.ottogroup.jackson.ecore.ser.EObjectIdGenerator;
import com.ottogroup.jackson.ecore.type.EClassType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EReference;

public class EAnnotationIntrospector extends JacksonAnnotationIntrospector {

  @Serial private static final long serialVersionUID = 1L;

  final Version version;

  public EAnnotationIntrospector(final Version version) {
    this.version = version;
  }

  @Override
  public Version version() {
    return version;
  }

  /*
  /**********************************************************
  /* Annotations for Object Id handling
  /**********************************************************
   */

  @Override
  public ObjectIdInfo findObjectIdInfo(final Annotated ann) {
    if (ann instanceof final AnnotatedFeature annotatedFeature
        && annotatedFeature.getFeature() instanceof final EReference reference
        && !reference.isContainment()) {
      final var type = annotatedFeature.getType();
      final var contentType = type.isContainerType() ? type.getContentType() : type;
      return new ObjectIdInfo(
              PropertyName.USE_DEFAULT,
              contentType.getRawClass(),
              EObjectIdGenerator.class,
              EObjectIdResolver.class)
          .withAlwaysAsId(true);
    }

    return super.findObjectIdInfo(ann);
  }

  /*
  /**********************************************************
  /* Deserialization: general annotations
  /**********************************************************
   */

  @Override
  public Object findDeserializer(final Annotated ann) {
    final JsonRawValue jsonRawValue = _findAnnotation(ann, JsonRawValue.class);
    if (jsonRawValue != null && jsonRawValue.value()) {
      return new RawDeserializer();
    }

    return super.findDeserializer(ann);
  }

  /*
  /**********************************************************
  /* Overridable methods: may be used as low-level extension
  /* points.
  /**********************************************************
   */

  @Override
  protected <A extends Annotation> A _findAnnotation(
      final Annotated ann, final Class<A> annoClass) {
    if (ann instanceof final AnnotatedFeature annotatedFeature) {
      final var feature = annotatedFeature.getFeature();
      return _findAnnotation(feature, annoClass);
    } else if (ann instanceof AnnotatedClass
        && ann.getType() instanceof final EClassType eClassType) {
      final var eClass = eClassType.getEClass();
      return _findAnnotation(eClass, annoClass);
    }

    return super._findAnnotation(ann, annoClass);
  }

  @SuppressWarnings("unchecked")
  protected <A extends Annotation> A _findAnnotation(
      final EModelElement modelElement, final Class<A> annoClass) {
    final var annotation = modelElement.getEAnnotation(annoClass.getSimpleName());
    if (annotation != null) {
      if (annoClass == JsonAlias.class) {
        return (A) new EJsonAlias(annotation);
      } else if (annoClass == JsonIgnore.class) {
        return (A) new EJsonIgnore(annotation);
      } else if (annoClass == JsonRawValue.class) {
        return (A) new EJsonRawValue(annotation);
      } else if (annoClass == JsonTypeIdResolver.class) {
        return (A) new EJsonTypeIdResolver(annotation);
      } else if (annoClass == JsonTypeInfo.class) {
        return (A) new EJsonTypeInfo(annotation);
      }
    }

    return null;
  }

  @Override
  protected boolean _hasAnnotation(
      final Annotated ann, final Class<? extends Annotation> annoClass) {
    if (ann instanceof final AnnotatedFeature annotatedFeature) {
      final var feature = annotatedFeature.getFeature();
      return _hasAnnotation(feature, annoClass);
    } else if (ann instanceof AnnotatedClass
        && ann.getType() instanceof final EClassType eClassType) {
      final var eClass = eClassType.getEClass();
      return _hasAnnotation(eClass, annoClass);
    }

    return super._hasAnnotation(ann, annoClass);
  }

  protected boolean _hasAnnotation(
      final EModelElement modelElement, final Class<? extends Annotation> annoClass) {
    return modelElement.getEAnnotation(annoClass.getSimpleName()) != null;
  }

  @Override
  protected boolean _hasOneOf(
      final Annotated ann, final Class<? extends Annotation>[] annoClasses) {
    if (ann instanceof final AnnotatedFeature annotatedFeature) {
      final var feature = annotatedFeature.getFeature();
      return _hasOneOf(feature, annoClasses);
    } else if (ann instanceof AnnotatedClass
        && ann.getType() instanceof final EClassType eClassType) {
      final var eClass = eClassType.getEClass();
      return _hasOneOf(eClass, annoClasses);
    }

    return super._hasOneOf(ann, annoClasses);
  }

  protected boolean _hasOneOf(
      final EModelElement modelElement, final Class<? extends Annotation>[] annoClasses) {
    for (final var annoClass : annoClasses) {
      if (_hasAnnotation(modelElement, annoClass)) return true;
    }

    return false;
  }
}
