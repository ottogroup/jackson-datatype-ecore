package com.ottogroup.jackson.ecore.type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ClassStack;
import com.fasterxml.jackson.databind.type.TypeBase;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.LookupCache;
import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class ETypeFactory extends TypeFactory {

  @Serial private static final long serialVersionUID = 1L;

  static final Set<Class<?>> NATIVE_DATA_TYPES;

  static {
    NATIVE_DATA_TYPES =
        Set.of(
            Boolean.class,
            BigDecimal.class,
            BigInteger.class,
            Byte.class,
            Character.class,
            Double.class,
            Float.class,
            Integer.class,
            Long.class,
            Object.class,
            Short.class,
            String.class);
  }

  final Map<Class<?>, EClass> knownClasses;

  public ETypeFactory(final EPackage.Registry packageRegistry) {
    super((LookupCache<Object, JavaType>) null);
    knownClasses =
        packageRegistry.values().stream()
            .map(obj -> (EPackage) obj)
            .flatMap(pack -> pack.getEClassifiers().stream())
            .filter(eClassifier -> eClassifier instanceof EClass)
            .map(eClassifier -> (EClass) eClassifier)
            .collect(Collectors.toUnmodifiableMap(this::getInstanceClass, Function.identity()));
  }

  /*
  /**********************************************************
  /* Actual type resolution, traversal
  /**********************************************************
   */

  @Override
  protected JavaType _fromClass(
      final ClassStack context, final Class<?> rawType, final TypeBindings bindings) {
    final var javaType = super._fromClass(context, rawType, bindings);

    if (EObject.class.isAssignableFrom(rawType)
        && !(javaType instanceof EClassType)
        && !isEObjectImpl(rawType)) {
      final var eClass = findEClass(rawType);
      final var eClassType = new EClassType(eClass, (TypeBase) javaType);
      _typeCache.put(rawType, eClassType);
      return eClassType;
    }

    return javaType;
  }

  public JavaType constructType(final EClassifier classifier, final boolean isMany) {
    final JavaType simpleType;

    if (classifier instanceof final EClass eClass) {
      if (isMany && isMapEntryLike(eClass)) {
        return constructEMapType(eClass);
      }
      simpleType = constructEClassType(eClass);
    } else {
      final var eDataType = (EDataType) classifier;
      simpleType = constructEDataTypeType(eDataType);
    }

    if (isMany) {
      return constructCollectionType(EList.class, simpleType);
    } else {
      final var instanceClass = getInstanceClass(classifier);
      if (instanceClass.isArray()) {
        return constructArrayType(instanceClass.componentType());
      }
    }

    return simpleType;
  }

  /*
  /**********************************************************
  /* Direct factory methods
  /**********************************************************
   */

  public EClassType constructEClassType(final EClass eClass) {
    final var base = super._fromClass(null, getInstanceClass(eClass), EMPTY_BINDINGS);
    return new EClassType(eClass, (TypeBase) base);
  }

  public JavaType constructEDataTypeType(final EDataType eDataType) {
    final var instanceClass = getInstanceClass(eDataType);
    final var base = (TypeBase) constructType(instanceClass);
    if (instanceClass.isPrimitive() || NATIVE_DATA_TYPES.contains(instanceClass)) {
      return base;
    }

    return new EDataTypeType(eDataType, base);
  }

  public JavaType constructEMapType(final EClass entryEClass) {
    final var keyFeature = entryEClass.getEStructuralFeature("key");
    if (keyFeature instanceof EReference) {
      return constructCollectionType(EList.class, constructEClassType(entryEClass));
    }

    final var keyClassifier = entryEClass.getFeatureType(keyFeature).getERawType();
    final var keyType = constructType(keyClassifier, false);
    final var valueFeature = entryEClass.getEStructuralFeature("value");
    final var valueClassifier = entryEClass.getFeatureType(valueFeature).getERawType();
    final var valueType = constructType(valueClassifier, false);
    return constructMapLikeType(EMap.class, keyType, valueType);
  }

  /*
  /**********************************************************
  /* Low-level helper methods
  /**********************************************************
   */

  public EClass findEClass(final Class<?> instanceClass) {
    var known = knownClasses.get(instanceClass);
    if (known != null) return known;

    for (final var iface : instanceClass.getInterfaces()) {
      known = knownClasses.get(iface);
      if (known != null) return known;
    }

    throw new RuntimeException("Cannot find EClass for Java class %s".formatted(instanceClass));
  }

  public Class<?> getInstanceClass(final EClassifier eClassifier) {
    final var instanceClass = eClassifier.getInstanceClass();
    if (instanceClass == Map.Entry.class && eClassifier instanceof final EClass eClass) {
      return EcoreUtil.create(eClass).getClass();
    }
    if (instanceClass != null) {
      return instanceClass;
    }

    final var instanceClassName = eClassifier.getInstanceClassName();
    if (instanceClassName != null) {
      try {
        return findClass(instanceClassName);
      } catch (final ClassNotFoundException e) {
        throw new RuntimeException("Cannot find instance class of " + eClassifier, e);
      }
    }

    if (eClassifier instanceof EClass) {
      return DynamicEObjectImpl.class;
    }
    if (eClassifier instanceof EEnum) {
      return Enumerator.class;
    }

    return Object.class;
  }

  public static boolean isMapEntryLike(final EClass eClass) {
    return Map.Entry.class.getName().equals(eClass.getInstanceClassName())
        && eClass.getEStructuralFeature("key") != null
        && eClass.getEStructuralFeature("value") != null;
  }

  /**
   * Checks whether the given class is a raw implementation class for {@link EObject} and does not
   * resemble a specific model {@code EClass}.
   *
   * @param cls a class
   * @return {@code true}, when the given class is a raw {@link EObject} implementation class
   */
  protected static boolean isEObjectImpl(final Class<?> cls) {
    return cls == EObject.class
        || cls == BasicEObjectImpl.class
        || cls == MinimalEObjectImpl.class
        || cls == MinimalEObjectImpl.Container.class
        || cls == MinimalEObjectImpl.Container.Dynamic.class
        || cls == DynamicEObjectImpl.class;
  }
}
