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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.google.json.JsonSanitizer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 * 
 * @author Xingdong Bian
 * @since 0.1.2
 */
@Ajaxified
public class FlexReportingControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(FlexReportingControllerHelperService.class);

  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private JdbcTemplate interfaceTemplate;

  public void setInterfaceTemplate(JdbcTemplate interfaceTemplate) {
    this.interfaceTemplate = interfaceTemplate;
  }

  public String flexHTMLTemplate(String content) {
    StringBuilder sb = new StringBuilder();
    // header
    sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n"
        + "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en-gb\">\n"
        + "<head>\n" + "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n"
        + "<meta http-equiv=\"Pragma\" content=\"no-cache\">\n" + "<meta http-equiv=\"Cache-Control\" content=\"no-cache\">\n"
        + "    <link rel=\"stylesheet\" href=\"/styles/style.css\" type=\"text/css\">\n" + "<title>MISO Report</title>"
        + "</head><body><table border=\"0\" width=\"100%\">\n" + "    <tr>\n" + "        <td class=\"headertable\" align=\"left\" \">\n"
        + "            <img src=\"/styles/images/miso_logo.png\" alt=\"MISO Logo\" name=\"logo\"\n"
        + "                                  border=\"0\" id=\"misologo\"/>\n" + "        </td>\n"
        + "        <td class=\"headertable\" align=\"right\" \">\n"
        + "            <img src=\"/styles/images/brand_logo.png\" alt=\"Brand Logo\" name=\"logo\"\n"
        + "                                  border=\"0\" id=\"brandlogo\"/>\n" + "        </td>\n" + "    </tr>\n" + "</table><hr/>");
    // end of header

    sb.append(content);

    // footer
    sb.append("</div>\n" + "<div id=\"footer\">\n" + "    <br/>\n" + "\n"
        + "    <p>&copy; 2010 - 2012 <a href=\"http://www.tgac.bbsrc.ac.uk/\" target=\"_blank\">The Genome Analysis Centre</a></p>\n"
        + "</div>\n" + "</body></html>");
    // end of footer

    return sb.toString();
  }

  public JSONObject initProjects(HttpSession session, JSONObject json) {
    try {
      JSONObject jsonObject = new JSONObject();
      StringBuilder a = new StringBuilder();
      JSONArray jsonArray = new JSONArray();
      Collection<Project> projects = requestManager.listAllProjects();
      for (Project project : projects) {
        jsonArray.add(projectRowBuilder(project));
      }
      for (String progress : ProgressType.getKeys()) {
        a.append("<option value=\"" + progress + "\">" + progress + "</option>");
      }
      jsonObject.put("html", jsonArray);
      jsonObject.put("progress", "<option value=\"all\">all</option>" + a.toString());
      return jsonObject;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject searchProjectsByCreationDateandString(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    String from = json.getString("from");
    String to = json.getString("to");
    String progress = json.getString("progress");
    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject = new JSONObject();
    try {

      Collection<Project> projects = null;
      if (!isStringEmptyOrNull(searchStr)) {
        projects = requestManager.listAllProjectsBySearch(searchStr);
      } else {
        projects = requestManager.listAllProjects();
      }

      for (Project project : projects) {
        if (progress.equals("all") || progress.equals(project.getProgress().getKey())) {

          if (!isStringEmptyOrNull(from) && !isStringEmptyOrNull(to)) {
            if (project.getCreationDate() != null) {

              DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
              Date startDate = df.parse(from);
              Date endDate = df.parse(to);
              Date creationDate = project.getCreationDate();

              if ((creationDate.after(startDate) && creationDate.before(endDate)) || creationDate.equals(startDate)
                  || creationDate.equals(endDate)) {
                jsonArray.add(projectRowBuilder(project));
              }
            }
          } else {
            jsonArray.add(projectRowBuilder(project));
          }
        }
      }

      jsonObject.put("html", jsonArray);
      return jsonObject;
    } catch (Exception e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public String projectRowBuilder(Project project) {
    return "['<input class=\"chkboxprojects\" id=\"" + project.getProjectId() + "\" type=\"checkbox\" name=\"projectIds\" value=\""
        + project.getProjectId() + "\" id=\"" + project.getProjectId() + "\"/>','" + project.getName() + "','" + project.getAlias() + "','"
        + project.getDescription() + "','" + project.getProgress().name() + "']";
  }

  public JSONObject generateProjectsFlexReport(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      JSONArray a = JSONArray.fromObject(json.get("form"));
      List<Project> projects = new ArrayList<Project>();
      JSONArray statusList = new JSONArray();

      Map<String, Integer> map = new HashMap<String, Integer>();
      JSONArray jsonArray = new JSONArray();

      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("name").equals("projectIds")) {
          Project p = requestManager.getProjectById(j.getLong("value"));
          if (p != null) {
            projects.add(p);

            int count = map.containsKey(p.getProgress().getKey()) ? map.get(p.getProgress().getKey()) : 0;
            count++;
            map.put(p.getProgress().getKey(), count);
          }
        }
      }

      for (String progress : ProgressType.getKeys()) {
        Integer no = map.containsKey(progress) ? map.get(progress) : 0;
        if (no > 0) {
          jsonArray.add("['" + progress + "'," + no + "]");
          statusList.add(JSONObject.fromObject("{'name': '" + progress + "','y':" + no + "}"));
        }
      }
      response.put("graph", statusList);
      response.put("overviewTable", jsonArray);
      response.put("reportTable", buildProjectReport(projects));
      response.put("detailTable", buildProjectDetailReport(projects));
      return response;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONArray buildProjectReport(List<Project> projects) {
    JSONArray jsonArray = new JSONArray();
    for (Project project : projects) {
      jsonArray.add(JsonSanitizer.sanitize("[\"" + (project.getName().replace("+", "-")) + "\",\"" + (project.getAlias().replace("+", "-"))
          + "\",\"" + (project.getDescription().replace("+", "-")) + "\",\"" + project.getProgress().name() + "\"]"));
    }
    return jsonArray;
  }

  public JSONArray buildProjectDetailReport(List<Project> projects) {
    JSONArray jsonArray = new JSONArray();
    try {
      for (Project project : projects) {
        Set<Library> librariesInRun = new HashSet<Library>();
        for (Run run : requestManager.listAllRunsByProjectId(project.getProjectId())) {
          Collection<SequencerPartitionContainer<SequencerPoolPartition>> spcs = requestManager
              .listSequencerPartitionContainersByRunId(run.getId());
          if (spcs.size() > 0) {
            for (SequencerPartitionContainer<SequencerPoolPartition> spc : spcs) {

              if (spc.getPartitions().size() > 0) {
                for (SequencerPoolPartition spp : spc.getPartitions()) {
                  if (spp.getPool() != null) {
                    if (spp.getPool().getDilutions().size() > 0) {
                      for (Dilution dilution : spp.getPool().getDilutions()) {
                        Library libraryInRun = dilution.getLibrary();
                        if (libraryInRun.getSample().getProject().equals(requestManager.getProjectById(project.getProjectId()))) {
                          if (librariesInRun.add(libraryInRun)) {

                            jsonArray.add(JsonSanitizer.sanitize("[\"" + project.getName() + "\",\"" + libraryInRun.getSample().getName()
                                + "\",\"" + libraryInRun.getName() + "\",\"" + spp.getPool().getName() + "\",\"" + run.getName() + "\",\""
                                + run.getStatus().getHealth().getKey() + "\"]"));
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }

        }
        for (Library library : requestManager.listAllLibrariesByProjectId(project.getProjectId())) {
          if (!librariesInRun.contains(library)) {
            Sample sample = library.getSample();
            jsonArray.add("['" + project.getName() + "','" + sample.getName() + "','" + library.getName() + "','NA','NA','NA']");
          }
        }

      }
      return jsonArray;
    } catch (IOException e) {
      log.debug("Failed", e);
      return jsonArray;
    }
  }

  public JSONObject searchProjectsByRunCompletionDateandString(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    String from = json.getString("from");
    String to = json.getString("to");

    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject = new JSONObject();
    try {

      Collection<Project> projects = null;
      if (!isStringEmptyOrNull(searchStr)) {
        projects = requestManager.listAllProjectsBySearch(searchStr);
      } else {
        projects = requestManager.listAllProjects();
      }

      for (Project project : projects) {
        Boolean projectBool = false;
        Collection<Run> runs = requestManager.listAllRunsByProjectId(project.getProjectId());
        for (Run run : runs) {

          if (!isStringEmptyOrNull(from) && !isStringEmptyOrNull(to)) {
            if (run.getStatus() != null && run.getStatus().getCompletionDate() != null) {

              DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
              Date startDate = df.parse(from);
              Date endDate = df.parse(to);
              Date runDate = run.getStatus().getCompletionDate();

              if ((runDate.after(startDate) && runDate.before(endDate)) || runDate.equals(startDate) || runDate.equals(endDate)) {
                projectBool = true;
              }
            }
          } else {
            projectBool = true;
          }

        }
        if (projectBool) {
          jsonArray.add(projectRunLaneRowBuilder(project));
        }
      }

      jsonObject.put("html", jsonArray);
      return jsonObject;
    } catch (Exception e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public String projectRunLaneRowBuilder(Project project) {
    StringBuilder sb = new StringBuilder();
    try {
      Collection<Run> runs = requestManager.listAllRunsByProjectId(project.getProjectId());
      if (runs.size() > 0) {
        sb.append("<ul>");
        for (Run run : runs) {
          sb.append("<li>");
          sb.append("<input class=\"runsinproject" + project.getProjectId() + "\" id=\"" + run.getName()
              + "\" type=\"checkbox\" name=\"runIds\" value=\"" + run.getId() + "\" />");
          sb.append(run.getName() + " - " + run.getStatus().getHealth().getKey() + " - " + run.getAlias());
          Collection<SequencerPartitionContainer<SequencerPoolPartition>> spcs = requestManager
              .listSequencerPartitionContainersByRunId(run.getId());
          if (spcs.size() > 0) {
            sb.append("<ul>");
            for (SequencerPartitionContainer<SequencerPoolPartition> spc : spcs) {

              if (spc.getPartitions().size() > 0) {
                for (SequencerPoolPartition spp : spc.getPartitions()) {
                  if (spp.getPool() != null) {
                    if (spp.getPool().getExperiments().size() > 0) {
                      for (Experiment experiment : spp.getPool().getExperiments()) {
                        if (experiment.getStudy().getProject().equals(project)) {

                          sb.append("<li>");
                          sb.append("Lane " + spp.getPartitionNumber() + ": ");
                          sb.append(" <b>" + spp.getPool().getName() + "</b> － " + spp.getPool().getAlias());
                          sb.append("</li>");

                        }
                      }
                    }
                  }
                }
              }
            }

            sb.append("</ul>");
          }
          sb.append("</li>");
        }
        sb.append("</ul>");
      }

      return "['<input id=\"" + project.getProjectId() + "\" type=\"radio\" name=\"projectId\" value=\"" + project.getProjectId()
          + "\" />','" + project.getName() + "','" + project.getAlias() + "','" + project.getProgress().name() + "','" + sb.toString()
          + "']";
    } catch (IOException e) {
      log.error("Failed", e);
      return "Failed: " + e.getMessage();
    }
  }

  public JSONObject generateSampleRelationReport(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      JSONArray a = JSONArray.fromObject(json.get("form"));
      Project p = null;
      List<Run> runs = new ArrayList<Run>();

      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("name").equals("projectId")) {
          p = requestManager.getProjectById(j.getLong("value"));
        }
        if (j.getString("name").equals("runIds")) {
          Run r = requestManager.getRunById(j.getLong("value"));
          if (r != null) {
            runs.add(r);
          }
        }
      }

      response.put("reportTable", buildSampleRelationReport(p, runs));
      return response;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONArray buildSampleRelationReport(Project project, List<Run> runs) {
    JSONArray jsonArray = new JSONArray();
    StringBuilder sb = new StringBuilder();
    try {

      Set<Library> librariesInRun = new HashSet<Library>();
      for (Run run : runs) {
        Collection<SequencerPartitionContainer<SequencerPoolPartition>> spcs = requestManager
            .listSequencerPartitionContainersByRunId(run.getId());
        if (spcs.size() > 0) {
          for (SequencerPartitionContainer<SequencerPoolPartition> spc : spcs) {

            if (spc.getPartitions().size() > 0) {
              for (SequencerPoolPartition spp : spc.getPartitions()) {
                if (spp.getPool() != null) {
                  if (spp.getPool().getDilutions().size() > 0) {
                    for (Dilution dilution : spp.getPool().getDilutions()) {
                      Library libraryInRun = dilution.getLibrary();
                      if (libraryInRun.getSample().getProject().equals(requestManager.getProjectById(project.getProjectId()))) {
                        if (librariesInRun.add(libraryInRun)) {
                          Sample sample = libraryInRun.getSample();

                          StringBuilder tagBarcode = new StringBuilder();
                          for (Map.Entry<Integer, TagBarcode> entry : libraryInRun.getTagBarcodes().entrySet()) {
                            tagBarcode.append(entry.getKey().toString() + ": " + entry.getValue().getName() + " ("
                                + entry.getValue().getSequence() + ")<br/>");
                          }

                          List list = new ArrayList(libraryInRun.getLibraryQCs());
                          LibraryQC libraryQc = (LibraryQC) list.get(list.size() - 1);

                          jsonArray.add(JsonSanitizer.sanitize("[\"" + sample.getAlias() + "\",\"" + sample.getDescription() + "\",\""
                              + sample.getSampleType() + "\",\"" + libraryInRun.getName() + "\",\"" + dilution.getName() + "\",\""
                              + tagBarcode.toString() + "\",\"" + libraryQc.getInsertSize().toString() + "\",\"" + run.getAlias() + "\",\""
                              + spp.getPartitionNumber().toString() + "\"]"));
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      return jsonArray;
    } catch (IOException e) {
      log.debug("Failed", e);
      return jsonArray;
    }
  }

  public JSONObject initSamples(HttpSession session, JSONObject json) {
    try {
      JSONObject jsonObject = new JSONObject();
      StringBuilder a = new StringBuilder();
      for (String sampleType : requestManager.listAllSampleTypes()) {
        a.append("<option value=\"" + sampleType + "\">" + sampleType + "</option>");
      }
      jsonObject.put("type", "<option value=\"all\">all</option>" + a.toString());
      return jsonObject;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public String sampleFormRowBuilder(Sample sample) {
    String qc = "unknown";
    if (sample.getQcPassed() != null) {
      qc = sample.getQcPassed().toString();
    }
    return "['<input class=\"chkboxsamples\" id=\"" + sample.getId() + "\" type=\"checkbox\" name=\"sampleIds\" value=\"" + sample.getId()
        + "\" id=\"" + sample.getId() + "\"/>','" + sample.getName() + "','" + sample.getAlias() + "','" + sample.getDescription() + "','"
        + sample.getSampleType() + "','" + qc + "']";
  }

  public JSONObject searchSamplesByCreationDateandString(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    String from = json.getString("from");
    String to = json.getString("to");
    String type = json.getString("type");
    String qc = json.getString("qc");
    JSONArray jsonArray = new JSONArray();
    JSONObject jsonObject = new JSONObject();
    try {
      Collection<Sample> samples = null;
      if (!isStringEmptyOrNull(searchStr)) {
        samples = requestManager.listAllSamplesBySearch(searchStr);
      } else {
        samples = requestManager.listAllSamples();
      }
      for (Sample sample : samples) {
        String sampleQC = "unknown";
        if (sample.getQcPassed() != null) {
          sampleQC = sample.getQcPassed().toString();
        }
        if ((type.equals("all") || type.equals(sample.getSampleType())) && (qc.equals("all") || qc.equals(sampleQC))) {

          if (!isStringEmptyOrNull(from) && !isStringEmptyOrNull(to)) {
            if (sample.getReceivedDate() != null) {

              DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
              Date startDate = df.parse(from);
              Date endDate = df.parse(to);
              Date receivedDate = sample.getReceivedDate();

              if ((receivedDate.after(startDate) && receivedDate.before(endDate)) || receivedDate.equals(startDate)
                  || receivedDate.equals(endDate)) {
                jsonArray.add(sampleFormRowBuilder(sample));
              }
            }
          } else {
            jsonArray.add(sampleFormRowBuilder(sample));
          }
        }
      }
      jsonObject.put("html", jsonArray);
      return jsonObject;
    } catch (Exception e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject generateSamplesFlexReport(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      JSONArray a = JSONArray.fromObject(json.get("form"));
      ArrayList<Sample> samples = new ArrayList<Sample>();

      Map<String, Integer> typeMap = new HashMap<String, Integer>();
      JSONArray overviewRelationArray = new JSONArray();
      JSONArray graphArray = new JSONArray();
      JSONArray qcGraphArray = new JSONArray();

      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("name").equals("sampleIds")) {
          Sample s = requestManager.getSampleById(j.getLong("value"));
          if (s != null) {
            samples.add(s);

            int count = typeMap.containsKey(s.getSampleType()) ? typeMap.get(s.getSampleType()) : 0;
            count++;
            typeMap.put(s.getSampleType(), count);

          }
        }
      }
      Integer totalCreated = 0;
      Integer totalReceived = 0;
      Integer totalQcPassed = 0;
      Integer totalQcFailed = 0;
      Integer totalQcUnknown = 0;

      for (String sampleType : requestManager.listAllSampleTypes()) {
        Integer no = typeMap.containsKey(sampleType) ? typeMap.get(sampleType) : 0;
        if (no > 0) {
          graphArray.add(JSONObject.fromObject("{'name': '" + sampleType + "','y':" + no + "}"));
          Integer received = 0;
          Integer qcpassed = 0;
          Integer qcfailed = 0;
          Integer qcunknown = 0;
          for (Sample s : samples) {
            if (s.getSampleType().equals(sampleType)) {
              if (s.getQcPassed() != null) {
                if (s.getQcPassed()) {
                  qcpassed++;
                } else {
                  qcfailed++;
                }
              } else {
                qcunknown++;
              }
              if (s.getReceivedDate() != null) {
                received++;
              }
            }
          }
          overviewRelationArray.add("['" + sampleType + "'," + no + "," + received + "," + qcpassed + "," + qcfailed + "]");
          totalCreated += no;
          totalReceived += received;
          totalQcPassed += qcpassed;
          totalQcFailed += qcfailed;
          totalQcUnknown += qcunknown;
        }
      }
      overviewRelationArray.add("['Total'," + totalCreated + "," + totalReceived + "," + totalQcPassed + "," + totalQcFailed + "]");

      qcGraphArray.add(JSONObject.fromObject("{'name': 'QC Passed ','y':" + totalQcPassed + "}"));
      qcGraphArray.add(JSONObject.fromObject("{'name': 'QC Not Passed ','y':" + totalQcFailed + "}"));
      qcGraphArray.add(JSONObject.fromObject("{'name': 'QC Unknown ','y':" + totalQcUnknown + "}"));

      response.put("overviewRelationTable", overviewRelationArray);
      response.put("graph", graphArray);
      response.put("qcgraph", qcGraphArray);
      response.put("reportTable", buildSampleReport(samples));
      return response;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONArray buildSampleReport(ArrayList<Sample> samples) {
    JSONArray jsonArray = new JSONArray();
    for (Sample sample : samples) {
      String qc = "unknown";
      if (sample.getQcPassed() != null) {
        qc = sample.getQcPassed().toString();
      }
      jsonArray.add("['" + (sample.getName().replace("+", "-")).replace("'", "\\'") + "','"
          + (sample.getAlias().replace("+", "-")).replace("'", "\\'") + "','"
          + (sample.getDescription().replace("+", "-")).replace("'", "\\'") + "','" + sample.getSampleType() + "','" + qc + "']");
    }
    return jsonArray;
  }

  // Starting Library

  public JSONObject initLibraries(HttpSession session, JSONObject json) {
    JSONObject jsonObject = new JSONObject();
    StringBuilder a = new StringBuilder();

    for (String platform : PlatformType.getKeys()) {
      a.append("<option value=\"" + platform + "\">" + platform + "</option>");
    }
    jsonObject.put("platform", "<option value=\"all\">all</option>" + a.toString());
    return jsonObject;
  }

  public String libraryFormRowBuilder(Library library) {
    String qc = "unknown";
    if (library.getQcPassed() != null) {
      qc = library.getQcPassed().toString();
    }
    return "['<input class=\"chkboxlibraries\" id=\"" + library.getId() + "\" type=\"checkbox\" name=\"libraryIds\" value=\""
        + library.getId() + "\" id=\"" + library.getId() + "\"/>','" + library.getName() + "','" + library.getAlias() + "','"
        + library.getDescription() + "','" + library.getPlatformName() + "','" + library.getLibraryType().getDescription() + "','" + qc
        + "']";
  }

  public JSONObject searchLibrariesByCreationDateandString(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    String from = json.getString("from");
    String to = json.getString("to");
    String platform = json.getString("platform");
    String qc = json.getString("qc");
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    try {
      Collection<Library> libraries = null;
      if (!isStringEmptyOrNull(searchStr)) {
        libraries = requestManager.listAllLibrariesBySearch(searchStr);
      } else {
        libraries = requestManager.listAllLibraries();
      }

      for (Library library : libraries) {
        if ((platform.equals("all") || platform.equals(library.getPlatformName()))
            && (qc.equals("all") || qc.equals(library.getQcPassed().toString()))) {

          if (!isStringEmptyOrNull(from) && !isStringEmptyOrNull(to) && library.getCreationDate() != null) {

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = df.parse(from);
            Date endDate = df.parse(to);
            Date receivedDate = library.getCreationDate();
            if ((receivedDate.after(startDate) && receivedDate.before(endDate)) || receivedDate.equals(startDate)
                || receivedDate.equals(endDate)) {
              jsonArray.add(libraryFormRowBuilder(library));
            }
          } else {
            jsonArray.add(libraryFormRowBuilder(library));
          }
        }
      }
      jsonObject.put("html", jsonArray);
      return jsonObject;
    } catch (Exception e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject generateLibrariesFlexReport(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      JSONArray a = JSONArray.fromObject(json.get("form"));
      ArrayList<Library> libraries = new ArrayList<Library>();

      Map<String, Integer> typeMap = new HashMap<String, Integer>();
      Map<String, Integer> platformMap = new HashMap<String, Integer>();
      JSONArray overviewRelationArray = new JSONArray();
      JSONArray graphArray = new JSONArray();
      JSONArray qcGraphArray = new JSONArray();
      JSONArray typeGraphArray = new JSONArray();

      Integer qcPassed = 0;
      Integer qcNotPassed = 0;

      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("name").equals("libraryIds")) {
          Library l = requestManager.getLibraryById(j.getLong("value"));
          if (l != null) {
            libraries.add(l);

            int count = typeMap.containsKey(l.getLibraryType().getDescription()) ? typeMap.get(l.getLibraryType().getDescription()) : 0;
            count++;
            typeMap.put(l.getLibraryType().getDescription(), count);

            int countPlatform = platformMap.containsKey(l.getPlatformName()) ? platformMap.get(l.getPlatformName()) : 0;
            countPlatform++;
            platformMap.put(l.getPlatformName(), countPlatform);
          }
        }
      }

      for (Map.Entry<String, Integer> entry : platformMap.entrySet()) {
        String platform = entry.getKey();
        Object no = entry.getValue();
        graphArray.add(JSONObject.fromObject("{'name': '" + platform + "','y':" + no + "}"));
      }

      for (Map.Entry<String, Integer> entry : typeMap.entrySet()) {
        String libraryType = entry.getKey();
        Object no = entry.getValue();
        typeGraphArray.add(JSONObject.fromObject("{'name': '" + libraryType + "','y':" + no + "}"));

        for (Map.Entry<String, Integer> platformEntry : platformMap.entrySet()) {
          String platform = platformEntry.getKey();
          Integer libqcpassed = 0;
          Integer libqcfailed = 0;
          for (Library l : libraries) {
            if (l.getLibraryType().getDescription().equals(libraryType) && l.getPlatformName().equals(platform)) {
              if (l.getQcPassed() != null) {
                if (l.getQcPassed()) {
                  libqcpassed++;
                }
              } else {
                libqcfailed++;
              }
            }
          }
          

          if (libqcpassed > 0 || libqcfailed > 0) {
            overviewRelationArray.add(
                "['" + libraryType + "','" + platform + "'," + libqcpassed + "," + libqcfailed + "," + (libqcpassed + libqcfailed) + "]");
            qcPassed += libqcpassed;
            qcNotPassed += libqcfailed;
          }
        }
      }

      overviewRelationArray.add("['Total',''," + qcPassed + "," + qcNotPassed + "," + (qcPassed + qcNotPassed) + "]");

      qcGraphArray.add(JSONObject.fromObject("{'name': 'QC Passed ','y':" + qcPassed + "}"));
      qcGraphArray.add(JSONObject.fromObject("{'name': 'QC Not Passed ','y':" + qcNotPassed + "}"));

      response.put("overviewRelationTable", overviewRelationArray);
      response.put("graph", graphArray);
      response.put("qcgraph", qcGraphArray);
      response.put("typegraph", typeGraphArray);
      response.put("reportTable", buildLibraryReport(libraries));
      response.put("relationQCTable", buildRelationQCTable(libraries));
      return response;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONArray buildLibraryReport(ArrayList<Library> libraries) {
    JSONArray jsonArray = new JSONArray();
    for (Library library : libraries) {
      String qc = "unknown";
      if (library.getQcPassed() != null) {
        qc = library.getQcPassed().toString();
      }
      jsonArray.add(JsonSanitizer.sanitize("[\"" + (library.getName().replace("+", "-")).replace("'", "\\'") + "\",\""
          + (library.getAlias().replace("+", "-")).replace("'", "\\'") + "\",\""
          + (library.getDescription().replace("+", "-")).replace("'", "\\'") + "\",\"" + library.getPlatformName() + "\",\""
          + library.getLibraryType().getDescription() + "\",\"" + qc + "\"]"));
    }
    return jsonArray;
  }

  public JSONArray buildRelationQCTable(ArrayList<Library> libraries) {
    JSONArray jsonArray = new JSONArray();
    for (Library library : libraries) {
      String qc = "unknown";
      String sampleQC = "unknown";
      if (library.getQcPassed() != null) {
        qc = library.getQcPassed().toString();
      }
      if (library.getSample().getQcPassed() != null) {
        sampleQC = library.getSample().getQcPassed().toString();
      }

      jsonArray.add("['" + library.getSample().getProject().getName() + "','" + library.getName() + "','" + library.getAlias() + "','"
          + library.getDescription() + "','" + library.getPlatformName() + "','" + library.getLibraryType().getDescription() + "','" + qc
          + "','" + LimsUtils.getDateAsString(library.getCreationDate()) + "','" + library.getSample().getName() + "','" + sampleQC + "']");
    }
    return jsonArray;
  }

  public JSONObject initRuns(HttpSession session, JSONObject json) {
    JSONObject jsonObject = new JSONObject();
    StringBuilder a = new StringBuilder();
    StringBuilder c = new StringBuilder();

    for (String platform : PlatformType.getKeys()) {
      a.append("<option value=\"" + platform + "\">" + platform + "</option>");
    }

    for (String healthString : HealthType.getKeys()) {
      c.append("<option value=\"" + healthString + "\">" + healthString + "</option>");
    }
    jsonObject.put("platform", "<option value=\"all\">all</option>" + a.toString());
    jsonObject.put("status", "<option value=\"all\">all</option>" + c.toString());
    return jsonObject;
  }

  public String runFormRowBuilder(Run run) {
    return "['<input class=\"chkboxruns\" id=\"" + run.getId() + "\" type=\"checkbox\" name=\"runIds\" value=\"" + run.getId() + "\" id=\""
        + run.getId() + "\"/>','" + run.getName() + "','" + run.getAlias() + "','"
        + (run.getStatus() != null && run.getStatus().getHealth() != null ? run.getStatus().getHealth().getKey() : "") + "','"
        + (run.getPlatformType() != null ? run.getPlatformType().getKey() : "") + "']";
  }

  public JSONObject searchRunsByCreationDateandString(HttpSession session, JSONObject json) {
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    String searchStr = json.getString("str");
    String from = json.getString("from");
    String to = json.getString("to");
    String runStartedFrom = json.getString("runStartedFrom");
    String runStartedTo = json.getString("runStartedTo");
    String platform = json.getString("platform");
    String status = json.getString("status");
    try {
      Collection<Run> runs = null;
      if (!isStringEmptyOrNull(searchStr)) {
        runs = requestManager.listAllRunsBySearch(searchStr);
      } else {
        runs = requestManager.listAllRuns();
      }

      for (Run run : runs) {
        if ((platform.equals("all") || platform.equals(run.getPlatformType().getKey()))
            && (status.equals("all") || (run.getStatus() != null && run.getStatus().getHealth() != null
                ? status.equals(run.getStatus().getHealth().getKey()) : true))) {

          if (!isStringEmptyOrNull(from) && !isStringEmptyOrNull(to)) {
            if (run.getStatus() != null && run.getStatus().getCompletionDate() != null) {

              DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
              Date startDate = df.parse(from);
              Date endDate = df.parse(to);
              Date receivedDate = run.getStatus().getCompletionDate();

              if ((receivedDate.after(startDate) && receivedDate.before(endDate)) || receivedDate.equals(startDate)
                  || receivedDate.equals(endDate)) {
                jsonArray.add(runFormRowBuilder(run));
              }
            }
          } else if (!isStringEmptyOrNull(runStartedFrom) && !isStringEmptyOrNull(runStartedTo)) {
            if (run.getStatus() != null && run.getStatus().getStartDate() != null) {

              DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
              Date startDate = df.parse(runStartedFrom);
              Date endDate = df.parse(runStartedTo);
              Date startedDate = run.getStatus().getStartDate();

              if ((startedDate.after(startDate) && startedDate.before(endDate)) || startedDate.equals(startDate)
                  || startedDate.equals(endDate)) {
                jsonArray.add(runFormRowBuilder(run));
              }
            }
          } else {
            jsonArray.add(runFormRowBuilder(run));
          }
        }
      }
      jsonObject.put("html", jsonArray);
      return jsonObject;
    } catch (Exception e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject generateRunsFlexReport(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      JSONArray a = JSONArray.fromObject(json.get("form"));
      ArrayList<Run> runs = new ArrayList<Run>();

      Map<String, Integer> platformTypeMap = new HashMap<String, Integer>();
      Map<String, Integer> statusMap = new HashMap<String, Integer>();
      JSONArray overviewArray = new JSONArray();
      JSONArray graphArray = new JSONArray();
      JSONArray platformGraphArray = new JSONArray();

      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("name").equals("runIds")) {
          Run run = requestManager.getRunById(j.getLong("value"));
          if (run != null) {
            runs.add(run);

            int count = platformTypeMap.containsKey(run.getPlatformType().getKey()) ? platformTypeMap.get(run.getPlatformType().getKey())
                : 0;
            count++;
            platformTypeMap.put(run.getPlatformType().getKey(), count);

            if (run.getStatus() != null && run.getStatus().getHealth() != null) {
              int countQC = statusMap.containsKey(run.getStatus().getHealth().getKey())
                  ? statusMap.get(run.getStatus().getHealth().getKey()) : 0;
              countQC++;
              statusMap.put(run.getStatus().getHealth().getKey(), countQC);
            }
          }
        }
      }

      for (String platformString : PlatformType.getKeys()) {
        Integer no = platformTypeMap.containsKey(platformString) ? platformTypeMap.get(platformString) : 0;
        if (no > 0) {
          platformGraphArray.add(JSONObject.fromObject("{'name': '" + platformString + "','y':" + no + "}"));
          overviewArray.add("['Platform Type: " + platformString + "'," + no + "]");
        }
      }

      for (String healthString : HealthType.getKeys()) {
        Integer no = statusMap.containsKey(healthString) ? statusMap.get(healthString) : 0;
        if (no > 0) {
          graphArray.add(JSONObject.fromObject("{'name': '" + healthString + "','y':" + no + "}"));
          overviewArray.add("['Run Status: " + healthString + "'," + no + "]");
        }
      }

      response.put("overviewTable", overviewArray);
      response.put("graph", graphArray);
      response.put("platformgraph", platformGraphArray);
      response.put("reportTable", buildRunReport(runs));
      response.put("runsPartitionReport", buildRunPartitionReport(runs));
      return response;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONArray buildRunReport(ArrayList<Run> runs) {
    JSONArray jsonArray = new JSONArray();
    for (Run run : runs) {
      jsonArray.add(JsonSanitizer.sanitize("[\"" + (run.getName().replace("+", "-")) + "\",\"" + (run.getAlias().replace("+", "-"))
          + "\",\"" + (run.getStatus() != null && run.getStatus().getHealth() != null ? run.getStatus().getHealth().getKey() : "") + "\",\""
          + run.getPlatformType().getKey() + "\"]"));
    }
    return jsonArray;
  }

  public JSONArray buildRunPartitionReport(ArrayList<Run> runs) {
    JSONArray jsonArray = new JSONArray();
    try {
      for (Run run : runs) {
        Collection<SequencerPartitionContainer<SequencerPoolPartition>> spcs = requestManager
            .listSequencerPartitionContainersByRunId(run.getId());
        if (spcs.size() > 0) {
          for (SequencerPartitionContainer<SequencerPoolPartition> spc : spcs) {

            if (spc.getPartitions().size() > 0) {
              for (SequencerPoolPartition spp : spc.getPartitions()) {
                if (spp.getPool() != null) {
                  Pool pool = spp.getPool();
                  if (spp.getPool().getDilutions().size() > 0) {
                    Map<String, Integer> projectMap = new HashMap<String, Integer>();
                    for (Dilution dilution : spp.getPool().getDilutions()) {
                      int count = projectMap.containsKey(dilution.getLibrary().getSample().getProject().getName())
                          ? projectMap.get(dilution.getLibrary().getSample().getProject().getName()) : 0;
                      count++;
                      projectMap.put(dilution.getLibrary().getSample().getProject().getName(), count);
                    }
                    Map<String, Integer> projectMapDisplayed = new HashMap<String, Integer>();
                    for (Dilution dilution : spp.getPool().getDilutions()) {
                      if (!projectMapDisplayed.containsKey(dilution.getLibrary().getSample().getProject().getName())) {
                        jsonArray.add(JsonSanitizer.sanitize("[\"" + run.getName() + "\",\"" + (run.getAlias().replace("+", "-")) + "\",\""
                            + (run.getStatus() != null ? LimsUtils.getDateAsString(run.getStatus().getStartDate()) : "") + "\",\""
                            + pool.getName() + "\",\"" + spp.getPartitionNumber() + "\",\""
                            + dilution.getLibrary().getSample().getProject().getName() + "\",\""
                            + projectMap.get(dilution.getLibrary().getSample().getProject().getName()) + "\",\""
                            + spp.getPool().getDilutions().size() + "\"]"));
                        projectMapDisplayed.put(dilution.getLibrary().getSample().getProject().getName(), 1);
                      }
                    }
                  }
                }
              }
            }
          }
        }

      }
      return jsonArray;
    } catch (IOException e) {
      log.debug("Failed", e);
      return jsonArray;
    }
  }

  public JSONObject d3graphRest(Long projectId) throws IOException {
    try {
      Project p = requestManager.getProjectById(projectId);
      JSONObject projectJSON = new JSONObject();
      projectJSON.put("name", p.getName());
      projectJSON.put("description", p.getAlias());
      JSONArray projectChildrenArray = new JSONArray();
      Collection<Sample> samples = requestManager.listAllSamplesByProjectId(p.getProjectId());
      Collection<Run> runs = requestManager.listAllRunsByProjectId(p.getProjectId());
      Collection<Study> studies = requestManager.listAllStudiesByProjectId(p.getProjectId());

      JSONObject runJSON = new JSONObject();
      JSONArray runsArray = new JSONArray();

      runJSON.put("name", "Runs");
      runJSON.put("description", "");
      for (Run run : runs) {
        if (run.getStatus() != null && run.getStatus().getHealth() != null && run.getStatus().getHealth().getKey().equals("Completed")) {
          runsArray.add(JSONObject.fromObject("{'name': '" + run.getName() + "','description':'" + run.getAlias() + "','color': '1'}"));
        } else {
          runsArray.add(JSONObject.fromObject("{'name': '" + run.getName() + "','description':'" + run.getAlias() + "','color': '0'}"));
        }
      }
      runJSON.put("children", runsArray);
      if (runsArray.size() > 0) {
        projectChildrenArray.add(runJSON);
      }

      JSONObject studyJSON = new JSONObject();
      JSONArray studiesArray = new JSONArray();

      studyJSON.put("name", "Studies");
      studyJSON.put("description", "");
      for (Study study : studies) {
        JSONObject substudyJSON = new JSONObject();
        JSONArray substudiesArray = new JSONArray();
        substudyJSON.put("name", study.getName());
        substudyJSON.put("description", study.getAlias());
        Collection<Experiment> experiments = requestManager.listAllExperimentsByStudyId(study.getId());
        if (experiments.size() > 0) {
          JSONObject experimentJSON = new JSONObject();
          JSONArray experimentsArray = new JSONArray();
          experimentJSON.put("name", "experiment");
          experimentJSON.put("description", "");
          for (Experiment e : experiments) {
            experimentsArray
                .add(JSONObject.fromObject("{'name': '" + e.getName() + "','description':'" + e.getAlias() + "','color': '2'}"));
          }
          experimentJSON.put("children", experimentsArray);
          substudiesArray.add(experimentJSON);
        }
        if (substudiesArray.size() > 0) {
          substudyJSON.put("children", substudiesArray);
        }
        studiesArray.add(substudyJSON);
      }
      studyJSON.put("children", studiesArray);

      if (studiesArray.size() > 0) {
        projectChildrenArray.add(studyJSON);
      }

      JSONObject sampleJSON = new JSONObject();
      JSONArray samplesArray = new JSONArray();

      sampleJSON.put("name", "Samples");
      sampleJSON.put("description", "");
      for (Sample sample : samples) {
        Collection<Library> libraries = requestManager.listAllLibrariesBySampleId(sample.getId());
        if (libraries.size() == 0) {
          if (sample.getQcPassed()) {
            samplesArray
                .add(JSONObject.fromObject("{'name': '" + sample.getName() + "','description':'" + sample.getAlias() + "','color': '1'}"));
          } else {
            samplesArray
                .add(JSONObject.fromObject("{'name': '" + sample.getName() + "','description':'" + sample.getAlias() + "','color': '0'}"));
          }
        } else {
          JSONObject libraryJSON = new JSONObject();
          JSONArray librariesArray = new JSONArray();

          libraryJSON.put("name", "Libraries");

          for (Library library : libraries) {
            if (library.getLibraryQCs().size() > 0) {
              librariesArray.add(
                  JSONObject.fromObject("{'name': '" + library.getName() + "','description':'" + library.getAlias() + "','color': '1'}"));
            } else {
              librariesArray.add(
                  JSONObject.fromObject("{'name': '" + library.getName() + "','description':'" + library.getAlias() + "','color': '0'}"));
            }
          }
          libraryJSON.put("children", librariesArray);

          JSONObject subsampleJSON = new JSONObject();
          subsampleJSON.put("name", sample.getName());
          subsampleJSON.put("description", sample.getAlias());
          subsampleJSON.put("children", librariesArray);
          samplesArray.add(subsampleJSON);
        }
      }
      sampleJSON.put("children", samplesArray);
      if (samplesArray.size() > 0) {
        projectChildrenArray.add(sampleJSON);
      }

      projectJSON.put("children", projectChildrenArray);
      return projectJSON;
    } catch (IOException e) {
      log.error("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

}
