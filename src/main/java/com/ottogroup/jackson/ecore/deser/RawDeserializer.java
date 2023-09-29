package com.ottogroup.jackson.ecore.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import java.io.IOException;
import java.io.Serial;
import java.io.StringWriter;

/**
 * A {@link com.fasterxml.jackson.databind.JsonDeserializer} to deserialize values into raw JSON
 * strings.
 *
 * <p>The current token will be read as a tree and re-serialized as JSON string.
 */
public class RawDeserializer extends StringDeserializer {

  @Serial private static final long serialVersionUID = 1L;

  @Override
  public String deserialize(final JsonParser p, final DeserializationContext ctxt)
      throws IOException {
    final var writer = new StringWriter();
    final var generator = p.getCodec().getFactory().createGenerator(writer);
    final var tree = p.readValueAsTree();
    generator.writeTree(tree);

    return writer.toString();
  }
}
