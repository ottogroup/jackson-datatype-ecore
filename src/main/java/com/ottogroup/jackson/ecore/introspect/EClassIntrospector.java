package com.ottogroup.jackson.ecore.introspect;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.ottogroup.jackson.ecore.type.EClassType;
import java.io.Serial;

public class EClassIntrospector extends BasicClassIntrospector {

  @Serial private static final long serialVersionUID = 1L;

  @Override
  public ClassIntrospector copy() {
    return new EClassIntrospector();
  }

  @Override
  public BasicBeanDescription forSerialization(
      final SerializationConfig cfg, final JavaType type, final MixInResolver r) {
    if (type instanceof final EClassType eClassType) {
      return new EClassDescription(eClassType, cfg, _resolveAnnotatedClass(cfg, type, r));
    }

    return super.forSerialization(cfg, type, r);
  }

  @Override
  public BasicBeanDescription forDeserialization(
      final DeserializationConfig cfg, final JavaType type, final MixInResolver r) {
    if (type instanceof final EClassType eClassType) {
      return new EClassDescription(eClassType, cfg, _resolveAnnotatedClass(cfg, type, r));
    }

    return super.forDeserialization(cfg, type, r);
  }

  @Override
  public BasicBeanDescription forClassAnnotations(
      final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
    if (type instanceof final EClassType eClassType) {
      return new EClassDescription(eClassType, config, _resolveAnnotatedClass(config, type, r));
    }

    return super.forClassAnnotations(config, type, r);
  }
}
