package com.ottogroup.jackson.ecore.annotation;

import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.lang.annotation.Annotation;
import org.eclipse.emf.ecore.EAnnotation;

public class EJsonTypeInfo extends EAnnotationBridge implements JsonTypeInfo {
  public EJsonTypeInfo(final EAnnotation ann) {
    super(ann);
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return JsonTypeId.class;
  }

  @Override
  public Id use() {
    final var use = ann.getDetails().get("use");
    return use != null ? Id.valueOf(use) : null;
  }

  @Override
  public As include() {
    final var include = ann.getDetails().get("include");
    return include != null ? As.valueOf(include) : As.PROPERTY;
  }

  @Override
  public String property() {
    final var property = ann.getDetails().get("property");
    return property != null ? property : "";
  }

  @Override
  public Class<?> defaultImpl() {
    final var defaultImpl = ann.getDetails().get("defaultImpl");
    if (defaultImpl != null) {
      try {
        return Class.forName(defaultImpl);
      } catch (final ClassNotFoundException e) {
        throw new RuntimeException(
            "Invalid defaultImpl in @JsonTypeInfo annotation on " + ann.getEModelElement(), e);
      }
    }

    return JsonTypeInfo.class;
  }

  @Override
  public boolean visible() {
    final var visible = ann.getDetails().get("visible");
    return Boolean.parseBoolean(visible);
  }
}
