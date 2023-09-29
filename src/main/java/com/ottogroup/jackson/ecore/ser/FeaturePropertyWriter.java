package com.ottogroup.jackson.ecore.ser;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.impl.UnknownSerializer;
import com.fasterxml.jackson.databind.util.Annotations;
import com.ottogroup.jackson.ecore.introspect.FeaturePropertyDefinition;
import com.ottogroup.jackson.ecore.type.ETypeFactory;
import java.io.Serial;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/** Responsible for serializing a single {@link EStructuralFeature}. */
public class FeaturePropertyWriter extends BeanPropertyWriter {

  public static final Object MARKER_FOR_DEFAULT = Include.NON_DEFAULT;

  @Serial private static final long serialVersionUID = 1L;

  final EStructuralFeature feature;

  /*
  /***********************************************************
  /* Construction, configuration
  /***********************************************************
   */

  public FeaturePropertyWriter(
      final FeaturePropertyDefinition propDef,
      final AnnotatedMember member,
      final Annotations contextAnnotations,
      final JavaType declaredType,
      final JsonSerializer<?> ser,
      final TypeSerializer typeSer,
      final JavaType serType,
      final boolean suppressNulls,
      final Object suppressableValue,
      final Class<?>[] includeInViews) {
    super(
        propDef,
        member,
        contextAnnotations,
        declaredType,
        ser,
        typeSer,
        serType,
        suppressNulls,
        suppressableValue,
        includeInViews);
    this.feature = propDef.getFeature();
  }

  @Override
  public void assignSerializer(final JsonSerializer<Object> ser) {
    // Instead of using the UnknownSerializer, let's force dynamic serializer lookup
    if (!(ser instanceof UnknownSerializer)) {
      super.assignSerializer(ser);
    }
  }

  @Override
  public void fixAccess(final SerializationConfig config) {}

  /*
  /***********************************************************
  /* PropertyWriter methods (serialization)
  /***********************************************************
   */

  @Override
  public void serializeAsField(
      final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
    final var eObject = (EObject) bean;
    final var value = eObject.eGet(feature);

    // Coped from super.serializeAsField, as get() is neither called nor overridable :-(
    // Null handling is bit different, check that first
    if (value == null) {
      if (_nullSerializer != null) {
        gen.writeFieldName(_name);
        _nullSerializer.serialize(null, gen, prov);
      }
      return;
    }
    // then find serializer to use
    final var ser = findSerializer(value, prov);
    // and then see if we must suppress certain values (default, empty)
    if (_suppressableValue != null) {
      if (MARKER_FOR_DEFAULT == _suppressableValue) {
        if (!eObject.eIsSet(feature)) {
          return;
        }
      } else if (MARKER_FOR_EMPTY == _suppressableValue) {
        if (ser.isEmpty(prov, value)) {
          return;
        }
      } else if (_suppressableValue.equals(value)) {
        return;
      }
    }
    // For non-nulls: simple check for direct cycles
    if (value == bean) {
      // four choices: exception; handled by call; pass-through or write null
      if (_handleSelfReference(bean, gen, prov, ser)) {
        return;
      }
    }
    gen.writeFieldName(_name);
    if (_typeSerializer != null) {
      ser.serializeWithType(value, gen, prov, _typeSerializer);
    } else {
      ser.serialize(value, gen, prov);
    }
  }

  @Override
  public void serializeAsElement(
      final Object bean, final JsonGenerator gen, final SerializerProvider prov) throws Exception {
    final var eObject = (EObject) bean;
    final var value = eObject.eGet(feature);

    // Coped from super.serializeAsElement, as get() is neither called nor overridable :-(
    if (value == null) { // nulls need specialized handling
      if (_nullSerializer != null) {
        _nullSerializer.serialize(null, gen, prov);
      } else { // can NOT suppress entries in tabular output
        gen.writeNull();
      }
      return;
    }
    // otherwise find serializer to use
    final var ser = findSerializer(value, prov);
    // and then see if we must suppress certain values (default, empty)
    if (_suppressableValue != null) {
      if (MARKER_FOR_DEFAULT == _suppressableValue) {
        if (!eObject.eIsSet(feature)) {
          return;
        }
      } else if (MARKER_FOR_EMPTY == _suppressableValue) {
        if (ser.isEmpty(prov, value)) { // can NOT suppress entries in
          // tabular output
          serializeAsPlaceholder(bean, gen, prov);
          return;
        }
      } else if (_suppressableValue.equals(value)) {
        // can NOT suppress entries in tabular output
        serializeAsPlaceholder(bean, gen, prov);
        return;
      }
    }
    // For non-nulls: simple check for direct cycles
    if (value == bean) {
      if (_handleSelfReference(bean, gen, prov, ser)) {
        return;
      }
    }
    if (_typeSerializer == null) {
      ser.serialize(value, gen, prov);
    } else {
      ser.serializeWithType(value, gen, prov, _typeSerializer);
    }
  }

  /*
  /**********************************************************
  /* Helper methods
  /**********************************************************
   */

  protected JsonSerializer<Object> findSerializer(final Object value, final SerializerProvider prov)
      throws JsonMappingException {
    if (_serializer != null) return _serializer;

    if (value instanceof final EObject eObject) {
      final var typeFactory = (ETypeFactory) prov.getTypeFactory();
      final var type = typeFactory.constructEClassType(eObject.eClass());
      return prov.findValueSerializer(type);
    }

    final Class<?> cls = value.getClass();
    final PropertySerializerMap map = _dynamicSerializers;
    final var ser = map.serializerFor(cls);
    return ser != null ? ser : _findAndAddDynamic(map, cls, prov);
  }
}
