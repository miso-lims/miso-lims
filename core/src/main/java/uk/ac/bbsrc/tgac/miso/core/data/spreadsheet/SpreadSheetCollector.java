package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.ByteArrayOutputStream;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collect a stream of string rows into a spreadsheet.
 */
public abstract class SpreadSheetCollector<T, Workbook, Sheet, Row>
    implements Collector<T, SpreadSheetCollector.State<Sheet, Workbook>, byte[]> {
  public static class State<Sheet, Workbook> {
    int currentRow = 1;
    Sheet sheet;
    Workbook workbook;
  }

  private final static Logger log = LoggerFactory.getLogger(SpreadSheetCollector.class);

  private final List<Column<T>> columns;

  /**
   * Create a new collector.
   * 
   * @param columns The names of the column headers.
   */
  public SpreadSheetCollector(List<Column<T>> columns) {
    this.columns = columns;
  }

  @Override
  public BiConsumer<State<Sheet, Workbook>, T> accumulator() {
    return (state, item) -> {
      if (state == null) return;
      Row row = createRow(state.workbook, state.sheet, state.currentRow++);
      for (int i = 0; i < columns.size(); i++) {
        setCell(state.workbook, state.sheet, row, i, columns.get(i), item);
      }
    };
  }

  @Override
  public Set<java.util.stream.Collector.Characteristics> characteristics() {
    return EnumSet.noneOf(Collector.Characteristics.class);
  }

  @Override
  public BinaryOperator<State<Sheet, Workbook>> combiner() {
    return (left, right) -> {
      throw new UnsupportedOperationException();
    };
  }

  /**
   * Append a new row to the sheet.
   * 
   * @param index The expected index of the row (including the header row).
   */
  protected abstract Row createRow(Workbook workbook, Sheet sheet, int index);

  /**
   * Create a new empty workbook.
   */
  protected abstract Workbook createWorkbook() throws Exception;

  @Override
  public Function<State<Sheet, Workbook>, byte[]> finisher() {
    return state -> {
      if (state == null) return null;
      try {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        write(state.workbook, output);
        return output.toByteArray();
      } catch (Exception e) {
        log.error("Cannot save workbook", e);
        return null;
      }

    };
  }

  /**
   * Create a new empty sheet in a workbook.
   */
  protected abstract Sheet getSheet(Workbook workbook);

  protected abstract void setCell(Workbook workbook, Sheet sheet, Row row, int i, Column<T> column, T item);

  protected abstract void setCell(Workbook workbook, Sheet sheet, Row row, int i, String string);

  @Override
  public Supplier<State<Sheet, Workbook>> supplier() {
    return () -> {
      try {
        State<Sheet, Workbook> state = new State<>();
        state.workbook = createWorkbook();
        state.sheet = getSheet(state.workbook);
        Row row = createRow(state.workbook, state.sheet, 0);
        int col = 0;
        for (Column<T> column : columns) {
          setCell(state.workbook, state.sheet, row, col++, column.name());
        }
        return state;
      } catch (Exception e) {
        log.error("Cannot create workbook", e);
        return null;
      }
    };
  }

  /**
   * Write a workbook to a file.
   */
  protected abstract void write(Workbook workbook, ByteArrayOutputStream output) throws Exception;

}
