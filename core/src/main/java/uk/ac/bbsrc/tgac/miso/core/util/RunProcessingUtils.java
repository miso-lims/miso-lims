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

package uk.ac.bbsrc.tgac.miso.core.util;

import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 28/03/12
 * @since 0.1.6
 */
public class RunProcessingUtils {
  public static String buildIlluminaDemultiplexCSV(Run r, SequencerPartitionContainer f, String casavaVersion,
      String userName) {
    boolean newCasava = false;

    StringBuilder sb = new StringBuilder();
    sb.append("FCID,").append("Lane,").append("SampleID,").append("SampleRef,").append("Index,").append("Description,").append("Control,")
        .append("Recipe,").append("Operator");

    if (casavaVersion.compareTo("1.7") >= 0) {
      newCasava = true;
    }

    if (newCasava) {
      sb.append(",Project\n");
    } else {
      sb.append("\n");
    }

    for (Partition l : f.getPartitions()) {
      Pool p = l.getPool();
      if (p != null) {
        for (PoolableElementView ld : p.getPoolableElementViews()) {
          sb.append(f.getIdentificationBarcode()).append(",").append(l.getPartitionNumber()).append(",").append(f.getId()).append("_")
              .append(ld.getLibraryName()).append("_").append(ld.getDilutionName()).append(",")
              .append(ld.getSampleAlias().replaceAll("\\s", "")).append(",");

          if (ld.getIndices() != null && !ld.getIndices().isEmpty()) {
            boolean first = true;
            for (Index index : ld.getIndices()) {
              sb.append(index.getSequence());
              if (first) {
                first = false;
              } else {
                sb.append("-");
              }
            }
            sb.append(",");
          } else {
            sb.append(",");
          }

          sb.append(ld.getLibraryDescription()).append(",").append("N").append(",").append("NA").append(",").append(userName);

          if (newCasava) {
            sb.append(",").append(ld.getProjectAlias().replaceAll("\\s", "")).append("\n");
          } else {
            sb.append("\n");
          }
        }
      }
    }
    return sb.toString();
  }
}
