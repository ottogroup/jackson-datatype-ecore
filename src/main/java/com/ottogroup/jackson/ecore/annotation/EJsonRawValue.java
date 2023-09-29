package com.ottogroup.jackson.ecore.annotation;

import com.fasterxml.jackson.annotation.JsonRawValue;
import java.lang.annotation.Annotation;
import org.eclipse.emf.ecore.EAnnotation;

public class EJsonRawValue extends EAnnotationBridge implements JsonRawValue {

  public EJsonRawValue(final EAnnotation ann) {
    super(ann);
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return JsonRawValue.class;
  }

  @Override
  public boolean value() {
    final var value = ann.getDetails().get("value");
    return value == null || Boolean.parseBoolean(value);
  }
}
