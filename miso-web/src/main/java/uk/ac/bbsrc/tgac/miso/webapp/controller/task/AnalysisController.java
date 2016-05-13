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

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.integration.AnalysisQueryService;
import uk.ac.bbsrc.tgac.miso.core.util.RunProcessingUtils;
import uk.ac.bbsrc.tgac.miso.integration.util.IntegrationException;

import java.io.IOException;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.3
 */
@Controller
//@SessionAttributes("tasks")
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
    }
    else {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      model.put("run", run);

      Map<String, String> map = new HashMap<String,String>();
      map.put("RunAccession", run.getAlias());
      map.put("basecall-path", run.getFilePath()+"/Data/Intensities/BaseCalls");
//      map.put("fastq-path", run.getFilePath()+"/Data/Intensities/BaseCalls/PAP");
//      map.put("makefile-path", run.getFilePath()+"/Data/Intensities/BaseCalls/PAP/Makefile");
//      map.put("sample-sheet-path", run.getFilePath()+"/Data/Intensities/BaseCalls/SampleSheet-pap.csv");

      map.put("fastq-path", "/net/tgac-labdata-nfs/ifs/TGAC/NGS_data/qc/"+run.getAlias()+"/PAP");
      map.put("makefile-path", "/net/tgac-labdata-nfs/ifs/TGAC/NGS_data/qc/"+run.getAlias()+"/PAP/Makefile");
      map.put("sample-sheet-path", "/net/tgac-labdata-nfs/ifs/TGAC/NGS_data/qc/"+run.getAlias()+"/SampleSheet-PAP.csv");

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
        }
        else {
          throw new IntegrationException("Cannot start analysis pipelines on a run with no pools on any lanes.");
        }
      }

      String instrumentModel = run.getSequencerReference().getPlatform().getInstrumentModel();
      if ("Illumina MiSeq".equals(instrumentModel) || "Illumina NextSeq 500".equals(instrumentModel)) {
        //append the base mask property for miseq runs
        String basesMask = "y"+run.getCycles()+",i"+indexValue;
        if (run.getPairedEnd()) {
          basesMask += ",y"+run.getCycles();
        }
        map.put("use-bases-mask", basesMask);
      }

      map.put("lane-value", laneValue);
      map.put("nucleic-acid-type", naType);

      map.put("sample-sheet-string", RunProcessingUtils.buildIlluminaDemultiplexCSV(run, f, "1.8.2", user.getFullName()).replaceAll("\n", "\\\n"));

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
      for (JSONObject pipeline : (Iterable<JSONObject>)queryService.getPipelines()) {
        pipelineNames.add(pipeline.getString("name"));
      }
      Collections.sort(pipelineNames);
      model.put("pipelines", pipelineNames);

      return new ModelAndView("/pages/createAnalysisTask.jsp", model);
    }
  }
}
