package com.ottogroup.jackson.ecore.annotation;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.lang.annotation.Annotation;
import org.eclipse.emf.ecore.EAnnotation;

public class EJsonAlias extends EAnnotationBridge implements JsonAlias {

  public EJsonAlias(final EAnnotation ann) {
    super(ann);
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return JsonAlias.class;
  }

  @Override
  public String[] value() {
    final var value = ann.getDetails().get("value");
    return value != null ? value.split(",", 0) : new String[0];
  }
}
