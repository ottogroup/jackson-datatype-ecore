package com.ottogroup.jackson.ecore.type;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Validity;
import java.io.Serial;
import org.eclipse.emf.ecore.EObject;

public class ETypeResolverBuilder extends DefaultTypeResolverBuilder {

  @Serial private static final long serialVersionUID = 1L;

  public ETypeResolverBuilder(final JavaType baseType) {
    this(baseType.getRawClass());
  }

  public ETypeResolverBuilder(final Class<?> baseType) {
    super(
        DefaultTyping.OBJECT_AND_NON_CONCRETE,
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(baseType).build());
  }

  @Override
  public boolean useForType(final JavaType t) {
    final var validity = subTypeValidator(null).validateBaseType(null, t);
    if (validity == Validity.ALLOWED) {
      if (super.useForType(t)) {
        return true;
      } else if (_appliesFor == DefaultTyping.JAVA_LANG_OBJECT
          || _appliesFor == DefaultTyping.OBJECT_AND_NON_CONCRETE) {
        return t.hasRawClass(EObject.class);
      }
    }
    return false;
  }

  @Override
  protected JavaType defineDefaultImpl(
      final DeserializationConfig config, final JavaType baseType) {
    if (!baseType.isAbstract()) {
      return baseType;
    }
    return null;
  }
}
