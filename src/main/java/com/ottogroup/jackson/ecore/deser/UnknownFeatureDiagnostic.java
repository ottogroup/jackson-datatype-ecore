package com.ottogroup.jackson.ecore.deser;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class UnknownFeatureDiagnostic implements Resource.Diagnostic {

  final String featureName;
  final EClass eClass;
  final String location;
  final int line;
  final int column;

  public UnknownFeatureDiagnostic(
      final String featureName,
      final EClass eClass,
      final String location,
      final int line,
      final int column) {
    this.featureName = featureName;
    this.eClass = eClass;
    this.location = location;
    this.line = line;
    this.column = column;
  }

  @Override
  public String getMessage() {
    return "Feature '%s' not found for class %s".formatted(featureName, EcoreUtil.getURI(eClass));
  }

  @Override
  public String getLocation() {
    return location;
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getColumn() {
    return column;
  }
}
