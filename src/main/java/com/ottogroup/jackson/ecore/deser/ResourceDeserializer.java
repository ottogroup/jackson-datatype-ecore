package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;
import java.io.Serial;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A {@link JsonDeserializer} for instances of {@link Resource}.
 *
 * <p>The deserializer accepts both a single value and an array and will populate the {@link
 * Resource#getContents()} accordingly.
 */
public class ResourceDeserializer extends ContainerDeserializerBase<Resource>
    implements ContextualDeserializer {
  @Serial private static final long serialVersionUID = 1L;

  protected final JsonDeserializer<Object> objectDeserializer;
  protected final JsonDeserializer<Object> collectionDeserializer;
  protected final TypeDeserializer typeDeserializer;

  /*
  /**********************************************************
  /* Life-cycle
  /**********************************************************
   */

  public ResourceDeserializer(final JavaType type) {
    this(type, null, null, null);
  }

  @SuppressWarnings("unchecked")
  protected ResourceDeserializer(
      final JavaType type,
      final JsonDeserializer<?> objectDeserializer,
      final JsonDeserializer<?> collectionDeserializer,
      final TypeDeserializer typeDeserializer) {
    super(type, null, true);
    this.objectDeserializer = (JsonDeserializer<Object>) objectDeserializer;
    this.collectionDeserializer = (JsonDeserializer<Object>) collectionDeserializer;
    this.typeDeserializer = typeDeserializer;
  }

  /*
  /**********************************************************
  /* Contextualization
  /**********************************************************
   */

  @Override
  public JsonDeserializer<?> createContextual(
      final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
    final var objectType = ctxt.getTypeFactory().constructType(EObject.class);
    final var objectDeser = ctxt.findContextualValueDeserializer(objectType, property);
    final var collectionType =
        ctxt.getTypeFactory().constructCollectionType(EList.class, objectType);
    final var collectionDeser = ctxt.findContextualValueDeserializer(collectionType, property);
    final var typeDeser = ctxt.getConfig().findTypeDeserializer(objectType);

    return new ResourceDeserializer(_containerType, objectDeser, collectionDeser, typeDeser);
  }

  /*
  /**********************************************************
  /* Accessors
  /**********************************************************
   */

  @Override
  public LogicalType logicalType() {
    return LogicalType.Collection;
  }

  @Override
  public boolean isCachable() {
    return true;
  }

  @Override
  public JsonDeserializer<?> getDelegatee() {
    return collectionDeserializer;
  }

  /*
  /**********************************************************
  /* JsonDeserializer API
  /**********************************************************
   */

  @Override
  public Resource deserialize(final JsonParser p, final DeserializationContext ctxt) {
    throw new UnsupportedOperationException("Dynamic creation of resources is not supported.");
  }

  @Override
  public Resource deserialize(
      final JsonParser p, final DeserializationContext ctxt, final Resource intoValue)
      throws IOException {
    ctxt.setAttribute(ResourceSet.class, intoValue.getResourceSet());
    ctxt.setAttribute(Resource.class, intoValue);

    if (p.isExpectedStartArrayToken()) {
      // The type is handled by the content serializer already
      collectionDeserializer.deserialize(p, ctxt, intoValue.getContents());
    } else if (p.isExpectedStartObjectToken()) {
      final EObject object;
      if (typeDeserializer != null) {
        object = (EObject) objectDeserializer.deserializeWithType(p, ctxt, typeDeserializer);
      } else {
        object = (EObject) objectDeserializer.deserialize(p, ctxt);
      }
      intoValue.getContents().add(object);
    } else {
      ctxt.handleUnexpectedToken(_containerType, p);
    }

    ctxt.checkUnresolvedObjectId();

    return intoValue;
  }

  /*
  /**********************************************************
  /* Extended API
  /**********************************************************
   */

  @Override
  public JsonDeserializer<Object> getContentDeserializer() {
    return objectDeserializer;
  }
}
