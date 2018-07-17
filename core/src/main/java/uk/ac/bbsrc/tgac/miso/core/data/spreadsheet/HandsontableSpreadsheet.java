package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.util.ArrayList;
import java.util.List;

public class HandsontableSpreadsheet implements Spreadsheet<List<String>> {

  private final List<Column<List<String>>> columns;

  public HandsontableSpreadsheet(List<String> headers) {
    List<Column<List<String>>> cols = new ArrayList<>();
    for (int i = 0; i < headers.size(); i++) {
      final int columnIndex = i;
      cols.add(Column.forString(headers.get(columnIndex), rowData -> rowData.get(columnIndex)));
    }
    this.columns = cols;
  }

  @Override
  public List<Column<List<String>>> columns() {
    return columns;
  }

  @Override
  public String description() {
    return "MISO Table Data";
  }

  @Override
  public String name() {
    return "miso-data";
  }

}
