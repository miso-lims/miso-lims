package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;

import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.persistence.HibernateSampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateIndexDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstituteDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLabDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleNumberPerProjectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleValidRelationshipDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLabService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleClassService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLBoxDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLChangeLogDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLExperimentDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLKitComponentDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLKitDescriptorDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryDilutionDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLNoteDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLPlatformDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLPoolDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLPoolQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLRunDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLRunQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSampleQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSecurityDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSecurityProfileDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerPartitionContainerDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerPoolPartitionDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerReferenceDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLStudyDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLTargetedSequencingDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLWatcherDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DaoLookup;

import uk.ac.bbsrc.tgac.miso.migration.util.OicrMigrationNamingScheme;

/**
 * This class is used to simplify creation and wiring of MISO services. Some of the config is currently hardcoded - mainly naming schemes
 * and authentication
 */
public class MisoServiceManager {

  private final JdbcTemplate jdbcTemplate;
  private final SessionFactory sessionFactory;

  private final DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();
  private final boolean autoGenerateIdBarcodes = false; // TODO: config option
  private NamingScheme namingScheme;

  private LocalSecurityManager securityManager; // Supports JDBC authentication only
  private MigrationAuthorizationManager authorizationManager;
  private DaoLookup daoLookup;

  private SQLSecurityDAO securityStore;
  private SQLSecurityProfileDAO securityProfileDao;
  private SQLWatcherDAO watcherDao;
  private SQLProjectDAO projectDao;
  private SQLChangeLogDAO changeLogDao;
  private SQLNoteDAO noteDao;
  private SQLSampleQCDAO sampleQcDao;
  private SQLLibraryDAO libraryDao;
  private SQLLibraryQCDAO libraryQcDao;
  private SQLLibraryDilutionDAO dilutionDao;
  private SQLTargetedSequencingDAO targetedSequencingDao;
  private SQLPoolDAO poolDao;
  private SQLPoolQCDAO poolQcDao;
  private SQLExperimentDAO experimentDao;
  private SQLKitComponentDAO kitDao;
  private SQLKitDescriptorDAO kitDescriptorDao;
  private SQLPlatformDAO platformDao;
  private SQLStudyDAO studyDao;
  private SQLRunDAO runDao;
  private SQLRunQCDAO runQcDao;
  private SQLSequencerPartitionContainerDAO sequencerPartitionContainerDao;
  private SQLSequencerPoolPartitionDAO partitionDao;
  private SQLStatusDAO statusDao;
  private SQLSequencerReferenceDAO sequencerReferenceDao;
  private SQLBoxDAO boxDao;

  private DefaultSampleClassService sampleClassService;
  private DefaultSampleService sampleService;
  private DefaultLabService labService;
  private DefaultSampleNumberPerProjectService sampleNumberPerProjectService;
  private DefaultSampleValidRelationshipService sampleValidRelationshipService;
  private DefaultReferenceGenomeService referenceGenomeService;

  private HibernateSampleClassDao sampleClassDao;
  private HibernateSampleDao sampleDao;
  private HibernateLabDao labDao;
  private HibernateInstituteDao instituteDao;
  private HibernateDetailedQcStatusDao detailedQcStatusDao;
  private HibernateSubprojectDao subprojectDao;
  private HibernateTissueOriginDao tissueOriginDao;
  private HibernateTissueTypeDao tissueTypeDao;
  private HibernateSamplePurposeDao samplePurposeDao;
  private HibernateTissueMaterialDao tissueMaterialDao;
  private HibernateSampleNumberPerProjectDao sampleNumberPerProjectDao;
  private HibernateSampleValidRelationshipDao sampleValidRelationshipDao;
  private HibernateLibraryAdditionalInfoDao libraryAdditionalInfoDao;
  private HibernateLibraryDesignDao libraryDesignDao;
  private HibernateLibraryDesignCodeDao libraryDesignCodeDao;
  private HibernateIndexDao indexDao;
  private HibernateSequencingParametersDao sequencingParametersDao;
  private HibernateReferenceGenomeDao referenceGenomeDao;

  /**
   * Constructs a new MisoServiceManager with no services initialized
   *
   * @param jdbcTemplate for JDBC access to the database
   * @param sessionFactory for Hibernate access to the database
   */
  public MisoServiceManager(JdbcTemplate jdbcTemplate, SessionFactory sessionFactory) {
    this.jdbcTemplate = jdbcTemplate;
    this.sessionFactory = sessionFactory;
  }

  /**
   * Factory method to create a MisoServiceManager with all services already created and wired
   *
   * @param jdbcTemplate for JDBC access to the database
   * @param sessionFactory for Hibernate access to the database
   * @param username user to attribute migration to
   * @return
   * @throws IOException
   */
  public static MisoServiceManager buildWithDefaults(JdbcTemplate jdbcTemplate, SessionFactory sessionFactory, String username)
      throws IOException {
    MisoServiceManager m = new MisoServiceManager(jdbcTemplate, sessionFactory);
    m.setDefaultBoxDao();
    m.setDefaultChangeLogDao();
    m.setDefaultDilutionDao();
    m.setDefaultExperimentDao();
    m.setDefaultInstituteDao();
    m.setDefaultKitDao();
    m.setDefaultLabDao();
    m.setDefaultLabService();
    m.setDefaultLibraryDao();
    m.setDefaultLibraryAdditionalInfoDao();
    m.setDefaultLibraryQcDao();
    m.setDefaultNoteDao();
    m.setDefaultPartitionDao();
    m.setDefaultPlatformDao();
    m.setDefaultPoolDao();
    m.setDefaultReferenceGenomeDao();
    m.setDefaultReferenceGenomeService();
    m.setDefaultProjectDao();
    m.setDefaultDetailedQcStatusDao();
    m.setDefaultRunDao();
    m.setDefaultRunQcDao();
    m.setDefaultSampleClassDao();
    m.setDefaultSampleClassService();
    m.setDefaultSampleDao();
    m.setDefaultSampleNumberPerProjectService();
    m.setDefaultSampleNumberPerProjectDao();
    m.setDefaultSamplePurposeDao();
    m.setDefaultSampleQcDao();
    m.setDefaultSampleService();
    m.setDefaultSampleValidRelationshipDao();
    m.setDefaultSampleValidRelationshipService();
    m.setDefaultSecurityManager();
    m.setDefaultSecurityProfileDao();
    m.setDefaultSecurityStore();
    m.setDefaultSequencerPartitionContainerDao();
    m.setDefaultSequencerReferenceDao();
    m.setDefaultStatusDao();
    m.setDefaultStudyDao();
    m.setDefaultSubprojectDao();
    m.setDefaultTargetedSequencingDao();
    m.setDefaultTissueMaterialDao();
    m.setDefaultTissueOriginDao();
    m.setDefaultTissueTypeDao();
    m.setDefaultWatcherDao();
    m.setDefaultLibraryDesignDao();
    m.setDefaultLibraryDesignCodeDao();
    m.setDefaultIndexDao();
    m.setDefaultDaoLookup();
    m.setDefaultPoolQcDao();
    m.setDefaultSequencingParametersDao();


    User migrationUser = m.getsecurityStore().getUserByLoginName(username);
    if (migrationUser == null) throw new IllegalArgumentException("User '" + username + "' not found");
    m.setUpSecurityContext(migrationUser);
    m.setAuthorizationManagerWithUser(migrationUser);
    return m;
  }

