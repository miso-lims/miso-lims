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

package uk.ac.bbsrc.tgac.miso.core.data.decorator.itext;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Reportable;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.AbstractReportDecorator;
import uk.ac.bbsrc.tgac.miso.core.exception.ReportingException;

/**
 * Decorates a Project so that an iText report can be built from it
 * 
 * @author Rob Davey
 * @date 12-Oct-2010
 * @since 0.0.2
 */
public class ITextProjectDecorator extends AbstractReportDecorator<Document> {
  protected static final Logger log = LoggerFactory.getLogger(ITextProjectDecorator.class);

  protected OutputStream stream;

  public ITextProjectDecorator(List<? extends Reportable> reportables, Document report, OutputStream stream) {
    super(reportables);
    this.stream = stream;
    this.report = report;
  }

  @Override
  public void buildReport() throws ReportingException {
    if (reportables.size() == 1) {
      List reportableslist = new ArrayList<Reportable>(reportables);
      Reportable reportable = (Reportable) reportableslist.get(0);
      reportable.buildReport();
      Project project = (Project) reportable;
      try {
        report = new Document();
        PdfWriter writer = PdfWriter.getInstance(report, stream);
        report.open();
        report.add(new Paragraph("Project Summary"));
        PdfContentByte cb = writer.getDirectContent();
        cb.setLineWidth(2.0f); // Make a bit thicker than 1.0 default
        cb.setGrayStroke(0.9f); // 1 = black, 0 = white
        float x = 72f;
        float y = 200f;
        cb.moveTo(x, y);
        cb.lineTo(x + 72f * 6, y);
        cb.stroke();

        report.add(new Paragraph(project.getAlias()));
        report.add(new Paragraph(project.getDescription()));

        PdfPTable t = new PdfPTable(1);
        t.setHorizontalAlignment(Element.ALIGN_CENTER);
        t.setWidthPercentage(100f); // this would be the 100 from setHorizontalLine
        t.setSpacingAfter(5f);
        t.setSpacingBefore(0f);
        t.getDefaultCell().setUseVariableBorders(true);
        t.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        t.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        t.getDefaultCell().setBorder(Rectangle.BOTTOM); // This generates the line
        t.getDefaultCell().setBorderWidth(1f); // this would be the 1 from setHorizontalLine
        t.getDefaultCell().setPadding(0);
        t.addCell("");
        report.add(t);

        x = 72f;
        y = 100f;
        cb.moveTo(x, y);
        cb.lineTo(x + 72f * 6, y);
        cb.stroke();

        if (project.getSamples().size() > 0) {
          report.add(new Paragraph("Samples"));
          for (Sample sample : project.getSamples()) {
            Paragraph sPara = new Paragraph(sample.getAlias(), FontFactory.getFont("Helvetica", 12, Font.BOLD));
            sPara.setIndentationLeft(20);
            report.add(sPara);
            report.add(new Paragraph(sample.getDescription()));
          }
        }

        report.close();
      } catch (DocumentException e) {
        log.error("build report", e);
        throw new ReportingException(e.getMessage());
      }
    } else if (reportables.size() > 1) {
    } else {

    }
  }

}
