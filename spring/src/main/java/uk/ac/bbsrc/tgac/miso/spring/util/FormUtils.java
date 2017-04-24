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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.spring.util;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.DeliveryFormException;
import uk.ac.bbsrc.tgac.miso.core.exception.InputFormException;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.util.AliasComparator;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

/**
 * @author Rob Davey
 */
public class FormUtils {

  protected static final Logger log = LoggerFactory.getLogger(FormUtils.class);

  private static final Pattern digitPattern = Pattern.compile("(^[0-9]+)[\\.0-9]*");
  private static final Pattern samplePattern = Pattern.compile("([A-z0-9]+)_S([A-z0-9]+)_(.*)");

  public static void createSampleInputSpreadsheet(Collection<Sample> samples, File outpath) throws Exception {
    Collections.sort(new ArrayList<>(samples), new AliasComparator<>());

    InputStream in = null;
    if (outpath.getName().endsWith(".xlsx")) {
      in = FormUtils.class.getResourceAsStream("/forms/ods/bulk_input.xlsx");
      if (in != null) {
        XSSFWorkbook oDoc = new XSSFWorkbook(in);
        FileOutputStream fileOut = new FileOutputStream(outpath);
        oDoc.write(fileOut);
        fileOut.close();
      } else {
        throw new IOException("Could not read from resource.");
      }
    } else if (outpath.getName().endsWith(".ods")) {
      in = FormUtils.class.getResourceAsStream("/forms/ods/bulk_input.ods");
      if (in != null) {
        OdfSpreadsheetDocument oDoc = OdfSpreadsheetDocument.loadDocument(in);
        oDoc.save(outpath);
      } else {
        throw new IOException("Could not read from resource.");
      }
    } else {
      throw new IllegalArgumentException("Can only produce bulk input forms in ods or xlsx formats.");
    }
  }

  public static void createPlateInputSpreadsheet(File outpath) throws Exception {
    InputStream in = null;
    if (outpath.getName().endsWith(".xlsx")) {
      in = FormUtils.class.getResourceAsStream("/forms/ods/plate_input.xlsx");
      if (in != null) {
        XSSFWorkbook oDoc = new XSSFWorkbook(in);
        FileOutputStream fileOut = new FileOutputStream(outpath);
        oDoc.write(fileOut);
        fileOut.close();
      } else {
        throw new IOException("Could not read from resource.");
      }
    } else if (outpath.getName().endsWith(".ods")) {
      in = FormUtils.class.getResourceAsStream("/forms/ods/plate_input.ods");
      if (in != null) {
        OdfSpreadsheetDocument oDoc = OdfSpreadsheetDocument.loadDocument(in);
        oDoc.save(outpath);
      } else {
        throw new IOException("Could not read from resource.");
      }
    } else {
      throw new IllegalArgumentException("Can only produce plate input forms in ods or xlsx formats.");
    }
  }

  public static void createSampleExportForm(File outpath, JSONArray jsonArray) throws Exception {
    InputStream in = null;
    in = FormUtils.class.getResourceAsStream("/forms/ods/export_samples.xlsx");
    if (in != null) {
      XSSFWorkbook oDoc = new XSSFWorkbook(in);

      XSSFSheet sheet = oDoc.getSheet("samples_export");
      FileOutputStream fileOut = new FileOutputStream(outpath);
      int i = 5;
      for (JSONObject jsonObject : (Iterable<JSONObject>) jsonArray) {
        if ("sampleinwell".equals(jsonObject.getString("name"))) {
          String sampleinwell = jsonObject.getString("value");
          // "sampleid:wellid:samplealias:projectname:projectalias:dnaOrRNA"
          String sampleId = sampleinwell.split(":")[0];
          String wellId = sampleinwell.split(":")[1];
          String sampleAlias = sampleinwell.split(":")[2];
          String projectName = sampleinwell.split(":")[3];
          String projectAlias = sampleinwell.split(":")[4];
          String dnaOrRNA = sampleinwell.split(":")[5];
          XSSFRow row = sheet.createRow(i);
          XSSFCell cellA = row.createCell(0);
          cellA.setCellValue(projectName);
          XSSFCell cellB = row.createCell(1);
          cellB.setCellValue(projectAlias);
          XSSFCell cellC = row.createCell(2);
          cellC.setCellValue(sampleId);
          XSSFCell cellD = row.createCell(3);
          cellD.setCellValue(sampleAlias);
          XSSFCell cellE = row.createCell(4);
          cellE.setCellValue(wellId);
          XSSFCell cellG = row.createCell(6);
          XSSFCell cellH = row.createCell(7);
          XSSFCell cellI = row.createCell(8);
          XSSFCell cellL = row.createCell(11);
          if ("R".equals(dnaOrRNA)) {
            cellG.setCellValue("NA");
            cellL.setCellFormula("1000/H" + (i + 1));
          } else if ("D".equals(dnaOrRNA)) {
            cellH.setCellValue("NA");
            cellI.setCellValue("NA");
            cellL.setCellFormula("1000/G" + (i + 1));
          }
          XSSFCell cellM = row.createCell(12);
          cellM.setCellFormula("50-L" + (i + 1));
          i++;
        }
      }
      oDoc.write(fileOut);
      fileOut.close();
    } else {
      throw new IOException("Could not read from resource.");
    }

  }

