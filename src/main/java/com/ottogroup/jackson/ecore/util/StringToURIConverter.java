package com.ottogroup.jackson.ecore.util;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.eclipse.emf.common.util.URI;

public class StringToURIConverter extends StdConverter<String, URI> {

  @Override
  public URI convert(final String value) {
    return URI.createURI(value);
  }
}
