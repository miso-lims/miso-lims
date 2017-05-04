package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * Convert a spread sheet file into a stream of string arrays for each row. If a column is blank or missing, null is returned. Blank rows
 * are filtered out.
 * 
 * @param <Sheet> The type of a sheet/table in the workbook
 * @param <Row> The type of a single row in the sheet.
 */
public abstract class SpreadSheetReader<Sheet, Row> {

  protected static <Workbook, Sheet, Row> Stream<String[]> createStream(Sheet sheet, SpreadSheetReader<Sheet, Row> reader) {

    return IntStream.range(0, reader.getRows(sheet)).mapToObj(i -> reader.getRow(sheet, i)).map(r -> reader.unpack(sheet, r))
        .filter(Objects::nonNull);
  }

  /**
   * The the number of rows for a particular sheet.
   */
  protected abstract int getRows(Sheet sheet);

  /**
   * Get a row by index.
   */
  protected abstract Row getRow(Sheet sheet, int index);

  /**
   * Get the number of columns in a row
   */
  protected abstract int getColumns(Sheet sheet, Row row);

  /**
   * Get the value of a column in a particular row.
   */
  protected abstract String getColumn(Sheet sheet, Row row, int i);

  private String[] unpack(Sheet sheet, Row row) {
    if (row == null) return null;
    int columns = getColumns(sheet, row);
    boolean seenUseful = false;
    String[] results = new String[columns];
    for (int i = 0; i < columns; i++) {
      String value = LimsUtils.nullifyStringIfBlank(getColumn(sheet, row, i));
      if (value != null) {
        results[i] = value;
        seenUseful = true;
      }
    }
    return seenUseful ? results : null;
  }

}
