package com.ottogroup.jackson.ecore.annotation;

import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import java.lang.annotation.Annotation;
import org.eclipse.emf.ecore.EAnnotation;

public class EJsonTypeIdResolver extends EAnnotationBridge implements JsonTypeIdResolver {

  public EJsonTypeIdResolver(final EAnnotation ann) {
    super(ann);
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return JsonTypeIdResolver.class;
  }

  @Override
  public Class<? extends TypeIdResolver> value() {
    final var value = ann.getDetails().get("value");
    if (value != null) {
      try {
        return Class.forName(value).asSubclass(TypeIdResolver.class);
      } catch (final ClassCastException | ClassNotFoundException e) {
        throw new RuntimeException(
            "Invalid @JsonTypeIdResolver annotation on " + ann.getEModelElement(), e);
      }
    }
    return null;
  }
}
