package com.ottogroup.jackson.ecore.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ottogroup.jackson.ecore.type.ETypeFactory;
import java.io.IOException;
import java.io.Serial;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;

/**
 * A {@link JsonSerializer} for {@link DynamicEObjectImpl}s.
 *
 * <p>Serialization work is delegated to a properly configured {@link
 * com.fasterxml.jackson.databind.ser.BeanSerializer} once the value's {@link EClass} is known at
 * serialization time. The dynamic nature disallows the creation of the serializer at
 * contextualization time.
 */
public class DynamicEObjectSerializer extends StdSerializer<DynamicEObjectImpl> {

  @Serial private static final long serialVersionUID = 1L;

  public DynamicEObjectSerializer(final JavaType type) {
    super(type);
  }

  /*
  /**********************************************************
  /* Serialization
  /**********************************************************
   */

  @Override
  public void serialize(
      final DynamicEObjectImpl value, final JsonGenerator gen, final SerializerProvider provider)
      throws IOException {
    final var ser = findSerializer(value.eClass(), provider);
    ser.serialize(value, gen, provider);
  }

  @Override
  public void serializeWithType(
      final DynamicEObjectImpl value,
      final JsonGenerator gen,
      final SerializerProvider provider,
      final TypeSerializer typeSer)
      throws IOException {
    final var ser = findSerializer(value.eClass(), provider);
    ser.serializeWithType(value, gen, provider, typeSer);
  }

  /*
  /**********************************************************
  /* Helper methods
  /**********************************************************
   */

  protected JsonSerializer<Object> findSerializer(
      final EClass eClass, final SerializerProvider provider) throws JsonMappingException {
    final var typeFactory = (ETypeFactory) provider.getTypeFactory();
    final var type = typeFactory.constructEClassType(eClass);
    return provider.findValueSerializer(type);
  }
}
