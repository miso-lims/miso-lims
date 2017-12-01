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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;

/**
 * Created by IntelliJ IDEA. User: davey Date: 25-May-2010 Time: 16:39:52
 */
@Ajaxified
public class ContainerControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ContainerControllerHelperService.class);

  @Autowired
  private ContainerService containerService;

  public JSONObject isSerialNumberUnique(HttpSession session, JSONObject json) {
    if (!json.has("serialNumber") || (json.has("serialNumber") && isStringEmptyOrNull(json.getString("serialNumber")))) {
      return JSONUtils.SimpleJSONError("Please supply a serial number to lookup.");
    }

    String serialNumber = json.getString("serialNumber"); // Want to know if a container with this serial number already exists.
    String containerId = json.getString("containerId"); // Id of the container the serial number will be applied to. Might be null.
    Map<String, Object> responseMap = new HashMap<>();
    try {
      Collection<SequencerPartitionContainer> containers = containerService.listByBarcode(serialNumber);
      if (containers.isEmpty()) {
        responseMap.put("isSerialNumberUnique", true);
      } else {
        SequencerPartitionContainer container = new ArrayList<>(containers).get(0);

        if (containerId != null && !containerId.equals("null") && Long.valueOf(containerId).longValue() == container.getId()) {
          // The serial number is unique. Lookup returned the container being edited.
          responseMap.put("isSerialNumberUnique", true);
        } else {
          responseMap.put("isSerialNumberUnique", false);
        }
      }
      return JSONUtils.JSONObjectResponse(responseMap);
    } catch (IOException e) {
      String err = String.format("Unable to lookup serial number %s.", serialNumber);
      log.error(err, e);
      responseMap.put("error", err);
      responseMap.put("isSerialNumberUnique", false); // Uniqueness is unknown. Enter the error state to be safe.
      return JSONUtils.JSONObjectResponse(responseMap);
    }

  }

  public void setContainerService(ContainerService containerService) {
    this.containerService = containerService;
  }
}
