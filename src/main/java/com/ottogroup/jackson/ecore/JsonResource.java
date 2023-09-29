package com.ottogroup.jackson.ecore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

public class JsonResource extends ResourceImpl {

  protected final ObjectReader reader;
  protected final ObjectWriter writer;

  public JsonResource(final URI uri, final ObjectReader reader, final ObjectWriter writer) {
    super(uri);
    this.reader = reader;
    this.writer = writer;

    this.defaultSaveOptions = Map.of();
    this.defaultLoadOptions = Map.of();
    this.defaultDeleteOptions = Map.of();
  }

  /**
   * Loads the resource from the given JSON tree using the specified options.
   *
   * @param node JSON contents to load
   * @param options the load options
   * @throws IOException in case of loading errors
   */
  public void load(final JsonNode node, final Map<?, ?> options) throws IOException {
    if (isLoaded) return;
    isLoading = true;

    if (errors != null) errors.clear();
    if (warnings != null) warnings.clear();

    try {
      doLoad(node, mergeMaps(options, defaultLoadOptions));
    } finally {
      isLoading = false;
      final var notification = setLoaded(true);
      if (notification != null) eNotify(notification);
      setModified(false);
    }
  }

  @Override
  protected void doLoad(final InputStream inputStream, final Map<?, ?> options) throws IOException {
    if (inputStream instanceof final URIConverter.Loadable loadable) {
      loadable.loadResource(this);
      return;
    }

    final var configured = options != null ? reader.withAttributes(options) : reader;
    configured.withValueToUpdate(this).readValue(inputStream);
  }

  protected void doLoad(final JsonNode node, final Map<?, ?> options) throws IOException {
    final var configured = options != null ? reader.withAttributes(options) : reader;
    configured.withValueToUpdate(this).readValue(node);
  }

  @Override
  protected void doSave(final OutputStream outputStream, final Map<?, ?> options)
      throws IOException {
    if (outputStream instanceof final URIConverter.Saveable saveable) {
      saveable.saveResource(this);
      return;
    }

    final var configured = options != null ? writer.withAttributes(options) : writer;
    configured.writeValue(outputStream, this);
  }
}
