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

package uk.ac.bbsrc.tgac.miso.core.service.submission;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;

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
  public Set<File> generateFilePath(SequencerPoolPartition partition, Dilution l) throws SubmissionException {
    Pool<? extends Poolable> pool = partition.getPool();
    if (pool != null) {
      if (pool.getExperiments() != null) {
        Collection<Experiment> experiments = pool.getExperiments();
        Experiment experiment = experiments.iterator().next();
        StringBuilder filePath = new StringBuilder();
        if (!isStringEmptyOrNull(basePath)) {
          filePath.append(partition.getSequencerPartitionContainer().getRun().getFilePath() + "/Data/Intensities/BaseCalls/PAP/Project_"
              + experiment.getStudy().getProject().getAlias() + "/Sample_" + l.getLibrary().getName() + "/" + l.getLibrary().getName());
        } else {
          filePath.append(basePath + "/" + experiment.getStudy().getProject().getAlias() + "/Sample_" + l.getLibrary().getName() + "/"
              + l.getLibrary().getName());
        }
        if (l.getLibrary().getTagBarcodes() != null && !l.getLibrary().getTagBarcodes().isEmpty()) {
          filePath.append("_");
          for (TagBarcode tb : l.getLibrary().getTagBarcodes().values()) {
            filePath.append(tb.getSequence());
          }
        }
        filePath.append("_L00" + partition.getPartitionNumber() + "*.fastq.gz");
        Set<File> files = new HashSet<File>();
        files.add(new File(filePath.toString()));
        return files;
      } else {
        throw new SubmissionException("partition.getPool=null!");
      }
    } else {
      throw new SubmissionException("Collection of experiments is empty");
    }
  }

  @Override
  public Set<File> generateFilePaths(SequencerPoolPartition partition) throws SubmissionException {
    Set<File> filePaths = new HashSet<File>();
    if ((partition.getSequencerPartitionContainer().getRun().getFilePath()) == null) {
      throw new SubmissionException("No valid run filepath!");
    }

    Pool<? extends Poolable> pool = partition.getPool();
    if (pool == null) {
      throw new SubmissionException("partition.getPool=null!");
    } else {
      Collection<Experiment> experiments = pool.getExperiments();
      if (experiments.isEmpty()) {
        throw new SubmissionException("Collection or experiments is empty");
      } else {
        Collection<? extends Dilution> libraryDilutions = pool.getDilutions();
        if (libraryDilutions.isEmpty()) {
          throw new SubmissionException("Collection of libraryDilutions is empty");
        } else {
          for (Dilution l : libraryDilutions) {
            Set<File> files = generateFilePath(partition, l);
            filePaths.addAll(files);
          }
        }
      }
    }
    return filePaths;
  }

  @Override
  public String getName() {
    return "TGAC Illumina File Path Generator";
  }

  @Override
  public PlatformType generatesFilePathsFor() {
    return PlatformType.ILLUMINA;
  }

  private class IlluminaFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      Pattern pattern = Pattern.compile("LIB|SAM[\\d]+_[ACTG]+_L00[\\d]{1}_.*\\.fastq.gz");
      Matcher m = pattern.matcher(name);
      return m.matches();
    }
  }
}
