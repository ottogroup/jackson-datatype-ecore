package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.ottogroup.jackson.ecore.type.EDataTypeType;
import java.io.IOException;
import java.io.Serial;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * A {@link com.fasterxml.jackson.databind.JsonDeserializer} for values of a specific {@link
 * EDataType}. Thus, values can either be {@link org.eclipse.emf.ecore.EEnum} literals or primitive
 * values with an optional {@code createFromString} logic.
 */
public class EDataTypeDeserializer extends StdScalarDeserializer<Object> {

  @Serial private static final long serialVersionUID = 1L;

  final EDataType eDataType;

  public EDataTypeDeserializer(final EDataTypeType type) {
    super(type);
    this.eDataType = type.getEDataType();
  }

  /*
  /**********************************************************
  /* Accessors
  /**********************************************************
   */

  @Override
  public boolean isCachable() {
    return true;
  }

  /*
  /**********************************************************
  /* JsonDeserializer API
  /**********************************************************
   */

  @Override
  public Object deserialize(final JsonParser p, final DeserializationContext ctxt)
      throws IOException {
    @SuppressWarnings("deprecation")
    final var literal = _parseString(p, ctxt);
    return EcoreUtil.createFromString(eDataType, literal);
  }
}
