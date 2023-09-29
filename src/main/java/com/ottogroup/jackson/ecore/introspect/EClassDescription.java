package com.ottogroup.jackson.ecore.introspect;

import static java.util.function.Predicate.not;

import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.NamingBase;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.PropertyNamingStrategyBase;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.ottogroup.jackson.ecore.type.EClassType;
import com.ottogroup.jackson.ecore.type.ETypeFactory;
import java.util.List;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMapUtil;

public class EClassDescription extends BasicBeanDescription {

  final EClass eClass;

  public EClassDescription(
      final EClassType type, final MapperConfig<?> cfg, final AnnotatedClass classInfo) {
    super(cfg, type, classInfo, null);
    this.eClass = type.getEClass();
  }

  public EClass getEClass() {
    return eClass;
  }

  @Override
  public EClassType getType() {
    return (EClassType) super.getType();
  }

  @Override
  public Object instantiateBean(final boolean fixAccess) {
    return EcoreUtil.create(eClass);
  }

  @Override
  public Value findExpectedFormat(final Value defValue) {
    if (ETypeFactory.isMapEntryLike(eClass)
        && eClass.getEStructuralFeature("key") instanceof EReference) {
      return Value.forShape(Shape.OBJECT);
    }

    return super.findExpectedFormat(defValue);
  }

  @Override
  protected synchronized List<BeanPropertyDefinition> _properties() {
    if (_properties == null) {
      final var typeFactory = (ETypeFactory) _config.getTypeFactory();
      final var annotationIntrospector = _config.getAnnotationIntrospector();
      _properties =
          eClass.getEAllStructuralFeatures().stream()
              .filter(this::isSerializable)
              .map(
                  feat ->
                      new FeaturePropertyDefinition(
                          annotationIntrospector,
                          typeFactory.constructType(
                              eClass.getFeatureType(feat).getERawType(), feat.isMany()),
                          feat))
              .filter(not(def -> annotationIntrospector.hasIgnoreMarker(def.getPrimaryMember())))
              .map(def -> rename(def, _config.getPropertyNamingStrategy()))
              .toList();
    }

    return _properties;
  }

  /*
  /**********************************************************
  /* Helper methods for field introspection
  /**********************************************************
   */

  protected boolean isSerializable(final EStructuralFeature feature) {
    if (feature.isTransient() || FeatureMapUtil.isFeatureMap(feature)) return false;

    if (feature instanceof final EReference reference) {
      final var opposite = reference.getEOpposite();
      return opposite == null || !opposite.isContainment();
    }

    return true;
  }

  @SuppressWarnings("deprecation")
  protected BeanPropertyDefinition rename(
      final BeanPropertyDefinition propDef, final PropertyNamingStrategy namingStrategy) {
    if (namingStrategy instanceof final NamingBase naming) {
      return propDef.withSimpleName(naming.translate(propDef.getName()));
    } else if (namingStrategy instanceof final PropertyNamingStrategyBase naming) {
      return propDef.withSimpleName(naming.translate(propDef.getName()));
    }

    return propDef;
  }

  /*
  /**********************************************************
  /* Standard methods
  /**********************************************************
   */

  @Override
  public String toString() {
    return "EClassDescription (eClass: %s)".formatted(eClass.getName());
  }
}
