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

package uk.ac.bbsrc.tgac.miso.core.service.submission;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * Created by IntelliJ IDEA. User: collesa Date: 04/04/12 Time: 15:15 To change this template use File | Settings | File Templates.
 */
@ServiceProvider
public class TGACIlluminaFilepathGenerator implements FilePathGenerator {
  protected static final Logger log = LoggerFactory.getLogger(TGACIlluminaFilepathGenerator.class);

  String basePath = "";

  public TGACIlluminaFilepathGenerator() {
  }

  public TGACIlluminaFilepathGenerator(String basePath) {
    this.basePath = basePath;
  }

  public void setBaseReadPath(String basePath) {
    this.basePath = basePath;
  }

  @Override
  public Stream<File> generateFilePath(Library library, Partition partition, Stream<Experiment> experiments) {
    String indexSlug = library.getIndices().isEmpty() ? ""
        : library.getIndices().stream().map(Index::getSequence).collect(Collectors.joining("_", "_", ""));
    String prefix = isStringEmptyOrNull(basePath)
        ? (partition.getSequencerPartitionContainer().getLastRun().getFilePath() + "/Data/Intensities/BaseCalls/PAP/Project_")
        : (basePath + "/");
    return experiments
        .filter(experiment -> experiment.getLibrary().equals(library)
            && experiment.getRunPartitions().stream().anyMatch(rp -> rp.getPartition().getId() == partition.getId()))
        .map(experiment -> String.format("%1$s%2$s/Sample_%3$s/%3$s%4$s_L00%5$d*.fastq.gz", prefix,
            experiment.getStudy().getProject().getAlias(),
            library.getName(), indexSlug, partition.getPartitionNumber()))
        .map(File::new);
  }

  @Override
  public String getName() {
    return "TGAC Illumina File Path Generator";
  }

  @Override
  public PlatformType generatesFilePathsFor() {
    return PlatformType.ILLUMINA;
  }
}
