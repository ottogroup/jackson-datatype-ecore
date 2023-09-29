package com.ottogroup.jackson.ecore.ser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.PropertyBuilder;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.ottogroup.jackson.ecore.introspect.EClassDescription;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * A factory for {@link com.fasterxml.jackson.databind.ser.BeanSerializer}s for typical Java POJOs
 * (beans) and {@link org.eclipse.emf.ecore.EObject}s.
 *
 * <p>This class is responsible for constructing {@link FeaturePropertyBuilder}s.
 */
public class ESerializerFactory extends BeanSerializerFactory {

  @Serial private static final long serialVersionUID = 1L;

  public ESerializerFactory() {
    this(null);
  }

  public ESerializerFactory(final SerializerFactoryConfig config) {
    super(config);
  }

  @Override
  public SerializerFactory withConfig(final SerializerFactoryConfig config) {
    return new ESerializerFactory(config);
  }

  @Override
  protected void removeIgnorableTypes(
      final SerializationConfig config,
      final BeanDescription beanDesc,
      final List<BeanPropertyDefinition> properties) {
    if (!(beanDesc instanceof EClassDescription))
      super.removeIgnorableTypes(config, beanDesc, properties);
  }

  @Override
  protected PropertyBuilder constructPropertyBuilder(
      final SerializationConfig config, final BeanDescription beanDesc) {
    if (beanDesc instanceof EClassDescription) {
      return new FeaturePropertyBuilder(config, beanDesc);
    }

    return super.constructPropertyBuilder(config, beanDesc);
  }

  @Override
  protected List<BeanPropertyWriter> findBeanProperties(
      final SerializerProvider prov,
      final BeanDescription beanDesc,
      final BeanSerializerBuilder builder)
      throws JsonMappingException {
    if (!(beanDesc instanceof EClassDescription)) {
      return super.findBeanProperties(prov, beanDesc, builder);
    }

    final var properties = beanDesc.findProperties();
    if (properties.isEmpty()) return null;

    final var pb = constructPropertyBuilder(prov.getConfig(), beanDesc);
    final var result = new ArrayList<BeanPropertyWriter>(properties.size());
    for (final var property : properties) {
      final var accessor = property.getAccessor();
      final var writer = _constructWriter(prov, property, pb, true, accessor);
      result.add(writer);
    }

    return result;
  }
}
