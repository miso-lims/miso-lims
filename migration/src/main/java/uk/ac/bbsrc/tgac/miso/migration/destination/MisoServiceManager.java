package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;

import javax.persistence.CascadeType;

import org.hibernate.SessionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultEntityNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrLibraryNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrSampleAliasGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrSampleNamingScheme;
import uk.ac.bbsrc.tgac.miso.persistence.HibernateSampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateIdentityDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstituteDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLabDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateQcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleAdditionalInfoDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleAnalyteDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleNumberPerProjectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleTissueDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleValidRelationshipDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultIdentityService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLabService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleAnalyteService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleClassService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleTissueService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLBoxDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLChangeLogDAO;
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
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLRunQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSampleQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSecurityDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSecurityProfileDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerPartitionContainerDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerPoolPartitionDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerReferenceDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLStatusDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLStudyDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLTargetedResequencingDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLWatcherDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DaoLookup;

/**
 * This class is used to simplify creation and wiring of MISO services. Some of the config is currently
 * hardcoded - mainly naming schemes and authentication
 */
public class MisoServiceManager {

  private final JdbcTemplate jdbcTemplate;
  private final SessionFactory sessionFactory;
  
  private final DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();
  private final boolean autoGenerateIdBarcodes = false; // TODO: config option
  private MisoNamingScheme<Sample> sampleNamingScheme;
  private MisoNamingScheme<Library> libraryNamingScheme;
  
  private LocalSecurityManager securityManager; // Supports JDBC authentication only
  private MigrationAuthorizationManager authorizationManager;
  private final DaoLookup daoLookup = null;
  
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
  private SQLTargetedResequencingDAO targetedResequencingDao;
  private SQLPoolDAO poolDao;
  private SQLExperimentDAO experimentDao;
  private SQLKitDAO kitDao;
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
  private DefaultIdentityService identityService;
  private DefaultSampleTissueService sampleTissueService;
  private DefaultSampleAdditionalInfoService sampleAdditionalInfoService;
  private DefaultLabService labService;
  private DefaultSampleAnalyteService sampleAnalyteService;
  private DefaultSampleNumberPerProjectService sampleNumberPerProjectService;
  private DefaultSampleValidRelationshipService sampleValidRelationshipService;
  
  private HibernateSampleClassDao sampleClassDao;
  private HibernateSampleDao sampleDao;
  private HibernateSampleAdditionalInfoDao sampleAdditionalInfoDao;
  private HibernateIdentityDao identityDao;
  private HibernateSampleTissueDao sampleTissueDao;
  private HibernateLabDao labDao;
  private HibernateInstituteDao instituteDao;
  private HibernateQcPassedDetailDao qcPassedDetailDao;
  private HibernateSubprojectDao subprojectDao;
  private HibernateTissueOriginDao tissueOriginDao;
  private HibernateTissueTypeDao tissueTypeDao;
  private HibernateSampleAnalyteDao sampleAnalyteDao;
  private HibernateSamplePurposeDao samplePurposeDao;
  private HibernateTissueMaterialDao tissueMaterialDao;
  private HibernateSampleNumberPerProjectDao sampleNumberPerProjectDao;
  private HibernateSampleValidRelationshipDao sampleValidRelationshipDao;
  private HibernateLibraryAdditionalInfoDao libraryAdditionalInfoDao;
  
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
    m.setDefaultIdentityDao();
    m.setDefaultIdentityService();
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
    m.setDefaultProjectDao();
    m.setDefaultQcPassedDetailDao();
    m.setDefaultRunDao();
    m.setDefaultRunQcDao();
    m.setDefaultSampleAdditionalInfoDao();
    m.setDefaultSampleAdditionalInfoService();
    m.setDefaultSampleAnalyteDao();
    m.setDefaultSampleAnalyteService();
    m.setDefaultSampleClassDao();
    m.setDefaultSampleClassService();
    m.setDefaultSampleDao();
    m.setDefaultSampleNumberPerProjectService();
    m.setDefaultSamplePurposeDao();
    m.setDefaultSampleQcDao();
    m.setDefaultSampleService();
    m.setDefaultSampleTissueDao();
    m.setDefaultSampleTissueService();
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
    m.setDefaultTargetedResequencingDao();
    m.setDefaultTissueMaterialDao();
    m.setDefaultTissueOriginDao();
    m.setDefaultTissueTypeDao();
    m.setDefaultWatcherDao();
    
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
  
