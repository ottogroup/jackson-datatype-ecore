package com.ottogroup.jackson.ecore.annotation;

import java.util.Objects;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.util.EcoreUtil;

public abstract class EAnnotationBridge {

  final EAnnotation ann;

  protected EAnnotationBridge(final EAnnotation ann) {
    Objects.requireNonNull(ann, "ann");
    this.ann = ann;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof final EAnnotationBridge that)) {
      return false;
    }
    return EcoreUtil.equals(ann, that.ann);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ann);
  }
}
