package com.ottogroup.jackson.ecore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

public class JsonResourceFactory implements Resource.Factory {

  protected final ObjectMapper mapper;
  protected final ObjectReader reader;
  protected final ObjectWriter writer;

  public JsonResourceFactory(final EPackage.Registry packageRegistry) {
    this(new EcoreMapper(packageRegistry));
  }

  public JsonResourceFactory(final EcoreMapper mapper) {
    this.mapper = mapper;
    reader = mapper.readerFor(Resource.class);
    writer = mapper.writerFor(Resource.class);
  }

  @Override
  public Resource createResource(final URI uri) {
    return new JsonResource(uri, reader, writer);
  }

  public ObjectMapper getMapper() {
    return mapper;
  }
}
