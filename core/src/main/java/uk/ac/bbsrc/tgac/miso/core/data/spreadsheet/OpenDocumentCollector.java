package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

public class OpenDocumentCollector<T> extends SpreadSheetCollector<T, OdfSpreadsheetDocument, OdfTable, OdfTableRow> {

  public OpenDocumentCollector(List<Column<T>> columns) {
    super(columns);
  }

  @Override
  protected OdfTableRow createRow(OdfSpreadsheetDocument workbook, OdfTable sheet, int index) {
    return sheet.getRowByIndex(index);
  }

  @Override
  protected OdfSpreadsheetDocument createWorkbook() throws Exception {
    return OdfSpreadsheetDocument.newSpreadsheetDocument();
  }

  @Override
  protected OdfTable getSheet(OdfSpreadsheetDocument workbook) {
    return workbook.getTableList().get(0);
  }

  @Override
  protected void setCell(OdfSpreadsheetDocument workbook, OdfTable sheet, OdfTableRow row, int i, String string) {
    sheet.getCellByPosition(i, row.getRowIndex()).setStringValue(string);
  }

  @Override
  protected void write(OdfSpreadsheetDocument workbook, ByteArrayOutputStream output) throws Exception {
    workbook.save(output);
  }

  @Override
  protected void setCell(OdfSpreadsheetDocument workbook, OdfTable sheet, OdfTableRow row, int i, Column<T> column, T item) {
    column.setODF(sheet.getCellByPosition(i, row.getRowIndex()), item);
  }

}
