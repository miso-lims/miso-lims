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

import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.manager.SubmissionManager;
import uk.ac.bbsrc.tgac.miso.core.service.submission.UploadReport;
import uk.ac.bbsrc.tgac.miso.core.service.submission.UploadJob;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class SubmissionControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(SubmissionControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private SubmissionManager submissionManager;

  public JSONObject previewSubmissionMetadata(HttpSession session, JSONObject json) {
    try {

      if (json.has("submissionId") && !json.get("submissionId").equals("")) {
        Long submissionId = ((Integer) json.get("submissionId")).longValue();
        Submission<Submittable, Document, Document> submission = requestManager.getSubmissionById(submissionId);

        SubmissionActionType action = SubmissionActionType.VALIDATE;
        if (json.has("operation")) {
          action = SubmissionActionType.valueOf(json.getString("operation"));
        }

        submission.setSubmissionActionType(action);

        try {
          submissionManager.generateSubmissionMetadata(submission);
          String s = submissionManager.prettifySubmissionMetadata(submission);
          return JSONUtils.JSONObjectResponse("metadata", s);
        }
        catch (SubmissionException se) {
          return JSONUtils.SimpleJSONError(se.getMessage());
        }
      }
    }
    catch (Exception e) {
      log.debug("Failed to get submission metadata: ", e);
      return JSONUtils.SimpleJSONError("Failed to get submission metadata");
    }
    return JSONUtils.SimpleJSONError("Cannot build submission metadata");
  }

  /*
 generates SubmissionMetadata from the submission Object, sets the action to 'validate'
 submits metadata to SRA and parses feedback
  */
  public JSONObject validateSubmissionMetadata(HttpSession session, JSONObject json) {
    try {
      if (json.has("submissionId") && !json.get("submissionId").equals("")) {

        Long submissionId = ((Integer) json.get("submissionId")).longValue();


        Submission<Submittable, Document, Document> submission = requestManager.getSubmissionById(submissionId);

        SubmissionActionType action = SubmissionActionType.VALIDATE;
        if (json.has("operation")) {

          action = SubmissionActionType.valueOf(json.getString("operation"));
        }

        submission.setSubmissionActionType(action);
        try {


          String s = submissionManager.generateSubmissionMetadata(submission);
          //return JSONUtils.JSONObjectResponse("metadata", s);
        }
        catch (SubmissionException se) {

          return JSONUtils.SimpleJSONError(se.getMessage());
        }

        Document report = (Document) submission.submit(submissionManager);

        if (report != null) {
          Map<String, Object> responseMap = (Map<String, Object>) submissionManager.parseResponse(report);
          return JSONUtils.JSONObjectResponse(responseMap);
        }
        else {
          return JSONUtils.SimpleJSONError("Failed to get submission report. Something went wrong in the submission process");
        }

      }
    }
    catch (Exception e) {
      log.debug("Failed to get submission metadata: ", e);
      return JSONUtils.SimpleJSONError("Failed to get submission metadata");
    }
    return JSONUtils.SimpleJSONError("Cannot build submission metadata");
  }

  public JSONObject submitSubmissionMetadata(HttpSession session, JSONObject json) {
    try {
      if (json.has("submissionId") && !json.get("submissionId").equals("")) {
        Long submissionId = ((Integer) json.get("submissionId")).longValue();
        Submission submission = requestManager.getSubmissionById(submissionId);

        //if no action is specified, validate. otherwise, set the action type.
        SubmissionActionType action = SubmissionActionType.VALIDATE;
        if (json.has("operation")) {
          action = SubmissionActionType.valueOf(json.getString("operation"));
        }

        submission.setSubmissionActionType(action);
        Document report = (Document) submission.submit(submissionManager);

        if (report != null) {
          Map<String, Object> responseMap = (Map<String, Object>) submissionManager.parseResponse(report);
          return JSONUtils.JSONObjectResponse(responseMap);
        }
        else {
          return JSONUtils.SimpleJSONError("Failed to get submission report. Something went wrong in the submission process");
        }
      }
    }
    catch (Exception e) {
      log.debug("Failed to get submission metadata: ", e);
      return JSONUtils.SimpleJSONError("Failed to get submission metadata");
    }
    return JSONUtils.SimpleJSONError("Cannot build submission metadata");
  }

  public JSONObject submitSequenceData(HttpSession session, JSONObject json) {
    try {

      if (json.has("submissionId") && !json.get("submissionId").equals("")) {
        Long submissionId = ((Integer) json.get("submissionId")).longValue();
        Submission<Submittable, Document, Document> submission = requestManager.getSubmissionById(submissionId);
        String response = submissionManager.submitSequenceData(submission);

        return (JSONUtils.SimpleJSONResponse(response));


      }
      else {
        return JSONUtils.SimpleJSONError("Failed to get submission report. Something went wrong in the sequence Data submission process");
      }
    }
    catch (Exception e) {
      log.debug("Failed to submit sequence data: ", e);
      return JSONUtils.SimpleJSONError("Failed to submit sequence data");
    }
  }

  public JSONObject checkUploadProgress(HttpSession session, JSONObject json) {
    try {
      if (json.has("submissionId") && !json.get("submissionId").equals("")/*&& !json.get("submissionId").equals(null)*/) {
        Long submissionId = json.getLong("submissionId");
        Map<String, Object> responseMap;
        JSONObject report = new JSONObject();

        JSONArray jsonUploadJobs = new JSONArray();
        //TODO - get upload report
        UploadReport uploadReport = submissionManager.getUploadProgress(submissionId);
        if (uploadReport != null) {
          List<UploadJob> uploads = uploadReport.getUploadJobs();

          for (UploadJob u : uploads) {
            JSONObject jsonUpload = new JSONObject();
            jsonUpload.put("filename", u.getFile().getName());
            jsonUpload.put("percent", u.getPercentageTransferred());
            jsonUploadJobs.add(jsonUpload);
          }

          report.put("status", uploadReport.getStatus());
          report.put("message", uploadReport.getMessage());
          report.put("uploadJobs", jsonUploadJobs);
          return report;
        }
        else {
          return JSONUtils.SimpleJSONResponse("sorry- no upload report has been returned!");
        }
      }
    }
    catch (Exception e) {
      log.debug("Failed to get upload progress ", e);
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to get upload report" + e.getMessage());
    }
    log.debug("Failed to get upload progress ");
    return JSONUtils.SimpleJSONError("Failed to get upload progress");
  }

  public JSONObject openSubmissionProjectNodes(HttpSession session, JSONObject json) {
    try {
      if (json.has("submissionId") && !json.get("submissionId").equals("") && !json.get("submissionId").equals(null)) {
        Long submissionId = json.getLong("submissionId");
        Map<String, Object> responseMap = new HashMap<String, Object>();

        //TODO - get projects from submission
        Submission sub = requestManager.getSubmissionById(submissionId);
        Set<Long> projectIds = new HashSet<Long>();
        for (Object o : sub.getSubmissionElements()) {
          if (o instanceof Project) {
            projectIds.add(((Project) o).getProjectId());
          }

          if (o instanceof Study) {
            projectIds.add(((Study) o).getProject().getProjectId());
          }

          if (o instanceof Experiment) {
            projectIds.add(((Experiment) o).getStudy().getProject().getProjectId());
          }

          if (o instanceof Sample) {
            projectIds.add(((Sample) o).getProject().getProjectId());
            /*
            if (o instanceof Partition){
                projectIds.add(((Partition) o).getFlowcell().getRun().get)
              */

          }
        }
        //checks submission ID

        //converts the list of projectIds to a JSONArray
        responseMap.put("projects", JSONArray.fromObject(projectIds));
        return JSONUtils.JSONObjectResponse(responseMap);
      }
    }
    catch (Exception e) {
      log.debug("Failed to open project nodes for submission: ", e);
      return JSONUtils.SimpleJSONError("Failed to open project nodes for submission");
    }
    return JSONUtils.SimpleJSONError("Cannot open project nodes for submission");
  }

  public JSONObject populateSubmissionProject(HttpSession session, JSONObject json) {
    try {
      Submission sub = null;
      if (json.has("submissionId") && !json.get("submissionId").equals("")) {
        sub = requestManager.getSubmissionById(json.getLong("submissionId"));
      }

      if (json.has("projectId") && !json.get("projectId").equals("")) {
        StringBuilder sb = new StringBuilder();
        Long projectId = json.getLong("projectId");
        Project p = requestManager.getProjectById(projectId);
        Collection<Study> studies = requestManager.listAllStudiesByProjectId(p.getProjectId());
        p.setStudies(studies);
        for (Study s : studies) {
          Collection<Experiment> experiments = requestManager.listAllExperimentsByStudyId(s.getStudyId());
          s.setExperiments(experiments);
        }

        List<Run> runs = new ArrayList<Run>(requestManager.listAllRunsByProjectId(projectId));
        Collections.sort(runs);
        if (runs.size() > 0) {
          sb.append("<ul id='runList" + projectId + "'>");
          for (Run r : runs) {
//            sb.append("<li><input type='checkbox' id='"+projectId+"_"+r.getRunId()+"' name='submissionElements' " +
//                      "itemLabel='"+r.getName()+"' itemValue='"+r.getName()+"' value='"+r.getName()+"'/>");
            sb.append("<li>");
            sb.append("<a href='/miso/run/" + r.getRunId() + "'><b>" + r.getName() + "</b> : " + r.getAlias() + "</a>");

            sb.append("<ul>");
            Collection<SequencerPartitionContainer<SequencerPoolPartition>> flowcells = requestManager.listSequencerPartitionContainersByRunId(r.getRunId());
            for (SequencerPartitionContainer<SequencerPoolPartition> f : flowcells) {
              sb.append("<li>");
              sb.append("<b>" + f.getIdentificationBarcode() + "</b> : " + f.getContainerId());

              sb.append("<ul>");
              Collection<SequencerPoolPartition> partitions = f.getPartitions();
              for (SequencerPoolPartition part : partitions) {
                boolean partitionInvolved = false;
                if (part.getPool() != null) {
                  Collection<Experiment> exps = part.getPool().getExperiments();
                  List<String> involvedExperiments = new ArrayList<String>();
                  for (Experiment e : exps) {
                    if (e.getStudy().getProject().getProjectId().equals(p.getProjectId())) {
                      involvedExperiments.add(e.getStudy().getProject().getAlias());
                      partitionInvolved = true;
                    }
                  }

                  if (partitionInvolved) {
                    sb.append("<li><input type='checkbox' id='" + r.getRunId() + "_" + f.getContainerId() + "_" + part.getPartitionNumber() + "' name='submissionElements' " +
                              "itemLabel='" + part.getPartitionNumber() + "' itemValue='PAR" + part.getId() + "' value='PAR" + part.getId() + "'");

                    if (sub != null && !sub.getSubmissionElements().isEmpty() && sub.getSubmissionElements().contains(part)) {
                      sb.append(" checked='checked' ");
                    }
                    sb.append("/>");

                    sb.append("<b>Partition " + part.getPartitionNumber() + "</b> : " + part.getPool().getName() + " (" + LimsUtils.join(involvedExperiments, ",") + ")");
                    sb.append("</li>");
                  }
                }
              }
              sb.append("</ul>");
              sb.append("</li>");
            }
            sb.append("</ul>");
            sb.append("</li>");
          }
          sb.append("</ul>");
        }
        return JSONUtils.JSONObjectResponse("html", sb.toString());
      }
    }
    catch (Exception e) {
      log.debug("Failed to populate project for submission: ", e);
      return JSONUtils.SimpleJSONError("Failed to populate project for submission");
    }
    return JSONUtils.SimpleJSONError("Cannot populate project for submission");
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setSubmissionManager(SubmissionManager submissionManager) {
    this.submissionManager = submissionManager;
  }
}
