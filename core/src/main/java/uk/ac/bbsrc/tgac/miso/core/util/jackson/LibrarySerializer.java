package uk.ac.bbsrc.tgac.miso.core.util.jackson;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import uk.ac.bbsrc.tgac.miso.core.data.Library;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 03/01/13
 * @since 0.1.9
 */
public class LibrarySerializer extends JsonSerializer<Library> {
  @Override
  public void serialize(Library library, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeEndObject();
  }
}
