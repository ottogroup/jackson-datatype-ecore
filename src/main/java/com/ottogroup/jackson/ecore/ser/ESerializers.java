package com.ottogroup.jackson.ecore.ser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.ottogroup.jackson.ecore.type.EClassType;
import com.ottogroup.jackson.ecore.type.EDataTypeType;
import com.ottogroup.jackson.ecore.util.EMapToMapConverter;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Responsible for discovery of {@link JsonSerializer}s based on a given type.
 *
 * <p>This class is responsible for constructing the different {@link JsonSerializer}
 * implementations.
 */
public class ESerializers extends Serializers.Base {

  @Override
  public JsonSerializer<?> findSerializer(
      final SerializationConfig config, final JavaType type, final BeanDescription beanDesc) {
    if (type.isTypeOrSubTypeOf(Resource.class)) {
      return new ResourceSerializer(type);
    }
    if (type instanceof final EDataTypeType eDataTypeType
        && eDataTypeType.getEDataType().isSerializable()) {
      return new EDataTypeSerializer(eDataTypeType);
    }
    if (!(type instanceof EClassType) && type.isTypeOrSubTypeOf(DynamicEObjectImpl.class)) {
      return new DynamicEObjectSerializer(type);
    }
    if (type.isTypeOrSubTypeOf(URI.class)) {
      return ToStringSerializer.instance;
    }

    return super.findSerializer(config, type, beanDesc);
  }

  @Override
  public JsonSerializer<?> findMapLikeSerializer(
      final SerializationConfig config,
      final MapLikeType type,
      final BeanDescription beanDesc,
      final JsonSerializer<Object> keySerializer,
      final TypeSerializer elementTypeSerializer,
      final JsonSerializer<Object> elementValueSerializer) {
    if (type.isTypeOrSubTypeOf(EMap.class)) {
      final var conv = new EMapToMapConverter(type);
      return new StdDelegatingSerializer(conv);
    }

    return super.findMapLikeSerializer(
        config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
  }
}
