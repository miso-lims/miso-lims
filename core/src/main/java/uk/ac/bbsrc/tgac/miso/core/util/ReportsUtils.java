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

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.util.Rotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Reportable;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.exception.ReportingException;

/**
 * Utility class to provide helpful utils for generating reports
 * 
 * @author Xingdong Bian
 * @author Rob Davey
 * @since 0.0.2
 */
public class ReportsUtils {
  protected static final Logger log = LoggerFactory.getLogger(ReportsUtils.class);

  public static String buildHTMLReport(List<? extends Reportable> reportables, String type, List<String> options)
      throws ReportingException {
    Random generator = new Random();
    StringBuilder sb = new StringBuilder();
    Boolean chartbool = false;
    if (options.contains("Chart")) {
      chartbool = true;
    }

    // header
    sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"
        + "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en-gb\">\n"
        + "<head>\n" + "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n"
        + "<meta http-equiv=\"Pragma\" content=\"no-cache\">\n" + "<meta http-equiv=\"Cache-Control\" content=\"no-cache\">\n"
        + "<meta http-equiv=\"Expires\" content=\"Sat, 01 Dec 2001 00:00:00 GMT\">\n"
        + "    <link rel=\"stylesheet\" href=\"/styles/style.css\" type=\"text/css\">\n" + "<title>MISO Report</title>"
        + "</head><body onLoad=\"self.focus()\"><table border=\"0\" width=\"100%\">\n" + "    <tr>\n"
        + "        <td class=\"headertable\" align=\"left\" \">\n"
        + "            <img src=\"/styles/images/miso_logo.png\" alt=\"MISO Logo\" name=\"logo\"\n"
        + "                                  border=\"0\" id=\"misologo\"/>\n" + "        </td>\n"
        + "        <td class=\"headertable\" align=\"right\" \">\n"
        + "            <img src=\"/styles/images/tgac_new_logo.png\" alt=\"TGAC Logo\" name=\"logo\"\n"
        + "                                  border=\"0\" id=\"tgaclogo\"/>\n" + "        </td>\n" + "    </tr>\n" + "</table><hr/>");
    // end of header

