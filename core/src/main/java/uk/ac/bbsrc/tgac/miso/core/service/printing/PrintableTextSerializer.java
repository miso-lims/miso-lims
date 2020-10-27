package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class PrintableTextSerializer extends JsonSerializer<PrintableText> {

  @Override
  public void serialize(PrintableText text, JsonGenerator generator, SerializerProvider provider)
      throws IOException, JsonProcessingException {
    text.asJson(generator);
  }

}
