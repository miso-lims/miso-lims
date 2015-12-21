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

package uk.ac.bbsrc.tgac.miso.sqlstore.util;

import java.sql.SQLException;

import javax.persistence.CascadeType;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;

import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;

import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoRequestManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLChangeLogDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLEmPCRDilutionDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLExperimentDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLKitDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryDilutionDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLNoteDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLPlatformDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLPoolDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLRunDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSampleDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSampleQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSecurityDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSecurityProfileDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerPartitionContainerDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerPoolPartitionDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerReferenceDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLStudyDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLTgacSubmissionDAO;

/**
 * Nasty class to manually wire up a MisoRequestManager.
 * <p/>
 * SHOULD ONLY BE USED FOR TESTING PURPOSES!
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class DaoUtils {
  public static <T extends LocalSecurityManager> void wireMisoRequestManager(MisoRequestManager requestManager, T lsm, JdbcTemplate jt)
      throws SQLException {
    SQLEmPCRDilutionDAO emPCRDilutionDAO;
    SQLLibraryDilutionDAO libraryDilutionDAO;
    SQLEmPCRDAO emPCRDAO;
    SQLExperimentDAO experimentDAO;
    SQLLibraryDAO libraryDAO;
    SQLLibraryQCDAO libraryQcDAO;
    SQLNoteDAO noteDAO;
    SQLSequencerPoolPartitionDAO partitionDAO;
    SQLPlatformDAO platformDAO;
    SQLPoolDAO poolDAO;
    SQLProjectDAO projectDAO;
    SQLRunDAO runDAO;
    SQLSampleDAO sampleDAO;
    SQLSampleQCDAO sampleQcDAO;
    SQLSecurityProfileDAO securityProfileDAO;
    SQLSecurityDAO securityDAO;
    SQLSequencerPartitionContainerDAO sequencerPartitionContainerDAO;
    SQLSequencerReferenceDAO sequencerReferenceDAO;
    SQLStatusDAO statusDAO;
    SQLStudyDAO studyDAO;
    SQLTgacSubmissionDAO submissionDAO;
    SQLKitDAO kitDAO;
    SQLChangeLogDAO changeLogDAO;

    DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();

    securityDAO = new SQLSecurityDAO();
    securityDAO.setLobHandler(new DefaultLobHandler());
    securityDAO.setJdbcTemplate(jt);

    securityProfileDAO = new SQLSecurityProfileDAO();
    emPCRDilutionDAO = new SQLEmPCRDilutionDAO();
    libraryDilutionDAO = new SQLLibraryDilutionDAO();
    emPCRDAO = new SQLEmPCRDAO();
    experimentDAO = new SQLExperimentDAO();
    kitDAO = new SQLKitDAO();
    libraryDAO = new SQLLibraryDAO();
    libraryQcDAO = new SQLLibraryQCDAO();
    noteDAO = new SQLNoteDAO();
    partitionDAO = new SQLSequencerPoolPartitionDAO();
    platformDAO = new SQLPlatformDAO();
    poolDAO = new SQLPoolDAO();
    projectDAO = new SQLProjectDAO();
    runDAO = new SQLRunDAO();
    sampleDAO = new SQLSampleDAO();
    sampleQcDAO = new SQLSampleQCDAO();
    sequencerPartitionContainerDAO = new SQLSequencerPartitionContainerDAO();
    sequencerReferenceDAO = new SQLSequencerReferenceDAO();
    statusDAO = new SQLStatusDAO();
    studyDAO = new SQLStudyDAO();
    submissionDAO = new SQLTgacSubmissionDAO();
    changeLogDAO = new SQLChangeLogDAO();

    changeLogDAO.setJdbcTemplate(jt);

    lsm.setSecurityStore(securityDAO);

    securityProfileDAO.setJdbcTemplate(jt);
    securityProfileDAO.setSecurityManager(lsm);

    emPCRDilutionDAO.setJdbcTemplate(jt);
    emPCRDilutionDAO.setSecurityProfileDAO(securityProfileDAO);
    emPCRDilutionDAO.setLibraryDAO(libraryDAO);
    emPCRDilutionDAO.setEmPcrDAO(emPCRDAO);
    emPCRDilutionDAO.setCascadeType(CascadeType.PERSIST);
    emPCRDilutionDAO.setDataObjectFactory(dataObjectFactory);

    libraryDilutionDAO.setJdbcTemplate(jt);
    libraryDilutionDAO.setSecurityProfileDAO(securityProfileDAO);
    libraryDilutionDAO.setLibraryDAO(libraryDAO);
    libraryDilutionDAO.setEmPcrDAO(emPCRDAO);
    libraryDilutionDAO.setCascadeType(CascadeType.PERSIST);
    libraryDilutionDAO.setDataObjectFactory(dataObjectFactory);

    emPCRDAO.setJdbcTemplate(jt);
    emPCRDAO.setSecurityProfileDAO(securityProfileDAO);
    emPCRDAO.setEmPCRDilutionDAO(emPCRDilutionDAO);
    emPCRDAO.setLibraryDilutionDAO(libraryDilutionDAO);
    emPCRDAO.setCascadeType(CascadeType.PERSIST);
    emPCRDAO.setDataObjectFactory(dataObjectFactory);

    experimentDAO.setJdbcTemplate(jt);
    experimentDAO.setSecurityProfileDAO(securityProfileDAO);
    experimentDAO.setPlatformDAO(platformDAO);
    experimentDAO.setPoolDAO(poolDAO);
    experimentDAO.setRunDAO(runDAO);
    experimentDAO.setSampleDAO(sampleDAO);
    experimentDAO.setStudyDAO(studyDAO);
    experimentDAO.setCascadeType(CascadeType.PERSIST);
    experimentDAO.setDataObjectFactory(dataObjectFactory);
    experimentDAO.setKitDAO(kitDAO);

    sequencerPartitionContainerDAO.setJdbcTemplate(jt);
    sequencerPartitionContainerDAO.setSecurityProfileDAO(securityProfileDAO);
    sequencerPartitionContainerDAO.setPartitionDAO(partitionDAO);
    sequencerPartitionContainerDAO.setCascadeType(CascadeType.PERSIST);
    sequencerPartitionContainerDAO.setDataObjectFactory(dataObjectFactory);

    kitDAO.setJdbcTemplate(jt);
    kitDAO.setNoteDAO(noteDAO);
    kitDAO.setCascadeType(CascadeType.PERSIST);
    kitDAO.setDataObjectFactory(dataObjectFactory);

    partitionDAO.setJdbcTemplate(jt);
    partitionDAO.setSecurityProfileDAO(securityProfileDAO);
    partitionDAO.setSequencerPartitionContainerDAO(sequencerPartitionContainerDAO);
    partitionDAO.setPoolDAO(poolDAO);
    partitionDAO.setCascadeType(CascadeType.PERSIST);
    partitionDAO.setDataObjectFactory(dataObjectFactory);

    libraryDAO.setJdbcTemplate(jt);
    libraryDAO.setSecurityProfileDAO(securityProfileDAO);
    libraryDAO.setSampleDAO(sampleDAO);
    libraryDAO.setLibraryQcDAO(libraryQcDAO);
    libraryDAO.setDilutionDAO(libraryDilutionDAO);
    libraryDAO.setCascadeType(CascadeType.PERSIST);
    libraryDAO.setDataObjectFactory(dataObjectFactory);

    libraryQcDAO.setJdbcTemplate(jt);
    libraryQcDAO.setLibraryDAO(libraryDAO);
    libraryQcDAO.setDataObjectFactory(dataObjectFactory);

    noteDAO.setJdbcTemplate(jt);
    noteDAO.setSecurityDAO(securityDAO);
    noteDAO.setDataObjectFactory(dataObjectFactory);

    poolDAO.setJdbcTemplate(jt);
    poolDAO.setSecurityProfileDAO(securityProfileDAO);
    poolDAO.setExperimentDAO(experimentDAO);
    poolDAO.setCascadeType(CascadeType.PERSIST);
    poolDAO.setDataObjectFactory(dataObjectFactory);

    projectDAO.setJdbcTemplate(jt);
    projectDAO.setSecurityProfileDAO(securityProfileDAO);
    projectDAO.setStudyDAO(studyDAO);
    projectDAO.setSampleDAO(sampleDAO);
    projectDAO.setLibraryDAO(libraryDAO);
    projectDAO.setRunDAO(runDAO);
    projectDAO.setNoteDAO(noteDAO);
    projectDAO.setCascadeType(CascadeType.PERSIST);
    projectDAO.setDataObjectFactory(dataObjectFactory);

    runDAO.setJdbcTemplate(jt);
    runDAO.setSecurityProfileDAO(securityProfileDAO);
    runDAO.setSequencerPartitionContainerDAO(sequencerPartitionContainerDAO);
    runDAO.setSequencerReferenceDAO(sequencerReferenceDAO);
    runDAO.setStatusDAO(statusDAO);
    runDAO.setCascadeType(CascadeType.PERSIST);
    runDAO.setDataObjectFactory(dataObjectFactory);

    sampleDAO.setJdbcTemplate(jt);
    sampleDAO.setChangeLogDAO(changeLogDAO);
    sampleDAO.setSecurityProfileDAO(securityProfileDAO);
    sampleDAO.setNoteDAO(noteDAO);
    sampleDAO.setLibraryDAO(libraryDAO);
    sampleDAO.setProjectDAO(projectDAO);
    sampleDAO.setSampleQcDAO(sampleQcDAO);
    sampleDAO.setCascadeType(CascadeType.PERSIST);
    sampleDAO.setDataObjectFactory(dataObjectFactory);

    sampleQcDAO.setJdbcTemplate(jt);
    sampleQcDAO.setSampleDAO(sampleDAO);
    sampleQcDAO.setDataObjectFactory(dataObjectFactory);

    studyDAO.setJdbcTemplate(jt);
    studyDAO.setSecurityProfileDAO(securityProfileDAO);
    studyDAO.setExperimentDAO(experimentDAO);
    studyDAO.setProjectDAO(projectDAO);
    studyDAO.setCascadeType(CascadeType.PERSIST);
    studyDAO.setDataObjectFactory(dataObjectFactory);

    submissionDAO.setJdbcTemplate(jt);
    submissionDAO.setExperimentDAO(experimentDAO);
    submissionDAO.setPartitionDAO(partitionDAO);
    submissionDAO.setRunDAO(runDAO);
    submissionDAO.setSampleDAO(sampleDAO);
    submissionDAO.setStudyDAO(studyDAO);
    submissionDAO.setDataObjectFactory(dataObjectFactory);

    platformDAO.setJdbcTemplate(jt);
    platformDAO.setDataObjectFactory(dataObjectFactory);

    statusDAO.setJdbcTemplate(jt);
    statusDAO.setDataObjectFactory(dataObjectFactory);

    sequencerReferenceDAO.setJdbcTemplate(jt);
    sequencerReferenceDAO.setPlatformDAO(platformDAO);
    sequencerReferenceDAO.setDataObjectFactory(dataObjectFactory);

    requestManager.setEmPCRDilutionStore(emPCRDilutionDAO);
    requestManager.setLibraryDilutionStore(libraryDilutionDAO);
    requestManager.setEmPCRStore(emPCRDAO);
    requestManager.setExperimentStore(experimentDAO);
    requestManager.setKitStore(kitDAO);
    requestManager.setLibraryQcStore(libraryQcDAO);
    requestManager.setLibraryStore(libraryDAO);
    requestManager.setNoteStore(noteDAO);
    requestManager.setPartitionStore(partitionDAO);
    requestManager.setPlatformStore(platformDAO);
    requestManager.setPoolStore(poolDAO);
    requestManager.setProjectStore(projectDAO);
    requestManager.setRunStore(runDAO);
    requestManager.setSampleQcStore(sampleQcDAO);
    requestManager.setSampleStore(sampleDAO);
    requestManager.setSequencerReferenceStore(sequencerReferenceDAO);
    requestManager.setSequencerPartitionContainerStore(sequencerPartitionContainerDAO);
    requestManager.setSecurityProfileStore(securityProfileDAO);
    requestManager.setStatusStore(statusDAO);
    requestManager.setStudyStore(studyDAO);
    requestManager.setChangeLogStore(changeLogDAO);
  }

  public static <T extends LocalSecurityManager> void wireLocalSecurityManager(T securityManager, JdbcTemplate jt) throws SQLException {
    SQLSecurityDAO securityDAO = new SQLSecurityDAO();
    securityDAO.setLobHandler(new DefaultLobHandler());
    securityDAO.setJdbcTemplate(jt);
    securityManager.setSecurityStore(securityDAO);
  }
}
