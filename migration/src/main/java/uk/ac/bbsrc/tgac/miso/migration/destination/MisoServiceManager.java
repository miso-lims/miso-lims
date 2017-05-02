package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.LocalSecurityManager;

import uk.ac.bbsrc.tgac.miso.core.event.manager.PoolAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.ProjectAlertManager;
import uk.ac.bbsrc.tgac.miso.core.event.manager.RunAlertManager;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoRequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrNamingScheme;
import uk.ac.bbsrc.tgac.miso.persistence.HibernateSampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateBoxDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateIndexDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstituteDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateKitDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLabDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDilutionDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryQcDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePlatformDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePoolDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePoolableElementViewDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateRunDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateRunQcDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleNumberPerProjectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleQcDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleValidRelationshipDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSecurityDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSecurityProfileDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencerPartitionContainerDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencerReferenceDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateStudyDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTargetedSequencingDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLabService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultPoolService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultPoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleClassService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultStudyService;

/**
 * This class is used to simplify creation and wiring of MISO services. Some of the config is currently hardcoded - mainly naming schemes
 * and authentication
 */
public class MisoServiceManager {

  private final JdbcTemplate jdbcTemplate;
  private final SessionFactory sessionFactory;

  private final boolean autoGenerateIdBarcodes = false; // TODO: config option
  private NamingScheme namingScheme;

  private LocalSecurityManager securityManager; // Supports JDBC authentication only
  private MigrationAuthorizationManager authorizationManager;

  private HibernateSecurityDao securityStore;
  private HibernateSecurityProfileDao securityProfileDao;
  private HibernateProjectDao projectDao;
  private HibernateChangeLogDao changeLogDao;
  private HibernateSampleQcDao sampleQcDao;
  private HibernateLibraryDao libraryDao;
  private HibernateLibraryQcDao libraryQcDao;
  private HibernateLibraryDilutionDao dilutionDao;
  private HibernateTargetedSequencingDao targetedSequencingDao;
  private HibernatePoolDao poolDao;
  private HibernateExperimentDao experimentDao;
  private HibernateKitDao kitDao;
  private HibernatePlatformDao platformDao;
  private HibernateStudyDao studyDao;
  private HibernateRunDao runDao;
  private HibernateRunQcDao runQcDao;
  private HibernateSequencerPartitionContainerDao sequencerPartitionContainerDao;
  private HibernateStatusDao statusDao;
  private HibernateSequencerReferenceDao sequencerReferenceDao;
  private HibernateBoxDao boxDao;
  private HibernatePoolableElementViewDao poolableElementViewDao;

  private DefaultSampleClassService sampleClassService;
  private DefaultSampleService sampleService;
  private DefaultLabService labService;
  private DefaultLibraryService libraryService;
  private DefaultLibraryDilutionService dilutionService;
  private DefaultSampleNumberPerProjectService sampleNumberPerProjectService;
  private DefaultSampleValidRelationshipService sampleValidRelationshipService;
  private DefaultReferenceGenomeService referenceGenomeService;
  private DefaultStudyService studyService;
  private DefaultPoolableElementViewService poolableElementViewService;
  private DefaultPoolService poolService;

  private PoolAlertManager poolAlertManager;
  private ProjectAlertManager projectAlertManager;
  private RunAlertManager runAlertManager;

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
  private HibernateLibraryDesignDao libraryDesignDao;
  private HibernateLibraryDesignCodeDao libraryDesignCodeDao;
  private HibernateIndexDao indexDao;
  private HibernateSequencingParametersDao sequencingParametersDao;
  private HibernateReferenceGenomeDao referenceGenomeDao;

