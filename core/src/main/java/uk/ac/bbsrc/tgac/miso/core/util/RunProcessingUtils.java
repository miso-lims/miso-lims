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

package uk.ac.bbsrc.tgac.miso.core.util;

import java.util.Map;
import java.util.TreeMap;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;

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
  public static String buildIlluminaDemultiplexCSV(Run r, SequencerPartitionContainer<SequencerPoolPartition> f, String casavaVersion,
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

    for (SequencerPoolPartition l : f.getPartitions()) {
      int count = 0;
      Pool<? extends Poolable> p = l.getPool();
      if (p != null) {
        for (Dilution ld : p.getDilutions()) {
          count++;
          sb.append(f.getIdentificationBarcode()).append(",").append(l.getPartitionNumber()).append(",").append(f.getId()).append("_")
              .append(ld.getLibrary().getName()).append("_").append(ld.getName()).append(",")
              .append(ld.getLibrary().getSample().getAlias().replaceAll("\\s", "")).append(",");

          if (ld.getLibrary().getTagBarcodes() != null && !ld.getLibrary().getTagBarcodes().isEmpty()) {
            Map<Integer, TagBarcode> barcodes = new TreeMap<Integer, TagBarcode>(ld.getLibrary().getTagBarcodes());
            int bcount = 1;
            for (Integer key : barcodes.keySet()) {
              TagBarcode t = barcodes.get(key);
              sb.append(t.getSequence());
              if (bcount < barcodes.keySet().size() && barcodes.keySet().size() > 1) {
                sb.append("-");
              }
              bcount++;
            }
            sb.append(",");
          } else {
            sb.append(",");
          }

          sb.append(ld.getLibrary().getDescription()).append(",").append("N").append(",").append("NA").append(",").append(userName);

          if (newCasava) {
            sb.append(",").append(ld.getLibrary().getSample().getProject().getAlias().replaceAll("\\s", "")).append("\n");
          } else {
            sb.append("\n");
          }
        }
      }
    }
    return sb.toString();
  }
}