  //TODO: Add naming scheme config instead of hard-coding
  private MisoNamingScheme<Sample> getSampleNamingScheme() {
    if (sampleNamingScheme == null) {
      sampleNamingScheme = new OicrSampleNamingScheme();
      sampleNamingScheme.registerCustomNameGenerator("alias", new OicrSampleAliasGenerator());
    }
    return sampleNamingScheme;
  }
  
  private MisoNamingScheme<Library> getLibraryNamingScheme() {
    if (libraryNamingScheme == null) {
      libraryNamingScheme = new OicrLibraryNamingScheme();
    }
    return libraryNamingScheme;
  }
  
  private <T extends Nameable> MisoNamingScheme<T> getNameableNamingScheme(Class<T> clazz) {
    return new DefaultEntityNamingScheme<T>(clazz);
  }
  
  public <T extends MisoNamingScheme<Nameable>> void setNameableNamingScheme(Class<T> clazz) {
    
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
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setAuthorizationManager(authorizationManager);
    if (identityService != null) identityService.setAuthorizationManager(authorizationManager);
    if (sampleAnalyteService != null) sampleAnalyteService.setAuthorizationManager(authorizationManager);
    if (labService != null) labService.setAuthorizationManager(authorizationManager);
    if (sampleNumberPerProjectService != null) sampleNumberPerProjectService.setAuthorizationManager(authorizationManager);
    if (sampleValidRelationshipService != null) sampleValidRelationshipService.setAuthorizationManager(authorizationManager);
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
    if (targetedResequencingDao != null) targetedResequencingDao.setSecurityDAO(securityStore);
    if (poolDao != null) poolDao.setSecurityDAO(securityStore);
    if (experimentDao != null) experimentDao.setSecurityDAO(securityStore);
    if (kitDao != null) kitDao.setSecurityDAO(securityStore);
    if (studyDao != null) studyDao.setSecurityDAO(securityStore);
    if (runDao != null) runDao.setSecurityDAO(securityStore);
    if (sequencerPartitionContainerDao != null) sequencerPartitionContainerDao.setSecurityDAO(securityStore);
    if (boxDao != null) boxDao.setSecurityDAO(securityStore);
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
    if (sampleTissueService != null) sampleTissueService.setSecurityManager(securityManager);
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
    dao.setNamingScheme(getNameableNamingScheme(Project.class));
    dao.setWatcherDAO(watcherDao);
    dao.setDataObjectFactory(dataObjectFactory);
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
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setSampleClassDao(sampleClassDao);
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
    svc.setNamingScheme(getNameableNamingScheme(Sample.class));
    svc.setSampleNamingScheme(getSampleNamingScheme());
    svc.setProjectStore(projectDao);
    svc.setSampleAdditionalInfoService(sampleAdditionalInfoService);
    svc.setIdentityService(identityService);
    svc.setSampleTissueService(sampleTissueService);
    svc.setSampleAnalyteService(sampleAnalyteService);
    svc.setKitStore(kitDao);
    svc.setQcPassedDetailDao(qcPassedDetailDao);
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
    dao.setNamingScheme(getNameableNamingScheme(Sample.class));
    dao.setNoteDao(noteDao);
    dao.setSampleNamingScheme(getSampleNamingScheme());
    dao.getSampleNamingScheme().registerCustomNameGenerator("alias", new OicrSampleAliasGenerator());
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
    if (experimentDao != null) experimentDao.setSampleDAO(sampleDao);
    if (boxDao != null) boxDao.setSampleDAO(sampleDao);
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setSampleDao(sampleDao);
    if (sampleAnalyteService != null) sampleAnalyteService.setSampleDao(sampleDao);
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
    setChangeLogDao(dao);
  }
  
