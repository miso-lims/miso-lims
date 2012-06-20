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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.fgpt.conan.model.ConanPipeline;
import uk.ac.ebi.fgpt.conan.model.ConanProcess;
import uk.ac.ebi.fgpt.conan.model.ConanUser;
import uk.ac.ebi.fgpt.conan.service.ConanPipelineService;
import uk.ac.ebi.fgpt.conan.service.ConanProcessService;
import uk.ac.ebi.fgpt.conan.service.ConanUserService;
//import uk.ac.ebi.fgpt.conan.web.view.PipelineCreationResponseBean;
//import uk.ac.ebi.fgpt.conan.web.view.PipelineReorderRequestBean;
//import uk.ac.ebi.fgpt.conan.web.view.PipelineReorderResponseBean;
//import uk.ac.ebi.fgpt.conan.web.view.PipelineRequestBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Allows querying for pipelines and creation of new pipelines.
 *
 * @author Tony Burdett
 * @date 30-Jul-2010
 */
@Controller
public class PipelineController {
  /*
    private ConanPipelineService pipelineService;
    private ConanProcessService processService;
    private ConanUserService userService;

    private Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public ConanPipelineService getPipelineService() {
        return pipelineService;
    }

    @Autowired
    public void setPipelineService(ConanPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    public ConanProcessService getProcessService() {
        return processService;
    }

    @Autowired
    public void setProcessService(ConanProcessService processService) {
        this.processService = processService;
    }

    public ConanUserService getUserService() {
        return userService;
    }

    @Autowired
    public void setUserService(ConanUserService userService) {
        this.userService = userService;
    }
*/
    /**
     * Gets a collection of pipelines that can be used when submitting tasks.  This operation requires knowledge of the
     * user making this request, so takes a restApiKey parameter.
     *
     * @param restApiKey the rest api key used to access this service, unique to each user
     * @return the list of pipelines
     */
    /*
    @RequestMapping(value= "/analysis/pipelines", method = RequestMethod.GET)
    public @ResponseBody Collection<ConanPipeline> getPipelines(@RequestParam String restApiKey) {
        // retrieve the user
        ConanUser conanUser = getUserService().getUserByRestApiKey(restApiKey);

        // return this user, or log an error and return an empty collection if this isn't a valid user
        if (conanUser != null) {
            return getPipelineService().getPipelines(conanUser);
        }
        else {
            getLog().warn("Cannot recover any details about the logged in user.  " +
                                  "No pipelines will be available to them.");
            return Collections.emptyList();
        }
    }
    */
  /*
    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody PipelineReorderResponseBean reorderPipelines(
            @RequestBody PipelineReorderRequestBean reorderRequest) {
        // recover the rest api key from the request
        String restApiKey = reorderRequest.getRestApiKey();

        // get the user identified by this rest api key
        ConanUser conanUser = getUserService().getUserByRestApiKey(restApiKey);

        // user has permission to do this?
        if (conanUser.getPermissions().compareTo(ConanUser.Permissions.SUBMITTER) > -1) {
            // change the pipeline order
            getPipelineService().reorderPipelines(conanUser, reorderRequest.getRequestedPipelineOrder());

            String msg = "Pipelines have been reordered as required";
            return new PipelineReorderResponseBean(true, msg, getPipelineService().getPipelines(conanUser));
        }
        else {
            String msg = "You do not have permission to create new pipelines";
            return new PipelineReorderResponseBean(false, msg, getPipelineService().getPipelines(conanUser));
        }
    }
    */
    /**
     * Submits a request to create a new pipeline, by POST request.  This request requires a single parameter that is a
     * JSON encoded version of a {@link uk.ac.ebi.fgpt.conan.web.view.PipelineRequestBean}.  This request is validated
     * within the server and used to add a new pipeline.  The response is the list of pipelines available to Conan after
     * the new pipeline request is added.
     *
     * @param pipelineRequest the request describing the pipeline to add
     * @return the pipelines available to use after the new pipelines have been added
     */
    /*
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody PipelineCreationResponseBean addPipeline(@RequestBody PipelineRequestBean pipelineRequest) {
        // recover the rest api key from the request
        String restApiKey = pipelineRequest.getRestApiKey();

        // get the user identified by this rest api key
        ConanUser conanUser = getUserService().getUserByRestApiKey(restApiKey);

        // user has permission to do this?
        if (conanUser.getPermissions().compareTo(ConanUser.Permissions.SUBMITTER) > -1) {
            // recover the processes for the given process descriptions
            List<ConanProcess> conanProcesses = new ArrayList<ConanProcess>();
            for (String processName : pipelineRequest.getProcesses()) {
                // lookup process
                ConanProcess conanProcess = getProcessService().getProcess(processName);
                conanProcesses.add(conanProcess);
            }

            // now we've created all the processes we need, generate the pipeline
            ConanPipeline newPipeline = getPipelineService().createPipeline(pipelineRequest.getName(),
                                                                            conanProcesses,
                                                                            conanUser,
                                                                            pipelineRequest.isPrivate());

            // and return the list of pipelines now
            String msg = "Your pipeline '" + newPipeline.getName() + "' was successfully created";
            return new PipelineCreationResponseBean(true, msg, newPipeline,
                                                    getPipelineService().getPipelines(conanUser));
        }
        else {
            String msg = "You do not have permission to create new pipelines";
            return new PipelineCreationResponseBean(false, msg, null, Collections.<ConanPipeline>emptySet());
        }
    }
    */
}