package com.ottogroup.jackson.ecore.annotation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.emf.ecore.EAnnotation;

public class EJsonIgnore extends EAnnotationBridge implements JsonIgnore {

  public EJsonIgnore(final EAnnotation ann) {
    super(ann);
  }

  @Override
  public Class<JsonIgnore> annotationType() {
    return JsonIgnore.class;
  }

  @Override
  public boolean value() {
    final var value = ann.getDetails().get("value");
    return value == null || Boolean.parseBoolean(value);
  }
}
