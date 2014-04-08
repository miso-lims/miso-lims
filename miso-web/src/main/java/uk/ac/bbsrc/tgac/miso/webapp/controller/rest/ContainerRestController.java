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

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import java.io.IOException;
import java.util.Collection;

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
public class ContainerRestController {
  protected static final Logger log = LoggerFactory.getLogger(ContainerRestController.class);

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @RequestMapping(value = "{containerBarcode}", method = RequestMethod.GET)
  public
  @ResponseBody
  String jsonRest(@PathVariable String containerBarcode) throws IOException {
    StringBuilder sb = new StringBuilder();
    Collection<SequencerPartitionContainer<SequencerPoolPartition>> sequencerPartitionContainerCollection = requestManager.listSequencerPartitionContainersByBarcode(containerBarcode);
    int i = 0;
    for (SequencerPartitionContainer<SequencerPoolPartition> sequencerPartitionContainer : sequencerPartitionContainerCollection) {
      i++;
      sb.append("{");
      sb.append("\"containerId\":\"" + sequencerPartitionContainer.getId() + "\",");
      sb.append("\"identificationBarcode\":\"" + sequencerPartitionContainer.getIdentificationBarcode() + "\",");
      sb.append("\"platform\":\"" + sequencerPartitionContainer.getPlatform().getNameAndModel() + "\",");
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
          //experiments
          sb.append("\"experiments\":[");
          int ie = 0;
          for (Experiment experiment : partition.getPool().getExperiments()) {
            ie++;
            ObjectMapper mappere = new ObjectMapper();
            sb.append(mappere.writeValueAsString(experiment));
            if (ie < partition.getPool().getExperiments().size()) {
              sb.append(",");
            }
          }
          sb.append("],");

          //dilutions
          sb.append("\"poolableElements\":[");
          int id = 0;
          for (Poolable poolable : partition.getPool().getPoolableElements()) {
            id++;
            ObjectMapper mapperd = new ObjectMapper();
            sb.append(mapperd.writeValueAsString(poolable));
            if (id < partition.getPool().getDilutions().size()) {
              sb.append(",");
            }

          }
          sb.append("]");
          sb.append("}");
        }
        else {
          sb.append("\"\"");
        }
        sb.append("}");
        if (ip < sequencerPartitionContainer.getPartitions().size()) {
          sb.append(",");
        }
      }
      sb.append("]");
      sb.append("}");
    }
    if (i < sequencerPartitionContainerCollection.size()) {
      sb.append(",");
    }
    return "[" + sb.toString() + "]";
  }
}
