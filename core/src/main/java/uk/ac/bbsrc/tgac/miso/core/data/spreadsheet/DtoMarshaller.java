package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Utility to make easy and consistent conversions between objects and spreadsheet rows.
 *
 * @param <T> The object type to convert from/to
 */
public class DtoMarshaller<T> implements Iterable<String> {
  private final List<Function<T, String>> getters = new ArrayList<>();
  private final List<String> names = new ArrayList<>();

  private final Supplier<T> newFunction;

  private final List<BiConsumer<T, String>> setters = new ArrayList<>();
  private final Consumer<String> errorHandler;

  /**
   * Create a new marshaller.
   * 
   * @param newFunction The constructor function for the DTO (i.e., {@code T::new})
   * @param errorHandler A function to collect any parsing errors.
   */
  public DtoMarshaller(Supplier<T> newFunction, Consumer<String> errorHandler) {
    this.newFunction = newFunction;
    this.errorHandler = errorHandler;
  }

  /**
   * Add a new column in the conversion. This will probably be {@code add("Foo", T::getFoo, T::setFoo);}
   * 
   * @param name the column name as it will appear in the spreadsheet
   * @param get The getter for this field
   * @param set The setter for this field
   */
  public void add(String name, Function<T, String> get, BiConsumer<T, String> set) {
    names.add(name);
    getters.add(get);
    setters.add(set);
  }

  public void addBoolean(String name, Function<T, Boolean> get, BiConsumer<T, Boolean> set) {
    add(name, item -> {
      Boolean value = get.apply(item);
      return value == null ? null : value.toString();
    }, (item, value) -> {
      Boolean result = null;
      if (!LimsUtils.isStringBlankOrNull(value)) {
        result = Boolean.parseBoolean(value);
      }
      set.accept(item, result);
    });
  }

  public void addInt(String name, Function<T, Integer> get, BiConsumer<T, Integer> set) {
    add(name, item -> {
      Integer value = get.apply(item);
      return value == null ? null : value.toString();
    }, (item, value) -> {
      Integer result = null;
      if (!LimsUtils.isStringBlankOrNull(value)) {
        try {
          result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
          errorHandler.accept(String.format("Failed to parse integer “%s” in column %s.", value, name));
        }
      }
      set.accept(item, result);
    });
  }

  public void addLong(String name, Function<T, Long> get, BiConsumer<T, Long> set) {
    add(name, item -> {
      Long value = get.apply(item);
      return value == null ? null : value.toString();
    }, (item, value) -> {
      Long result = null;
      if (!LimsUtils.isStringBlankOrNull(value)) {
        try {
          result = Long.parseLong(value);
        } catch (NumberFormatException e) {
          errorHandler.accept(String.format("Failed to parse long “%s” in column %s.", value, name));
        }
      }
      set.accept(item, result);
    });
  }

  /**
   * Convert an object to a spreadsheet row.
   * 
   * This will probably be invoked in a stream:
   * {@code sourceList.asStream().map(marshaller::fromDto).collect(new ExcelCollector(marshaller))}
   */
  public String[] fromDto(T item) {
    String[] row = new String[getters.size()];
    for (int i = 0; i < row.length; i++) {
      row[i] = getters.get(i).apply(item);
    }
    return row;
  }

  @Override
  public Iterator<String> iterator() {
    return names.iterator();
  }

  /**
   * Convert a spreadsheet row into an object. If there are not enough columns provided, they are treated as if they were blank.
   * 
   * This will probably be invoked in a stream:
   * {@code ExcelReader.open(somefile).map(marshaller::toDto).collect(Collectors.toList())}
   */
  public T toDto(String[] row) {
    T dto = newFunction.get();
    for (int i = 0; i < Math.min(row.length, setters.size()); i++) {
      setters.get(i).accept(dto, row[i]);
    }
    for (int i = row.length; i < setters.size(); i++) {
      setters.get(i).accept(dto, null);
    }
    return dto;
  }
}
