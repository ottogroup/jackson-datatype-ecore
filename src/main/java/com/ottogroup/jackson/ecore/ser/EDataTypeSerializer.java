package com.ottogroup.jackson.ecore.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.ottogroup.jackson.ecore.type.EDataTypeType;
import java.io.IOException;
import java.io.Serial;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * A {@link com.fasterxml.jackson.databind.JsonSerializer} for values of a specific {@link
 * EDataType}. Thus values can either be {@link org.eclipse.emf.ecore.EEnum} literals or primitive
 * values with an optinal {@code convertToString} logic.
 */
public class EDataTypeSerializer extends StdScalarSerializer<Object> {

  @Serial private static final long serialVersionUID = 1L;

  final EDataType eDataType;

  protected EDataTypeSerializer(final EDataTypeType type) {
    super(type.getRawClass(), true);
    this.eDataType = type.getEDataType();
  }

  @Override
  public void serialize(
      final Object value, final JsonGenerator gen, final SerializerProvider provider)
      throws IOException {
    final var literal = EcoreUtil.convertToString(eDataType, value);
    gen.writeString(literal);
  }
}