  public static void createLibraryPoolExportFormFromWeb(File outpath, JSONArray jsonArray, String indexFamily) throws Exception {
    InputStream in = null;
    in = FormUtils.class.getResourceAsStream("/forms/ods/export_libraries_pools.xlsx");
    if (in != null) {
      XSSFWorkbook oDoc = new XSSFWorkbook(in);

      XSSFSheet sheet = oDoc.getSheet("library_pool_export");
      FileOutputStream fileOut = new FileOutputStream(outpath);
      XSSFRow row2 = sheet.getRow(1);

      int i = 6;
      for (JSONObject jsonObject : (Iterable<JSONObject>) jsonArray) {
        if ("paired".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellA = row2.createCell(0);
          row2cellA.setCellValue(jsonObject.getString("value"));
        } else if ("platform".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellB = row2.createCell(1);
          row2cellB.setCellValue(jsonObject.getString("value"));
        } else if ("type".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellC = row2.createCell(2);
          row2cellC.setCellValue(jsonObject.getString("value"));
        } else if ("selection".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellD = row2.createCell(3);
          row2cellD.setCellValue(jsonObject.getString("value"));
        } else if ("strategy".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellE = row2.createCell(4);
          row2cellE.setCellValue(jsonObject.getString("value"));
        }
        if ("sampleinwell".equals(jsonObject.getString("name"))) {
          String sampleinwell = jsonObject.getString("value");
          // "sampleid:wellid:samplealias:projectname:projectalias:dnaOrRNA"
          String sampleId = sampleinwell.split(":")[0];
          String wellId = sampleinwell.split(":")[1];
          String sampleAlias = sampleinwell.split(":")[2];
          String projectName = sampleinwell.split(":")[3];
          String projectAlias = sampleinwell.split(":")[4];
          XSSFRow row = sheet.createRow(i);
          XSSFCell cellA = row.createCell(0);
          cellA.setCellValue(projectName);
          XSSFCell cellB = row.createCell(1);
          cellB.setCellValue(projectAlias);
          XSSFCell cellC = row.createCell(2);
          cellC.setCellValue(sampleId);
          XSSFCell cellD = row.createCell(3);
          cellD.setCellValue(sampleAlias);
          XSSFCell cellE = row.createCell(4);
          cellE.setCellValue(wellId);
          if (indexFamily != null) {
            XSSFCell cellJ = row.createCell(9);
            cellJ.setCellValue(indexFamily);
          }
          i++;
        }
      }
      oDoc.write(fileOut);
      fileOut.close();
    } else {
      throw new IOException("Could not read from resource.");
    }

  }

