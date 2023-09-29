package com.ottogroup.jackson.ecore;

import static org.eclipse.emf.ecore.EcorePackage.Literals.EOBJECT;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ottogroup.jackson.ecore.deser.EDeserializationProblemHandler;
import com.ottogroup.jackson.ecore.deser.EDeserializers;
import com.ottogroup.jackson.ecore.deser.EInstantiators;
import com.ottogroup.jackson.ecore.introspect.EClassIntrospector;
import com.ottogroup.jackson.ecore.ser.ESerializerFactory;
import com.ottogroup.jackson.ecore.ser.ESerializers;
import com.ottogroup.jackson.ecore.type.ETypeFactory;
import com.ottogroup.jackson.ecore.type.ETypeResolverBuilder;
import com.ottogroup.jackson.ecore.type.URITypeIdResolver;

public class EcoreModule extends Module {

  public static final String DEFAULT_TYPE_PROPERTY_NAME = "eClass";

  @Override
  public String getModuleName() {
    return "jackson-datatype-ecore";
  }

  @Override
  public Version version() {
    return Version.unknownVersion();
  }

  @Override
  public void setupModule(final SetupContext context) {
    final var mapper = context.<ObjectMapper>getOwner();

    // Deserialization
    context.addDeserializers(new EDeserializers());
    context.addValueInstantiators(new EInstantiators());
    context.addDeserializationProblemHandler(new EDeserializationProblemHandler());

    // Serialization
    mapper.setSerializerFactory(new ESerializerFactory());
    context.addSerializers(new ESerializers());

    // Typing
    final var typeFactory = (ETypeFactory) mapper.getTypeFactory();
    final var typeResolverBaseType = typeFactory.constructEClassType(EOBJECT);
    final var idRes = new URITypeIdResolver(typeResolverBaseType, typeFactory);
    mapper.setDefaultTyping(
        new ETypeResolverBuilder(typeResolverBaseType)
            .init(idRes.getMechanism(), idRes)
            .inclusion(JsonTypeInfo.As.PROPERTY)
            .typeProperty(DEFAULT_TYPE_PROPERTY_NAME));
    context.setClassIntrospector(new EClassIntrospector());
    context.insertAnnotationIntrospector(new EAnnotationIntrospector(version()));
  }
}
