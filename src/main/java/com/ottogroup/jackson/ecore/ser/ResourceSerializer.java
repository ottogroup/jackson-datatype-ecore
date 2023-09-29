package com.ottogroup.jackson.ecore.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.AsArraySerializerBase;
import java.io.IOException;
import java.io.Serial;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A {@link JsonSerializer} for instances of {@link Resource}.
 *
 * <p>Single contents are unwrapped, i.e. if the resource only contains a single object, it will be
 * serialized as an object. Otherwise, all contents are serialized as an array.
 */
public class ResourceSerializer extends ContainerSerializer<Resource>
    implements ContextualSerializer {
  @Serial private static final long serialVersionUID = 1L;

  protected final JavaType type;
  protected final ContainerSerializer<Object> contentSerializer;

  /*
  /**********************************************************
  /* Life-cycle
  /**********************************************************
   */

  public ResourceSerializer(final JavaType type) {
    this(type, null);
  }

  @SuppressWarnings("unchecked")
  protected ResourceSerializer(
      final JavaType type, final ContainerSerializer<?> contentSerializer) {
    super(type);
    this.type = type;
    this.contentSerializer = (ContainerSerializer<Object>) contentSerializer;
  }

  @Override
  protected ContainerSerializer<?> _withValueTypeSerializer(final TypeSerializer vts) {
    return new ResourceSerializer(type, contentSerializer.withValueTypeSerializer(vts));
  }

  /*
  /**********************************************************
  /* Accessors
  /**********************************************************
   */

  @Override
  public JavaType getContentType() {
    return contentSerializer.getContentType();
  }

  @Override
  public JsonSerializer<?> getContentSerializer() {
    return contentSerializer;
  }

  @Override
  public boolean isEmpty(final SerializerProvider provider, final Resource value) {
    return contentSerializer.isEmpty(provider, value.getContents());
  }

  @Override
  public boolean hasSingleElement(final Resource value) {
    return contentSerializer.hasSingleElement(value.getContents());
  }

  /*
  /**********************************************************
  /* Contextualization
  /**********************************************************
   */

  @Override
  public JsonSerializer<?> createContextual(
      final SerializerProvider prov, final BeanProperty property) throws JsonMappingException {
    final var contentType =
        prov.getTypeFactory().constructCollectionType(EList.class, EObject.class);
    final var typeSer = prov.findTypeSerializer(contentType.getContentType());
    final var arraySer =
        (AsArraySerializerBase<?>) prov.findContentValueSerializer(contentType, property);
    final var contentSer =
        arraySer.withResolved(
            property, typeSer, arraySer.getContentSerializer(), /*unwrapSingle*/ true);

    return new ResourceSerializer(type, contentSer);
  }

  /*
  /**********************************************************
  /* Serialization
  /**********************************************************
   */

  @Override
  public void serialize(
      final Resource value, final JsonGenerator gen, final SerializerProvider provider)
      throws IOException {
    provider.setAttribute(Resource.class, value);
    provider.setAttribute(ResourceSet.class, value.getResourceSet());
    contentSerializer.serialize(value.getContents(), gen, provider);
  }
}
