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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
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

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlateImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatePool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleQCImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Simple384WellPlate;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.DeliveryFormException;
import uk.ac.bbsrc.tgac.miso.core.exception.InputFormException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.RequestManagerAwareNamingScheme;

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

  public static void generateCSVBAC(File outpath, JSONArray jsonArray) throws Exception {
    StringBuilder sb = new StringBuilder();
    PrintWriter out = new PrintWriter(outpath);
    String formIEMFileVersion = "";
    String formInvestigatorName = "";
    String formExperimentName = "";
    String formDate = "";
    String formWorkflow = "";
    String formApplication = "";
    String formAssay = "";
    String formDescription = "";
    String formChemistry = "";
    String formRead1 = "";
    String formRead2 = "";
    String formRead3 = "";
    String formReverseComplement = "";
    String formAdapter = "";
    String formLane1Plate1 = "";
    String formBarcodeLane1Plate1 = "";
    String formLane1Plate2 = "";
    String formBarcodeLane1Plate2 = "";
    String formLane1Plate3 = "";
    String formBarcodeLane1Plate3 = "";
    String formLane1Plate4 = "";
    String formBarcodeLane1Plate4 = "";
    String formLane1Plate5 = "";
    String formBarcodeLane1Plate5 = "";
    String formLane1Plate6 = "";
    String formBarcodeLane1Plate6 = "";
    String formLane2Plate1 = "";
    String formBarcodeLane2Plate1 = "";
    String formLane2Plate2 = "";
    String formBarcodeLane2Plate2 = "";
    String formLane2Plate3 = "";
    String formBarcodeLane2Plate3 = "";
    String formLane2Plate4 = "";
    String formBarcodeLane2Plate4 = "";
    String formLane2Plate5 = "";
    String formBarcodeLane2Plate5 = "";
    String formLane2Plate6 = "";
    String formBarcodeLane2Plate6 = "";
    for (JSONObject jsonObject : (Iterable<JSONObject>) jsonArray) {
      if ("IEMFileVersion".equals(jsonObject.getString("name"))) {
        formIEMFileVersion = jsonObject.getString("value");
      }
      if ("InvestigatorName".equals(jsonObject.getString("name"))) {
        formInvestigatorName = jsonObject.getString("value");
      }
      if ("ExperimentName".equals(jsonObject.getString("name"))) {
        formExperimentName = jsonObject.getString("value");
      }
      if ("Date".equals(jsonObject.getString("name"))) {
        formDate = jsonObject.getString("value");
      }
      if ("Workflow".equals(jsonObject.getString("name"))) {
        formWorkflow = jsonObject.getString("value");
      }
      if ("Application".equals(jsonObject.getString("name"))) {
        formApplication = jsonObject.getString("value");
      }
      if ("Assay".equals(jsonObject.getString("name"))) {
        formAssay = jsonObject.getString("value");
      }
      if ("Description".equals(jsonObject.getString("name"))) {
        formDescription = jsonObject.getString("value");
      }
      if ("Chemistry".equals(jsonObject.getString("name"))) {
        formChemistry = jsonObject.getString("value");
      }
      if ("Read1".equals(jsonObject.getString("name"))) {
        formRead1 = jsonObject.getString("value");
      }
      if ("Read2".equals(jsonObject.getString("name"))) {
        formRead2 = jsonObject.getString("value");
      }
      if ("Read3".equals(jsonObject.getString("name"))) {
        formRead3 = jsonObject.getString("value");
      }
      if ("ReverseComplement".equals(jsonObject.getString("name"))) {
        formReverseComplement = jsonObject.getString("value");
      }
      if ("Adapter".equals(jsonObject.getString("name"))) {
        formAdapter = jsonObject.getString("value");
      }
      if ("Lane1Plate1".equals(jsonObject.getString("name"))) {
        formLane1Plate1 = jsonObject.getString("value");
      }
      if ("Lane1Plate2".equals(jsonObject.getString("name"))) {
        formLane1Plate2 = jsonObject.getString("value");
      }
      if ("Lane1Plate3".equals(jsonObject.getString("name"))) {
        formLane1Plate3 = jsonObject.getString("value");
      }
      if ("Lane1Plate4".equals(jsonObject.getString("name"))) {
        formLane1Plate4 = jsonObject.getString("value");
      }
      if ("Lane1Plate5".equals(jsonObject.getString("name"))) {
        formLane1Plate5 = jsonObject.getString("value");
      }
      if ("Lane1Plate6".equals(jsonObject.getString("name"))) {
        formLane1Plate6 = jsonObject.getString("value");
      }
      if ("Lane2Plate1".equals(jsonObject.getString("name"))) {
        formLane2Plate1 = jsonObject.getString("value");
      }
      if ("Lane2Plate2".equals(jsonObject.getString("name"))) {
        formLane2Plate2 = jsonObject.getString("value");
      }
      if ("Lane2Plate3".equals(jsonObject.getString("name"))) {
        formLane2Plate3 = jsonObject.getString("value");
      }
      if ("Lane2Plate4".equals(jsonObject.getString("name"))) {
        formLane2Plate4 = jsonObject.getString("value");
      }
      if ("Lane2Plate5".equals(jsonObject.getString("name"))) {
        formLane2Plate5 = jsonObject.getString("value");
      }
      if ("Lane2Plate6".equals(jsonObject.getString("name"))) {
        formLane2Plate6 = jsonObject.getString("value");
      }
      if ("BarcodeLane1Plate1".equals(jsonObject.getString("name"))) {
        formBarcodeLane1Plate1 = jsonObject.getString("value");
      }
      if ("BarcodeLane1Plate2".equals(jsonObject.getString("name"))) {
        formBarcodeLane1Plate2 = jsonObject.getString("value");
      }
      if ("BarcodeLane1Plate3".equals(jsonObject.getString("name"))) {
        formBarcodeLane1Plate3 = jsonObject.getString("value");
      }
      if ("BarcodeLane1Plate4".equals(jsonObject.getString("name"))) {
        formBarcodeLane1Plate4 = jsonObject.getString("value");
      }
      if ("BarcodeLane1Plate5".equals(jsonObject.getString("name"))) {
        formBarcodeLane1Plate5 = jsonObject.getString("value");
      }
      if ("BarcodeLane1Plate6".equals(jsonObject.getString("name"))) {
        formBarcodeLane1Plate6 = jsonObject.getString("value");
      }
      if ("BarcodeLane2Plate1".equals(jsonObject.getString("name"))) {
        formBarcodeLane2Plate1 = jsonObject.getString("value");
      }
      if ("BarcodeLane2Plate2".equals(jsonObject.getString("name"))) {
        formBarcodeLane2Plate2 = jsonObject.getString("value");
      }
      if ("BarcodeLane2Plate3".equals(jsonObject.getString("name"))) {
        formBarcodeLane2Plate3 = jsonObject.getString("value");
      }
      if ("BarcodeLane2Plate4".equals(jsonObject.getString("name"))) {
        formBarcodeLane2Plate4 = jsonObject.getString("value");
      }
      if ("BarcodeLane2Plate5".equals(jsonObject.getString("name"))) {
        formBarcodeLane2Plate5 = jsonObject.getString("value");
      }
      if ("BarcodeLane2Plate6".equals(jsonObject.getString("name"))) {
        formBarcodeLane2Plate6 = jsonObject.getString("value");
      }
    }
    sb.append("[Header],,,,,,,,,,\n");
    sb.append("IEMFileVersion," + formIEMFileVersion + ",,,,,,,,,\n");
    sb.append("Investigator Name," + formInvestigatorName + ",,,,,,,,,\n");
    sb.append("Experiment Name," + formExperimentName + ",,,,,,,,,\n");
    sb.append("Date," + formDate + ",,,,,,,,,\n");
    sb.append("Workflow," + formWorkflow + ",,,,,,,,\n");
    sb.append("Application," + formApplication + ",,,,,,,,,\n");
    sb.append("Assay," + formAssay + ",,,,,,,,,\n");
    sb.append("Description," + formDescription + ",,,,,,,,,\n");
    sb.append("Chemistry," + formChemistry + ",,,,,,,,,\n");
    sb.append(",,,,,,,,,,\n");
    sb.append("[Reads],,,,,,,,,,\n");
    sb.append(formRead1 + ",,,,,,,,,,\n");
    sb.append(formRead2 + ",,,,,,,,,,\n");
    sb.append(formRead3 + ",,,,,,,,,,\n");
    sb.append("[Settings],,,,,,,,,,\n");
    sb.append("ReverseComplement," + formReverseComplement + ",,,,,,,,,\n");
    sb.append("Adapter," + formAdapter + ",,,,,,,,,\n");
    sb.append(",,,,,,,,,,\n");
    sb.append("[Data],,,,,,,,,,\n");
    sb.append("Lane,Sample_ID,Sample_Name,Sample_Plate,Sample_Well,I7_Index_ID,index,I5_Index_ID,index2,Sample_Project,Description\n");

    sb.append("1,Plate 13_A1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n");

    // starting the plate part

    sb.append(barcodeLanePlateCSV(formBarcodeLane1Plate1, "1", formLane1Plate1));
    sb.append(barcodeLanePlateCSV(formBarcodeLane1Plate2, "1", formLane1Plate2));
    sb.append(barcodeLanePlateCSV(formBarcodeLane1Plate3, "1", formLane1Plate3));
    sb.append(barcodeLanePlateCSV(formBarcodeLane1Plate4, "1", formLane1Plate4));
    sb.append(barcodeLanePlateCSV(formBarcodeLane1Plate5, "1", formLane1Plate5));
    sb.append(barcodeLanePlateCSV(formBarcodeLane1Plate6, "1", formLane1Plate6));

    sb.append(barcodeLanePlateCSV(formBarcodeLane2Plate1, "2", formLane2Plate1));
    sb.append(barcodeLanePlateCSV(formBarcodeLane2Plate2, "2", formLane2Plate2));
    sb.append(barcodeLanePlateCSV(formBarcodeLane2Plate3, "2", formLane2Plate3));
    sb.append(barcodeLanePlateCSV(formBarcodeLane2Plate4, "2", formLane2Plate4));
    sb.append(barcodeLanePlateCSV(formBarcodeLane2Plate5, "2", formLane2Plate5));
    sb.append(barcodeLanePlateCSV(formBarcodeLane2Plate6, "2", formLane2Plate6));

    out.write(sb.toString());
    out.close();

  }

  public static String barcodeLanePlateCSV(String barcode, String lane, String plateName) {
    if ("1+7".equals(barcode)) {
      return lane + "," + plateName + "_A1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + ","
          + plateName + "_A10,,,,CGCCTCGGT P7_index_9nt_243 ,CGCCTCGGT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A11,,,,CGGTTGGCG P7_index_9nt_256 ,CGGTTGGCG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A12,,,,CTCTAGGTT P7_index_9nt_277 ,CTCTAGGTT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A13,,,,GAAGGTTAC P7_index_9nt_290 ,GAAGGTTAC,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A14,,,,GATCGCTTC P7_index_9nt_308 ,GATCGCTTC ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A15,,,,GCCGCTGGC P7_index_9nt_321 ,GCCGCTGGC,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A17,,,,GTATTATCT P7_index_9nt_363 ,GTATTATCT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A18,,,,GTCCTTGAT P7_index_9nt_368 ,GTCCTTGAT ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A2,,,,AAGAACCAT P7_index_9nt_23 ,AAGAACCAT ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A21,,,,TCTTGACTC P7_index_9nt_404 ,TCTTGACTC ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A22,,,,TGAGTCGGC P7_index_9nt_409 ,TGAGTCGGC ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A24,,,,TTGAAGGAT P7_index_9nt_435 ,TTGAAGGAT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A3,,,,AATATTCCG P7_index_9nt_45 ,AATATTCCG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A4,,,,ACTTCGTTA P7_index_9nt_91 ,ACTTCGTTA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A5,,,,AGAACTCTT P7_index_9nt_94 ,AGAACTCTT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A6,,,,AGTTCAATC P7_index_9nt_129 ,AGTTCAATC,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A7,,,,CAGAATGCT P7_index_9nt_173 ,CAGAATGCT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A8,,,,CCATCTCCA P7_index_9nt_204 ,CCATCTCCA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A9,,,,CCTGAACCA P7_index_9nt_225 ,CCTGAACCA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_B1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B10,,,,CGCCTCGGT P7_index_9nt_243 ,CGCCTCGGT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B11,,,,CGGTTGGCG P7_index_9nt_256 ,CGGTTGGCG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B12,,,,CTCTAGGTT P7_index_9nt_277 ,CTCTAGGTT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B13,,,,GAAGGTTAC P7_index_9nt_290 ,GAAGGTTAC,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B14,,,,GATCGCTTC P7_index_9nt_308 ,GATCGCTTC ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B15,,,,GCCGCTGGC P7_index_9nt_321 ,GCCGCTGGC,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B17,,,,GTATTATCT P7_index_9nt_363 ,GTATTATCT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B18,,,,GTCCTTGAT P7_index_9nt_368 ,GTCCTTGAT ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B2,,,,AAGAACCAT P7_index_9nt_23 ,AAGAACCAT ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B21,,,,TCTTGACTC P7_index_9nt_404 ,TCTTGACTC ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B22,,,,TGAGTCGGC P7_index_9nt_409 ,TGAGTCGGC ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B24,,,,TTGAAGGAT P7_index_9nt_435 ,TTGAAGGAT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B3,,,,AATATTCCG P7_index_9nt_45 ,AATATTCCG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B4,,,,ACTTCGTTA P7_index_9nt_91 ,ACTTCGTTA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B5,,,,AGAACTCTT P7_index_9nt_94 ,AGAACTCTT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B6,,,,AGTTCAATC P7_index_9nt_129 ,AGTTCAATC,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B7,,,,CAGAATGCT P7_index_9nt_173 ,CAGAATGCT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B8,,,,CCATCTCCA P7_index_9nt_204 ,CCATCTCCA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B9,,,,CCTGAACCA P7_index_9nt_225 ,CCTGAACCA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_C1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_D1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_E1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_F1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_G1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_H1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_I1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_J1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_K1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_L1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_M1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_N1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_O1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_P1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n";
    } else if ("1+9".equals(barcode)) {
      return lane + "," + plateName + "_A1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + ","
          + plateName + "_A10,,,,CGCCTCGGT P7_index_9nt_243 ,CGCCTCGGT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A11,,,,CGGTTGGCG P7_index_9nt_256 ,CGGTTGGCG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A12,,,,CTCTAGGTT P7_index_9nt_277 ,CTCTAGGTT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A13,,,,GAAGGTTAC P7_index_9nt_290 ,GAAGGTTAC,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A14,,,,GATCGCTTC P7_index_9nt_308 ,GATCGCTTC ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A15,,,,GCCGCTGGC P7_index_9nt_321 ,GCCGCTGGC,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A17,,,,GTATTATCT P7_index_9nt_363 ,GTATTATCT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A18,,,,GTCCTTGAT P7_index_9nt_368 ,GTCCTTGAT ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A2,,,,AAGAACCAT P7_index_9nt_23 ,AAGAACCAT ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A21,,,,TCTTGACTC P7_index_9nt_404 ,TCTTGACTC ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A22,,,,TGAGTCGGC P7_index_9nt_409 ,TGAGTCGGC ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A24,,,,TTGAAGGAT P7_index_9nt_435 ,TTGAAGGAT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A3,,,,AATATTCCG P7_index_9nt_45 ,AATATTCCG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A4,,,,ACTTCGTTA P7_index_9nt_91 ,ACTTCGTTA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A5,,,,AGAACTCTT P7_index_9nt_94 ,AGAACTCTT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A6,,,,AGTTCAATC P7_index_9nt_129 ,AGTTCAATC,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A7,,,,CAGAATGCT P7_index_9nt_173 ,CAGAATGCT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A8,,,,CCATCTCCA P7_index_9nt_204 ,CCATCTCCA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A9,,,,CCTGAACCA P7_index_9nt_225 ,CCTGAACCA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_B1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B10,,,,CGCCTCGGT P7_index_9nt_243 ,CGCCTCGGT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B11,,,,CGGTTGGCG P7_index_9nt_256 ,CGGTTGGCG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B12,,,,CTCTAGGTT P7_index_9nt_277 ,CTCTAGGTT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B13,,,,GAAGGTTAC P7_index_9nt_290 ,GAAGGTTAC,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B14,,,,GATCGCTTC P7_index_9nt_308 ,GATCGCTTC ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B15,,,,GCCGCTGGC P7_index_9nt_321 ,GCCGCTGGC,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B17,,,,GTATTATCT P7_index_9nt_363 ,GTATTATCT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B18,,,,GTCCTTGAT P7_index_9nt_368 ,GTCCTTGAT ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B2,,,,AAGAACCAT P7_index_9nt_23 ,AAGAACCAT ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B21,,,,TCTTGACTC P7_index_9nt_404 ,TCTTGACTC ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B22,,,,TGAGTCGGC P7_index_9nt_409 ,TGAGTCGGC ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B24,,,,TTGAAGGAT P7_index_9nt_435 ,TTGAAGGAT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B3,,,,AATATTCCG P7_index_9nt_45 ,AATATTCCG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B4,,,,ACTTCGTTA P7_index_9nt_91 ,ACTTCGTTA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B5,,,,AGAACTCTT P7_index_9nt_94 ,AGAACTCTT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B6,,,,AGTTCAATC P7_index_9nt_129 ,AGTTCAATC,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B7,,,,CAGAATGCT P7_index_9nt_173 ,CAGAATGCT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B8,,,,CCATCTCCA P7_index_9nt_204 ,CCATCTCCA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B9,,,,CCTGAACCA P7_index_9nt_225 ,CCTGAACCA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_C1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_D1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_E1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_F1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_G1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_H1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_I1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_J1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_K1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_L1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_M1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_N1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_O1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_P1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n";
    } else if ("1+11".equals(barcode)) {
      return lane + "," + plateName + "_A1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + ","
          + plateName + "_A10,,,,CGCCTCGGT P7_index_9nt_243 ,CGCCTCGGT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A11,,,,CGGTTGGCG P7_index_9nt_256 ,CGGTTGGCG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A12,,,,CTCTAGGTT P7_index_9nt_277 ,CTCTAGGTT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A13,,,,GAAGGTTAC P7_index_9nt_290 ,GAAGGTTAC,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A14,,,,GATCGCTTC P7_index_9nt_308 ,GATCGCTTC ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A15,,,,GCCGCTGGC P7_index_9nt_321 ,GCCGCTGGC,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A17,,,,GTATTATCT P7_index_9nt_363 ,GTATTATCT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A18,,,,GTCCTTGAT P7_index_9nt_368 ,GTCCTTGAT ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A2,,,,AAGAACCAT P7_index_9nt_23 ,AAGAACCAT ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A21,,,,TCTTGACTC P7_index_9nt_404 ,TCTTGACTC ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A22,,,,TGAGTCGGC P7_index_9nt_409 ,TGAGTCGGC ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A24,,,,TTGAAGGAT P7_index_9nt_435 ,TTGAAGGAT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A3,,,,AATATTCCG P7_index_9nt_45 ,AATATTCCG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A4,,,,ACTTCGTTA P7_index_9nt_91 ,ACTTCGTTA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A5,,,,AGAACTCTT P7_index_9nt_94 ,AGAACTCTT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A6,,,,AGTTCAATC P7_index_9nt_129 ,AGTTCAATC,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A7,,,,CAGAATGCT P7_index_9nt_173 ,CAGAATGCT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A8,,,,CCATCTCCA P7_index_9nt_204 ,CCATCTCCA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A9,,,,CCTGAACCA P7_index_9nt_225 ,CCTGAACCA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_B1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B10,,,,CGCCTCGGT P7_index_9nt_243 ,CGCCTCGGT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B11,,,,CGGTTGGCG P7_index_9nt_256 ,CGGTTGGCG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B12,,,,CTCTAGGTT P7_index_9nt_277 ,CTCTAGGTT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B13,,,,GAAGGTTAC P7_index_9nt_290 ,GAAGGTTAC,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B14,,,,GATCGCTTC P7_index_9nt_308 ,GATCGCTTC ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B15,,,,GCCGCTGGC P7_index_9nt_321 ,GCCGCTGGC,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B17,,,,GTATTATCT P7_index_9nt_363 ,GTATTATCT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B18,,,,GTCCTTGAT P7_index_9nt_368 ,GTCCTTGAT ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B2,,,,AAGAACCAT P7_index_9nt_23 ,AAGAACCAT ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B21,,,,TCTTGACTC P7_index_9nt_404 ,TCTTGACTC ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B22,,,,TGAGTCGGC P7_index_9nt_409 ,TGAGTCGGC ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B24,,,,TTGAAGGAT P7_index_9nt_435 ,TTGAAGGAT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B3,,,,AATATTCCG P7_index_9nt_45 ,AATATTCCG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B4,,,,ACTTCGTTA P7_index_9nt_91 ,ACTTCGTTA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B5,,,,AGAACTCTT P7_index_9nt_94 ,AGAACTCTT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B6,,,,AGTTCAATC P7_index_9nt_129 ,AGTTCAATC,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B7,,,,CAGAATGCT P7_index_9nt_173 ,CAGAATGCT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B8,,,,CCATCTCCA P7_index_9nt_204 ,CCATCTCCA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B9,,,,CCTGAACCA P7_index_9nt_225 ,CCTGAACCA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_C1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_D1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_E1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_F1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_G1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_H1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_I1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_J1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_K1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_L1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_M1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_N1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_O1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_P1,,,,AACCAACCG P7_index_9nt_1 ,AACCAACCG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P10,,,,CGCCTCGGT P7_index_9nt_243,CGCCTCGGT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P11,,,,CGGTTGGCG P7_index_9nt_256,CGGTTGGCG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P12,,,,CTCTAGGTT P7_index_9nt_277,CTCTAGGTT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P13,,,,GAAGGTTAC P7_index_9nt_290,GAAGGTTAC,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P14,,,,GATCGCTTC P7_index_9nt_308,GATCGCTTC ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P15,,,,GCCGCTGGC P7_index_9nt_321,GCCGCTGGC,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P16,,,,GGATCCGTA P7_index_9nt_343,GGATCCGTA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P17,,,,GTATTATCT P7_index_9nt_363,GTATTATCT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P18,,,,GTCCTTGAT P7_index_9nt_368,GTCCTTGAT ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P19,,,,TAGGTTAGG P7_index_9nt_381,TAGGTTAGG ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P2,,,,AAGAACCAT P7_index_9nt_23,AAGAACCAT ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P20,,,,TCGCGCCGC P7_index_9nt_397,TCGCGCCGC ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P21,,,,TCTTGACTC P7_index_9nt_404,TCTTGACTC ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P22,,,,TGAGTCGGC P7_index_9nt_409,TGAGTCGGC ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P23,,,,TTACTAACC P7_index_9nt_424,TTACTAACC ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P24,,,,TTGAAGGAT P7_index_9nt_435,TTGAAGGAT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P3,,,,AATATTCCG P7_index_9nt_45,AATATTCCG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P4,,,,ACTTCGTTA P7_index_9nt_91,ACTTCGTTA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P5,,,,AGAACTCTT P7_index_9nt_94,AGAACTCTT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P6,,,,AGTTCAATC P7_index_9nt_129,AGTTCAATC,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P7,,,,CAGAATGCT P7_index_9nt_173,CAGAATGCT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P8,,,,CCATCTCCA P7_index_9nt_204,CCATCTCCA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P9,,,,CCTGAACCA P7_index_9nt_225,CCTGAACCA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n";
    } else if ("2+7".equals(barcode)) {
      return lane + "," + plateName + "_A1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + ","
          + plateName + "_A10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A11,,,,CTCCGGTAT P7_index_9nt_273 ,CTCCGGTAT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A12,,,,CTTCCAAGC P7_index_9nt_285 ,CTTCCAAGC ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A13,,,,GAAGTCCTC P7_index_9nt_291 ,GAAGTCCTC,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A15,,,,GCTGGAGTA P7_index_9nt_336 ,GCTGGAGTA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A16,,,,GGCGGTTGG P7_index_9nt_348 ,GGCGGTTGG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A17,,,,GTCAACCGT P7_index_9nt_364 ,GTCAACCGT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A18,,,,GTCGGCTAA P7_index_9nt_371 ,GTCGGCTAA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A19,,,,TCGCCTGCC P7_index_9nt_396 ,TCGCCTGCC  ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A20,,,,TCGGATATC P7_index_9nt_398 ,TCGGATATC ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A22,,,,TGGTTAATG P7_index_9nt_420 ,TGGTTAATG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A23,,,,TTATAGTTC P7_index_9nt_427 ,TTATAGTTC,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A24,,,,TTGGTCTCG P7_index_9nt_438 ,TTGGTCTCG ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A3,,,,ACCAAGAGT P7_index_9nt_54 ,ACCAAGAGT,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A4,,,,AGAACGAAG P7_index_9nt_93 ,AGAACGAAG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A5,,,,AGAAGAACT P7_index_9nt_95 ,AGAAGAACT ,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A6,,,,ATGCTCCAG P7_index_9nt_150 ,ATGCTCCAG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A7,,,,CAGAGGTAG P7_index_9nt_175 ,CAGAGGTAG,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A8,,,,CCTCAGAGA P7_index_9nt_222 ,CCTCAGAGA,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_A9,,,,CCTTGGTAC P7_index_9nt_228 ,CCTTGGTAC,AACCATGGA P5_index_9nt_3,AACCATGGA ,,\n" + lane + "," + plateName
          + "_B1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B11,,,,CTCCGGTAT P7_index_9nt_273 ,CTCCGGTAT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B12,,,,CTTCCAAGC P7_index_9nt_285 ,CTTCCAAGC ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B13,,,,GAAGTCCTC P7_index_9nt_291 ,GAAGTCCTC,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B15,,,,GCTGGAGTA P7_index_9nt_336 ,GCTGGAGTA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B16,,,,GGCGGTTGG P7_index_9nt_348 ,GGCGGTTGG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B17,,,,GTCAACCGT P7_index_9nt_364 ,GTCAACCGT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B18,,,,GTCGGCTAA P7_index_9nt_371 ,GTCGGCTAA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B19,,,,TCGCCTGCC P7_index_9nt_396 ,TCGCCTGCC  ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B20,,,,TCGGATATC P7_index_9nt_398 ,TCGGATATC ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B22,,,,TGGTTAATG P7_index_9nt_420 ,TGGTTAATG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B23,,,,TTATAGTTC P7_index_9nt_427 ,TTATAGTTC,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B24,,,,TTGGTCTCG P7_index_9nt_438 ,TTGGTCTCG ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B3,,,,ACCAAGAGT P7_index_9nt_54 ,ACCAAGAGT,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B4,,,,AGAACGAAG P7_index_9nt_93 ,AGAACGAAG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B5,,,,AGAAGAACT P7_index_9nt_95 ,AGAAGAACT ,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B6,,,,ATGCTCCAG P7_index_9nt_150 ,ATGCTCCAG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B7,,,,CAGAGGTAG P7_index_9nt_175 ,CAGAGGTAG,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B8,,,,CCTCAGAGA P7_index_9nt_222 ,CCTCAGAGA,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_B9,,,,CCTTGGTAC P7_index_9nt_228 ,CCTTGGTAC,AACGATATG P5_index_9nt_11,AACGATATG ,,\n" + lane + "," + plateName
          + "_C1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_C9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,AAGAGTATT P5_index_9nt_29,AAGAGTATT ,,\n" + lane + "," + plateName
          + "_D1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_D9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,ATATAACCA P5_index_9nt_143,ATATAACCA ,,\n" + lane + "," + plateName
          + "_E1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_E9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CAACCTAAC P5_index_9nt_160,CAACCTAAC,,\n" + lane + "," + plateName
          + "_F1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_F9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CCGACTAGG P5_index_9nt_207,CCGACTAGG,,\n" + lane + "," + plateName
          + "_G1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_G9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CGGTCTATA P5_index_9nt_255,CGGTCTATA ,,\n" + lane + "," + plateName
          + "_H1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_H9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CTCATGCTA P5_index_9nt_271,CTCATGCTA ,,\n" + lane + "," + plateName
          + "_I1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_I9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GAACTGAGC P5_index_9nt_288,GAACTGAGC ,,\n" + lane + "," + plateName
          + "_J1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_J9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GCCAGCGCT P5_index_9nt_318,GCCAGCGCT,,\n" + lane + "," + plateName
          + "_K1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_K9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GCTCTGGTT P5_index_9nt_335,GCTCTGGTT ,,\n" + lane + "," + plateName
          + "_L1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_L9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GGTAACTTA P5_index_9nt_351,GGTAACTTA ,,\n" + lane + "," + plateName
          + "_M1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_M9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GTTGCATCG P5_index_9nt_375,GTTGCATCG,,\n" + lane + "," + plateName
          + "_N1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_N9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TCCTTCTTG P5_index_9nt_395,TCCTTCTTG ,,\n" + lane + "," + plateName
          + "_O1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_O9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TGAGCAACG P5_index_9nt_407,TGAGCAACG ,,\n" + lane + "," + plateName
          + "_P1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n" + lane + "," + plateName
          + "_P9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TTCGTAGAA P5_index_9nt_431,TTCGTAGAA,,\n";
    } else if ("2+9".equals(barcode)) {
      return lane + "," + plateName + "_A1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + ","
          + plateName + "_A10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A11,,,,CTCCGGTAT P7_index_9nt_273 ,CTCCGGTAT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A12,,,,CTTCCAAGC P7_index_9nt_285 ,CTTCCAAGC ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A13,,,,GAAGTCCTC P7_index_9nt_291 ,GAAGTCCTC,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A15,,,,GCTGGAGTA P7_index_9nt_336 ,GCTGGAGTA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A16,,,,GGCGGTTGG P7_index_9nt_348 ,GGCGGTTGG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A17,,,,GTCAACCGT P7_index_9nt_364 ,GTCAACCGT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A18,,,,GTCGGCTAA P7_index_9nt_371 ,GTCGGCTAA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A19,,,,TCGCCTGCC P7_index_9nt_396 ,TCGCCTGCC  ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A20,,,,TCGGATATC P7_index_9nt_398 ,TCGGATATC ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A22,,,,TGGTTAATG P7_index_9nt_420 ,TGGTTAATG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A23,,,,TTATAGTTC P7_index_9nt_427 ,TTATAGTTC,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A24,,,,TTGGTCTCG P7_index_9nt_438 ,TTGGTCTCG ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A3,,,,ACCAAGAGT P7_index_9nt_54 ,ACCAAGAGT,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A4,,,,AGAACGAAG P7_index_9nt_93 ,AGAACGAAG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A5,,,,AGAAGAACT P7_index_9nt_95 ,AGAAGAACT ,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A6,,,,ATGCTCCAG P7_index_9nt_150 ,ATGCTCCAG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A7,,,,CAGAGGTAG P7_index_9nt_175 ,CAGAGGTAG,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A8,,,,CCTCAGAGA P7_index_9nt_222 ,CCTCAGAGA,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_A9,,,,CCTTGGTAC P7_index_9nt_228 ,CCTTGGTAC,AACCGCCAA P5_index_9nt_5,AACCGCCAA,,\n" + lane + "," + plateName
          + "_B1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B11,,,,CTCCGGTAT P7_index_9nt_273 ,CTCCGGTAT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B12,,,,CTTCCAAGC P7_index_9nt_285 ,CTTCCAAGC ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B13,,,,GAAGTCCTC P7_index_9nt_291 ,GAAGTCCTC,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B15,,,,GCTGGAGTA P7_index_9nt_336 ,GCTGGAGTA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B16,,,,GGCGGTTGG P7_index_9nt_348 ,GGCGGTTGG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B17,,,,GTCAACCGT P7_index_9nt_364 ,GTCAACCGT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B18,,,,GTCGGCTAA P7_index_9nt_371 ,GTCGGCTAA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B19,,,,TCGCCTGCC P7_index_9nt_396 ,TCGCCTGCC  ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B20,,,,TCGGATATC P7_index_9nt_398 ,TCGGATATC ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B22,,,,TGGTTAATG P7_index_9nt_420 ,TGGTTAATG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B23,,,,TTATAGTTC P7_index_9nt_427 ,TTATAGTTC,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B24,,,,TTGGTCTCG P7_index_9nt_438 ,TTGGTCTCG ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B3,,,,ACCAAGAGT P7_index_9nt_54 ,ACCAAGAGT,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B4,,,,AGAACGAAG P7_index_9nt_93 ,AGAACGAAG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B5,,,,AGAAGAACT P7_index_9nt_95 ,AGAAGAACT ,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B6,,,,ATGCTCCAG P7_index_9nt_150 ,ATGCTCCAG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B7,,,,CAGAGGTAG P7_index_9nt_175 ,CAGAGGTAG,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B8,,,,CCTCAGAGA P7_index_9nt_222 ,CCTCAGAGA,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_B9,,,,CCTTGGTAC P7_index_9nt_228 ,CCTTGGTAC,AACGCCGTA P5_index_9nt_13,AACGCCGTA ,,\n" + lane + "," + plateName
          + "_C1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_C9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,AGCGTCGAG P5_index_9nt_117,AGCGTCGAG ,,\n" + lane + "," + plateName
          + "_D1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_D9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,ATGCCTTAC P5_index_9nt_149,ATGCCTTAC,,\n" + lane + "," + plateName
          + "_E1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_E9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CCATAGACG P5_index_9nt_203,CCATAGACG ,,\n" + lane + "," + plateName
          + "_F1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_F9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CCGTCAGCT P5_index_9nt_216,CCGTCAGCT ,,\n" + lane + "," + plateName
          + "_G1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_G9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CGTAGAGCC P5_index_9nt_258,CGTAGAGCC ,,\n" + lane + "," + plateName
          + "_H1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_H9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CTGGAACGC P5_index_9nt_282,CTGGAACGC ,,\n" + lane + "," + plateName
          + "_I1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_I9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GAAGGACGG P5_index_9nt_289,GAAGGACGG,,\n" + lane + "," + plateName
          + "_J1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_J9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GCCTATTAA P5_index_9nt_324,GCCTATTAA,,\n" + lane + "," + plateName
          + "_K1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_K9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GGATATCCT P5_index_9nt_342,GGATATCCT ,,\n" + lane + "," + plateName
          + "_L1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_L9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GGTTCCTCC P5_index_9nt_355,GGTTCCTCC,,\n" + lane + "," + plateName
          + "_M1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_M9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TAACTCTAC P5_index_9nt_376,TAACTCTAC ,,\n" + lane + "," + plateName
          + "_N1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_N9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TCTATCCTT P5_index_9nt_400,TCTATCCTT ,,\n" + lane + "," + plateName
          + "_O1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_O9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TGGAGTTCC P5_index_9nt_417,TGGAGTTCC ,,\n" + lane + "," + plateName
          + "_P1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n" + lane + "," + plateName
          + "_P9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TTGAGAGCG P5_index_9nt_436,TTGAGAGCG,,\n";
    } else if ("2+11".equals(barcode)) {
      return lane + "," + plateName + "_A1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + ","
          + plateName + "_A10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A11,,,,CTCCGGTAT P7_index_9nt_273 ,CTCCGGTAT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A12,,,,CTTCCAAGC P7_index_9nt_285 ,CTTCCAAGC ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A13,,,,GAAGTCCTC P7_index_9nt_291 ,GAAGTCCTC,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A15,,,,GCTGGAGTA P7_index_9nt_336 ,GCTGGAGTA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A16,,,,GGCGGTTGG P7_index_9nt_348 ,GGCGGTTGG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A17,,,,GTCAACCGT P7_index_9nt_364 ,GTCAACCGT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A18,,,,GTCGGCTAA P7_index_9nt_371 ,GTCGGCTAA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A19,,,,TCGCCTGCC P7_index_9nt_396 ,TCGCCTGCC  ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A20,,,,TCGGATATC P7_index_9nt_398 ,TCGGATATC ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A22,,,,TGGTTAATG P7_index_9nt_420 ,TGGTTAATG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A23,,,,TTATAGTTC P7_index_9nt_427 ,TTATAGTTC,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A24,,,,TTGGTCTCG P7_index_9nt_438 ,TTGGTCTCG ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A3,,,,ACCAAGAGT P7_index_9nt_54 ,ACCAAGAGT,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A4,,,,AGAACGAAG P7_index_9nt_93 ,AGAACGAAG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A5,,,,AGAAGAACT P7_index_9nt_95 ,AGAAGAACT ,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A6,,,,ATGCTCCAG P7_index_9nt_150 ,ATGCTCCAG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A7,,,,CAGAGGTAG P7_index_9nt_175 ,CAGAGGTAG,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A8,,,,CCTCAGAGA P7_index_9nt_222 ,CCTCAGAGA,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_A9,,,,CCTTGGTAC P7_index_9nt_228 ,CCTTGGTAC,AACCGGTTG P5_index_9nt_6,AACCGGTTG ,,\n" + lane + "," + plateName
          + "_B1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B11,,,,CTCCGGTAT P7_index_9nt_273 ,CTCCGGTAT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B12,,,,CTTCCAAGC P7_index_9nt_285 ,CTTCCAAGC ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B13,,,,GAAGTCCTC P7_index_9nt_291 ,GAAGTCCTC,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B15,,,,GCTGGAGTA P7_index_9nt_336 ,GCTGGAGTA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B16,,,,GGCGGTTGG P7_index_9nt_348 ,GGCGGTTGG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B17,,,,GTCAACCGT P7_index_9nt_364 ,GTCAACCGT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B18,,,,GTCGGCTAA P7_index_9nt_371 ,GTCGGCTAA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B19,,,,TCGCCTGCC P7_index_9nt_396 ,TCGCCTGCC  ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B20,,,,TCGGATATC P7_index_9nt_398 ,TCGGATATC ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B22,,,,TGGTTAATG P7_index_9nt_420 ,TGGTTAATG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B23,,,,TTATAGTTC P7_index_9nt_427 ,TTATAGTTC,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B24,,,,TTGGTCTCG P7_index_9nt_438 ,TTGGTCTCG ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B3,,,,ACCAAGAGT P7_index_9nt_54 ,ACCAAGAGT,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B4,,,,AGAACGAAG P7_index_9nt_93 ,AGAACGAAG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B5,,,,AGAAGAACT P7_index_9nt_95 ,AGAAGAACT ,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B6,,,,ATGCTCCAG P7_index_9nt_150 ,ATGCTCCAG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B7,,,,CAGAGGTAG P7_index_9nt_175 ,CAGAGGTAG,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B8,,,,CCTCAGAGA P7_index_9nt_222 ,CCTCAGAGA,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_B9,,,,CCTTGGTAC P7_index_9nt_228 ,CCTTGGTAC,AAGACCAGA P5_index_9nt_25,AAGACCAGA,,\n" + lane + "," + plateName
          + "_C1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_C9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,AGTAATGAT P5_index_9nt_125,AGTAATGAT ,,\n" + lane + "," + plateName
          + "_D1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_D9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,ATTGCCAAG P5_index_9nt_157,ATTGCCAAG ,,\n" + lane + "," + plateName
          + "_E1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_E9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CCATTGCAT P5_index_9nt_205,CCATTGCAT ,,\n" + lane + "," + plateName
          + "_F1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_F9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CCTTGCCGA P5_index_9nt_227,CCTTGCCGA ,,\n" + lane + "," + plateName
          + "_G1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_G9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CTACTATAG P5_index_9nt_263,CTACTATAG ,,\n" + lane + "," + plateName
          + "_H1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_H9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,CTTGGAGAG P5_index_9nt_287,CTTGGAGAG ,,\n" + lane + "," + plateName
          + "_I1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_I9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GCATAGCGA P5_index_9nt_315,GCATAGCGA ,,\n" + lane + "," + plateName
          + "_J1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_J9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GCTAGTTGA P5_index_9nt_333,GCTAGTTGA,,\n" + lane + "," + plateName
          + "_K1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_K9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GGCGAATCA P5_index_9nt_346,GGCGAATCA ,,\n" + lane + "," + plateName
          + "_L1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_L9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,GTTAGTACT P5_index_9nt_374,GTTAGTACT ,,\n" + lane + "," + plateName
          + "_M1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_M9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TATTAGAGT P5_index_9nt_388,TATTAGAGT ,,\n" + lane + "," + plateName
          + "_N1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_N9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TGACTGGTA P5_index_9nt_406,TGACTGGTA ,,\n" + lane + "," + plateName
          + "_O1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O2,,,,AAGACTGAG P7_index_9nt_27,AAGACTGAG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_O9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TGGCAGCTC P5_index_9nt_418,TGGCAGCTC ,,\n" + lane + "," + plateName
          + "_P1,,,,AACCGAAGT P7_index_9nt_4,AACCGAAGT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P10,,,,CGCTAACGA P7_index_9nt_248,CGCTAACGA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P11,,,,CTCCGGTAT P7_index_9nt_273,CTCCGGTAT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P12,,,,CTTCCAAGC P7_index_9nt_285,CTTCCAAGC ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P13,,,,GAAGTCCTC P7_index_9nt_291,GAAGTCCTC,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P14,,,,GATGCGAAC P7_index_9nt_310,GATGCGAAC,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P15,,,,GCTGGAGTA P7_index_9nt_336,GCTGGAGTA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P16,,,,GGCGGTTGG P7_index_9nt_348,GGCGGTTGG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P17,,,,GTCAACCGT P7_index_9nt_364,GTCAACCGT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P18,,,,GTCGGCTAA P7_index_9nt_371,GTCGGCTAA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P19,,,,TCGCCTGCC P7_index_9nt_396,TCGCCTGCC  ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P2,,,,AAGACTGAG P7_index_9nt_27 ,AAGACTGAG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P20,,,,TCGGATATC P7_index_9nt_398,TCGGATATC ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P21,,,,TGAACCTGA P7_index_9nt_405,TGAACCTGA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P22,,,,TGGTTAATG P7_index_9nt_420,TGGTTAATG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P23,,,,TTATAGTTC P7_index_9nt_427,TTATAGTTC,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P24,,,,TTGGTCTCG P7_index_9nt_438,TTGGTCTCG ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P3,,,,ACCAAGAGT P7_index_9nt_54,ACCAAGAGT,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P4,,,,AGAACGAAG P7_index_9nt_93,AGAACGAAG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P5,,,,AGAAGAACT P7_index_9nt_95,AGAAGAACT ,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P6,,,,ATGCTCCAG P7_index_9nt_150,ATGCTCCAG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P7,,,,CAGAGGTAG P7_index_9nt_175,CAGAGGTAG,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P8,,,,CCTCAGAGA P7_index_9nt_222,CCTCAGAGA,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n" + lane + "," + plateName
          + "_P9,,,,CCTTGGTAC P7_index_9nt_228,CCTTGGTAC,TTGCTGCGT P5_index_9nt_437,TTGCTGCGT ,,\n";
    } else {
      return "";
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
          if (barcodekit != null) {
            XSSFCell cellJ = row.createCell(9);
            cellJ.setCellValue(barcodekit);
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
    } else {
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
        } else if ("paired".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellA = row2.createCell(0);
          row2cellA.setCellValue(jsonObject.getString("value"));
        } else if ("platforms".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellB = row2.createCell(1);
          row2cellB.setCellValue(jsonObject.getString("value"));
        } else if ("types".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellC = row2.createCell(2);
          row2cellC.setCellValue(jsonObject.getString("value"));
        } else if ("selections".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellD = row2.createCell(3);
          row2cellD.setCellValue(jsonObject.getString("value"));
        } else if ("strategies".equals(jsonObject.getString("name"))) {
          XSSFCell row2cellE = row2.createCell(4);
          row2cellE.setCellValue(jsonObject.getString("value"));
        } else if ("sampleinwell".equals(jsonObject.getString("name"))) {
          String sampleinwell = jsonObject.getString("value");
          // "sampleid:wellid:samplealias:projectname"
          // String sampleId = sampleinwell.split(":")[0];
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
    } else {
      throw new IOException("Could not read from resource.");
    }

  }

  // private static Map<String, Pool<Plate<LinkedList<Library>, Library>>> process384PlateInputODS(OdfSpreadsheetDocument oDoc, User u,
  // RequestManager manager, MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
  private static Map<String, PlatePool> process384PlateInputODS(OdfSpreadsheetDocument oDoc, User u, RequestManager manager,
      MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme) libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
    OdfTable oTable = oDoc.getTableList().get(0);

    // process global headers
    OdfTableCell pairedCell = oTable.getCellByPosition("A2");
    boolean paired = false;
    if (pairedCell.getBooleanValue() != null) {
      paired = pairedCell.getBooleanValue();
      log.info("Got paired: " + paired);
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
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
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
      ls = manager.getLibrarySelectionTypeByName(selectionCell.getStringValue());
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + selectionCell.getStringValue() + "'");
    } else {
      log.info("Got library selection type: " + ls.getName());
    }

    OdfTableCell strategyCell = oTable.getCellByPosition("E2");
    LibraryStrategyType lst = null;
    if (!isStringEmptyOrNull(strategyCell.getStringValue())) {
      lst = manager.getLibraryStrategyTypeByName(strategyCell.getStringValue());
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + strategyCell.getStringValue() + "'");
    } else {
      log.info("Got library strategy type: " + lst.getName());
    }

    OdfTableCell plateBarcodeCell = oTable.getCellByPosition("F2");
    String plateBarcode = null;
    if (!isStringEmptyOrNull(plateBarcodeCell.getStringValue())) {
      plateBarcode = plateBarcodeCell.getStringValue();
    }
    if (plateBarcode == null) {
      throw new InputFormException("Cannot resolve plate barcode from: '" + plateBarcodeCell.getStringValue() + "'");
    } else {
      log.info("Got plate parcode: " + plateBarcode);
    }

    // process entries
    Simple384WellPlate libraryPlate = null;
    // Map<String, Pool<Plate<LinkedList<Library>, Library>>> pools = new HashMap<String, Pool<Plate<LinkedList<Library>, Library>>>();
    Map<String, PlatePool> pools = new HashMap<String, PlatePool>();
    for (OdfTableRow row : oTable.getRowList()) {
      int ri = row.getRowIndex();
      if (ri > 3) {
        // Ax - plate position
        OdfTableCell platePosCell = oTable.getCellByPosition(0, ri);
        if (!isStringEmptyOrNull(platePosCell.getStringValue()) && libraryPlate == null) {
          // plated libraries - process as plate
          libraryPlate = new Simple384WellPlate();
          libraryPlate.setIdentificationBarcode(plateBarcode);
          libraryPlate.setCreationDate(new Date());
        }

        // cell defs
        OdfTableCell sampleAliasCell = oTable.getCellByPosition(1, ri);

        Sample s = null;
        if (!isStringEmptyOrNull(sampleAliasCell.getStringValue())) {
          Collection<Sample> ss = manager.listSamplesByAlias(sampleAliasCell.getStringValue());
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
          OdfTableCell entityIDCell = oTable.getCellByPosition(2, ri);
          OdfTableCell poolNumberCell = oTable.getCellByPosition(3, ri);
          OdfTableCell sampleQcCell = oTable.getCellByPosition(4, ri);
          // OdfTableCell sampleAmountCell = oTable.getCellByPosition(5, ri);
          // OdfTableCell sampleWaterAmountCell = oTable.getCellByPosition(6, ri);
          OdfTableCell barcodeKitCell = oTable.getCellByPosition(7, ri);
          OdfTableCell barcodeTagsCell = oTable.getCellByPosition(8, ri);
          OdfTableCell libraryQcCell = oTable.getCellByPosition(9, ri);
          OdfTableCell libraryQcInsertSizeCell = oTable.getCellByPosition(10, ri);
          OdfTableCell libraryQcMolarityCell = oTable.getCellByPosition(11, ri);
          OdfTableCell libraryQcPassFailCell = oTable.getCellByPosition(12, ri);
          // OdfTableCell libraryAmountCell = oTable.getCellByPosition(13, ri);
          // OdfTableCell libraryWaterAmountCell = oTable.getCellByPosition(14, ri);
          // OdfTableCell dilutionQcCell = oTable.getCellByPosition(15, ri);
          OdfTableCell dilutionMolarityCell = oTable.getCellByPosition(16, ri);
          // OdfTableCell dilutionAmountCell = oTable.getCellByPosition(17, ri);
          // OdfTableCell dilutionWaterAmountCell = oTable.getCellByPosition(18, ri);
          OdfTableCell poolQcCell = oTable.getCellByPosition(19, ri);
          // OdfTableCell poolAverageInsertSizeCell = oTable.getCellByPosition(20, ri);
          OdfTableCell poolConvertedMolarityCell = oTable.getCellByPosition(21, ri);

          // add pool, if any
          if (!isStringEmptyOrNull(poolNumberCell.getStringValue())) {
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

          // process sample QC
          if (!isStringEmptyOrNull(sampleQcCell.getStringValue())) {
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
            } catch (NumberFormatException nfe) {
              throw new InputFormException(
                  "Supplied Sample QC concentration for sample '" + sampleAliasCell.getStringValue() + "' is invalid", nfe);
            }
          }

          // if (!"".equals(libraryQcCell.getStringValue())) {
          if (barcodeKitCell.getStringValue() != null && barcodeTagsCell.getStringValue() != null) {
            // create library
            Library library = new LibraryImpl();
            library.setSample(s);

            Matcher mat = samplePattern.matcher(s.getAlias());
            if (mat.matches()) {
              String libAlias = plateBarcode + "_" + "L" + mat.group(2) + "-" + platePosCell.getStringValue() + "_"
                  + entityIDCell.getStringValue();
              // String libAlias = libraryNamingScheme.generateNameFor("alias", library);
              // library.setAlias(libAlias);

              library.setAlias(libAlias);
              library.setSecurityProfile(s.getSecurityProfile());
              library.setDescription(s.getDescription());
              library.setCreationDate(new Date());
              library.setPlatformName(pt.name());
              library.setLibraryType(lt);
              library.setLibrarySelectionType(ls);
              library.setLibraryStrategyType(lst);
              library.setPaired(paired);

              if (!isStringEmptyOrNull(libraryQcMolarityCell.getStringValue())) {
                int insertSize = 0;
                try {
                  String bp = libraryQcInsertSizeCell.getStringValue();
                  Matcher m = digitPattern.matcher(bp);
                  if (m.matches()) {
                    insertSize = Integer.valueOf(m.group(1));
                  } else {
                    throw new InputFormException(
                        "Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid");
                  }
                } catch (NumberFormatException nfe) {
                  throw new InputFormException(
                      "Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
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
                  } else {
                    // TODO check libraryQcPassFailCell?
                    library.setQcPassed(true);
                  }
                } catch (NumberFormatException nfe) {
                  throw new InputFormException(
                      "Supplied Library QC concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              if (!isStringEmptyOrNull(barcodeKitCell.getStringValue())) {
                Collection<TagBarcode> bcs = manager.listAllTagBarcodesByStrategyName(barcodeKitCell.getStringValue());
                if (!bcs.isEmpty()) {
                  String tags = barcodeTagsCell.getStringValue();
                  if (!isStringEmptyOrNull(tags)) {
                    HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
                    if (tags.contains("-")) {
                      String[] splits = tags.split("-");
                      int count = 1;
                      for (String tag : splits) {
                        for (TagBarcode tb : bcs) {
                          if (tb.getName().equals(tag)) {
                            // set tag barcodes
                            tbs.put(count, tb);
                            count++;
                          }
                        }
                      }
                    } else {
                      for (TagBarcode tb : bcs) {
                        if (tb.getName().equals(tags)) {
                          // set tag barcode
                          tbs.put(1, tb);
                        }
                      }
                    }

                    library.setTagBarcodes(tbs);
                  } else {
                    throw new InputFormException(
                        "Barcode Kit specified but no tag barcodes entered for: '" + sampleAliasCell.getStringValue() + "'.");
                  }
                } else {
                  throw new InputFormException(
                      "No tag barcodes associated with this kit definition: '" + barcodeKitCell.getStringValue() + "'.");
                }
              }

              if (!isStringEmptyOrNull(poolConvertedMolarityCell.getStringValue())) {
                Pool<Plate<LinkedList<Library>, Library>> p = pools.get(poolNumberCell.getStringValue());
                if (p != null) {
                  log.debug("Retrieved pool " + poolNumberCell.getStringValue());
                  try {
                    double d = Double.valueOf(poolConvertedMolarityCell.getStringValue());
                    p.setConcentration(d);
                  } catch (NumberFormatException nfe) {
                    throw new InputFormException(
                        "Supplied pool concentration for pool '" + poolNumberCell.getStringValue() + "' is invalid", nfe);
                  }
                }
              }

              log.info("Added library: " + library.toString());

              if (!isStringEmptyOrNull(platePosCell.getStringValue()) && libraryPlate != null) {
                libraryPlate.addElement(library);
                log.info("Added library " + library.getAlias() + " to " + platePosCell.getStringValue());
              }

              samples.add(s);

              Pool<Plate<LinkedList<Library>, Library>> p = pools.get(poolNumberCell.getStringValue());
              if (p != null && !p.getPoolableElements().contains(libraryPlate)) {
                p.addPoolableElement(libraryPlate);
                log.info("Added plate to pool: " + p.toString());
              }
            } else {
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
          Date date = new Date();

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
          } else {
            jsonObject.put("adaptor", "");

          }

          jsonArray.add(jsonObject);
          XSSFCell qcResultCell = null;
          if (!"NA".equals(getCellValueAsString(row.getCell(6)))) {
            qcResultCell = row.getCell(6);
          } else if (!"NA".equals(getCellValueAsString(row.getCell(7)))) {
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
              } else {
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
                } else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
                  s.setQcPassed(false);
                  manager.saveSample(s);
                  log.info("Marked sample QC Passed as False");
                }

              }
            }
            if (!isStringEmptyOrNull(getCellValueAsString(wellCell))
                && !"NA".equals(getCellValueAsString(wellCell))) {
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
            if (!isStringEmptyOrNull(getCellValueAsString(rinCell))
                && !"NA".equals(getCellValueAsString(rinCell))) {
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
            if (!isStringEmptyOrNull(getCellValueAsString(sample260280Cell))) {
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
            if (!isStringEmptyOrNull(getCellValueAsString(sample260230Cell))) {
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

          } catch (NumberFormatException nfe) {
            throw new InputFormException(
                "Supplied Sample QC concentration for sample '" + getCellValueAsString(sampleAliasCell) + "' is invalid", nfe);
          }
        }
      }
      return jsonArray;
    } else {
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
          Collection<Sample> ss = manager.listSamplesByAlias(salias);
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

          if (getCellValueAsString(barcodeKitCell) != null
              && ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(barcodeKitCell));
          } else {
            rowsJSONArray.add("");
          }

          if (getCellValueAsString(barcodeTagsCell) != null
              && ("A".equals(proceedKey) || "L".equals(proceedKey) || "U".equals(proceedKey))) {
            rowsJSONArray.add(getCellValueAsString(barcodeTagsCell));
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

  public static String processLibraryPoolSheetImport(File inPath, User u, RequestManager manager) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      XSSFSheet sheet = wb.getSheetAt(0);

      XSSFRow glrow = sheet.getRow(1);

      // process global headers
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
          lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
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
        ls = manager.getLibrarySelectionTypeByName(getCellValueAsString(selectionCell));
      }
      if (ls == null) {
        throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
      } else {
        log.info("Got library selection type: " + ls.getName());
      }

      XSSFCell strategyCell = glrow.getCell(4);
      LibraryStrategyType lst = null;
      if (getCellValueAsString(strategyCell) != null) {
        lst = manager.getLibraryStrategyTypeByName(getCellValueAsString(strategyCell));
      }
      if (lst == null) {
        throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) + "'");
      } else {
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
            // create library
            Library library = new LibraryImpl();
            library.setSample(s);

            Matcher mat = samplePattern.matcher(s.getAlias());
            if (mat.matches()) {
              String libAlias = mat.group(1) + "_" + "L" + mat.group(2) + "-" + platePos + "_" + mat.group(3);

              library.setAlias(libAlias);
              library.setSecurityProfile(s.getSecurityProfile());
              if (!isStringEmptyOrNull(getCellValueAsString(libraryDescriptionCell))) {
                library.setDescription(getCellValueAsString(libraryDescriptionCell));
              } else {
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
                  } else {
                    throw new InputFormException(
                        "Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid");
                  }
                } catch (NumberFormatException nfe) {
                  throw new InputFormException(
                      "Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
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
                  } else {
                    if (getCellValueAsString(qcPassedCell) != null) {
                      if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
                        library.setQcPassed(true);
                        log.info("Marked library QC Passed as True");
                      } else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
                        library.setQcPassed(false);
                        log.info("Marked library QC Passed as False");
                      }
                    }
                  }
                } catch (NumberFormatException nfe) {
                  throw new InputFormException(
                      "Supplied Library QC concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              if (getCellValueAsString(libraryQubitCell) != null) {
                int insertSize = 0;
                try {
                  String bp = getCellValueAsString(libraryQcInsertSizeCell);
                  Matcher m = digitPattern.matcher(bp);
                  if (m.matches()) {
                    insertSize = Integer.valueOf(m.group(1));
                  } else {
                    throw new InputFormException(
                        "Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid");
                  }
                } catch (NumberFormatException nfe) {
                  throw new InputFormException(
                      "Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
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
                  } else {
                    if (getCellValueAsString(qcPassedCell) != null) {
                      if ("Y".equals(getCellValueAsString(qcPassedCell)) || "y".equals(getCellValueAsString(qcPassedCell))) {
                        library.setQcPassed(true);
                        log.info("Marked library QC Passed as True");
                      } else if ("N".equals(getCellValueAsString(qcPassedCell)) || "n".equals(getCellValueAsString(qcPassedCell))) {
                        library.setQcPassed(false);
                        log.info("Marked library QC Passed as False");
                      }
                    }
                  }
                } catch (NumberFormatException nfe) {
                  throw new InputFormException(
                      "Supplied Library QC concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              if (getCellValueAsString(barcodeKitCell) != null) {
                Collection<TagBarcode> bcs = manager.listAllTagBarcodesByStrategyName(getCellValueAsString(barcodeKitCell));
                if (!bcs.isEmpty()) {
                  String tags = getCellValueAsString(barcodeTagsCell);
                  if (!isStringEmptyOrNull(tags)) {
                    HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
                    if (tags.contains("-")) {
                      String[] splits = tags.split("-");
                      int count = 1;
                      for (String tag : splits) {
                        for (TagBarcode tb : bcs) {
                          if (tb.getName().equals(tag)) {
                            // set tag barcodes
                            tbs.put(count, tb);
                            count++;
                          }
                        }
                      }
                    } else {
                      for (TagBarcode tb : bcs) {
                        if (tb.getName().equals(tags) || tb.getSequence().equals(tags)) {
                          // set tag barcode
                          tbs.put(1, tb);
                          log.info("Got tag barcode: " + tb.getName());
                          break;
                        }
                      }
                    }

                    library.setTagBarcodes(tbs);
                  } else {
                    throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '" + s.getAlias() + "'.");
                  }
                } else {
                  throw new InputFormException("No tag barcodes associated with the kit definition '" + getCellValueAsString(barcodeKitCell)
                      + "' for sample: '" + s.getAlias() + "'.");
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
                } catch (NumberFormatException nfe) {
                  throw new InputFormException(
                      "Supplied LibraryDilution concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
                }
              }

              log.info("Added library: " + library.toString());
              manager.saveLibrary(library);
            } else {
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
              } else {
                pool.setConcentration(0.0);
              }
              pools.put(poolNum, pool);
              log.info("Added pool: " + poolNum);
              if (ldi != null) {
                pool.addPoolableElement(ldi);
              }
              manager.savePool(pool);
            } else {
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
    } else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  private static Map<String, PlatePool> process384PlateInputXLSX(XSSFWorkbook wb, User u, RequestManager manager,
      MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme) libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
    XSSFSheet sheet = wb.getSheetAt(0);
    int rows = sheet.getPhysicalNumberOfRows();

    XSSFRow glrow = sheet.getRow(1);

    // process global headers
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
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
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
      ls = manager.getLibrarySelectionTypeByName(getCellValueAsString(selectionCell));
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
    } else {
      log.info("Got library selection type: " + ls.getName());
    }

    XSSFCell strategyCell = glrow.getCell(4);
    LibraryStrategyType lst = null;
    if (getCellValueAsString(strategyCell) != null) {
      lst = manager.getLibraryStrategyTypeByName(getCellValueAsString(strategyCell));
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) + "'");
    } else {
      log.info("Got library strategy type: " + lst.getName());
    }

    XSSFCell plateBarcodeCell = glrow.getCell(5);
    String plateBarcode = null;
    if (getCellValueAsString(plateBarcodeCell) != null) {
      plateBarcode = getCellValueAsString(plateBarcodeCell);
    }
    if (plateBarcode == null) {
      throw new InputFormException("Cannot resolve plate barcode from: '" + getCellValueAsString(plateBarcodeCell) + "'");
    } else {
      log.info("Got plate barcode: " + plateBarcode);
    }

    // process entries
    Simple384WellPlate libraryPlate = null;
    Map<String, PlatePool> pools = new HashMap<String, PlatePool>();
    for (int ri = 4; ri < rows; ri++) {
      XSSFRow row = sheet.getRow(ri);

      // Ax - plate position
      XSSFCell platePosCell = row.getCell(0);
      String platePos = getCellValueAsString(platePosCell);
      if (platePos != null && libraryPlate == null) {
        // plated libraries - process as plate
        libraryPlate = new Simple384WellPlate();
        libraryPlate.setIdentificationBarcode(plateBarcode);
        libraryPlate.setCreationDate(new Date());
      }

      // cell defs
      XSSFCell sampleAliasCell = row.getCell(2);

      Sample s = null;
      if (getCellValueAsString(sampleAliasCell) != null) {
        String salias = getCellValueAsString(sampleAliasCell);
        Collection<Sample> ss = manager.listSamplesByAlias(salias);
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
        XSSFCell entityIDCell = row.getCell(2);
        XSSFCell poolNumberCell = row.getCell(3);
        XSSFCell sampleQcCell = row.getCell(4);
        XSSFCell libraryDescriptionCell = row.getCell(7);
        XSSFCell barcodeKitCell = row.getCell(8);
        XSSFCell barcodeTagsCell = row.getCell(9);
        XSSFCell libraryQcCell = row.getCell(10);
        XSSFCell libraryQcInsertSizeCell = row.getCell(11);
        XSSFCell libraryQcMolarityCell = row.getCell(12);
        XSSFCell libraryQcPassFailCell = row.getCell(13);
        XSSFCell dilutionMolarityCell = row.getCell(17);
        XSSFCell poolQcCell = row.getCell(20);
        XSSFCell poolConvertedMolarityCell = row.getCell(22);

        // add pool, if any
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
            } else {
              pool.setConcentration(0.0);
            }
            pools.put(poolNum, pool);
            log.info("Added pool: " + poolNum);
            manager.savePool(pool);
          }
        }

        // process sample QC
        if (getCellValueAsString(sampleQcCell) != null) {
          try {
            SampleQC sqc = new SampleQCImpl();
            sqc.setSample(s);
            sqc.setResults(Double.valueOf(getCellValueAsString(sampleQcCell)));
            sqc.setQcCreator(u.getLoginName());
            sqc.setQcDate(new Date());
            if (manager.getSampleQcTypeByName("Picogreen") != null) {
              sqc.setQcType(manager.getSampleQcTypeByName("Picogreen"));
            } else {
              sqc.setQcType(manager.getSampleQcTypeByName("QuBit"));
            }
            if (!s.getSampleQCs().contains(sqc)) {
              s.addQc(sqc);
              manager.saveSampleQC(sqc);
              manager.saveSample(s);
              log.info("Added sample QC: " + sqc.toString());
            }
          } catch (NumberFormatException nfe) {
            throw new InputFormException(
                "Supplied Sample QC concentration for sample '" + getCellValueAsString(sampleAliasCell) + "' is invalid", nfe);
          }
        }

        if (getCellValueAsString(barcodeKitCell) != null && getCellValueAsString(barcodeTagsCell) != null) {
          // create library
          Library library = new LibraryImpl();
          library.setSample(s);

          Matcher mat = samplePattern.matcher(s.getAlias());
          if (mat.matches()) {
            String libAlias = plateBarcode + "_" + "L" + mat.group(2) + "-" + platePos + "_" + getCellValueAsString(entityIDCell);

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
                } else {
                  throw new InputFormException(
                      "Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid");
                }
              } catch (NumberFormatException nfe) {
                throw new InputFormException(
                    "Supplied Library insert size for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
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
                } else {
                  // TODO check libraryQcPassFailCell?
                  library.setQcPassed(true);
                }
              } catch (NumberFormatException nfe) {
                throw new InputFormException(
                    "Supplied Library QC concentration for library '" + libAlias + "' (" + s.getAlias() + ") is invalid", nfe);
              }
            }

            if (getCellValueAsString(barcodeKitCell) != null) {
              Collection<TagBarcode> bcs = manager.listAllTagBarcodesByStrategyName(getCellValueAsString(barcodeKitCell));
              if (!bcs.isEmpty()) {
                String tags = getCellValueAsString(barcodeTagsCell);
                if (!isStringEmptyOrNull(tags)) {
                  HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
                  if (tags.contains("-")) {
                    String[] splits = tags.split("-");
                    int count = 1;
                    for (String tag : splits) {
                      for (TagBarcode tb : bcs) {
                        if (tb.getName().equals(tag)) {
                          // set tag barcodes
                          tbs.put(count, tb);
                          count++;
                        }
                      }
                    }
                  } else {
                    for (TagBarcode tb : bcs) {
                      if (tb.getName().equals(tags) || tb.getSequence().equals(tags)) {
                        // set tag barcode
                        tbs.put(1, tb);
                        log.info("Got tag barcode: " + tb.getName());
                        break;
                      }
                    }
                  }

                  library.setTagBarcodes(tbs);
                } else {
                  throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '" + s.getAlias() + "'.");
                }
              } else {
                throw new InputFormException("No tag barcodes associated with the kit definition '" + getCellValueAsString(barcodeKitCell)
                    + "' for sample: '" + s.getAlias() + "'.");
              }

              if (getCellValueAsString(poolConvertedMolarityCell) != null) {
                String poolNum = getCellValueAsString(poolNumberCell);
                Pool<Plate<LinkedList<Library>, Library>> p = pools.get(poolNum);
                if (p != null) {
                  log.debug("Retrieved pool " + poolNum);
                  try {
                    p.setConcentration(Double.valueOf(getCellValueAsString(poolConvertedMolarityCell)));
                  } catch (NumberFormatException nfe) {
                    throw new InputFormException("Supplied pool concentration for pool '" + poolNum + "' is invalid", nfe);
                  }
                }
              }

              log.info("Added library: " + library.toString());
              manager.saveLibrary(library);

              if (getCellValueAsString(platePosCell) != null && libraryPlate != null) {
                // libraryPlate.setElement(getCellValueAsString(platePosCell), library);
                libraryPlate.addElement(library);
                log.info("Added library " + library.getAlias() + " to " + getCellValueAsString(platePosCell));
              }

              samples.add(s);

              Pool<Plate<LinkedList<Library>, Library>> p = pools.get(getCellValueAsString(poolNumberCell));
              if (p != null && !p.getPoolableElements().contains(libraryPlate)) {
                p.addPoolableElement(libraryPlate);
                log.info("Added plate to pool: " + p.toString());
              }
            } else {
              log.error("Cannot generate library alias from specified parent sample alias. Does it match the required schema?");
            }
          }
        }
      }
    }
    log.info("Done");
    return pools;
  }

  public static Map<String, PlatePool> importPlateInputSpreadsheet(File inPath, User u, RequestManager manager,
      MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      return process384PlateInputXLSX(wb, u, manager, libraryNamingScheme);
    } else if (inPath.getName().endsWith(".ods")) {
      OdfSpreadsheetDocument oDoc = (OdfSpreadsheetDocument) OdfDocument.loadDocument(inPath);
      return process384PlateInputODS(oDoc, u, manager, libraryNamingScheme);
    } else {
      throw new UnsupportedOperationException("Cannot process bulk input files other than xls, xlsx, and ods.");
    }
  }

  private static List<Sample> processSampleInputODS(OdfSpreadsheetDocument oDoc, User u, RequestManager manager,
      MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme) libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
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
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
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
      ls = manager.getLibrarySelectionTypeByName(selectionCell.getStringValue());
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + selectionCell.getStringValue() + "'");
    } else {
      log.info("Got library selection type: " + ls.getName());
    }

    OdfTableCell strategyCell = oTable.getCellByPosition("E2");
    LibraryStrategyType lst = null;
    if (!isStringEmptyOrNull(strategyCell.getStringValue())) {
      lst = manager.getLibraryStrategyTypeByName(strategyCell.getStringValue());
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + strategyCell.getStringValue() + "'");
    } else {
      log.info("Got library strategy type: " + lst.getName());
    }

    // process entries
    Plate<LinkedList<Sample>, Sample> samplePlate = null;
    Map<String, Pool<Dilution>> pools = new HashMap<String, Pool<Dilution>>();
    for (OdfTableRow row : oTable.getRowList()) {
      int ri = row.getRowIndex();
      if (ri > 3) {
        // Ax - plate position
        OdfTableCell platePosCell = oTable.getCellByPosition(0, ri);
        if (!isStringEmptyOrNull(platePosCell.getStringValue())) {
          // plated samples - process as plate
          samplePlate = new PlateImpl<Sample>();
        }

        // cell defs
        OdfTableCell sampleAliasCell = oTable.getCellByPosition(2, ri);

        Sample s = null;
        if (!isStringEmptyOrNull(sampleAliasCell.getStringValue())) {
          Collection<Sample> ss = manager.listSamplesByAlias(sampleAliasCell.getStringValue());
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
          String projectAliasCell = oTable.getCellByPosition(1, ri).getStringValue();
          String poolNumberCell = oTable.getCellByPosition(3, ri).getStringValue();
          String sampleQcCell = oTable.getCellByPosition(4, ri).getStringValue();
          String libraryDescriptionCell = oTable.getCellByPosition(7, ri).getStringValue();
          String barcodeKitCell = oTable.getCellByPosition(8, ri).getStringValue();
          String barcodeTagsCell = oTable.getCellByPosition(9, ri).getStringValue();
          String libraryQcCell = oTable.getCellByPosition(10, ri).getStringValue();
          String libraryQcInsertSizeCell = oTable.getCellByPosition(11, ri).getStringValue();
          String libraryQcMolarityCell = oTable.getCellByPosition(12, ri).getStringValue();
          String libraryQcPassFailCell = oTable.getCellByPosition(13, ri).getStringValue();
          String dilutionMolarityCell = oTable.getCellByPosition(17, ri).getStringValue();
          String poolQcCell = oTable.getCellByPosition(20, ri).getStringValue();
          String poolConvertedMolarityCell = oTable.getCellByPosition(22, ri).getStringValue();

          // add pool, if any
          processPool(poolNumberCell, poolConvertedMolarityCell, pools);
          processSampleQC(sampleQcCell, s, u, manager);

          Library library = processLibrary(libraryQcCell, libraryDescriptionCell, libraryQcPassFailCell, s, pt, lt, ls, lst, paired,
              libraryNamingScheme);
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

  private static List<Sample> processSampleInputXLSX(XSSFWorkbook wb, User u, RequestManager manager,
      MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    ((RequestManagerAwareNamingScheme) libraryNamingScheme).setRequestManager(manager);

    List<Sample> samples = new ArrayList<Sample>();
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
        lt = manager.getLibraryTypeByDescriptionAndPlatform(type, pt);
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
      ls = manager.getLibrarySelectionTypeByName(getCellValueAsString(selectionCell));
    }
    if (ls == null) {
      throw new InputFormException("Cannot resolve Library Selection type from: '" + getCellValueAsString(selectionCell) + "'");
    } else {
      log.info("Got library selection type: " + ls.getName());
    }

    XSSFCell strategyCell = glrow.getCell(4);
    LibraryStrategyType lst = null;
    if (getCellValueAsString(strategyCell) != null) {
      lst = manager.getLibraryStrategyTypeByName(getCellValueAsString(strategyCell));
    }
    if (lst == null) {
      throw new InputFormException("Cannot resolve Library Strategy type from: '" + getCellValueAsString(strategyCell) + "'");
    } else {
      log.info("Got library strategy type: " + lst.getName());
    }

    // process entries
    Plate<LinkedList<Sample>, Sample> samplePlate = null;
    Map<String, Pool<Dilution>> pools = new HashMap<String, Pool<Dilution>>();

    for (int ri = 4; ri < rows; ri++) {
      XSSFRow row = sheet.getRow(ri);

      // Ax - plate position
      XSSFCell platePosCell = row.getCell(0);
      if (getCellValueAsString(platePosCell) != null && samplePlate == null) {
        // plated samples - process as plate
        samplePlate = new PlateImpl<Sample>();
      }

      // cell defs
      XSSFCell sampleAliasCell = row.getCell(2);

      Sample s = null;
      if (getCellValueAsString(sampleAliasCell) != null) {
        String salias = getCellValueAsString(sampleAliasCell);
        Collection<Sample> ss = manager.listSamplesByAlias(salias);
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
        String projectAliasCell = getCellValueAsString(row.getCell(1));
        String poolNumberCell = getCellValueAsString(row.getCell(3));
        String sampleQcCell = getCellValueAsString(row.getCell(4));
        String libraryDescriptionCell = getCellValueAsString(row.getCell(7));
        String barcodeKitCell = getCellValueAsString(row.getCell(8));
        String barcodeTagsCell = getCellValueAsString(row.getCell(9));
        String libraryQcCell = getCellValueAsString(row.getCell(10));
        String libraryQcInsertSizeCell = getCellValueAsString(row.getCell(11));
        String libraryQcMolarityCell = getCellValueAsString(row.getCell(12));
        String libraryQcPassFailCell = getCellValueAsString(row.getCell(13));
        String dilutionMolarityCell = getCellValueAsString(row.getCell(17));
        String poolQcCell = getCellValueAsString(row.getCell(20));
        String poolConvertedMolarityCell = getCellValueAsString(row.getCell(22));

        // add pool, if any
        processPool(poolNumberCell, poolConvertedMolarityCell, pools);
        processSampleQC(sampleQcCell, s, u, manager);

        Library library = processLibrary(libraryQcCell, libraryDescriptionCell, libraryQcPassFailCell, s, pt, lt, ls, lst, paired,
            libraryNamingScheme);
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
    if (!isStringEmptyOrNull(poolAlias)) {
      if (!pools.containsKey(poolAlias)) {
        Pool<Dilution> pool = new PoolImpl<Dilution>();
        pool.setAlias(poolAlias);
        pools.put(poolAlias, pool);
        log.info("Added pool: " + poolAlias);
      }

      if (!isStringEmptyOrNull(poolConvertedMolarity)) {
        Pool<Dilution> p = pools.get(poolAlias);
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

  private static void processSampleQC(String sampleQc, Sample s, User u, RequestManager manager) throws Exception {
    // process sample QC
    if (!isStringEmptyOrNull(sampleQc)) {
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
      } catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied Sample QC concentration for sample '" + sampleQc + "' is invalid", nfe);
      }
    }
  }

  private static Library processLibrary(String libraryQc, String libraryDescription, String libraryQcPassFail, Sample s, PlatformType pt,
      LibraryType lt, LibrarySelectionType ls, LibraryStrategyType lst, boolean paired, MisoNamingScheme<Library> libraryNamingScheme)
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
      library.setPlatformName(pt.name());
      library.setLibraryType(lt);
      library.setLibrarySelectionType(ls);
      library.setLibraryStrategyType(lst);
      library.setPaired(paired);

      if (!isStringEmptyOrNull(libraryQcPassFail)) {
        library.setQcPassed(Boolean.parseBoolean(libraryQcPassFail));
      }

      String libAlias = libraryNamingScheme.generateNameFor("alias", library);

      library.setAlias(libAlias);

      return library;
    }
    return null;
  }

  private static void processLibraryQC(String libraryQc, String libraryQcMolarity, String libraryQcInsertSize, Library library, User u,
      RequestManager manager) throws Exception {
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
        } else {
          throw new InputFormException("No such Library QC type '" + libraryQc + "'");
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

  private static void processBarcodes(String barcodeKit, String barcodeTags, Library library, RequestManager manager) throws Exception {
    if (!isStringEmptyOrNull(barcodeKit)) {
      Collection<TagBarcode> bcs = manager.listAllTagBarcodesByStrategyName(barcodeKit);
      if (!bcs.isEmpty()) {
        if (!isStringEmptyOrNull(barcodeTags)) {
          HashMap<Integer, TagBarcode> tbs = new HashMap<Integer, TagBarcode>();
          if (barcodeTags.contains("-")) {
            String[] splits = barcodeTags.split("-");
            int count = 1;
            for (String tag : splits) {
              for (TagBarcode tb : bcs) {
                if (tb.getName().equals(tag)) {
                  // set tag barcodes
                  tbs.put(count, tb);
                  count++;
                }
              }
            }
          } else {
            for (TagBarcode tb : bcs) {
              if (tb.getName().equals(barcodeTags)) {
                // set tag barcode
                tbs.put(1, tb);
              }
            }
          }

          library.setTagBarcodes(tbs);
        } else {
          throw new InputFormException("Barcode Kit specified but no tag barcodes entered for: '" + library.getSample().getAlias() + "'.");
        }
      } else {
        throw new InputFormException("No tag barcodes associated with this kit definition: '" + library.getSample().getAlias() + "'.");
      }
    }
  }

  private static void processDilutions(String dilutionMolarity, Library library, Pool<Dilution> p, User u) throws Exception {
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
          p.addPoolableElement(ldi);
          log.info("Added library dilution to pool: " + p.toString());
        }
      } catch (NumberFormatException nfe) {
        throw new InputFormException("Supplied LibraryDilution concentration for library '" + library.getAlias() + "' ("
            + library.getSample().getAlias() + ") is invalid", nfe);
      }
    }
  }

  public static List<Sample> importSampleInputSpreadsheet(File inPath, User u, RequestManager manager,
      MisoNamingScheme<Library> libraryNamingScheme) throws Exception {
    if (inPath.getName().endsWith(".xlsx")) {
      XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inPath));
      return processSampleInputXLSX(wb, u, manager, libraryNamingScheme);
    } else if (inPath.getName().endsWith(".ods")) {
      OdfSpreadsheetDocument oDoc = (OdfSpreadsheetDocument) OdfDocument.loadDocument(inPath);
      return processSampleInputODS(oDoc, u, manager, libraryNamingScheme);
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
    Collections.sort(samples, new AliasComparator(Sample.class));

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

  public static void convertToPDF(OdfTextDocument oDoc) throws Exception {
    OutputStream out = new FileOutputStream(new File("/tmp/test-sample-form.pdf"));
    ODF2PDFViaITextConverter.getInstance().convert(oDoc, out, null);
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
      for (String item : (Iterable<String>) array) {
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
}
