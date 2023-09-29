package com.ottogroup.jackson.ecore;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.ottogroup.jackson.ecore.deser.EDeserializationContext;
import com.ottogroup.jackson.ecore.introspect.EClassIntrospector;
import com.ottogroup.jackson.ecore.type.ETypeFactory;
import java.io.Serial;
import org.eclipse.emf.ecore.EPackage;

public class EcoreMapper extends ObjectMapper {

  @Serial private static final long serialVersionUID = 1L;

  /*
  /**********************************************************
  /* Life-cycle: constructing instance
  /**********************************************************
   */

  /** Uses the global, static package registry. */
  public EcoreMapper() {
    this(EPackage.Registry.INSTANCE);
  }

  public EcoreMapper(final EPackage.Registry packageRegistry) {
    this(null, packageRegistry);
  }

  public EcoreMapper(final JsonFactory jsonFactory, final EPackage.Registry packageRegistry) {
    super(jsonFactory, null, new EDeserializationContext());
    setSerializationInclusion(Include.NON_DEFAULT);
    setTypeFactory(new ETypeFactory(packageRegistry));
    registerModule(new EcoreModule());
  }

  protected EcoreMapper(final EcoreMapper src) {
    super(src);
  }

  protected EcoreMapper(final EcoreMapper src, final JsonFactory factory) {
    super(src, factory);
  }

  @Override
  protected ClassIntrospector defaultClassIntrospector() {
    return new EClassIntrospector();
  }

  /*
  /**********************************************************
  /* Methods subclasses MUST override
  /**********************************************************
   */

  @Override
  public ObjectMapper copy() {
    return new EcoreMapper(this);
  }

  @Override
  public ObjectMapper copyWith(final JsonFactory factory) {
    return new EcoreMapper(this, factory);
  }
}