  private MisoRequestManager requestManager;

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
    m.setDefaultDilutionService();
    m.setDefaultExperimentDao();
    m.setDefaultInstituteDao();
    m.setDefaultKitDao();
    m.setDefaultLabDao();
    m.setDefaultLabService();
    m.setDefaultLibraryDao();
    m.setDefaultLibraryService();
    m.setDefaultLibraryQcDao();
    m.setDefaultPlatformDao();
    m.setDefaultPoolDao();
    m.setDefaultPoolService();
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
    m.setDefaultStudyService();
    m.setDefaultSubprojectDao();
    m.setDefaultTargetedSequencingDao();
    m.setDefaultTissueMaterialDao();
    m.setDefaultTissueOriginDao();
    m.setDefaultTissueTypeDao();
    m.setDefaultLibraryDesignDao();
    m.setDefaultLibraryDesignCodeDao();
    m.setDefaultIndexDao();
    m.setDefaultSequencingParametersDao();
    m.setDefaultPoolableElementViewDao();
    m.setDefaultPoolableElementViewService();

    // sigh
    m.setDefaultRequestManager();

    m.setDefaultPoolAlertManager();
    m.setDefaultProjectAlertManager();
    m.setDefaultRunAlertManager();

    User migrationUser = m.getUserByLoginNameInTransaction(m.getSecurityStore(), username);
    if (migrationUser == null) throw new IllegalArgumentException("User '" + username + "' not found");
    m.setUpSecurityContext(migrationUser);
    m.setAuthorizationManagerWithUser(migrationUser);
    return m;
  }

  /**
   * Hibernate needs this to be wrapped in a transaction
   * 
   * @param securityStore
   * @return User
   * @throws IOException
   */
  public User getUserByLoginNameInTransaction(HibernateSecurityDao securityStore, String username) throws IOException {

    Transaction tx = sessionFactory.getCurrentSession().beginTransaction();
    User user;
    try {
      user = securityStore.getUserByLoginName(username);
      tx.commit();
    } catch (Exception e) {
      if (tx.isActive()) tx.rollback();
      throw e;
    }
    return user;
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
      namingScheme = new OicrNamingScheme();
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
    if (libraryService != null) libraryService.setAuthorizationManager(authorizationManager);
    if (sampleNumberPerProjectService != null) sampleNumberPerProjectService.setAuthorizationManager(authorizationManager);
    if (sampleValidRelationshipService != null) sampleValidRelationshipService.setAuthorizationManager(authorizationManager);
    if (referenceGenomeService != null) referenceGenomeService.setAuthorizationManager(authorizationManager);
    if (studyService != null) studyService.setAuthorizationManager(authorizationManager);
    if (dilutionService != null) dilutionService.setAuthorizationManager(authorizationManager);
    if (poolService != null) poolService.setAuthorizationManager(authorizationManager);
  }

  public MisoRequestManager getRequestManager() {
    return requestManager;
  }

  public void setRequestManager(MisoRequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setDefaultRequestManager() {
    MisoRequestManager rm = new MisoRequestManager();
    // Set stores for entities which need names generated before creation and can't be saved via services.
    rm.setProjectStore(projectDao);
    rm.setPoolStore(poolDao);
    rm.setRunStore(runDao);
    rm.setSampleQcStore(sampleQcDao);
    rm.setNamingScheme(getNamingScheme());
    rm.setSecurityStore(securityStore);
    rm.setSecurityProfileStore(securityProfileDao);
    rm.setPoolAlertManager(poolAlertManager);
    rm.setProjectAlertManager(projectAlertManager);
    rm.setRunAlertManager(runAlertManager);
    rm.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    rm.setSecurityStore(securityStore);
    rm.setSecurityManager(securityManager);
    rm.setLibraryDilutionStore(dilutionDao);
    rm.setChangeLogStore(changeLogDao);
    rm.setSequencerPartitionContainerStore(sequencerPartitionContainerDao);
    rm.setBoxStore(boxDao);
    rm.setSampleStore(sampleDao);
    rm.setLibraryStore(libraryDao);
    setRequestManager(rm);
  }

  public HibernateSecurityDao getSecurityStore() {
    return securityStore;
  }

  public void setSecurityStore(HibernateSecurityDao securityStore) {
    this.securityStore = securityStore;
    updateSecurityStoreDependencies();
  }

  public void setDefaultSecurityStore() {
    HibernateSecurityDao store = new HibernateSecurityDao();
    store.setJdbcTemplate(jdbcTemplate);
    store.setSessionFactory(sessionFactory);
    setSecurityStore(store);
  }

  private void updateSecurityStoreDependencies() {
    if (securityManager != null) securityManager.setSecurityStore(securityStore);
    if (poolDao != null) poolDao.setSecurityStore(securityStore);
    if (requestManager != null) requestManager.setSecurityStore(securityStore);
    if (projectDao != null) projectDao.setSecurityStore(securityStore);
    if (runDao != null) runDao.setSecurityStore(securityStore);
  }

  public HibernateSecurityProfileDao getSecurityProfileDao() {
    return securityProfileDao;
  }

  public void setSecurityProfileDao(HibernateSecurityProfileDao securityProfileDao) {
    this.securityProfileDao = securityProfileDao;
    updateSecurityProfileDaoDependencies();
  }

  public void setDefaultSecurityProfileDao() {
    HibernateSecurityProfileDao store = new HibernateSecurityProfileDao();
    store.setSessionFactory(sessionFactory);
    setSecurityProfileDao(store);
  }

  private void updateSecurityProfileDaoDependencies() {
    if (requestManager != null) requestManager.setSecurityProfileStore(securityProfileDao);
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
    if (poolAlertManager != null) poolAlertManager.setSecurityManager(securityManager);
    if (projectAlertManager != null) projectAlertManager.setSecurityManager(securityManager);
    if (runAlertManager != null) runAlertManager.setSecurityManager(securityManager);
    if (requestManager != null) requestManager.setSecurityManager(securityManager);
    if (libraryService != null) libraryService.setSecurityManager(securityManager);
    if (sampleService != null) sampleService.setSecurityManager(securityManager);
  }

  public HibernateProjectDao getProjectDao() {
    return projectDao;
  }

  public void setProjectDao(HibernateProjectDao projectDao) {
    this.projectDao = projectDao;
    updateProjectDaoDependencies();
  }

  public void setDefaultProjectDao() {
    HibernateProjectDao dao = new HibernateProjectDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    dao.setSecurityStore(securityStore);
    dao.setProjectAlertManager(projectAlertManager);
    setProjectDao(dao);
  }

  private void updateProjectDaoDependencies() {
    if (sampleNumberPerProjectService != null) sampleNumberPerProjectService.setProjectStore(projectDao);
    if (requestManager != null) requestManager.setProjectStore(projectDao);
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
    svc.setSampleQcDao(sampleQcDao);
    svc.setSecurityManager(securityManager);
    setSampleService(svc);
  }

  private void updateSampleServiceDependencies() {
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
    dao.setSessionFactory(sessionFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    setSampleDao(dao);
  }

  private void updateSampleDaoDependencies() {
    if (sampleService != null) sampleService.setSampleDao(sampleDao);
    if (libraryService != null) libraryService.setSampleDao(sampleDao);
    if (requestManager != null) requestManager.setSampleStore(sampleDao);
  }

  public HibernateChangeLogDao getChangeLogDao() {
    return changeLogDao;
  }

  public void setChangeLogDao(HibernateChangeLogDao changeLogDao) {
    this.changeLogDao = changeLogDao;
    updateChangeLogDaoDependencies();
  }

  public void setDefaultChangeLogDao() {
    HibernateChangeLogDao dao = new HibernateChangeLogDao();
    dao.setSessionFactory(sessionFactory);
    setChangeLogDao(dao);
  }

  private void updateChangeLogDaoDependencies() {
    if (libraryService != null) libraryService.setChangeLogDao(changeLogDao);
    if (requestManager != null) requestManager.setChangeLogStore(changeLogDao);
  }

  public HibernateSampleQcDao getSampleQcDao() {
    return sampleQcDao;
  }

  public void setSampleQcDao(HibernateSampleQcDao sampleQcDao) {
    this.sampleQcDao = sampleQcDao;
    updateSampleQcDaoDependencies();
  }

  public void setDefaultSampleQcDao() {
    HibernateSampleQcDao dao = new HibernateSampleQcDao();
    dao.setSessionFactory(sessionFactory);
    setSampleQcDao(dao);
  }

  private void updateSampleQcDaoDependencies() {
    if (requestManager != null) requestManager.setSampleQcStore(sampleQcDao);
    if (sampleService != null) sampleService.setSampleQcDao(sampleQcDao);
  }

  public HibernateLibraryDao getLibraryDao() {
    return libraryDao;
  }

  public void setLibraryDao(HibernateLibraryDao libraryDao) {
    this.libraryDao = libraryDao;
    updateLibraryDaoDependencies();
  }

  public void setDefaultLibraryDao() {
    HibernateLibraryDao dao = new HibernateLibraryDao();
    dao.setSessionFactory(sessionFactory);
    dao.setBoxDao(boxDao);
    dao.setDetailedSampleEnabled(true);
    setLibraryDao(dao);
  }

  private void updateLibraryDaoDependencies() {
    if (libraryDesignDao != null) libraryDesignDao.setLibraryDao(libraryDao);
    if (libraryService != null) libraryService.setLibraryDao(libraryDao);
    if (dilutionService != null) dilutionService.setLibraryDao(libraryDao);
    if (requestManager != null) requestManager.setLibraryStore(libraryDao);
  }

  public DefaultLibraryService getLibraryService() {
    return libraryService;
  }

  public void setLibraryService(DefaultLibraryService libraryService) {
    this.libraryService = libraryService;
    updateLibraryServiceDependencies();
  }

  public void setDefaultLibraryService() {
    DefaultLibraryService svc = new DefaultLibraryService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    svc.setNamingScheme(getNamingScheme());
    svc.setLibraryDao(libraryDao);
    svc.setLibraryDesignDao(libraryDesignDao);
    svc.setLibraryDesignCodeDao(libraryDesignCodeDao);
    svc.setLibraryQcDao(libraryQcDao);
    svc.setIndexDao(indexDao);
    svc.setKitDao(kitDao);
    svc.setSampleDao(sampleDao);
    svc.setSecurityManager(securityManager);
    svc.setChangeLogDao(changeLogDao);
    setLibraryService(svc);
  }

  private void updateLibraryServiceDependencies() {
  }

  public HibernateLibraryQcDao getLibraryQcDao() {
    return libraryQcDao;
  }

  public void setLibraryQcDao(HibernateLibraryQcDao libraryQcDao) {
    this.libraryQcDao = libraryQcDao;
    updateLibraryQcDaoDependencies();
  }

  public void setDefaultLibraryQcDao() {
    HibernateLibraryQcDao dao = new HibernateLibraryQcDao();
    dao.setSessionFactory(sessionFactory);
    setLibraryQcDao(dao);
  }

  private void updateLibraryQcDaoDependencies() {
    if (libraryService != null) libraryService.setLibraryQcDao(libraryQcDao);
  }

  public HibernateLibraryDilutionDao getDilutionDao() {
    return dilutionDao;
  }

  public void setDilutionDao(HibernateLibraryDilutionDao dilutionDao) {
    this.dilutionDao = dilutionDao;
    updateDilutionDaoDependencies();
  }

  public void setDefaultDilutionDao() {
    HibernateLibraryDilutionDao dao = new HibernateLibraryDilutionDao();
    dao.setSessionFactory(sessionFactory);
    setDilutionDao(dao);
  }

  private void updateDilutionDaoDependencies() {
    if (dilutionService != null) dilutionService.setDilutionDao(dilutionDao);
    if (requestManager != null) requestManager.setLibraryDilutionStore(dilutionDao);
  }

  public DefaultLibraryDilutionService getDilutionService() {
    return dilutionService;
  }

  public void setDilutionService(DefaultLibraryDilutionService dilutionService) {
    this.dilutionService = dilutionService;
    updateDilutionServiceDependencies();
  }

  public void setDefaultDilutionService() {
    DefaultLibraryDilutionService svc = new DefaultLibraryDilutionService();
    svc.setDilutionDao(dilutionDao);
    svc.setAuthorizationManager(authorizationManager);
    svc.setNamingScheme(getNamingScheme());
    svc.setLibraryDao(libraryDao);
    svc.setTargetedSequencingDao(targetedSequencingDao);
    svc.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    setDilutionService(svc);
  }

  private void updateDilutionServiceDependencies() {
  }

  public HibernateTargetedSequencingDao getTargetedSequencingDao() {
    return targetedSequencingDao;
  }

  public void setTargetedSequencingDao(HibernateTargetedSequencingDao targetedSequencingDao) {
    this.targetedSequencingDao = targetedSequencingDao;
    updateTargetedSequencingDaoDependencies();
  }

  public void setDefaultTargetedSequencingDao() {
    HibernateTargetedSequencingDao dao = new HibernateTargetedSequencingDao();
    dao.setSessionFactory(sessionFactory);
    setTargetedSequencingDao(dao);
  }

  private void updateTargetedSequencingDaoDependencies() {
    if (dilutionService != null) dilutionService.setTargetedSequencingDao(targetedSequencingDao);
  }

  public HibernatePoolDao getPoolDao() {
    return poolDao;
  }

  public void setPoolDao(HibernatePoolDao poolDao) {
    this.poolDao = poolDao;
    updatePoolDaoDependencies();
  }

  public void setDefaultPoolDao() {
    HibernatePoolDao dao = new HibernatePoolDao();
    dao.setBoxStore(boxDao);
    dao.setSecurityStore(securityStore);
    dao.setSessionFactory(sessionFactory);
    setPoolDao(dao);
  }

  private void updatePoolDaoDependencies() {
    if (requestManager != null) requestManager.setPoolStore(poolDao);
  }

  public DefaultPoolService getPoolService() {
    return poolService;
  }

  public void setPoolService(DefaultPoolService service) {
    this.poolService = service;
    updatePoolServiceDependencies();
  }

  public void setDefaultPoolService() {
    DefaultPoolService service = new DefaultPoolService();
    service.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    service.setAuthorizationManager(authorizationManager);
    service.setNamingScheme(namingScheme);
    service.setPoolAlertManager(poolAlertManager);
    service.setPoolStore(poolDao);
    service.setPoolableElementViewService(poolableElementViewService);
    setPoolService(service);
  }

  private void updatePoolServiceDependencies() {
    // no dependants
  }

  public HibernateExperimentDao getExperimentDao() {
    return experimentDao;
  }

  public void setExperimentDao(HibernateExperimentDao experimentDao) {
    this.experimentDao = experimentDao;
    updateExperimentDaoDependencies();
  }

  public void setDefaultExperimentDao() {
    HibernateExperimentDao dao = new HibernateExperimentDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    setExperimentDao(dao);
  }

  private void updateExperimentDaoDependencies() {
  }

  public HibernateKitDao getKitDao() {
    return kitDao;
  }

  public void setKitDao(HibernateKitDao kitDao) {
    this.kitDao = kitDao;
    updateKitDaoDependencies();
  }

  public void setDefaultKitDao() {
    HibernateKitDao dao = new HibernateKitDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    setKitDao(dao);
  }

  private void updateKitDaoDependencies() {
    if (libraryService != null) libraryService.setKitDao(kitDao);
  }

  public HibernatePlatformDao getPlatformDao() {
    return platformDao;
  }

  public void setPlatformDao(HibernatePlatformDao platformDao) {
    this.platformDao = platformDao;
    updatePlatformDaoDependencies();
  }

  public void setDefaultPlatformDao() {
    HibernatePlatformDao dao = new HibernatePlatformDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    setPlatformDao(dao);
  }

  private void updatePlatformDaoDependencies() {
    if (requestManager != null) requestManager.setPlatformStore(platformDao);
  }

  public HibernateStudyDao getStudyDao() {
    return studyDao;
  }

  public void setStudyDao(HibernateStudyDao studyDao) {
    this.studyDao = studyDao;
    updateStudyDaoDependencies();
  }

  public void setDefaultStudyDao() {
    HibernateStudyDao dao = new HibernateStudyDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    setStudyDao(dao);
  }

  private void updateStudyDaoDependencies() {
  }

  public HibernateRunDao getRunDao() {
    return runDao;
  }

  public void setRunDao(HibernateRunDao runDao) {
    this.runDao = runDao;
    updateRunDaoDependencies();
  }

  public void setDefaultRunDao() {
    HibernateRunDao dao = new HibernateRunDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    dao.setSecurityStore(securityStore);
    setRunDao(dao);
  }

  private void updateRunDaoDependencies() {
    if (requestManager != null) requestManager.setRunStore(runDao);
  }

  public HibernateRunQcDao getRunQcDao() {
    return runQcDao;
  }

  public void setRunQcDao(HibernateRunQcDao runQcDao) {
    this.runQcDao = runQcDao;
    updateRunQcDependencies();
  }

  public void setDefaultRunQcDao() {
    HibernateRunQcDao dao = new HibernateRunQcDao();
    dao.setSessionFactory(sessionFactory);
    setRunQcDao(dao);
  }

  private void updateRunQcDependencies() {
  }

  public HibernateSequencerPartitionContainerDao getSequencerPartitionContainerDao() {
    return sequencerPartitionContainerDao;
  }

  public void setSequencerPartitionContainerDao(HibernateSequencerPartitionContainerDao sequencerPartitionContainerDao) {
    this.sequencerPartitionContainerDao = sequencerPartitionContainerDao;
    updateSequencerPartitionContainerDaoDependencies();
  }

  public void setDefaultSequencerPartitionContainerDao() {
    HibernateSequencerPartitionContainerDao dao = new HibernateSequencerPartitionContainerDao();
    dao.setSessionFactory(sessionFactory);
    setSequencerPartitionContainerDao(dao);
  }

  private void updateSequencerPartitionContainerDaoDependencies() {
    if (requestManager != null) requestManager.setSequencerPartitionContainerStore(getSequencerPartitionContainerDao());
  }

  public HibernateStatusDao getStatusDao() {
    return statusDao;
  }

  public void setStatusDao(HibernateStatusDao statusDao) {
    this.statusDao = statusDao;
    updateStatusDaoDependencies();
  }

  public void setDefaultStatusDao() {
    HibernateStatusDao dao = new HibernateStatusDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    setStatusDao(dao);
  }

  private void updateStatusDaoDependencies() {
  }

  public HibernateSequencerReferenceDao getSequencerReferenceDao() {
    return sequencerReferenceDao;
  }

  public void setSequencerReferenceDao(HibernateSequencerReferenceDao sequencerReferenceDao) {
    this.sequencerReferenceDao = sequencerReferenceDao;
    updateSequencerReferenceDaoDependencies();
  }

  public void setDefaultSequencerReferenceDao() {
    HibernateSequencerReferenceDao dao = new HibernateSequencerReferenceDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    setSequencerReferenceDao(dao);
  }

  private void updateSequencerReferenceDaoDependencies() {
  }

  public HibernateBoxDao getBoxDao() {
    return boxDao;
  }

  public void setBoxDao(HibernateBoxDao boxDao) {
    this.boxDao = boxDao;
    updateBoxDaoDependencies();
  }

  public void setDefaultBoxDao() {
    HibernateBoxDao dao = new HibernateBoxDao();
    dao.setSessionFactory(sessionFactory);
    dao.setJdbcTemplate(jdbcTemplate);
    setBoxDao(dao);
  }

  private void updateBoxDaoDependencies() {
    if (poolDao != null) poolDao.setBoxStore(boxDao);
    if (requestManager != null) requestManager.setBoxStore(boxDao);
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
    svc.setProjectStore(projectDao);
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

  public DefaultStudyService getStudyService() {
    return studyService;
  }

  public void setStudyService(DefaultStudyService studyService) {
    this.studyService = studyService;
    updateStudyServiceDependencies();
  }

  public void setDefaultStudyService() {
    DefaultStudyService svc = new DefaultStudyService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setProjectStore(projectDao);
    svc.setStudyStore(studyDao);
    svc.setNamingScheme(getNamingScheme());
    setStudyService(svc);
  }

  private void updateStudyServiceDependencies() {
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
    if (libraryService != null) libraryService.setLibraryDesignDao(libraryDesignDao);
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

  private void updateLibraryDesignCodeDaoDependencies() {
    if (libraryService != null) libraryService.setLibraryDesignCodeDao(libraryDesignCodeDao);
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
    if (libraryService != null) libraryService.setIndexDao(indexDao);
  }

  public void setDefaultSequencingParametersDao() {
    HibernateSequencingParametersDao dao = new HibernateSequencingParametersDao();
    dao.setSessionFactory(sessionFactory);
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
  }

  public PoolAlertManager getPoolAlertManager() {
    return poolAlertManager;
  }

  public void setPoolAlertManager(PoolAlertManager poolAlertManager) {
    this.poolAlertManager = poolAlertManager;
    updatePoolAlertManagerDependencies();
  }

  public void setDefaultPoolAlertManager() {
    PoolAlertManager pam = new PoolAlertManager();
    pam.setSecurityManager(securityManager);
    setPoolAlertManager(pam);
  }

  private void updatePoolAlertManagerDependencies() {
    if (requestManager != null) requestManager.setPoolAlertManager(poolAlertManager);
  }

  public ProjectAlertManager getProjectAlertManager() {
    return projectAlertManager;
  }

  public void setProjectAlertManager(ProjectAlertManager projectAlertManager) {
    this.projectAlertManager = projectAlertManager;
    updateProjectAlertManagerDependencies();
  }

  public void setDefaultProjectAlertManager() {
    ProjectAlertManager pam = new ProjectAlertManager();
    pam.setSecurityManager(securityManager);
    setProjectAlertManager(pam);
  }

  private void updateProjectAlertManagerDependencies() {
    if (projectDao != null) projectDao.setProjectAlertManager(projectAlertManager);
  }

  public RunAlertManager getRunAlertManager() {
    return runAlertManager;
  }

  public void setRunAlertManager(RunAlertManager runAlertManager) {
    this.runAlertManager = runAlertManager;
    updateRunAlertManagerDependencies();
  }

  public void setDefaultRunAlertManager() {
    RunAlertManager ram = new RunAlertManager();
    ram.setSecurityManager(securityManager);
    setRunAlertManager(ram);
  }

  private void updateRunAlertManagerDependencies() {
    if (requestManager != null) requestManager.setRunAlertManager(runAlertManager);
  }

  public HibernatePoolableElementViewDao getPoolableElementViewDao() {
    return this.poolableElementViewDao;
  }

  public void setPoolableElementViewDao(HibernatePoolableElementViewDao dao) {
    this.poolableElementViewDao = dao;
    updatePoolableElementViewDaoDependencies();
  }

  public void setDefaultPoolableElementViewDao() {
    HibernatePoolableElementViewDao dao = new HibernatePoolableElementViewDao();
    dao.setSessionFactory(sessionFactory);
    setPoolableElementViewDao(dao);
  }

  private void updatePoolableElementViewDaoDependencies() {
    if (poolableElementViewService != null) poolableElementViewService.setPoolableElementViewDao(poolableElementViewDao);
  }

  public DefaultPoolableElementViewService getPoolableElementViewService() {
    return this.poolableElementViewService;
  }

  public void setPoolableElementViewService(DefaultPoolableElementViewService service) {
    this.poolableElementViewService = service;
    updatePoolableElementViewServiceDependencies();
  }

  public void setDefaultPoolableElementViewService() {
    DefaultPoolableElementViewService service = new DefaultPoolableElementViewService();
    service.setPoolableElementViewDao(poolableElementViewDao);
    setPoolableElementViewService(service);
  }

  private void updatePoolableElementViewServiceDependencies() {
    if (poolService != null) poolService.setPoolableElementViewService(poolableElementViewService);
  }

}
