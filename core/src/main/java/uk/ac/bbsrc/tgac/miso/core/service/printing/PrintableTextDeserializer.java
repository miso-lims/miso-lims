package uk.ac.bbsrc.tgac.miso.core.service.printing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Pair;
import uk.ac.bbsrc.tgac.miso.core.service.printing.LabelCanvas.FontStyle;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class PrintableTextDeserializer extends ValueDeserializer<PrintableText> {

  private static final class LiteralText implements PrintableText {
    private final String text;

    public LiteralText(String text) {
      this.text = text;
    }

    @Override
    public JsonNode asJson() {
      return JsonNodeFactory.instance.stringNode(text);
    }

    @Override
    public void asJson(JsonGenerator generator) {
      generator.writeString(text);
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
    public void asJson(JsonGenerator generator) {
      generator.writeStartArray();
      for (final PrintableText field : fields) {
        field.asJson(generator);
      }
      generator.writeEndArray();
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
    if (node.isString()) {
      return new LiteralText(node.asString());
    }
    if (node.isArray()) {
      final List<PrintableText> fields = new ArrayList<>();
      for (final JsonNode inner : node) {
        fields.add(deserialize(inner));
      }
      return new PrintableGroup(fields);
    }
    if (node.isObject() && node.has("use")) {
      final PrintableField field = PrintableField.valueOf(node.get("use").asString());
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
  public PrintableText deserialize(JsonParser parser, DeserializationContext context) {
    final JsonNode node = context.readTree(parser);
    return deserialize(node);
  }

}
