package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.cfg.CacheProvider;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import java.io.Serial;

public class EDeserializationContext extends DefaultDeserializationContext {

  @Serial private static final long serialVersionUID = 1L;

  public EDeserializationContext() {
    this(new EDeserializerFactory());
  }

  protected EDeserializationContext(
      final EDeserializationContext src,
      final DeserializationConfig config,
      final JsonParser p,
      final InjectableValues values) {
    super(src, config, p, values);
  }

  protected EDeserializationContext(final EDeserializationContext src) {
    super(src);
  }

  protected EDeserializationContext(final DeserializerFactory factory) {
    super(factory, null);
  }

  protected EDeserializationContext(
      final EDeserializationContext src, final DeserializationConfig config) {
    super(src, config);
  }

  protected EDeserializationContext(DefaultDeserializationContext src, CacheProvider cp) {
    super(src, cp);
  }

  @Override
  public DefaultDeserializationContext copy() {
    return new EDeserializationContext(this);
  }

  /*
  /**********************************************************
  /* Abstract methods impls, Object Id
  /**********************************************************
   */

  @Override
  protected ReadableObjectId createReadableObjectId(final IdKey key) {
    return new ReadableEObjectId(key);
  }

  /*
  /**********************************************************
  /* Extended API, life-cycle
  /**********************************************************
   */

  @Override
  public DefaultDeserializationContext with(final DeserializerFactory factory) {
    return new EDeserializationContext(factory);
  }

  @Override
  public DefaultDeserializationContext withCaches(CacheProvider cacheProvider) {
    return new EDeserializationContext(this, cacheProvider);
  }

  @Override
  public DefaultDeserializationContext createInstance(
      final DeserializationConfig config, final JsonParser p, final InjectableValues values) {
    return new EDeserializationContext(this, config, p, values);
  }

  @Override
  public DefaultDeserializationContext createDummyInstance(final DeserializationConfig config) {
    return new EDeserializationContext(this, config);
  }
}
