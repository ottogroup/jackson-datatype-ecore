package com.ottogroup.jackson.ecore.type;

import com.fasterxml.jackson.databind.type.TypeBase;
import java.io.Serial;
import org.eclipse.emf.ecore.EClass;

public class EClassType extends EClassifierType {

  @Serial private static final long serialVersionUID = 1L;

  final EClass eClass;

  EClassType(final EClass eClass, final TypeBase base) {
    super(eClass, base);
    this.eClass = eClass;
  }

  public EClass getEClass() {
    return eClass;
  }

  @Override
  public boolean isAbstract() {
    return eClass.isAbstract();
  }

  @Override
  public boolean isConcrete() {
    return !eClass.isAbstract() && !eClass.isInterface();
  }

  @Override
  public boolean hasGenericTypes() {
    return !eClass.getETypeParameters().isEmpty();
  }
}
