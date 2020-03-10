package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.FontStyle;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class PrintableTextDeserializer extends JsonDeserializer<PrintableText> {

  private static final class LiteralText implements PrintableText {
    private final String text;

    public LiteralText(String text) {
      this.text = text;
    }

    @Override
    public JsonNode asJson() {
      return JsonNodeFactory.instance.textNode(text);
    }

    @Override
    public Pair<FontStyle, String> line(Barcodable barcodable) {
      return new Pair<>(FontStyle.REGULAR, text);
    }

    @Override
    public Stream<Pair<FontStyle, String>> lines(Barcodable barcodable) {
      return Stream.of(line(barcodable));
    }

    @Override
    public String text(Barcodable barcodable) {
      return text;
    }
  }

  private static final class PrintableGroup implements PrintableText {
    private final List<PrintableText> fields;

    private PrintableGroup(List<PrintableText> fields) {
      this.fields = fields;
    }

    @Override
    public JsonNode asJson() {
      final ArrayNode results = JsonNodeFactory.instance.arrayNode();
      for (PrintableText field : fields) {
        results.add(field.asJson());
      }
      return results;
    }

    @Override
    public Pair<FontStyle, String> line(Barcodable barcodable) {
      return new Pair<>(FontStyle.REGULAR, text(barcodable));
    }

    @Override
    public Stream<Pair<FontStyle, String>> lines(Barcodable barcodable) {
      return fields.stream().map(field -> field.line(barcodable));
    }

    @Override
    public String text(Barcodable barcodable) {
      return fields.stream().map(field -> field.text(barcodable)).filter(s -> !LimsUtils.isStringBlankOrNull(s))
          .collect(Collectors.joining(" "));
    }
  }

  private PrintableText deserialize(JsonNode node) {
    if (node.isTextual()) {
      return new LiteralText(node.asText());
    }
    if (node.isArray()) {
      final List<PrintableText> fields = new ArrayList<>();
      for (final JsonNode inner : node) {
        fields.add(deserialize(inner));
      }
      return new PrintableGroup(fields);
    }
    if (node.isObject() && node.has("use")) {
      final PrintableField field = PrintableField.valueOf(node.get("use").asText());
      return field == null ? PrintableText.NULL : field;
    }
    if (node.isObject()) {
      final Map<Barcodable.EntityType, PrintableText> options = new HashMap<>();
      for (Barcodable.EntityType e : Barcodable.EntityType.values()) {
        if (node.has(e.name())) {
          options.put(e, deserialize(node.get(e.name())));
        }
      }
      if (!options.isEmpty()) {
        return new TypeAlternate(options);
      }
    }
    return PrintableText.NULL;
  }

  @Override
  public PrintableText deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
    final ObjectCodec oc = parser.getCodec();
    final JsonNode node = oc.readTree(parser);
    return deserialize(node);
  }

}
