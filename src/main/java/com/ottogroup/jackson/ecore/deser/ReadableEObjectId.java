package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import java.io.IOException;

public class ReadableEObjectId extends ReadableObjectId {

  public ReadableEObjectId(final IdKey key) {
    super(key);
  }

  @Override
  public boolean tryToResolveUnresolved(final DeserializationContext ctxt) {
    final var resolved = resolve();
    if (resolved != null) {
      try {
        bindItem(resolved);
        return true;
      } catch (final IOException e) {
        throw new RuntimeException("Error handling resolved forward reference", e);
      }
    }

    return super.tryToResolveUnresolved(ctxt);
  }
}
