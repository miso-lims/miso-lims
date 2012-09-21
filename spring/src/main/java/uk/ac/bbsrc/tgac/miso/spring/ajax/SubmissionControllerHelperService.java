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
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PartitionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubmissionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.manager.SubmissionManager;
import uk.ac.bbsrc.tgac.miso.core.service.submission.FilePathGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.submission.TGACIlluminaFilepathGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.submission.UploadReport;
import uk.ac.bbsrc.tgac.miso.core.service.submission.UploadJob;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;
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

  //Saves a new submission to the DB, or updates an existing submission, based on details sent vie AJAX from
  //editSubmission.jsp
  public JSONObject saveSubmission(HttpSession session, JSONObject json) {
    try {
      if (json.has("form") && !json.get("form").equals("")) {
        //creates a new submission object using the form info
        Submission newSubmission = new SubmissionImpl();
        //if editing an existing submission
        if (json.has("submissionId") && json.get("submissionId") != "" && !json.get("submissionId").equals(-1)) {
          //calls up the existing submission with this ID
          Submission oldSubmission = requestManager.getSubmissionById(Long.parseLong(json.getString("submissionId")));
          //sets the details of the new one to match the old.
          newSubmission.setId(oldSubmission.getId());
          newSubmission.setAccession(oldSubmission.getAccession());
          //newSubmission.setAlias(oldSubmission.getAlias());
          newSubmission.setCreationDate(oldSubmission.getCreationDate());
          //newSubmission.setDescription(oldSubmission.getDescription());
          newSubmission.setName(oldSubmission.getName());
          newSubmission.setSubmissionDate(oldSubmission.getSubmissionDate());
          //newSubmission.setTitle(oldSubmission.getTitle());
          newSubmission.setVerified(oldSubmission.isVerified());
          newSubmission.setCompleted(oldSubmission.isCompleted());
        }

        //sets the title, alias and description based on form contents
        JSONArray form = JSONArray.fromObject(json.get("form"));
        Set<SequencerPoolPartition> newPartitions = new HashSet<SequencerPoolPartition>();

        for (JSONObject j : (Iterable<JSONObject>) form) {
          if (j.getString("name").equals("title")) {
            newSubmission.setTitle(j.getString("value"));
          }
          if (j.getString("name").equals("alias")) {
            newSubmission.setAlias(j.getString("value"));
          }
          if (j.getString("name").equals("description")) {
            newSubmission.setDescription(j.getString("value"));
          }

          if (j.getString("name").contains("DIL")) {
            Long dilutionId = Long.parseLong(j.getString("name").replaceAll("\\D+", ""));
            Long partitionId = Long.parseLong(j.getString("value").replaceAll("\\D+", ""));
            //and a new Partition created from the ID
            PartitionImpl newPartition = new PartitionImpl();
            newPartition.setId(partitionId);
            //if the partition is not already in the set of newPartitions:
            if (newPartitions.add(newPartition)) {
              // a new pool is created
              Pool<Dilution> newPool = new PoolImpl<Dilution>();
              //details of the original partition's pool are copied to the new one
              Pool<? extends Poolable> oldPool = requestManager.getSequencerPoolPartitionById(partitionId).getPool();
              newPool.setExperiments(oldPool.getExperiments());
              newPool.setPlatformType(oldPool.getPlatformType());
              //the new pool is added to the partition
              newPartition.setPool(newPool);
            }

            for (SequencerPoolPartition nextPartition : newPartitions) {
              if (nextPartition.getId() == partitionId) {
                Dilution dilution = requestManager.getDilutionByIdAndPlatform(dilutionId, nextPartition.getPool().getPlatformType());
                Pool pool = nextPartition.getPool();
                pool.addPoolableElement(dilution);
              }
            }
          }
        }
        //the set of partitions is added to the new Submission
        for (SequencerPoolPartition sequencerPoolPartition : newPartitions) {
          newSubmission.addSubmissionElement(sequencerPoolPartition);
        }
        //the submission is saved
        requestManager.saveSubmission(newSubmission);
        return JSONUtils.SimpleJSONResponse("Submission " + newSubmission.getId() + " saved!");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }
    return JSONUtils.SimpleJSONResponse("saveSubmission called");
  }

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
          String s = "<pre>" + submissionManager.generateSubmissionMetadata(submission) + "</pre>";
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
        }
        catch (SubmissionException se) {
          return JSONUtils.SimpleJSONError(se.getMessage());
        }
        Document report = submission.submit(submissionManager);
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
          }
        }
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

  //creates the list of runs, partitions and submittable dilutions for a project, to be displayed on
  //editSubmission.jsp when the project is clicked. Also checks the boxes next to any dilutions
  //that are already in the submission.
  public JSONObject populateSubmissionProject(HttpSession session, JSONObject json) {
    try {
      //gets the submission from the database
      Submission sub = null;
      if (json.has("submissionId") && !json.get("submissionId").equals("")) {
        sub = requestManager.getSubmissionById(json.getLong("submissionId"));
      }
      //gets studies and experiments for the project that has been selected
      if (json.has("projectId") && !json.get("projectId").equals("")) {
        StringBuilder sb = new StringBuilder();
        Long projectId = json.getLong("projectId");
        Collection<LibraryDilution> projectDilutions = requestManager.listAllLibraryDilutionsByProjectId(projectId);

        Project p = requestManager.getProjectById(projectId);
        Collection<Study> studies = requestManager.listAllStudiesByProjectId(p.getProjectId());
        p.setStudies(studies);
        for (Study s : studies) {
          Collection<Experiment> experiments = requestManager.listAllExperimentsByStudyId(s.getId());
          s.setExperiments(experiments);
        }
        //gets the runs for the project
        List<Run> runs = new ArrayList<Run>(requestManager.listAllRunsByProjectId(projectId));
        Collections.sort(runs);

        //creates HTML list of runs
        if (runs.size() > 0) {
          sb.append("<ul id='runList" + projectId + "'>");
          for (Run r : runs) {
            sb.append("<li>");
            sb.append("<a href='/miso/run/" + r.getId() + "'><b>" + r.getName() + "</b> : " + r.getAlias() + "</a>");
            sb.append("<ul>");

            //creates HTML list of partition containers for each run
            Collection<SequencerPartitionContainer<SequencerPoolPartition>> partitionContainers = requestManager.listSequencerPartitionContainersByRunId(r.getId());
            for (SequencerPartitionContainer<SequencerPoolPartition> partitionContainer : partitionContainers) {
              sb.append("<li>");
              sb.append("<b>" + partitionContainer.getIdentificationBarcode() + "</b> : " + partitionContainer.getId());
              sb.append("<ul>");

              //creates HTML list of partitions for each partition container
              Collection<SequencerPoolPartition> partitions = partitionContainer.getPartitions();
              for (SequencerPoolPartition part : partitions) {

                // Checks whether the partition was involved in the project.
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
                    //If the partition was involved in the project, it is listed
                    sb.append("<li><input type='checkbox' id='" + r.getId() + "_" + partitionContainer.getId() + "_" + part.getPartitionNumber() + "' name='partition' " +
                              "itemLabel='" + part.getPartitionNumber() + "' itemValue='PAR" + part.getId() + "' value='PAR" + part.getId() + "'");

                    if (sub != null) {
                      // checks checkboxes if the partition is in the submission
                      if (sub != null && !sub.getSubmissionElements().isEmpty() && sub.getSubmissionElements().contains(part)) {
                        sb.append(" checked='checked' ");
                      }
                    }

                    sb.append("/>");
                    //adds the Partition info: number, name, experiments etc.
                    sb.append("<b>Partition " + part.getPartitionNumber() + "</b> : " + part.getPool().getName() + " (" + LimsUtils.join(involvedExperiments, ",") + ")");
                    sb.append("</li>");

                    //creates HTML for list of library dilutions and corresponding datafiles.
                    //gets all the dilutions in that partition's pool.
                    Collection<? extends Dilution> libraryDilutions = part.getPool().getDilutions();

                    //creates a filpathgenerator
                    FilePathGenerator fpg = new TGACIlluminaFilepathGenerator();
                    sb.append("<ul>");

                    //this is failing- it's ticking all the dilutions, regardless of whether they're in the submission. I reckon it's checking the wrong Partition somehow.
                    //Better method might be to create a list of dilutions in each submission partition, and check each dilution in the menu list against it.
                    for (Dilution d : libraryDilutions) {
                      sb.append("<li><input type='checkbox'  name='DIL_" + d.getId() + "' id='DIL" + d.getId() + "_PAR" + part.getId() + "' value='PAR_" + part.getId() + "' ");

                      if (sub != null && sub.getSubmissionElements().contains(part)) {
                        //checks dilution checkboxes if dilution is in the submission
                        for (Object o : sub.getSubmissionElements()) {
                          if (o.equals(part)) {
                            SequencerPoolPartition subPart = (SequencerPoolPartition) o;
                            //this is causing problems- it's returning dilutions from the original partition!
                            for (Dilution bla : subPart.getPool().getDilutions()) {
                              if (bla.getId() == d.getId()) {
                                sb.append(" checked='checked' ");
                              }
                            }
                          }
                        }
                      }
                      sb.append(">" + d.getLibrary().getName() + d.getName() + ": (" + fpg.generateFilePath(part, d).getName() + ")</li>");
                    }
                    sb.append("</ul>");
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
        else {
          sb.append("<ul id='runList" + projectId + "'>");
          sb.append("Project " + projectId + " contains no submittable elements.");
          sb.append("</ul>");
        }
        return JSONUtils.JSONObjectResponse("html", sb.toString());
      }
    }
    catch (SubmissionException e) {
      log.debug("Failed to generate path for data file in submission: ", e);
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to populate project for submission");
    }
    catch (IOException e) {
      log.debug("Failed to populate project for submission: ", e);
      e.printStackTrace();
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