  private void updateChangeLogDaoDependencies() {
    if (sampleDao != null) sampleDao.setChangeLogDao(changeLogDao);
    if (libraryDao != null) libraryDao.setChangeLogDAO(changeLogDao);
    if (poolDao != null) poolDao.setChangeLogDAO(changeLogDao);
    if (experimentDao != null) experimentDao.setChangeLogDAO(changeLogDao);
    if (kitDao != null) kitDao.setChangeLogDAO(changeLogDao);
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
    dao.setAutoGenerateIdentificationBarcodes(autoGenerateIdBarcodes);
    dao.setBoxDAO(boxDao);
    dao.setChangeLogDAO(changeLogDao);
    dao.setDataObjectFactory(dataObjectFactory);
    dao.setDilutionDAO(dilutionDao);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setLibraryNamingScheme(getLibraryNamingScheme());
    dao.setLibraryQcDAO(libraryQcDao);
    dao.setNamingScheme(getNameableNamingScheme(Library.class));
    dao.setNoteDAO(noteDao);
    dao.setPoolDAO(poolDao);
    dao.setSampleDAO(sampleDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setLibraryAdditionalInfoDao(libraryAdditionalInfoDao);
    setLibraryDao(dao);
  }
  
  private void updateLibraryDaoDependencies() {
    if (sampleDao != null) sampleDao.setLibraryDao(libraryDao);
    if (libraryQcDao != null) libraryQcDao.setLibraryDAO(libraryDao);
    if (dilutionDao != null) dilutionDao.setLibraryDAO(libraryDao);
    if (boxDao != null) boxDao.setLibraryDAO(libraryDao);
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
    dao.setNamingScheme(getNameableNamingScheme(LibraryDilution.class));
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setTargetedResequencingDAO(targetedResequencingDao);
    setDilutionDao(dao);
  }
  
  private void updateDilutionDaoDependencies() {
    if (libraryDao != null) libraryDao.setDilutionDAO(dilutionDao);
  }

  public SQLTargetedResequencingDAO getTargetedResequencingDao() {
    return targetedResequencingDao;
  }

  public void setTargetedResequencingDao(SQLTargetedResequencingDAO targetedResequencingDao) {
    this.targetedResequencingDao = targetedResequencingDao;
    updateTargetedResequencingDaoDependencies();
  }
  
  public void setDefaultTargetedResequencingDao() {
    SQLTargetedResequencingDAO dao = new SQLTargetedResequencingDAO();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSecurityDAO(securityStore);
    setTargetedResequencingDao(dao);
  }
  
  private void updateTargetedResequencingDaoDependencies() {
    if (dilutionDao != null) dilutionDao.setTargetedResequencingDAO(targetedResequencingDao);
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
    dao.setNamingScheme(new DefaultEntityNamingScheme<Pool<? extends Poolable>>()); // TODO: config
    dao.setNoteDAO(noteDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityManager(securityManager);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setWatcherDAO(watcherDao);
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
    dao.setKitDAO(kitDao);
    dao.setNamingScheme(getNameableNamingScheme(Experiment.class));
    dao.setPlatformDAO(platformDao);
    dao.setPoolDAO(poolDao);
    dao.setRunDAO(runDao);
    dao.setSampleDAO(sampleDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setStudyDAO(studyDao);
    setExperimentDao(dao);
  }
  
  private void updateExperimentDaoDependencies() {
    if (poolDao != null) poolDao.setExperimentDAO(experimentDao);
    if (studyDao != null) studyDao.setExperimentDAO(experimentDao);
  }

  public SQLKitDAO getKitDao() {
    return kitDao;
  }

  public void setKitDao(SQLKitDAO kitDao) {
    this.kitDao = kitDao;
    updateKitDaoDependencies();
  }
  
  public void setDefaultKitDao() {
    SQLKitDAO dao = new SQLKitDAO();
    dao.setChangeLogDAO(changeLogDao);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setNoteDAO(noteDao);
    dao.setSecurityDAO(securityStore);
    setKitDao(dao);
  }
  
  private void updateKitDaoDependencies() {
    if (experimentDao != null) experimentDao.setKitDAO(kitDao);
    if (sampleService != null) sampleService.setKitStore(kitDao);
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setSqlKitDao(kitDao);
    if (libraryAdditionalInfoDao != null) libraryAdditionalInfoDao.setKitStore(kitDao);
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
    dao.setNamingScheme(getNameableNamingScheme(Study.class));
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
    dao.setNamingScheme(getNameableNamingScheme(Run.class));
    dao.setNoteDAO(noteDao);
    dao.setRunQcDAO(runQcDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityManager(securityManager);
    dao.setSecurityProfileDAO(securityProfileDao);
    dao.setSequencerPartitionContainerDAO(sequencerPartitionContainerDao);
    dao.setSequencerReferenceDAO(sequencerReferenceDao);
    dao.setStatusDAO(statusDao);
    dao.setWatcherDAO(watcherDao);
    dao.setCascadeType(CascadeType.PERSIST);
    setRunDao(dao);
  }
  
  private void updateRunDaoDependencies() {
    if (experimentDao != null) experimentDao.setRunDAO(runDao);
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
    dao.setNamingScheme(new DefaultEntityNamingScheme<SequencerPartitionContainer<SequencerPoolPartition>>()); // TODO: naming scheme config
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
    dao.setNamingScheme(getNameableNamingScheme(Box.class));
    dao.setPoolDAO(poolDao);
    dao.setSampleDAO(sampleDao);
    dao.setSecurityDAO(securityStore);
    dao.setSecurityProfileDAO(securityProfileDao);
    setBoxDao(dao);
  }
  
  private void updateBoxDaoDependencies() {
    if (poolDao != null) poolDao.setBoxDAO(boxDao);
  }

  public DefaultIdentityService getIdentityService() {
    return identityService;
  }

  public void setIdentityService(DefaultIdentityService identityService) {
    this.identityService = identityService;
    updateIdentityServiceDependencies();
  }
  
  public void setDefaultIdentityService() {
    DefaultIdentityService svc = new DefaultIdentityService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setIdentityDao(identityDao);
    setIdentityService(svc);
  }
  
  private void updateIdentityServiceDependencies() {
    if (sampleService != null) sampleService.setIdentityService(identityService);
  }

  public HibernateIdentityDao getIdentityDao() {
    return identityDao;
  }

  public void setIdentityDao(HibernateIdentityDao identityDao) {
    this.identityDao = identityDao;
    updateIdentityDaoDependencies();
  }
  
  public void setDefaultIdentityDao() {
    HibernateIdentityDao dao = new HibernateIdentityDao();
    dao.setSessionFactory(sessionFactory);
    setIdentityDao(dao);
  }
  
  private void updateIdentityDaoDependencies() {
    if (identityService != null) identityService.setIdentityDao(identityDao);
  }

  public DefaultSampleTissueService getSampleTissueService() {
    return sampleTissueService;
  }

  public void setSampleTissueService(DefaultSampleTissueService sampleTissueService) {
    this.sampleTissueService = sampleTissueService;
    updateSampleTissueServiceDependencies();
  }
  
  public void setDefaultSampleTissueService() {
    DefaultSampleTissueService svc = new DefaultSampleTissueService();
    svc.setSecurityManager(securityManager);
    svc.setSampleTissueDao(sampleTissueDao);
    setSampleTissueService(sampleTissueService);
  }
  
  private void updateSampleTissueServiceDependencies() {
    if (sampleService != null) sampleService.setSampleTissueService(sampleTissueService);
  }

  public HibernateSampleTissueDao getSampleTissueDao() {
    return sampleTissueDao;
  }

  public void setSampleTissueDao(HibernateSampleTissueDao sampleTissueDao) {
    this.sampleTissueDao = sampleTissueDao;
    updateSampleTissueDaoDependencies();
  }
  
  public void setDefaultSampleTissueDao() {
    HibernateSampleTissueDao dao = new HibernateSampleTissueDao();
    dao.setSessionFactory(sessionFactory);
    setSampleTissueDao(dao);
  }
  
  private void updateSampleTissueDaoDependencies() {
    if (sampleTissueService != null) sampleTissueService.setSampleTissueDao(sampleTissueDao);
  }

  public HibernateSampleAdditionalInfoDao getSampleAdditionalInfoDao() {
    return sampleAdditionalInfoDao;
  }

  public void setSampleAdditionalInfoDao(HibernateSampleAdditionalInfoDao sampleAdditionalInfoDao) {
    this.sampleAdditionalInfoDao = sampleAdditionalInfoDao;
    updateSampleAdditionalInfoDaoDependencies();
  }
  
  public void setDefaultSampleAdditionalInfoDao() {
    HibernateSampleAdditionalInfoDao dao = new HibernateSampleAdditionalInfoDao();
    dao.setKitStore(kitDao);
    dao.setSessionFactory(sessionFactory);
    setSampleAdditionalInfoDao(dao);
  }
  
  private void updateSampleAdditionalInfoDaoDependencies() {
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setSampleAdditionalInfoDao(sampleAdditionalInfoDao);
  }

  public DefaultSampleAdditionalInfoService getSampleAdditionalInfoService() {
    return sampleAdditionalInfoService;
  }

  public void setSampleAdditionalInfoService(DefaultSampleAdditionalInfoService sampleAdditionalInfoService) {
    this.sampleAdditionalInfoService = sampleAdditionalInfoService;
    updateSampleAdditionalInfoServiceDependencies();
  }
  
  public void setDefaultSampleAdditionalInfoService() {
    DefaultSampleAdditionalInfoService svc = new DefaultSampleAdditionalInfoService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setLabService(labService);
    svc.setQcPassedDetailDao(qcPassedDetailDao);
    svc.setSampleAdditionalInfoDao(sampleAdditionalInfoDao);
    svc.setSampleClassDao(sampleClassDao);
    svc.setSampleDao(sampleDao);
    svc.setSqlKitDao(kitDao);
    svc.setSubprojectDao(subprojectDao);
    svc.setTissueOriginDao(tissueOriginDao);
    svc.setTissueTypeDao(tissueTypeDao);
    setSampleAdditionalInfoService(svc);
  }
  
  private void updateSampleAdditionalInfoServiceDependencies() {
    if (sampleService != null) sampleService.setSampleAdditionalInfoService(sampleAdditionalInfoService);
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
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setLabService(labService);
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

  public HibernateQcPassedDetailDao getQcPassedDetailDao() {
    return qcPassedDetailDao;
  }

  public void setQcPassedDetailDao(HibernateQcPassedDetailDao qcPassedDetailDao) {
    this.qcPassedDetailDao = qcPassedDetailDao;
    updateQcPassedDetailDaoDependencies();
  }
  
  public void setDefaultQcPassedDetailDao() {
    HibernateQcPassedDetailDao dao = new HibernateQcPassedDetailDao();
    dao.setSessionFactory(sessionFactory);
    setQcPassedDetailDao(dao);
  }
  
  private void updateQcPassedDetailDaoDependencies() {
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setQcPassedDetailDao(qcPassedDetailDao);
    if (sampleService != null) sampleService.setQcPassedDetailDao(qcPassedDetailDao);
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
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setSubprojectDao(subprojectDao);
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
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setTissueOriginDao(tissueOriginDao);
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
    if (sampleAdditionalInfoService != null) sampleAdditionalInfoService.setTissueTypeDao(tissueTypeDao);
    if (sampleService != null) sampleService.setTissueTypeDao(tissueTypeDao);
  }

  public DefaultSampleAnalyteService getSampleAnalyteService() {
    return sampleAnalyteService;
  }

  public void setSampleAnalyteService(DefaultSampleAnalyteService sampleAnalyteService) {
    this.sampleAnalyteService = sampleAnalyteService;
    updateSampleAnalyteServiceDependencies();
  }
  
  public void setDefaultSampleAnalyteService() {
    DefaultSampleAnalyteService svc = new DefaultSampleAnalyteService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setSampleAnalyteDao(sampleAnalyteDao);
    svc.setSampleDao(sampleDao);
    svc.setSamplePurposeDao(samplePurposeDao);
    svc.setTissueMaterialDao(tissueMaterialDao);
    setSampleAnalyteService(svc);
  }
  
  private void updateSampleAnalyteServiceDependencies() {
    if (sampleService != null) sampleService.setSampleAnalyteService(sampleAnalyteService);
  }

  public HibernateSampleAnalyteDao getSampleAnalyteDao() {
    return sampleAnalyteDao;
  }

  public void setSampleAnalyteDao(HibernateSampleAnalyteDao sampleAnalyteDao) {
    this.sampleAnalyteDao = sampleAnalyteDao;
    updateSampleAnalyteDaoDependencies();
  }
  
  public void setDefaultSampleAnalyteDao() {
    HibernateSampleAnalyteDao dao = new HibernateSampleAnalyteDao();
    dao.setSessionFactory(sessionFactory);
    setSampleAnalyteDao(dao);
  }
  
  private void updateSampleAnalyteDaoDependencies() {
    if (sampleAnalyteService != null) sampleAnalyteService.setSampleAnalyteDao(sampleAnalyteDao);
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
    if (sampleAnalyteService != null) sampleAnalyteService.setSamplePurposeDao(samplePurposeDao);
    if (sampleService != null) sampleService.setSampleAnalyteService(sampleAnalyteService);
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
    if (sampleAnalyteService != null) sampleAnalyteService.setTissueMaterialDao(tissueMaterialDao);
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
    dao.setKitStore(kitDao);
    dao.setSessionFactory(sessionFactory);
    setLibraryAdditionalInfoDao(dao);
  }
  
  public void updateLibraryAdditionalInfoDaoDependencies() {
    if (libraryDao != null) libraryDao.setLibraryAdditionalInfoDao(libraryAdditionalInfoDao);
  }

}
