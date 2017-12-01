package uk.ac.bbsrc.tgac.miso.webapp.util;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JsonArrayCollector implements Collector<java.lang.String, StringBuilder, java.lang.String> {

  @Override
  public BiConsumer<StringBuilder, String> accumulator() {
    return (sb, metrics) -> {
      if (sb.length() == 0) {
        sb.append(metrics);
      } else {
        sb.append(".concat(");
        sb.append(metrics);
        sb.append(")");
      }
    };
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Collections.emptySet();
  }

  @Override
  public BinaryOperator<StringBuilder> combiner() {
    return (primary, secondary) -> {
      if (primary.length() == 0 && secondary.length() == 0) {
        return primary;
      }
      if (secondary.length() == 0) {
        return primary;
      }
      if (primary.length() == 0) {
        return secondary;
      }
      return primary.append(".concat(").append(secondary).append(")");
    };

  }

  @Override
  public Function<StringBuilder, String> finisher() {
    return sb -> sb.length() == 0 ? "[]" : sb.toString();
  }

  @Override
  public Supplier<StringBuilder> supplier() {
    return StringBuilder::new;
  }

}
