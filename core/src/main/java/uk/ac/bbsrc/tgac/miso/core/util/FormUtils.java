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
import org.odftoolkit.odfdom.converter.itext.ODF2PDFViaITextConverter;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextParagraph;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.exception.DeliveryFormException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
