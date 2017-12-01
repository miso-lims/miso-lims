package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelCollector extends SpreadSheetCollector<XSSFWorkbook, XSSFSheet, XSSFRow> {

  public ExcelCollector(Iterable<String> columns) {
    super(columns);
  }

  @Override
  protected XSSFRow createRow(XSSFWorkbook workbook, XSSFSheet sheet, int row) {
    return sheet.createRow(row);
  }

  @Override
  protected XSSFWorkbook createWorkbook() throws Exception {
    return new XSSFWorkbook();
  }

  @Override
  protected XSSFSheet getSheet(XSSFWorkbook workbook) {
    return workbook.createSheet();
  }

  @Override
  protected void setCell(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, int i, String string) {
    row.createCell(i).setCellValue(string);
  }

  @Override
  protected void write(XSSFWorkbook workbook, ByteArrayOutputStream output) throws IOException {
    workbook.write(output);
  }

}
