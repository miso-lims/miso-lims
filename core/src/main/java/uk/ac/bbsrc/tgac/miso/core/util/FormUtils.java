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
        //return oDoc;
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
        //return oDoc;
      }
      else {
        throw new IOException("Could not read from resource.");
      }
    }
    else {
      throw new IllegalArgumentException("Can only produce bulk input forms in ods or xlsx formats.");
    }
  }

  private static List<Sample> processSampleInputODS(OdfSpreadsheetDocument oDoc, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme)libraryNamingScheme).setRequestManager(manager);

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
      log.info("Got platform type: " + pt.getKey());
    }
    if (pt == null) { throw new InputFormException("Cannot resolve Platform type from: '" + platformCell.getStringValue() +"'"); }

    OdfTableCell typeCell = oTable.getCellByPosition("C2");
    LibraryType lt = null;
    if (!"".equals(typeCell.getStringValue())) {
      String[] split = typeCell.getStringValue().split("-");
      String plat = split[0];
      String type = split[1];
      if (platformCell.getStringValue().equals(plat)) {
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
        log.info("Got library type: " + lt.getDescription());
      }
      else {
        throw new InputFormException("Selected library type '"+typeCell.getStringValue()+"' doesn't match platform type: '" + platformCell.getStringValue() +"'");
      }
    }
    if (lt == null) { throw new InputFormException("Cannot resolve Library type from: '" + typeCell.getStringValue() +"'"); }

    OdfTableCell selectionCell = oTable.getCellByPosition("D2");
    LibrarySelectionType ls = null;
    if (!"".equals(selectionCell.getStringValue())) {
      ls = manager.getLibrarySelectionTypeByName(selectionCell.getStringValue());
      log.info("Got library selection type: " + ls.getName());
    }
    if (ls == null) { throw new InputFormException("Cannot resolve Library Selection type from: '" + selectionCell.getStringValue() +"'"); }

    OdfTableCell strategyCell = oTable.getCellByPosition("E2");
    LibraryStrategyType lst = null;
    if (!"".equals(strategyCell.getStringValue())) {
      lst = manager.getLibraryStrategyTypeByName(strategyCell.getStringValue());
      log.info("Got library strategy type: " + lst.getName());
    }
    if (lst == null) { throw new InputFormException("Cannot resolve Library Strategy type from: '" + strategyCell.getStringValue() +"'"); }

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
              throw new InputFormException("Multiple samples retrieved with this alias: '"+sampleAliasCell.getStringValue()+"'. Cannot process.");
            }
          }
          else {
            throw new InputFormException("No such sample '"+sampleAliasCell.getStringValue()+"'in database. Samples need to be created before using the form input functionality");
          }
        }
        else {
          log.info("Blank sample row found. Ending import.");
          break;
        }

        //sample OK - good to go
        if (s != null) {
          OdfTableCell poolNumberCell = oTable.getCellByPosition(2, ri);
          OdfTableCell sampleQcCell = oTable.getCellByPosition(3, ri);
          //OdfTableCell sampleAmountCell = oTable.getCellByPosition(4, ri);
          //OdfTableCell sampleWaterAmountCell = oTable.getCellByPosition(5, ri);
          OdfTableCell barcodeKitCell = oTable.getCellByPosition(6, ri);
          OdfTableCell barcodeTagsCell = oTable.getCellByPosition(7, ri);
          OdfTableCell libraryQcCell = oTable.getCellByPosition(8, ri);
          OdfTableCell libraryQcInsertSizeCell = oTable.getCellByPosition(9, ri);
          OdfTableCell libraryQcMolarityCell = oTable.getCellByPosition(10, ri);
          OdfTableCell libraryQcPassFailCell = oTable.getCellByPosition(11, ri);
          //OdfTableCell libraryAmountCell = oTable.getCellByPosition(12, ri);
          //OdfTableCell libraryWaterAmountCell = oTable.getCellByPosition(13, ri);
          //OdfTableCell dilutionQcCell = oTable.getCellByPosition(14, ri);
          OdfTableCell dilutionMolarityCell = oTable.getCellByPosition(15, ri);
          //OdfTableCell dilutionAmountCell = oTable.getCellByPosition(16, ri);
          //OdfTableCell dilutionWaterAmountCell = oTable.getCellByPosition(17, ri);
          //OdfTableCell poolQcCell = oTable.getCellByPosition(18, ri);
          //OdfTableCell poolAverageInsertSizeCell = oTable.getCellByPosition(19, ri);
          OdfTableCell poolConvertedMolarityCell = oTable.getCellByPosition(20, ri);

          //add pool, if any
          if (!"".equals(poolNumberCell.getStringValue())) {
            if (!pools.containsKey(poolNumberCell.getStringValue())) {
              pools.put(poolNumberCell.getStringValue(), new PoolImpl<Dilution>());
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
              sqc.setQcType(manager.getSampleQcTypeByName("QuBit"));
              if (!s.getSampleQCs().contains(sqc)) {
                s.addQc(sqc);
                log.info("Added sample QC: " + sqc.toString());
              }
            }
            catch (NumberFormatException nfe) {
              throw new InputFormException("Supplied Sample QC concentration for sample '"+sampleAliasCell.getStringValue()+"' is invalid", nfe);
            }
          }

          if (!"".equals(libraryQcCell.getStringValue())) {
            //create library
            Library library = new LibraryImpl();
            library.setSample(s);
            library.setSecurityProfile(s.getSecurityProfile());
            library.setDescription(s.getDescription());
            library.setCreationDate(new Date());
            library.setPlatformName(pt.name());
            library.setLibraryType(lt);
            library.setLibrarySelectionType(ls);
            library.setLibraryStrategyType(lst);
            library.setPaired(paired);

            String libAlias = libraryNamingScheme.generateNameFor("alias", library);

            library.setAlias(libAlias);

            if (!"".equals(libraryQcMolarityCell.getStringValue())) {
              int insertSize = 0;
              try {
                insertSize = Integer.valueOf(libraryQcInsertSizeCell.getStringValue());
              }
              catch (NumberFormatException nfe) {
                throw new InputFormException("Supplied Library insert size for library '"+libAlias+"' ("+s.getAlias()+") is invalid", nfe);
              }

              try {
                LibraryQC lqc = new LibraryQCImpl();
                lqc.setLibrary(library);
                lqc.setInsertSize(insertSize);
                lqc.setResults(Double.valueOf(libraryQcMolarityCell.getStringValue()));
                lqc.setQcCreator(u.getLoginName());
                lqc.setQcDate(new Date());
                lqc.setQcType(manager.getLibraryQcTypeByName("Bioanalyzer"));
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
                throw new InputFormException("Supplied Library QC concentration for library '"+libAlias+"' ("+s.getAlias()+") is invalid", nfe);
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
                  throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '"+sampleAliasCell.getStringValue()+"'.");
                }
              }
              else {
                throw new InputFormException("No tag barcodes associated with this kit definition: '"+barcodeKitCell.getStringValue()+"'.");
              }
            }

            log.info("Added library: " + library.toString());

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

                Pool<Dilution> p = pools.get(poolNumberCell.getStringValue());
                if (p != null) {
                  p.addPoolableElement(ldi);
                  log.info("Added library dilution to pool: " + p.toString());
                }
              }
              catch (NumberFormatException nfe) {
                throw new InputFormException("Supplied LibraryDilution concentration for library '"+libAlias+"' ("+s.getAlias()+") is invalid", nfe);
              }
            }

            if (!"".equals(poolConvertedMolarityCell.getStringValue())) {
              Pool<Dilution> p = pools.get(poolNumberCell.getStringValue());
              if (p != null) {
                log.info("Retrieved pool "+poolNumberCell.getStringValue());
                try {
                  double d = Double.valueOf(poolConvertedMolarityCell.getStringValue());
                  log.info("Got conc " + d);
                  p.setConcentration(d);
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied pool concentration for pool '"+poolNumberCell.getStringValue()+"' is invalid", nfe);
                }
              }
            }
          }

          samples.add(s);
        }
      }
    }
    log.info("Done");
    return samples;
  }

  private static List<Sample> processSampleInputXLSX(XSSFWorkbook wb, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme)libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
    XSSFSheet sheet = wb.getSheetAt(0);
    int rows = sheet.getPhysicalNumberOfRows();

    XSSFRow glrow = sheet.getRow(1);

    //process global headers
    XSSFCell pairedCell = glrow.getCell(0);
    boolean paired = pairedCell.getBooleanCellValue();
    log.info("Got paired: " + paired);

    XSSFCell platformCell = glrow.getCell(1);
    PlatformType pt = null;
    if (getCellValueAsString(platformCell) != null) {
      pt = PlatformType.get(getCellValueAsString(platformCell));
      log.info("Got platform type: " + pt.getKey());
    }
    if (pt == null) { throw new InputFormException("Cannot resolve Platform type from: '" + getCellValueAsString(platformCell) +"'"); }

    XSSFCell typeCell = glrow.getCell(2);
    LibraryType lt = null;
    if (getCellValueAsString(typeCell) != null) {
      String[] split = getCellValueAsString(typeCell).split("-");
      String plat = split[0];
      String type = split[1];
      if (getCellValueAsString(platformCell).equals(plat)) {
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
        log.info("Got library type: " + lt.getDescription());
      }
      else {
        throw new InputFormException("Selected library type '"+getCellValueAsString(typeCell)+"' doesn't match platform type: '" + getCellValueAsString(platformCell) +"'");
      }
    }
    if (lt == null) { throw new InputFormException("Cannot resolve Library type from: '" + getCellValueAsString(typeCell) +"'"); }

    XSSFCell selectionCell = glrow.getCell(3);
    LibrarySelectionType ls = null;
    if (getCellValueAsString(selectionCell) != null) {
      ls = manager.getLibrarySelectionTypeByName(getCellValueAsString(selectionCell));
      log.info("Got library selection type: " + ls.getName());
    }
    if (ls == null) { throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) +"'"); }

    XSSFCell strategyCell = glrow.getCell(4);
    LibraryStrategyType lst = null;
    if (getCellValueAsString(strategyCell) != null) {
      lst = manager.getLibraryStrategyTypeByName(getCellValueAsString(strategyCell));
      log.info("Got library strategy type: " + lst.getName());
    }
    if (lst == null) { throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) +"'"); }

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
      XSSFCell sampleAliasCell = row.getCell(1);

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
            throw new InputFormException("Multiple samples retrieved with this alias: '"+salias+"'. Cannot process.");
          }
        }
        else {
          throw new InputFormException("No such sample '"+salias+"'in database. Samples need to be created before using the form input functionality");
        }
      }
      else {
        log.info("Blank sample row found. Ending import.");
        break;
      }

      //sample OK - good to go
      if (s != null) {
        XSSFCell poolNumberCell = row.getCell(2);
        XSSFCell sampleQcCell = row.getCell(3);
        //XSSFCell sampleAmountCell = row.getCell(4);
        //XSSFCell sampleWaterAmountCell = row.getCell(5);
        XSSFCell barcodeKitCell = row.getCell(6);
        XSSFCell barcodeTagsCell = row.getCell(7);
        XSSFCell libraryQcCell = row.getCell(8);
        XSSFCell libraryQcInsertSizeCell = row.getCell(9);
        XSSFCell libraryQcMolarityCell = row.getCell(10);
        XSSFCell libraryQcPassFailCell = row.getCell(11);
        //XSSFCell libraryAmountCell = row.getCell(12);
        //XSSFCell libraryWaterAmountCell = row.getCell(13);
        //XSSFCell dilutionQcCell = row.getCell(14);
        XSSFCell dilutionMolarityCell = row.getCell(15);
        //XSSFCell dilutionAmountCell = row.getCell(16);
        //XSSFCell dilutionWaterAmountCell = row.getCell(17);
        //XSSFCell poolQcCell = row.getCell(18);
        //XSSFCell poolAverageInsertSizeCell = row.getCell(19);
        XSSFCell poolConvertedMolarityCell = row.getCell(20);

        //add pool, if any
        if (getCellValueAsString(poolNumberCell) != null) {
          String poolNum = String.valueOf(new Double(getCellValueAsString(poolNumberCell)).intValue());
          if (!pools.containsKey(poolNum)) {
            pools.put(poolNum, new PoolImpl<Dilution>());
            log.info("Added pool: " + poolNum);
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
            sqc.setQcType(manager.getSampleQcTypeByName("QuBit"));
            if (!s.getSampleQCs().contains(sqc)) {
              s.addQc(sqc);
              log.info("Added sample QC: " + sqc.toString());
            }
          }
          catch (NumberFormatException nfe) {
            throw new InputFormException("Supplied Sample QC concentration for sample '"+getCellValueAsString(sampleAliasCell)+"' is invalid", nfe);
          }
        }

        if (getCellValueAsString(libraryQcCell) != null) {
          //create library
          Library library = new LibraryImpl();
          library.setSample(s);

          String libAlias = libraryNamingScheme.generateNameFor("alias", library);

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
              insertSize = new Double(getCellValueAsString(libraryQcInsertSizeCell)).intValue();
            }
            catch (NumberFormatException nfe) {
              throw new InputFormException("Supplied Library insert size for library '"+libAlias+"' ("+s.getAlias()+") is invalid", nfe);
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
              throw new InputFormException("Supplied Library QC concentration for library '"+libAlias+"' ("+s.getAlias()+") is invalid", nfe);
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
                    if (tb.getName().equals(tags)) {
                      //set tag barcode
                      tbs.put(1, tb);
                    }
                  }
                }

                library.setTagBarcodes(tbs);
              }
              else {
                throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '"+s.getAlias()+"'.");
              }
            }
            else {
              throw new InputFormException("No tag barcodes associated with this kit definition: '"+s.getAlias()+"'.");
            }
          }

          log.info("Added library: " + library.toString());

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
                Pool<Dilution> p = pools.get(poolNum);
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

          if (getCellValueAsString(poolConvertedMolarityCell) != null) {
            if (getCellValueAsString(poolNumberCell) != null) {
              String poolNum = String.valueOf(new Double(getCellValueAsString(poolNumberCell)).intValue());
              Pool<Dilution> p = pools.get(poolNum);
              if (p != null) {
                try {
                  p.setConcentration(Double.valueOf(getCellValueAsString(poolConvertedMolarityCell)));
                }
                catch (NumberFormatException nfe) {
                  throw new InputFormException("Supplied pool concentration for pool '"+poolNum+"' is invalid", nfe);
                }
              }
            }
          }
        }

        samples.add(s);
      }
    }
    return samples;
  }

  public static List<Sample> importSampleInputSpreadsheet(File inPath, User u, RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      return processSampleInputXLSX(wb, u, manager, libraryNamingScheme);
    }
    else if (inPath.getName().endsWith(".ods")) {
      OdfSpreadsheetDocument oDoc = (OdfSpreadsheetDocument)OdfDocument.loadDocument(inPath);
      return processSampleInputODS(oDoc, u, manager, libraryNamingScheme);
    }
    else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  private static String getCellValueAsString(XSSFCell cell) {
    if (cell != null) {
      switch (cell.getCellType()) {
        case XSSFCell.CELL_TYPE_BLANK: return null;
        case XSSFCell.CELL_TYPE_BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
        case XSSFCell.CELL_TYPE_ERROR: return cell.getErrorCellString();
        case XSSFCell.CELL_TYPE_FORMULA: return cell.getRawValue();
        case XSSFCell.CELL_TYPE_NUMERIC: return String.valueOf(cell.getNumericCellValue());
        case XSSFCell.CELL_TYPE_STRING: return cell.getStringCellValue();
        default: return null;
      }
    }
    return null;
  }

  public static OdfTextDocument createSampleDeliveryForm(List<Sample> samples, File outpath) throws Exception {
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

      for (Sample s : samples) {
        OdfTableRow row = oTable.appendRow();

        OdfTableCell cell0 = row.getCellByIndex(0);
        OdfTextParagraph cp0 = new OdfTextParagraph(contentDom);
        cp0.setTextContent(s.getAlias());
        cp0.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell0.getOdfElement().appendChild(cp0);

        OdfTableCell cell1 = row.getCellByIndex(1);
        OdfTextParagraph cp1 = new OdfTextParagraph(contentDom);
        cp1.setTextContent(s.getScientificName());
        cp1.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell1.getOdfElement().appendChild(cp1);

        OdfTableCell cell2 = row.getCellByIndex(2);
        OdfTextParagraph cp2 = new OdfTextParagraph(contentDom);
        cp2.setTextContent(s.getIdentificationBarcode());
        cp2.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell2.getOdfElement().appendChild(cp2);

        OdfTableCell cell3 = row.getCellByIndex(3);
        OdfTextParagraph cp3 = new OdfTextParagraph(contentDom);
        cp3.setTextContent(s.getSampleType());
        cp3.setProperty(StyleTextPropertiesElement.FontSize, "8pt");
        cell3.getOdfElement().appendChild(cp3);
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

          Node n2 = ttre.getChildNodes().item(1);
          if (n2.getFirstChild() != null) {
            s.setScientificName(n2.getFirstChild().getTextContent());
          }

          Node n3 = ttre.getChildNodes().item(2);
          if (n3.getFirstChild() != null) {
            s.setIdentificationBarcode(n3.getFirstChild().getTextContent());
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

          Node n4 = ttre.getChildNodes().item(4);
          if (n4.getFirstChild() != null) {
            s.setDescription(n4.getFirstChild().getTextContent());
          }

          Node n9 = ttre.getChildNodes().item(9);
          if (n9.getFirstChild() != null) {
            if (!"".equals(n9.getFirstChild().getTextContent())) {
              Note n = new Note();
              n.setText(n9.getFirstChild().getTextContent());
              s.addNote(n);
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
