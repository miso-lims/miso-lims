package uk.ac.bbsrc.tgac.miso.core.service.printing;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public final class PrintableTextSerializer extends ValueSerializer<PrintableText> {

  @Override
  public void serialize(PrintableText text, JsonGenerator generator, SerializationContext ctxt) {
    text.asJson(generator);
  }

}
