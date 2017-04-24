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

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;

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

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.dto.ContainerDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.ContainerService;

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

  @Autowired
  private ContainerService containerService;

  private final JQueryDataTableBackend<SequencerPartitionContainer, ContainerDto> jQueryBackend = new JQueryDataTableBackend<SequencerPartitionContainer, ContainerDto>() {

    @Override
    protected ContainerDto asDto(SequencerPartitionContainer model, UriComponentsBuilder builder) {
      ContainerDto dto = Dtos.asDto(model);
      dto.writeUrls(builder);
      return dto;
    }

    @Override
    protected PaginatedDataSource<SequencerPartitionContainer> getSource() throws IOException {
      return containerService;
    }

  };

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{containerBarcode}", method = RequestMethod.GET, produces = "application/json")
  public @ResponseBody String jsonRest(@PathVariable String containerBarcode) throws IOException {
    StringBuilder sb = new StringBuilder();
    Collection<SequencerPartitionContainer> sequencerPartitionContainerCollection = requestManager
        .listSequencerPartitionContainersByBarcode(containerBarcode);
    int i = 0;
    for (SequencerPartitionContainer sequencerPartitionContainer : sequencerPartitionContainerCollection) {
      i++;
      sb.append("{");
      sb.append("\"containerId\":\"" + sequencerPartitionContainer.getId() + "\",");
      sb.append("\"identificationBarcode\":\"" + sequencerPartitionContainer.getIdentificationBarcode() + "\",");
      if (sequencerPartitionContainer.getPlatform() != null) {
        sb.append("\"platform\":\"" + sequencerPartitionContainer.getPlatform().getNameAndModel() + "\",");
      }
      sb.append("\"partitions\":[");
      int ip = 0;
      for (Partition partition : sequencerPartitionContainer.getPartitions()) {
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
          for (PoolableElementView poolable : partition.getPool().getPoolableElementViews()) {
            id++;
            sb.append(mapper.writeValueAsString(poolable));
            if (id < partition.getPool().getPoolableElementViews().size()) {
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
    return jQueryBackend.get(request, response, uriBuilder);
  }
}
