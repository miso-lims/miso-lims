package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.IOException;
import java.io.InputStream;

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;

public class OpenDocumentSpreadsheetWrapper implements SpreadsheetWrapper {

  private final OdfSpreadsheetDocument workbook;
  private final OdfTable table;

  public OpenDocumentSpreadsheetWrapper(InputStream in) throws IOException {
    try {
      this.workbook = OdfSpreadsheetDocument.loadDocument(in);
      this.table = this.workbook.getTableList().get(0);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  @Override
  public int getRowCount() {
    return table.getRowCount();
  }

  @Override
  public int getColumnCount() {
    return table.getColumnCount();
  }

  @Override
  public String getCellValue(int row, int col) {
    return table.getCellByPosition(col, row).getStringValue();
  }

}
