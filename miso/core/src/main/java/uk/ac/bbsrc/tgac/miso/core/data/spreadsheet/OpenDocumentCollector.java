package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.ByteArrayOutputStream;

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

public class OpenDocumentCollector extends SpreadSheetCollector<OdfSpreadsheetDocument, OdfTable, OdfTableRow> {

  public OpenDocumentCollector(Iterable<String> columns) {
    super(columns);
  }

  @Override
  protected OdfTableRow createRow(OdfSpreadsheetDocument workbook, OdfTable sheet, int index) {
    return sheet.appendRow();
  }

  @Override
  protected OdfSpreadsheetDocument createWorkbook() throws Exception {
    return OdfSpreadsheetDocument.newSpreadsheetDocument();
  }

  @Override
  protected OdfTable getSheet(OdfSpreadsheetDocument workbook) {
    return OdfTable.newTable(workbook);
  }

  @Override
  protected void setCell(OdfSpreadsheetDocument workbook, OdfTable sheet, OdfTableRow row, int i, String string) {
    sheet.getCellByPosition(i, row.getRowIndex()).setStringValue(string);
  }

  @Override
  protected void write(OdfSpreadsheetDocument workbook, ByteArrayOutputStream output) throws Exception {
    workbook.save(output);
  }

}
