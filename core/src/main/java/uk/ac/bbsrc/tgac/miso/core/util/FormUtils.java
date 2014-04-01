/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
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

package uk.ac.bbsrc.tgac.miso.core.util;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.odftoolkit.odfdom.converter.itext.ODF2PDFViaITextConverter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Node;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.*;
import uk.ac.bbsrc.tgac.miso.core.exception.DeliveryFormException;
import uk.ac.bbsrc.tgac.miso.core.exception.InputFormException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.RequestManagerAwareNamingScheme;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @date 06-Sep-2011
 * @since 0.1.1
 */
public class FormUtils {

  protected static final Logger log = LoggerFactory.getLogger(FormUtils.class);

  private static final Pattern digitPattern = Pattern.compile("(^[0-9]+)[\\.0-9]*");
  private static final Pattern samplePattern = Pattern.compile("([A-z0-9]+)_S([A-z0-9]+)_(.*)");

  public static void createSampleInputSpreadsheet(Collection<Sample> samples, File outpath) throws Exception {
    Collections.sort(new ArrayList<Sample>(samples), new AliasComparator(Sample.class));

    InputStream in = null;
    if (outpath.getName().endsWith(".xlsx")) {
      in = FormUtils.class.getResourceAsStream("/forms/ods/bulk_input.xlsx");
      if (in != null) {
        XSSFWorkbook oDoc = new XSSFWorkbook(in);
        FileOutputStream fileOut = new FileOutputStream(outpath);
        oDoc.write(fileOut);
        fileOut.close();
      }
      else {
        throw new IOException("Could not read from resource.");
      }
    }
    else if (outpath.getName().endsWith(".ods")) {
      in = FormUtils.class.getResourceAsStream("/forms/ods/bulk_input.ods");
      if (in != null) {
        OdfSpreadsheetDocument oDoc = OdfSpreadsheetDocument.loadDocument(in);
        oDoc.save(outpath);
      }
      else {
        throw new IOException("Could not read from resource.");
      }
    }
    else {
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
      }
      else {
        throw new IOException("Could not read from resource.");
      }
    }
    else if (outpath.getName().endsWith(".ods")) {
      in = FormUtils.class.getResourceAsStream("/forms/ods/plate_input.ods");
      if (in != null) {
        OdfSpreadsheetDocument oDoc = OdfSpreadsheetDocument.loadDocument(in);
        oDoc.save(outpath);
      }
      else {
        throw new IOException("Could not read from resource.");
      }
    }
    else {
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
          //"sampleid:wellid:samplealias:projectname:projectalias:dnaOrRNA"
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
          }
          else if ("D".equals(dnaOrRNA)) {
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
    }
    else {
      throw new IOException("Could not read from resource.");
    }

  }

  public static void createLibraryPoolExportFormFromWeb(File outpath, JSONArray jsonArray, String barcodekit) throws Exception {
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
        }
        else if ("platform".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellB = row2.createCell(1);
          row2cellB.setCellValue(jsonObject.getString("value"));
        }
        else if ("type".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellC = row2.createCell(2);
          row2cellC.setCellValue(jsonObject.getString("value"));
        }
        else if ("selection".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellD = row2.createCell(3);
          row2cellD.setCellValue(jsonObject.getString("value"));
        }
        else if ("strategy".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellE = row2.createCell(4);
          row2cellE.setCellValue(jsonObject.getString("value"));
        }
        if ("sampleinwell".equals(jsonObject.getString("name"))) {
          String sampleinwell = jsonObject.getString("value");
          //"sampleid:wellid:samplealias:projectname:projectalias:dnaOrRNA"
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
          if (barcodekit != null) {
            XSSFCell cellJ = row.createCell(9);
            cellJ.setCellValue(barcodekit);
          }
          i++;
        }
      }
      oDoc.write(fileOut);
      fileOut.close();
    }
    else {
      throw new IOException("Could not read from resource.");
    }

  }

  public static void createLibraryPoolExportForm(File outpath, JSONArray jsonArray) throws Exception {
    InputStream in = null;
    in = FormUtils.class.getResourceAsStream("/forms/ods/export_libraries_pools.xlsx");
    if (in != null) {
      XSSFWorkbook oDoc = new XSSFWorkbook(in);

      XSSFSheet sheet = oDoc.getSheet("library_pool_export");
      FileOutputStream fileOut = new FileOutputStream(outpath);

      int i = 6;
      for (JSONArray jsonArrayElement : (Iterable<JSONArray>) jsonArray) {
        XSSFRow row = sheet.createRow(i);
        XSSFCell cellA = row.createCell(0);
        cellA.setCellValue(jsonArrayElement.getString(0));
        XSSFCell cellB = row.createCell(1);
        cellB.setCellValue(jsonArrayElement.getString(1));
        XSSFCell cellC = row.createCell(2);
        cellC.setCellValue(jsonArrayElement.getString(2));
        XSSFCell cellD = row.createCell(3);
        cellD.setCellValue(jsonArrayElement.getString(3));
        XSSFCell cellE = row.createCell(4);
        cellE.setCellValue(jsonArrayElement.getString(4));
        XSSFCell cellF = row.createCell(5);
        cellF.setCellValue(jsonArrayElement.getString(5));
        i++;
      }
      oDoc.write(fileOut);
      fileOut.close();
    }
    else {
      throw new IOException("Could not read from resource.");
    }

  }

  public static void createPlateExportForm(File outpath, JSONArray jsonArray) throws Exception {
    InputStream in = null;
    in = FormUtils.class.getResourceAsStream("/forms/ods/plate_input.xlsx");
    if (in != null) {
      XSSFWorkbook oDoc = new XSSFWorkbook(in);

      XSSFSheet sheet = oDoc.getSheet("Input");
      FileOutputStream fileOut = new FileOutputStream(outpath);
      XSSFRow row2 = sheet.getRow(1);
      int i = 4;
      for (JSONObject jsonObject : (Iterable<JSONObject>) jsonArray) {
        if ("platebarcode".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellF = row2.createCell(5);
          row2cellF.setCellValue(jsonObject.getString("value"));
        }
        else if ("paired".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellA = row2.createCell(0);
          row2cellA.setCellValue(jsonObject.getString("value"));
        }
        else if ("platforms".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellB = row2.createCell(1);
          row2cellB.setCellValue(jsonObject.getString("value"));
        }
        else if ("types".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellC = row2.createCell(2);
          row2cellC.setCellValue(jsonObject.getString("value"));
        }
        else if ("selections".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellD = row2.createCell(3);
          row2cellD.setCellValue(jsonObject.getString("value"));
        }
        else if ("strategies".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellE = row2.createCell(4);
          row2cellE.setCellValue(jsonObject.getString("value"));
        }
        else if ("sampleinwell".equals(jsonObject.getString("name"))) {
          String sampleinwell = jsonObject.getString("value");
          //"sampleid:wellid:samplealias:projectname"
          //String sampleId = sampleinwell.split(":")[0];
          String wellId = sampleinwell.split(":")[1];
          String sampleAlias = sampleinwell.split(":")[2];
          String projectName = sampleinwell.split(":")[3];
          XSSFRow row = sheet.createRow(i);
          XSSFCell cellA = row.createCell(0);
          cellA.setCellValue(wellId);
          XSSFCell cellB = row.createCell(1);
          cellB.setCellValue(projectName);
          XSSFCell cellC = row.createCell(2);
          cellC.setCellValue(sampleAlias);
          i++;
        }
      }
      oDoc.write(fileOut);
      fileOut.close();
    }
    else {
      throw new IOException("Could not read from resource.");
    }

  }

  //private static Map<String, Pool<Plate<LinkedList<Library>, Library>>> process384PlateInputODS(OdfSpreadsheetDocument oDoc, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
  private static Map<String, PlatePool> process384PlateInputODS(OdfSpreadsheetDocument oDoc, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme) libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
    OdfTable oTable = oDoc.getTableList().get(0);

    //process global headers
    OdfTableCell pairedCell = oTable.getCellByPosition("A2");
    boolean paired = false;
    if (pairedCell.getBooleanValue() != null) {
      paired = pairedCell.getBooleanValue();
      log.info("Got paired: " + paired);
    }

    OdfTableCell platformCell = oTable.getCellByPosition("B2");
    PlatformType pt = null;
    if (!"".equals(platformCell.getStringValue())) {
      pt = PlatformType.get(platformCell.getStringValue());
    }
    if (pt == null) {
      throw new InputFormException("Cannot resolve Platform type from: '" + platformCell.getStringValue() + "'");
    }
    else {
      log.info("Got platform type: " + pt.getKey());
    }

    OdfTableCell typeCell = oTable.getCellByPosition("C2");
    LibraryType lt = null;
    if (!"".equals(typeCell.getStringValue())) {
      String[] split = typeCell.getStringValue().split("-");
      String plat = split[0];
      String type = split[1];
      if (platformCell.getStringValue().equals(plat)) {
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
      }
      else {
        throw new InputFormException("Selected library type '" + typeCell.getStringValue() + "' doesn't match platform type: '" + platformCell.getStringValue() + "'");
      }
    }
    if (lt == null) {
      throw new InputFormException("Cannot resolve Library type from: '" + typeCell.getStringValue() + "'");
    }
    else {
      log.info("Got library type: " + lt.getDescription());
    }

    OdfTableCell selectionCell = oTable.getCellByPosition("D2");
    LibrarySelectionType ls = null;
    if (!"".equals(selectionCell.getStringValue())) {
      ls = manager.getLibrarySelectionTypeByName(selectionCell.getStringValue());
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + selectionCell.getStringValue() + "'");
    }
    else {
      log.info("Got library selection type: " + ls.getName());
    }

    OdfTableCell strategyCell = oTable.getCellByPosition("E2");
    LibraryStrategyType lst = null;
    if (!"".equals(strategyCell.getStringValue())) {
      lst = manager.getLibraryStrategyTypeByName(strategyCell.getStringValue());
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + strategyCell.getStringValue() + "'");
    }
    else {
      log.info("Got library strategy type: " + lst.getName());
    }

    OdfTableCell plateBarcodeCell = oTable.getCellByPosition("F2");
    String plateBarcode = null;
    if (!"".equals(plateBarcodeCell.getStringValue())) {
      plateBarcode = plateBarcodeCell.getStringValue();
    }
    if (plateBarcode == null) {
      throw new InputFormException("Cannot resolve plate barcode from: '" + plateBarcodeCell.getStringValue() + "'");
    }
    else {
      log.info("Got plate parcode: " + plateBarcode);
    }

    //process entries
    Simple384WellPlate libraryPlate = null;
    //Map<String, Pool<Plate<LinkedList<Library>, Library>>> pools = new HashMap<String, Pool<Plate<LinkedList<Library>, Library>>>();
    Map<String, PlatePool> pools = new HashMap<String, PlatePool>();
    for (OdfTableRow row : oTable.getRowList()) {
      int ri = row.getRowIndex();
      if (ri > 3) {
        // Ax - plate position
        OdfTableCell platePosCell = oTable.getCellByPosition(0, ri);
        if (!"".equals(platePosCell.getStringValue()) && libraryPlate == null) {
          //plated libraries - process as plate
          libraryPlate = new Simple384WellPlate();
          libraryPlate.setIdentificationBarcode(plateBarcode);
          libraryPlate.setCreationDate(new Date());
        }

        //cell defs
        OdfTableCell sampleAliasCell = oTable.getCellByPosition(1, ri);

        Sample s = null;
        if (!"".equals(sampleAliasCell.getStringValue())) {
          Collection<Sample> ss = manager.listSamplesByAlias(sampleAliasCell.getStringValue());
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            }
            else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + sampleAliasCell.getStringValue() + "'. Cannot process.");
            }
          }
          else {
            throw new InputFormException("No such sample '" + sampleAliasCell.getStringValue() + "'in database. Samples need to be created before using the form input functionality");
          }
        }
        else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        //sample OK - good to go
        if (s != null) {
          OdfTableCell entityIDCell = oTable.getCellByPosition(2, ri);
          OdfTableCell poolNumberCell = oTable.getCellByPosition(3, ri);
          OdfTableCell sampleQcCell = oTable.getCellByPosition(4, ri);
          //OdfTableCell sampleAmountCell = oTable.getCellByPosition(5, ri);
          //OdfTableCell sampleWaterAmountCell = oTable.getCellByPosition(6, ri);
          OdfTableCell barcodeKitCell = oTable.getCellByPosition(7, ri);
          OdfTableCell barcodeTagsCell = oTable.getCellByPosition(8, ri);
          OdfTableCell libraryQcCell = oTable.getCellByPosition(9, ri);
          OdfTableCell libraryQcInsertSizeCell = oTable.getCellByPosition(10, ri);
          OdfTableCell libraryQcMolarityCell = oTable.getCellByPosition(11, ri);
          OdfTableCell libraryQcPassFailCell = oTable.getCellByPosition(12, ri);
          //OdfTableCell libraryAmountCell = oTable.getCellByPosition(13, ri);
          //OdfTableCell libraryWaterAmountCell = oTable.getCellByPosition(14, ri);
          //OdfTableCell dilutionQcCell = oTable.getCellByPosition(15, ri);
          OdfTableCell dilutionMolarityCell = oTable.getCellByPosition(16, ri);
          //OdfTableCell dilutionAmountCell = oTable.getCellByPosition(17, ri);
          //OdfTableCell dilutionWaterAmountCell = oTable.getCellByPosition(18, ri);
          OdfTableCell poolQcCell = oTable.getCellByPosition(19, ri);
          //OdfTableCell poolAverageInsertSizeCell = oTable.getCellByPosition(20, ri);
          OdfTableCell poolConvertedMolarityCell = oTable.getCellByPosition(21, ri);

          //add pool, if any
          if (!"".equals(poolNumberCell.getStringValue())) {
            if (!pools.containsKey(poolNumberCell.getStringValue())) {
              PlatePool pool = new PlatePool();
              pool.setAlias(poolNumberCell.getStringValue());
              pool.setPlatformType(pt);
              pool.setReadyToRun(true);
              pool.setCreationDate(new Date());
              pools.put(poolNumberCell.getStringValue(), pool);
              log.info("Added pool: " + poolNumberCell.getStringValue());
            }
          }

          //process sample QC
          if (!"".equals(sampleQcCell.getStringValue())) {
            try {
              SampleQC sqc = new SampleQCImpl();
              sqc.setSample(s);
              sqc.setResults(Double.valueOf(sampleQcCell.getStringValue()));
              sqc.setQcCreator(u.getLoginName());
              sqc.setQcDate(new Date());
              sqc.setQcType(manager.getSampleQcTypeByName("Picogreen"));
              if (!s.getSampleQCs().contains(sqc)) {
                s.addQc(sqc);
                log.info("Added sample QC: " + sqc.toString());
              }
            }
            catch (NumberFormatException nfe) {
              throw new InputFormException("Supplied Sample QC concentration for sample '" + sampleAliasCell.getStringValue() + "' is invalid", nfe);
            }
          }

          //if (!"".equals(libraryQcCell.getStringValue())) {
          if (barcodeKitCell.getStringValue() != null && barcodeTagsCell.getStringValue() != null) {
            //create library
            Library library = new LibraryImpl();
            library.setSample(s);

            Matcher mat = samplePattern.matcher(s.getAlias());
            if (mat.matches()) {
              String libAlias = plateBarcode + "_" + "L" + mat.group(2) + "-" + platePosCell.getStringValue() + "_" + entityIDCell.getStringValue();
              //String libAlias = libraryNamingScheme.generateNameFor("alias", library);
              //library.setAlias(libAlias);

              library.setAlias(libAlias);
              library.setSecurityProfile(s.getSecurityProfile());
              library.setDescription(s.getDescription());
              library.setCreationDate(new Date());
              library.setPlatformName(pt.name());
              library.setLibraryType(lt);
              library.setLibrarySelectionType(ls);
              library.setLibraryStrategyType(lst);
              library.setPaired(paired);

              if (!"".equals(libraryQcMolarityCell.getStringValue())) {
                int insertSize = 0;
                try {
                  String bp = libraryQcInsertSizeCell.getStringValue();
                  Matcher m = digitPattern.matcher(bp);
                  if (m.matches()) {
                    insertSize = Integer.valueOf(m.group(1));
                  }
                  else {
                    throw new InputFormException("Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid");
                  }
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }

                try {
                  LibraryQC lqc = new LibraryQCImpl();
                  lqc.setLibrary(library);
                  lqc.setInsertSize(insertSize);
                  lqc.setResults(Double.valueOf(libraryQcMolarityCell.getStringValue()));
                  lqc.setQcCreator(u.getLoginName());
                  lqc.setQcDate(new Date());
                  lqc.setQcType(manager.getLibraryQcTypeByName("Picogreen"));
                  if (!library.getLibraryQCs().contains(lqc)) {
                    library.addQc(lqc);
                    log.info("Added library QC: " + lqc.toString());
                  }

                  if (insertSize == 0 && lqc.getResults() == 0) {
                    library.setQcPassed(false);
                  }
                  else {
                    //TODO check libraryQcPassFailCell?
                    library.setQcPassed(true);
                  }
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied Library QC concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              if (!"".equals(barcodeKitCell.getStringValue())) {
                Collection<TagBarcode> bcs = manager.listAllTagBarcodesByStrategyName(barcodeKitCell.getStringValue());
                if (!bcs.isEmpty()) {
                  String tags = barcodeTagsCell.getStringValue();
                  if (!"".equals(tags)) {
                    HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
                    if (tags.contains("-")) {
                      String[] splits = tags.split("-");
                      int count = 1;
                      for (String tag : splits) {
                        for (TagBarcode tb : bcs) {
                          if (tb.getName().equals(tag)) {
                            //set tag barcodes
                            tbs.put(count, tb);
                            count++;
                          }
                        }
                      }
                    }
                    else {
                      for (TagBarcode tb : bcs) {
                        if (tb.getName().equals(tags)) {
                          //set tag barcode
                          tbs.put(1, tb);
                        }
                      }
                    }

                    library.setTagBarcodes(tbs);
                  }
                  else {
                    throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '" + sampleAliasCell.getStringValue() + "'.");
                  }
                }
                else {
                  throw new InputFormException("No tag barcodes associated with this kit definition: '" + barcodeKitCell.getStringValue() + "'.");
                }
              }

              /*
              if (!"".equals(dilutionMolarityCell.getStringValue())) {
                try {
                  LibraryDilution ldi = new LibraryDilution();
                  ldi.setLibrary(library);
                  ldi.setSecurityProfile(library.getSecurityProfile());
                  ldi.setConcentration(Double.valueOf(dilutionMolarityCell.getStringValue()));
                  ldi.setCreationDate(new Date());
                  ldi.setDilutionCreator(u.getLoginName());
                  if (!library.getLibraryDilutions().contains(ldi)) {
                    library.addDilution(ldi);
                    log.info("Added library dilution: " + ldi.toString());
                  }

                  Pool<Plate<LinkedList<Library>, Library>> p = pools.get(poolNumberCell.getStringValue());
                  if (p != null) {
                    p.addPoolableElement(ldi);
                    log.info("Added library dilution to pool: " + p.toString());
                  }
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied LibraryDilution concentration for library '"+libAlias+"' ("+s.getAlias()+") is invalid", nfe);
                }
              }
              */

              if (!"".equals(poolConvertedMolarityCell.getStringValue())) {
                Pool<Plate<LinkedList<Library>, Library>> p = pools.get(poolNumberCell.getStringValue());
                if (p != null) {
                  log.debug("Retrieved pool " + poolNumberCell.getStringValue());
                  try {
                    double d = Double.valueOf(poolConvertedMolarityCell.getStringValue());
                    p.setConcentration(d);
                  }
                  catch (NumberFormatException nfe) {
                    throw new InputFormException("Supplied pool concentration for pool '" + poolNumberCell.getStringValue() + "' is invalid", nfe);
                  }
                }
              }

              log.info("Added library: " + library.toString());

              if (!"".equals(platePosCell.getStringValue()) && libraryPlate != null) {
                //libraryPlate.setElement(platePosCell.getStringValue(), library);
                libraryPlate.addElement(library);
                log.info("Added library " + library.getAlias() + " to " + platePosCell.getStringValue());
              }

              samples.add(s);

              Pool<Plate<LinkedList<Library>, Library>> p = pools.get(poolNumberCell.getStringValue());
              if (p != null && !p.getPoolableElements().contains(libraryPlate)) {
                p.addPoolableElement(libraryPlate);
                log.info("Added plate to pool: " + p.toString());
              }
            }
            else {
              log.error("Cannot generate library alias from specified parent sample alias. Does it match the required schema?");
            }
          }
        }
      }
    }
    log.info("Done");
    return pools;
  }

  public static JSONArray preProcessSampleSheetImport(File inPath, User u, RequestManager manager) throws Exception {
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
          Collection<Sample> ss = manager.listSamplesByAlias(salias);
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            }
            else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
            }
          }
          else {
            throw new InputFormException("No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
          }
        }
        else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        //sample OK - good to go
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
          }
          else {
            sampleArray.add("");

          }

          XSSFCell qcResultCell = null;


          if ("GENOMIC".equals(s.getSampleType())
              || "METAGENOMIC".equals(s.getSampleType())) {
            qcResultCell = row.getCell(6);
          }
          else if ("NON GENOMIC".equals(s.getSampleType())
                   || "VIRAL RNA".equals(s.getSampleType())
                   || "TRANSCRIPTOMIC".equals(s.getSampleType())
                   || "METATRANSCRIPTOMIC".equals(s.getSampleType())) {
            qcResultCell = row.getCell(7);
          }
          else {

            if (!"NA".equals(getCellValueAsString(row.getCell(6)))) {
              qcResultCell = row.getCell(6);
            }
            else if (!"NA".equals(getCellValueAsString(row.getCell(7)))) {
              qcResultCell = row.getCell(7);
            }
          }


          XSSFCell rinCell = row.getCell(8);
          XSSFCell sample260280Cell = row.getCell(9);
          XSSFCell sample260230Cell = row.getCell(10);
          Date date = new Date();

          try {
            if (getCellValueAsString(qcResultCell) != null && !"NA".equals(getCellValueAsString(qcResultCell))) {

              sampleArray.add(Double.valueOf(getCellValueAsString(qcResultCell)));
              if (getCellValueAsString(qcPassedCell) != null) {
                if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
                  sampleArray.add("true");
                }
                else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
                  sampleArray.add("false");
                }

              }
            }
            else {
              sampleArray.add("");
              sampleArray.add("");
            }

            StringBuilder noteSB = new StringBuilder();
            if (getCellValueAsString(rinCell) != null && !"".equals(getCellValueAsString(rinCell)) && !"NA".equals(getCellValueAsString(rinCell))) {
              noteSB.append("RIN:" + getCellValueAsString(rinCell) + ";");
            }
            if (getCellValueAsString(sample260280Cell) != null && !"".equals(getCellValueAsString(sample260280Cell))) {
              noteSB.append("260/280:" + getCellValueAsString(sample260280Cell) + ";");
            }
            if (getCellValueAsString(sample260230Cell) != null && !"".equals(getCellValueAsString(sample260230Cell))) {
              noteSB.append("260/230:" + getCellValueAsString(sample260230Cell) + ";");
            }
            sampleArray.add(noteSB.toString());
          }
          catch (NumberFormatException nfe) {
            throw new InputFormException("Supplied Sample QC concentration for sample '" + getCellValueAsString(sampleAliasCell) + "' is invalid", nfe);
          }
          jsonArray.add(sampleArray);
        }
      }
      return jsonArray;
    }
    else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  public static JSONArray processSampleSheetImport(File inPath, User u, RequestManager manager) throws Exception {
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
          Collection<Sample> ss = manager.listSamplesByAlias(salias);
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            }
            else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
            }
          }
          else {
            throw new InputFormException("No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
          }
        }
        else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        //sample OK - good to go
        if (s != null) {
          JSONObject jsonObject = new JSONObject();

          XSSFCell projectNameCell = row.getCell(0);
          XSSFCell projectAliasCell = row.getCell(1);
          XSSFCell sampleNameCell = row.getCell(2);
          XSSFCell wellCell = row.getCell(4);
          XSSFCell adaptorCell = row.getCell(5);
          XSSFCell qcPassedCell = row.getCell(13);

          jsonObject.put("row", ri);
          jsonObject.put("projectName", getCellValueAsString(projectNameCell));
          jsonObject.put("projectAlias", getCellValueAsString(projectAliasCell));
          jsonObject.put("sampleName", getCellValueAsString(sampleNameCell));
          jsonObject.put("sampleAlias", getCellValueAsString(sampleAliasCell));
          jsonObject.put("well", getCellValueAsString(wellCell));
          if ((getCellValueAsString(adaptorCell)) != null) {
            jsonObject.put("adaptor", getCellValueAsString(adaptorCell));
          }
          else {
            jsonObject.put("adaptor", "");

          }

          jsonArray.add(jsonObject);
          XSSFCell qcResultCell = null;
          if (!"NA".equals(getCellValueAsString(row.getCell(6)))) {
            qcResultCell = row.getCell(6);
          }
          else if (!"NA".equals(getCellValueAsString(row.getCell(7)))) {
            qcResultCell = row.getCell(7);
          }
          XSSFCell rinCell = row.getCell(8);
          XSSFCell sample260280Cell = row.getCell(9);
          XSSFCell sample260230Cell = row.getCell(10);
          Date date = new Date();

          try {
            if (getCellValueAsString(qcResultCell) != null && !"NA".equals(getCellValueAsString(qcResultCell))) {

              SampleQC sqc = new SampleQCImpl();
              sqc.setSample(s);
              sqc.setResults(Double.valueOf(getCellValueAsString(qcResultCell)));
              sqc.setQcCreator(u.getLoginName());
              sqc.setQcDate(date);
              if (manager.getSampleQcTypeByName("Picogreen") != null) {
                sqc.setQcType(manager.getSampleQcTypeByName("Picogreen"));
              }
              else {
                sqc.setQcType(manager.getSampleQcTypeByName("QuBit"));
              }
              if (!s.getSampleQCs().contains(sqc)) {
                s.addQc(sqc);
                manager.saveSampleQC(sqc);
                manager.saveSample(s);
                log.info("Added sample QC: " + sqc.toString());
              }
              if (getCellValueAsString(qcPassedCell) != null) {
                if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
                  s.setQcPassed(true);
                  manager.saveSample(s);
                  log.info("Marked sample QC Passed as True");
                }
                else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
                  s.setQcPassed(false);
                  manager.saveSample(s);
                  log.info("Marked sample QC Passed as False");
                }

              }
            }
            if (getCellValueAsString(wellCell) != null && !"".equals(getCellValueAsString(wellCell)) && !"NA".equals(getCellValueAsString(wellCell))) {
              Note note = new Note();
              note.setCreationDate(date);
              note.setOwner(u);
              note.setText("Well: " + getCellValueAsString(wellCell));
              if (!s.getNotes().contains(note)) {
                s.addNote(note);
                manager.saveSampleNote(s, note);
                manager.saveSample(s);
                log.info("Added sample Note for Well: " + note.toString());
              }
            }
            if (getCellValueAsString(rinCell) != null && !"".equals(getCellValueAsString(rinCell)) && !"NA".equals(getCellValueAsString(rinCell))) {
              Note note = new Note();
              note.setCreationDate(date);
              note.setOwner(u);
              note.setText("RIN: " + getCellValueAsString(rinCell));
              if (!s.getNotes().contains(note)) {
                s.addNote(note);
                manager.saveSampleNote(s, note);
                manager.saveSample(s);
                log.info("Added sample Note for RIN: " + note.toString());
              }
            }
            if (getCellValueAsString(sample260280Cell) != null && !"".equals(getCellValueAsString(sample260280Cell))) {
              Note note = new Note();
              note.setCreationDate(date);
              note.setOwner(u);
              note.setText("260/280: " + getCellValueAsString(sample260280Cell));
              if (!s.getNotes().contains(note)) {
                s.addNote(note);
                manager.saveSampleNote(s, note);
                manager.saveSample(s);
                log.info("Added sample Note for 260/280: " + note.toString());
              }
            }
            if (getCellValueAsString(sample260230Cell) != null && !"".equals(getCellValueAsString(sample260230Cell))) {
              Note note = new Note();
              note.setCreationDate(date);
              note.setOwner(u);
              note.setText("260/230: " + getCellValueAsString(sample260230Cell));
              if (!s.getNotes().contains(note)) {
                s.addNote(note);
                manager.saveSampleNote(s, note);
                manager.saveSample(s);
                log.info("Added sample Note for 260/230: " + note.toString());
              }
            }

          }
          catch (NumberFormatException nfe) {
            throw new InputFormException("Supplied Sample QC concentration for sample '" + getCellValueAsString(sampleAliasCell) + "' is invalid", nfe);
          }
        }
      }
      return jsonArray;
    }
    else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  public static JSONObject preProcessLibraryPoolSheetImport(File inPath, User u, RequestManager manager) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      JSONObject jsonObject = new JSONObject();
      JSONArray sampleArray = new JSONArray();
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      XSSFSheet sheet = wb.getSheetAt(0);

      XSSFRow glrow = sheet.getRow(1);

      //process global headers
      XSSFCell pairedCell = glrow.getCell(0);
      jsonObject.put("paired", getCellValueAsString(pairedCell));

      XSSFCell platformCell = glrow.getCell(1);
      if (getCellValueAsString(platformCell) != null) {
        jsonObject.put("platform", getCellValueAsString(platformCell));
      }
      else {
        throw new InputFormException("Cannot resolve Platform type from: '" + getCellValueAsString(platformCell) + "'");
      }

      XSSFCell typeCell = glrow.getCell(2);
      if (getCellValueAsString(typeCell) != null) {
        String[] split = getCellValueAsString(typeCell).split("-");
        String plat = split[0];
        String type = split[1];
        if (getCellValueAsString(platformCell).equals(plat)) {
          jsonObject.put("type", type);
        }
        else {
          throw new InputFormException("Selected library type '" + getCellValueAsString(typeCell) + "' doesn't match platform type: '" + getCellValueAsString(platformCell) + "'");
        }
      }
      else {
        throw new InputFormException("Cannot resolve Library type from: '" + getCellValueAsString(typeCell) + "'");
      }

      XSSFCell selectionCell = glrow.getCell(3);
      if (getCellValueAsString(selectionCell) != null) {
        jsonObject.put("selection", getCellValueAsString(selectionCell));
      }
      else {
        throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
      }

      XSSFCell strategyCell = glrow.getCell(4);
      if (getCellValueAsString(strategyCell) != null) {
        jsonObject.put("strategy", getCellValueAsString(strategyCell));
      }
      else {
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
          Collection<Sample> ss = manager.listSamplesByAlias(salias);
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            }
            else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
            }
          }
          else {
            throw new InputFormException("No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
          }
        }
        else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        //sample OK - good to go
        if (s != null) {
          XSSFCell barcodeKitCell = row.getCell(9);
          XSSFCell barcodeTagsCell = row.getCell(10);
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
          rowsJSONArray.add(getCellValueAsString(wellCell));

          String libAlias = "";
          Matcher mat = samplePattern.matcher(getCellValueAsString(sampleAliasCell));
          if (mat.matches()) {
            String platePos = getCellValueAsString(wellCell);
            libAlias = mat.group(1) + "_" + "L" + mat.group(2) + "-" + platePos.toUpperCase() + "_" + mat.group(3);
          }
          rowsJSONArray.add(libAlias);

          String libDesc = s.getDescription();
          if (getCellValueAsString(libraryDescriptionCell) != null && "".equals(getCellValueAsString(libraryDescriptionCell))) {
            libDesc = getCellValueAsString(libraryDescriptionCell);
          }
          rowsJSONArray.add(libDesc);

          if (getCellValueAsString(libraryQubitCell) != null) {
            rowsJSONArray.add(getCellValueAsString(libraryQubitCell));
          }
          else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(libraryQcInsertSizeCell) != null) {
            rowsJSONArray.add(getCellValueAsString(libraryQcInsertSizeCell));
          }
          else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(libraryQcMolarityCell) != null) {
            rowsJSONArray.add(getCellValueAsString(libraryQcMolarityCell));
          }
          else {
            rowsJSONArray.add("");
          }


          if (getCellValueAsString(qcPassedCell) != null) {
            if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
              rowsJSONArray.add("true");
            }
            else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
              rowsJSONArray.add("false");
            }
          }
          else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(barcodeKitCell) != null) {
            rowsJSONArray.add(getCellValueAsString(barcodeKitCell));
          }
          else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(barcodeTagsCell) != null) {
            rowsJSONArray.add(getCellValueAsString(barcodeTagsCell));
          }
          else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(dilutionMolarityCell) != null) {
            rowsJSONArray.add(getCellValueAsString(dilutionMolarityCell));
          }
          else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(poolNameCell) != null) {
            rowsJSONArray.add(getCellValueAsString(poolNameCell));
          }
          else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(poolConvertedMolarityCell) != null) {
            rowsJSONArray.add(getCellValueAsString(poolConvertedMolarityCell));
          }
          else {
            rowsJSONArray.add("");
          }
        }
        sampleArray.add(rowsJSONArray);
      }
      jsonObject.put("rows", sampleArray);
      return jsonObject;
    }
    else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  public static String processLibraryPoolSheetImport(File inPath, User u, RequestManager manager) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      XSSFSheet sheet = wb.getSheetAt(0);

      XSSFRow glrow = sheet.getRow(1);

      //process global headers
      XSSFCell pairedCell = glrow.getCell(0);
      boolean paired = Boolean.parseBoolean(getCellValueAsString(pairedCell));
      log.info("Got paired: " + paired);

      XSSFCell platformCell = glrow.getCell(1);
      PlatformType pt = null;
      if (getCellValueAsString(platformCell) != null) {
        pt = PlatformType.get(getCellValueAsString(platformCell));
      }
      if (pt == null) {
        throw new InputFormException("Cannot resolve Platform type from: '" + getCellValueAsString(platformCell) + "'");
      }
      else {
        log.info("Got platform type: " + pt.getKey());
      }

      XSSFCell typeCell = glrow.getCell(2);
      LibraryType lt = null;
      if (getCellValueAsString(typeCell) != null) {
        String[] split = getCellValueAsString(typeCell).split("-");
        String plat = split[0];
        String type = split[1];
        if (getCellValueAsString(platformCell).equals(plat)) {
          lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
        }
        else {
          throw new InputFormException("Selected library type '" + getCellValueAsString(typeCell) + "' doesn't match platform type: '" + getCellValueAsString(platformCell) + "'");
        }
      }
      if (lt == null) {
        throw new InputFormException("Cannot resolve Library type from: '" + getCellValueAsString(typeCell) + "'");
      }
      else {
        log.info("Got library type: " + lt.getDescription());
      }

      XSSFCell selectionCell = glrow.getCell(3);
      LibrarySelectionType ls = null;
      if (getCellValueAsString(selectionCell) != null) {
        ls = manager.getLibrarySelectionTypeByName(getCellValueAsString(selectionCell));
      }
      if (ls == null) {
        throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
      }
      else {
        log.info("Got library selection type: " + ls.getName());
      }

      XSSFCell strategyCell = glrow.getCell(4);
      LibraryStrategyType lst = null;
      if (getCellValueAsString(strategyCell) != null) {
        lst = manager.getLibraryStrategyTypeByName(getCellValueAsString(strategyCell));
      }
      if (lst == null) {
        throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) + "'");
      }
      else {
        log.info("Got library strategy type: " + lst.getName());
      }

      Map<String, Pool> pools = new HashMap<String, Pool>();
      int rows = sheet.getPhysicalNumberOfRows();
      for (int ri = 6; ri < rows; ri++) {
        XSSFRow row = sheet.getRow(ri);
        XSSFCell sampleAliasCell = row.getCell(3);
        Sample s = null;
        if (getCellValueAsString(sampleAliasCell) != null) {
          String salias = getCellValueAsString(sampleAliasCell);
          Collection<Sample> ss = manager.listSamplesByAlias(salias);
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            }
            else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
            }
          }
          else {
            throw new InputFormException("No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
          }
        }
        else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        //sample OK - good to go
        if (s != null) {

          XSSFCell barcodeKitCell = row.getCell(9);
          XSSFCell barcodeTagsCell = row.getCell(10);
          XSSFCell libraryQubitCell = row.getCell(6);
          XSSFCell libraryQcInsertSizeCell = row.getCell(7);
          XSSFCell libraryQcMolarityCell = row.getCell(8);
          XSSFCell qcPassedCell = row.getCell(11);
          XSSFCell libraryDescriptionCell = row.getCell(12);
          XSSFCell wellCell = row.getCell(4);
          XSSFCell dilutionMolarityCell = row.getCell(16);
          XSSFCell poolNumberCell = row.getCell(21);
          XSSFCell poolConvertedMolarityCell = row.getCell(20);

          String platePos = getCellValueAsString(wellCell);
          LibraryDilution ldi = new LibraryDilution();

          Date date = new Date();
          if (getCellValueAsString(barcodeKitCell) != null && getCellValueAsString(barcodeTagsCell) != null) {
            //create library
            Library library = new LibraryImpl();
            library.setSample(s);

            Matcher mat = samplePattern.matcher(s.getAlias());
            if (mat.matches()) {
              String libAlias = mat.group(1) + "_" + "L" + mat.group(2) + "-" + platePos + "_" + mat.group(3);
              //String libAlias = libraryNamingScheme.generateNameFor("alias", library);
              //library.setAlias(libAlias);

              library.setAlias(libAlias);
              library.setSecurityProfile(s.getSecurityProfile());
              if (getCellValueAsString(libraryDescriptionCell) != null && "".equals(getCellValueAsString(libraryDescriptionCell))) {
                library.setDescription(getCellValueAsString(libraryDescriptionCell));
              }
              else {
                library.setDescription(s.getDescription());
              }
              library.setCreationDate(new Date());
              library.setPlatformName(pt.name());
              library.setLibraryType(lt);
              library.setLibrarySelectionType(ls);
              library.setLibraryStrategyType(lst);
              library.setPaired(paired);

              if (getCellValueAsString(libraryQcMolarityCell) != null) {
                int insertSize = 0;
                try {
                  String bp = getCellValueAsString(libraryQcInsertSizeCell);
                  Matcher m = digitPattern.matcher(bp);
                  if (m.matches()) {
                    insertSize = Integer.valueOf(m.group(1));
                  }
                  else {
                    throw new InputFormException("Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid");
                  }
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }

                try {
                  LibraryQC lqc = new LibraryQCImpl();
                  lqc.setLibrary(library);
                  lqc.setInsertSize(insertSize);
                  lqc.setResults(Double.valueOf(getCellValueAsString(libraryQcMolarityCell)));
                  lqc.setQcCreator(u.getLoginName());
                  lqc.setQcDate(new Date());
                  lqc.setQcType(manager.getLibraryQcTypeByName("Bioanalyzer"));
                  if (!library.getLibraryQCs().contains(lqc)) {
                    library.addQc(lqc);
                    manager.saveLibraryQC(lqc);
                    log.info("Added library QC: " + lqc.toString());
                  }

                  if (insertSize == 0 && lqc.getResults() == 0) {
                    library.setQcPassed(false);
                  }
                  else {
                    if (getCellValueAsString(qcPassedCell) != null) {
                      if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
                        library.setQcPassed(true);
                        log.info("Marked library QC Passed as True");
                      }
                      else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
                        library.setQcPassed(false);
                        log.info("Marked library QC Passed as False");
                      }
                    }
                  }
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied Library QC concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }


              if (getCellValueAsString(libraryQubitCell) != null) {
                int insertSize = 0;
                try {
                  String bp = getCellValueAsString(libraryQcInsertSizeCell);
                  Matcher m = digitPattern.matcher(bp);
                  if (m.matches()) {
                    insertSize = Integer.valueOf(m.group(1));
                  }
                  else {
                    throw new InputFormException("Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid");
                  }
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }

                try {
                  LibraryQC lqc = new LibraryQCImpl();
                  lqc.setLibrary(library);
                  lqc.setInsertSize(insertSize);
                  lqc.setResults(Double.valueOf(getCellValueAsString(libraryQubitCell)));
                  lqc.setQcCreator(u.getLoginName());
                  lqc.setQcDate(new Date());
                  lqc.setQcType(manager.getLibraryQcTypeByName("Qubit"));
                  if (!library.getLibraryQCs().contains(lqc)) {
                    library.addQc(lqc);
                    manager.saveLibraryQC(lqc);
                    log.info("Added library QC: " + lqc.toString());
                  }

                  if (insertSize == 0 && lqc.getResults() == 0) {
                    library.setQcPassed(false);
                  }
                  else {
                    if (getCellValueAsString(qcPassedCell) != null) {
                      if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
                        library.setQcPassed(true);
                        log.info("Marked library QC Passed as True");
                      }
                      else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
                        library.setQcPassed(false);
                        log.info("Marked library QC Passed as False");
                      }
                    }
                  }
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied Library QC concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              if (getCellValueAsString(barcodeKitCell) != null) {
                Collection<TagBarcode> bcs = manager.listAllTagBarcodesByStrategyName(getCellValueAsString(barcodeKitCell));
                if (!bcs.isEmpty()) {
                  String tags = getCellValueAsString(barcodeTagsCell);
                  if (!"".equals(tags)) {
                    HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
                    if (tags.contains("-")) {
                      String[] splits = tags.split("-");
                      int count = 1;
                      for (String tag : splits) {
                        for (TagBarcode tb : bcs) {
                          if (tb.getName().equals(tag)) {
                            //set tag barcodes
                            tbs.put(count, tb);
                            count++;
                          }
                        }
                      }
                    }
                    else {
                      for (TagBarcode tb : bcs) {
                        if (tb.getName().equals(tags) || tb.getSequence().equals(tags)) {
                          //set tag barcode
                          tbs.put(1, tb);
                          log.info("Got tag barcode: " + tb.getName());
                          break;
                        }
                      }
                    }

                    library.setTagBarcodes(tbs);
                  }
                  else {
                    throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '" + s.getAlias() + "'.");
                  }
                }
                else {
                  throw new InputFormException("No tag barcodes associated with the kit definition '" + getCellValueAsString(barcodeKitCell) + "' for sample: '" + s.getAlias() + "'.");
                }
              }

              if (getCellValueAsString(dilutionMolarityCell) != null) {
                try {
                  ldi.setLibrary(library);
                  ldi.setSecurityProfile(library.getSecurityProfile());
                  ldi.setConcentration(Double.valueOf(getCellValueAsString(dilutionMolarityCell)));
                  ldi.setCreationDate(new Date());
                  ldi.setDilutionCreator(u.getLoginName());
                  if (!library.getLibraryDilutions().contains(ldi)) {
                    library.addDilution(ldi);
                    log.info("Added library dilution: " + ldi.toString());
                  }
                  manager.saveLibraryDilution(ldi);
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied LibraryDilution concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              log.info("Added library: " + library.toString());
              manager.saveLibrary(library);
            }
            else {
              log.error("Cannot generate library alias from specified parent sample alias. Does it match the required schema?");
            }
          }

          if (getCellValueAsString(poolNumberCell) != null) {
            String poolNum = getCellValueAsString(poolNumberCell);
            Pool pool = new PoolImpl();
            if (!pools.containsKey(poolNum)) {
              pool.setAlias("pool" + poolNum);
              pool.setPlatformType(pt);
              pool.setReadyToRun(true);
              pool.setCreationDate(new Date());
              if (getCellValueAsString(poolConvertedMolarityCell) != null) {
                pool.setConcentration(poolConvertedMolarityCell.getNumericCellValue());
              }
              else {
                pool.setConcentration(0.0);
              }
              pools.put(poolNum, pool);
              log.info("Added pool: " + poolNum);
              if (ldi != null) {
                pool.addPoolableElement(ldi);
              }
              manager.savePool(pool);
            }
            else {
              pool = pools.get(poolNum);
              if (ldi != null) {
                pool.addPoolableElement(ldi);
                manager.savePool(pool);
              }
            }
          }
        }
      }
      return "ok";
    }
    else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  //private static Map<String, Pool<Plate<LinkedList<Library>, Library>>> process384PlateInputXLSX(XSSFWorkbook wb, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
  private static Map<String, PlatePool> process384PlateInputXLSX(XSSFWorkbook wb, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme) libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
    XSSFSheet sheet = wb.getSheetAt(0);
    int rows = sheet.getPhysicalNumberOfRows();

    XSSFRow glrow = sheet.getRow(1);

    //process global headers
    XSSFCell pairedCell = glrow.getCell(0);
    boolean paired = Boolean.parseBoolean(pairedCell.getStringCellValue());
    log.info("Got paired: " + paired);

    XSSFCell platformCell = glrow.getCell(1);
    PlatformType pt = null;
    if (getCellValueAsString(platformCell) != null) {
      pt = PlatformType.get(getCellValueAsString(platformCell));
    }
    if (pt == null) {
      throw new InputFormException("Cannot resolve Platform type from: '" + getCellValueAsString(platformCell) + "'");
    }
    else {
      log.info("Got platform type: " + pt.getKey());
    }

    XSSFCell typeCell = glrow.getCell(2);
    LibraryType lt = null;
    if (getCellValueAsString(typeCell) != null) {
      String[] split = getCellValueAsString(typeCell).split("-");
      String plat = split[0];
      String type = split[1];
      if (getCellValueAsString(platformCell).equals(plat)) {
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
      }
      else {
        throw new InputFormException("Selected library type '" + getCellValueAsString(typeCell) + "' doesn't match platform type: '" + getCellValueAsString(platformCell) + "'");
      }
    }
    if (lt == null) {
      throw new InputFormException("Cannot resolve Library type from: '" + getCellValueAsString(typeCell) + "'");
    }
    else {
      log.info("Got library type: " + lt.getDescription());
    }

    XSSFCell selectionCell = glrow.getCell(3);
    LibrarySelectionType ls = null;
    if (getCellValueAsString(selectionCell) != null) {
      ls = manager.getLibrarySelectionTypeByName(getCellValueAsString(selectionCell));
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
    }
    else {
      log.info("Got library selection type: " + ls.getName());
    }

    XSSFCell strategyCell = glrow.getCell(4);
    LibraryStrategyType lst = null;
    if (getCellValueAsString(strategyCell) != null) {
      lst = manager.getLibraryStrategyTypeByName(getCellValueAsString(strategyCell));
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) + "'");
    }
    else {
      log.info("Got library strategy type: " + lst.getName());
    }

    XSSFCell plateBarcodeCell = glrow.getCell(5);
    String plateBarcode = null;
    if (getCellValueAsString(plateBarcodeCell) != null) {
      plateBarcode = getCellValueAsString(plateBarcodeCell);
    }
    if (plateBarcode == null) {
      throw new InputFormException("Cannot resolve plate barcode from: '" + getCellValueAsString(plateBarcodeCell) + "'");
    }
    else {
      log.info("Got plate barcode: " + plateBarcode);
    }

    //process entries
    Simple384WellPlate libraryPlate = null;
    //Map<String, Pool<Plate<LinkedList<Library>, Library>>> pools = new HashMap<String, Pool<Plate<LinkedList<Library>, Library>>>();
    Map<String, PlatePool> pools = new HashMap<String, PlatePool>();
    for (int ri = 4; ri < rows; ri++) {
      XSSFRow row = sheet.getRow(ri);

      // Ax - plate position
      XSSFCell platePosCell = row.getCell(0);
      String platePos = getCellValueAsString(platePosCell);
      if (platePos != null && libraryPlate == null) {
        //plated libraries - process as plate
        libraryPlate = new Simple384WellPlate();
        libraryPlate.setIdentificationBarcode(plateBarcode);
        libraryPlate.setCreationDate(new Date());
      }

      //cell defs
      XSSFCell sampleAliasCell = row.getCell(2);

      Sample s = null;
      if (getCellValueAsString(sampleAliasCell) != null) {
        String salias = getCellValueAsString(sampleAliasCell);
        Collection<Sample> ss = manager.listSamplesByAlias(salias);
        if (!ss.isEmpty()) {
          if (ss.size() == 1) {
            s = ss.iterator().next();
            log.info("Got sample: " + s.getAlias());
          }
          else {
            throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
          }
        }
        else {
          throw new InputFormException("No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
        }
      }
      else {
        log.info("Blank sample row found. Ending import.");
        break;
      }

      //sample OK - good to go
      if (s != null) {
        XSSFCell entityIDCell = row.getCell(2);
        XSSFCell poolNumberCell = row.getCell(3);
        XSSFCell sampleQcCell = row.getCell(4);
        //XSSFCell sampleAmountCell = row.getCell(5);
        //XSSFCell sampleWaterAmountCell = row.getCell(6);
        XSSFCell libraryDescriptionCell = row.getCell(7);
        XSSFCell barcodeKitCell = row.getCell(8);
        XSSFCell barcodeTagsCell = row.getCell(9);
        XSSFCell libraryQcCell = row.getCell(10);
        XSSFCell libraryQcInsertSizeCell = row.getCell(11);
        XSSFCell libraryQcMolarityCell = row.getCell(12);
        XSSFCell libraryQcPassFailCell = row.getCell(13);
        //XSSFCell libraryAmountCell = row.getCell(14);
        //XSSFCell libraryWaterAmountCell = row.getCell(15);
        //XSSFCell dilutionQcCell = row.getCell(16);
        XSSFCell dilutionMolarityCell = row.getCell(17);
        //XSSFCell dilutionAmountCell = row.getCell(18);
        //XSSFCell dilutionWaterAmountCell = row.getCell(19);
        XSSFCell poolQcCell = row.getCell(20);
        //XSSFCell poolAverageInsertSizeCell = row.getCell(21);
        XSSFCell poolConvertedMolarityCell = row.getCell(22);

        //add pool, if any
        if (getCellValueAsString(poolNumberCell) != null) {
          String poolNum = getCellValueAsString(poolNumberCell);
          if (!pools.containsKey(poolNum)) {
            PlatePool pool = new PlatePool();
            pool.setAlias(poolNum);
            pool.setPlatformType(pt);
            pool.setReadyToRun(true);
            pool.setCreationDate(new Date());
            if (getCellValueAsString(poolConvertedMolarityCell) != null) {
              pool.setConcentration(poolConvertedMolarityCell.getNumericCellValue());
            }
            else {
              pool.setConcentration(0.0);
            }
            pools.put(poolNum, pool);
            log.info("Added pool: " + poolNum);
            manager.savePool(pool);
          }
        }

        //process sample QC
        if (getCellValueAsString(sampleQcCell) != null) {
          try {
            SampleQC sqc = new SampleQCImpl();
            sqc.setSample(s);
            sqc.setResults(Double.valueOf(getCellValueAsString(sampleQcCell)));
            sqc.setQcCreator(u.getLoginName());
            sqc.setQcDate(new Date());
            if (manager.getSampleQcTypeByName("Picogreen") != null) {
              sqc.setQcType(manager.getSampleQcTypeByName("Picogreen"));
            }
            else {
              sqc.setQcType(manager.getSampleQcTypeByName("QuBit"));
            }
            if (!s.getSampleQCs().contains(sqc)) {
              s.addQc(sqc);
              manager.saveSampleQC(sqc);
              manager.saveSample(s);
              log.info("Added sample QC: " + sqc.toString());
            }
          }
          catch (NumberFormatException nfe) {
            throw new InputFormException("Supplied Sample QC concentration for sample '" + getCellValueAsString(sampleAliasCell) + "' is invalid", nfe);
          }
        }

        //if (getCellValueAsString(libraryQcCell) != null) {

        if (getCellValueAsString(barcodeKitCell) != null && getCellValueAsString(barcodeTagsCell) != null) {
          //create library
          Library library = new LibraryImpl();
          library.setSample(s);

          Matcher mat = samplePattern.matcher(s.getAlias());
          if (mat.matches()) {
            String libAlias = plateBarcode + "_" + "L" + mat.group(2) + "-" + platePos + "_" + getCellValueAsString(entityIDCell);
            //String libAlias = libraryNamingScheme.generateNameFor("alias", library);
            //library.setAlias(libAlias);

            library.setAlias(libAlias);
            library.setSecurityProfile(s.getSecurityProfile());
            library.setDescription(s.getDescription());
            library.setCreationDate(new Date());
            library.setPlatformName(pt.name());
            library.setLibraryType(lt);
            library.setLibrarySelectionType(ls);
            library.setLibraryStrategyType(lst);
            library.setPaired(paired);

            if (getCellValueAsString(libraryQcMolarityCell) != null) {
              int insertSize = 0;
              try {
                String bp = getCellValueAsString(libraryQcInsertSizeCell);
                Matcher m = digitPattern.matcher(bp);
                if (m.matches()) {
                  insertSize = Integer.valueOf(m.group(1));
                }
                else {
                  throw new InputFormException("Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid");
                }
              }
              catch (NumberFormatException nfe) {
                throw new InputFormException("Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
              }

              try {
                LibraryQC lqc = new LibraryQCImpl();
                lqc.setLibrary(library);
                lqc.setInsertSize(insertSize);
                lqc.setResults(Double.valueOf(getCellValueAsString(libraryQcMolarityCell)));
                lqc.setQcCreator(u.getLoginName());
                lqc.setQcDate(new Date());
                lqc.setQcType(manager.getLibraryQcTypeByName("Bioanalyzer"));
                if (!library.getLibraryQCs().contains(lqc)) {
                  library.addQc(lqc);
                  manager.saveLibraryQC(lqc);
                  log.info("Added library QC: " + lqc.toString());
                }

                if (insertSize == 0 && lqc.getResults() == 0) {
                  library.setQcPassed(false);
                }
                else {
                  //TODO check libraryQcPassFailCell?
                  library.setQcPassed(true);
                }
              }
              catch (NumberFormatException nfe) {
                throw new InputFormException("Supplied Library QC concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
              }
            }

            if (getCellValueAsString(barcodeKitCell) != null) {
              Collection<TagBarcode> bcs = manager.listAllTagBarcodesByStrategyName(getCellValueAsString(barcodeKitCell));
              if (!bcs.isEmpty()) {
                String tags = getCellValueAsString(barcodeTagsCell);
                if (!"".equals(tags)) {
                  HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
                  if (tags.contains("-")) {
                    String[] splits = tags.split("-");
                    int count = 1;
                    for (String tag : splits) {
                      for (TagBarcode tb : bcs) {
                        if (tb.getName().equals(tag)) {
                          //set tag barcodes
                          tbs.put(count, tb);
                          count++;
                        }
                      }
                    }
                  }
                  else {
                    for (TagBarcode tb : bcs) {
                      if (tb.getName().equals(tags) || tb.getSequence().equals(tags)) {
                        //set tag barcode
                        tbs.put(1, tb);
                        log.info("Got tag barcode: " + tb.getName());
                        break;
                      }
                    }
                  }

                  library.setTagBarcodes(tbs);
                }
                else {
                  throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '" + s.getAlias() + "'.");
                }
              }
              else {
                throw new InputFormException("No tag barcodes associated with the kit definition '" + getCellValueAsString(barcodeKitCell) + "' for sample: '" + s.getAlias() + "'.");
              }
            }

            /*
            if (getCellValueAsString(dilutionMolarityCell) != null) {
              try {
                LibraryDilution ldi = new LibraryDilution();
                ldi.setLibrary(library);
                ldi.setSecurityProfile(library.getSecurityProfile());
                ldi.setConcentration(Double.valueOf(getCellValueAsString(dilutionMolarityCell)));
                ldi.setCreationDate(new Date());
                ldi.setDilutionCreator(u.getLoginName());
                if (!library.getLibraryDilutions().contains(ldi)) {
                  library.addDilution(ldi);
                  log.info("Added library dilution: " + ldi.toString());
                }

                if (getCellValueAsString(poolNumberCell) != null) {
                  String poolNum = String.valueOf(new Double(getCellValueAsString(poolNumberCell)).intValue());
                  Pool<Plate<LinkedList<Library>, Library>> p = pools.get(poolNum);
                  if (p != null) {
                    p.addPoolableElement(ldi);
                    log.info("Added library dilution to pool: " + p.toString());
                  }
                }
              }
              catch (NumberFormatException nfe) {
                throw new InputFormException("Supplied LibraryDilution concentration for library '"+libAlias+"' ("+s.getAlias()+") is invalid", nfe);
              }
            }
            */

            if (getCellValueAsString(poolConvertedMolarityCell) != null) {
              String poolNum = getCellValueAsString(poolNumberCell);
              Pool<Plate<LinkedList<Library>, Library>> p = pools.get(poolNum);
              if (p != null) {
                log.debug("Retrieved pool " + poolNum);
                try {
                  p.setConcentration(Double.valueOf(getCellValueAsString(poolConvertedMolarityCell)));
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied pool concentration for pool '" + poolNum + "' is invalid", nfe);
                }
              }
            }

            log.info("Added library: " + library.toString());
            manager.saveLibrary(library);

            if (getCellValueAsString(platePosCell) != null && libraryPlate != null) {
              //libraryPlate.setElement(getCellValueAsString(platePosCell), library);
              libraryPlate.addElement(library);
              log.info("Added library " + library.getAlias() + " to " + getCellValueAsString(platePosCell));
            }

            samples.add(s);

            Pool<Plate<LinkedList<Library>, Library>> p = pools.get(getCellValueAsString(poolNumberCell));
            if (p != null && !p.getPoolableElements().contains(libraryPlate)) {
              p.addPoolableElement(libraryPlate);
              log.info("Added plate to pool: " + p.toString());
            }
          }
          else {
            log.error("Cannot generate library alias from specified parent sample alias. Does it match the required schema?");
          }
        }
      }
    }
    log.info("Done");
    return pools;
  }

  //public static Map<String, Pool<Plate<LinkedList<Library>, Library>>> importPlateInputSpreadsheet(File inPath, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
  public static Map<String, PlatePool> importPlateInputSpreadsheet(File inPath, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      return process384PlateInputXLSX(wb, u, manager, libraryNamingScheme);
    }
    else if (inPath.getName().endsWith(".ods")) {
      OdfSpreadsheetDocument oDoc = (OdfSpreadsheetDocument) OdfDocument.loadDocument(inPath);
      return process384PlateInputODS(oDoc, u, manager, libraryNamingScheme);
    }
    else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  private static List<Sample> processSampleInputODS(OdfSpreadsheetDocument oDoc, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme) libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
    OdfTable oTable = oDoc.getTableList().get(0);

    //process global headers
    OdfTableCell pairedCell = oTable.getCellByPosition("A2");
    boolean paired;
    if (pairedCell != null && pairedCell.getBooleanValue() != null) {
      paired = pairedCell.getBooleanValue();
      log.info("Got paired: " + paired);
    }
    else {
      throw new InputFormException("'Paired' cell is empty. Please specify TRUE or FALSE.");
    }

    OdfTableCell platformCell = oTable.getCellByPosition("B2");
    PlatformType pt = null;
    if (!"".equals(platformCell.getStringValue())) {
      pt = PlatformType.get(platformCell.getStringValue());
    }
    if (pt == null) {
      throw new InputFormException("Cannot resolve Platform type from: '" + platformCell.getStringValue() + "'");
    }
    else {
      log.info("Got platform type: " + pt.getKey());
    }

    OdfTableCell typeCell = oTable.getCellByPosition("C2");
    LibraryType lt = null;
    if (!"".equals(typeCell.getStringValue())) {
      String[] split = typeCell.getStringValue().split("-");
      String plat = split[0];
      String type = split[1];
      if (platformCell.getStringValue().equals(plat)) {
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
      }
      else {
        throw new InputFormException("Selected library type '" + typeCell.getStringValue() + "' doesn't match platform type: '" + platformCell.getStringValue() + "'");
      }
    }
    if (lt == null) {
      throw new InputFormException("Cannot resolve Library type from: '" + typeCell.getStringValue() + "'");
    }
    else {
      log.info("Got library type: " + lt.getDescription());
    }

    OdfTableCell selectionCell = oTable.getCellByPosition("D2");
    LibrarySelectionType ls = null;
    if (!"".equals(selectionCell.getStringValue())) {
      ls = manager.getLibrarySelectionTypeByName(selectionCell.getStringValue());
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + selectionCell.getStringValue() + "'");
    }
    else {
      log.info("Got library selection type: " + ls.getName());
    }

    OdfTableCell strategyCell = oTable.getCellByPosition("E2");
    LibraryStrategyType lst = null;
    if (!"".equals(strategyCell.getStringValue())) {
      lst = manager.getLibraryStrategyTypeByName(strategyCell.getStringValue());
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + strategyCell.getStringValue() + "'");
    }
    else {
      log.info("Got library strategy type: " + lst.getName());
    }

    //process entries
    Plate<LinkedList<Sample>, Sample> samplePlate = null;
    Map<String, Pool<Dilution>> pools = new HashMap<String, Pool<Dilution>>();
    for (OdfTableRow row : oTable.getRowList()) {
      int ri = row.getRowIndex();
      if (ri > 3) {
        // Ax - plate position
        OdfTableCell platePosCell = oTable.getCellByPosition(0, ri);
        if (!"".equals(platePosCell.getStringValue())) {
          //plated samples - process as plate
          samplePlate = new PlateImpl<Sample>();
        }

        //cell defs
        OdfTableCell sampleAliasCell = oTable.getCellByPosition(2, ri);

        Sample s = null;
        if (!"".equals(sampleAliasCell.getStringValue())) {
          Collection<Sample> ss = manager.listSamplesByAlias(sampleAliasCell.getStringValue());
          if (!ss.isEmpty()) {
            if (ss.size() == 1) {
              s = ss.iterator().next();
              log.info("Got sample: " + s.getAlias());
            }
            else {
              throw new InputFormException("Multiple samples retrieved with this alias: '" + sampleAliasCell.getStringValue() + "'. Cannot process.");
            }
          }
          else {
            throw new InputFormException("No such sample '" + sampleAliasCell.getStringValue() + "'in database. Samples need to be created before using the form input functionality");
          }
        }
        else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        //sample OK - good to go
        if (s != null) {
          String projectAliasCell = oTable.getCellByPosition(1, ri).getStringValue();
          String poolNumberCell = oTable.getCellByPosition(3, ri).getStringValue();
          String sampleQcCell = oTable.getCellByPosition(4, ri).getStringValue();
          //String sampleAmountCell = oTable.getCellByPosition(5, ri).getStringValue();
          //String sampleWaterAmountCell = oTable.getCellByPosition(6, ri).getStringValue();
          String libraryDescriptionCell = oTable.getCellByPosition(7, ri).getStringValue();
          String barcodeKitCell = oTable.getCellByPosition(8, ri).getStringValue();
          String barcodeTagsCell = oTable.getCellByPosition(9, ri).getStringValue();
          String libraryQcCell = oTable.getCellByPosition(10, ri).getStringValue();
          String libraryQcInsertSizeCell = oTable.getCellByPosition(11, ri).getStringValue();
          String libraryQcMolarityCell = oTable.getCellByPosition(12, ri).getStringValue();
          String libraryQcPassFailCell = oTable.getCellByPosition(13, ri).getStringValue();
          //String libraryAmountCell = oTable.getCellByPosition(14, ri).getStringValue();
          //String libraryWaterAmountCell = oTable.getCellByPosition(15, ri).getStringValue();
          //String dilutionQcCell = oTable.getCellByPosition(16, ri).getStringValue();
          String dilutionMolarityCell = oTable.getCellByPosition(17, ri).getStringValue();
          //String dilutionAmountCell = oTable.getCellByPosition(18, ri).getStringValue();
          //String dilutionWaterAmountCell = oTable.getCellByPosition(19, ri).getStringValue();
          String poolQcCell = oTable.getCellByPosition(20, ri).getStringValue();
          //String poolAverageInsertSizeCell = oTable.getCellByPosition(21, ri).getStringValue().getStringValue();
          String poolConvertedMolarityCell = oTable.getCellByPosition(22, ri).getStringValue();

          //add pool, if any
          processPool(poolNumberCell, poolConvertedMolarityCell, pools);
          processSampleQC(sampleQcCell, s, u, manager);

          Library library = processLibrary(libraryQcCell, libraryDescriptionCell, libraryQcPassFailCell, s, pt, lt, ls, lst, paired, libraryNamingScheme);
          if (library != null) {
            processLibraryQC(libraryQcCell, libraryQcMolarityCell, libraryQcInsertSizeCell, library, u, manager);
            processBarcodes(barcodeKitCell, barcodeTagsCell, library, manager);
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

  private static List<Sample> processSampleInputXLSX(XSSFWorkbook wb, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme) libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
    XSSFSheet sheet = wb.getSheetAt(0);
    int rows = sheet.getPhysicalNumberOfRows();

    XSSFRow glrow = sheet.getRow(1);

    //process global headers
    XSSFCell pairedCell = glrow.getCell(0);
    boolean paired;
    if (getCellValueAsString(pairedCell) != null) {
      paired = pairedCell.getBooleanCellValue();
      log.info("Got paired: " + paired);
    }
    else {
      throw new InputFormException("'Paired' cell is empty. Please specify TRUE or FALSE.");
    }

    XSSFCell platformCell = glrow.getCell(1);
    PlatformType pt = null;
    if (getCellValueAsString(platformCell) != null) {
      pt = PlatformType.get(getCellValueAsString(platformCell));
    }
    if (pt == null) {
      throw new InputFormException("Cannot resolve Platform type from: '" + getCellValueAsString(platformCell) + "'");
    }
    else {
      log.info("Got platform type: " + pt.getKey());
    }

    XSSFCell typeCell = glrow.getCell(2);
    LibraryType lt = null;
    if (getCellValueAsString(typeCell) != null) {
      String[] split = getCellValueAsString(typeCell).split("-");
      String plat = split[0];
      String type = split[1];
      if (getCellValueAsString(platformCell).equals(plat)) {
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
      }
      else {
        throw new InputFormException("Selected library type '" + getCellValueAsString(typeCell) + "' doesn't match platform type: '" + getCellValueAsString(platformCell) + "'");
      }
    }
    if (lt == null) {
      throw new InputFormException("Cannot resolve Library type from: '" + getCellValueAsString(typeCell) + "'");
    }
    else {
      log.info("Got library type: " + lt.getDescription());
    }

    XSSFCell selectionCell = glrow.getCell(3);
    LibrarySelectionType ls = null;
    if (getCellValueAsString(selectionCell) != null) {
      ls = manager.getLibrarySelectionTypeByName(getCellValueAsString(selectionCell));
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
    }
    else {
      log.info("Got library selection type: " + ls.getName());
    }

    XSSFCell strategyCell = glrow.getCell(4);
    LibraryStrategyType lst = null;
    if (getCellValueAsString(strategyCell) != null) {
      lst = manager.getLibraryStrategyTypeByName(getCellValueAsString(strategyCell));
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) + "'");
    }
    else {
      log.info("Got library strategy type: " + lst.getName());
    }

    //process entries
    Plate<LinkedList<Sample>, Sample> samplePlate = null;
    Map<String, Pool<Dilution>> pools = new HashMap<String, Pool<Dilution>>();

    for (int ri = 4; ri < rows; ri++) {
      XSSFRow row = sheet.getRow(ri);

      // Ax - plate position
      XSSFCell platePosCell = row.getCell(0);
      if (getCellValueAsString(platePosCell) != null && samplePlate == null) {
        //plated samples - process as plate
        samplePlate = new PlateImpl<Sample>();
      }

      //cell defs
      XSSFCell sampleAliasCell = row.getCell(2);

      Sample s = null;
      if (getCellValueAsString(sampleAliasCell) != null) {
        String salias = getCellValueAsString(sampleAliasCell);
        Collection<Sample> ss = manager.listSamplesByAlias(salias);
        if (!ss.isEmpty()) {
          if (ss.size() == 1) {
            s = ss.iterator().next();
            log.info("Got sample: " + s.getAlias());
          }
          else {
            throw new InputFormException("Multiple samples retrieved with this alias: '" + salias + "'. Cannot process.");
          }
        }
        else {
          throw new InputFormException("No such sample '" + salias + "'in database. Samples need to be created before using the form input functionality");
        }
      }
      else {
        log.info("Blank sample row found. Ending import.");
        break;
      }

      //sample OK - good to go
      if (s != null) {
        String projectAliasCell = getCellValueAsString(row.getCell(1));
        String poolNumberCell = getCellValueAsString(row.getCell(3));
        String sampleQcCell = getCellValueAsString(row.getCell(4));
        //String sampleAmountCell = getCellValueAsString(row.getCell(5));
        //String sampleWaterAmountCell = getCellValueAsString(row.getCell(6));
        String libraryDescriptionCell = getCellValueAsString(row.getCell(7));
        String barcodeKitCell = getCellValueAsString(row.getCell(8));
        String barcodeTagsCell = getCellValueAsString(row.getCell(9));
        String libraryQcCell = getCellValueAsString(row.getCell(10));
        String libraryQcInsertSizeCell = getCellValueAsString(row.getCell(11));
        String libraryQcMolarityCell = getCellValueAsString(row.getCell(12));
        String libraryQcPassFailCell = getCellValueAsString(row.getCell(13));
        //String libraryAmountCell = getCellValueAsString(row.getCell(14));
        //String libraryWaterAmountCell = getCellValueAsString(row.getCell(15));
        //String dilutionQcCell = getCellValueAsString(row.getCell(16));
        String dilutionMolarityCell = getCellValueAsString(row.getCell(17));
        //String dilutionAmountCell = getCellValueAsString(row.getCell(18));
        //String dilutionWaterAmountCell = getCellValueAsString(row.getCell(19));
        String poolQcCell = getCellValueAsString(row.getCell(20));
        //String poolAverageInsertSizeCell = getCellValueAsString(row.getCell(21));
        String poolConvertedMolarityCell = getCellValueAsString(row.getCell(22));

        //add pool, if any
        processPool(poolNumberCell, poolConvertedMolarityCell, pools);
        processSampleQC(sampleQcCell, s, u, manager);

        Library library = processLibrary(libraryQcCell, libraryDescriptionCell, libraryQcPassFailCell, s, pt, lt, ls, lst, paired, libraryNamingScheme);
        if (library != null) {
          processLibraryQC(libraryQcCell, libraryQcMolarityCell, libraryQcInsertSizeCell, library, u, manager);
          processBarcodes(barcodeKitCell, barcodeTagsCell, library, manager);
          processDilutions(dilutionMolarityCell, library, pools.get(poolNumberCell), u);
          log.info("Added library: " + library.toString());
          s.addLibrary(library);
        }
        samples.add(s);
      }
    }
    return samples;
  }

  private static void processPool(String poolAlias, String poolConvertedMolarity, Map<String, Pool<Dilution>> pools) throws Exception {
    if (!"".equals(poolAlias)) {
      if (!pools.containsKey(poolAlias)) {
        Pool<Dilution> pool = new PoolImpl<Dilution>();
        pool.setAlias(poolAlias);
        pools.put(poolAlias, pool);
        log.info("Added pool: " + poolAlias);
      }

      if (!"".equals(poolConvertedMolarity)) {
        Pool<Dilution> p = pools.get(poolAlias);
        if (p != null) {
          log.info("Retrieved pool " + poolAlias);
          try {
            double d = Double.valueOf(poolConvertedMolarity);
            log.info("Got conc " + d);
            p.setConcentration(d);
          }
          catch (NumberFormatException nfe) {
            throw new InputFormException("Supplied pool concentration for pool '" + poolAlias + "' is invalid", nfe);
          }
        }
      }
    }
  }

  private static void processSampleQC(String sampleQc, Sample s, User u, RequestManager manager) throws Exception {
    //process sample QC
    if (!"".equals(sampleQc)) {
      try {
        SampleQC sqc = new SampleQCImpl();
        sqc.setSample(s);
        sqc.setResults(Double.valueOf(sampleQc));
        sqc.setQcCreator(u.getLoginName());
        sqc.setQcDate(new Date());
        sqc.setQcType(manager.getSampleQcTypeByName("QuBit"));
        if (!s.getSampleQCs().contains(sqc)) {
          s.addQc(sqc);
          log.info("Added sample QC: " + sqc.toString());
        }
      }
      catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied Sample QC concentration for sample '" + sampleQc + "' is invalid", nfe);
      }
    }
  }

  private static Library processLibrary(String libraryQc,
                                        String libraryDescription,
                                        String libraryQcPassFail,
                                        Sample s,
                                        PlatformType pt,
                                        LibraryType lt,
                                        LibrarySelectionType ls,
                                        LibraryStrategyType lst,
                                        boolean paired,
                                        MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    if (!"".equals(libraryQc)) {
      //create library
      Library library = new LibraryImpl();
      library.setSample(s);
      library.setSecurityProfile(s.getSecurityProfile());
      if (!"".equals(libraryDescription)) {
        library.setDescription(libraryDescription);
      }
      else {
        library.setDescription(s.getDescription());
      }
      library.setCreationDate(new Date());
      library.setPlatformName(pt.name());
      library.setLibraryType(lt);
      library.setLibrarySelectionType(ls);
      library.setLibraryStrategyType(lst);
      library.setPaired(paired);

      if (!"".equals(libraryQcPassFail)) {
        library.setQcPassed(Boolean.parseBoolean(libraryQcPassFail));
      }

      String libAlias = libraryNamingScheme.generateNameFor("alias", library);

      library.setAlias(libAlias);

      return library;
    }
    return null;
  }

  private static void processLibraryQC(String libraryQc,
                                       String libraryQcMolarity,
                                       String libraryQcInsertSize,
                                       Library library,
                                       User u,
                                       RequestManager manager) throws Exception {
    if (!"".equals(libraryQcMolarity)) {
      int insertSize = 0;
      try {
        Matcher m = digitPattern.matcher(libraryQcInsertSize);
        if (m.matches()) {
          insertSize = Integer.valueOf(m.group(1));
        }
        else {
          throw new InputFormException("Supplied Library insert size for library '" + library.getAlias() + "' (" + library.getSample().getAlias() + ") is invalid");
        }
      }
      catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied Library insert size for library '" + library.getAlias() + "' (" + library.getSample().getAlias() + ") is invalid", nfe);
      }

      try {
        LibraryQC lqc = new LibraryQCImpl();
        lqc.setLibrary(library);
        lqc.setInsertSize(insertSize);
        lqc.setResults(Double.valueOf(libraryQcMolarity));
        lqc.setQcCreator(u.getLoginName());
        lqc.setQcDate(new Date());
        QcType lqct = manager.getLibraryQcTypeByName(libraryQc);
        if (lqct != null) {
          lqc.setQcType(manager.getLibraryQcTypeByName(libraryQc));
          if (!library.getLibraryQCs().contains(lqc)) {
            library.addQc(lqc);
            log.info("Added library QC: " + lqc.toString());
          }
        }
        else {
          throw new InputFormException("No such Library QC type '" + libraryQc + "'");
        }

        if (insertSize == 0 && lqc.getResults() == 0) {
          library.setQcPassed(false);
        }
        else {
          //TODO check libraryQcPassFailCell?
          library.setQcPassed(true);
        }
      }
      catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied Library QC concentration for library '" + library.getAlias() + "' (" + library.getSample().getAlias() + ") is invalid", nfe);
      }
    }
  }

  private static void processBarcodes(String barcodeKit, String barcodeTags, Library library, RequestManager manager) throws Exception {
    if (!"".equals(barcodeKit)) {
      Collection<TagBarcode> bcs = manager.listAllTagBarcodesByStrategyName(barcodeKit);
      if (!bcs.isEmpty()) {
        if (!"".equals(barcodeTags)) {
          HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
          if (barcodeTags.contains("-")) {
            String[] splits = barcodeTags.split("-");
            int count = 1;
            for (String tag : splits) {
              for (TagBarcode tb : bcs) {
                if (tb.getName().equals(tag)) {
                  //set tag barcodes
                  tbs.put(count, tb);
                  count++;
                }
              }
            }
          }
          else {
            for (TagBarcode tb : bcs) {
              if (tb.getName().equals(barcodeTags)) {
                //set tag barcode
                tbs.put(1, tb);
              }
            }
          }

          library.setTagBarcodes(tbs);
        }
        else {
          throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '" + library.getSample().getAlias() + "'.");
        }
      }
      else {
        throw new InputFormException("No tag barcodes associated with this kit definition: '" + library.getSample().getAlias() + "'.");
      }
    }
  }

  private static void processDilutions(String dilutionMolarity, Library library, Pool<Dilution> p, User u) throws Exception {
    if (!"".equals(dilutionMolarity)) {
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

        //Pool<Dilution> p = pools.get(poolNumberCell.getStringValue());
        if (p != null) {
          p.addPoolableElement(ldi);
          log.info("Added library dilution to pool: " + p.toString());
        }
      }
      catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied LibraryDilution concentration for library '" + library.getAlias() + "' (" + library.getSample().getAlias() + ") is invalid", nfe);
      }
    }
  }

  public static List<Sample> importSampleInputSpreadsheet(File inPath, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      return processSampleInputXLSX(wb, u, manager, libraryNamingScheme);
    }
    else if (inPath.getName().endsWith(".ods")) {
      OdfSpreadsheetDocument oDoc = (OdfSpreadsheetDocument) OdfDocument.loadDocument(inPath);
      return processSampleInputODS(oDoc, u, manager, libraryNamingScheme);
    }
    else {
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
    Collections.sort(samples, new AliasComparator(Sample.class));

    InputStream in = FormUtils.class.getResourceAsStream("/forms/odt/sampleDeliveryForm.odt");

    if (in != null) {
      OdfTextDocument oDoc = OdfTextDocument.loadDocument(in);
      OdfContentDom contentDom = oDoc.getContentDom();

      /*
      OfficeTextElement contentRoot = oDoc.getContentRoot();
      NodeList nl = contentRoot.getElementsByTagName("TABLE");
      for (int i = 0; i < nl.getLength(); i++) {
        Element e = (Element)nl.item(i);
        System.out.println(e.getTagName() + " :: " + e.toString());
      }

      List<OdfTable> cTables = contentDom.getTableList();
      for (OdfTable c : cTables) {
        System.out.println(c.getTableName() + " :: " + c.getRowCount() + " :: " + c.getColumnCount());
      }
      */
      /*
      OdfTable cTable = cTables.get(1);
      for (OdfTableRow ctr : cTable.getRowList()) {
        if (ctr.getRowIndex() == 0) {
          OdfTableCell ctc0 = ctr.getCellByIndex(1);
          OdfTextParagraph ctcp0 = new OdfTextParagraph(contentDom);
          User u = samples.get(0).getProject().getSecurityProfile().getOwner();
          ctcp0.setTextContent(u.getFullName() + " " + u.getEmail());
          ctcp0.setProperty(StyleTextPropertiesElement.FontSize, "12pt");
          ctc0.getOdfElement().appendChild(ctcp0);
        }
      }
      */

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
        }
        else {
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
    }
    else {
      throw new Exception("Could not read from resource");
    }
  }

  public static String getPlatePosition(int rowCount) {
    int columnIndex = (int) Math.round(rowCount / 8);
    int remainder = rowCount % 8;

    if (remainder == 0) {
      return "H" + columnIndex;
    }
    else {
      columnIndex++;
      if (remainder == 1) {
        return "A" + columnIndex;
      }
      else if (remainder == 2) {
        return "B" + columnIndex;
      }
      else if (remainder == 3) {
        return "C" + columnIndex;
      }
      else if (remainder == 4) {
        return "D" + columnIndex;
      }
      else if (remainder == 5) {
        return "E" + columnIndex;
      }
      else if (remainder == 6) {
        return "F" + columnIndex;
      }
      else if (remainder == 7) {
        return "G" + columnIndex;
      }
      else{
        return "NA";
      }
    }
  }

  public static List<Sample> importSampleDeliveryForm(File inPath) throws Exception {
    List<Sample> samples = new ArrayList<Sample>();
    OdfTextDocument oDoc = (OdfTextDocument) OdfDocument.loadDocument(inPath);
    OdfTable sampleTable = oDoc.getTableList().get(1);

    if (sampleTable != null) {
      DataObjectFactory df = new TgacDataObjectFactory();
      for (OdfTableRow row : sampleTable.getRowList()) {
        if (row.getRowIndex() != 0) {
          TableTableRowElement ttre = row.getOdfElement();

          Sample s = df.getSample();

          Node n1 = ttre.getChildNodes().item(0);
          if (n1.getFirstChild() != null) {
            s.setAlias(n1.getFirstChild().getTextContent());
          }
          //well
          Node n2 = ttre.getChildNodes().item(1);
          if (n2.getFirstChild() != null) {
            if (!"".equals(n2.getFirstChild().getTextContent())) {
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

          /*
          OdfTableCell cell3 = row.getCellByIndex(3);
          if (cell3.getStringValue() != null) {
            s.setSampleType(cell3.getStringValue().toUpperCase());
          }
          else {
            s.setSampleType("OTHER");
          }
          */

          Node n5 = ttre.getChildNodes().item(5);
          if (n5.getFirstChild() != null) {
            s.setDescription(n5.getFirstChild().getTextContent());
          }

          Node n9 = ttre.getChildNodes().item(9);
          if (n9.getFirstChild() != null) {
            if (!"".equals(n9.getFirstChild().getTextContent())) {
              Note note1 = new Note();
              note1.setText("260/280:" + n9.getFirstChild().getTextContent());
              s.addNote(note1);
            }
          }

          Node n10 = ttre.getChildNodes().item(10);
          if (n10.getFirstChild() != null) {
            if (!"".equals("260/230:" + n10.getFirstChild().getTextContent())) {
              Note note2 = new Note();
              note2.setText(n9.getFirstChild().getTextContent());
              s.addNote(note2);
            }
          }

          samples.add(s);
        }
      }
    }
    else {
      throw new DeliveryFormException("Cannot resolve sample table. Please check your delivery form.");
    }
    return samples;
  }

  public static void convertToPDF(OdfTextDocument oDoc) throws Exception {
    OutputStream out = new FileOutputStream(new File("/tmp/test-sample-form.pdf"));
    ODF2PDFViaITextConverter.getInstance().convert(oDoc, out, null);
  }
}