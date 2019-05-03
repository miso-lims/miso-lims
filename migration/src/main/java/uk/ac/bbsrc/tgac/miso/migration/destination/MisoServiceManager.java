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

import uk.ac.bbsrc.tgac.miso.core.security.SuperuserAuthentication;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrNamingScheme;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateBoxDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateIndexDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstituteDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstrumentDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstrumentModelDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateKitDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLabDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDilutionDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryQcDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePoolDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePoolOrderDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePoolableElementViewDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateQcTypeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateReferenceGenomeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateRunDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleNumberPerProjectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleQcDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSampleValidRelationshipDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSecurityDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencerPartitionContainerDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateStudyDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTargetedSequencingDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultBoxService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultContainerService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultExperimentService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultIndexService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultInstrumentModelService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultInstrumentService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultKitService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLabService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryDesignService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultPoolOrderService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultPoolService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultPoolableElementViewService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultProjectService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultQualityControlService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultRunService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleClassService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultStudyService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultTargetedSequencingService;

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
  private HibernateInstrumentModelDao platformDao;
  private HibernateStudyDao studyDao;
  private HibernateRunDao runDao;
  private HibernateSequencerPartitionContainerDao sequencerPartitionContainerDao;
  private HibernateInstrumentDao instrumentDao;
  private HibernateBoxDao boxDao;
  private HibernatePoolableElementViewDao poolableElementViewDao;
  private HibernatePoolOrderDao poolOrderDao;

  private DefaultChangeLogService changeLogService;
  private DefaultContainerService containerService;
  private DefaultExperimentService experimentService;
  private DefaultSampleClassService sampleClassService;
  private DefaultSampleService sampleService;
  private DefaultIndexService indexService;
  private DefaultKitService kitService;
  private DefaultLabService labService;
  private DefaultLibraryService libraryService;
  private DefaultLibraryDesignService libraryDesignService;
  private DefaultLibraryDesignCodeService libraryDesignCodeService;
  private DefaultLibraryDilutionService dilutionService;
  private DefaultInstrumentModelService platformService;
  private DefaultSampleNumberPerProjectService sampleNumberPerProjectService;
  private DefaultSampleValidRelationshipService sampleValidRelationshipService;
  private DefaultReferenceGenomeService referenceGenomeService;
  private DefaultRunService runService;
  private DefaultInstrumentService instrumentService;
  private DefaultSequencingParametersService sequencingParametersService;
  private DefaultStudyService studyService;
  private DefaultPoolableElementViewService poolableElementViewService;
  private DefaultPoolOrderService poolOrderService;
  private DefaultPoolService poolService;
  private DefaultTargetedSequencingService targetedSequencingService;
  private DefaultBoxService boxService;
  private DefaultQualityControlService qcService;

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
  private HibernateQcTypeDao qcTypeDao;

  private DefaultProjectService projectService;

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
    m.setDefaultChangeLogService();
    m.setDefaultContainerService();
    m.setDefaultDilutionDao();
    m.setDefaultDilutionService();
    m.setDefaultExperimentDao();
    m.setDefaultExperimentService();
    m.setDefaultIndexService();
    m.setDefaultInstituteDao();
    m.setDefaultKitDao();
    m.setDefaultKitService();
    m.setDefaultLabDao();
    m.setDefaultLabService();
    m.setDefaultLibraryDao();
    m.setDefaultLibraryService();
    m.setDefaultLibraryDesignService();
    m.setDefaultLibraryDesignCodeService();
    m.setDefaultLibraryQcDao();
    m.setDefaultPlatformDao();
    m.setDefaultPlatformService();
    m.setDefaultPoolDao();
    m.setDefaultPoolOrderDao();
    m.setDefaultPoolOrderService();
    m.setDefaultPoolService();
    m.setDefaultReferenceGenomeDao();
    m.setDefaultReferenceGenomeService();
    m.setDefaultRunService();
    m.setDefaultProjectDao();
    m.setDefaultDetailedQcStatusDao();
    m.setDefaultRunDao();
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
    m.setDefaultSecurityStore();
    m.setDefaultSequencerPartitionContainerDao();
    m.setDefaultInstrumentDao();
    m.setDefaultInstrumentService();
    m.setDefaultSequencingParametersService();
    m.setDefaultStudyDao();
    m.setDefaultStudyService();
    m.setDefaultSubprojectDao();
    m.setDefaultTargetedSequencingDao();
    m.setDefaultTargetedSequencingService();
    m.setDefaultTissueMaterialDao();
    m.setDefaultTissueOriginDao();
    m.setDefaultTissueTypeDao();
    m.setDefaultLibraryDesignDao();
    m.setDefaultLibraryDesignCodeDao();
    m.setDefaultIndexDao();
    m.setDefaultSequencingParametersDao();
    m.setDefaultPoolableElementViewDao();
    m.setDefaultPoolableElementViewService();
    m.setDefaultBoxService();
    m.setDefaultQualityControlService();
    m.setDefaultQcTypeDao();
    m.setDefaultProjectService();

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
    Authentication auth = new SuperuserAuthentication(migrationUser);
    SecurityContext context = new SecurityContextImpl();
    context.setAuthentication(auth);
    SecurityContextHolder.setContext(context);
  }

  // TODO: Add naming scheme config instead of hard-coding
  public NamingScheme getNamingScheme() {
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
    if (runService != null) runService.setAuthorizationManager(authorizationManager);
    if (containerService != null) containerService.setAuthorizationManager(authorizationManager);
    if (instrumentService != null) instrumentService.setAuthorizationManager(authorizationManager);
    if (kitService != null) kitService.setAuthorizationManager(authorizationManager);
    if (changeLogService != null) changeLogService.setAuthorizationManager(authorizationManager);
    if (experimentService != null) experimentService.setAuthorizationManager(authorizationManager);
    if (boxService != null) boxService.setAuthorizationManager(authorizationManager);
  }

  public DefaultProjectService getProjectService() {
    return projectService;
  }

  public void setProjectService(DefaultProjectService projectService) {
    this.projectService = projectService;
  }

  public void setDefaultProjectService() {
    DefaultProjectService projectService = new DefaultProjectService();
    // Set stores for entities which need names generated before creation and can't be saved via services.
    projectService.setProjectStore(projectDao);
    projectService.setNamingScheme(getNamingScheme());
    projectService.setReferenceGenomeDao(referenceGenomeDao);
    setProjectService(projectService);
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
    if (projectDao != null) projectDao.setSecurityStore(securityStore);
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
    if (runService != null) runService.setSecurityManager(securityManager);
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
    setProjectDao(dao);
  }

  private void updateProjectDaoDependencies() {
    if (sampleNumberPerProjectService != null) sampleNumberPerProjectService.setProjectStore(projectDao);
    if (projectService != null) projectService.setProjectStore(projectDao);
    if (sampleService != null) sampleService.setProjectStore(projectDao);
    if (studyService != null) studyService.setProjectStore(projectDao);
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
    if (sampleService != null) sampleService.setSampleClassService(sampleClassService);
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
    svc.setSampleClassService(sampleClassService);
    svc.setSamplePurposeDao(samplePurposeDao);
    svc.setSampleValidRelationshipService(sampleValidRelationshipService);
    svc.setSubProjectDao(subprojectDao);
    svc.setTissueMaterialDao(tissueMaterialDao);
    svc.setTissueOriginDao(tissueOriginDao);
    svc.setTissueTypeDao(tissueTypeDao);
    svc.setSampleStore(sampleDao);
    svc.setLabService(labService);
    setSampleService(svc);
  }

  private void updateSampleServiceDependencies() {
    if (libraryService != null) libraryService.setSampleService(sampleService);
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
    dao.setBoxStore(boxDao);
    setSampleDao(dao);
  }

  private void updateSampleDaoDependencies() {
    if (sampleService != null) sampleService.setSampleStore(sampleDao);
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
    if (changeLogService != null) changeLogService.setChangeLogDao(changeLogDao);
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
    if (qcService != null) qcService.setSampleQcStore(sampleQcDao);
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
    if (libraryService != null) libraryService.setLibraryDao(libraryDao);
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
    svc.setLibraryDesignService(libraryDesignService);
    svc.setLibraryDesignCodeService(libraryDesignCodeService);
    svc.setIndexService(indexService);
    svc.setKitService(kitService);
    svc.setSampleService(sampleService);
    svc.setChangeLogService(changeLogService);
    setLibraryService(svc);
  }

  private void updateLibraryServiceDependencies() {
    if (dilutionService != null) dilutionService.setLibraryService(libraryService);
    if (experimentService != null) experimentService.setLibraryService(libraryService);
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
    if (qcService != null) qcService.setLibraryQcStore(libraryQcDao);
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
    dao.setBoxStore(boxDao);
    setDilutionDao(dao);
  }

  private void updateDilutionDaoDependencies() {
    if (dilutionService != null) dilutionService.setDilutionDao(dilutionDao);
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
    svc.setLibraryService(libraryService);
    svc.setTargetedSequencingService(targetedSequencingService);
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
    if (targetedSequencingService != null) targetedSequencingService.setTargetedSequencingDao(targetedSequencingDao);
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
    if (poolService != null) poolService.setPoolStore(poolDao);
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
    service.setNamingScheme(getNamingScheme());
    service.setPoolStore(poolDao);
    service.setPoolableElementViewService(poolableElementViewService);
    setPoolService(service);
  }

  private void updatePoolServiceDependencies() {
    if (containerService != null) containerService.setPoolService(poolService);
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
    if (experimentService != null) experimentService.setExperimentStore(experimentDao);
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
    if (kitService != null) kitService.setKitStore(kitDao);
  }

  public HibernateInstrumentModelDao getPlatformDao() {
    return platformDao;
  }

  public void setPlatformDao(HibernateInstrumentModelDao platformDao) {
    this.platformDao = platformDao;
    updatePlatformDaoDependencies();
  }

  public void setDefaultPlatformDao() {
    HibernateInstrumentModelDao dao = new HibernateInstrumentModelDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    setPlatformDao(dao);
  }

  private void updatePlatformDaoDependencies() {
    if (platformService != null) platformService.setPlatformDao(platformDao);
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
    if (studyService != null) studyService.setStudyStore(studyDao);
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
    dao.setSessionFactory(sessionFactory);
    setRunDao(dao);
  }

  private void updateRunDaoDependencies() {
    if (runService != null) runService.setRunDao(runDao);
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
    if (containerService != null) containerService.setContainerDao(sequencerPartitionContainerDao);
  }

  public HibernateInstrumentDao getInstrumentDao() {
    return instrumentDao;
  }

  public void setInstrumentDao(HibernateInstrumentDao instrumentDao) {
    this.instrumentDao = instrumentDao;
    updateInstrumentDaoDependencies();
  }

  public void setDefaultInstrumentDao() {
    HibernateInstrumentDao dao = new HibernateInstrumentDao();
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setSessionFactory(sessionFactory);
    setInstrumentDao(dao);
  }

  private void updateInstrumentDaoDependencies() {
    if (instrumentService != null) instrumentService.setInstrumentDao(instrumentDao);
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
    if (sampleDao != null) sampleDao.setBoxStore(boxDao);
    if (libraryDao != null) libraryDao.setBoxDao(boxDao);
    if (dilutionDao != null) dilutionDao.setBoxStore(boxDao);
    if (poolDao != null) poolDao.setBoxStore(boxDao);
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
    // none
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
    if (experimentService != null) experimentService.setStudyService(studyService);
  }

  public DefaultReferenceGenomeService getReferenceGenomeService() {
    return referenceGenomeService;
  }

  private void setReferenceGenomeService(DefaultReferenceGenomeService referenceGenomeService) {
    this.referenceGenomeService = referenceGenomeService;
    updateReferenceGenomeServiceDependencies();
  }

  public void setDefaultReferenceGenomeService() {
    DefaultReferenceGenomeService svc = new DefaultReferenceGenomeService();
    svc.setAuthorizationManager(authorizationManager);
    svc.setReferenceGenomeDao(referenceGenomeDao);
    setReferenceGenomeService(svc);
  }

  private void updateReferenceGenomeServiceDependencies() {

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
    if (projectService != null) projectService.setReferenceGenomeStore(referenceGenomeDao);
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
    setLibraryDesignDao(dao);
  }

  private void updateLibraryDesignDaoDependencies() {
    if (libraryDesignService != null) libraryDesignService.setLibraryDesignDao(libraryDesignDao);
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
    if (libraryDesignCodeService != null) libraryDesignCodeService.setLibraryDesignCodeDao(libraryDesignCodeDao);
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
    if (indexService != null) indexService.setIndexStore(indexDao);
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
    if (sequencingParametersService != null) sequencingParametersService.setSequencingParametersDao(sequencingParametersDao);
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

  public DefaultRunService getRunService() {
    return runService;
  }

  public void setRunService(DefaultRunService service) {
    this.runService = service;
    updateRunServiceDependencies();
  }

  public void setDefaultRunService() {
    DefaultRunService service = new DefaultRunService();
    service.setAuthorizationManager(authorizationManager);
    service.setRunDao(runDao);
    service.setSecurityManager(securityManager);
    service.setNamingScheme(getNamingScheme());
    service.setContainerService(containerService);
    service.setInstrumentService(instrumentService);
    service.setSequencingParametersService(sequencingParametersService);
    setRunService(service);
  }

  private void updateRunServiceDependencies() {
    if (instrumentService != null) instrumentService.setRunService(runService);
  }

  public DefaultContainerService getContainerService() {
    return containerService;
  }

  public void setContainerService(DefaultContainerService service) {
    this.containerService = service;
    updateContainerServiceDependencies();
  }

  public void setDefaultContainerService() {
    DefaultContainerService service = new DefaultContainerService();
    service.setAuthorizationManager(authorizationManager);
    service.setContainerDao(sequencerPartitionContainerDao);
    service.setPoolService(poolService);
    setContainerService(service);
  }

  private void updateContainerServiceDependencies() {
    if (runService != null) runService.setContainerService(containerService);
  }

  public DefaultInstrumentModelService getPlatformService() {
    return platformService;
  }

  public void setPlatformService(DefaultInstrumentModelService service) {
    this.platformService = service;
    updatePlatformServiceDependencies();
  }

  public void setDefaultPlatformService() {
    DefaultInstrumentModelService service = new DefaultInstrumentModelService();
    service.setPlatformDao(platformDao);
    setPlatformService(service);
  }

  private void updatePlatformServiceDependencies() {
    if (experimentService != null) experimentService.setPlatformService(platformService);
  }

  public DefaultInstrumentService getInstrumentService() {
    return instrumentService;
  }

  public void setInstrumentService(DefaultInstrumentService service) {
    this.instrumentService = service;
    updateInstrumentServiceDependencies();
  }

  public void setDefaultInstrumentService() {
    DefaultInstrumentService service = new DefaultInstrumentService();
    service.setAuthorizationManager(authorizationManager);
    service.setInstrumentDao(instrumentDao);
    service.setRunService(runService);
    setInstrumentService(service);
  }

  private void updateInstrumentServiceDependencies() {
    if (runService != null) runService.setInstrumentService(instrumentService);
  }

  public DefaultLibraryDesignService getLibraryDesignService() {
    return libraryDesignService;
  }

  public void setLibraryDesignService(DefaultLibraryDesignService service) {
    this.libraryDesignService = service;
    updateLibraryDesignServiceDependencies();
  }

  public void setDefaultLibraryDesignService() {
    DefaultLibraryDesignService service = new DefaultLibraryDesignService();
    service.setLibraryDesignDao(libraryDesignDao);
    setLibraryDesignService(service);
  }

  private void updateLibraryDesignServiceDependencies() {
    if (libraryService != null) libraryService.setLibraryDesignService(libraryDesignService);
  }

  public DefaultLibraryDesignCodeService getLibraryDesignCodeService() {
    return libraryDesignCodeService;
  }

  public void setLibraryDesignCodeService(DefaultLibraryDesignCodeService service) {
    this.libraryDesignCodeService = service;
    updateLibraryDesignCodeServiceDependencies();
  }

  public void setDefaultLibraryDesignCodeService() {
    DefaultLibraryDesignCodeService service = new DefaultLibraryDesignCodeService();
    service.setLibraryDesignCodeDao(libraryDesignCodeDao);
    setLibraryDesignCodeService(service);
  }

  private void updateLibraryDesignCodeServiceDependencies() {
    if (libraryService != null) libraryService.setLibraryDesignCodeService(libraryDesignCodeService);
  }

  public DefaultIndexService getIndexService() {
    return indexService;
  }

  public void setIndexService(DefaultIndexService service) {
    this.indexService = service;
    updateIndexServiceDependencies();
  }

  public void setDefaultIndexService() {
    DefaultIndexService service = new DefaultIndexService();
    service.setIndexStore(indexDao);
    setIndexService(service);
  }

  private void updateIndexServiceDependencies() {
    if (libraryService != null) libraryService.setIndexService(indexService);
  }

  public DefaultKitService getKitService() {
    return kitService;
  }

  public void setKitService(DefaultKitService service) {
    this.kitService = service;
    updateKitServiceDependencies();
  }

  public void setDefaultKitService() {
    DefaultKitService service = new DefaultKitService();
    service.setKitStore(kitDao);
    service.setAuthorizationManager(authorizationManager);
    setKitService(service);
  }

  private void updateKitServiceDependencies() {
    if (libraryService != null) libraryService.setKitService(kitService);
    if (experimentService != null) experimentService.setKitService(kitService);
  }

  public DefaultTargetedSequencingService getTargetedSequencingService() {
    return targetedSequencingService;
  }

  public void setTargetedSequencingService(DefaultTargetedSequencingService service) {
    this.targetedSequencingService = service;
    updateTargetedSequencingServiceDependencies();
  }

  public void setDefaultTargetedSequencingService() {
    DefaultTargetedSequencingService service = new DefaultTargetedSequencingService();
    service.setTargetedSequencingDao(targetedSequencingDao);
    setTargetedSequencingService(service);
  }

  private void updateTargetedSequencingServiceDependencies() {
    if (dilutionService != null) dilutionService.setTargetedSequencingService(targetedSequencingService);
  }

  public DefaultChangeLogService getChangeLogService() {
    return changeLogService;
  }

  public void setChangeLogService(DefaultChangeLogService service) {
    this.changeLogService = service;
    updateChangeLogServiceDependencies();
  }

  public void setDefaultChangeLogService() {
    DefaultChangeLogService service = new DefaultChangeLogService();
    service.setChangeLogDao(changeLogDao);
    service.setAuthorizationManager(authorizationManager);
    setChangeLogService(service);
  }

  private void updateChangeLogServiceDependencies() {
    if (runService != null) runService.setChangeLogService(changeLogService);
    if (poolService != null) poolService.setChangeLogService(changeLogService);
    if (libraryService != null) libraryService.setChangeLogService(changeLogService);
  }

  public DefaultSequencingParametersService getSequencingParametersService() {
    return sequencingParametersService;
  }

  public void setSequencingParametersService(DefaultSequencingParametersService service) {
    this.sequencingParametersService = service;
    updateSequencingParametersServiceDependencies();
  }

  public void setDefaultSequencingParametersService() {
    DefaultSequencingParametersService service = new DefaultSequencingParametersService();
    service.setSequencingParametersDao(sequencingParametersDao);
    service.setAuthorizationManager(authorizationManager);
    setSequencingParametersService(service);
  }

  private void updateSequencingParametersServiceDependencies() {
    if (runService != null) runService.setSequencingParametersService(sequencingParametersService);
    if (poolOrderService != null) poolOrderService.setSequencingParametersService(sequencingParametersService);
  }

  public DefaultPoolOrderService getPoolOrderService() {
    return poolOrderService;
  }

  public void setPoolOrderService(DefaultPoolOrderService service) {
    this.poolOrderService = service;
    updatePoolOrderServiceDependencies();
  }

  public void setDefaultPoolOrderService() {
    DefaultPoolOrderService service = new DefaultPoolOrderService();
    service.setPoolOrderDao(poolOrderDao);
    service.setSequencingParametersService(sequencingParametersService);
    service.setPoolService(poolService);
    service.setAuthorizationManager(authorizationManager);
    setPoolOrderService(service);
  }

  private void updatePoolOrderServiceDependencies() {
  }

  public HibernatePoolOrderDao getPoolOrderDao() {
    return poolOrderDao;
  }

  public void setPoolOrderDao(HibernatePoolOrderDao dao) {
    this.poolOrderDao = dao;
    updatePoolOrderDaoDependencies();
  }

  public void setDefaultPoolOrderDao() {
    HibernatePoolOrderDao dao = new HibernatePoolOrderDao();
    dao.setSessionFactory(sessionFactory);
    setPoolOrderDao(dao);
  }

  private void updatePoolOrderDaoDependencies() {
    if (poolOrderService != null) poolOrderService.setPoolOrderDao(poolOrderDao);
  }

  public DefaultExperimentService getExperimentService() {
    return experimentService;
  }

  public void setExperimentService(DefaultExperimentService svc) {
    this.experimentService = svc;
    updateExperimentServiceDependencies();
  }

  public void setDefaultExperimentService() {
    DefaultExperimentService service = new DefaultExperimentService();
    service.setAuthorizationManager(authorizationManager);
    service.setExperimentStore(experimentDao);
    service.setKitService(kitService);
    service.setNamingScheme(getNamingScheme());
    service.setPlatformService(platformService);
    service.setLibraryService(libraryService);
    service.setStudyService(studyService);
    setExperimentService(service);
  }

  private void updateExperimentServiceDependencies() {
  }

  public void setDefaultBoxService() {
    DefaultBoxService service = new DefaultBoxService();
    service.setBoxStore(boxDao);
    service.setAuthorizationManager(authorizationManager);
    service.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    service.setChangeLogService(changeLogService);
    service.setNamingScheme(namingScheme);
    setBoxService(service);
  }

  public void setBoxService(DefaultBoxService service) {
    this.boxService = service;
    updateBoxServiceDependencies();
  }

  private void updateBoxServiceDependencies() {

  }

  public BoxService getBoxService() {
    return boxService;
  }

  private void setDefaultQualityControlService() {
    DefaultQualityControlService service = new DefaultQualityControlService();
    service.setAuthorizationManager(authorizationManager);
    service.setLibraryQcStore(libraryQcDao);
    // Skip setPoolQcStore
    service.setSampleQcStore(sampleQcDao);
    service.setQcTypeStore(qcTypeDao);
    setQualityControlService(service);
  }

  public void setQualityControlService(DefaultQualityControlService qualityControlService) {
    this.qcService = qualityControlService;
    updateQualityServiceDependencies();
  }

  private void updateQualityServiceDependencies() {
    // nothing depends on QualityControlService
  }

  public QualityControlService getQualityControlService() {
    return qcService;
  }

  private void setDefaultQcTypeDao() {
    HibernateQcTypeDao dao = new HibernateQcTypeDao();
    dao.setSessionFactory(sessionFactory);
    setQcTypeDao(dao);
  }

  public void setQcTypeDao(HibernateQcTypeDao qcTypeDao) {
    this.qcTypeDao = qcTypeDao;
    updateQcTypeDaoDependencies();
  }

  private void updateQcTypeDaoDependencies() {
    if (qcService != null) qcService.setQcTypeStore(qcTypeDao);
  }

}