  /**
   * Sets up the SecurityContext to authenticate as migrationUser
   *
   * @param migrationUser
   */
  public void setUpSecurityContext(User migrationUser) {
    Authentication auth = new MigrationAuthentication(migrationUser);
    SecurityContext context = new SecurityContextImpl();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);
  }

  // TODO: Add naming scheme config instead of hard-coding
  private NamingScheme getNamingScheme() {
    if (namingScheme == null) {
      namingScheme = new OicrMigrationNamingScheme();
    }
    return namingScheme;
  }

  public MigrationAuthorizationManager getAuthorizationManager() {
    return authorizationManager;
  }

  public void setAuthorizationManager(MigrationAuthorizationManager authorizationManager) {
    this.authorizationManager = authorizationManager;
    updateAuthorizationManagerDependencies();
  }

  public void setAuthorizationManagerWithUser(User migrationUser) {
    setAuthorizationManager(new MigrationAuthorizationManager(migrationUser));
  }

  private void updateAuthorizationManagerDependencies() {
    if (sampleClassService != null) sampleClassService.setAuthorizationManager(authorizationManager);
    if (sampleService != null) sampleService.setAuthorizationManager(authorizationManager);
    if (labService != null) labService.setAuthorizationManager(authorizationManager);
    if (sampleNumberPerProjectService != null) sampleNumberPerProjectService.setAuthorizationManager(authorizationManager);
    if (sampleValidRelationshipService != null) sampleValidRelationshipService.setAuthorizationManager(authorizationManager);
    if (referenceGenomeService != null) referenceGenomeService.setAuthorizationManager(authorizationManager);
  }

  public SQLSecurityDAO getsecurityStore() {
    return securityStore;
  }

  public void setSecurityStore(SQLSecurityDAO securityStore) {
    this.securityStore = securityStore;
    updateSecurityStoreDependencies();
  }

  public void setDefaultSecurityStore() {
    SQLSecurityDAO store = new SQLSecurityDAO();
    store.setJdbcTemplate(jdbcTemplate);
    store.setSecurityProfileDAO(securityProfileDao);
    setSecurityStore(store);
  }

  private void updateSecurityStoreDependencies() {
    if (securityManager != null) securityManager.setSecurityStore(securityStore);
    if (sampleDao != null) sampleDao.setSecurityDao(securityStore);
    if (noteDao != null) noteDao.setSecurityDAO(securityStore);
    if (libraryDao != null) libraryDao.setSecurityDAO(securityStore);
    if (targetedSequencingDao != null) targetedSequencingDao.setSecurityDAO(securityStore);
    if (poolDao != null) poolDao.setSecurityDAO(securityStore);
    if (experimentDao != null) experimentDao.setSecurityDAO(securityStore);
    //if (kitDao != null) kitDao.setSecurityDAO(securityStore);
    if (studyDao != null) studyDao.setSecurityDAO(securityStore);
    if (runDao != null) runDao.setSecurityDAO(securityStore);
    if (sequencerPartitionContainerDao != null) sequencerPartitionContainerDao.setSecurityDAO(securityStore);
    if (boxDao != null) boxDao.setSecurityDAO(securityStore);
    if (changeLogDao != null) changeLogDao.setSecurityDAO(securityStore);
  }

  public SQLSecurityProfileDAO getSecurityProfileDao() {
    return securityProfileDao;
  }

  public void setSecurityProfileDao(SQLSecurityProfileDAO securityProfileDao) {
    this.securityProfileDao = securityProfileDao;
    updateSecurityProfileDaoDependencies();
  }

  public void setDefaultSecurityProfileDao() {
    SQLSecurityProfileDAO store = new SQLSecurityProfileDAO();
    store.setJdbcTemplate(jdbcTemplate);
    store.setSecurityManager(securityManager);
    setSecurityProfileDao(store);
  }

  private void updateSecurityProfileDaoDependencies() {
    if (securityStore != null) securityStore.setSecurityProfileDAO(securityProfileDao);
    if (projectDao != null) projectDao.setSecurityProfileDAO(securityProfileDao);
    if (sampleDao != null) sampleDao.setSecurityProfileDao(securityProfileDao);
    if (libraryDao != null) libraryDao.setSecurityProfileDAO(securityProfileDao);
    if (dilutionDao != null) dilutionDao.setSecurityProfileDAO(securityProfileDao);
    if (poolDao != null) poolDao.setSecurityProfileDAO(securityProfileDao);
    if (experimentDao != null) experimentDao.setSecurityProfileDAO(securityProfileDao);
    if (studyDao != null) studyDao.setSecurityProfileDAO(securityProfileDao);
    if (runDao != null) runDao.setSecurityProfileDAO(securityProfileDao);
    if (sequencerPartitionContainerDao != null) sequencerPartitionContainerDao.setSecurityProfileDAO(securityProfileDao);
    if (partitionDao != null) partitionDao.setSecurityProfileDAO(securityProfileDao);
    if (boxDao != null) boxDao.setSecurityProfileDAO(securityProfileDao);
  }

  public LocalSecurityManager getSecurityManager() {
    return securityManager;
  }

  public void setSecurityManager(LocalSecurityManager securityManager) {
    this.securityManager = securityManager;
    updateSecurityManagerDependencies();
  }

  public void setDefaultSecurityManager() {
    LocalSecurityManager mgr = new LocalSecurityManager();
    mgr.setSecurityStore(securityStore);
    setSecurityManager(mgr);
  }

  private void updateSecurityManagerDependencies() {
    if (securityProfileDao != null) securityProfileDao.setSecurityManager(securityManager);
    if (watcherDao != null) watcherDao.setSecurityManager(securityManager);
    if (projectDao != null) projectDao.setSecurityManager(securityManager);
    if (poolDao != null) poolDao.setSecurityManager(securityManager);
    if (runDao != null) runDao.setSecurityManager(securityManager);
  }

  public SQLWatcherDAO getWatcherDao() {
    return watcherDao;
  }

  public void setWatcherDao(SQLWatcherDAO watcherDao) {
    this.watcherDao = watcherDao;
    updateWatcherDaoDependencies();
  }

  public void setDefaultWatcherDao() {
    SQLWatcherDAO dao = new SQLWatcherDAO();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityManager(securityManager);
    setWatcherDao(dao);
  }

  private void updateWatcherDaoDependencies() {
    if (projectDao != null) projectDao.setWatcherDAO(watcherDao);
    if (poolDao != null) poolDao.setWatcherDAO(watcherDao);
    if (runDao != null) runDao.setWatcherDAO(watcherDao);
  }

  public SQLProjectDAO getProjectDao() {
    return projectDao;
  }

  public void setProjectDao(SQLProjectDAO projectDao) {
    this.projectDao = projectDao;
    updateProjectDaoDependencies();
  }

  public void setDefaultProjectDao() {
    SQLProjectDAO dao = new SQLProjectDAO();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityManager(securityManager);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setNamingScheme(getNamingScheme());
    dao.setWatcherDAO(watcherDao);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setReferenceGenomeDao(referenceGenomeDao);
    setProjectDao(dao);
  }

  private void updateProjectDaoDependencies() {
    if (studyDao != null) studyDao.setProjectDAO(projectDao);
    if (sampleNumberPerProjectService != null) sampleNumberPerProjectService.setSqlProjectDAO(projectDao);
  }

  public HibernateSampleClassDao getSampleClassDao() {
    return sampleClassDao;
  }

  public void setSampleClassDao(HibernateSampleClassDao sampleClassDao) {
    this.sampleClassDao = sampleClassDao;
    updateSampleClassDaoDependencies();
  }

  public void setDefaultSampleClassDao() {
    HibernateSampleClassDao dao = new HibernateSampleClassDao();
    dao.setSessionFactory(sessionFactory);
    setSampleClassDao(dao);
  }

  private void updateSampleClassDaoDependencies() {
    if (sampleClassService != null) sampleClassService.setSampleClassDao(sampleClassDao);
    if (sampleService != null) sampleService.setSampleClassDao(sampleClassDao);
    if (sampleValidRelationshipService != null) sampleValidRelationshipService.setSampleClassDao(sampleClassDao);
  }

  public DefaultSampleClassService getSampleClassService() {
    return sampleClassService;
  }

  public void setSampleClassService(DefaultSampleClassService sampleClassService) {
    this.sampleClassService = sampleClassService;
    updateSampleClassServiceDependencies();
  }

  public void setDefaultSampleClassService() {
    DefaultSampleClassService svc = new DefaultSampleClassService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setSampleClassDao(sampleClassDao);
    setSampleClassService(svc);
  }

  private void updateSampleClassServiceDependencies() {

  }

  public DefaultSampleService getSampleService() {
    return sampleService;
  }

  public void setSampleService(DefaultSampleService sampleService) {
    this.sampleService = sampleService;
    updateSampleServiceDependencies();
  }

  public void setDefaultSampleService() {
    DefaultSampleService svc = new DefaultSampleService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    svc.setNamingScheme(getNamingScheme());
    svc.setProjectStore(projectDao);
    svc.setDetailedQcStatusDao(detailedQcStatusDao);
    svc.setSampleClassDao(sampleClassDao);
    svc.setSampleNumberPerProjectService(sampleNumberPerProjectService);
    svc.setSamplePurposeDao(samplePurposeDao);
    svc.setSampleValidRelationshipService(sampleValidRelationshipService);
    svc.setSubProjectDao(subprojectDao);
    svc.setTissueMaterialDao(tissueMaterialDao);
    svc.setTissueOriginDao(tissueOriginDao);
    svc.setTissueTypeDao(tissueTypeDao);
    svc.setSampleDao(sampleDao);
    svc.setLabService(labService);
    setSampleService(svc);
  }

  private void updateSampleServiceDependencies() {
    if (libraryDao != null) libraryDao.setSampleDAO(sampleDao);
  }

  public HibernateSampleDao getSampleDao() {
    return sampleDao;
  }

  public void setSampleDao(HibernateSampleDao sampleDao) {
    this.sampleDao = sampleDao;
    updateSampleDaoDependencies();
  }

  public void setDefaultSampleDao() {
    HibernateSampleDao dao = new HibernateSampleDao();
    dao.setAutoGenerateIdentificationBarcodes(autoGenerateIdBarcodes);
    dao.setCascadeType(null);
    dao.setChangeLogDao(changeLogDao);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setLibraryDao(libraryDao);
    dao.setNoteDao(noteDao);
    dao.setSampleQcDao(sampleQcDao);
    dao.setSecurityDao(securityStore);
    dao.setSecurityProfileDao(securityProfileDao);
    dao.setSessionFactory(sessionFactory);
    setSampleDao(dao);
  }

  private void updateSampleDaoDependencies() {
    if (sampleService != null) sampleService.setSampleDao(sampleDao);
    if (sampleQcDao != null) sampleQcDao.setSampleDAO(sampleDao);
    if (libraryDao != null) libraryDao.setSampleDAO(sampleDao);
    if (boxDao != null) boxDao.setSampleDAO(sampleDao);
    if (projectDao != null) projectDao.setSampleDAO(sampleDao);
  }

  public SQLChangeLogDAO getChangeLogDao() {
    return changeLogDao;
  }

  public void setChangeLogDao(SQLChangeLogDAO changeLogDao) {
    this.changeLogDao = changeLogDao;
    updateChangeLogDaoDependencies();
  }

  public void setDefaultChangeLogDao() {
    SQLChangeLogDAO dao = new SQLChangeLogDAO();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityDAO(securityStore);
    setChangeLogDao(dao);
  }

  private void updateChangeLogDaoDependencies() {
    if (sampleDao != null) sampleDao.setChangeLogDao(changeLogDao);
    if (libraryDao != null) libraryDao.setChangeLogDAO(changeLogDao);
    if (poolDao != null) poolDao.setChangeLogDAO(changeLogDao);
    if (experimentDao != null) experimentDao.setChangeLogDAO(changeLogDao);
    //if (kitDao != null) kitDao.setChangeLogDAO(changeLogDao);
    if (studyDao != null) studyDao.setChangeLogDAO(changeLogDao);
    if (sequencerPartitionContainerDao != null) sequencerPartitionContainerDao.setChangeLogDAO(changeLogDao);
    if (boxDao != null) boxDao.setChangeLogDAO(changeLogDao);
  }

  public SQLNoteDAO getNoteDao() {
    return noteDao;
  }

  public void setNoteDao(SQLNoteDAO noteDao) {
    this.noteDao = noteDao;
    updateNoteDaoDependencies();
  }

  public void setDefaultNoteDao() {
    SQLNoteDAO dao = new SQLNoteDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityDAO(securityStore);
    setNoteDao(dao);
  }

  private void updateNoteDaoDependencies() {
    if (sampleDao != null) sampleDao.setNoteDao(noteDao);
    if (libraryDao != null) libraryDao.setNoteDAO(noteDao);
    if (poolDao != null) poolDao.setNoteDAO(noteDao);
    if (kitDao != null) kitDao.setNoteDAO(noteDao);
    if (runDao != null) runDao.setNoteDAO(noteDao);
  }

  public SQLSampleQCDAO getSampleQcDao() {
    return sampleQcDao;
  }

  public void setSampleQcDao(SQLSampleQCDAO sampleQcDao) {
    this.sampleQcDao = sampleQcDao;
    updateSampleQcDaoDependencies();
  }

  public void setDefaultSampleQcDao() {
    SQLSampleQCDAO dao = new SQLSampleQCDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSampleDAO(sampleDao);
    setSampleQcDao(dao);
  }

  private void updateSampleQcDaoDependencies() {
    if (sampleDao != null) sampleDao.setSampleQcDao(sampleQcDao);
  }

  public SQLLibraryDAO getLibraryDao() {
    return libraryDao;
  }

  public void setLibraryDao(SQLLibraryDAO libraryDao) {
    this.libraryDao = libraryDao;
    updateLibraryDaoDependencies();
  }

  public void setDefaultLibraryDao() {
    SQLLibraryDAO dao = new SQLLibraryDAO();
    dao.setDetailedSampleEnabled(true);
    dao.setAutoGenerateIdentificationBarcodes(autoGenerateIdBarcodes);
    dao.setBoxDAO(boxDao);
    dao.setChangeLogDAO(changeLogDao);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setDilutionDAO(dilutionDao);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setLibraryQcDAO(libraryQcDao);
    dao.setNamingScheme(getNamingScheme());
    dao.setNoteDAO(noteDao);
    dao.setPoolDAO(poolDao);
    dao.setSampleDAO(sampleDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setLibraryAdditionalInfoDao(libraryAdditionalInfoDao);
    dao.setIndexStore(indexDao);
    setLibraryDao(dao);
  }

  private void updateLibraryDaoDependencies() {
    if (sampleDao != null) sampleDao.setLibraryDao(libraryDao);
    if (libraryQcDao != null) libraryQcDao.setLibraryDAO(libraryDao);
    if (dilutionDao != null) dilutionDao.setLibraryDAO(libraryDao);
    if (boxDao != null) boxDao.setLibraryDAO(libraryDao);
    if (libraryDesignDao != null) libraryDesignDao.setLibraryDao(libraryDao);
  }

  public SQLLibraryQCDAO getLibraryQcDao() {
    return libraryQcDao;
  }

  public void setLibraryQcDao(SQLLibraryQCDAO libraryQcDao) {
    this.libraryQcDao = libraryQcDao;
    updateLibraryQcDaoDependencies();
  }

  public void setDefaultLibraryQcDao() {
    SQLLibraryQCDAO dao = new SQLLibraryQCDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setLibraryDAO(libraryDao);
    setLibraryQcDao(dao);
  }

  private void updateLibraryQcDaoDependencies() {
    if (libraryDao != null) libraryDao.setLibraryQcDAO(libraryQcDao);
  }

  public SQLLibraryDilutionDAO getDilutionDao() {
    return dilutionDao;
  }

  public void setDilutionDao(SQLLibraryDilutionDAO dilutionDao) {
    this.dilutionDao = dilutionDao;
    updateDilutionDaoDependencies();
  }

  public void setDefaultDilutionDao() {
    SQLLibraryDilutionDAO dao = new SQLLibraryDilutionDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setLibraryDAO(libraryDao);
    dao.setNamingScheme(getNamingScheme());
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setTargetedSequencingDAO(targetedSequencingDao);
    setDilutionDao(dao);
  }

  private void updateDilutionDaoDependencies() {
    if (libraryDao != null) libraryDao.setDilutionDAO(dilutionDao);
    if (daoLookup != null) daoLookup.setDaos(makeDaoLookupMap());
  }

  public SQLTargetedSequencingDAO getTargetedSequencingDao() {
    return targetedSequencingDao;
  }

  public void setTargetedSequencingDao(SQLTargetedSequencingDAO targetedSequencingDao) {
    this.targetedSequencingDao = targetedSequencingDao;
    updateTargetedSequencingDaoDependencies();
  }

  public void setDefaultTargetedSequencingDao() {
    SQLTargetedSequencingDAO dao = new SQLTargetedSequencingDAO();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityDAO(securityStore);
    setTargetedSequencingDao(dao);
  }

  private void updateTargetedSequencingDaoDependencies() {
    if (dilutionDao != null) dilutionDao.setTargetedSequencingDAO(targetedSequencingDao);
  }

  public SQLPoolDAO getPoolDao() {
    return poolDao;
  }

  public void setPoolDao(SQLPoolDAO poolDao) {
    this.poolDao = poolDao;
    updatePoolDaoDependencies();
  }

  public void setDefaultPoolDao() {
    SQLPoolDAO dao = new SQLPoolDAO();
    dao.setAutoGenerateIdentificationBarcodes(autoGenerateIdBarcodes);
    dao.setBoxDAO(boxDao);
    dao.setChangeLogDAO(changeLogDao);
    dao.setDaoLookup(daoLookup);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setExperimentDAO(experimentDao);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setNamingScheme(getNamingScheme());
    dao.setNoteDAO(noteDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityManager(securityManager);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setWatcherDAO(watcherDao);
    dao.setPoolQcDAO(poolQcDao);
    setPoolDao(dao);
  }

  private void updatePoolDaoDependencies() {
    if (libraryDao != null) libraryDao.setPoolDAO(poolDao);
    if (experimentDao != null) experimentDao.setPoolDAO(poolDao);
    if (partitionDao != null) partitionDao.setPoolDAO(poolDao);
    if (boxDao != null) boxDao.setPoolDAO(poolDao);
  }

  public SQLExperimentDAO getExperimentDao() {
    return experimentDao;
  }

  public void setExperimentDao(SQLExperimentDAO experimentDao) {
    this.experimentDao = experimentDao;
    updateExperimentDaoDependencies();
  }

  public void setDefaultExperimentDao() {
    SQLExperimentDAO dao = new SQLExperimentDAO();
    dao.setChangeLogDAO(changeLogDao);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setKitComponentDAO(kitDao);
    dao.setNamingScheme(getNamingScheme());
    dao.setPlatformDAO(platformDao);
    dao.setPoolDAO(poolDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setStudyDAO(studyDao);
    setExperimentDao(dao);
  }

  private void updateExperimentDaoDependencies() {
    if (poolDao != null) poolDao.setExperimentDAO(experimentDao);
    if (studyDao != null) studyDao.setExperimentDAO(experimentDao);
  }

  public SQLKitComponentDAO getKitDao() {
    return kitDao;
  }

  public void setKitDao(SQLKitComponentDAO kitDao) {
    this.kitDao = kitDao;
    updateKitDaoDependencies();
  }
  
    public SQLKitDescriptorDAO getKitDescriptorDao() {
    return kitDescriptorDao;
  }

  public void setKitDescriptorDao(SQLKitDescriptorDAO kitDescriptorDao) {
    this.kitDao = kitDao;
    updateKitDaoDependencies();
  }

  public void setDefaultKitDao() {
    SQLKitComponentDAO dao = new SQLKitComponentDAO();
    //dao.setChangeLogDAO(changeLogDao);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setNoteDAO(noteDao);
    //dao.setSecurityDAO(securityStore);
    setKitDao(dao);
  }

  private void updateKitDaoDependencies() {
    if (experimentDao != null) experimentDao.setKitComponentDAO(kitDao);
    if (libraryAdditionalInfoDao != null) libraryAdditionalInfoDao.setKitDescriptorStore(kitDescriptorDao);
  }

  public SQLPlatformDAO getPlatformDao() {
    return platformDao;
  }

  public void setPlatformDao(SQLPlatformDAO platformDao) {
    this.platformDao = platformDao;
    updatePlatformDaoDependencies();
  }

  public void setDefaultPlatformDao() {
    SQLPlatformDAO dao = new SQLPlatformDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    setPlatformDao(dao);
  }

  private void updatePlatformDaoDependencies() {
    if (experimentDao != null) experimentDao.setPlatformDAO(platformDao);
    if (sequencerPartitionContainerDao != null) sequencerPartitionContainerDao.setPlatformDAO(platformDao);
    if (sequencerReferenceDao != null) sequencerReferenceDao.setPlatformDAO(platformDao);
  }

  public SQLStudyDAO getStudyDao() {
    return studyDao;
  }

  public void setStudyDao(SQLStudyDAO studyDao) {
    this.studyDao = studyDao;
    updateStudyDaoDependencies();
  }

  public void setDefaultStudyDao() {
    SQLStudyDAO dao = new SQLStudyDAO();
    dao.setChangeLogDAO(changeLogDao);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setExperimentDAO(experimentDao);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setNamingScheme(getNamingScheme());
    dao.setProjectDAO(projectDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityProfileDAO(securityProfileDao);
    setStudyDao(dao);
  }

  private void updateStudyDaoDependencies() {
    if (experimentDao != null) experimentDao.setStudyDAO(studyDao);
    if (projectDao != null) projectDao.setStudyDAO(studyDao);
  }

  public SQLRunDAO getRunDao() {
    return runDao;
  }

  public void setRunDao(SQLRunDAO runDao) {
    this.runDao = runDao;
    updateRunDaoDependencies();
  }

  public void setDefaultRunDao() {
    SQLRunDAO dao = new SQLRunDAO();
    dao.setChangeLogDAO(changeLogDao);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setNamingScheme(getNamingScheme());
    dao.setNoteDAO(noteDao);
    dao.setRunQcDAO(runQcDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityManager(securityManager);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setSequencerPartitionContainerDAO(sequencerPartitionContainerDao);
    dao.setSequencerReferenceDAO(sequencerReferenceDao);
    dao.setStatusDAO(statusDao);
    dao.setWatcherDAO(watcherDao);
    dao.setSequencingParametersDao(sequencingParametersDao);
    dao.setCascadeType(CascadeType.PERSIST);
    setRunDao(dao);
  }

  private void updateRunDaoDependencies() {
    if (runQcDao != null) runQcDao.setRunDAO(runDao);
    if (sequencerPartitionContainerDao != null) sequencerPartitionContainerDao.setRunDAO(runDao);
  }

  public SQLRunQCDAO getRunQcDao() {
    return runQcDao;
  }

  public void setRunQcDao(SQLRunQCDAO runQcDao) {
    this.runQcDao = runQcDao;
    updateRunQcDependencies();
  }

  public void setDefaultRunQcDao() {
    SQLRunQCDAO dao = new SQLRunQCDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setRunDAO(runDao);
    dao.setSequencerPartitionContainerDAO(sequencerPartitionContainerDao);
    setRunQcDao(dao);
  }

  private void updateRunQcDependencies() {
    if (runDao != null) runDao.setRunQcDAO(runQcDao);
  }

  public SQLSequencerPartitionContainerDAO getSequencerPartitionContainerDao() {
    return sequencerPartitionContainerDao;
  }

  public void setSequencerPartitionContainerDao(SQLSequencerPartitionContainerDAO sequencerPartitionContainerDao) {
    this.sequencerPartitionContainerDao = sequencerPartitionContainerDao;
    updateSequencerPartitionContainerDaoDependencies();
  }

  public void setDefaultSequencerPartitionContainerDao() {
    SQLSequencerPartitionContainerDAO dao = new SQLSequencerPartitionContainerDAO();
    dao.setChangeLogDAO(changeLogDao);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setPartitionDAO(partitionDao);
    dao.setPlatformDAO(platformDao);
    dao.setRunDAO(runDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityProfileDAO(securityProfileDao);
    setSequencerPartitionContainerDao(dao);
  }

  private void updateSequencerPartitionContainerDaoDependencies() {
    if (runQcDao != null) runQcDao.setSequencerPartitionContainerDAO(sequencerPartitionContainerDao);
    if (runDao != null) runDao.setSequencerPartitionContainerDAO(sequencerPartitionContainerDao);
    if (partitionDao != null) partitionDao.setSequencerPartitionContainerDAO(sequencerPartitionContainerDao);
  }

  public SQLSequencerPoolPartitionDAO getPartitionDao() {
    return partitionDao;
  }

  public void setPartitionDao(SQLSequencerPoolPartitionDAO partitionDao) {
    this.partitionDao = partitionDao;
    updatePartitionDaoDependencies();
  }

  public void setDefaultPartitionDao() {
    SQLSequencerPoolPartitionDAO dao = new SQLSequencerPoolPartitionDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setPoolDAO(poolDao);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setSequencerPartitionContainerDAO(sequencerPartitionContainerDao);
    setPartitionDao(dao);
  }

  private void updatePartitionDaoDependencies() {
    if (sequencerPartitionContainerDao != null) sequencerPartitionContainerDao.setPartitionDAO(partitionDao);
  }

  public SQLStatusDAO getStatusDao() {
    return statusDao;
  }

  public void setStatusDao(SQLStatusDAO statusDao) {
    this.statusDao = statusDao;
    updateStatusDaoDependencies();
  }

  public void setDefaultStatusDao() {
    SQLStatusDAO dao = new SQLStatusDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    setStatusDao(dao);
  }

  private void updateStatusDaoDependencies() {
    if (runDao != null) runDao.setStatusDAO(statusDao);
  }

  public SQLSequencerReferenceDAO getSequencerReferenceDao() {
    return sequencerReferenceDao;
  }

  public void setSequencerReferenceDao(SQLSequencerReferenceDAO sequencerReferenceDao) {
    this.sequencerReferenceDao = sequencerReferenceDao;
    updateSequencerReferenceDaoDependencies();
  }

  public void setDefaultSequencerReferenceDao() {
    SQLSequencerReferenceDAO dao = new SQLSequencerReferenceDAO();
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setPlatformDAO(platformDao);
    setSequencerReferenceDao(dao);
  }

  private void updateSequencerReferenceDaoDependencies() {
    if (runDao != null) runDao.setSequencerReferenceDAO(sequencerReferenceDao);
  }

  public SQLBoxDAO getBoxDao() {
    return boxDao;
  }

  public void setBoxDao(SQLBoxDAO boxDao) {
    this.boxDao = boxDao;
    updateBoxDaoDependencies();
  }

  public void setDefaultBoxDao() {
    SQLBoxDAO dao = new SQLBoxDAO();
    dao.setAutoGenerateIdentificationBarcodes(autoGenerateIdBarcodes);
    dao.setChangeLogDAO(changeLogDao);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setLibraryDAO(libraryDao);
    dao.setNamingScheme(getNamingScheme());
    dao.setPoolDAO(poolDao);
    dao.setSampleDAO(sampleDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityProfileDAO(securityProfileDao);
    setBoxDao(dao);
  }

  private void updateBoxDaoDependencies() {
    if (poolDao != null) poolDao.setBoxDAO(boxDao);
  }

  public DefaultLabService getLabService() {
    return labService;
  }

  public void setLabService(DefaultLabService labService) {
    this.labService = labService;
    updateLabServiceDependencies();
  }

  public void setDefaultLabService() {
    DefaultLabService svc = new DefaultLabService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setInstituteDao(instituteDao);
    svc.setLabDao(labDao);
    setLabService(svc);
  }

  private void updateLabServiceDependencies() {
    if (sampleService != null) sampleService.setLabService(labService);
  }

  public HibernateLabDao getLabDao() {
    return labDao;
  }

  public void setLabDao(HibernateLabDao labDao) {
    this.labDao = labDao;
    updateLabDaoDependencies();
  }

  public void setDefaultLabDao() {
    HibernateLabDao dao = new HibernateLabDao();
    dao.setSessionFactory(sessionFactory);
    setLabDao(dao);
  }

  private void updateLabDaoDependencies() {
    if (labService != null) labService.setLabDao(labDao);
  }

  public HibernateInstituteDao getInstituteDao() {
    return instituteDao;
  }

  public void setInstituteDao(HibernateInstituteDao instituteDao) {
    this.instituteDao = instituteDao;
    updateInstituteDaoDependencies();
  }

  public void setDefaultInstituteDao() {
    HibernateInstituteDao dao = new HibernateInstituteDao();
    dao.setSessionFactory(sessionFactory);
    setInstituteDao(dao);
  }

  private void updateInstituteDaoDependencies() {
    if (labService != null) labService.setInstituteDao(instituteDao);
  }

  public HibernateDetailedQcStatusDao getDetailedQcStatusDao() {
    return detailedQcStatusDao;
  }

  public void setDetailedQcStatusDao(HibernateDetailedQcStatusDao detailedQcStatus) {
    this.detailedQcStatusDao = detailedQcStatus;
    updateDetailedQcStatusDaoDependencies();
  }

  public void setDefaultDetailedQcStatusDao() {
    HibernateDetailedQcStatusDao dao = new HibernateDetailedQcStatusDao();
    dao.setSessionFactory(sessionFactory);
    setDetailedQcStatusDao(dao);
  }

  private void updateDetailedQcStatusDaoDependencies() {
    if (sampleService != null) sampleService.setDetailedQcStatusDao(detailedQcStatusDao);
  }

  public HibernateSubprojectDao getSubprojectDao() {
    return subprojectDao;
  }

  public void setSubprojectDao(HibernateSubprojectDao subprojectDao) {
    this.subprojectDao = subprojectDao;
    updateSubprojectDaoDependencies();
  }

  public void setDefaultSubprojectDao() {
    HibernateSubprojectDao dao = new HibernateSubprojectDao();
    dao.setSessionFactory(sessionFactory);
    setSubprojectDao(dao);
  }

  private void updateSubprojectDaoDependencies() {
    if (sampleService != null) sampleService.setSubProjectDao(subprojectDao);
  }

  public HibernateTissueOriginDao getTissueOriginDao() {
    return tissueOriginDao;
  }

  public void setTissueOriginDao(HibernateTissueOriginDao tissueOriginDao) {
    this.tissueOriginDao = tissueOriginDao;
    updateTissueOriginDaoDependencies();
  }

  public void setDefaultTissueOriginDao() {
    HibernateTissueOriginDao dao = new HibernateTissueOriginDao();
    dao.setSessionFactory(sessionFactory);
    setTissueOriginDao(dao);
  }

  private void updateTissueOriginDaoDependencies() {
    if (sampleService != null) sampleService.setTissueOriginDao(tissueOriginDao);
  }

  public HibernateTissueTypeDao getTissueTypeDao() {
    return tissueTypeDao;
  }

  public void setTissueTypeDao(HibernateTissueTypeDao tissueTypeDao) {
    this.tissueTypeDao = tissueTypeDao;
    updateTissueTypeDaoDependencies();
  }

  public void setDefaultTissueTypeDao() {
    HibernateTissueTypeDao dao = new HibernateTissueTypeDao();
    dao.setSessionFactory(sessionFactory);
    setTissueTypeDao(dao);
  }

  private void updateTissueTypeDaoDependencies() {
    if (sampleService != null) sampleService.setTissueTypeDao(tissueTypeDao);
  }

  public HibernateSamplePurposeDao getSamplePurposeDao() {
    return samplePurposeDao;
  }

  public void setSamplePurposeDao(HibernateSamplePurposeDao samplePurposeDao) {
    this.samplePurposeDao = samplePurposeDao;
    updateSamplePurposeDaoDependencies();
  }

  public void setDefaultSamplePurposeDao() {
    HibernateSamplePurposeDao dao = new HibernateSamplePurposeDao();
    dao.setSessionFactory(sessionFactory);
    setSamplePurposeDao(dao);
  }

  private void updateSamplePurposeDaoDependencies() {
    if (sampleService != null) sampleService.setSamplePurposeDao(samplePurposeDao);
  }

  public HibernateTissueMaterialDao getTissueMaterialDao() {
    return tissueMaterialDao;
  }

  public void setTissueMaterialDao(HibernateTissueMaterialDao tissueMaterialDao) {
    this.tissueMaterialDao = tissueMaterialDao;
    updateTissueMaterialDaoDependencies();
  }

  public void setDefaultTissueMaterialDao() {
    HibernateTissueMaterialDao dao = new HibernateTissueMaterialDao();
    dao.setSessionFactory(sessionFactory);
    setTissueMaterialDao(dao);
  }

  private void updateTissueMaterialDaoDependencies() {
    if (sampleService != null) sampleService.setTissueMaterialDao(tissueMaterialDao);
  }

  public DefaultSampleNumberPerProjectService getSampleNumberPerProjectService() {
    return sampleNumberPerProjectService;
  }

  public void setSampleNumberPerProjectService(DefaultSampleNumberPerProjectService sampleNumberPerProjectService) {
    this.sampleNumberPerProjectService = sampleNumberPerProjectService;
    updateSampleNumberPerProjectServiceDependencies();
  }

  public void setDefaultSampleNumberPerProjectService() {
    DefaultSampleNumberPerProjectService svc = new DefaultSampleNumberPerProjectService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setSampleNumberPerProjectDao(sampleNumberPerProjectDao);
    svc.setSqlProjectDAO(projectDao);
    setSampleNumberPerProjectService(svc);
  }

  private void updateSampleNumberPerProjectServiceDependencies() {
    if (sampleService != null) sampleService.setSampleNumberPerProjectService(sampleNumberPerProjectService);
  }

  public HibernateSampleNumberPerProjectDao getSampleNumberPerProjectDao() {
    return sampleNumberPerProjectDao;
  }

  public void setSampleNumberPerProjectDao(HibernateSampleNumberPerProjectDao sampleNumberPerProjectDao) {
    this.sampleNumberPerProjectDao = sampleNumberPerProjectDao;
    updateSampleNumberPerProjectDaoDependencies();
  }

  public void setDefaultSampleNumberPerProjectDao() {
    HibernateSampleNumberPerProjectDao dao = new HibernateSampleNumberPerProjectDao();
    dao.setSessionFactory(sessionFactory);
    setSampleNumberPerProjectDao(dao);
  }

  private void updateSampleNumberPerProjectDaoDependencies() {
    if (sampleNumberPerProjectService != null) sampleNumberPerProjectService.setSampleNumberPerProjectDao(sampleNumberPerProjectDao);
  }

  public DefaultSampleValidRelationshipService getSampleValidRelationshipService() {
    return sampleValidRelationshipService;
  }

  public void setSampleValidRelationshipService(DefaultSampleValidRelationshipService sampleValidRelationshipService) {
    this.sampleValidRelationshipService = sampleValidRelationshipService;
    updateSampleValidRelationshipServiceDependencies();
  }

  public void setDefaultSampleValidRelationshipService() {
    DefaultSampleValidRelationshipService svc = new DefaultSampleValidRelationshipService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setSampleClassDao(sampleClassDao);
    svc.setSampleValidRelationshipDao(sampleValidRelationshipDao);
    setSampleValidRelationshipService(svc);
  }

  private void updateSampleValidRelationshipServiceDependencies() {
    if (sampleService != null) sampleService.setSampleValidRelationshipService(sampleValidRelationshipService);
  }

  public DefaultReferenceGenomeService getReferenceGenomeService() {
    return referenceGenomeService;
  }

  private void setReferenceGenomeService(DefaultReferenceGenomeService referenceGenomeService) {
    this.referenceGenomeService = referenceGenomeService;
  }

  public void setDefaultReferenceGenomeService() {
    DefaultReferenceGenomeService svc = new DefaultReferenceGenomeService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setReferenceGenomeDao(referenceGenomeDao);
    setReferenceGenomeService(svc);
  }

  public HibernateReferenceGenomeDao getReferenceGenomeDao() {
    return referenceGenomeDao;
  }

  public void setReferenceGenomeDao(HibernateReferenceGenomeDao referenceGenomeDao) {
    this.referenceGenomeDao = referenceGenomeDao;
    updateReferenceGenomeDaoDependencies();
  }

  public void setDefaultReferenceGenomeDao() {
    HibernateReferenceGenomeDao dao = new HibernateReferenceGenomeDao();
    dao.setSessionFactory(sessionFactory);
    setReferenceGenomeDao(dao);
  }

  private void updateReferenceGenomeDaoDependencies() {
    if (projectDao != null) projectDao.setReferenceGenomeDao(referenceGenomeDao);
    if (referenceGenomeService != null) referenceGenomeService.setReferenceGenomeDao(referenceGenomeDao);
  }

  public HibernateSampleValidRelationshipDao getSampleValidRelationshipDao() {
    return sampleValidRelationshipDao;
  }

  public void setSampleValidRelationshipDao(HibernateSampleValidRelationshipDao sampleValidRelationshipDao) {
    this.sampleValidRelationshipDao = sampleValidRelationshipDao;
    updateSampleValidRelationshipDaoDependencies();
  }

  public void setDefaultSampleValidRelationshipDao() {
    HibernateSampleValidRelationshipDao dao = new HibernateSampleValidRelationshipDao();
    dao.setSessionFactory(sessionFactory);
    setSampleValidRelationshipDao(dao);
  }

  private void updateSampleValidRelationshipDaoDependencies() {
    if (sampleValidRelationshipService != null) sampleValidRelationshipService.setSampleValidRelationshipDao(sampleValidRelationshipDao);
  }

  public HibernateLibraryAdditionalInfoDao getLibraryAdditionalInfoDao() {
    return libraryAdditionalInfoDao;
  }

  public void setLibraryAdditionalInfoDao(HibernateLibraryAdditionalInfoDao libraryAdditionalInfoDao) {
    this.libraryAdditionalInfoDao = libraryAdditionalInfoDao;
    updateLibraryAdditionalInfoDaoDependencies();
  }

  public void setDefaultLibraryAdditionalInfoDao() {
    HibernateLibraryAdditionalInfoDao dao = new HibernateLibraryAdditionalInfoDao();
    dao.setKitDescriptorStore(kitDescriptorDao);
    dao.setSessionFactory(sessionFactory);
    setLibraryAdditionalInfoDao(dao);
  }

  private void updateLibraryAdditionalInfoDaoDependencies() {
    if (libraryDao != null) libraryDao.setLibraryAdditionalInfoDao(libraryAdditionalInfoDao);
  }

  public HibernateLibraryDesignDao getLibraryDesignDao() {
    return libraryDesignDao;
  }

  public void setLibraryDesignDao(HibernateLibraryDesignDao libraryDesignDao) {
    this.libraryDesignDao = libraryDesignDao;
    updateLibraryDesignDaoDependencies();
  }

  public void setDefaultLibraryDesignDao() {
    HibernateLibraryDesignDao dao = new HibernateLibraryDesignDao();
    dao.setSessionFactory(sessionFactory);
    dao.setLibraryDao(libraryDao);
    setLibraryDesignDao(dao);
  }

  private void updateLibraryDesignDaoDependencies() {

  }

  public HibernateLibraryDesignCodeDao getLibraryDesignCodeDao() {
    return libraryDesignCodeDao;
  }

  public void setLibraryDesignCodeDao(HibernateLibraryDesignCodeDao libraryDesignCodeDao) {
    this.libraryDesignCodeDao = libraryDesignCodeDao;
    updateLibraryDesignCodeDaoDependencies();
  }

  public void setDefaultLibraryDesignCodeDao() {
    HibernateLibraryDesignCodeDao dao = new HibernateLibraryDesignCodeDao();
    dao.setSessionFactory(sessionFactory);
    setLibraryDesignCodeDao(dao);
  }

  public void updateLibraryDesignCodeDaoDependencies() {

  }

  public HibernateIndexDao getIndexDao() {
    return indexDao;
  }

  public void setIndexDao(HibernateIndexDao indexDao) {
    this.indexDao = indexDao;
    updateIndexDaoDependencies();
  }

  public void setDefaultIndexDao() {
    HibernateIndexDao dao = new HibernateIndexDao();
    dao.setSessionFactory(sessionFactory);
    setIndexDao(dao);
  }

  private void updateIndexDaoDependencies() {
    if (libraryDao != null) libraryDao.setIndexStore(indexDao);
  }

  public DaoLookup getDaoLookup() {
    return daoLookup;
  }

  public void setDaoLookup(DaoLookup daoLookup) {
    this.daoLookup = daoLookup;
    updateDaoLookupDependencies();
  }

  public void setDefaultDaoLookup() {
    DaoLookup lookup = new DaoLookup();
    lookup.setDaos(makeDaoLookupMap());
    setDaoLookup(lookup);
  }

  private Map<Class<?>, Store<?>> makeDaoLookupMap() {
    Map<Class<?>, Store<?>> daoMap = new HashMap<>();
    if (dilutionDao != null) daoMap.put(LibraryDilution.class, dilutionDao);
    return daoMap;
  }

  private void updateDaoLookupDependencies() {
    if (poolDao != null) poolDao.setDaoLookup(daoLookup);
  }

  public SQLPoolQCDAO getPoolQcDao() {
    return poolQcDao;
  }

  public void setPoolQcDao(SQLPoolQCDAO poolQcDao) {
    this.poolQcDao = poolQcDao;
    updatePoolQcDaoDependencies();
  }

  public void setDefaultPoolQcDao() {
    SQLPoolQCDAO dao = new SQLPoolQCDAO();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setPoolDAO(poolDao);
    setPoolQcDao(dao);
  }

  private void updatePoolQcDaoDependencies() {
    if (poolDao != null) poolDao.setPoolQcDAO(poolQcDao);
  }

  public void setDefaultSequencingParametersDao() {
    HibernateSequencingParametersDao dao = new HibernateSequencingParametersDao();
    dao.setSessionFactory(sessionFactory);
    dao.setPlatformStore(platformDao);
    setSequencingParametersDao(dao);
  }

  public HibernateSequencingParametersDao getSequencingParametersDao() {
    return sequencingParametersDao;
  }

  public void setSequencingParametersDao(HibernateSequencingParametersDao sequencingParametersDao) {
    this.sequencingParametersDao = sequencingParametersDao;
    updateSequencingParametersDaoDependencies();
  }



  private void updateSequencingParametersDaoDependencies() {
    if (runDao != null) runDao.setSequencingParametersDao(sequencingParametersDao);
  }

}
