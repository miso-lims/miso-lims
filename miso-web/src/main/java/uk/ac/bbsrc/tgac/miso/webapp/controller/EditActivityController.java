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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.eaglegenomics.simlims.core.ActivitySessionFactory;
import com.eaglegenomics.simlims.core.manager.ProtocolManager;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
//import com.eaglegenomics.simlims.spring.ActivityControllerHelper;
import uk.ac.bbsrc.tgac.miso.webapp.util.ActivityControllerHelperLoader;

@Controller
@SessionAttributes({ "activitySession", "dataModel" })
public class EditActivityController {
  protected static final Logger log = LoggerFactory.getLogger(EditActivityController.class);

  // @Autowired
  private InputDataXmlView inputDataXmlView;

  // @Autowired
  private ActivityControllerHelperLoader activityControllerHelperLoader;

  // @Autowired
  private SecurityManager securityManager;

  // @Autowired
  private ProtocolManager protocolManager;

  // @Autowired
  private ActivitySessionFactory activitySessionFactory;

  public void setInputDataXmlView(InputDataXmlView inputDataXmlView) {
    this.inputDataXmlView = inputDataXmlView;
  }

  public void setActivitySessionFactory(ActivitySessionFactory activitySessionFactory) {
    this.activitySessionFactory = activitySessionFactory;
  }

  public void setActivityHelperLoader(ActivityControllerHelperLoader activityControllerHelperLoader) {
    this.activityControllerHelperLoader = activityControllerHelperLoader;
  }

