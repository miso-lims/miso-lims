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

import com.eaglegenomics.simlims.core.User;
import junit.framework.TestCase;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  //@Test
  public void testListAllObjects() {
    try {
      TestCase.assertNotNull(getDilutionDAO().listAll());
      TestCase.assertNotNull(getEmPCRDAO().listAll());
      TestCase.assertNotNull(getExperimentDAO().listAll());
      TestCase.assertNotNull(getSequencerPartitionContainerDAO().listAll());
      TestCase.assertNotNull(getKitDAO().listAll());
      TestCase.assertNotNull(getPartitionDAO().listAll());
      TestCase.assertNotNull(getLibraryDAO().listAll());
      TestCase.assertNotNull(getLibraryQcDAO().listAll());
      TestCase.assertNotNull(getNoteDAO().listAll());
      TestCase.assertNotNull(getPlatformDAO().listAll());
      TestCase.assertNotNull(getPoolDAO().listAll());
      TestCase.assertNotNull(getProjectDAO().listAll());
      TestCase.assertNotNull(getRunDAO().listAll());
      TestCase.assertNotNull(getRunQcDAO().listAll());
      TestCase.assertNotNull(getSampleDAO().listAll());
      TestCase.assertNotNull(getSampleQcDAO().listAll());
      TestCase.assertNotNull(getSequencerReferenceDAO().listAll());
      TestCase.assertNotNull(getSecurityDAO().listAllUsers());
      TestCase.assertNotNull(getSecurityDAO().listAllGroups());
      TestCase.assertNotNull(getSecurityProfileDAO().listAll());
      TestCase.assertNotNull(getStatusDAO().listAll());
      TestCase.assertNotNull(getStudyDAO().listAll());
      TestCase.assertNotNull(getSubmissionDAO().listAll());
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllUsers() {
    try {
      int expected = getDataSet().getTable("User").getRowCount();
      int actual = getSecurityDAO().listAllUsers().size();
      for(User u:getSecurityDAO().listAllUsers()) {
        System.out.println(u.toString());
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetAllDilutions() {
    try {
      int emExpected = getDataSet().getTable("emPCRDilution").getRowCount();

      //int ls454Actual = getDilutionDAO().listAllDilutionsByPlatform(PlatformType.LS454).size();
      //int solidActual = getDilutionDAO().listAllDilutionsByPlatform(PlatformType.Solid).size();
      //assertEquals("Wrong number of emPCRDilutions", emExpected, (ls454Actual+solidActual));
      //System.out.println("Expected number of emPCR (Solid+454) dilutions: " + emExpected + ", actual: " + (ls454Actual+solidActual));

      int emPcrActual = getDilutionDAO().listAllEmPcrDilutions().size();
      TestCase.assertEquals("Wrong number of emPCRDilutions", emExpected, emPcrActual);
      System.out.println("Expected number of emPCR (Solid+454) dilutions: " + emExpected + ", actual: " + emPcrActual);

      int libExpected = getDataSet().getTable("LibraryDilution").getRowCount();
      //int libActual = getDilutionDAO().listAllDilutionsByPlatform(PlatformType.Illumina).size();
      int libActual = getDilutionDAO().listAllLibraryDilutions().size();
      TestCase.assertEquals("Wrong number of LibraryDilutions", libExpected, libActual);
      System.out.println(
              "Expected number of Library (Illumina) dilutions: " + libExpected + ", actual: " + libActual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllEmPCRs() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("emPCR").getRowCount();

      // get number of experiments from the DAO
      int actual = getEmPCRDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of emPCRs", expected, actual);

      System.out.println(
              "Expected number of emPCRs: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllExperiments() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Experiment").getRowCount();

      // get number of experiments from the DAO
      int actual = getExperimentDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of experiments", expected, actual);

      System.out.println(
              "Expected number of experiments: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllSequencerPartitionContainers() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("SequencerPartitionContainer").getRowCount();

      // get number of experiments from the DAO
      int actual = getSequencerPartitionContainerDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of containers", expected, actual);

      System.out.println(
              "Expected number of containers: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllKits() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Kit").getRowCount();

      // get number of experiments from the DAO
      int actual = getKitDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of kits", expected, actual);

      System.out.println(
              "Expected number of kits: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllPartitions() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Partition").getRowCount();

      // get number of experiments from the DAO
      int actual = getPartitionDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of partitions", expected, actual);

      System.out.println(
              "Expected number of partitions: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllLibraries() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Library").getRowCount();

      // get number of experiments from the DAO
      int actual = getLibraryDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of Library", expected, actual);

      System.out.println(
              "Expected number of Library: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllLibraryQCs() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("LibraryQC").getRowCount();

      // get number of experiments from the DAO
      int actual = getLibraryQcDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of LibraryQC", expected, actual);

      System.out.println(
              "Expected number of LibraryQC: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllPlatforms() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Platform").getRowCount();

      // get number of experiments from the DAO
      int actual = getPlatformDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of Platform", expected, actual);
      System.out.println("Expected number of Platform: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllPools() {
    try {
      int expected = getDataSet().getTable("Pool").getRowCount();

      int actual = getPoolDAO().listAll().size();

      TestCase.assertEquals("Wrong number of Pools", expected, actual);
      System.out.println("Expected number of Pools: " + expected + ", actual: " + actual);      
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllProjects() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Project").getRowCount();

      // get number of experiments from the DAO
      int actual = getProjectDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of Project", expected, actual);

      System.out.println(
              "Expected number of Project: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllRuns() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Run").getRowCount();

      // get number of experiments from the DAO
      int actual = getRunDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of Run", expected, actual);

      System.out.println(
              "Expected number of Run: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllRunQCs() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("RunQC").getRowCount();

      // get number of experiments from the DAO
      int actual = getRunQcDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of RunQC", expected, actual);

      System.out.println(
              "Expected number of RunQC: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllSamples() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Sample").getRowCount();

      // get number of experiments from the DAO
      int actual = getSampleDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of Sample", expected, actual);

      System.out.println(
              "Expected number of Sample: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllSampleQCs() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("SampleQC").getRowCount();

      // get number of experiments from the DAO
      int actual = getSampleQcDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of SampleQC", expected, actual);

      System.out.println(
              "Expected number of SampleQC: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllSequencerReferences() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("SequencerReference").getRowCount();

      // get number of experiments from the DAO
      int actual = getSequencerReferenceDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of SequencerReference", expected, actual);

      System.out.println(
              "Expected number of SequencerReference: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllStatus() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Status").getRowCount();

      // get number of experiments from the DAO
      int actual = getStatusDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of Status", expected, actual);

      System.out.println(
              "Expected number of Status: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllStudies() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Study").getRowCount();

      // get number of experiments from the DAO
      int actual = getStudyDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of Study", expected, actual);

      System.out.println(
              "Expected number of Study: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }

  @Test
  public void testGetAllSubmissions() {
    try {
      // get row count of experiments in the dataset
      int expected = getDataSet().getTable("Submission").getRowCount();

      // get number of experiments from the DAO
      int actual = getSubmissionDAO().listAll().size();

      // test data contains 2 experiments, check size of returned list
      TestCase.assertEquals("Wrong number of submissions", expected, actual);

      System.out.println(
              "Expected number of submissions: " + expected + ", actual: " + actual);
    }
    catch (Exception e) {
      e.printStackTrace();
      TestCase.fail();
    }
  }
}
