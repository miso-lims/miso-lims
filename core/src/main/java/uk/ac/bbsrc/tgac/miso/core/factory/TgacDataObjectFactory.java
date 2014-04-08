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

package uk.ac.bbsrc.tgac.miso.core.factory;

import com.eaglegenomics.simlims.core.Group;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.pacbio.PacBioPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.pacbio.PacBioRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidRun;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

import java.util.LinkedList;
import java.util.List;

/**
 * uk.ac.bbsrc.tgac.miso.core.factory
 * <p/>
 * TODO Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class TgacDataObjectFactory extends DataObjectFactory {
  @Override
  public User getUser() {
    return new UserImpl();
  }

  @Override
  public Group getGroup() {
    return new Group();
  }

  @Override
  public Project getProject() {
    return new ProjectImpl();
  }

  public Project getProject(User user) {
    return new ProjectImpl(user);
  }  

  @Override
  public Study getStudy() {
    return new StudyImpl();
  }

  @Override
  public Study getStudy(User user) {
    return new StudyImpl(user);
  }

  @Override
  public Experiment getExperiment() {
    return new ExperimentImpl();
  }

  @Override
  public Experiment getExperiment(User user) {
    return new ExperimentImpl(user);
  }

  @Override
  public Sample getSample() {
    return new SampleImpl();
  }

  @Override
  public Run getRun() {
    return new RunImpl();
  }

  @Override
  public Library getLibrary() {
    return new LibraryImpl();
  }

  @Override
  public Library getLibrary(User user) {
    return new LibraryImpl(user);
  }

  @Override
  public LibraryDilution getLibraryDilution() {
    return new LibraryDilution();
  }

  @Override
  public LibraryDilution getLibraryDilution(User user) {
    return new LibraryDilution(user);
  }

  @Override
  public emPCRDilution getEmPCRDilution() {
    return new emPCRDilution();
  }

  @Override
  public emPCRDilution getEmPCRDilution(User user) {
    return new emPCRDilution(user);
  }

  @Override
  public emPCR getEmPCR() {
    return new emPCR();
  }

  @Override
  public emPCR getEmPCR(User user) {
    return new emPCR(user);
  }

  @Override
  //public <T extends List<S>, S extends Plateable> Plate<T, S> getPlateOfSize(int size) {
  public Plate<LinkedList<Plateable>, Plateable> getPlateOfSize(int size) {
    return new PlateImpl<Plateable>(size);
  }

  @Override
  public Plate<LinkedList<Plateable>, Plateable> getPlateOfSize(int size, User user) {
    return new PlateImpl<Plateable>(size, user);
  }

  @Override
  public Pool<? extends Poolable> getPool() {
    return new PoolImpl<Poolable>();
  }

  public Pool<? extends Poolable> getPool(User user) {
    return new PoolImpl<Poolable>(user);
  }

  @Override
  @Deprecated
  public IlluminaPool getIlluminaPool() {
    return new IlluminaPool();
  }

  @Deprecated
  public IlluminaPool getIlluminaPool(User user) {
    return new IlluminaPool(user);
  }

  @Override
  @Deprecated
  public LS454Pool getLS454Pool() {
    return new LS454Pool();
  }

  @Deprecated
  public LS454Pool getLS454Pool(User user) {
    return new LS454Pool(user);
  }

  @Override
  @Deprecated
  public SolidPool getSolidPool() {
    return new SolidPool();
  }

  @Deprecated
  public SolidPool getSolidPool(User user) {
    return new SolidPool(user);
  }

  @Override
  @Deprecated
  public PacBioPool getPacBioPool() {
    return new PacBioPool();
  }

  @Deprecated
  public PacBioPool getPacBioPool(User user) {
    return new PacBioPool(user);
  }

  @Override
  @Deprecated
  public emPCRPool getEmPCRPool(PlatformType platformType) {
    return new emPCRPool(platformType);
  }

  @Deprecated
  public emPCRPool getEmPCRPool(PlatformType platformType, User user) {
    return new emPCRPool(user, platformType);
  }

  @Override
  public SampleQC getSampleQC() {
    return new SampleQCImpl();
  }

  @Override
  public LibraryQC getLibraryQC() {
    return new LibraryQCImpl();
  }

  @Override
  public PoolQC getPoolQC() {
    return new PoolQCImpl();
  }

  @Override
  public RunQC getRunQC() {
    return new RunQCImpl();
  }

  @Override
  public Status getStatus() {
    return new StatusImpl();
  }

  @Override
  public SequencerReference getSequencerReference() {
    return new SequencerReferenceImpl(null, null, null);
  }

  public Library getLibrary(Sample sample, User user) {
    if (sample.userCanWrite(user)) {
      return new LibraryImpl(sample, user);
    }
    else {
      throw new SecurityException();
    }
  }

  public Study getStudy(Project project, User user) {
    if (project.userCanWrite(user)) {
      return new StudyImpl(project, user);
    }
    else {
      throw new SecurityException();
    }
  }

  public Experiment getExperiment(Study study, User user) {
    if (study.userCanWrite(user)) {
      return new ExperimentImpl(study, user);
    }
    else {
      throw new SecurityException();
    }
  }

  public Sample getSample(Project project, User user) {
    if (project.userCanWrite(user)) {
      return new SampleImpl(project, user);
    }
    else {
      throw new SecurityException();
    }
  }

  public Sample getSample(User user) {
    return new SampleImpl(user);
  }

  public SampleQC getSampleQC(Sample sample, User user) {
    if (sample.userCanWrite(user)) {
      return new SampleQCImpl(sample, user);
    }
    else {
      throw new SecurityException();
    }
  }

  public LibraryQC getLibraryQC(Library library, User user) {
    if (library.userCanWrite(user)) {
      return new LibraryQCImpl(library, user);
    }
    else {
      throw new SecurityException();
    }
  }

  public Run getRun(Experiment experiment, User user) {
    if (experiment.userCanWrite(user)) {
      return new RunImpl(experiment, user);
    }
    else {
      throw new SecurityException();
    }
  }

  public Run getRun(User user) {
    return new RunImpl(user);
  }

  public Run getIlluminaRun() {
    return new IlluminaRun();
  }

  public Run getIlluminaRun(User user) {
    return new IlluminaRun(user);
  }

  public Run getIlluminaRun(Experiment experiment, User user) {
    if (experiment.userCanWrite(user)) {
      return new IlluminaRun(user);
    }
    else {
      throw new SecurityException();
    }
  }

  public Run getLs454Run() {
    return new LS454Run();
  }

  public Run getLs454Run(User user) {
    return new LS454Run(user);
  }

  public Run getLs454Run(Experiment experiment, User user) {
    if (experiment.userCanWrite(user)) {
      return new LS454Run(user);
    }
    else {
      throw new SecurityException();
    }
  }

  public Run getSolidRun() {
    return new SolidRun();
  }

  public Run getSolidRun(User user) {
    return new SolidRun(user);
  }

  public Run getSolidRun(Experiment experiment, User user) {
    if (experiment.userCanWrite(user)) {
      return new SolidRun(user);

    }
    else {
      throw new SecurityException();
    }
  }

  public Run getPacBioRun() {
    return new PacBioRun();
  }

  public Run getPacBioRun(User user) {
    return new PacBioRun(user);
  }

  public Run getPacBioRun(Experiment experiment, User user) {
    if (experiment.userCanWrite(user)) {
      return new PacBioRun(user);

    }
    else {
      throw new SecurityException();
    }
  }
  
  public Pool<? extends Poolable> getPoolOfType(PlatformType platformtype, User user) throws IllegalArgumentException {
    if (platformtype != null) {
      Pool<? extends Poolable> p = getPool(user);
      p.setPlatformType(platformtype);
      return p;
      /*
      if (platformtype.equals(PlatformType.ILLUMINA)) {
        return getIlluminaPool(user);
      }
      else if (platformtype.equals(PlatformType.LS454)) {
        return getLS454Pool(user);
      }
      else if (platformtype.equals(PlatformType.SOLID)) {
        return getSolidPool(user);
      }
      else if (platformtype.equals(PlatformType.PACBIO)) {
        return getPacBioPool(user);
      }
      else {
        throw new IllegalArgumentException("Unrecognised PlatformType");
      }
      */
    }
    else {
      throw new IllegalArgumentException("Null PlatformType supplied");
    }
  }

  public Run getRunOfType(PlatformType platformtype) throws IllegalArgumentException {
    if (platformtype != null) {
      if (platformtype.equals(PlatformType.ILLUMINA)) {
        return getIlluminaRun();
      }
      else if (platformtype.equals(PlatformType.LS454)) {
        return getLs454Run();
      }
      else if (platformtype.equals(PlatformType.SOLID)) {
        return getSolidRun();
      }
      else if (platformtype.equals(PlatformType.PACBIO)) {
        return getPacBioRun();
      }
      else {
        throw new IllegalArgumentException("Unrecognised PlatformType");
      }
    }
    else {
      throw new IllegalArgumentException("Null PlatformType supplied");
    }
  }

  public Run getRunOfType(PlatformType platformtype, User user) throws IllegalArgumentException {
    if (platformtype.equals(PlatformType.ILLUMINA)) {
      return getIlluminaRun(user);
    }
    else if (platformtype.equals(PlatformType.LS454)) {
      return getLs454Run(user);
    }
    else if (platformtype.equals(PlatformType.SOLID)) {
      return getSolidRun(user);
    }
    else if (platformtype.equals(PlatformType.PACBIO)) {
      return getPacBioRun(user);
    }
    else {
      throw new IllegalArgumentException("Unrecognised PlatformType");
    }
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainer() {
    return new SequencerPartitionContainerImpl();
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainer(User user) {
    return new SequencerPartitionContainerImpl(user);
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainer(Platform platformType) {
    SequencerPartitionContainer<SequencerPoolPartition> s = new SequencerPartitionContainerImpl();
    s.setPlatform(platformType);
    return s;
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainer(Platform platformType, User user) {
    SequencerPartitionContainer<SequencerPoolPartition> s = new SequencerPartitionContainerImpl(user);
    s.setPlatform(platformType);
    return s;
  }

  @Override
  public SequencerPoolPartition getSequencerPoolPartition() {
    return new PartitionImpl();
  }

  public Run getRunOfType(PlatformType platformtype, Experiment experiment, User user) throws IllegalArgumentException {
    if (platformtype.equals(PlatformType.ILLUMINA)) {
      return getIlluminaRun(experiment, user);
    }
    else if (platformtype.equals(PlatformType.LS454)) {
      return getLs454Run(experiment, user);
    }
    else if (platformtype.equals(PlatformType.SOLID)) {
      return getSolidRun(experiment, user);
    }
    else if (platformtype.equals(PlatformType.PACBIO)) {
      return getPacBioRun(user);
    }
    else {
      throw new IllegalArgumentException("Unrecognised PlatformType");
    }
  }

  public SubmissionImpl getSubmission() {
    return new SubmissionImpl();
  }

  public SubmissionImpl getSubmission(User user) {
    return new SubmissionImpl(user);
  }
}
