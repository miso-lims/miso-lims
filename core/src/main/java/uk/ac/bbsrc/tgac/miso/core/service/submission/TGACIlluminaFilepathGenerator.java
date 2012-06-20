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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: collesa
 * Date: 04/04/12
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class TGACIlluminaFilepathGenerator implements FilePathGenerator {
  @Autowired
  private RequestManager requestManager;

  protected static final Logger log = LoggerFactory.getLogger(TGACIlluminaFilepathGenerator.class);

  @Override
  public File generateFilePath(SequencerPoolPartition partition, LibraryDilution l) throws SubmissionException {
    log.debug("Generating filepaths for partition " + partition.getId());

    Pool pool = partition.getPool();
    if (pool != null) {
      if (pool.getExperiments() != null) {

        Collection<Experiment> experiments = pool.getExperiments();
        Experiment experiment = experiments.iterator().next();
        //String filePath = lane.getFlowcell().getRun().getFilePath()+"/Data/Intensities/BaseCalls/PAP/Project_"+
        String filePath = partition.getSequencerPartitionContainer().getRun().getFilePath() + "/Data/Intensities/BaseCalls/PAP/Project_" +
                          experiment.getStudy().getProject().getAlias() + "/Sample_" + l.getLibrary().getName() + "/" +
                          l.getLibrary().getName() + "_" +
                          l.getLibrary().getTagBarcode().getSequence() + "_L00" + partition.getPartitionNumber() + "*.fastq.gz";
        //System.out.println(filePath);
        File file = new File(filePath);
        return (file);
      }
      else {
        throw new SubmissionException("partition.getPool=null!");
      }
    }
    else {
      throw new SubmissionException("Collection of experiments is empty");
    }
  }

  @Override
  public Set<File> generateFilePaths(SequencerPoolPartition partition) throws SubmissionException {
    log.debug("Generating filepaths for partition " + partition.getId());
    Set<File> filePaths = new HashSet<File>();

    Pool pool = partition.getPool();
    if (pool == null) {
      throw new SubmissionException("partition.getPool=null!");
    }
    else {
      Collection<Experiment> experiments = pool.getExperiments();
      if (experiments.isEmpty()) {
        throw new SubmissionException("Collection or experiments is empty");
      }
      else {
        Collection<LibraryDilution> libraryDilutions = pool.getDilutions();
        if (libraryDilutions.isEmpty()) {
          throw new SubmissionException("Collection or libraryDilutions is empty");
        }
        else {
          for (Experiment e : experiments) {
            StringBuilder filePath = new StringBuilder();

            filePath.append(partition.getSequencerPartitionContainer().getRun().getFilePath());
            filePath.append("/Data/Intensities/BaseCalls/PAP/Project_");
            filePath.append(e.getStudy().getProject().getAlias());
            filePath.append("/Sample_");

            for (LibraryDilution l : libraryDilutions) {
              //filePath.append(l.getLibrary().getName()+"/");
              /*
                      +l.getLibrary().getName()+"_"+l.getLibrary().getTagBarcode().getSequence());
              filePath.append("L00"+lane.getPartitionNumber())
              */
              String folder = filePath.toString() + l.getLibrary().getName() + "/*.fastq.gz";
              //System.out.println(folder);
              File file = new File(folder);
              filePaths.add(file);
            }
          }
        }
      }
    }
    return (filePaths);
  }

  private class IlluminaFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
      Pattern pattern = Pattern.compile("LIB[\\d]+_[ACTG]+_L00[\\d]{1}_.*\\.fastq.gz");
      Matcher m = pattern.matcher(name);
      return m.matches();
    }
  }
}
