/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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
import org.springframework.web.servlet.ModelAndView;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.service.ExperimentService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;

@Controller
@RequestMapping("/experimentwizard")
@SessionAttributes("experimentwizard")
public class ExperimentWizardController {
  protected static final Logger log = LoggerFactory.getLogger(ExperimentWizardController.class);

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private ExperimentService experimentService;

  @Autowired
  private StudyService studyService;

  @Autowired
  private PoolService poolService;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @ModelAttribute("studyTypes")
  public Collection<StudyType> populateStudyTypes() throws IOException {
    return studyService.listTypes();
  }

  @ModelAttribute("maxLengths")
  public Map<String, Integer> maxLengths() throws IOException {
    return experimentService.getColumnSizes();
  }

  @ModelAttribute("platforms")
  public Collection<Platform> populatePlatforms() throws IOException {
    return requestManager.listAllPlatforms();
  }

  public Collection<? extends Pool> populateAvailablePools(Experiment experiment) throws IOException {
    if (experiment.getPlatform() != null) {
      PlatformType platformType = experiment.getPlatform().getPlatformType();
      ArrayList<Pool> pools = new ArrayList<>();
      for (Pool p : poolService.listAllPoolsByPlatform(platformType)) {
        if (experiment.getPool() == null || !experiment.getPool().equals(p)) {
          pools.add(p);
        }
        Collections.sort(pools);
      }
      return pools;
    }
    return poolService.listAllPools();
  }

  @RequestMapping(value = "/new/{projectId}", method = RequestMethod.GET)
  public ModelAndView newAssignedProject(@PathVariable Long projectId, ModelMap model) throws IOException {
    model.put("projectId", projectId);
    return new ModelAndView("/pages/experimentWizard.jsp", model);
  }
}