  public static JSONArray preProcessSampleSheetImport(File inPath, User u, SampleService sampleService) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      JSONArray jsonArray = new JSONArray();
      XSSFSheet sheet = wb.getSheetAt(0);
      int rows = sheet.getPhysicalNumberOfRows();
      for (int ri = 5; ri < rows; ri++) {
        XSSFRow row = sheet.getRow(ri);
        XSSFCell sampleAliasCell = row.getCell(3);
        Sample s = null;
        if (getCellValueAsString(sampleAliasCell) != null) {
          String salias = getCellValueAsString(sampleAliasCell);
          Collection<Sample> ss = sampleService.getByAlias(salias);
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            } else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
            }
          } else {
            throw new InputFormException(
                "No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
          }
        } else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        // sample OK - good to go
        if (s != null) {
          JSONArray sampleArray = new JSONArray();

          XSSFCell projectNameCell = row.getCell(0);
          XSSFCell projectAliasCell = row.getCell(1);
          XSSFCell sampleNameCell = row.getCell(2);
          XSSFCell wellCell = row.getCell(4);
          XSSFCell adaptorCell = row.getCell(5);
          XSSFCell qcPassedCell = row.getCell(13);

          sampleArray.add(getCellValueAsString(projectNameCell));
          sampleArray.add(getCellValueAsString(projectAliasCell));
          sampleArray.add(getCellValueAsString(sampleNameCell));
          sampleArray.add(getCellValueAsString(sampleAliasCell));
          sampleArray.add(getCellValueAsString(wellCell));
          if ((getCellValueAsString(adaptorCell)) != null) {
            sampleArray.add(getCellValueAsString(adaptorCell));
          } else {
            sampleArray.add("");

          }

          XSSFCell qcResultCell = null;

          if ("GENOMIC".equals(s.getSampleType()) || "METAGENOMIC".equals(s.getSampleType())) {
            qcResultCell = row.getCell(6);
          } else if ("NON GENOMIC".equals(s.getSampleType()) || "VIRAL RNA".equals(s.getSampleType())
              || "TRANSCRIPTOMIC".equals(s.getSampleType()) || "METATRANSCRIPTOMIC".equals(s.getSampleType())) {
            qcResultCell = row.getCell(7);
          } else {

            if (!"NA".equals(getCellValueAsString(row.getCell(6)))) {
              qcResultCell = row.getCell(6);
            } else if (!"NA".equals(getCellValueAsString(row.getCell(7)))) {
              qcResultCell = row.getCell(7);
            }
          }

          XSSFCell rinCell = row.getCell(8);
          XSSFCell sample260280Cell = row.getCell(9);
          XSSFCell sample260230Cell = row.getCell(10);

          try {
            if (getCellValueAsString(qcResultCell) != null && !"NA".equals(getCellValueAsString(qcResultCell))) {

              sampleArray.add(Double.valueOf(getCellValueAsString(qcResultCell)));
              if (getCellValueAsString(qcPassedCell) != null) {
                if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
                  sampleArray.add("true");
                } else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
                  sampleArray.add("false");
                }

              }
            } else {
              sampleArray.add("");
              sampleArray.add("");
            }

            StringBuilder noteSB = new StringBuilder();
            if (!isStringEmptyOrNull(getCellValueAsString(rinCell)) && !"NA".equals(getCellValueAsString(rinCell))) {
              noteSB.append("RIN:" + getCellValueAsString(rinCell) + ";");
            }
            if (!isStringEmptyOrNull(getCellValueAsString(sample260280Cell))) {
              noteSB.append("260/280:" + getCellValueAsString(sample260280Cell) + ";");
            }
            if (!isStringEmptyOrNull(getCellValueAsString(sample260230Cell))) {
              noteSB.append("260/230:" + getCellValueAsString(sample260230Cell) + ";");
            }
            sampleArray.add(noteSB.toString());
          } catch (NumberFormatException nfe) {
            throw new InputFormException(
                "Supplied Sample QC concentration for sample '" + getCellValueAsString(sampleAliasCell) + "' is invalid", nfe);
          }
          jsonArray.add(sampleArray);
        }
      }
      return jsonArray;
    } else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  public static JSONObject preProcessLibraryPoolSheetImport(File inPath, User u, SampleService sampleService) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      JSONObject jsonObject = new JSONObject();
      JSONArray sampleArray = new JSONArray();
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      XSSFSheet sheet = wb.getSheetAt(0);

      XSSFRow glrow = sheet.getRow(1);

      // process global headers
      XSSFCell pairedCell = glrow.getCell(0);
      jsonObject.put("paired", getCellValueAsString(pairedCell));

      XSSFCell platformCell = glrow.getCell(1);
      if (getCellValueAsString(platformCell) != null) {
        jsonObject.put("platform", getCellValueAsString(platformCell));
      } else {
        throw new InputFormException("Cannot resolve Platform type from: '" + getCellValueAsString(platformCell) + "'");
      }

      XSSFCell typeCell = glrow.getCell(2);
      if (getCellValueAsString(typeCell) != null) {
        String[] split = getCellValueAsString(typeCell).split("-");
        String plat = split[0];
        String type = split[1];
        if (getCellValueAsString(platformCell).equals(plat)) {
          jsonObject.put("type", type);
        } else {
          throw new InputFormException("Selected library type '" + getCellValueAsString(typeCell) + "' doesn't match platform type: '"
              + getCellValueAsString(platformCell) + "'");
        }
      } else {
        throw new InputFormException("Cannot resolve Library type from: '" + getCellValueAsString(typeCell) + "'");
      }

      XSSFCell selectionCell = glrow.getCell(3);
      if (getCellValueAsString(selectionCell) != null) {
        jsonObject.put("selection", getCellValueAsString(selectionCell));
      } else {
        throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
      }

      XSSFCell strategyCell = glrow.getCell(4);
      if (getCellValueAsString(strategyCell) != null) {
        jsonObject.put("strategy", getCellValueAsString(strategyCell));
      } else {
        throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) + "'");
      }

      int rows = sheet.getPhysicalNumberOfRows();
      for (int ri = 6; ri < rows; ri++) {
        JSONArray rowsJSONArray = new JSONArray();
        XSSFRow row = sheet.getRow(ri);
        XSSFCell sampleNameCell = row.getCell(2);
        XSSFCell sampleAliasCell = row.getCell(3);
        Sample s = null;
        if (getCellValueAsString(sampleAliasCell) != null) {
          String salias = getCellValueAsString(sampleAliasCell);
          Collection<Sample> ss = sampleService.getByAlias(salias);
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            } else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
            }
          } else {
            throw new InputFormException(
                "No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
          }
        } else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        // sample OK - good to go
        if (s != null) {
          XSSFCell indexFamilyCell = row.getCell(9);
          XSSFCell indicesCell = row.getCell(10);
          XSSFCell libraryQubitCell = row.getCell(6);
          XSSFCell libraryQcInsertSizeCell = row.getCell(7);
          XSSFCell libraryQcMolarityCell = row.getCell(8);
          XSSFCell qcPassedCell = row.getCell(11);
          XSSFCell libraryDescriptionCell = row.getCell(12);
          XSSFCell wellCell = row.getCell(4);
          XSSFCell dilutionMolarityCell = row.getCell(16);
          XSSFCell poolNameCell = row.getCell(21);
          XSSFCell poolConvertedMolarityCell = row.getCell(20);

          rowsJSONArray.add(getCellValueAsString(sampleNameCell));
          rowsJSONArray.add(getCellValueAsString(sampleAliasCell));
          rowsJSONArray.add(getCellValueAsString(wellCell).replaceAll("\\s", ""));

          XSSFCell proceedKeyCell = row.getCell(22);

          String proceedKey = "A";

          if (getCellValueAsString(proceedKeyCell) != null) {
            String proceedKeyString = getCellValueAsString(proceedKeyCell).toUpperCase().replaceAll("\\s", "");
            if ("L".equals(proceedKeyString)) {
              proceedKey = "L";
            } else if ("U".equals(proceedKeyString)) {
              proceedKey = "U";
            } else if ("P".equals(proceedKeyString)) {
              proceedKey = "P";
            }
          }

          String libAlias = "";
          Matcher mat = samplePattern.matcher(getCellValueAsString(sampleAliasCell));
          if (mat.matches()) {
            String platePos = getCellValueAsString(wellCell);
            libAlias = mat.group(1) + "_" + "L" + mat.group(2) + "-" + platePos.toUpperCase() + "_" + mat.group(3);
          }
          rowsJSONArray.add(libAlias);

          if ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey)) {
            String libDesc = s.getDescription();
            if (!isStringEmptyOrNull(getCellValueAsString(libraryDescriptionCell))) {
              libDesc = getCellValueAsString(libraryDescriptionCell);
            }
            rowsJSONArray.add(libDesc);
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(libraryQubitCell) != null
              && ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(libraryQubitCell));
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(libraryQcInsertSizeCell) != null
              && ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(libraryQcInsertSizeCell));
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(libraryQcMolarityCell) != null
              && ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(libraryQcMolarityCell));
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(qcPassedCell) != null && ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey))) {
            if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
              rowsJSONArray.add("true");
            } else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
              rowsJSONArray.add("false");
            }
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(indexFamilyCell) != null
              && ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(indexFamilyCell));
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(indicesCell) != null && ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(indicesCell));
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(dilutionMolarityCell) != null && ("A".equals(proceedKey) || "P".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(dilutionMolarityCell));
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(poolNameCell) != null && ("A".equals(proceedKey) || "P".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(poolNameCell));
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(poolConvertedMolarityCell) != null && ("A".equals(proceedKey) || "P".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(poolConvertedMolarityCell));
          } else {
            rowsJSONArray.add("");
          }

          rowsJSONArray.add(proceedKey);
          if ("A".equals(proceedKey)) {
            rowsJSONArray.add("A: Import everything");
          } else if ("L".equals(proceedKey)) {
            rowsJSONArray.add("L: Import and create library only");
          } else if ("U".equals(proceedKey)) {
            rowsJSONArray.add("U: Updated the library info only");
          } else if ("P".equals(proceedKey)) {
            rowsJSONArray.add("P: import the library dilution and pool based on the library info");
          }
        }
        sampleArray.add(rowsJSONArray);
      }
      jsonObject.put("rows", sampleArray);
      return jsonObject;
    } else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  // TODO: use Sample QC and Library QC Services instead of Stores (when they exist)
  private static List<Sample> processSampleInputODS(OdfSpreadsheetDocument oDoc, User u, SampleService sampleService,
      LibraryService libraryService, SampleQcStore sampleQcStore, LibraryQcStore libraryQcStore, NamingScheme namingScheme,
      IndexService indexService) throws Exception {
    List<Sample> samples = new ArrayList<>();
    OdfTable oTable = oDoc.getTableList().get(0);

    // process global headers
    OdfTableCell pairedCell = oTable.getCellByPosition("A2");
    boolean paired;
    if (pairedCell != null && pairedCell.getBooleanValue() != null) {
      paired = pairedCell.getBooleanValue();
      log.info("Got paired: " + paired);
    } else {
      throw new InputFormException("'Paired' cell is empty. Please specify TRUE or FALSE.");
    }

    OdfTableCell platformCell = oTable.getCellByPosition("B2");
    PlatformType pt = null;
    if (!isStringEmptyOrNull(platformCell.getStringValue())) {
      pt = PlatformType.get(platformCell.getStringValue());
    }
    if (pt == null) {
      throw new InputFormException("Cannot resolve Platform type from: '" + platformCell.getStringValue() + "'");
    } else {
      log.info("Got platform type: " + pt.getKey());
    }

    OdfTableCell typeCell = oTable.getCellByPosition("C2");
    LibraryType lt = null;
    if (!isStringEmptyOrNull(typeCell.getStringValue())) {
      String[] split = typeCell.getStringValue().split("-");
      String plat = split[0];
      String type = split[1];
      if (platformCell.getStringValue().equals(plat)) {
        lt = libraryService.getLibraryTypeByDescriptionAndPlatform(type, pt);
      } else {
        throw new InputFormException("Selected library type '" + typeCell.getStringValue() + "' doesn't match platform type: '"
            + platformCell.getStringValue() + "'");
      }
    }
    if (lt == null) {
      throw new InputFormException("Cannot resolve Library type from: '" + typeCell.getStringValue() + "'");
    } else {
      log.info("Got library type: " + lt.getDescription());
    }

    OdfTableCell selectionCell = oTable.getCellByPosition("D2");
    LibrarySelectionType ls = null;
    if (!isStringEmptyOrNull(selectionCell.getStringValue())) {
      ls = libraryService.getLibrarySelectionTypeByName(selectionCell.getStringValue());
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + selectionCell.getStringValue() + "'");
    } else {
      log.info("Got library selection type: " + ls.getName());
    }

    OdfTableCell strategyCell = oTable.getCellByPosition("E2");
    LibraryStrategyType lst = null;
    if (!isStringEmptyOrNull(strategyCell.getStringValue())) {
      lst = libraryService.getLibraryStrategyTypeByName(strategyCell.getStringValue());
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + strategyCell.getStringValue() + "'");
    } else {
      log.info("Got library strategy type: " + lst.getName());
    }

    // process entries
    Map<String, Pool> pools = new HashMap<>();
    for (OdfTableRow row : oTable.getRowList()) {
      int ri = row.getRowIndex();
      if (ri > 3) {
        // cell defs
        OdfTableCell sampleAliasCell = oTable.getCellByPosition(2, ri);

        Sample s = null;
        if (!isStringEmptyOrNull(sampleAliasCell.getStringValue())) {
          Collection<Sample> ss = sampleService.getByAlias(sampleAliasCell.getStringValue());
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            } else {
              throw new InputFormException(
                  "Multiple samples retrieved with this alias: '" + sampleAliasCell.getStringValue() + "'. Cannot process.");
            }
          } else {
            throw new InputFormException("No such sample '" + sampleAliasCell.getStringValue()
                + "'in database. Samples need to be created before using the form input functionality");
          }
        } else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        // sample OK - good to go
        if (s != null) {
          String poolNumberCell = oTable.getCellByPosition(3, ri).getStringValue();
          String sampleQcCell = oTable.getCellByPosition(4, ri).getStringValue();
          String libraryDescriptionCell = oTable.getCellByPosition(7, ri).getStringValue();
          String indexFamilyCell = oTable.getCellByPosition(8, ri).getStringValue();
          String indicesCell = oTable.getCellByPosition(9, ri).getStringValue();
          String libraryQcCell = oTable.getCellByPosition(10, ri).getStringValue();
          String libraryQcInsertSizeCell = oTable.getCellByPosition(11, ri).getStringValue();
          String libraryQcMolarityCell = oTable.getCellByPosition(12, ri).getStringValue();
          String libraryQcPassFailCell = oTable.getCellByPosition(13, ri).getStringValue();
          String dilutionMolarityCell = oTable.getCellByPosition(17, ri).getStringValue();
          String poolConvertedMolarityCell = oTable.getCellByPosition(22, ri).getStringValue();

          // add pool, if any
          processPool(poolNumberCell, poolConvertedMolarityCell, pools);
          processSampleQC(sampleQcCell, s, u, sampleQcStore);

          Library library = processLibrary(libraryQcCell, libraryDescriptionCell, libraryQcPassFailCell, s, pt, lt, ls, lst, paired,
              namingScheme);
          if (library != null) {
            processLibraryQC(libraryQcCell, libraryQcMolarityCell, libraryQcInsertSizeCell, library, u, libraryQcStore);
            processIndices(indexFamilyCell, indicesCell, library, indexService);
            processDilutions(dilutionMolarityCell, library, pools.get(poolNumberCell), u);
            log.info("Added library: " + library.toString());
            s.addLibrary(library);
          }
          samples.add(s);
        }
      }
    }
    log.info("Done");
    return samples;
  }

  // TODO: use Sample QC and Library QC Services instead of Stores (when they exist)
  private static List<Sample> processSampleInputXLSX(XSSFWorkbook wb, User u, SampleService sampleService, LibraryService libraryService,
      SampleQcStore sampleQcStore, LibraryQcStore libraryQcStore, NamingScheme namingScheme, IndexService indexService) throws Exception {
    List<Sample> samples = new ArrayList<>();
    XSSFSheet sheet = wb.getSheetAt(0);
    int rows = sheet.getPhysicalNumberOfRows();

    XSSFRow glrow = sheet.getRow(1);

    // process global headers
    XSSFCell pairedCell = glrow.getCell(0);
    boolean paired;
    if (getCellValueAsString(pairedCell) != null) {
      paired = pairedCell.getBooleanCellValue();
      log.info("Got paired: " + paired);
    } else {
      throw new InputFormException("'Paired' cell is empty. Please specify TRUE or FALSE.");
    }

    XSSFCell platformCell = glrow.getCell(1);
    PlatformType pt = null;
    if (getCellValueAsString(platformCell) != null) {
      pt = PlatformType.get(getCellValueAsString(platformCell));
    }
    if (pt == null) {
      throw new InputFormException("Cannot resolve Platform type from: '" + getCellValueAsString(platformCell) + "'");
    } else {
      log.info("Got platform type: " + pt.getKey());
    }

    XSSFCell typeCell = glrow.getCell(2);
    LibraryType lt = null;
    if (getCellValueAsString(typeCell) != null) {
      String[] split = getCellValueAsString(typeCell).split("-");
      String plat = split[0];
      String type = split[1];
      if (getCellValueAsString(platformCell).equals(plat)) {
        lt = libraryService.getLibraryTypeByDescriptionAndPlatform(type, pt);
      } else {
        throw new InputFormException("Selected library type '" + getCellValueAsString(typeCell) + "' doesn't match platform type: '"
            + getCellValueAsString(platformCell) + "'");
      }
    }
    if (lt == null) {
      throw new InputFormException("Cannot resolve Library type from: '" + getCellValueAsString(typeCell) + "'");
    } else {
      log.info("Got library type: " + lt.getDescription());
    }

    XSSFCell selectionCell = glrow.getCell(3);
    LibrarySelectionType ls = null;
    if (getCellValueAsString(selectionCell) != null) {
      ls = libraryService.getLibrarySelectionTypeByName(getCellValueAsString(selectionCell));
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
    } else {
      log.info("Got library selection type: " + ls.getName());
    }

    XSSFCell strategyCell = glrow.getCell(4);
    LibraryStrategyType lst = null;
    if (getCellValueAsString(strategyCell) != null) {
      lst = libraryService.getLibraryStrategyTypeByName(getCellValueAsString(strategyCell));
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) + "'");
    } else {
      log.info("Got library strategy type: " + lst.getName());
    }

    // process entries
    Map<String, Pool> pools = new HashMap<>();

    for (int ri = 4; ri < rows; ri++) {
      XSSFRow row = sheet.getRow(ri);

      // cell defs
      XSSFCell sampleAliasCell = row.getCell(2);

      Sample s = null;
      if (getCellValueAsString(sampleAliasCell) != null) {
        String salias = getCellValueAsString(sampleAliasCell);
        Collection<Sample> ss = sampleService.getByAlias(salias);
        if (!ss.isEmpty()) {
          if (ss.size() == 1) {
            s = ss.iterator().next();
            log.info("Got sample: " + s.getAlias());
          } else {
            throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
          }
        } else {
          throw new InputFormException(
              "No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
        }
      } else {
        log.info("Blank sample row found. Ending import.");
        break;
      }

      // sample OK - good to go
      if (s != null) {
        String poolNumberCell = getCellValueAsString(row.getCell(3));
        String sampleQcCell = getCellValueAsString(row.getCell(4));
        String libraryDescriptionCell = getCellValueAsString(row.getCell(7));
        String indexKitCell = getCellValueAsString(row.getCell(8));
        String indexTagsCell = getCellValueAsString(row.getCell(9));
        String libraryQcCell = getCellValueAsString(row.getCell(10));
        String libraryQcInsertSizeCell = getCellValueAsString(row.getCell(11));
        String libraryQcMolarityCell = getCellValueAsString(row.getCell(12));
        String libraryQcPassFailCell = getCellValueAsString(row.getCell(13));
        String dilutionMolarityCell = getCellValueAsString(row.getCell(17));
        String poolConvertedMolarityCell = getCellValueAsString(row.getCell(22));

        // add pool, if any
        processPool(poolNumberCell, poolConvertedMolarityCell, pools);
        processSampleQC(sampleQcCell, s, u, sampleQcStore);

        Library library = processLibrary(libraryQcCell, libraryDescriptionCell, libraryQcPassFailCell, s, pt, lt, ls, lst, paired,
            namingScheme);
        if (library != null) {
          processLibraryQC(libraryQcCell, libraryQcMolarityCell, libraryQcInsertSizeCell, library, u, libraryQcStore);
          processIndices(indexKitCell, indexTagsCell, library, indexService);
          processDilutions(dilutionMolarityCell, library, pools.get(poolNumberCell), u);
          log.info("Added library: " + library.toString());
          s.addLibrary(library);
        }
        samples.add(s);
      }
    }
    return samples;
  }

  private static void processPool(String poolAlias, String poolConvertedMolarity, Map<String, Pool> pools) throws Exception {
    if (!isStringEmptyOrNull(poolAlias)) {
      if (!pools.containsKey(poolAlias)) {
        Pool pool = new PoolImpl();
        pool.setAlias(poolAlias);
        pools.put(poolAlias, pool);
        log.info("Added pool: " + poolAlias);
      }

      if (!isStringEmptyOrNull(poolConvertedMolarity)) {
        Pool p = pools.get(poolAlias);
        if (p != null) {
          log.info("Retrieved pool " + poolAlias);
          try {
            double d = Double.valueOf(poolConvertedMolarity);
            log.info("Got conc " + d);
            p.setConcentration(d);
          } catch (NumberFormatException nfe) {
            throw new InputFormException("Supplied pool concentration for pool '" + poolAlias + "' is invalid", nfe);
          }
        }
      }
    }
  }

  private static void processSampleQC(String sampleQc, Sample s, User u, SampleQcStore sampleQcStore) throws Exception {
    // process sample QC
    if (!isStringEmptyOrNull(sampleQc)) {
      try {
        SampleQC sqc = new SampleQCImpl();
        sqc.setSample(s);
        sqc.setResults(Double.valueOf(sampleQc));
        sqc.setQcCreator(u.getLoginName());
        sqc.setQcDate(new Date());
        sqc.setQcType(sampleQcStore.getSampleQcTypeByName("QuBit"));
        if (!s.getSampleQCs().contains(sqc)) {
          s.addQc(sqc);
          log.info("Added sample QC: " + sqc.toString());
        }
      } catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied Sample QC concentration for sample '" + sampleQc + "' is invalid", nfe);
      }
    }
  }

  private static Library processLibrary(String libraryQc, String libraryDescription, String libraryQcPassFail, Sample s, PlatformType pt,
      LibraryType lt, LibrarySelectionType ls, LibraryStrategyType lst, boolean paired, NamingScheme namingScheme)
      throws Exception {
    if (!isStringEmptyOrNull(libraryQc)) {
      // create library
      Library library = new LibraryImpl();
      library.setSample(s);
      library.setSecurityProfile(s.getSecurityProfile());
      if (!isStringEmptyOrNull(libraryDescription)) {
        library.setDescription(libraryDescription);
      } else {
        library.setDescription(s.getDescription());
      }
      library.setCreationDate(new Date());
      library.setPlatformType(pt.name());
      library.setLibraryType(lt);
      library.setLibrarySelectionType(ls);
      library.setLibraryStrategyType(lst);
      library.setPaired(paired);

      if (!isStringEmptyOrNull(libraryQcPassFail)) {
        library.setQcPassed(Boolean.parseBoolean(libraryQcPassFail));
      }

      String libAlias = namingScheme.generateLibraryAlias(library);

      library.setAlias(libAlias);

      return library;
    }
    return null;
  }

  private static void processLibraryQC(String libraryQc, String libraryQcMolarity, String libraryQcInsertSize, Library library, User u,
      LibraryQcStore libraryQcStore) throws Exception {
    if (!isStringEmptyOrNull(libraryQcMolarity)) {
      int insertSize = 0;
      try {
        Matcher m = digitPattern.matcher(libraryQcInsertSize);
        if (m.matches()) {
          insertSize = Integer.valueOf(m.group(1));
        } else {
          throw new InputFormException(
              "Supplied Library insert size for library '" + library.getAlias() + "' (" + library.getSample().getAlias() + ") is invalid");
        }
      } catch (NumberFormatException nfe) {
        throw new InputFormException(
            "Supplied Library insert size for library '" + library.getAlias() + "' (" + library.getSample().getAlias() + ") is invalid",
            nfe);
      }

      try {
        LibraryQC lqc = new LibraryQCImpl();
        lqc.setLibrary(library);
        lqc.setResults(Double.valueOf(libraryQcMolarity));
        lqc.setQcCreator(u.getLoginName());
        lqc.setQcDate(new Date());
        QcType lqct = libraryQcStore.getLibraryQcTypeByName(libraryQc);
        if (lqct != null) {
          lqc.setQcType(libraryQcStore.getLibraryQcTypeByName(libraryQc));
          if (!library.getLibraryQCs().contains(lqc)) {
            library.addQc(lqc);
            log.info("Added library QC: " + lqc.toString());
          }
        } else {
          throw new InputFormException("No such Library QC type '" + libraryQc + "'");
        }

        LibraryQC lis = new LibraryQCImpl();
        lis.setLibrary(library);
        lis.setResults((double) insertSize);
        lis.setQcCreator(u.getLoginName());
        lis.setQcDate(new Date());
        QcType lisqct = libraryQcStore.getLibraryQcTypeByName("Insert Size");
        lis.setQcType(lisqct);
        if (lisqct == null) {
          throw new InputFormException("No such Library QC type 'Insert Size'");
        }

        if (insertSize == 0 && lqc.getResults() == 0) {
          library.setQcPassed(false);
        } else {
          // TODO check libraryQcPassFailCell?
          library.setQcPassed(true);
        }
      } catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied Library QC concentration for library '" + library.getAlias() + "' ("
            + library.getSample().getAlias() + ") is invalid", nfe);
      }
    }
  }

  private static void processIndices(String indexKit, String indexTags, Library library, IndexService indexService)
      throws Exception {
    if (!isStringEmptyOrNull(indexKit)) {
      IndexFamily ifam = indexService.getIndexFamilyByName(indexKit);
      if (ifam != null) {
        if (!isStringEmptyOrNull(indexTags)) {
          library.setIndices(matchIndicesFromText(ifam.getIndices(), indexTags));
        } else {
          throw new InputFormException("Index Kit specified but no indices entered for: '" + library.getSample().getAlias() + "'.");
        }
      } else {
        throw new InputFormException("No indices associated with this kit definition: '" + library.getSample().getAlias() + "'.");
      }
    }
  }

  private static void processDilutions(String dilutionMolarity, Library library, Pool p, User u) throws Exception {
    if (!isStringEmptyOrNull(dilutionMolarity)) {
      try {
        LibraryDilution ldi = new LibraryDilution();
        ldi.setLibrary(library);
        ldi.setSecurityProfile(library.getSecurityProfile());
        ldi.setConcentration(Double.valueOf(dilutionMolarity));
        ldi.setCreationDate(new Date());
        ldi.setDilutionCreator(u.getLoginName());
        if (!library.getLibraryDilutions().contains(ldi)) {
          library.addDilution(ldi);
          log.info("Added library dilution: " + ldi.toString());
        }
        if (p != null) {
          p.getPoolableElementViews().add(PoolableElementView.fromDilution(ldi));
          log.info("Added library dilution to pool: " + p.toString());
        }
      } catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied LibraryDilution concentration for library '" + library.getAlias() + "' ("
            + library.getSample().getAlias() + ") is invalid", nfe);
      }
    }
  }

  public static List<Sample> importSampleInputSpreadsheet(File inPath, User u, SampleService sampleService, LibraryService libraryService,
      SampleQcStore sampleQcStore, LibraryQcStore libraryQcStore, NamingScheme namingScheme, IndexService indexService) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      return processSampleInputXLSX(wb, u, sampleService, libraryService, sampleQcStore, libraryQcStore, namingScheme, indexService);
    } else if (inPath.getName().endsWith(".ods")) {
      OdfSpreadsheetDocument oDoc = (OdfSpreadsheetDocument) OdfDocument.loadDocument(inPath);
      return processSampleInputODS(oDoc, u, sampleService, libraryService, sampleQcStore, libraryQcStore, namingScheme, indexService);
    } else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  private static String getCellValueAsString(XSSFCell cell) {
    if (cell != null) {
      switch (cell.getCellType()) {
      case XSSFCell.CELL_TYPE_BLANK:
        return null;
      case XSSFCell.CELL_TYPE_BOOLEAN:
        return String.valueOf(cell.getBooleanCellValue());
      case XSSFCell.CELL_TYPE_ERROR:
        return cell.getErrorCellString();
      case XSSFCell.CELL_TYPE_FORMULA:
        return cell.getRawValue();
      case XSSFCell.CELL_TYPE_NUMERIC:
        return String.valueOf(cell.getNumericCellValue());
      case XSSFCell.CELL_TYPE_STRING:
        return cell.getStringCellValue();
      default:
        return null;
      }
    }
    return null;
  }

  public static OdfTextDocument createSampleDeliveryForm(List<Sample> samples, File outpath, Boolean plate) throws Exception {
    Collections.sort(samples, new AliasComparator<>());

    InputStream in = FormUtils.class.getResourceAsStream("/forms/odt/sampleDeliveryForm.odt");

    if (in != null) {
      OdfTextDocument oDoc = OdfTextDocument.loadDocument(in);
      OdfContentDom contentDom = oDoc.getContentDom();
      OdfTable oTable = oDoc.getTableByName("SamplesTable");

      int rowCount = 1;

      for (Sample s : samples) {
        OdfTableRow row = oTable.appendRow();

        OdfTableCell cell0 = row.getCellByIndex(0);
        OdfTextParagraph cp0 = new OdfTextParagraph(contentDom);
        cp0.setTextContent(s.getAlias());
        cp0.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell0.getOdfElement().appendChild(cp0);

        OdfTableCell cell1 = row.getCellByIndex(1);
        OdfTextParagraph cp1 = new OdfTextParagraph(contentDom);
        if (!plate) {
          cp1.setTextContent("NA");
        } else {
          cp1.setTextContent(getPlatePosition(rowCount));
        }
        cp1.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell1.getOdfElement().appendChild(cp1);

        OdfTableCell cell2 = row.getCellByIndex(2);
        OdfTextParagraph cp2 = new OdfTextParagraph(contentDom);
        cp2.setTextContent(s.getScientificName());
        cp2.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell2.getOdfElement().appendChild(cp2);

        OdfTableCell cell3 = row.getCellByIndex(3);
        OdfTextParagraph cp3 = new OdfTextParagraph(contentDom);
        cp3.setTextContent(s.getIdentificationBarcode());
        cp3.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell3.getOdfElement().appendChild(cp3);

        OdfTableCell cell4 = row.getCellByIndex(4);
        OdfTextParagraph cp4 = new OdfTextParagraph(contentDom);
        cp4.setTextContent(s.getSampleType());
        cp4.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell4.getOdfElement().appendChild(cp4);

        rowCount++;
      }

      int count = 0;
      for (OdfTableRow row : oTable.getRowList()) {
        if (count % 2 != 0) {
          for (int i = 0; i < row.getCellCount(); i++) {
            row.getCellByIndex(i).setCellBackgroundColor("#EEEEEE");
          }
        }
        count++;
      }

      oDoc.save(outpath);
      return oDoc;
    } else {
      throw new Exception("Could not read from resource");
    }
  }

  public static String getPlatePosition(int rowCount) {
    int columnIndex = Math.round(rowCount / 8);
    int remainder = rowCount % 8;

    if (remainder == 0) {
      return "H" + columnIndex;
    } else {
      columnIndex++;
      if (remainder == 1) {
        return "A" + columnIndex;
      } else if (remainder == 2) {
        return "B" + columnIndex;
      } else if (remainder == 3) {
        return "C" + columnIndex;
      } else if (remainder == 4) {
        return "D" + columnIndex;
      } else if (remainder == 5) {
        return "E" + columnIndex;
      } else if (remainder == 6) {
        return "F" + columnIndex;
      } else if (remainder == 7) {
        return "G" + columnIndex;
      } else {
        return "NA";
      }
    }
  }

  public static List<Sample> importSampleDeliveryForm(File inPath) throws Exception {
    List<Sample> samples = new ArrayList<>();
    OdfTextDocument oDoc = (OdfTextDocument) OdfDocument.loadDocument(inPath);
    OdfTable sampleTable = oDoc.getTableList().get(1);

    if (sampleTable != null) {
      for (OdfTableRow row : sampleTable.getRowList()) {
        if (row.getRowIndex() != 0) {
          TableTableRowElement ttre = row.getOdfElement();

          Sample s = new SampleImpl();

          Node n1 = ttre.getChildNodes().item(0);
          if (n1.getFirstChild() != null) {
            s.setAlias(n1.getFirstChild().getTextContent());
          }
          // well
          Node n2 = ttre.getChildNodes().item(1);
          if (n2.getFirstChild() != null) {
            if (!isStringEmptyOrNull(n2.getFirstChild().getTextContent())) {
              Note noteWell = new Note();
              noteWell.setText("well:" + n2.getFirstChild().getTextContent());
              s.addNote(noteWell);
            }
          }

          Node n3 = ttre.getChildNodes().item(2);
          if (n3.getFirstChild() != null) {
            s.setScientificName(n3.getFirstChild().getTextContent());
          }

          Node n4 = ttre.getChildNodes().item(3);
          if (n4.getFirstChild() != null) {
            s.setIdentificationBarcode(n4.getFirstChild().getTextContent());
          }

          Node n5 = ttre.getChildNodes().item(5);
          if (n5.getFirstChild() != null) {
            s.setDescription(n5.getFirstChild().getTextContent());
          }

          Node n9 = ttre.getChildNodes().item(9);
          if (n9.getFirstChild() != null) {
            if (!isStringEmptyOrNull(n9.getFirstChild().getTextContent())) {
              Note note1 = new Note();
              note1.setText("260/280:" + n9.getFirstChild().getTextContent());
              s.addNote(note1);
            }
          }

          Node n10 = ttre.getChildNodes().item(10);
          if (n10.getFirstChild() != null) {
            if (!isStringEmptyOrNull("260/230:" + n10.getFirstChild().getTextContent())) {
              Note note2 = new Note();
              note2.setText(n9.getFirstChild().getTextContent());
              s.addNote(note2);
            }
          }

          samples.add(s);
        }
      }
    } else {
      throw new DeliveryFormException("Cannot resolve sample table. Please check your delivery form.");
    }
    return samples;
  }

  public static void createBoxContentsSpreadsheet(File outpath, ArrayList<String> array) throws IOException {
    InputStream in = null;
    in = FormUtils.class.getResourceAsStream("/forms/ods/box_input.xlsx");
    if (in != null) {
      XSSFWorkbook oDoc = new XSSFWorkbook(in);

      XSSFSheet sheet = oDoc.getSheet("Input");
      FileOutputStream fileOut = new FileOutputStream(outpath);

      String boxInfo = array.remove(0);
      String boxName = boxInfo.split(":")[0];
      String boxAlias = boxInfo.split(":")[1];
      XSSFRow row1 = sheet.createRow(1);
      XSSFCell cellA = row1.createCell(0);
      cellA.setCellValue(boxName);
      XSSFCell cellB = row1.createCell(1);
      cellB.setCellValue(boxAlias);

      int i = 4; // start on row 4 of the sheet
      for (String item : array) {
        String position = item.split(":")[0];
        String name = item.split(":")[1];
        String alias = item.split(":")[2];

        XSSFRow row = sheet.createRow(i);
        cellA = row.createCell(0);
        cellA.setCellValue(position);
        cellB = row.createCell(1);
        cellB.setCellValue(name);
        XSSFCell cellC = row.createCell(2);
        cellC.setCellValue(alias);
        i++;
      }
      oDoc.write(fileOut);
      fileOut.close();
    } else {
      throw new IOException("Could not read from resource.");
    }
  }

  public static List<Index> matchIndicesFromText(Iterable<Index> allowIndices, String indexText) throws InputFormException {
    List<Index> matchedIndices = new ArrayList<>();
    String[] splits = indexText.split("-");
    for (String tag : splits) {
      boolean success = false;
      for (Index index : allowIndices) {
        if (index.getName().equals(tag) || index.getSequence().equals(indexText)) {
          matchedIndices.add(index);
          success = true;
          break;
        }
        if (!success) {
          throw new InputFormException("Unknown index: " + tag);
        }
      }
    }
    return matchedIndices;
  }
}
