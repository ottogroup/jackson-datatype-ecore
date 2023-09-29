package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.CollectionDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.ottogroup.jackson.ecore.deser.EInstantiators.EListInstantiator;
import com.ottogroup.jackson.ecore.type.EDataTypeType;
import com.ottogroup.jackson.ecore.util.StringToURIConverter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Responsible for discovery of {@link JsonDeserializer}s based on a given type.
 *
 * <p>This class is responsible for constructing the different {@link JsonDeserializer}
 * implementations.
 */
public class EDeserializers extends Deserializers.Base {

  @Override
  public JsonDeserializer<?> findEnumDeserializer(
      final Class<?> type, final DeserializationConfig config, final BeanDescription beanDesc)
      throws JsonMappingException {
    if (beanDesc.getType() instanceof final EDataTypeType eDataTypeType
        && eDataTypeType.getEDataType().isSerializable()) {
      return new EDataTypeDeserializer(eDataTypeType);
    }

    return super.findEnumDeserializer(type, config, beanDesc);
  }

  @Override
  public JsonDeserializer<?> findBeanDeserializer(
      final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc)
      throws JsonMappingException {
    if (type.isTypeOrSubTypeOf(Resource.class)) {
      return new ResourceDeserializer(type);
    }
    if (type instanceof final EDataTypeType eDataTypeType
        && eDataTypeType.getEDataType().isSerializable()) {
      return new EDataTypeDeserializer(eDataTypeType);
    }
    if (type.isTypeOrSubTypeOf(URI.class)) {
      return new StdDelegatingDeserializer<>(new StringToURIConverter());
    }

    return super.findBeanDeserializer(type, config, beanDesc);
  }

  @Override
  @SuppressWarnings("unchecked")
  public JsonDeserializer<?> findCollectionDeserializer(
      final CollectionType type,
      final DeserializationConfig config,
      final BeanDescription beanDesc,
      final TypeDeserializer elementTypeDeserializer,
      final JsonDeserializer<?> elementDeserializer)
      throws JsonMappingException {
    if (type.isTypeOrSubTypeOf(EList.class)) {
      return new CollectionDeserializer(
          type,
          (JsonDeserializer<Object>) elementDeserializer,
          elementTypeDeserializer,
          EListInstantiator.INSTANCE);
    }

    return super.findCollectionDeserializer(
        type, config, beanDesc, elementTypeDeserializer, elementDeserializer);
  }

  @Override
  public JsonDeserializer<?> findMapLikeDeserializer(
      final MapLikeType type,
      final DeserializationConfig config,
      final BeanDescription beanDesc,
      final KeyDeserializer keyDeserializer,
      final TypeDeserializer elementTypeDeserializer,
      final JsonDeserializer<?> elementDeserializer)
      throws JsonMappingException {
    if (type.isTypeOrSubTypeOf(EMap.class)) {
      return new EMapDeserializer(type);
    }

    return super.findMapLikeDeserializer(
        type, config, beanDesc, keyDeserializer, elementTypeDeserializer, elementDeserializer);
  }
}
