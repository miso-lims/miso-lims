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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.dto.ContainerDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller.rest
 * <p/>
 * Info
 * 
 * @author Xingdong Bian
 */
@Controller
@RequestMapping("/rest/container")
@SessionAttributes("container")
public class ContainerRestController extends RestController {
  protected static final Logger log = LoggerFactory.getLogger(ContainerRestController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{containerBarcode}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String jsonRest(@PathVariable String containerBarcode) throws IOException {
    StringBuilder sb = new StringBuilder();
    Collection<SequencerPartitionContainer<SequencerPoolPartition>> sequencerPartitionContainerCollection = requestManager
        .listSequencerPartitionContainersByBarcode(containerBarcode);
    int i = 0;
    for (SequencerPartitionContainer<SequencerPoolPartition> sequencerPartitionContainer : sequencerPartitionContainerCollection) {
      i++;
      sb.append("{");
      sb.append("\"containerId\":\"" + sequencerPartitionContainer.getId() + "\",");
      sb.append("\"identificationBarcode\":\"" + sequencerPartitionContainer.getIdentificationBarcode() + "\",");
      if (sequencerPartitionContainer.getPlatform() != null) {
        sb.append("\"platform\":\"" + sequencerPartitionContainer.getPlatform().getNameAndModel() + "\",");
      }
      sb.append("\"partitions\":[");
      int ip = 0;
      for (SequencerPoolPartition partition : sequencerPartitionContainer.getPartitions()) {
        ip++;
        sb.append("{");
        sb.append("\"partition\":\"" + partition.getId() + "\",");
        sb.append("\"pool\":");
        if (partition.getPool() != null) {
          sb.append("{");
          sb.append("\"poolName\":\"" + partition.getPool().getName() + "\",");
          sb.append("\"poolDate\":\"" + partition.getPool().getCreationDate() + "\",");
          // experiments
          sb.append("\"experiments\":[");
          int ie = 0;
          ObjectMapper mapper = new ObjectMapper();
          for (Experiment experiment : partition.getPool().getExperiments()) {
            ie++;
            sb.append(mapper.writeValueAsString(experiment));
            if (ie < partition.getPool().getExperiments().size()) {
              sb.append(",");
            }
          }
          sb.append("],");

          // dilutions
          sb.append("\"poolableElements\":[");
          int id = 0;
          for (Dilution poolable : partition.getPool().getPoolableElements()) {
            id++;
            sb.append(mapper.writeValueAsString(poolable));
            if (id < partition.getPool().getPoolableElements().size()) {
              sb.append(",");
            }

          }
          sb.append("]");
          sb.append("}");
        } else {
          sb.append("\"\"");
        }
        sb.append("}");
        if (ip < sequencerPartitionContainer.getPartitions().size()) {
          sb.append(",");
        }
      }
      sb.append("]");
      sb.append("}");
      if (i < sequencerPartitionContainerCollection.size()) {
        sb.append(",");
      }
    }
    return "[" + sb.toString() + "]";
  }

  @RequestMapping(value = "/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<ContainerDto> getContainers(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    if (request.getParameterMap().size() > 0) {
      Long numContainers = requestManager.countContainers();
      // get request params from DataTables
      Integer iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
      Integer iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
      String sSearch = request.getParameter("sSearch");
      String sSortDir = request.getParameter("sSortDir_0");
      String sortColIndex = request.getParameter("iSortCol_0");
      String sortCol = request.getParameter("mDataProp_" + sortColIndex);

      // get requested subset of containers
      Collection<SequencerPartitionContainer<SequencerPoolPartition>> containerSubset;
      Long numMatches;

      if (!isStringEmptyOrNull(sSearch)) {
        containerSubset = requestManager.getContainersByPageSizeSearch(iDisplayStart, iDisplayLength, sSearch, sSortDir, sortCol);
        numMatches = Long.valueOf(requestManager.countContainersBySearch(sSearch));
      } else {
        containerSubset = requestManager.getContainersByPageAndSize(iDisplayStart, iDisplayLength, sSortDir, sortCol);
        numMatches = numContainers;
      }
      List<ContainerDto> containerDtos = Dtos.asContainerDtos(containerSubset);
      URI baseUri = uriBuilder.build().toUri();
      for (ContainerDto containerDto : containerDtos) {
        containerDto.setUrl(
            UriComponentsBuilder.fromUri(baseUri).path("/rest/run/container/{barcode}")
                .buildAndExpand(containerDto.getIdentificationBarcode()).toUriString());
      }

      DataTablesResponseDto<ContainerDto> dtResponse = new DataTablesResponseDto<>();
      dtResponse.setITotalRecords(numContainers);
      dtResponse.setITotalDisplayRecords(numMatches);
      dtResponse.setAaData(containerDtos);
      dtResponse.setSEcho(new Long(request.getParameter("sEcho")));
      return dtResponse;
    } else {
      throw new RestException("Request must specify DataTables parameters.");
    }
  }

}
