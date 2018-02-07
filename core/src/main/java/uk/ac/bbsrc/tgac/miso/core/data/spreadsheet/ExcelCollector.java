package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelCollector<T> extends SpreadSheetCollector<T, XSSFWorkbook, XSSFSheet, XSSFRow> {

  public ExcelCollector(List<Column<T>> columns) {
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
  protected void setCell(XSSFWorkbook workbook, XSSFSheet sheet, XSSFRow row, int i, Column<T> column, T item) {
    column.setExcel(row.createCell(i), item);
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
