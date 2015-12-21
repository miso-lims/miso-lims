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

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eaglegenomics.simlims.core.User;

import junit.framework.Assert;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */

public class LimsDAO extends LimsDAOTestCase {
  protected static final Logger log = LoggerFactory.getLogger(LimsDAO.class);

  @Test
  public void testUsers() {
    try {
      int expected = getDataSet().getTable("User").getRowCount();
      int actual = getSecurityDAO().listAllUsers().size();
      for (User u : getSecurityDAO().listAllUsers()) {
        System.out.println(u.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private <T> Collection<T> random(Store<T> s, int rows, int sampleSize) {
    if (rows > 0) {
      Set<T> ret = new HashSet<T>();
      Random r = new Random();
      for (int i = 0; i < sampleSize; i++) {
        try {
          int rand = r.nextInt(rows);
          if (rand != 0) {
            T t;
            try {
              Method lazy = s.getClass().getDeclaredMethod("lazyGet", Long.TYPE);
              t = (T) lazy.invoke(s, new Long(rand));
            } catch (NoSuchMethodException e) {
              System.out.println("WARN:: Unable to lazily get object. Using full get.");
              t = s.get(Integer.valueOf(rand).longValue());
            } catch (InvocationTargetException e) {
              System.out.println("WARN:: Unable to lazily get object. Using full get.");
              t = s.get(Integer.valueOf(rand).longValue());
            } catch (IllegalAccessException e) {
              System.out.println("WARN:: Unable to lazily get object. Using full get.");
              t = s.get(Integer.valueOf(rand).longValue());
            }

            if (t != null) {
              ret.add(t);
            }
          }
        } catch (IOException e) {
          System.out.println("ERROR:: could not get random object from store");
        }
      }
      return ret;
    } else {
      return Collections.emptySet();
    }
  }

  @Test
  public void testDilutions() {
    try {
      int ec = getDataSet().getTable("emPCRDilution").getRowCount();
      int lc = getDataSet().getTable("LibraryDilution").getRowCount();

      int expected = ec + lc;
      int actualL = getEmPCRDilutionDAO().count();
      int actualE = getLibraryDilutionDAO().count();
      Assert.assertEquals("Wrong number of dilutions", expected, (actualL + actualE));
      System.out.println("Expected number of dilutions: " + expected + ", actual: " + (actualL + actualE));

      ((MockSQLSecurityDAO) getSecurityDAO()).clearCaches();
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testEmPCRs() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("emPCR").getRowCount();

      // get number of experiments from the DAO
      int actual = getEmPCRDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of emPCRs", expected, actual);

      System.out.println("Expected number of emPCRs: " + expected + ", actual: " + actual);

      for (emPCR d : random(getEmPCRDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testExperiments() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Experiment").getRowCount();

      // get number of experiments from the DAO
      int actual = getExperimentDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of experiments", expected, actual);

      System.out.println("Expected number of experiments: " + expected + ", actual: " + actual);

      for (Experiment d : random(getExperimentDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testSequencerPartitionContainers() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("SequencerPartitionContainer").getRowCount();

      // get number of experiments from the DAO
      int actual = getSequencerPartitionContainerDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of containers", expected, actual);

      System.out.println("Expected number of containers: " + expected + ", actual: " + actual);

      for (SequencerPartitionContainer d : random(getSequencerPartitionContainerDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testKits() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Kit").getRowCount();

      // get number of experiments from the DAO
      int actual = getKitDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of kits", expected, actual);

      System.out.println("Expected number of kits: " + expected + ", actual: " + actual);

      for (Kit d : random(getKitDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testPartitions() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("_Partition").getRowCount();

      // get number of experiments from the DAO
      int actual = getPartitionDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of partitions", expected, actual);

      System.out.println("Expected number of partitions: " + expected + ", actual: " + actual);

      for (Partition d : random(getPartitionDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testLibraries() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Library").getRowCount();

      // get number of experiments from the DAO
      int actual = getLibraryDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of Library", expected, actual);

      System.out.println("Expected number of Library: " + expected + ", actual: " + actual);

      for (Library d : random(getLibraryDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testLibraryQCs() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("LibraryQC").getRowCount();

      // get number of experiments from the DAO
      int actual = getLibraryQcDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of LibraryQC", expected, actual);

      System.out.println("Expected number of LibraryQC: " + expected + ", actual: " + actual);

      for (LibraryQC d : random(getLibraryQcDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testPlatforms() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Platform").getRowCount();

      // get number of experiments from the DAO
      int actual = getPlatformDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of Platform", expected, actual);
      System.out.println("Expected number of Platform: " + expected + ", actual: " + actual);

      for (Platform d : random(getPlatformDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getPlatformId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testPools() {
    try {
      int expected = getDataSet().getTable("Pool").getRowCount();

      int actual = getPoolDAO().count();

      Assert.assertEquals("Wrong number of Pools", expected, actual);
      System.out.println("Expected number of Pools: " + expected + ", actual: " + actual);

      for (Pool d : random(getPoolDAO(), actual, 1)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testProjects() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Project").getRowCount();

      // get number of experiments from the DAO
      int actual = getProjectDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of Project", expected, actual);

      System.out.println("Expected number of Project: " + expected + ", actual: " + actual);

      for (Project d : random(getProjectDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getProjectId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testRuns() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Run").getRowCount();

      // get number of experiments from the DAO
      int actual = getRunDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of Run", expected, actual);

      System.out.println("Expected number of Run: " + expected + ", actual: " + actual);

      for (Run d : random(getRunDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testRunQCs() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("RunQC").getRowCount();

      // get number of experiments from the DAO
      int actual = getRunQcDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of RunQC", expected, actual);

      System.out.println("Expected number of RunQC: " + expected + ", actual: " + actual);

      for (RunQC d : random(getRunQcDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testSamples() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Sample").getRowCount();

      // get number of experiments from the DAO
      int actual = getSampleDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of Sample", expected, actual);

      System.out.println("Expected number of Sample: " + expected + ", actual: " + actual);

      for (Sample d : random(getSampleDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testSampleQCs() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("SampleQC").getRowCount();

      // get number of experiments from the DAO
      int actual = getSampleQcDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of SampleQC", expected, actual);

      System.out.println("Expected number of SampleQC: " + expected + ", actual: " + actual);

      for (SampleQC d : random(getSampleQcDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testSequencerReferences() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("SequencerReference").getRowCount();

      // get number of experiments from the DAO
      int actual = getSequencerReferenceDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of SequencerReference", expected, actual);

      System.out.println("Expected number of SequencerReference: " + expected + ", actual: " + actual);

      for (SequencerReference d : random(getSequencerReferenceDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testStatus() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Status").getRowCount();

      // get number of experiments from the DAO
      int actual = getStatusDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of Status", expected, actual);

      System.out.println("Expected number of Status: " + expected + ", actual: " + actual);

      for (Status d : random(getStatusDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getStatusId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  @Test
  public void testStudies() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Study").getRowCount();

      // get number of experiments from the DAO
      int actual = getStudyDAO().count();

      // test data contains 2 experiments, check size of returned list
      Assert.assertEquals("Wrong number of Study", expected, actual);

      System.out.println("Expected number of Study: " + expected + ", actual: " + actual);

      for (Study d : random(getStudyDAO(), actual, 5)) {
        Assert.assertNotNull(d);
        Assert.assertNotNull(d.getId());
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }
}
