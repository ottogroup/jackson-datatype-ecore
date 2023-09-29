package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.IOException;
import java.io.Serial;
import java.util.Set;

public class EObjectDeserializer extends BeanDeserializer {

  @Serial private static final long serialVersionUID = 6823390801802874803L;

  /*
  /**********************************************************
  /* Life-cycle, construction, initialization
  /**********************************************************
   */

  public EObjectDeserializer(final BeanDeserializerBase src) {
    super(src);
  }

  public EObjectDeserializer(final BeanDeserializerBase src, final boolean ignoreAllUnknown) {
    super(src, ignoreAllUnknown);
  }

  public EObjectDeserializer(final BeanDeserializerBase src, final NameTransformer unwrapper) {
    super(src, unwrapper);
  }

  public EObjectDeserializer(final BeanDeserializerBase src, final ObjectIdReader oir) {
    super(src, oir);
  }

  public EObjectDeserializer(
      final BeanDeserializerBase src,
      final Set<String> ignorableProps,
      final Set<String> includableProps) {
    super(src, ignorableProps, includableProps);
  }

  public EObjectDeserializer(final BeanDeserializerBase src, final BeanPropertyMap props) {
    super(src, props);
  }

  @Override
  public BeanDeserializer withObjectIdReader(final ObjectIdReader oir) {
    return new EObjectDeserializer(this, oir);
  }

  @Override
  public BeanDeserializer withByNameInclusion(
      final Set<String> ignorableProps, final Set<String> includableProps) {
    return new EObjectDeserializer(this, ignorableProps, includableProps);
  }

  @Override
  public BeanDeserializerBase withIgnoreAllUnknown(final boolean ignoreUnknown) {
    return new EObjectDeserializer(this, ignoreUnknown);
  }

  @Override
  public BeanDeserializerBase withBeanProperties(final BeanPropertyMap props) {
    return new EObjectDeserializer(this, props);
  }

  /*
  /**********************************************************
  /* Bean deserializer implementation
  /**********************************************************
   */

  @Override
  protected Object deserializeFromObjectId(final JsonParser p, final DeserializationContext ctxt)
      throws IOException {
    ctxt.setAttribute(EObjectIdResolver.RESOLVED_TYPE_ATTR, _beanType);
    return super.deserializeFromObjectId(p, ctxt);
  }

  @Override
  public Object deserializeFromObject(final JsonParser p, final DeserializationContext ctxt)
      throws IOException {
    if (_objectIdReader != null
        && _objectIdReader.maySerializeAsObject()
        && p.hasToken(JsonToken.FIELD_NAME)
        && _objectIdReader.isValidReferencePropertyName(p.currentName(), p)) {
      p.nextToken(); // Forward from field name to $ref value
      final var obj = deserializeFromObjectId(p, ctxt);
      p.nextToken(); // Forward from $ref value to END_OBJECT
      return obj;
    }

    return super.deserializeFromObject(p, ctxt);
  }
}