    if (type.equals("Project")) {
      Boolean incOverview = false;
      Boolean incSamples = false;
      if (options.contains("Overview")) {
        incOverview = true;
      }
      if (options.contains("Samples")) {
        incSamples = true;
      }

      if (reportables.size() == 1) {
        // Single Project report
        List reportableslist = new ArrayList<Reportable>(reportables);
        Reportable reportable = (Reportable) reportableslist.get(0);
        Project project = (Project) reportable;
        sb.append("<h1>Project Information</h1><br/><div class='report'>");
        sb.append("<b>Project Name: </b> " + project.getName());
        sb.append("<br/><br/>");
        sb.append("<b>Project Alias: </b> " + project.getAlias());
        sb.append("<br/><br/>");
        sb.append("<b>Project Description: </b> " + project.getDescription());
        sb.append("<br/><br/>");
        sb.append("<b>Progress: </b> " + project.getProgress().name());
        sb.append("<br/><br/>");

        if (project.getOverviews().size() > 0 && incOverview) {
          sb.append("<table class=\"list\">\n" + "            <thead>\n" + "            <tr>\n"
              + "                <th>Principal Investigator</th>\n" + "                <th>Start Date</th>\n"
              + "                <th>End Date</th>\n" + "                <th>Proposed Samples</th>\n"
              + "                <th>QC Passed Sample</th>\n" + "            </tr>\n" + "            </thead>\n" + "            <tbody>");
          for (ProjectOverview overview : project.getOverviews()) {
            sb.append("<tr>\n" + "                    <td>" + overview.getPrincipalInvestigator() + "</td>\n" + "                    <td>"
                + overview.getStartDate() + "</td>\n" + "                    <td>" + overview.getEndDate() + "</td>\n"
                + "                    <td>" + overview.getNumProposedSamples().toString() + "</td>\n" + "                    <td>"
                + overview.getQcPassedSamples().size() + "</td>\n" + "                </tr>");
          }
          sb.append("</tbody>\n" + "        </table>");
        } else {
          if (incOverview) {
            sb.append("<b>Overview:</b> None.");
          }
        }
        sb.append("<br/>");
        if (project.getSamples().size() > 0 && incSamples) {
          sb.append("<table class=\"list\">\n" + "            <thead>\n" + "            <tr>\n" + "                <th>Sample Name</th>\n"
              + "                <th>Sample Alias</th>\n" + "                <th>Type</th>\n" + "                <th>QC Passed</th>\n"
              + "            </tr>\n" + "            </thead>\n" + "            <tbody>");
          for (Sample sample : project.getSamples()) {
            sb.append("<tr>\n" + "                    <td><b>" + sample.getName() + "</b></td>\n" + "                    <td>"
                + sample.getAlias() + "</td>\n" + "                    <td>" + sample.getSampleType() + "</td>\n"
                + "                    <td>" + sample.getQcPassed().toString() + "</td>\n" + "                </tr>");
          }
          sb.append("</tbody>\n" + "        </table>");
        } else {
          if (incSamples) {
            sb.append("<b>Samples:</b> None.");
          }
        }

      }
      // Lists of Projects report
      else if (reportables.size() > 1) {
        sb.append("<h1>Projects Information</h1><br/><div class='report'>");
        Map<String, Integer> map = new HashMap<String, Integer>();
        sb.append("<table class=\"list\">\n" + "    <thead>\n" + "    <tr>\n" + "        <th>Project Name</th>\n"
            + "        <th>Project Alias</th>\n" + "        <th>Project Description</th>\n" + "        <th>Progress</th>\n");
        if (incOverview) {
          sb.append("        <th>Overviews</th>\n");
        }
        if (incSamples) {
          sb.append("        <th>Samples</th>\n");
        }
        sb.append("    </tr>\n" + "    </thead>\n" + "    <tbody>");
        for (Reportable reportable : reportables) {
          Project project = (Project) reportable;
          sb.append("<tr><td>" + project.getName());
          sb.append("</td>");
          sb.append("<td> " + project.getAlias());
          sb.append("</td>");
          sb.append("<td> " + project.getDescription());
          sb.append("</td>");
          sb.append("<td> " + project.getProgress().name());
          sb.append("</td>");
          if (incOverview) {
            sb.append("<td>");
            if (project.getOverviews().size() > 0) {
              sb.append("<ul class='bullets'>");
              for (ProjectOverview overview : project.getOverviews()) {
                sb.append("<li>Principal Investigator: " + overview.getPrincipalInvestigator() + "<br/> Start Date: "
                    + overview.getStartDate() + "<br/> End Date: " + overview.getEndDate() + "<br/> Proposed Samples: "
                    + overview.getNumProposedSamples().toString() + "<br/> QC Passed Samples: " + overview.getQcPassedSamples().size()
                    + "</li>");
              }
              sb.append("</ul>");
            } else {
              sb.append("None");
            }
            sb.append("</td>");
          }
          if (incSamples) {
            sb.append("<td>");
            if (project.getSamples().size() > 0) {
              sb.append("<ul class='bullets'>");
              for (Sample sample : project.getSamples()) {
                sb.append("<li>" + sample.getAlias() + "</li>");
              }
              sb.append("</ul>");
            } else {
              sb.append("None");
            }
            sb.append("</td>");
          }
          sb.append("</tr>");
          int count = map.containsKey(project.getProgress().getKey()) ? map.get(project.getProgress().getKey()) : 0;
          count++;
          map.put(project.getProgress().getKey(), count);
        }
        sb.append("    </tbody>\n" + "</table>");
        Integer unknown = map.containsKey("Unknown") ? map.get("Unknown") : 0;
        Integer active = map.containsKey("Active") ? map.get("Active") : 0;
        Integer inactive = map.containsKey("Inactive") ? map.get("Inactive") : 0;
        Integer cancelled = map.containsKey("Cancelled") ? map.get("Cancelled") : 0;
        Integer proposed = map.containsKey("Proposed") ? map.get("Proposed") : 0;
        Integer pending = map.containsKey("Pending") ? map.get("Pending") : 0;
        Integer approved = map.containsKey("Approved") ? map.get("Approved") : 0;
        Integer completed = map.containsKey("Completed") ? map.get("Completed") : 0;
        // jfreechart
        final DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Unknown " + unknown, unknown);
        data.setValue("Active " + active, active);
        data.setValue("Inactive " + inactive, inactive);
        data.setValue("Cancelled " + cancelled, cancelled);
        data.setValue("Proposed " + proposed, proposed);
        data.setValue("Pending " + pending, pending);
        data.setValue("Approved " + approved, approved);
        data.setValue("Completed " + completed, completed);

        final JFreeChart chart = ChartFactory.createPieChart("Projects Status Information", // chart title
            data, // data
            false, // include legend
            true, false);

        final PiePlot plot = (PiePlot) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setBackgroundPaint(Color.white);
        plot.setForegroundAlpha(0.5f);
        plot.setNoDataMessage("No data to display");
        int r = generator.nextInt();
        File projectchart = new File("../webapps/ROOT/styles/images/projectschart" + r + ".png");
        try {
          ChartUtilities.saveChartAsPNG(projectchart, chart, 500, 300);
        } catch (IOException e) {
          log.error("build HTML report", e);
        }
        if (chartbool) {
          sb.append("<br/><img src='/styles/images/projectschart" + r + ".png'/>");
        }
      } else {
        sb.append("<h1>Project Information</h1><br/><div class='report'>Nothing to Report.");
      }

    } else if (type.equals("Sample")) {
      Boolean incQC = false;
      if (options.contains("QC")) {
        incQC = true;
      }

      if (reportables.size() == 1) {
        // Single Sample report
        List reportableslist = new ArrayList<Reportable>(reportables);
        Reportable reportable = (Reportable) reportableslist.get(0);
        Sample sample = (Sample) reportable;
        sb.append("<h1>Sample Information</h1><br/><div class='report'>");
        sb.append("<b>Sample Name: </b> " + sample.getName());
        sb.append("<br/><br/>");
        sb.append("<b>Project Alias: </b> " + sample.getProject().getAlias());
        sb.append("<br/><br/>");
        sb.append("<b>Sample Alias: </b> " + sample.getAlias());
        sb.append("<br/><br/>");
        sb.append("<b>Sample Description: </b> " + sample.getDescription());
        sb.append("<br/><br/>");
        sb.append("<b>Scientific Name: </b> " + sample.getScientificName());
        sb.append("<br/><br/>");
        sb.append("<b>Sample Type: </b> " + sample.getSampleType());
        sb.append("<br/><br/>");
        sb.append("<b>QC Passed: </b> " + sample.getQcPassed().toString());
        sb.append("<br/><br/>");

        if (sample.getSampleQCs().size() > 0 && incQC) {
          sb.append("<table class=\"list\">\n" + "            <thead>\n" + "            <tr>\n" + "                <th>QCed By</th>\n"
              + "                <th>QC Date</th>\n" + "                <th>Method</th>\n" + "                <th>Results</th>\n"
              + "            </tr>\n" + "            </thead>\n" + "            <tbody>");
          for (SampleQC sampleQC : sample.getSampleQCs()) {
            sb.append("<tr>\n" + "                    <td>" + sampleQC.getQcCreator() + "</td>\n" + "                    <td>"
                + LimsUtils.getDateAsString(sampleQC.getQcDate()) + "</td>\n" + "                    <td>" + sampleQC.getQcType().getName()
                + "</td>\n" + "                    <td>" + sampleQC.getResults().toString() + "ng/&#181;l</td>\n"
                + "                </tr>");
          }
          sb.append("</tbody>\n" + "        </table>");
        } else {
          if (incQC) {
            sb.append("<b>QC:</b> None.");
          }
        }

      }
      // Lists of Samples report
      else if (reportables.size() > 1) {
        sb.append("<h1>Samples Information</h1><br/><div class='report'>");
        Map<String, Integer> typeMap = new HashMap<String, Integer>();
        Map<String, Integer> qcMap = new HashMap<String, Integer>();
        sb.append("<table class=\"list\">\n" + "    <thead>\n" + "    <tr>\n" + "        <th>Sample Name</th>\n"
            + "        <th>Project Alias</th>\n" + "        <th>Sample Alias</th>\n" + "        <th>Sample Description</th>\n"
            + "        <th>Scientific Name</th>\n" + "        <th>Sample Type</th>\n" + "        <th>QC Passed</th>\n");
        if (incQC) {
          sb.append("        <th>QC</th>\n");
        }
        sb.append("    </tr>\n" + "    </thead>\n" + "    <tbody>");
        for (Reportable reportable : reportables) {
          Sample sample = (Sample) reportable;
          sb.append("<tr><td>" + sample.getName());
          sb.append("</td>");
          sb.append("<td>" + sample.getProject().getAlias());
          sb.append("</td>");
          sb.append("<td> " + sample.getAlias());
          sb.append("</td>");
          sb.append("<td> " + sample.getDescription());
          sb.append("</td>");
          sb.append("<td> " + sample.getScientificName());
          sb.append("</td>");
          sb.append("<td> " + sample.getSampleType());
          sb.append("</td>");
          sb.append("<td> " + sample.getQcPassed().toString());
          sb.append("</td>");
          if (incQC) {
            sb.append("<td>");
            if (sample.getSampleQCs().size() > 0) {
              sb.append("<ul class='bullets'>");
              for (SampleQC sampleQC : sample.getSampleQCs()) {
                sb.append("<li>\n" + "                    QCed By: " + sampleQC.getQcCreator() + "<br/>\n" + "                    QC Date: "
                    + LimsUtils.getDateAsString(sampleQC.getQcDate()) + "<br/>\n" + "                    Method: "
                    + sampleQC.getQcType().getName() + "<br/>\n" + "                    Results: " + sampleQC.getResults().toString()
                    + "ng/&#181;l</li>");
              }
              sb.append("</ul>");
            } else {
              sb.append("None");
            }
            sb.append("</td>");
          }
          sb.append("</tr>");

          int typecount = typeMap.containsKey(sample.getSampleType()) ? typeMap.get(sample.getSampleType()) : 0;
          typecount++;
          typeMap.put(sample.getSampleType(), typecount);

          int qccount = qcMap.containsKey(sample.getQcPassed().toString()) ? qcMap.get(sample.getQcPassed().toString()) : 0;
          qccount++;
          qcMap.put(sample.getQcPassed().toString(), qccount);
        }
        sb.append("    </tbody>\n" + "</table>");
        Integer nonGENOMIC = typeMap.containsKey("NON GENOMIC") ? typeMap.get("NON GENOMIC") : 0;
        Integer genomic = typeMap.containsKey("GENOMIC") ? typeMap.get("GENOMIC") : 0;
        Integer other = typeMap.containsKey("OTHER") ? typeMap.get("OTHER") : 0;
        Integer viralRNA = typeMap.containsKey("VIRAL RNA") ? typeMap.get("VIRAL RNA") : 0;
        Integer synthetic = typeMap.containsKey("SYNTHETIC") ? typeMap.get("SYNTHETIC") : 0;

        // jfreechart for type
        final DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("NON GENOMIC " + nonGENOMIC, nonGENOMIC);
        data.setValue("GENOMIC " + genomic, genomic);
        data.setValue("OTHER " + other, other);
        data.setValue("VIRAL RNA " + viralRNA, viralRNA);
        data.setValue("SYNTHETIC " + synthetic, synthetic);

        final JFreeChart chart = ChartFactory.createPieChart("Samples Type Information", // chart title
            data, // data
            false, // include legend
            true, false);

        final PiePlot plot = (PiePlot) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.5f);
        plot.setBackgroundPaint(Color.white);
        plot.setNoDataMessage("No data to display");
        int r = generator.nextInt();
        File samplechart = new File("../webapps/ROOT/styles/images/samplestypechart" + r + ".png");
        try {
          ChartUtilities.saveChartAsPNG(samplechart, chart, 500, 300);
        } catch (IOException e) {
          log.error("save chart as PNG", e);
        }

        if (chartbool) {
          sb.append("<br/><img src='/styles/images/samplestypechart" + r + ".png'/>");
        }
        Integer qcpassed = qcMap.containsKey("true") ? qcMap.get("true") : 0;
        Integer qcnotpassed = qcMap.containsKey("false") ? qcMap.get("false") : 0;

        // jfreechart for qc
        final DefaultPieDataset qcdata = new DefaultPieDataset();
        qcdata.setValue("QC Passed " + qcpassed, qcpassed);
        qcdata.setValue("QC Not Passed " + qcnotpassed, qcnotpassed);

        final JFreeChart qcchart = ChartFactory.createPieChart("Samples QC Information", // chart title
            qcdata, // data
            false, // include legend
            true, false);

        final PiePlot qcplot = (PiePlot) qcchart.getPlot();
        qcplot.setStartAngle(290);
        qcplot.setDirection(Rotation.CLOCKWISE);
        qcplot.setForegroundAlpha(0.5f);
        qcplot.setBackgroundPaint(Color.white);
        qcplot.setNoDataMessage("No data to display");
        File sampleqcchart = new File("../webapps/ROOT/styles/images/samplesqcchart" + r + ".png");
        try {
          ChartUtilities.saveChartAsPNG(sampleqcchart, qcchart, 500, 300);
        } catch (IOException e) {
          log.error("save chart as PNG", e);
        }
        if (chartbool) {
          sb.append(" <img src='/styles/images/samplesqcchart" + r + ".png'/>");
        }
      } else {
        sb.append("<h1>Sample Information</h1><br/><div class='report'>Nothing to Report.");
      }

    } else if (type.equals("Run")) {
      Boolean incAlias = false;
      Boolean incDescription = false;
      if (options.contains("Alias")) {
        incAlias = true;
      }
      if (options.contains("Description")) {
        incDescription = true;
      }

      if (reportables.size() == 1) {
        // Single Run report
        List reportableslist = new ArrayList<Reportable>(reportables);
        Reportable reportable = (Reportable) reportableslist.get(0);
        Run run = (Run) reportable;
        sb.append("<h1>Run Information</h1><br/><div class='report'>");
        sb.append("<b>Run Name: </b> " + run.getName());
        sb.append("<br/><br/>");
        sb.append("<b>Platform: </b> " + run.getPlatformType().getKey());
        sb.append("<br/><br/>");
        sb.append("<b>Sequencer: </b> " + run.getSequencerReference().getName());
        sb.append("<br/><br/>");
        if (incAlias) {
          sb.append("<b>Run Alias: </b> " + run.getAlias());
          sb.append("<br/><br/>");
        }
        if (incDescription) {
          sb.append("<b>Run Description: </b> " + run.getDescription());
          sb.append("<br/><br/>");
        }
        sb.append("<b>Pair End: </b> " + run.getPairedEnd().toString());
        sb.append("<br/><br/>");
        sb.append("<b>Status: </b> " + run.getStatus().getHealth().getKey());
        sb.append("<br/><br/>");

      }
      // Lists of Runs report
      else if (reportables.size() > 1) {
        sb.append("<h1>Runs Information</h1><br/><div class='report'>");
        Map<String, Integer> statusMap = new HashMap<String, Integer>();
        Map<String, Integer> platformMap = new HashMap<String, Integer>();
        sb.append("<table class=\"list\">\n" + "    <thead>\n" + "    <tr>\n" + "        <th>Run Name</th>\n"
            + "        <th>Platform</th>\n" + "        <th>Sequencer</th>\n");
        if (incAlias) {
          sb.append("        <th>Run Alias</th>\n");
        }
        if (incDescription) {
          sb.append("        <th>Run Description</th>\n");
        }
        sb.append("        <th>Pair End</th>\n" + "        <th>Status</th>\n" + "    </tr>\n" + "    </thead>\n" + "    <tbody>");
        for (Reportable reportable : reportables) {
          Run run = (Run) reportable;
          sb.append("<tr><td>" + run.getName());
          sb.append("</td>");
          sb.append("<td> " + run.getPlatformType().getKey());
          sb.append("</td>");
          sb.append("<td> " + run.getSequencerReference().getName());
          sb.append("</td>");
          if (incAlias) {
            sb.append("<td> " + run.getAlias());
            sb.append("</td>");
          }
          if (incDescription) {
            sb.append("<td> " + run.getDescription());
            sb.append("</td>");
          }
          sb.append("<td> " + run.getPairedEnd().toString());
          sb.append("</td>");
          sb.append("<td> " + run.getStatus().getHealth().getKey());
          sb.append("</td></tr>");

          int statuscount = statusMap.containsKey(run.getStatus().getHealth().getKey())
              ? statusMap.get(run.getStatus().getHealth().getKey()) : 0;
          statuscount++;
          statusMap.put(run.getStatus().getHealth().getKey(), statuscount);

          int platformcount = platformMap.containsKey(run.getPlatformType().getKey()) ? platformMap.get(run.getPlatformType().getKey()) : 0;
          platformcount++;
          platformMap.put(run.getPlatformType().getKey(), platformcount);
        }
        sb.append("    </tbody>\n" + "</table>");
        Integer unknown = statusMap.containsKey("Unknown") ? statusMap.get("Unknown") : 0;
        Integer completed = statusMap.containsKey("Completed") ? statusMap.get("Completed") : 0;
        Integer failed = statusMap.containsKey("Failed") ? statusMap.get("Failed") : 0;
        Integer started = statusMap.containsKey("Started") ? statusMap.get("Started") : 0;
        Integer stopped = statusMap.containsKey("Stopped") ? statusMap.get("Stopped") : 0;
        Integer running = statusMap.containsKey("Running") ? statusMap.get("Running") : 0;

        // jfreechart for status
        final DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("Unknown " + unknown, unknown);
        data.setValue("Completed " + completed, completed);
        data.setValue("Failed " + failed, failed);
        data.setValue("Started " + started, started);
        data.setValue("Stopped " + stopped, stopped);
        data.setValue("Running " + running, running);

        final JFreeChart chart = ChartFactory.createPieChart("Run Status Information", // chart title
            data, // data
            false, // include legend
            true, false);

        final PiePlot plot = (PiePlot) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setBackgroundPaint(Color.white);
        plot.setForegroundAlpha(0.5f);
        plot.setNoDataMessage("No data to display");
        int r = generator.nextInt();
        File runchart = new File("../webapps/ROOT/styles/images/runsstatuschart" + r + ".png");
        try {
          ChartUtilities.saveChartAsPNG(runchart, chart, 500, 300);
        } catch (IOException e) {
          log.error("save chart as PNG", e);
        }

        if (chartbool) {
          sb.append("<br/><img src='/styles/images/runsstatuschart" + r + ".png'/>");
        }
      } else {
        sb.append("<h1>Run Information</h1><br/><div class='report'>Nothing to Report.");
      }

    }

    // footer
    sb.append("</div>\n" + "<div id=\"footer\">\n" + "    <br/>\n" + "\n"
        + "    <p>&copy; 2010 -2011 <a href=\"http://www.tgac.bbsrc.ac.uk/\" target=\"_blank\">The Genome Analysis Centre</a></p>\n"
        + "</div>\n" + "</body></html>");
    // end of footer
    return sb.toString();

  }

}
