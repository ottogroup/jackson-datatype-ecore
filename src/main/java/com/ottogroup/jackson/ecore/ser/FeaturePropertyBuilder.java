package com.ottogroup.jackson.ecore.ser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyBuilder;
import com.fasterxml.jackson.databind.util.Annotations;
import com.ottogroup.jackson.ecore.introspect.FeaturePropertyDefinition;

/** Creates {@link FeaturePropertyWriter} based on {@link FeaturePropertyDefinition}s. */
public class FeaturePropertyBuilder extends PropertyBuilder {

  public FeaturePropertyBuilder(final SerializationConfig config, final BeanDescription beanDesc) {
    super(config, beanDesc);
  }

  /*
  /**********************************************************
  /* Public API
  /**********************************************************
   */

  @Override
  protected BeanPropertyWriter _constructPropertyWriter(
      final BeanPropertyDefinition propDef,
      final AnnotatedMember member,
      final Annotations contextAnnotations,
      final JavaType declaredType,
      final JsonSerializer<?> ser,
      final TypeSerializer typeSer,
      final JavaType serType,
      final boolean suppressNulls,
      final Object suppressableValue,
      final Class<?>[] includeInViews) {
    if (!(propDef instanceof final FeaturePropertyDefinition featurePropDef)) {
      throw new IllegalArgumentException("propDef must be a FeaturePropertyDefinition");
    }

    final var actualType = (serType == null) ? declaredType : serType;
    final var inclusion =
        _config
            .getDefaultInclusion(actualType.getRawClass(), member.getRawType(), _defaultInclusion)
            .withOverrides(propDef.findInclusion())
            .getValueInclusion();

    final var suppressMarker =
        switch (inclusion) {
          case ALWAYS -> null;
          case NON_EMPTY -> FeaturePropertyWriter.MARKER_FOR_EMPTY;
          default -> FeaturePropertyWriter.MARKER_FOR_DEFAULT;
        };

    return new FeaturePropertyWriter(
        featurePropDef,
        member,
        contextAnnotations,
        declaredType,
        ser,
        typeSer,
        serType,
        suppressNulls,
        suppressMarker,
        includeInViews);
  }
}
