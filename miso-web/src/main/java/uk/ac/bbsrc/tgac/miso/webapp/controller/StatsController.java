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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.integration.factory.SequencerInterrogatorFactory;
import uk.ac.bbsrc.tgac.miso.core.service.integration.strategy.interrogator.SequencerInterrogator;
import uk.ac.bbsrc.tgac.miso.core.service.integration.ws.solid.SolidService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

import com.eaglegenomics.simlims.core.manager.SecurityManager;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
@RequestMapping("/stats")
@SessionAttributes("sequencerReference")
public class StatsController {
  protected static final Logger log = LoggerFactory.getLogger(StatsController.class);
  
  private enum ModelKeys {
    SEQUENCER("sequencerReference"),
    SEQUENCER_LIST("sequencerReferences"),
    RUNS("sequencerRuns"),
    RECORDS("sequencerServiceRecords"),
    PLATFORM("platformtype"),
    OTHER_SEQUENCERS("otherSequencerReferences"),
    TRIMMED_IP("trimmedIpAddress"),
    ALL_STATUS("stats"),
    SEQUENCER_NAME("referenceName"),
    SEQUENCER_ID("referenceId"),
    ERROR("error"),
    RUN_NAME("runName"),
    RUN_ID("runId"),
    RUN_STATUS("runStatus"),
    STATS_XML("statusXml"),
    CLUSTER_STATUS("clusterStatus");
    
    private final String key;
    
    ModelKeys(String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }
  }
  
