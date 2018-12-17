/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormUtils {
  protected static final Logger log = LoggerFactory.getLogger(FormUtils.class);

  public static void createPlainBoxSpreadsheet(File outpath, String name, String alias, List<List<String>> boxContents)
      throws IOException {
    createBoxSpreadsheet("/forms/ods/box_input_plain.xlsx", outpath, name, alias, boxContents);
  }

  public static void createDetailedBoxSpreadsheet(File outpath, String name, String alias, List<List<String>> boxContents)
      throws IOException {
    createBoxSpreadsheet("/forms/ods/box_input_detailed.xlsx", outpath, name, alias, boxContents);
  }

  private static void createBoxSpreadsheet(String templateName, File outpath, String name, String alias, List<List<String>> boxContents)
      throws IOException {
    try (FileOutputStream fileOut = new FileOutputStream(outpath); InputStream in = FormUtils.class.getResourceAsStream(templateName)) {
      XSSFWorkbook oDoc = new XSSFWorkbook(in);

      writeBoxSpreadsheet(oDoc, name, alias, boxContents, fileOut);
    }
  }

  private static void writeBoxSpreadsheet(XSSFWorkbook oDoc, String name, String alias, List<List<String>> boxContents,
      FileOutputStream fileOut) throws IOException {
    XSSFSheet sheet = oDoc.getSheet("Input");

    writeBoxContentsHeader(name, alias, sheet);
    writeBoxContentsBody(boxContents, sheet);

    oDoc.write(fileOut);
  }

  private static void writeBoxContentsHeader(String name, String alias, XSSFSheet sheet) {
    XSSFRow row = sheet.createRow(1);
    row.createCell(0).setCellValue(name);
    row.createCell(1).setCellValue(alias);
  }

  private static void writeBoxContentsBody(List<List<String>> boxContents, XSSFSheet sheet) {
    int rowIndex = 4; // start on row 5 of the sheet

    for (List<String> row : boxContents) {
      XSSFRow sheetRow = sheet.createRow(rowIndex);

      for (int colIndex = 0; colIndex < row.size(); ++colIndex) {
        sheetRow.createCell(colIndex).setCellValue(row.get(colIndex));
      }

      rowIndex++;
    }
  }
}
