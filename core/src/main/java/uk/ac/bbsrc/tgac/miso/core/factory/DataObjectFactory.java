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

import java.util.LinkedList;

import com.eaglegenomics.simlims.core.Group;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.pacbio.PacBioPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidPool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * uk.ac.bbsrc.tgac.miso.core.factory
 * <p/>
 * Provides the basis of an Object Factory needed by MISO
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class DataObjectFactory {
  public abstract User getUser();

  public abstract Group getGroup();

  public abstract Project getProject();

  public abstract Project getProject(User user);

  public abstract Study getStudy();

  public abstract Study getStudy(User user);

  public abstract Experiment getExperiment();

  public abstract Experiment getExperiment(User user);

  public abstract Sample getSample();

  public abstract Sample getSample(User user);

  public abstract SampleQC getSampleQC();

  public abstract LibraryQC getLibraryQC();

  public abstract Run getRun();

  public abstract Run getRun(User user);

  public abstract RunQC getRunQC();

  public abstract Run getRunOfType(PlatformType platformType);

  public abstract Run getRunOfType(PlatformType platformType, User user);

  public abstract SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainer();

  public abstract SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainer(User user);

  public abstract SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainer(Platform platformType);

  public abstract SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainer(Platform platformType, User user);

  public abstract SequencerPoolPartition getSequencerPoolPartition();

  public abstract Library getLibrary();

  public abstract Library getLibrary(User user);

  public abstract LibraryDilution getLibraryDilution();

  public abstract LibraryDilution getLibraryDilution(User user);

  public abstract emPCRDilution getEmPCRDilution();

  public abstract emPCRDilution getEmPCRDilution(User user);

  public abstract emPCR getEmPCR();

  public abstract emPCR getEmPCR(User user);

  public abstract Plate<LinkedList<Plateable>, Plateable> getPlateOfSize(int size);

  public abstract Plate<LinkedList<Plateable>, Plateable> getPlateOfSize(int size, User user);

  public abstract Pool<? extends Poolable> getPool();

  public abstract Pool<? extends Poolable> getPool(User user);

  public abstract Pool<? extends Poolable> getPoolOfType(PlatformType platformType, User user);

  public abstract PoolQC getPoolQC();

  @Deprecated
  public abstract IlluminaPool getIlluminaPool();

  @Deprecated
  public abstract IlluminaPool getIlluminaPool(User user);

  @Deprecated
  public abstract LS454Pool getLS454Pool();

  @Deprecated
  public abstract LS454Pool getLS454Pool(User user);

  @Deprecated
  public abstract SolidPool getSolidPool();

  @Deprecated
  public abstract SolidPool getSolidPool(User user);

  @Deprecated
  public abstract PacBioPool getPacBioPool();

  @Deprecated
  public abstract PacBioPool getPacBioPool(User user);

  @Deprecated
  public abstract emPCRPool getEmPCRPool(PlatformType platformType);

  @Deprecated
  public abstract emPCRPool getEmPCRPool(PlatformType platformType, User user);

  public abstract Status getStatus();

  public abstract SequencerReference getSequencerReference();

  public abstract Submission getSubmission();

  public abstract Submission getSubmission(User user);

  public abstract Box getBox();

  public abstract Box getBox(User user);
}
