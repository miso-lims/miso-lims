package uk.ac.bbsrc.tgac.miso.core.data.spreadsheet;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class ExcelSpreadsheetWrapper implements SpreadsheetWrapper {

  private final XSSFWorkbook workbook;
  private final XSSFSheet sheet;

  public ExcelSpreadsheetWrapper(InputStream in) throws IOException {
    this.workbook = new XSSFWorkbook(in);
    this.sheet = this.workbook.getSheetAt(0);
  }

  @Override
  public int getRowCount() {
    return sheet.getLastRowNum() + 1;
  }

  @Override
  public int getColumnCount() {
    int columns = 0;
    for (int i = 0; i < getRowCount(); i++) {
      columns = Math.max(columns, sheet.getRow(i).getLastCellNum());
    }
    return columns;
  }

  @Override
  public String getCellValue(int rowNum, int colNum) {
    XSSFRow row = sheet.getRow(rowNum);
    XSSFCell cell = row.getCell(colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
    switch (cell.getCellTypeEnum()) {
      case BLANK:
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return LimsUtils.formatDate(cell.getDateCellValue());
        } else {
          return cell.getRawValue();
        }
      default:
        throw new IllegalArgumentException("Unhandled cell type");
    }
  }

}
