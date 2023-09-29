package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import java.io.Serial;
import java.lang.annotation.Annotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A {@link SettableBeanProperty} that uses the {@link EObject#eSet(EStructuralFeature, Object)}
 * method instead of reflection.
 */
public class SettableFeatureProperty extends SettableBeanProperty {

  @Serial private static final long serialVersionUID = 1L;

  final EClass eClass;
  final EStructuralFeature feature;
  final AnnotatedMember annotated;

  public SettableFeatureProperty(
      final JavaType type,
      final EClass eClass,
      final EStructuralFeature feature,
      final AnnotatedMember annotated,
      final TypeDeserializer typeDeserializer) {
    super(PropertyName.construct(feature.getName()), type, null, typeDeserializer, null, null);
    this.eClass = eClass;
    this.feature = feature;
    this.annotated = annotated;
  }

  SettableFeatureProperty(
      final SettableFeatureProperty src,
      final JsonDeserializer<?> valueDeserializer,
      final NullValueProvider nuller) {
    super(src, valueDeserializer, nuller);
    this.eClass = src.eClass;
    this.feature = src.feature;
    this.annotated = src.annotated;
  }

  SettableFeatureProperty(final SettableFeatureProperty src, final PropertyName newName) {
    super(src, newName);
    this.eClass = src.eClass;
    this.feature = src.feature;
    this.annotated = src.annotated;
  }

  @Override
  public SettableFeatureProperty withValueDeserializer(final JsonDeserializer<?> deser) {
    return new SettableFeatureProperty(this, deser, _nullProvider);
  }

  @Override
  public SettableBeanProperty withName(final PropertyName newName) {
    return new SettableFeatureProperty(this, newName);
  }

  @Override
  public SettableBeanProperty withNullProvider(final NullValueProvider nva) {
    return new SettableFeatureProperty(this, _valueDeserializer, nva);
  }

  @Override
  public AnnotatedMember getMember() {
    return annotated;
  }

  @Override
  public <A extends Annotation> A getAnnotation(final Class<A> acls) {
    // TODO: implement
    return null;
  }

  @Override
  public void deserializeAndSet(
      final JsonParser p, final DeserializationContext ctxt, final Object instance)
      throws IOException {
    if (p.hasToken(JsonToken.VALUE_NULL)) {
      return;
    }

    if (feature.isMany()) {
      deserializeAndSetInto(p, ctxt, instance);
    } else {
      deserializeAndSetDefault(p, ctxt, instance);
    }
  }

  void deserializeAndSetInto(
      final JsonParser p, final DeserializationContext ctxt, final Object instance)
      throws IOException {
    assert instance instanceof EObject : "Expected EObject, got " + instance.getClass();
    final var eObject = (EObject) instance;
    try {
      final var intoValue = eObject.eGet(feature);
      if (_valueTypeDeserializer != null) {
        _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer, intoValue);
      } else {
        _valueDeserializer.deserialize(p, ctxt, intoValue);
      }
    } catch (final Exception e) {
      throw JsonMappingException.wrapWithPath(e, instance, getName());
    }
  }

  void deserializeAndSetDefault(
      final JsonParser p, final DeserializationContext ctxt, final Object instance)
      throws IOException {
    final Object value;
    try {
      if (_valueTypeDeserializer != null) {
        value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
      } else {
        value = _valueDeserializer.deserialize(p, ctxt);
      }
    } catch (final Exception e) {
      throw JsonMappingException.wrapWithPath(e, instance, getName());
    }

    try {
      set(instance, value);
    } catch (final Exception e) {
      _throwAsIOE(p, e, value);
    }
  }

  @Override
  public Object deserializeSetAndReturn(
      final JsonParser p, final DeserializationContext ctxt, final Object instance)
      throws IOException {
    deserializeAndSet(p, ctxt, instance);
    return instance;
  }

  @Override
  public void set(final Object instance, final Object value) {
    assert instance instanceof EObject : "Expected EObject, got " + instance.getClass();
    final var eObject = (EObject) instance;
    eObject.eSet(feature, value);
  }

  @Override
  public Object setAndReturn(final Object instance, final Object value) {
    set(instance, value);
    return instance;
  }
}
