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

package uk.ac.bbsrc.tgac.miso.webapp.controller.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONObject;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.RunProcessingUtils;
import uk.ac.bbsrc.tgac.miso.integration.AnalysisQueryService;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;
import uk.ac.ebi.fgpt.conan.model.ConanPipeline;
import uk.ac.ebi.fgpt.conan.model.ConanTask;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.3
 */
@Controller
// @SessionAttributes("tasks")
public class AnalysisController {
  protected static final Logger log = LoggerFactory.getLogger(AnalysisController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private AnalysisQueryService queryService;

  public AnalysisQueryService getQueryService() {
    return queryService;
  }

  public void setQueryService(AnalysisQueryService queryService) {
    this.queryService = queryService;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "/analysis", method = RequestMethod.GET)
  public ModelAndView view(ModelMap model) throws IOException {
    return new ModelAndView("/pages/listAnalysisTasks.jsp", model);
  }

  @RequestMapping(value = "/analysis/new/run/{runId}", method = RequestMethod.GET)
  public ModelAndView runTask(@PathVariable long runId, ModelMap model) throws IOException, IntegrationException {
    Run run = requestManager.getRunById(runId);
    if (run == null) {
      throw new SecurityException("No such Run.");
    } else {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      model.put("run", run);

      Map<String, String> map = new HashMap<String, String>();
      map.put("RunAccession", run.getAlias());
      map.put("basecall-path", run.getFilePath() + "/Data/Intensities/BaseCalls");

      map.put("fastq-path", "/net/tgac-labdata-nfs/ifs/TGAC/NGS_data/qc/" + run.getAlias() + "/PAP");
      map.put("makefile-path", "/net/tgac-labdata-nfs/ifs/TGAC/NGS_data/qc/" + run.getAlias() + "/PAP/Makefile");
      map.put("sample-sheet-path", "/net/tgac-labdata-nfs/ifs/TGAC/NGS_data/qc/" + run.getAlias() + "/SampleSheet-PAP.csv");

      map.put("instrument-id", run.getSequencerReference().getName());

      SequencerPartitionContainer<SequencerPoolPartition> f = ((RunImpl) run).getSequencerPartitionContainers().get(0);
      String laneValue = "8";
      String naType = "dna";
      String indexValue = "6";

      if (f != null && f.getPartitions().size() != 0) {
        laneValue = String.valueOf(f.getPartitions().size());
        Pool<? extends Poolable> p = f.getPartitionAt(1).getPool();
        if (p != null) {
          if (!p.getPoolableElements().isEmpty()) {
            Poolable pable = p.getPoolableElements().iterator().next();
            if (pable instanceof Dilution) {
              Library l = ((Dilution) pable).getLibrary();
              if ("RNA-Seq".equals(l.getLibraryStrategyType().getName())) naType = "rna";
              for (TagBarcode tb : l.getTagBarcodes().values()) {
                indexValue = Integer.toString(tb.getSequence().length());
              }
            }
          }
        } else {
          throw new IntegrationException("Cannot start analysis pipelines on a run with no pools on any lanes.");
        }
      }

      String instrumentModel = run.getSequencerReference().getPlatform().getInstrumentModel();
      if ("Illumina MiSeq".equals(instrumentModel) || "Illumina NextSeq 500".equals(instrumentModel)) {
        // append the base mask property for miseq runs
        String basesMask = "y" + run.getCycles() + ",i" + indexValue;
        if (run.getPairedEnd()) {
          basesMask += ",y" + run.getCycles();
        }
        map.put("use-bases-mask", basesMask);
      }

      map.put("lane-value", laneValue);
      map.put("nucleic-acid-type", naType);

      map.put("sample-sheet-string",
          RunProcessingUtils.buildIlluminaDemultiplexCSV(run, f, "1.8.2", user.getFullName()).replaceAll("\n", "\\\n"));

      map.put("contaminant-list", "ecoli,phix_174,human_chr17,arabidopsis_chloroplast,vectors");

      map.put("username", user.getLoginName());

      map.put("paired-end", String.valueOf(run.getPairedEnd()));

      map.put("email-report", "on");
      map.put("ignore-missing-stats", "on");
      map.put("ignore-missing-bcls", "on");
      map.put("ignore-missing-controls", "on");
      map.put("allow-mismatch", "on");

      model.put("defaultRunValues", map);

      List<String> pipelineNames = new ArrayList<String>();
      for (JSONObject pipeline : (Iterable<JSONObject>) queryService.getPipelines()) {
        pipelineNames.add(pipeline.getString("name"));
      }
      Collections.sort(pipelineNames);
      model.put("pipelines", pipelineNames);

      return new ModelAndView("/pages/createAnalysisTask.jsp", model);
    }
  }

  /**
   * Gets the {@link uk.ac.ebi.fgpt.conan.model.ConanTask} with the given ID.
   * 
   * @param taskID
   *          the ID of the task to retrieve
   * @return the task assigned this ID
   */
  @RequestMapping(value = "/analysis/task/{taskID}", method = RequestMethod.GET)
  public @ResponseBody ConanTask<? extends ConanPipeline> getTask(@PathVariable String taskID) {
    return null;
  }

  /**
   * Returns a list of all submitted tasks, in submission order. This includes all pending, running and completed tasks - basically a
   * history of everything that has ever been submitted.
   * 
   * @return the list of all submitted tasks
   */
  @RequestMapping(value = "/analysis/tasks", method = RequestMethod.GET)
  public @ResponseBody List<ConanTask<? extends ConanPipeline>> getTasks() {
    return null;
  }

  /**
   * Returns a list of all tasks that have been submitted but are pending execution. Tasks in this list may have been executed but failed:
   * tasks that fail should highlight their failure to the submitter, and flag the task as pending.
   * 
   * @return a list of all tasks pending execution
   */
  @RequestMapping(value = "/analysis/tasks", method = RequestMethod.GET, params = "pending")
  public @ResponseBody List<ConanTask<? extends ConanPipeline>> getPendingTasks() {
    return null;
  }

  /**
   * Returns a list of all tasks that are currently being executed.
   * 
   * @return the currently executing tasks
   */
  @RequestMapping(value = "/analysis/tasks", method = RequestMethod.GET, params = "running")
  public @ResponseBody List<ConanTask<? extends ConanPipeline>> getRunningTasks() {
    return null;
  }

  /**
   * Returns a list of all tasks that have been executed and completed. This includes tasks that completed successfully, and those that
   * completed because a process failed and was subsequently marked as complete by the submitter.
   * 
   * @return the tasks that have completed
   */
  @RequestMapping(value = "/analysis/tasks", method = RequestMethod.GET, params = "complete")
  public @ResponseBody List<ConanTask<? extends ConanPipeline>> getCompletedTasks() {
    return null;
  }

  /**
   * Returns a list of all submitted tasks, in submission order. This includes all pending, running and completed tasks - basically a
   * history of everything that has ever been submitted.
   * 
   * @return the list of all submitted tasks
   */
  @RequestMapping(value = "/analysis/pipeline/{pipelineName}", method = RequestMethod.GET)
  public @ResponseBody ConanPipeline getPipeline(@PathVariable String pipelineName) {
    return null;
  }

  /**
   * Returns a list of all tasks that have been executed and completed. This includes tasks that completed successfully, and those that
   * completed because a process failed and was subsequently marked as complete by the submitter.
   * 
   * @return the tasks that have completed
   */
  @RequestMapping(value = "/analysis/pipelines", method = RequestMethod.GET)
  public @ResponseBody List<ConanPipeline> getPipelines() {
    return null;
  }
}
