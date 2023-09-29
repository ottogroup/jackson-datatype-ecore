package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import java.io.IOException;
import java.io.Serial;
import java.util.Map;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EMap;

/**
 * A {@link JsonDeserializer} for instances of {@link EMap}.
 *
 * <p>As {@link EMap}s don't implement the {@link java.util.Map} interface per se, some additional
 * handling is necessary. The actual deserialization work is delegated to a {@link
 * com.fasterxml.jackson.databind.deser.std.MapDeserializer}.
 */
public class EMapDeserializer extends DelegatingDeserializer {

  @Serial private static final long serialVersionUID = 1L;

  final MapLikeType mapLikeType;

  /*
  /**********************************************************
  /* Life-cycle
  /**********************************************************
   */

  public EMapDeserializer(final MapLikeType mapLikeType) {
    this(NullifyingDeserializer.instance, mapLikeType);
  }

  protected EMapDeserializer(final JsonDeserializer<?> delegate, final MapLikeType mapLikeType) {
    super(delegate);
    this.mapLikeType = mapLikeType;
  }

  @Override
  protected JsonDeserializer<?> newDelegatingInstance(final JsonDeserializer<?> newDelegatee) {
    return new EMapDeserializer(newDelegatee, mapLikeType);
  }

  /*
  /**********************************************************
  /* Contextualization
  /**********************************************************
   */

  @Override
  public JsonDeserializer<?> createContextual(
      final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
    if (_delegatee == NullifyingDeserializer.instance) {
      final var mapType =
          ctxt.getTypeFactory()
              .constructMapType(Map.class, mapLikeType.getKeyType(), mapLikeType.getContentType());
      final var del = ctxt.findContextualValueDeserializer(mapType, property);
      return newDelegatingInstance(del);
    } else {
      final var valueType = ctxt.constructType(handledType());
      final var del = ctxt.handleSecondaryContextualization(_delegatee, property, valueType);
      return replaceDelegatee(del);
    }
  }

  /*
  /**********************************************************
  /* JsonDeserializer API
  /**********************************************************
   */

  @Override
  public EMap<?, ?> deserialize(final JsonParser p, final DeserializationContext ctxt)
      throws IOException {
    final var map = (Map<?, ?>) super.deserialize(p, ctxt);
    return new BasicEMap<>(map);
  }

  @Override
  public EMap<?, ?> deserialize(
      final JsonParser p, final DeserializationContext ctxt, final Object intoValue)
      throws IOException {
    final var eMap = (EMap<?, ?>) intoValue;
    super.deserialize(p, ctxt, eMap.map());
    return eMap;
  }

  @Override
  public Object deserializeWithType(
      final JsonParser p,
      final DeserializationContext ctxt,
      final TypeDeserializer typeDeserializer)
      throws IOException {
    final var map = (Map<?, ?>) super.deserializeWithType(p, ctxt, typeDeserializer);
    return new BasicEMap<>(map);
  }

  @Override
  public Object deserializeWithType(
      final JsonParser p,
      final DeserializationContext ctxt,
      final TypeDeserializer typeDeserializer,
      final Object intoValue)
      throws IOException {
    final var eMap = (EMap<?, ?>) intoValue;
    super.deserializeWithType(p, ctxt, typeDeserializer, eMap.map());
    return intoValue;
  }

  /*
  /**********************************************************
  /* Other public accessors
  /**********************************************************
   */

  @Override
  public boolean isCachable() {
    return true;
  }

  @Override
  public Object getEmptyValue(final DeserializationContext ctxt) {
    return new BasicEMap<>();
  }
}