  public void setProtocolManager(ProtocolManager protocolManager) {
    this.protocolManager = protocolManager;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }
  /*
   * @RequestMapping(value = "/activity/{activityId}", method = RequestMethod.GET) public ModelAndView setupForm(
   * 
   * @PathVariable String activityId, ModelMap model) throws IOException { try { User user = securityManager
   * .getUserByLoginName(SecurityContextHolder.getContext() .getAuthentication().getName()); Activity activity =
   * protocolManager.getActivity(activityId); if (!activity.userCanWrite(user)) { throw new SecurityException("Permission denied."); }
   * ActivityControllerHelper helper = activityControllerHelperLoader .getHelper(activity); model.put("dataModel",
   * helper.createDataModel()); model.put("activitySession", activitySessionFactory .createActivitySession(user, activity)); return new
   * ModelAndView(helper.getDataModelView(), model); } catch (IOException ex) { if (log.isDebugEnabled()) {
   * log.debug("Failed to show activity", ex); } throw ex; } }
   * 
   * @RequestMapping(value = "/activity/process", method = RequestMethod.POST) public String processSubmit(
   * 
   * @ModelAttribute("activitySession") ActivitySession activitySession,
   * 
   * @ModelAttribute("dataModel") Object dataModel, ModelMap model, SessionStatus session) throws IOException { try { User user =
   * securityManager .getUserByLoginName(SecurityContextHolder.getContext() .getAuthentication().getName()); Activity activity =
   * activitySession.getActivity(); if (!activity.userCanWrite(user)) { throw new SecurityException("Permission denied."); } Properties
   * props = activityControllerHelperLoader.getHelper( activity).convertDataModelToActivityProperties( activitySession.getLockedInputData(),
   * dataModel); for (Map.Entry<Object, Object> entry : props.entrySet()) { activitySession.getProperties().put(entry.getKey(),
   * entry.getValue()); } activitySession.executeActivity(); activitySession.finish(); model.clear(); session.setComplete(); return
   * "redirect:/miso/activity/activities"; } catch (IOException ex) { if (log.isDebugEnabled()) { log.debug("Failed to process activity",
   * ex); } throw ex; } }
   * 
   * @RequestMapping(value = "/activity/input/available", method = RequestMethod.GET) public ModelAndView ajaxAvailableInput(
   * 
   * @ModelAttribute("activitySession") ActivitySession activitySession) throws IOException { try { if (activitySession == null) { throw new
   * SecurityException("Permission denied."); } User user = securityManager .getUserByLoginName(SecurityContextHolder.getContext()
   * .getAuthentication().getName()); Activity activity = activitySession.getActivity(); if (!activity.userCanWrite(user)) { throw new
   * SecurityException("Permission denied."); } ActivityControllerHelper helper = activityControllerHelperLoader .getHelper(activity);
   * Map<String, ActivityData> inputData = new HashMap<String, ActivityData>(); for (Iterator<ActivityData> iterator = activitySession
   * .getLockableInputData(ActivityDataFilter.FILTER_ACCEPT_ALL); iterator .hasNext();) { ActivityData activityData = iterator.next();
   * inputData.put(helper .convertActivityDataToDisplayName(activityData), activityData); } // Some kind of XML view? return new
   * ModelAndView(inputDataXmlView, "inputData", inputData); } catch (IOException ex) { if (log.isDebugEnabled()) {
   * log.debug("Failed to list available input", ex); } throw ex; } }
   * 
   * @RequestMapping(value = "/activity/input/lock", method = RequestMethod.GET) public ModelAndView ajaxLockInput(
   * 
   * @ModelAttribute("activitySession") ActivitySession activitySession,
   * 
   * @RequestParam(value = "inputId", required = true) Long[] inputIds) throws IOException { try { if (activitySession == null) { throw new
   * SecurityException("Permission denied."); } User user = securityManager .getUserByLoginName(SecurityContextHolder.getContext()
   * .getAuthentication().getName()); Activity activity = activitySession.getActivity(); if (!activity.userCanWrite(user)) { throw new
   * SecurityException("Permission denied."); } // Some kind of XML view? List<Long> ids = Arrays.asList(inputIds); Collection<ActivityData>
   * inputData = new HashSet<ActivityData>(); for (Iterator<ActivityData> iterator = activitySession
   * .getLockableInputData(ActivityDataFilter.FILTER_ACCEPT_ALL); iterator .hasNext();) { ActivityData lockableData = iterator.next(); if
   * (ids.contains(lockableData.getUniqueId())) { inputData.add(lockableData); } } Collection<ActivityData> lockedData = activitySession
   * .lockInputData(inputData); Map<String, ActivityData> lockedDataMap = new HashMap<String, ActivityData>(); ActivityControllerHelper
   * helper = activityControllerHelperLoader .getHelper(activity); for (ActivityData activityData : lockedData) { lockedDataMap.put(helper
   * .convertActivityDataToDisplayName(activityData), activityData); } return new ModelAndView(inputDataXmlView, "inputData",
   * lockedDataMap); } catch (IOException ex) { if (log.isDebugEnabled()) { log.debug("Failed to list locked data", ex); } throw ex; } }
   * 
   * @RequestMapping(value = "/activity/input/release", method = RequestMethod.GET) public ModelAndView ajaxReleaseInput(
   * 
   * @ModelAttribute("activitySession") ActivitySession activitySession,
   * 
   * @RequestParam(value = "inputId", required = true) Long[] inputIds) throws IOException { try { if (activitySession == null) { throw new
   * SecurityException("Permission denied."); } User user = securityManager .getUserByLoginName(SecurityContextHolder.getContext()
   * .getAuthentication().getName()); Activity activity = activitySession.getActivity(); if (!activity.userCanWrite(user)) { throw new
   * SecurityException("Permission denied."); } // Some kind of XML view? List<Long> ids = Arrays.asList(inputIds); Collection<ActivityData>
   * inputData = new HashSet<ActivityData>(); for (ActivityData lockedData : activitySession.getLockedInputData()) { if
   * (ids.contains(lockedData.getUniqueId())) { inputData.add(lockedData); } } Collection<ActivityData> releasedData = activitySession
   * .releaseInputData(inputData); Map<String, ActivityData> releasedDataMap = new HashMap<String, ActivityData>(); ActivityControllerHelper
   * helper = activityControllerHelperLoader .getHelper(activity); for (ActivityData activityData : releasedData) {
   * releasedDataMap.put(helper .convertActivityDataToDisplayName(activityData), activityData); } return new ModelAndView(inputDataXmlView,
   * "inputData", releasedDataMap); } catch (IOException ex) { if (log.isDebugEnabled()) { log.debug("Failed to release data", ex); } throw
   * ex; } }
   */
}
