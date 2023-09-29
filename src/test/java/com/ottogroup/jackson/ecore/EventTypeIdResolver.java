package com.ottogroup.jackson.ecore;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.ottogroup.jackson.ecore.type.ETypeFactory;
import org.eclipse.emfcloud.jackson.junit.model.FooEvent;
import org.eclipse.emfcloud.jackson.junit.model.ModelPackage;

public class EventTypeIdResolver extends TypeIdResolverBase {

  @Override
  public String idFromValue(final Object value) {
    if (value instanceof final FooEvent event) {
      return event.getType();
    }

    throw new IllegalArgumentException("Only subtypes of Event are supported");
  }

  @Override
  public String idFromValueAndType(final Object value, final Class<?> suggestedType) {
    return idFromValue(value);
  }

  @Override
  public JsonTypeInfo.Id getMechanism() {
    return JsonTypeInfo.Id.CUSTOM;
  }

  @Override
  public JavaType typeFromId(final DatabindContext context, final String id) {
    if ("foo".equals(id)) {
      final var eClass = ModelPackage.Literals.FOO_EVENT;
      return ((ETypeFactory) context.getTypeFactory()).constructEClassType(eClass);
    }

    return null;
  }
}
