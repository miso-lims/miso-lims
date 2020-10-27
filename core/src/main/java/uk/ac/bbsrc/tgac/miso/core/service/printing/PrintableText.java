package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.FontStyle;

@JsonDeserialize(using = PrintableTextDeserializer.class)
@JsonSerialize(using = PrintableTextSerializer.class)
public interface PrintableText {
  static PrintableText NULL = new PrintableText() {

    @Override
    public JsonNode asJson() {
      return JsonNodeFactory.instance.nullNode();
    }

    @Override
    public void asJson(JsonGenerator generator) throws IOException, JsonProcessingException {
      generator.writeNull();
    }

    @Override
    public Pair<FontStyle, String> line(Barcodable barcodable) {
      return null;
    }

    @Override
    public Stream<Pair<FontStyle, String>> lines(Barcodable barcodable) {
      return Stream.empty();
    }

    @Override
    public String text(Barcodable barcodable) {
      return null;
    }
  };

  JsonNode asJson();

  void asJson(JsonGenerator generator) throws IOException, JsonProcessingException;

  Pair<LabelCanvas.FontStyle, String> line(Barcodable barcodable);

  Stream<Pair<LabelCanvas.FontStyle, String>> lines(Barcodable barcodable);

  String text(Barcodable barcodable);
}