  @Autowired
  private SecurityManager securityManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager) {
    this.requestManager = requestManager;
  }
  
  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return requestManager.getSequencerReferenceColumnSizes();
  }

  public Collection<SequencerReference> populateSequencerReferences() throws IOException {
    return requestManager.listAllSequencerReferences();
  }

  @RequestMapping(method = RequestMethod.GET)
  public ModelAndView view(ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), "All");
    model.put(ModelKeys.SEQUENCER_LIST.getKey(), populateSequencerReferences());
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/sequencer/{referenceId}", method = RequestMethod.GET)
  public ModelAndView viewSequencer(@PathVariable(value = "referenceId") Long referenceId, ModelMap model) throws IOException {
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
    
    if (sr != null) {
      Collection<Run> runs = requestManager.listRunsBySequencerId(referenceId);
      Collection<SequencerServiceRecord> serviceRecords = requestManager.listSequencerServiceRecordsBySequencerId(referenceId);
      Collection<SequencerReference> otherSequencers = getOtherSequencers(sr.getId());
      
      model.put(ModelKeys.SEQUENCER.getKey(), sr);
      model.put(ModelKeys.RUNS.getKey(), runs);
      model.put(ModelKeys.RECORDS.getKey(), serviceRecords);
      model.put(ModelKeys.OTHER_SEQUENCERS.getKey(), otherSequencers);
      String ip = sr.getIpAddress() == null ? "" : sr.getIpAddress().toString();
      if (ip.startsWith("/")) {
        model.put(ModelKeys.TRIMMED_IP.getKey(), ip.substring(1));
      } else {
        model.put(ModelKeys.TRIMMED_IP.getKey(), ip);
      }
    } else {
      throw new IOException("Cannot retrieve the named Sequencer reference");
    }
    return new ModelAndView("/pages/editSequencerReference.jsp", model);
  }
  
  private Collection<SequencerReference> getOtherSequencers(long selfId) throws IOException {
    Collection<SequencerReference> otherSequencers = requestManager.listAllSequencerReferences();
    
    // remove self from upgraded sequencer reference list
    SequencerReference sameRef = null;
    for (SequencerReference ref : otherSequencers) {
      if (ref.getId() == selfId) {
        sameRef = ref;
        break;
      }
    }
    otherSequencers.remove(sameRef);
    return otherSequencers;
  }

  @RequestMapping(value = "/sequencer", method = RequestMethod.POST)
  public String processSubmit(@ModelAttribute("sequencerReference") SequencerReference sr, ModelMap model, SessionStatus session)
      throws IOException {
    try {
      requestManager.saveSequencerReference(sr);
      session.setComplete();
      model.clear();
      return "redirect:/miso/stats";
    } catch (IOException ex) {
      if (log.isDebugEnabled()) {
        log.debug("Failed to save Sequencer Reference", ex);
      }
      throw ex;
    }
  }

  @RequestMapping(value = "/ls454", method = RequestMethod.GET)
  public ModelAndView allLs454Stats(ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.LS454);
    Collection<SequencerReference> s = new ArrayList<SequencerReference>();
    for (SequencerReference sr : populateSequencerReferences()) {
      if (sr.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        s.add(sr);
      }
    }
    model.put(ModelKeys.SEQUENCER_LIST.getKey(), s);
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/ls454/{referenceId}", method = RequestMethod.GET)
  public ModelAndView ls454Stats(@PathVariable(value = "referenceId") Long referenceId, ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.LS454);
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
    if (sr != null) {
      if (!sr.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        throw new IOException(
            "Trying to interrogate a " + sr.getPlatform().getPlatformType().getKey() + " sequencer reference with a 454 strategy");
      }
      try {
        SequencerInterrogator context = SequencerInterrogatorFactory.getSequencerInterrogator(sr);

        model.put(ModelKeys.ALL_STATUS.getKey(), context.listAllStatus());
        model.put(ModelKeys.SEQUENCER_NAME.getKey(), sr.getName());
        model.put(ModelKeys.SEQUENCER_ID.getKey(), sr.getId());
      } catch (InterrogationException e) {
        model.put(ModelKeys.ERROR.getKey(), MisoWebUtils.generateErrorDivMessage(
            "Cannot retrieve status information for the given sequencer reference: " + sr.getName(), e.getMessage()));
        log.error("cannot retrieve status for sequencer", e);
      }
    } else {
      throw new IOException("Cannot retrieve the named Sequencer reference");
    }
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/ls454/{referenceId}/{runName}", method = RequestMethod.GET)
  public ModelAndView ls454Stats(@PathVariable(value = "referenceId") Long referenceId, @PathVariable(value = "runName") String runName,
      ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.LS454);
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
    if (sr != null) {
      if (!sr.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        throw new IOException(
            "Trying to interrogate a " + sr.getPlatform().getPlatformType().getKey() + " sequencer reference with a 454 strategy");
      }
      try {
        SequencerInterrogator context = SequencerInterrogatorFactory.getSequencerInterrogator(sr);
        Status status = context.getRunStatus(runName);
        if (status != null) {
          model.put(ModelKeys.SEQUENCER_NAME.getKey(), sr.getName());
          model.put(ModelKeys.SEQUENCER_ID.getKey(), sr.getId());
          model.put(ModelKeys.RUN_ID.getKey(), requestManager.getRunByAlias(runName).getId());
          model.put(ModelKeys.RUN_NAME.getKey(), runName);
          model.put(ModelKeys.RUN_STATUS.getKey(), status);
        } else {
          model.put(ModelKeys.ERROR.getKey(), MisoWebUtils.generateErrorDivMessage("Cannot consume the xml file for the given run name: " + runName));
        }
      } catch (InterrogationException e) {
        model.put(ModelKeys.ERROR.getKey(), MisoWebUtils.generateErrorDivMessage(
            "Cannot retrieve status information for the given sequencer reference: " + sr.getName(), e.getMessage()));
        log.error("cannot retrieve the status for sequencer", e);
      }
    } else {
      throw new IOException("Cannot retrieve the named Sequencer reference");
    }
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/illumina", method = RequestMethod.GET)
  public ModelAndView allIlluminaStats(ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.ILLUMINA);
    Collection<SequencerReference> s = new ArrayList<SequencerReference>();
    for (SequencerReference sr : populateSequencerReferences()) {
      if (sr.getPlatform() != null && PlatformType.ILLUMINA.equals(sr.getPlatform().getPlatformType())) {
        s.add(sr);
      }
    }
    model.put(ModelKeys.SEQUENCER_LIST.getKey(), s);
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/illumina/{referenceId}", method = RequestMethod.GET)
  public ModelAndView illuminaStats(@PathVariable(value = "referenceId") Long referenceId, ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.ILLUMINA.getKey().toLowerCase());
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
    if (sr != null) {
      if (!sr.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
        throw new IOException(
            "Trying to interrogate a " + sr.getPlatform().getPlatformType().getKey() + " sequencer reference with an Illumina strategy");
      }
      model.put(ModelKeys.ALL_STATUS.getKey(), requestManager.listAllStatusBySequencerName(sr.getName()));
      model.put(ModelKeys.SEQUENCER_NAME.getKey(), sr.getName());
      model.put(ModelKeys.SEQUENCER_ID.getKey(), sr.getId());
    } else {
      model.put(ModelKeys.ERROR.getKey(), "Cannot retrieve the given sequencer reference: " + referenceId);
    }
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/illumina/{referenceId}/{runName}", method = RequestMethod.GET)
  public ModelAndView illuminaStats(@PathVariable(value = "referenceId") Long referenceId, @PathVariable(value = "runName") String runName,
      ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.ILLUMINA);
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
    if (sr != null) {
      if (!sr.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
        throw new IOException(
            "Trying to interrogate a " + sr.getPlatform().getPlatformType().getKey() + " sequencer reference with an Illumina strategy");
      }

      try {
        Status status = requestManager.getStatusByRunName(runName);
        if (status != null) {
          model.put(ModelKeys.SEQUENCER_NAME.getKey(), sr.getName());
          model.put(ModelKeys.SEQUENCER_ID.getKey(), sr.getId());
          model.put(ModelKeys.RUN_ID.getKey(), requestManager.getRunByAlias(runName).getId());
          model.put(ModelKeys.RUN_NAME.getKey(), runName);
          model.put(ModelKeys.RUN_STATUS.getKey(), status);

          InputStream in = StatsController.class.getResourceAsStream("/status/xsl/illumina/statusXml.xsl");
          if (in != null) {
            String xsl = LimsUtils.inputStreamToString(in);
            model.put(ModelKeys.STATS_XML.getKey(), (SubmissionUtils.xslTransform(status.getXml(), xsl)));
          }
        } else {
          model.put(ModelKeys.ERROR.getKey(), MisoWebUtils.generateErrorDivMessage("Cannot consume the xml file for the given run name: " + runName));
        }
      } catch (TransformerException e) {
        model.put(ModelKeys.ERROR.getKey(),
            MisoWebUtils.generateErrorDivMessage("Cannot retrieve status XML for the given run: " + runName, e.getMessage()));
        log.error("Cannot retrieve XML for run", e);
      }
    } else {
      model.put(ModelKeys.ERROR.getKey(), "Cannot retrieve the given sequencer reference: " + referenceId);
    }
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/solid", method = RequestMethod.GET)
  public ModelAndView allSolidStats(ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.SOLID);
    Collection<SequencerReference> s = new ArrayList<SequencerReference>();
    for (SequencerReference sr : populateSequencerReferences()) {
      if (sr.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
        s.add(sr);
      }
    }
    model.put(ModelKeys.SEQUENCER_LIST.getKey(), s);
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/solid/{referenceId}", method = RequestMethod.GET)
  public ModelAndView solidStats(@PathVariable(value = "referenceId") Long referenceId, ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.SOLID);
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);

    if (!sr.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
      throw new IOException(
          "Trying to interrogate a " + sr.getPlatform().getPlatformType().getKey() + " sequencer reference with a SOLiD strategy");
    }

    if (!sr.getPlatform().getInstrumentModel().contains("5500xl")) {
      SolidService ss = new SolidService(new URL("http://" + sr.getFQDN() + ":8080/sets/webservice/solid?wsdl"),
          new QName("http://solid.aga.appliedbiosystems.com", "SolidService"));
      StringBuilder sb = new StringBuilder();
      try {
        InputStream in = StatsController.class.getResourceAsStream("/integration/solid/xsl/clusterStatus.xsl");
        if (in != null) {
          String xsl = LimsUtils.inputStreamToString(in);
          sb.append(SubmissionUtils.xslTransform(ss.getSolidPort().getClusterStatus().getXml(), xsl));
        }
      } catch (TransformerException e) {
        sb.append("Unable to transform Cluster Status XML: " + e.getMessage());
        log.error("Unable to transform cluster status XML", e);
      }
      model.put(ModelKeys.CLUSTER_STATUS.getKey(), sb.toString());
    }

    model.put(ModelKeys.ALL_STATUS.getKey(), requestManager.listAllStatusBySequencerName(sr.getName()));
    model.put(ModelKeys.SEQUENCER_NAME.getKey(), sr.getName());
    model.put(ModelKeys.SEQUENCER_ID.getKey(), sr.getId());

    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/solid/{referenceId}/{runName}", method = RequestMethod.GET)
  public ModelAndView solidStats(@PathVariable(value = "referenceId") Long referenceId, @PathVariable(value = "runName") String runName,
      ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.SOLID);
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
    if (sr != null) {
      if (!sr.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
        throw new IOException(
            "Trying to interrogate a " + sr.getPlatform().getPlatformType().getKey() + " sequencer reference with an SOLiD strategy");
      }

      try {
        Status status = requestManager.getStatusByRunName(runName);
        if (status != null) {
          model.put(ModelKeys.SEQUENCER_NAME.getKey(), sr.getName());
          model.put(ModelKeys.SEQUENCER_ID.getKey(), sr.getId());
          model.put(ModelKeys.RUN_ID.getKey(), requestManager.getRunByAlias(runName).getId());
          model.put(ModelKeys.RUN_NAME.getKey(), runName);
          model.put(ModelKeys.RUN_STATUS.getKey(), status);

          InputStream in = StatsController.class.getResourceAsStream("/status/xsl/solid/statusXml.xsl");
          if (in != null) {
            String xsl = LimsUtils.inputStreamToString(in);
            model.put(ModelKeys.STATS_XML.getKey(), (SubmissionUtils.xslTransform(status.getXml(), xsl)));
          }
        } else {
          model.put(ModelKeys.ERROR.getKey(), MisoWebUtils.generateErrorDivMessage("Cannot consume the xml file for the given run name: " + runName));
        }
      } catch (TransformerException e) {
        model.put(ModelKeys.ERROR.getKey(),
            MisoWebUtils.generateErrorDivMessage("Cannot retrieve status XML for the given run: " + runName, e.getMessage()));
        log.error("Cannot retrieve status XML for run", e);
      }
    } else {
      model.put(ModelKeys.ERROR.getKey(), "Cannot retrieve the given sequencer reference: " + referenceId);
    }
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/pacbio", method = RequestMethod.GET)
  public ModelAndView allPacbioStats(ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.PACBIO);
    Collection<SequencerReference> s = new ArrayList<SequencerReference>();
    for (SequencerReference sr : populateSequencerReferences()) {
      if (sr.getPlatform().getPlatformType().equals(PlatformType.PACBIO)) {
        s.add(sr);
      }
    }
    model.put(ModelKeys.SEQUENCER_LIST.getKey(), s);
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/pacbio/{referenceId}", method = RequestMethod.GET)
  public ModelAndView pacbioStats(@PathVariable(value = "referenceId") Long referenceId, ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.PACBIO.getKey().toLowerCase());
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
    if (sr != null) {
      if (!sr.getPlatform().getPlatformType().equals(PlatformType.PACBIO)) {
        throw new IOException(
            "Trying to interrogate a " + sr.getPlatform().getPlatformType().getKey() + " sequencer reference with a PacBio strategy");
      }
      model.put(ModelKeys.ALL_STATUS.getKey(), requestManager.listAllStatusBySequencerName(sr.getName()));
      model.put(ModelKeys.SEQUENCER_NAME.getKey(), sr.getName());
      model.put(ModelKeys.SEQUENCER_ID.getKey(), sr.getId());
    } else {
      model.put(ModelKeys.ERROR.getKey(), "Cannot retrieve the given sequencer reference: " + referenceId);
    }
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/pacbio/{referenceId}/{runName}", method = RequestMethod.GET)
  public ModelAndView pacbioStats(@PathVariable(value = "referenceId") Long referenceId, @PathVariable(value = "runName") String runName,
      ModelMap model) throws IOException {
    model.put(ModelKeys.PLATFORM.getKey(), PlatformType.PACBIO);
    SequencerReference sr = requestManager.getSequencerReferenceById(referenceId);
    if (sr != null) {
      if (!sr.getPlatform().getPlatformType().equals(PlatformType.PACBIO)) {
        throw new IOException(
            "Trying to interrogate a " + sr.getPlatform().getPlatformType().getKey() + " sequencer reference with a PacBio strategy");
      }

      Status status = requestManager.getStatusByRunName(runName);
      if (status != null) {
        model.put(ModelKeys.SEQUENCER_NAME.getKey(), sr.getName());
        model.put(ModelKeys.SEQUENCER_ID.getKey(), sr.getId());
        model.put(ModelKeys.RUN_ID.getKey(), requestManager.getRunByAlias(runName).getId());
        model.put(ModelKeys.RUN_NAME.getKey(), runName);
        model.put(ModelKeys.RUN_STATUS.getKey(), status);

      } else {
        model.put(ModelKeys.ERROR.getKey(), MisoWebUtils.generateErrorDivMessage("Cannot consume the xml file for the given run name: " + runName));
      }
    } else {
      model.put(ModelKeys.ERROR.getKey(), "Cannot retrieve the given sequencer reference: " + referenceId);
    }
    return new ModelAndView("/pages/viewStats.jsp", model);
  }

  @RequestMapping(value = "/configure", method = RequestMethod.GET)
  public ModelAndView configure(ModelMap model) throws IOException {
    return new ModelAndView("/pages/configureStats.jsp", model);
  }
}
