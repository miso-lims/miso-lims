package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.opencsv.CSVReader;

public class DelimitedSpreadsheetWrapper implements SpreadsheetWrapper {

  List<String[]> rows;

  public DelimitedSpreadsheetWrapper(InputStream in) throws IOException {
    try (CSVReader reader = new CSVReader(new InputStreamReader(in))) {
      this.rows = reader.readAll();
    }
  }

  @Override
  public int getRowCount() {
    return rows.size();
  }

  @Override
  public int getColumnCount() {
    return rows.stream().map(row -> row.length).max(Integer::compare).orElse(0);
  }

  @Override
  public String getCellValue(int row, int col) {
    return rows.get(row)[col];
  }

}
