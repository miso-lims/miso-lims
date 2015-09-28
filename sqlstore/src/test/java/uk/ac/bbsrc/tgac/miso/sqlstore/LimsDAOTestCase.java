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

import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;
import org.dbunit.DatabaseTestCase;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.BarcodePrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintContextResolverService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DaoLookup;

import javax.persistence.CascadeType;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Set up an in memory (hypersonic) database, and creating a DBUnit environment for
 * testing DAO methods.
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class LimsDAOTestCase extends DatabaseTestCase {
  private QueryDataSet dataSet;

  private static final String DRIVER = "org.hsqldb.jdbcDriver";
  private static final String URL = "jdbc:hsqldb:mem:miso";
  private static final String USER = "sa";
  private static final String PASSWORD = "";

  private static final String[] tables =
  {
    "Alert",
    "EntityGroup",
    "EntityGroup_Elements",
    "Experiment",
    "Experiment_Kit",
    "Kit",
    "KitDescriptor",
    "Kit_Note",
    "Library",
    "LibraryDilution",
    "LibraryQC",
    "LibrarySelectionType",
    "LibraryStrategyType",
    "LibraryType",
    "Library_Note",
    "Library_TagBarcode",
    "Note",
    "_Partition",
    "Plate",
    "Plate_Elements",
    "Platform",
    "Pool",
    "PoolQC",
    "Pool_Elements",
    "Pool_Experiment",
    "PrintJob",
    "PrintService",
    "Project",
    "ProjectOverview",
    "ProjectOverview_Note",
    "Project_Issues",
    "Project_Note",
    "Project_ProjectOverview",
    "Project_Request",
    "Project_Study",
    "QCType",
    "Request",
    "Request_Note",
    "Run",
    "RunQC",
    "RunQC_Partition",
    "Run_Note",
    "Sample",
    "SampleQC",
    "SampleType",
    "Sample_Note",
    "SecurityProfile",
    "SecurityProfile_ReadGroup",
    "SecurityProfile_ReadUser",
    "SecurityProfile_WriteGroup",
    "SecurityProfile_WriteUser",
    "SequencerPartitionContainer",
    "SequencerPartitionContainer_Partition",
    "SequencerReference",
    "Status",
    "Study",
    "StudyType",
    "Study_Experiment",
    "Submission",
    "Submission_Experiment",
    "Submission_Partition_Dilution",
    "Submission_Sample",
    "Submission_Study",
    "TagBarcodes",
    "User",
    "User_Group",
    "Watcher",
    "_Group",
    "emPCR",
    "emPCRDilution",
  };

  private DataSource datasource;

  private SQLAlertDAO alertDAO;
  private SQLLibraryDilutionDAO libraryDilutionDAO;
  private SQLEntityGroupDAO entityGroupDAO;
  private SQLEmPCRDAO emPCRDAO;
  private SQLEmPCRDilutionDAO emPCRDilutionDAO;
  private SQLExperimentDAO experimentDAO;
  private SQLKitDAO kitDAO;
  private SQLLibraryDAO libraryDAO;
  private SQLLibraryQCDAO libraryQcDAO;
  private SQLNoteDAO noteDAO;
  private SQLSequencerPoolPartitionDAO partitionDAO;
  private SQLPlateDAO plateDAO;
  private SQLPlatformDAO platformDAO;
  private SQLPoolDAO poolDAO;
  private SQLPoolQCDAO poolQcDAO;
  private SQLPrintJobDAO printJobDAO;
  private SQLPrintServiceDAO printServiceDAO;
  private SQLProjectDAO projectDAO;
  private SQLRunDAO runDAO;
  private SQLRunQCDAO runQcDAO;
  private SQLSampleDAO sampleDAO;
  private SQLSampleQCDAO sampleQcDAO;
  private SQLSecurityProfileDAO securityProfileDAO;
  private SQLSecurityDAO securityDAO;
  private SQLSequencerPartitionContainerDAO sequencerPartitionContainerDAO;
  private SQLSequencerReferenceDAO sequencerReferenceDAO;
  private SQLStatusDAO statusDAO;
  private SQLStudyDAO studyDAO;
  private SQLTgacSubmissionDAO submissionDAO;
  private SQLWatcherDAO watcherDAO;

  private DaoLookup daoLookup;

  public DataSource getDataSource() {
    if (datasource != null) {
      return datasource;
    }
    else {
      return null;
    }
  }

  public DaoLookup getDaoLookup() {
    if (daoLookup != null) {
      return daoLookup;
    }
    else {
      return null;
    }
  }

  public SQLAlertDAO getAlertDAO() {
    if (alertDAO != null) {
      return alertDAO;
    }
    else {
      return null;
    }
  }

  public SQLLibraryDilutionDAO getLibraryDilutionDAO() {
    if (libraryDilutionDAO != null) {
      return libraryDilutionDAO;
    }
    else {
      return null;
    }
  }

  public SQLEntityGroupDAO getEntityGroupDAO() {
    if (entityGroupDAO != null) {
      return entityGroupDAO;
    }
    else {
      return null;
    }
  }


  public SQLEmPCRDAO getEmPCRDAO() {
    if (emPCRDAO != null) {
      return emPCRDAO;
    }
    else {
      return null;
    }
  }

  public SQLEmPCRDilutionDAO getEmPCRDilutionDAO() {
    if (emPCRDilutionDAO != null) {
      return emPCRDilutionDAO;
    }
    else {
      return null;
    }
  }

  public SQLExperimentDAO getExperimentDAO() {
    if (experimentDAO != null) {
      return experimentDAO;
    }
    else {
      return null;
    }
  }

  public SQLKitDAO getKitDAO() {
    if (kitDAO != null) {
      return kitDAO;
    }
    else {
      return null;
    }
  }

  public SQLLibraryDAO getLibraryDAO() {
    if (libraryDAO != null) {
      return libraryDAO;
    }
    else {
      return null;
    }
  }

  public SQLLibraryQCDAO getLibraryQcDAO() {
    if (libraryQcDAO != null) {
      return libraryQcDAO;
    }
    else {
      return null;
    }
  }

  public SQLNoteDAO getNoteDAO() {
    if (noteDAO != null) {
      return noteDAO;
    }
    else {
      return null;
    }
  }

  public SQLPlateDAO getPlateDAO() {
    if (plateDAO != null) {
      return plateDAO;
    }
    else {
      return null;
    }
  }

  public SQLPlatformDAO getPlatformDAO() {
    if (platformDAO != null) {
      return platformDAO;
    }
    else {
      return null;
    }
  }

  public SQLPoolDAO getPoolDAO() {
    if (poolDAO != null) {
      return poolDAO;
    }
    else {
      return null;
    }
  }

  public SQLPoolQCDAO getPoolQcDAO() {
    if (poolQcDAO != null) {
      return poolQcDAO;
    }
    else {
      return null;
    }
  }

  public SQLProjectDAO getProjectDAO() {
    if (projectDAO != null) {
      return projectDAO;
    }
    else {
      return null;
    }
  }

  public SQLPrintJobDAO getPrintJobDAO() {
    if (printJobDAO != null) {
      return printJobDAO;
    }
    else {
      return null;
    }
  }

  public SQLPrintServiceDAO getPrintServiceDAO() {
    if (printServiceDAO != null) {
      return printServiceDAO;
    }
    else {
      return null;
    }
  }

  public SQLRunDAO getRunDAO() {
    if (runDAO != null) {
      return runDAO;
    }
    else {
      return null;
    }
  }

  public SQLRunQCDAO getRunQcDAO() {
    if (runQcDAO != null) {
      return runQcDAO;
    }
    else {
      return null;
    }
  }

  public SQLSampleDAO getSampleDAO() {
    if (sampleDAO != null) {
      return sampleDAO;
    }
    else {
      return null;
    }
  }

  public SQLSampleQCDAO getSampleQcDAO() {
    if (sampleQcDAO != null) {
      return sampleQcDAO;
    }
    else {
      return null;
    }
  }

  public SQLSecurityProfileDAO getSecurityProfileDAO() {
    if (securityProfileDAO != null) {
      return securityProfileDAO;
    }
    else {
      return null;
    }
  }

  public SQLSecurityDAO getSecurityDAO() {
    if (securityDAO != null) {
      return securityDAO;
    }
    else {
      return null;
    }
  }

  public SQLSequencerReferenceDAO getSequencerReferenceDAO() {
    if (sequencerReferenceDAO != null) {
      return sequencerReferenceDAO;
    }
    else {
      return null;
    }
  }

  public SQLStatusDAO getStatusDAO() {
    if (statusDAO != null) {
      return statusDAO;
    }
    else {
      return null;
    }
  }

  public SQLStudyDAO getStudyDAO() {
    if (studyDAO != null) {
      return studyDAO;
    }
    else {
      return null;
    }
  }

  public SQLTgacSubmissionDAO getSubmissionDAO() {
    assert submissionDAO != null;
    return submissionDAO;
  }

  public SQLWatcherDAO getWatcherDAO() {
    assert watcherDAO != null;
    return watcherDAO;
  }


  public SQLSequencerPoolPartitionDAO getPartitionDAO() {
    assert partitionDAO != null;
    return partitionDAO;
  }

  public SQLSequencerPartitionContainerDAO getSequencerPartitionContainerDAO() {
    assert sequencerPartitionContainerDAO != null;
    return sequencerPartitionContainerDAO;
  }

  @Override
  protected IDataSet getDataSet() throws Exception {
    if (dataSet == null) {
      System.out.print("Getting dataset...");
      InputStream in = LimsDAOTestCase.class.getClassLoader().getResourceAsStream("test.db.properties");
      Properties props = new Properties();
      props.load(in);
      System.out.print("properties loaded...");

      Connection jdbcConnection = DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.username"), props.getProperty("db.password"));
      IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
      DatabaseConfig config = connection.getConfig();
      config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
      System.out.print("mysql connection set...");

      dataSet = new QueryDataSet(connection);
      for (String table : tables) {
        dataSet.addTable(table);
      }
      System.out.print("dataset ready.\n");
    }
    return dataSet;
  }

  @Override
  protected IDatabaseConnection getConnection() throws Exception {
    JdbcDatabaseTester dbTester = new JdbcDatabaseTester(DRIVER, URL, USER, PASSWORD);
    dbTester.setDataSet(getDataSet());
    IDatabaseConnection connection = dbTester.getConnection();
    DatabaseConfig memConfig = connection.getConfig();
    memConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
    return connection;
  }

  /**
   * This sets up an in-memory database using Hypersonic, and uses DBUnit to dump sample data from miso-db.xml into
   * this in-memory database.  It then configures a SingleConnectionDataSource from spring to provide access to the
   * underlying DB connection.  Finally, it initialises a JdbcTemplate using this datasource, and all the relevant DAOs using
   * this template.  After setup, you should be able to use the DAOs to test method calls against the data configured
   * in the sample dataset, or add to it and check the resulting data.
   *
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    System.out.println(">>>>>>>>>>>>>> SETUP");

    InputStream in = LimsDAOTestCase.class.getClassLoader().getResourceAsStream("test.db.properties");
    Properties props = new Properties();
    props.load(in);

    datasource = new DriverManagerDataSource(props.getProperty("db.url"), props.getProperty("db.username"), props.getProperty("db.password"));

    DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();
    LobHandler lh = new DefaultLobHandler();
    JdbcTemplate template = new JdbcTemplate(datasource);

    //securityDAO = new SQLSecurityDAO();
    securityDAO = new MockSQLSecurityDAO();
    securityDAO.setLobHandler(lh);
    securityDAO.setJdbcTemplate(template);

    securityProfileDAO = new SQLSecurityProfileDAO();
    alertDAO = new SQLAlertDAO();
    libraryDilutionDAO = new SQLLibraryDilutionDAO();
    entityGroupDAO = new SQLEntityGroupDAO();
    emPCRDAO = new SQLEmPCRDAO();
    emPCRDilutionDAO = new SQLEmPCRDilutionDAO();
    experimentDAO = new SQLExperimentDAO();
    kitDAO = new SQLKitDAO();
    libraryDAO = new SQLLibraryDAO();
    libraryQcDAO = new SQLLibraryQCDAO();
    noteDAO = new SQLNoteDAO();
    partitionDAO = new SQLSequencerPoolPartitionDAO();
    plateDAO = new SQLPlateDAO();
    platformDAO = new SQLPlatformDAO();
    poolDAO = new SQLPoolDAO();
    poolQcDAO = new SQLPoolQCDAO();
    printJobDAO = new SQLPrintJobDAO();
    printServiceDAO = new SQLPrintServiceDAO();
    runDAO = new SQLRunDAO();
    runQcDAO = new SQLRunQCDAO();
    sampleDAO = new SQLSampleDAO();
    sampleQcDAO = new SQLSampleQCDAO();
    sequencerPartitionContainerDAO = new SQLSequencerPartitionContainerDAO();
    sequencerReferenceDAO = new SQLSequencerReferenceDAO();
    statusDAO = new SQLStatusDAO();
    studyDAO = new SQLStudyDAO();
    submissionDAO = new SQLTgacSubmissionDAO();
    watcherDAO = new SQLWatcherDAO();

    projectDAO = new MockSQLProjectDAO();

    Map<Class<?>, Store<?>> daos = new HashMap<Class<?>, Store<?>>();

    //just use a basic SQL auth for testing
    LocalSecurityManager sm = new LocalSecurityManager();
    sm.setSecurityStore(securityDAO);
    securityProfileDAO.setJdbcTemplate(template);
    securityProfileDAO.setSecurityManager(sm);

    alertDAO.setJdbcTemplate(template);
    alertDAO.setSecurityManager(sm);
    daos.put(uk.ac.bbsrc.tgac.miso.core.event.Alert.class, alertDAO);

    libraryDilutionDAO.setJdbcTemplate(template);
    libraryDilutionDAO.setSecurityProfileDAO(securityProfileDAO);
    libraryDilutionDAO.setLibraryDAO(libraryDAO);
    libraryDilutionDAO.setEmPcrDAO(emPCRDAO);
    libraryDilutionDAO.setCascadeType(CascadeType.PERSIST);
    libraryDilutionDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution.class, libraryDilutionDAO);

    entityGroupDAO.setJdbcTemplate(template);
    entityGroupDAO.setCascadeType(CascadeType.PERSIST);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.EntityGroup.class, entityGroupDAO);

    emPCRDAO.setJdbcTemplate(template);
    emPCRDAO.setSecurityProfileDAO(securityProfileDAO);
    emPCRDAO.setLibraryDilutionDAO(libraryDilutionDAO);
    emPCRDAO.setEmPCRDilutionDAO(emPCRDilutionDAO);
    emPCRDAO.setCascadeType(CascadeType.PERSIST);
    emPCRDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR.class, emPCRDAO);

    emPCRDilutionDAO.setJdbcTemplate(template);
    emPCRDilutionDAO.setSecurityProfileDAO(securityProfileDAO);
    emPCRDilutionDAO.setEmPcrDAO(emPCRDAO);
    emPCRDilutionDAO.setLibraryDAO(libraryDAO);
    emPCRDilutionDAO.setCascadeType(CascadeType.PERSIST);
    emPCRDilutionDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution.class, emPCRDilutionDAO);

    experimentDAO.setJdbcTemplate(template);
    experimentDAO.setSecurityProfileDAO(securityProfileDAO);
    experimentDAO.setKitDAO(kitDAO);
    experimentDAO.setPlatformDAO(platformDAO);
    experimentDAO.setPoolDAO(poolDAO);
    experimentDAO.setRunDAO(runDAO);
    experimentDAO.setSampleDAO(sampleDAO);
    experimentDAO.setStudyDAO(studyDAO);
    experimentDAO.setCascadeType(CascadeType.PERSIST);
    experimentDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Experiment.class, experimentDAO);

    kitDAO.setJdbcTemplate(template);
    kitDAO.setNoteDAO(noteDAO);
    kitDAO.setCascadeType(CascadeType.PERSIST);
    kitDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Kit.class, kitDAO);

    libraryDAO.setJdbcTemplate(template);
    libraryDAO.setSecurityProfileDAO(securityProfileDAO);
    libraryDAO.setNoteDAO(noteDAO);
    libraryDAO.setSampleDAO(sampleDAO);
    libraryDAO.setLibraryQcDAO(libraryQcDAO);
    libraryDAO.setDilutionDAO(libraryDilutionDAO);
    libraryDAO.setCascadeType(CascadeType.PERSIST);
    libraryDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Library.class, libraryDAO);

    libraryQcDAO.setJdbcTemplate(template);
    libraryQcDAO.setLibraryDAO(libraryDAO);
    libraryQcDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.LibraryQC.class, libraryQcDAO);

    noteDAO.setJdbcTemplate(template);
    noteDAO.setSecurityDAO(securityDAO);
    daos.put(com.eaglegenomics.simlims.core.Note.class, noteDAO);

    partitionDAO.setJdbcTemplate(template);
    partitionDAO.setSecurityProfileDAO(securityProfileDAO);
    partitionDAO.setSequencerPartitionContainerDAO(sequencerPartitionContainerDAO);
    partitionDAO.setPoolDAO(poolDAO);
    partitionDAO.setCascadeType(CascadeType.PERSIST);
    partitionDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Partition.class, partitionDAO);

    plateDAO.setJdbcTemplate(template);
    plateDAO.setSecurityProfileDAO(securityProfileDAO);
    plateDAO.setLibraryDAO(libraryDAO);
    plateDAO.setSampleDAO(sampleDAO);
    plateDAO.setDilutionDAO(libraryDilutionDAO);
    plateDAO.setCascadeType(CascadeType.PERSIST);
    plateDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Plate.class, plateDAO);

    platformDAO.setJdbcTemplate(template);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Platform.class, platformDAO);

    poolDAO.setJdbcTemplate(template);
    poolDAO.setSecurityProfileDAO(securityProfileDAO);
    poolDAO.setExperimentDAO(experimentDAO);
    poolDAO.setPoolQcDAO(poolQcDAO);
    poolDAO.setWatcherDAO(watcherDAO);
    poolDAO.setCascadeType(CascadeType.PERSIST);
    poolDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Pool.class, poolDAO);

    poolQcDAO.setJdbcTemplate(template);
    poolQcDAO.setPoolDAO(poolDAO);
    poolQcDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.PoolQC.class, poolQcDAO);

    printJobDAO.setJdbcTemplate(template);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.PrintJob.class, printJobDAO);

    MisoPrintContextResolverService mpcrs = new MisoPrintContextResolverService();
    PrintManager<MisoPrintService, ?> pm = new BarcodePrintManager(mpcrs);
    printServiceDAO.setPrintManager(pm);
    printServiceDAO.setJdbcTemplate(template);
    daos.put(MisoPrintService.class, printServiceDAO);

    projectDAO.setJdbcTemplate(template);
    projectDAO.setEntityGroupDAO(entityGroupDAO);
    projectDAO.setSecurityProfileDAO(securityProfileDAO);
    projectDAO.setStudyDAO(studyDAO);
    projectDAO.setSampleDAO(sampleDAO);
    projectDAO.setLibraryDAO(libraryDAO);
    projectDAO.setRunDAO(runDAO);
    projectDAO.setNoteDAO(noteDAO);
    projectDAO.setWatcherDAO(watcherDAO);
    projectDAO.setCascadeType(CascadeType.PERSIST);
    projectDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Project.class, projectDAO);

    runDAO.setJdbcTemplate(template);
    runDAO.setSecurityProfileDAO(securityProfileDAO);
    runDAO.setSequencerPartitionContainerDAO(sequencerPartitionContainerDAO);
    runDAO.setSequencerReferenceDAO(sequencerReferenceDAO);
    runDAO.setStatusDAO(statusDAO);
    runDAO.setNoteDAO(noteDAO);
    runDAO.setRunQcDAO(runQcDAO);
    runDAO.setWatcherDAO(watcherDAO);
    runDAO.setCascadeType(CascadeType.PERSIST);
    runDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Run.class, runDAO);

    runQcDAO.setJdbcTemplate(template);
    runQcDAO.setRunDAO(runDAO);
    runQcDAO.setSequencerPartitionContainerDAO(sequencerPartitionContainerDAO);
    runQcDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.RunQC.class, runQcDAO);

    sampleDAO.setJdbcTemplate(template);
    sampleDAO.setSecurityProfileDAO(securityProfileDAO);
    sampleDAO.setNoteDAO(noteDAO);
    sampleDAO.setLibraryDAO(libraryDAO);
    sampleDAO.setProjectDAO(projectDAO);
    sampleDAO.setSampleQcDAO(sampleQcDAO);
    sampleDAO.setCascadeType(CascadeType.PERSIST);
    sampleDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Sample.class, sampleDAO);

    sampleQcDAO.setJdbcTemplate(template);
    sampleQcDAO.setSampleDAO(sampleDAO);
    sampleQcDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.SampleQC.class, sampleQcDAO);

    sequencerPartitionContainerDAO.setJdbcTemplate(template);
    sequencerPartitionContainerDAO.setSecurityProfileDAO(securityProfileDAO);
    sequencerPartitionContainerDAO.setPartitionDAO(partitionDAO);
    sequencerPartitionContainerDAO.setPlatformDAO(platformDAO);
    sequencerPartitionContainerDAO.setRunDAO(runDAO);
    sequencerPartitionContainerDAO.setCascadeType(CascadeType.PERSIST);
    sequencerPartitionContainerDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer.class, sequencerPartitionContainerDAO);

    sequencerReferenceDAO.setJdbcTemplate(template);
    sequencerReferenceDAO.setDataObjectFactory(dataObjectFactory);
    sequencerReferenceDAO.setPlatformDAO(platformDAO);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.SequencerReference.class, sequencerReferenceDAO);

    studyDAO.setJdbcTemplate(template);
    studyDAO.setSecurityProfileDAO(securityProfileDAO);
    studyDAO.setExperimentDAO(experimentDAO);
    studyDAO.setProjectDAO(projectDAO);
    studyDAO.setCascadeType(CascadeType.PERSIST);
    studyDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Study.class, studyDAO);

    submissionDAO.setJdbcTemplate(template);
    submissionDAO.setPartitionDAO(partitionDAO);
    submissionDAO.setExperimentDAO(experimentDAO);
    submissionDAO.setRunDAO(runDAO);
    submissionDAO.setSampleDAO(sampleDAO);
    submissionDAO.setStudyDAO(studyDAO);
    submissionDAO.setDataObjectFactory(dataObjectFactory);
    submissionDAO.setDilutionDAO(libraryDilutionDAO);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Submission.class, submissionDAO);

    statusDAO.setJdbcTemplate(template);
    statusDAO.setDataObjectFactory(dataObjectFactory);
    daos.put(uk.ac.bbsrc.tgac.miso.core.data.Status.class, statusDAO);

    watcherDAO.setJdbcTemplate(template);
    watcherDAO.setSecurityManager(sm);

    daoLookup = new DaoLookup();
    daoLookup.setDaos(daos);

    poolDAO.setDaoLookup(daoLookup);
    plateDAO.setDaoLookup(daoLookup);
    entityGroupDAO.setDaoLookup(daoLookup);
  }

  @After
  public void tearDown() throws Exception {
    System.out.println("<<<<<<<<<<<<<<<< TEARDOWN");
    datasource = null;
  }

  private static void createDatabase(Connection conn) throws Exception {
    // get a database connection that will create the DB in memory
    System.out.println("Creating test database tables...");

    runStatement(conn,
                 "CREATE TABLE Alert (" +
                 "alertId BIGINT NOT NULL," +
                 "title VARCHAR(100) NOT NULL," +
                 "text LONGVARCHAR NOT NULL," +
                 "userId BIGINT NOT NULL," +
                 "date DATE NOT NULL," +
                 "isRead BIT NOT NULL," +
                 "level VARCHAR(8) NOT NULL," +
                 "PRIMARY KEY (alertId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE EntityGroup (" +
                 "entityGroupId BIGINT NOT NULL, " +
                 "parentId BIGINT NOT NULL, " +
                 "parentType VARCHAR(255) NOT NULL," +
                 "PRIMARY KEY  (entityGroupId, parentId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE EntityGroup_Elements (" +
                 "entityGroup_entityGroupId BIGINT NOT NULL, " +
                 "entityId BIGINT NOT NULL, " +
                 "entityType VARCHAR(255) NOT NULL," +
                 "PRIMARY KEY  (entityGroup_entityGroupId,entityId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Experiment (" +
                 "experimentId BIGINT NOT NULL, " +
                 "name VARCHAR(255) NOT NULL, " +
                 "description VARCHAR(255) NOT NULL," +
                 "accession VARCHAR(50) default NULL," +
                 "title VARCHAR(255) NOT NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "study_studyId BIGINT default NULL," +
                 "alias VARCHAR(30) default NULL," +
                 "platform_platformId BIGINT default NULL," +
                 "PRIMARY KEY  (experimentId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Experiment_Kit (" +
                 "experiments_experimentId BIGINT NOT NULL," +
                 "kits_kitId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Kit (" +
                 "kitId BIGINT NOT NULL," +
                 "identificationBarcode VARCHAR(255) default NULL," +
                 "locationBarcode VARCHAR(255) default NULL," +
                 "lotNumber VARCHAR(30) NOT NULL," +
                 "kitDate DATE NOT NULL," +
                 "kitDescriptorId BIGINT NOT NULL," +
                 "PRIMARY KEY  (kitId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE KitDescriptor (" +
                 "kitDescriptorId BIGINT NOT NULL," +
                 "name VARCHAR(255) default NULL," +
                 "version INT default NULL," +
                 "manufacturer VARCHAR(100) NOT NULL," +
                 "partNumber VARCHAR(50) NOT NULL," +
                 "stockLevel INT NOT NULL," +
                 "kitType VARCHAR(30) NOT NULL," +
                 "platformType VARCHAR(20) NOT NULL," +
                 "PRIMARY KEY  (kitDescriptorId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Kit_Note (" +
                 "kit_kitId BIGINT NOT NULL," +
                 "notes_noteId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Library (" +
                 "libraryId BIGINT NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "accession VARCHAR(30) default NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "sample_sampleId BIGINT NOT NULL," +
                 "identificationBarcode VARCHAR(255) default NULL," +
                 "locationBarcode VARCHAR(255) default NULL," +
                 "libraryType BIGINT default NULL," +
                 "librarySelectionType BIGINT default NULL," +
                 "libraryStrategyType BIGINT default NULL," +
                 "concentration TINYINT default NULL," +
                 "creationDate TIMESTAMP NOT NULL," +
                 "platformName VARCHAR(255) default NULL," +
                 "alias VARCHAR(30) default NULL," +
                 "paired BIT NOT NULL," +
                 "qcPassed VARCHAR(5) default NULL," +
                 "PRIMARY KEY  (libraryId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE LibraryDilution (" +
                 "dilutionId BIGINT NOT NULL," +
                 "concentration DOUBLE NOT NULL," +
                 "library_libraryId BIGINT NOT NULL," +
                 "identificationBarcode VARCHAR(255) default NULL," +
                 "creationDate DATE NOT NULL," +
                 "dilutionUserName VARCHAR(255) NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "PRIMARY KEY  (dilutionId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE LibraryQC (" +
                 "qcId BIGINT NOT NULL," +
                 "library_libraryId BIGINT NOT NULL," +
                 "qcUserName VARCHAR(255) NOT NULL," +
                 "qcDate TIMESTAMP NOT NULL," +
                 "qcMethod BIGINT NOT NULL," +
                 "results DOUBLE default NULL," +
                 "insertSize INT NOT NULL," +
                 "PRIMARY KEY (qcId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE LibraryType (" +
                 "libraryTypeId BIGINT NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "platformType VARCHAR(50) NOT NULL," +
                 "PRIMARY KEY  (libraryTypeId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE LibrarySelectionType (" +
                 "librarySelectionTypeId BIGINT NOT NULL," +
                 "name VARCHAR(50) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "PRIMARY KEY  (librarySelectionTypeId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE LibraryStrategyType (" +
                 "libraryStrategyTypeId BIGINT NOT NULL," +
                 "name VARCHAR(50) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "PRIMARY KEY  (libraryStrategyTypeId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Library_Note (" +
                 "library_libraryId BIGINT NOT NULL," +
                 "notes_noteId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Library_TagBarcode (" +
                 "library_libraryId BIGINT NOT NULL," +
                 "barcode_barcodeId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Note (" +
                 "noteId BIGINT NOT NULL, " +
                 "creationDate DATE default NULL, " +
                 "internalOnly BIT NOT NULL, " +
                 "text VARCHAR(255) default NULL, " +
                 "owner_userId BIGINT default NULL, " +
                 "PRIMARY KEY (noteId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE _Partition (" +
                 "partitionId BIGINT NOT NULL," +
                 "partitionNumber TINYINT NOT NULL," +
                 "pool_poolId BIGINT default NULL," +
                 "securityProfile_profileId BIGINT NOT NULL," +
                 "PRIMARY KEY  (partitionId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Plate (" +
                 "plateId BIGINT NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "creationDate DATE NOT NULL," +
                 "plateMaterialType VARCHAR(20) NOT NULL," +
                 "identificationBarcode VARCHAR(255) DEFAULT NULL," +
                 "locationBarcode VARCHAR(255) DEFAULT NULL," +
                 "size INT NOT NULL," +
                 "tagBarcodeId BIGINT DEFAULT NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "PRIMARY KEY (plateId) " +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Plate_Elements (" +
                 "plate_plateId BIGINT NOT NULL," +
                 "elementType VARCHAR(255) NOT NULL" +
                 "elementPosition INTEGER NOT NULL" +
                 "elementId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Pool (" +
                 "poolId BIGINT NOT NULL," +
                 "concentration DOUBLE NOT NULL," +
                 "identificationBarcode VARCHAR(13) default NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "alias VARCHAR(50) default NULL," +
                 "creationDate DATE NOT NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "experiment_experimentId BIGINT default NULL," +
                 "platformType VARCHAR(20) NOT NULL," +
                 "ready BOOLEAN NOT NULL," +
                 "qcPassed VARCHAR(5) default NULL," +
                 "PRIMARY KEY  (poolId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE PoolQC (" +
                 "qcId BIGINT NOT NULL," +
                 "pool_poolId BIGINT NOT NULL," +
                 "qcUserName VARCHAR(255) NOT NULL," +
                 "qcDate TIMESTAMP NOT NULL," +
                 "qcMethod BIGINT NOT NULL," +
                 "results DOUBLE default NULL," +
                 "PRIMARY KEY (qcId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Pool_Experiment (" +
                 "pool_poolId BIGINT NOT NULL," +
                 "experiments_experimentId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Pool_Elements (" +
                 "pool_poolId BIGINT NOT NULL," +
                 "elementType VARCHAR(255) NOT NULL" +
                 "elementId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Platform (" +
                 "platformId BIGINT NOT NULL," +
                 "name VARCHAR(50) NOT NULL," +
                 "instrumentModel VARCHAR(100) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "numContainers TINYINT NOT NULL," +
                 "PRIMARY KEY  (platformId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE PrintJob (" +
                 "jobId BIGINT NOT NULL," +
                 "printServiceName VARCHAR(255) NOT NULL," +
                 "printDate TIMESTAMP NOT NULL," +
                 "jobCreator_userId BIGINT NOT NULL," +
                 "printedElements BINARY NOT NULL," +
                 "status VARCHAR(20) NOT NULL," +
                 "PRIMARY KEY (jobId));");

    runStatement(conn,
                 "CREATE TABLE PrintService (" +
                 "serviceId BIGINT NOT NULL," +
                 "serviceName VARCHAR(100) NOT NULL," +
                 "contextName VARCHAR(100) NOT NULL," +
                 "contextFields VARCHAR(255)," +
                 "enabled BIT NOT NULL," +
                 "printServiceFor VARCHAR(255) NOT NULL," +
                 "PRIMARY KEY (serviceId));");

    runStatement(conn,
                 "CREATE TABLE Project (" +
                 "projectId BIGINT NOT NULL," +
                 "creationDate TIMESTAMP default NULL," +
                 "description VARCHAR(255) default NULL," +
                 "name VARCHAR(255) default NULL," +
                 "alias VARCHAR(50) default NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "progress VARCHAR(20) NOT NULL," +
                 "lastUpdated TIMESTAMP default CURRENT_TIMESTAMP, " +
                 "PRIMARY KEY  (projectId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Project_Issues (" +
                 "project_projectId BIGINT NOT NULL," +
                 "issueKey VARCHAR NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE ProjectOverview (" +
                 "overviewId BIGINT NOT NULL," +
                 "principalInvestigator VARCHAR(255) NOT NULL," +
                 "startDate DATE default NULL," +
                 "endDate DATE default NULL," +
                 "numProposedSamples INT default NULL," +
                 "locked BIT NOT NULL," +
                 "allSampleQcPassed BIT default NULL," +
                 "libraryPreparationComplete BIT default NULL," +
                 "allLibraryQcPassed BIT default NULL," +
                 "allPoolsConstructed BIT default NULL," +
                 "allRunsCompleted BIT default NULL," +
                 "primaryAnalysisCompleted BIT default NULL," +
                 "lastUpdated TIMESTAMP default CURRENT_TIMESTAMP, " +
                 "PRIMARY KEY (overviewId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE ProjectOverview_Note (" +
                 "overview_overviewId BIGINT NOT NULL," +
                 "notes_noteId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Project_Note (" +
                 "project_projectId BIGINT NOT NULL," +
                 "notes_noteId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Project_ProjectOverview (" +
                 "project_projectID BIGINT NOT NULL," +
                 "overviews_overviewId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Project_Request (" +
                 "Project_projectId BIGINT NOT NULL," +
                 "requests_requestId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Project_Study (" +
                 "Project_projectId BIGINT NOT NULL," +
                 "studies_studyId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE QcType (" +
                 "qcTypeId BIGINT NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "qcTarget VARCHAR(50) NOT NULL," +
                 "units VARCHAR(10) NOT NULL," +
                 "PRIMARY KEY (qcTypeId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Request (" +
                 "requestId BIGINT NOT NULL," +
                 "creationDate TIMESTAMP default NULL," +
                 "description VARCHAR(255) default NULL," +
                 "executionCount INT NOT NULL," +
                 "lastExecutionDate TIMESTAMP default NULL," +
                 "name VARCHAR(255) default NULL," +
                 "protocolUniqueIdentifier VARCHAR(255) default NULL," +
                 "project_projectId BIGINT default NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "PRIMARY KEY (requestId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Request_Note (" +
                 "Request_requestId BIGINT NOT NULL," +
                 "notes_noteId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Run (" +
                 "runId BIGINT NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "alias VARCHAR(50) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "accession VARCHAR(50) default NULL," +
                 "platformRunId INT default NULL," +
                 "pairedEnd BOOLEAN NOT NULL," +
                 "cycles SMALLINT default NULL," +
                 "filePath VARCHAR(255) default NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "platformType VARCHAR(50) NOT NULL," +
                 "status_statusId BIGINT default NULL," +
                 "sequencerReference_sequencerReferenceId BIGINT default NULL," +
                 "PRIMARY KEY  (runId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE RunQC (" +
                 "qcId BIGINT NOT NULL," +
                 "run_runId BIGINT NOT NULL," +
                 "qcUserName VARCHAR(255) NOT NULL," +
                 "qcDate TIMESTAMP NOT NULL," +
                 "qcMethod BIGINT NOT NULL," +
                 "information LONGVARCHAR default NULL," +
                 "doNotProcess BIT NOT NULL," +
                 "PRIMARY KEY (qcId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE RunQC_Partition (" +
                 "runQc_runQcId BIGINT NOT NULL," +
                 "containers_containerId BIGINT NOT NULL," +
                 "partitionNumber TINYINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Run_SequencerPartitionContainer (" +
                 "Run_runId BIGINT NOT NULL," +
                 "containers_containerId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Run_Note (" +
                 "run_runId BIGINT NOT NULL," +
                 "notes_noteId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Sample (" +
                 "sampleId BIGINT NOT NULL," +
                 "accession VARCHAR(50) default NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "scientificName VARCHAR(255) NOT NULL," +
                 "taxonIdentifier VARCHAR(255) default NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "identificationBarcode VARCHAR(255) default NULL," +
                 "locationBarcode VARCHAR(255) default NULL," +
                 "sampleType VARCHAR(50) NOT NULL," +
                 "receivedDate TIMESTAMP default NULL," +
                 "qcPassed VARCHAR(5) default NULL," +
                 "alias VARCHAR(30) default NULL," +
                 "project_projectId BIGINT NOT NULL," +
                 "PRIMARY KEY  (sampleId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SampleQC (" +
                 "qcId BIGINT NOT NULL," +
                 "sample_sampleId BIGINT NOT NULL," +
                 "qcUserName VARCHAR(255) NOT NULL," +
                 "qcDate TIMESTAMP NOT NULL," +
                 "qcMethod BIGINT NOT NULL," +
                 "results DOUBLE default NULL," +
                 "PRIMARY KEY (qcId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SampleType (" +
                 "typeId BIGINT NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "PRIMARY KEY (typeId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Sample_Note (" +
                 "sample_sampleId BIGINT NOT NULL," +
                 "notes_noteId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SecurityProfile (" +
                 "profileId BIGINT NOT NULL," +
                 "allowAllInternal BIT NOT NULL," +
                 "owner_userId BIGINT default NULL," +
                 "PRIMARY KEY  (profileId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SecurityProfile_ReadGroup (" +
                 "SecurityProfile_profileId BIGINT NOT NULL," +
                 "readGroup_groupId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SecurityProfile_ReadUser (" +
                 "SecurityProfile_profileId BIGINT NOT NULL," +
                 "readUser_userId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SecurityProfile_WriteGroup (" +
                 "SecurityProfile_profileId BIGINT NOT NULL," +
                 "writeGroup_groupId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SecurityProfile_WriteUser (" +
                 "SecurityProfile_profileId BIGINT NOT NULL," +
                 "writeUser_userId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SequencerPartitionContainer (" +
                 "containerId BIGINT NOT NULL," +
                 "platform BIGINT DEFAULT NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "identificationBarcode VARCHAR(255) default NULL," +
                 "locationBarcode VARCHAR(255) default NULL," +
                 "validationBarcode VARCHAR(255) default NULL," +
                 "PRIMARY KEY  (containerId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SequencerPartitionContainer_Partition (" +
                 "container_containerId BIGINT NOT NULL," +
                 "partitions_partitionId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE SequencerReference (" +
                 "referenceId BIGINT NOT NULL," +
                 "name VARCHAR(30) NOT NULL," +
                 "ipAddress BINARY NOT NULL," +
                 "platformId BIGINT NOT NULL," +
                 "available BIT NOT NULL," +
                 "PRIMARY KEY  (referenceId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Status (" +
                 "statusId BIGINT NOT NULL," +
                 "health VARCHAR(50) NOT NULL," +
                 "completionDate DATE default NULL," +
                 "startDate DATE default NULL," +
                 "xml BINARY default NULL," +
                 "runName VARCHAR(255) default NULL," +
                 "instrumentName VARCHAR(255) default NULL," +
                 "lastUpdated TIMESTAMP default CURRENT_TIMESTAMP, " +
                 "PRIMARY KEY (statusId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Study (" +
                 "studyId BIGINT NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "description VARCHAR(255) NOT NULL," +
                 "accession VARCHAR(30) default NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "project_projectId BIGINT NOT NULL," +
                 "studyType VARCHAR(255) default NULL," +
                 "alias VARCHAR(30) default NULL," +
                 "PRIMARY KEY  (studyId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE StudyType (" +
                 "typeId BIGINT NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "PRIMARY KEY (typeId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Study_Experiment (" +
                 "Study_studyId BIGINT NOT NULL," +
                 "experiments_experimentId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Submission (" +
                 "submissionId BIGINT NOT NULL," +
                 "creationDate DATE NOT NULL," +
                 "submittedDate DATE DEFAULT NULL," +
                 "verified BIT NOT NULL," +
                 "description VARCHAR(255)," +
                 "name VARCHAR(255)," +
                 "title VARCHAR(255)," +
                 "accession VARCHAR(50) DEFAULT NULL," +
                 "alias VARCHAR(30) DEFAULT NULL," +
                 "completed BIT NOT NULL," +
                 "PRIMARY KEY (submissionId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Submission_Experiment (" +
                 "submission_submissionId BIGINT NOT NULL," +
                 "experiments_experimentId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Submission_Partition_Dilution (" +
                 "submission_submissionId BIGINT NOT NULL," +
                 "partition_partitionId BIGINT NOT NULL," +
                 "dilution_dilutionId BIGINT NOT NULL," +
                 "PRIMARY KEY (submission_submissionId,partition_partitionId,dilution_dilutionId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Submission_Sample (" +
                 "submission_submissionId BIGINT NOT NULL," +
                 "samples_sampleId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Submission_Study (" +
                 "submission_submissionId BIGINT NOT NULL," +
                 "studies_studyId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE TagBarcodes (" +
                 "tagId BIGINT NOT NULL," +
                 "name VARCHAR(10) NOT NULL," +
                 "sequence VARCHAR(20) NOT NULL," +
                 "platformName VARCHAR(20) NOT NULL," +
                 "strategyName VARCHAR(100) NOT NULL," +
                 "PRIMARY KEY  (tagId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE User (" +
                 "userId BIGINT NOT NULL," +
                 "active BIT NOT NULL," +
                 "admin BIT NOT NULL," +
                 "external BIT NOT NULL," +
                 "fullName VARCHAR(255) default NULL," +
                 "internal BIT NOT NULL," +
                 "loginName VARCHAR(255) default NULL," +
                 "roles BINARY," +
                 "password VARCHAR(255) default NULL," +
                 "email VARCHAR(255) default NULL," +
                 "PRIMARY KEY  (userId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE User_Group (" +
                 "users_userId BIGINT NOT NULL," +
                 "groups_groupId BIGINT NOT NULL" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE _Group (" +
                 "groupId BIGINT NOT NULL," +
                 "description VARCHAR(255) default NULL," +
                 "name VARCHAR(255) default NULL," +
                 "PRIMARY KEY  (groupId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE emPCR (" +
                 "pcrId BIGINT NOT NULL," +
                 "concentration DOUBLE NOT NULL," +
                 "dilution_dilutionId BIGINT NOT NULL," +
                 "creationDate DATE NOT NULL," +
                 "pcrUserName VARCHAR(255) NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "PRIMARY KEY  (pcrId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE emPCRDilution (" +
                 "dilutionId BIGINT NOT NULL," +
                 "concentration DOUBLE NOT NULL," +
                 "emPCR_pcrId BIGINT NOT NULL," +
                 "identificationBarcode VARCHAR(255) default NULL," +
                 "creationDate DATE NOT NULL," +
                 "dilutionUserName VARCHAR(255) NOT NULL," +
                 "name VARCHAR(255) NOT NULL," +
                 "securityProfile_profileId BIGINT default NULL," +
                 "PRIMARY KEY  (dilutionId)" +
                 ");");

    runStatement(conn,
                 "CREATE TABLE Watcher (" +
                 "entityName VARCHAR(4) NOT NULL," +
                 "userId BIGINT NOT NULL" +
                 ");");

    System.out.println("...done!");
    conn.close();
  }

  protected static void runStatement(Connection conn, String sql) throws SQLException {
    Statement st = conn.createStatement();
    st.executeUpdate(sql);
    st.close();
  }
}
