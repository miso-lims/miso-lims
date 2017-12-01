package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.ByteArrayOutputStream;
import java.util.EnumSet;
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
public abstract class SpreadSheetCollector<Workbook, Sheet, Row>
    implements Collector<String[], SpreadSheetCollector<Workbook, Sheet, Row>.State, byte[]> {
  public class State {
    int currentRow = 1;
    Sheet sheet;
    Workbook workbook;
  }

  private final static Logger log = LoggerFactory.getLogger(SpreadSheetCollector.class);

  private final Iterable<String> columns;

  /**
   * Create a new collector.
   * 
   * @param columns The names of the column headers.
   */
  public SpreadSheetCollector(Iterable<String> columns) {
    this.columns = columns;
  }

  @Override
  public BiConsumer<State, String[]> accumulator() {
    return (state, contents) -> {
      if (state == null) return;
      Row row = createRow(state.workbook, state.sheet, state.currentRow++);
      for (int i = 0; i < contents.length; i++) {
        setCell(state.workbook, state.sheet, row, i, contents[i]);
      }
    };
  }

  @Override
  public Set<java.util.stream.Collector.Characteristics> characteristics() {
    return EnumSet.noneOf(Collector.Characteristics.class);
  }

  @Override
  public BinaryOperator<State> combiner() {
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
  public Function<State, byte[]> finisher() {
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

  protected abstract void setCell(Workbook workbook, Sheet sheet, Row row, int i, String string);

  @Override
  public Supplier<State> supplier() {
    return () -> {
      try {
        State state = new State();
        state.workbook = createWorkbook();
        state.sheet = getSheet(state.workbook);
        Row row = createRow(state.workbook, state.sheet, 0);
        int col = 0;
        for (String column : columns) {
          setCell(state.workbook, state.sheet, row, col++, column);
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
