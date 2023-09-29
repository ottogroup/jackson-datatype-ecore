package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.ottogroup.jackson.ecore.introspect.EClassDescription;
import com.ottogroup.jackson.ecore.introspect.FeaturePropertyDefinition;
import java.io.Serial;

/**
 * A factory for {@link com.fasterxml.jackson.databind.deser.BeanDeserializer}s for typical Java
 * POJOs (beans) and {@link org.eclipse.emf.ecore.EObject}s.
 *
 * <p>This class is responsible for constructing {@link SettableFeatureProperty}s.
 */
public class EDeserializerFactory extends BeanDeserializerFactory {

  @Serial private static final long serialVersionUID = 1L;

  public EDeserializerFactory() {
    this(new DeserializerFactoryConfig());
  }

  public EDeserializerFactory(final DeserializerFactoryConfig config) {
    super(config);
  }

  @Override
  public DeserializerFactory withConfig(final DeserializerFactoryConfig config) {
    return new EDeserializerFactory(config);
  }

  @Override
  protected void addBeanProps(
      final DeserializationContext ctxt,
      final BeanDescription beanDesc,
      final BeanDeserializerBuilder builder)
      throws JsonMappingException {
    if (beanDesc instanceof final EClassDescription eClassDescription) {
      for (final var propDef : eClassDescription.findProperties()) {
        final var type = propDef.getPrimaryType();
        final var prop = constructSettableProperty(ctxt, beanDesc, propDef, type);
        builder.addProperty(prop);
      }
    } else {
      super.addBeanProps(ctxt, beanDesc, builder);
    }
  }

  @Override
  protected SettableBeanProperty constructSettableProperty(
      final DeserializationContext ctxt,
      final BeanDescription beanDesc,
      final BeanPropertyDefinition propDef,
      final JavaType type)
      throws JsonMappingException {
    if (beanDesc instanceof final EClassDescription eClassDescription
        && propDef instanceof final FeaturePropertyDefinition featurePropDef) {
      final var eClass = eClassDescription.getEClass();
      final var feature = featurePropDef.getFeature();
      final var annotated = featurePropDef.getPrimaryMember();
      final var typeDeser = findTypeDeserializer(ctxt.getConfig(), type);

      var prop = new SettableFeatureProperty(type, eClass, feature, annotated, typeDeser);

      final var deser = findDeserializerFromAnnotation(ctxt, annotated);
      if (deser != null) {
        ctxt.handlePrimaryContextualization(deser, prop, type);
        prop = prop.withValueDeserializer(deser);
      }

      return prop;
    }

    return super.constructSettableProperty(ctxt, beanDesc, propDef, type);
  }

  @Override
  public JsonDeserializer<Object> buildBeanDeserializer(
      final DeserializationContext ctxt, final JavaType type, final BeanDescription beanDesc)
      throws JsonMappingException {
    final var deserializer = super.buildBeanDeserializer(ctxt, type, beanDesc);
    if (beanDesc instanceof EClassDescription
        && deserializer instanceof final BeanDeserializerBase beanDeserializer) {
      return new EObjectDeserializer(beanDeserializer);
    }
    return deserializer;
  }
}
