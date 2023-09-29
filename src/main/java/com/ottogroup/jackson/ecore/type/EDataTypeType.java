package com.ottogroup.jackson.ecore.type;

import com.fasterxml.jackson.databind.type.TypeBase;
import java.io.Serial;
import org.eclipse.emf.ecore.EDataType;

public class EDataTypeType extends EClassifierType {

  @Serial private static final long serialVersionUID = 1L;

  final EDataType eDataType;

  EDataTypeType(final EDataType eDataType, final TypeBase base) {
    super(eDataType, base);
    this.eDataType = eDataType;
  }

  public EDataType getEDataType() {
    return eDataType;
  }
}
