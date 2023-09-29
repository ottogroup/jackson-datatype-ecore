package com.ottogroup.jackson.ecore.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import java.util.Map;
import org.eclipse.emf.common.util.EMap;

public class EMapToMapConverter implements Converter<EMap<?, ?>, Map<?, ?>> {

  final MapLikeType eMapType;

  public EMapToMapConverter(final MapLikeType eMapType) {
    this.eMapType = eMapType;
  }

  @Override
  public Map<?, ?> convert(final EMap<?, ?> value) {
    return value.map();
  }

  @Override
  public JavaType getInputType(final TypeFactory typeFactory) {
    return eMapType;
  }

  @Override
  public JavaType getOutputType(final TypeFactory typeFactory) {
    return typeFactory.constructMapType(
        Map.class, eMapType.getKeyType(), eMapType.getContentType());
  }
}
