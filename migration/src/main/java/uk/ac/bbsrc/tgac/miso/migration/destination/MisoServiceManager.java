package uk.ac.bbsrc.tgac.miso.migration.destination;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

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
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.service.naming.OicrNamingScheme;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateBoxDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateBoxSizeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateBoxUseDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateChangeLogDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDeletionDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateExperimentDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateIndexDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstituteDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstrumentDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateInstrumentModelDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateKitDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLabDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryAliquotDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryQcDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibrarySelectionDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryStrategyDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernatePoolDao;
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
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencingOrderDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSequencingParametersDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateStudyDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateStudyTypeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTargetedSequencingDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultBoxService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultBoxSizeService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultBoxUseService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultContainerService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultDetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultExperimentService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultIndexService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultInstrumentModelService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultInstrumentService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultKitDescriptorService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultKitService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLabService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryAliquotService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryDesignCodeService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryDesignService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibrarySelectionService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultLibraryStrategyService;
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
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSequencingOrderService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSequencingParametersService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultStudyService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultStudyTypeService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultSubprojectService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultTargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultUserService;

/**
 * This class is used to simplify creation and wiring of MISO services. Some of the config is currently hardcoded - mainly naming schemes
 * and authentication
 */
public class MisoServiceManager {

  private final boolean autoGenerateIdBarcodes = false; // TODO: config option

  private final Map<Class<?>, Object> services = new HashMap<>();
  private static final Map<Class<?>, Map<Class<?>, BiConsumer<?, ?>>> dependencies = new HashMap<>();

  static {
    addDependency(DefaultProjectService.class, HibernateProjectDao.class, DefaultProjectService::setProjectStore);
    addDependency(DefaultProjectService.class, NamingSchemeHolder.class, DefaultProjectService::setNamingSchemeHolder);
    addDependency(DefaultProjectService.class, HibernateReferenceGenomeDao.class, DefaultProjectService::setReferenceGenomeDao);
    addDependency(HibernateSecurityDao.class, SessionFactory.class, HibernateSecurityDao::setSessionFactory);
    addDependency(DefaultUserService.class, HibernateSecurityDao.class, DefaultUserService::setSecurityStore);
    addDependency(DefaultUserService.class, LocalSecurityManager.class, DefaultUserService::setSecurityManager);
    addDependency(HibernateProjectDao.class, SessionFactory.class, HibernateProjectDao::setSessionFactory);
    addDependency(HibernateProjectDao.class, HibernateSecurityDao.class, HibernateProjectDao::setSecurityStore);
    addDependency(HibernateSampleClassDao.class, SessionFactory.class, HibernateSampleClassDao::setSessionFactory);
    addDependency(DefaultSampleClassService.class, MigrationAuthorizationManager.class, DefaultSampleClassService::setAuthorizationManager);
    addDependency(DefaultSampleClassService.class, HibernateSampleClassDao.class, DefaultSampleClassService::setSampleClassDao);
    addDependency(DefaultSampleService.class, MigrationAuthorizationManager.class, DefaultSampleService::setAuthorizationManager);
    addDependency(DefaultSampleService.class, NamingSchemeHolder.class, DefaultSampleService::setNamingSchemeHolder);
    addDependency(DefaultSampleService.class, HibernateProjectDao.class, DefaultSampleService::setProjectStore);
    addDependency(DefaultSampleService.class, DefaultDetailedQcStatusService.class, DefaultSampleService::setDetailedQcStatusService);
    addDependency(DefaultSampleService.class, DefaultSampleClassService.class, DefaultSampleService::setSampleClassService);
    addDependency(DefaultSampleService.class, HibernateSamplePurposeDao.class, DefaultSampleService::setSamplePurposeDao);
    addDependency(DefaultSampleService.class, DefaultSampleValidRelationshipService.class,
        DefaultSampleService::setSampleValidRelationshipService);
    addDependency(DefaultSampleService.class, DefaultSubprojectService.class, DefaultSampleService::setSubprojectService);
    addDependency(DefaultSampleService.class, HibernateTissueMaterialDao.class, DefaultSampleService::setTissueMaterialDao);
    addDependency(DefaultSampleService.class, HibernateTissueOriginDao.class, DefaultSampleService::setTissueOriginDao);
    addDependency(DefaultSampleService.class, HibernateTissueTypeDao.class, DefaultSampleService::setTissueTypeDao);
    addDependency(DefaultSampleService.class, HibernateSampleDao.class, DefaultSampleService::setSampleStore);
    addDependency(DefaultSampleService.class, DefaultLabService.class, DefaultSampleService::setLabService);
    addDependency(DefaultSampleService.class, HibernateDeletionDao.class, DefaultSampleService::setDeletionStore);
    addDependency(HibernateSampleDao.class, SessionFactory.class, HibernateSampleDao::setSessionFactory);
    addDependency(HibernateSampleDao.class, HibernateBoxDao.class, HibernateSampleDao::setBoxStore);
    addDependency(HibernateChangeLogDao.class, SessionFactory.class, HibernateChangeLogDao::setSessionFactory);
    addDependency(HibernateSampleQcDao.class, SessionFactory.class, HibernateSampleQcDao::setSessionFactory);
    addDependency(HibernateLibraryDao.class, SessionFactory.class, HibernateLibraryDao::setSessionFactory);
    addDependency(HibernateLibraryDao.class, HibernateBoxDao.class, HibernateLibraryDao::setBoxDao);
    addDependency(DefaultLibraryService.class, MigrationAuthorizationManager.class, DefaultLibraryService::setAuthorizationManager);
    addDependency(DefaultLibraryService.class, NamingSchemeHolder.class, DefaultLibraryService::setNamingSchemeHolder);
    addDependency(DefaultLibraryService.class, HibernateLibraryDao.class, DefaultLibraryService::setLibraryDao);
    addDependency(DefaultLibraryService.class, DefaultLibraryDesignService.class, DefaultLibraryService::setLibraryDesignService);
    addDependency(DefaultLibraryService.class, DefaultLibraryDesignCodeService.class, DefaultLibraryService::setLibraryDesignCodeService);
    addDependency(DefaultLibraryService.class, DefaultIndexService.class, DefaultLibraryService::setIndexService);
    addDependency(DefaultLibraryService.class, DefaultKitDescriptorService.class, DefaultLibraryService::setKitDescriptorService);
    addDependency(DefaultLibraryService.class, DefaultSampleService.class, DefaultLibraryService::setSampleService);
    addDependency(DefaultLibraryService.class, DefaultChangeLogService.class, DefaultLibraryService::setChangeLogService);
    addDependency(HibernateLibraryQcDao.class, SessionFactory.class, HibernateLibraryQcDao::setSessionFactory);
    addDependency(HibernateLibraryAliquotDao.class, SessionFactory.class, HibernateLibraryAliquotDao::setSessionFactory);
    addDependency(HibernateLibraryAliquotDao.class, HibernateBoxDao.class, HibernateLibraryAliquotDao::setBoxStore);
    addDependency(DefaultLibraryAliquotService.class, HibernateLibraryAliquotDao.class, DefaultLibraryAliquotService::setLibraryAliquotDao);
    addDependency(DefaultLibraryAliquotService.class, MigrationAuthorizationManager.class,
        DefaultLibraryAliquotService::setAuthorizationManager);
    addDependency(DefaultLibraryAliquotService.class, NamingSchemeHolder.class, DefaultLibraryAliquotService::setNamingSchemeHolder);
    addDependency(DefaultLibraryAliquotService.class, DefaultLibraryService.class, DefaultLibraryAliquotService::setLibraryService);
    addDependency(DefaultLibraryAliquotService.class, DefaultTargetedSequencingService.class,
        DefaultLibraryAliquotService::setTargetedSequencingService);
    addDependency(HibernateTargetedSequencingDao.class, SessionFactory.class, HibernateTargetedSequencingDao::setSessionFactory);
    addDependency(HibernatePoolDao.class, HibernateBoxDao.class, HibernatePoolDao::setBoxStore);
    addDependency(HibernatePoolDao.class, HibernateSecurityDao.class, HibernatePoolDao::setSecurityStore);
    addDependency(HibernatePoolDao.class, SessionFactory.class, HibernatePoolDao::setSessionFactory);
    addDependency(DefaultPoolService.class, MigrationAuthorizationManager.class, DefaultPoolService::setAuthorizationManager);
    addDependency(DefaultPoolService.class, NamingSchemeHolder.class, DefaultPoolService::setNamingSchemeHolder);
    addDependency(DefaultPoolService.class, HibernatePoolDao.class, DefaultPoolService::setPoolStore);
    addDependency(DefaultPoolService.class, DefaultPoolableElementViewService.class, DefaultPoolService::setPoolableElementViewService);
    addDependency(HibernateExperimentDao.class, SessionFactory.class, HibernateExperimentDao::setSessionFactory);
    addDependency(HibernateKitDao.class, SessionFactory.class, HibernateKitDao::setSessionFactory);
    addDependency(HibernateInstrumentModelDao.class, SessionFactory.class, HibernateInstrumentModelDao::setSessionFactory);
    addDependency(HibernateInstrumentModelDao.class, JdbcTemplate.class, HibernateInstrumentModelDao::setJdbcTemplate);
    addDependency(HibernateStudyDao.class, SessionFactory.class, HibernateStudyDao::setSessionFactory);
    addDependency(HibernateRunDao.class, SessionFactory.class, HibernateRunDao::setSessionFactory);
    addDependency(HibernateSequencerPartitionContainerDao.class, SessionFactory.class,
        HibernateSequencerPartitionContainerDao::setSessionFactory);
    addDependency(HibernateInstrumentDao.class, SessionFactory.class, HibernateInstrumentDao::setSessionFactory);
    addDependency(HibernateBoxDao.class, SessionFactory.class, HibernateBoxDao::setSessionFactory);
    addDependency(DefaultLabService.class, MigrationAuthorizationManager.class, DefaultLabService::setAuthorizationManager);
    addDependency(DefaultLabService.class, HibernateInstituteDao.class, DefaultLabService::setInstituteDao);
    addDependency(DefaultLabService.class, HibernateLabDao.class, DefaultLabService::setLabDao);
    addDependency(HibernateInstituteDao.class, SessionFactory.class, HibernateInstituteDao::setSessionFactory);
    addDependency(HibernateDetailedQcStatusDao.class, SessionFactory.class, HibernateDetailedQcStatusDao::setSessionFactory);
    addDependency(HibernateSubprojectDao.class, SessionFactory.class, HibernateSubprojectDao::setSessionFactory);
    addDependency(HibernateTissueOriginDao.class, SessionFactory.class, HibernateTissueOriginDao::setSessionFactory);
    addDependency(HibernateTissueTypeDao.class, SessionFactory.class, HibernateTissueTypeDao::setSessionFactory);
    addDependency(HibernateSamplePurposeDao.class, SessionFactory.class, HibernateSamplePurposeDao::setSessionFactory);
    addDependency(HibernateTissueMaterialDao.class, SessionFactory.class, HibernateTissueMaterialDao::setSessionFactory);
    addDependency(DefaultSampleNumberPerProjectService.class, MigrationAuthorizationManager.class,
        DefaultSampleNumberPerProjectService::setAuthorizationManager);
    addDependency(DefaultSampleNumberPerProjectService.class, HibernateSampleNumberPerProjectDao.class,
        DefaultSampleNumberPerProjectService::setSampleNumberPerProjectDao);
    addDependency(DefaultSampleNumberPerProjectService.class, HibernateProjectDao.class,
        DefaultSampleNumberPerProjectService::setProjectStore);
    addDependency(HibernateSampleNumberPerProjectDao.class, SessionFactory.class, HibernateSampleNumberPerProjectDao::setSessionFactory);
    addDependency(DefaultSampleValidRelationshipService.class, MigrationAuthorizationManager.class,
        DefaultSampleValidRelationshipService::setAuthorizationManager);
    addDependency(DefaultSampleValidRelationshipService.class, HibernateSampleValidRelationshipDao.class,
        DefaultSampleValidRelationshipService::setSampleValidRelationshipDao);
    addDependency(DefaultStudyService.class, MigrationAuthorizationManager.class, DefaultStudyService::setAuthorizationManager);
    addDependency(DefaultStudyService.class, HibernateProjectDao.class, DefaultStudyService::setProjectStore);
    addDependency(DefaultStudyService.class, HibernateStudyDao.class, DefaultStudyService::setStudyStore);
    addDependency(DefaultStudyService.class, NamingSchemeHolder.class, DefaultStudyService::setNamingSchemeHolder);
    addDependency(DefaultReferenceGenomeService.class, MigrationAuthorizationManager.class,
        DefaultReferenceGenomeService::setAuthorizationManager);
    addDependency(DefaultReferenceGenomeService.class, HibernateReferenceGenomeDao.class,
        DefaultReferenceGenomeService::setReferenceGenomeDao);
    addDependency(HibernateReferenceGenomeDao.class, SessionFactory.class, HibernateReferenceGenomeDao::setSessionFactory);
    addDependency(HibernateSampleValidRelationshipDao.class, SessionFactory.class, HibernateSampleValidRelationshipDao::setSessionFactory);
    addDependency(HibernateLibraryDesignDao.class, SessionFactory.class, HibernateLibraryDesignDao::setSessionFactory);
    addDependency(HibernateLibraryDesignCodeDao.class, SessionFactory.class, HibernateLibraryDesignCodeDao::setSessionFactory);
    addDependency(HibernateIndexDao.class, SessionFactory.class, HibernateIndexDao::setSessionFactory);
    addDependency(HibernateSequencingParametersDao.class, SessionFactory.class, HibernateSequencingParametersDao::setSessionFactory);
    addDependency(HibernatePoolableElementViewDao.class, SessionFactory.class, HibernatePoolableElementViewDao::setSessionFactory);
    addDependency(HibernateTissueMaterialDao.class, SessionFactory.class, HibernateTissueMaterialDao::setSessionFactory);
    addDependency(HibernateTissueMaterialDao.class, SessionFactory.class, HibernateTissueMaterialDao::setSessionFactory);
    addDependency(DefaultPoolableElementViewService.class, HibernatePoolableElementViewDao.class,
        DefaultPoolableElementViewService::setPoolableElementViewDao);
    addDependency(DefaultRunService.class, MigrationAuthorizationManager.class, DefaultRunService::setAuthorizationManager);
    addDependency(DefaultRunService.class, HibernateRunDao.class, DefaultRunService::setRunDao);
    addDependency(DefaultRunService.class, DefaultUserService.class, DefaultRunService::setUserService);
    addDependency(DefaultRunService.class, NamingSchemeHolder.class, DefaultRunService::setNamingSchemeHolder);
    addDependency(DefaultRunService.class, DefaultContainerService.class, DefaultRunService::setContainerService);
    addDependency(DefaultRunService.class, DefaultInstrumentService.class, DefaultRunService::setInstrumentService);
    addDependency(DefaultRunService.class, DefaultSequencingParametersService.class, DefaultRunService::setSequencingParametersService);
    addDependency(DefaultContainerService.class, MigrationAuthorizationManager.class, DefaultContainerService::setAuthorizationManager);
    addDependency(DefaultContainerService.class, HibernateSequencerPartitionContainerDao.class, DefaultContainerService::setContainerDao);
    addDependency(DefaultContainerService.class, DefaultPoolService.class, DefaultContainerService::setPoolService);
    addDependency(DefaultInstrumentModelService.class, HibernateInstrumentModelDao.class, DefaultInstrumentModelService::setInstrumentModelStore);
    addDependency(DefaultInstrumentService.class, MigrationAuthorizationManager.class, DefaultInstrumentService::setAuthorizationManager);
    addDependency(DefaultInstrumentService.class, HibernateInstrumentDao.class, DefaultInstrumentService::setInstrumentDao);
    addDependency(DefaultLibraryDesignService.class, HibernateLibraryDesignDao.class, DefaultLibraryDesignService::setLibraryDesignDao);
    addDependency(DefaultLibraryDesignCodeService.class, HibernateLibraryDesignCodeDao.class,
        DefaultLibraryDesignCodeService::setLibraryDesignCodeDao);
    addDependency(DefaultIndexService.class, HibernateIndexDao.class, DefaultIndexService::setIndexStore);
    addDependency(DefaultKitDescriptorService.class, HibernateKitDao.class, DefaultKitDescriptorService::setKitStore);
    addDependency(DefaultKitDescriptorService.class, MigrationAuthorizationManager.class,
        DefaultKitDescriptorService::setAuthorizationManager);
    addDependency(DefaultTargetedSequencingService.class, HibernateTargetedSequencingDao.class,
        DefaultTargetedSequencingService::setTargetedSequencingDao);
    addDependency(DefaultChangeLogService.class, HibernateChangeLogDao.class, DefaultChangeLogService::setChangeLogDao);
    addDependency(DefaultChangeLogService.class, MigrationAuthorizationManager.class, DefaultChangeLogService::setAuthorizationManager);
    addDependency(DefaultSequencingParametersService.class, HibernateSequencingParametersDao.class,
        DefaultSequencingParametersService::setSequencingParametersDao);
    addDependency(DefaultSequencingParametersService.class, MigrationAuthorizationManager.class,
        DefaultSequencingParametersService::setAuthorizationManager);
    addDependency(DefaultSequencingOrderService.class, HibernateSequencingOrderDao.class, DefaultSequencingOrderService::setSequencingOrderDao);
    addDependency(DefaultSequencingOrderService.class, DefaultSequencingParametersService.class,
        DefaultSequencingOrderService::setSequencingParametersService);
    addDependency(DefaultSequencingOrderService.class, DefaultPoolService.class, DefaultSequencingOrderService::setPoolService);
    addDependency(DefaultSequencingOrderService.class, MigrationAuthorizationManager.class, DefaultSequencingOrderService::setAuthorizationManager);
    addDependency(HibernateSequencingOrderDao.class, SessionFactory.class, HibernateSequencingOrderDao::setSessionFactory);
    addDependency(DefaultExperimentService.class, MigrationAuthorizationManager.class, DefaultExperimentService::setAuthorizationManager);
    addDependency(DefaultExperimentService.class, HibernateExperimentDao.class, DefaultExperimentService::setExperimentStore);
    addDependency(DefaultExperimentService.class, DefaultKitService.class, DefaultExperimentService::setKitService);
    addDependency(DefaultExperimentService.class, NamingSchemeHolder.class, DefaultExperimentService::setNamingSchemeHolder);
    addDependency(DefaultExperimentService.class, DefaultInstrumentModelService.class, DefaultExperimentService::setInstrumentModelService);
    addDependency(DefaultExperimentService.class, DefaultLibraryService.class, DefaultExperimentService::setLibraryService);
    addDependency(DefaultExperimentService.class, DefaultStudyService.class, DefaultExperimentService::setStudyService);
    addDependency(DefaultBoxService.class, HibernateBoxDao.class, DefaultBoxService::setBoxStore);
    addDependency(DefaultBoxService.class, MigrationAuthorizationManager.class, DefaultBoxService::setAuthorizationManager);
    addDependency(DefaultBoxService.class, DefaultChangeLogService.class, DefaultBoxService::setChangeLogService);
    addDependency(DefaultBoxService.class, NamingSchemeHolder.class, DefaultBoxService::setNamingSchemeHolder);
    addDependency(DefaultBoxService.class, DefaultBoxUseService.class, DefaultBoxService::setBoxUseService);
    addDependency(DefaultBoxService.class, DefaultBoxSizeService.class, DefaultBoxService::setBoxSizeService);
    addDependency(DefaultQualityControlService.class, MigrationAuthorizationManager.class,
        DefaultQualityControlService::setAuthorizationManager);
    addDependency(DefaultQualityControlService.class, HibernateLibraryQcDao.class, DefaultQualityControlService::setLibraryQcStore);
    addDependency(DefaultQualityControlService.class, HibernateSampleQcDao.class, DefaultQualityControlService::setSampleQcStore);
    addDependency(DefaultQualityControlService.class, HibernateQcTypeDao.class, DefaultQualityControlService::setQcTypeStore);
    addDependency(HibernateQcTypeDao.class, SessionFactory.class, HibernateQcTypeDao::setSessionFactory);
    addDependency(DefaultBoxUseService.class, HibernateBoxUseDao.class, DefaultBoxUseService::setBoxUseDao);
    addDependency(DefaultBoxUseService.class, MigrationAuthorizationManager.class, DefaultBoxUseService::setAuthorizationManager);
    addDependency(DefaultBoxUseService.class, HibernateDeletionDao.class, DefaultBoxUseService::setDeletionStore);
    addDependency(HibernateBoxUseDao.class, SessionFactory.class, HibernateBoxUseDao::setSessionFactory);
    addDependency(DefaultBoxSizeService.class, HibernateBoxSizeDao.class, DefaultBoxSizeService::setBoxSizeDao);
    addDependency(DefaultBoxSizeService.class, MigrationAuthorizationManager.class, DefaultBoxSizeService::setAuthorizationManager);
    addDependency(DefaultBoxSizeService.class, HibernateDeletionDao.class, DefaultBoxSizeService::setDeletionStore);
    addDependency(HibernateBoxSizeDao.class, SessionFactory.class, HibernateBoxSizeDao::setSessionFactory);
    addDependency(HibernateStudyTypeDao.class, SessionFactory.class, HibernateStudyTypeDao::setSessionFactory);
    addDependency(DefaultStudyTypeService.class, HibernateStudyTypeDao.class, DefaultStudyTypeService::setStudyTypeDao);
    addDependency(DefaultStudyTypeService.class, MigrationAuthorizationManager.class, DefaultStudyTypeService::setAuthorizationManager);
    addDependency(DefaultStudyTypeService.class, HibernateDeletionDao.class, DefaultStudyTypeService::setDeletionStore);
    addDependency(HibernateLibrarySelectionDao.class, SessionFactory.class, HibernateLibrarySelectionDao::setSessionFactory);
    addDependency(DefaultLibrarySelectionService.class, HibernateLibrarySelectionDao.class,
        DefaultLibrarySelectionService::setLibrarySelectionDao);
    addDependency(DefaultLibrarySelectionService.class, HibernateDeletionDao.class, DefaultLibrarySelectionService::setDeletionStore);
    addDependency(DefaultLibrarySelectionService.class, MigrationAuthorizationManager.class,
        DefaultLibrarySelectionService::setAuthorizationManager);
    addDependency(HibernateLibraryStrategyDao.class, SessionFactory.class, HibernateLibraryStrategyDao::setSessionFactory);
    addDependency(DefaultLibraryStrategyService.class, HibernateLibraryStrategyDao.class,
        DefaultLibraryStrategyService::setLibraryStrategyDao);
    addDependency(DefaultLibraryStrategyService.class, HibernateDeletionDao.class, DefaultLibraryStrategyService::setDeletionStore);
    addDependency(DefaultLibraryStrategyService.class, MigrationAuthorizationManager.class,
        DefaultLibraryStrategyService::setAuthorizationManager);
  }

  private static <O, D> void addDependency(Class<O> owner, Class<D> dependency, BiConsumer<O, D> setter) {
    if (!dependencies.containsKey(owner)) {
      dependencies.put(owner, new HashMap<>());
    }
    Map<Class<?>, BiConsumer<?, ?>> deps = dependencies.get(owner);
    deps.put(dependency, setter);
  }

  /**
   * Constructs a new MisoServiceManager with no services initialized
   * 
   * @param jdbcTemplate for JDBC access to the database
   * @param sessionFactory for Hibernate access to the database
   */
  public MisoServiceManager(JdbcTemplate jdbcTemplate, SessionFactory sessionFactory) {
    setService(JdbcTemplate.class, jdbcTemplate);
    setService(SessionFactory.class, sessionFactory);

    // TODO: Add naming scheme config instead of hard-coding
    NamingSchemeHolder namingSchemeHolder = new NamingSchemeHolder();
    namingSchemeHolder.setPrimary(new OicrNamingScheme());
    setService(NamingSchemeHolder.class, namingSchemeHolder);
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
    m.setAllDefaults();

    User migrationUser = m.getUserByLoginNameInTransaction(m.getSecurityStore(), username);
    if (migrationUser == null) throw new IllegalArgumentException("User '" + username + "' not found");
    m.setUpSecurityContext(migrationUser);
    m.setAuthorizationManagerWithUser(migrationUser);
    return m;
  }

  public static MisoServiceManager buildWithDefaults(JdbcTemplate jdbcTemplate, SessionFactory sessionFactory, User migrationUser)
      throws IOException {
    MisoServiceManager m = new MisoServiceManager(jdbcTemplate, sessionFactory);
    m.setAllDefaults();

    m.setUpSecurityContext(migrationUser);
    m.setAuthorizationManagerWithUser(migrationUser);
    return m;
  }

  private void setAllDefaults() {
    setDefaultBoxDao();
    setDefaultChangeLogDao();
    setDefaultChangeLogService();
    setDefaultContainerService();
    setDefaultLibraryAliquotDao();
    setDefaultLibraryAliquotService();
    setDefaultExperimentDao();
    setDefaultExperimentService();
    setDefaultIndexService();
    setDefaultInstituteDao();
    setDefaultKitDao();
    setDefaultKitService();
    setDefaultKitDescriptorService();
    setDefaultLabDao();
    setDefaultLabService();
    setDefaultLibraryDao();
    setDefaultLibraryService();
    setDefaultLibraryDesignService();
    setDefaultLibraryDesignCodeService();
    setDefaultLibraryQcDao();
    setDefaultInstrumentModelDao();
    setDefaultInstrumentModelService();
    setDefaultPoolDao();
    setDefaultSequencingOrderDao();
    setDefaultSequencingOrderService();
    setDefaultPoolService();
    setDefaultReferenceGenomeDao();
    setDefaultReferenceGenomeService();
    setDefaultRunService();
    setDefaultProjectDao();
    setDefaultDetailedQcStatusDao();
    setDefaultRunDao();
    setDefaultSampleClassDao();
    setDefaultSampleClassService();
    setDefaultSampleDao();
    setDefaultSampleNumberPerProjectService();
    setDefaultSampleNumberPerProjectDao();
    setDefaultSamplePurposeDao();
    setDefaultSampleQcDao();
    setDefaultSampleService();
    setDefaultSampleValidRelationshipDao();
    setDefaultSampleValidRelationshipService();
    setDefaultSecurityManager();
    setDefaultSecurityStore();
    setDefaultSequencerPartitionContainerDao();
    setDefaultInstrumentDao();
    setDefaultInstrumentService();
    setDefaultSequencingParametersService();
    setDefaultStudyDao();
    setDefaultStudyService();
    setDefaultSubprojectDao();
    setDefaultTargetedSequencingDao();
    setDefaultTargetedSequencingService();
    setDefaultTissueMaterialDao();
    setDefaultTissueOriginDao();
    setDefaultTissueTypeDao();
    setDefaultLibraryDesignDao();
    setDefaultLibraryDesignCodeDao();
    setDefaultIndexDao();
    setDefaultSequencingParametersDao();
    setDefaultPoolableElementViewDao();
    setDefaultPoolableElementViewService();
    setDefaultBoxService();
    setDefaultQualityControlService();
    setDefaultQcTypeDao();
    setDefaultProjectService();
    setDefaultUserService();
    setDefaultDeletionStore();
    setDefaultBoxUseDao();
    setDefaultBoxUseService();
    setDefaultBoxSizeDao();
    setDefaultBoxSizeService();
    setDefaultStudyTypeDao();
    setDefaultStudyTypeService();
    setDefaultLibrarySelectionDao();
    setDefaultLibrarySelectionService();
    setDefaultLibraryStrategyDao();
    setDefaultLibraryStrategyService();
  }

  /**
   * Hibernate needs this to be wrapped in a transaction
   * 
   * @param securityStore
   * @return User
   * @throws IOException
   */
  public User getUserByLoginNameInTransaction(HibernateSecurityDao securityStore, String username) throws IOException {

    Transaction tx = getService(SessionFactory.class).getCurrentSession().beginTransaction();
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

  public NamingSchemeHolder getNamingSchemeHolder() {
    return getService(NamingSchemeHolder.class);
  }

  private <T> T getService(Class<T> clazz) {
    T service = clazz.cast(services.get(clazz));
    return service;
  }

  private <T> void setService(Class<T> clazz, T service) {
    setService(clazz, service, true);
  }

  private <T> void setService(Class<T> clazz, T service, boolean wire) {
    // wire into existing services
    for (Entry<Class<?>, Object> entry : services.entrySet()) {
      Map<Class<?>, BiConsumer<?, ?>> deps = dependencies.get(entry.getKey());
      if (deps != null && deps.containsKey(clazz)) {
        setDependency(entry.getValue(), service, deps.get(clazz));
      }
    }
    if (wire) {
      // wire in existing services
      Map<Class<?>, BiConsumer<?, ?>> deps = dependencies.get(clazz);
      if (deps != null) {
        for (Entry<Class<?>, BiConsumer<?, ?>> entry : deps.entrySet()) {
          Object existing = services.get(entry.getKey());
          if (existing != null) {
            setDependency(service, existing, entry.getValue());
          }
        }
      }
    }
    services.put(clazz, service);
  }

  private static <O, D> void setDependency(O owner, D dependency, BiConsumer<?, ?> setter) {
    @SuppressWarnings("unchecked")
    BiConsumer<O, D> castedSetter = (BiConsumer<O, D>) setter;
    castedSetter.accept(owner, dependency);
  }

  public MigrationAuthorizationManager getAuthorizationManager() {
    return getService(MigrationAuthorizationManager.class);
  }

  public void setAuthorizationManager(MigrationAuthorizationManager authorizationManager) {
    setService(MigrationAuthorizationManager.class, authorizationManager, false);
  }

  public void setAuthorizationManagerWithUser(User migrationUser) {
    setAuthorizationManager(new MigrationAuthorizationManager(migrationUser));
  }

  public DefaultProjectService getProjectService() {
    return getService(DefaultProjectService.class);
  }

  public void setProjectService(DefaultProjectService projectService) {
    setService(DefaultProjectService.class, projectService, false);
  }

  public void setDefaultProjectService() {
    setService(DefaultProjectService.class, new DefaultProjectService());
  }

  public HibernateSecurityDao getSecurityStore() {
    return getService(HibernateSecurityDao.class);
  }

  public void setSecurityStore(HibernateSecurityDao securityStore) {
    setService(HibernateSecurityDao.class, securityStore, false);
  }

  public void setDefaultSecurityStore() {
    setService(HibernateSecurityDao.class, new HibernateSecurityDao());
  }

  public LocalSecurityManager getSecurityManager() {
    return getService(LocalSecurityManager.class);
  }

  public void setSecurityManager(LocalSecurityManager securityManager) {
    setService(LocalSecurityManager.class, securityManager, false);
  }

  public void setDefaultSecurityManager() {
    setService(LocalSecurityManager.class, new LocalSecurityManager());
  }

  public DefaultUserService getUserService() {
    return getService(DefaultUserService.class);
  }

  public void setUserService(DefaultUserService userService) {
    setService(DefaultUserService.class, userService, false);
  }

  public void setDefaultUserService() {
    setService(DefaultUserService.class, new DefaultUserService());
  }

  public HibernateProjectDao getProjectDao() {
    return getService(HibernateProjectDao.class);
  }

  public void setProjectDao(HibernateProjectDao projectDao) {
    setService(HibernateProjectDao.class, projectDao, false);
  }

  public void setDefaultProjectDao() {
    setService(HibernateProjectDao.class, new HibernateProjectDao());
  }

  public HibernateSampleClassDao getSampleClassDao() {
    return getService(HibernateSampleClassDao.class);
  }

  public void setSampleClassDao(HibernateSampleClassDao sampleClassDao) {
    setService(HibernateSampleClassDao.class, sampleClassDao, false);
  }

  public void setDefaultSampleClassDao() {
    setService(HibernateSampleClassDao.class, new HibernateSampleClassDao());
  }

  public DefaultSampleClassService getSampleClassService() {
    return getService(DefaultSampleClassService.class);
  }

  public void setSampleClassService(DefaultSampleClassService sampleClassService) {
    setService(DefaultSampleClassService.class, sampleClassService, false);
  }

  public void setDefaultSampleClassService() {
    setService(DefaultSampleClassService.class, new DefaultSampleClassService());
  }

  public DefaultSampleService getSampleService() {
    return getService(DefaultSampleService.class);
  }

  public void setSampleService(DefaultSampleService sampleService) {
    setService(DefaultSampleService.class, sampleService, false);
  }

  public void setDefaultSampleService() {
    DefaultSampleService svc = new DefaultSampleService();
    svc.setAutoGenerateIdBarcodes(true);
    setService(DefaultSampleService.class, svc);
  }

  public HibernateSampleDao getSampleDao() {
    return getService(HibernateSampleDao.class);
  }

  public void setSampleDao(HibernateSampleDao sampleDao) {
    setService(HibernateSampleDao.class, sampleDao, false);
  }

  public void setDefaultSampleDao() {
    setService(HibernateSampleDao.class, new HibernateSampleDao());
  }

  public HibernateChangeLogDao getChangeLogDao() {
    return getService(HibernateChangeLogDao.class);
  }

  public void setChangeLogDao(HibernateChangeLogDao changeLogDao) {
    setService(HibernateChangeLogDao.class, changeLogDao, false);
  }

  public void setDefaultChangeLogDao() {
    setService(HibernateChangeLogDao.class, new HibernateChangeLogDao());
  }

  public HibernateSampleQcDao getSampleQcDao() {
    return getService(HibernateSampleQcDao.class);
  }

  public void setSampleQcDao(HibernateSampleQcDao sampleQcDao) {
    setService(HibernateSampleQcDao.class, sampleQcDao, false);
  }

  public void setDefaultSampleQcDao() {
    setService(HibernateSampleQcDao.class, new HibernateSampleQcDao());
  }

  public HibernateLibraryDao getLibraryDao() {
    return getService(HibernateLibraryDao.class);
  }

  public void setLibraryDao(HibernateLibraryDao libraryDao) {
    setService(HibernateLibraryDao.class, libraryDao, false);
  }

  public void setDefaultLibraryDao() {
    HibernateLibraryDao dao = new HibernateLibraryDao();
    dao.setDetailedSampleEnabled(true);
    setService(HibernateLibraryDao.class, dao);
  }

  public DefaultLibraryService getLibraryService() {
    return getService(DefaultLibraryService.class);
  }

  public void setLibraryService(DefaultLibraryService libraryService) {
    setService(DefaultLibraryService.class, libraryService, false);
  }

  public void setDefaultLibraryService() {
    DefaultLibraryService svc = new DefaultLibraryService();
    svc.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    setService(DefaultLibraryService.class, svc);
  }

  public HibernateLibraryQcDao getLibraryQcDao() {
    return getService(HibernateLibraryQcDao.class);
  }

  public void setLibraryQcDao(HibernateLibraryQcDao libraryQcDao) {
    setService(HibernateLibraryQcDao.class, libraryQcDao, false);
  }

  public void setDefaultLibraryQcDao() {
    setService(HibernateLibraryQcDao.class, new HibernateLibraryQcDao());
  }

  public HibernateLibraryAliquotDao getLibraryAliquotDao() {
    return getService(HibernateLibraryAliquotDao.class);
  }

  public void setLibraryAliquotDao(HibernateLibraryAliquotDao libraryAliquotDao) {
    setService(HibernateLibraryAliquotDao.class, libraryAliquotDao, false);
  }

  public void setDefaultLibraryAliquotDao() {
    setService(HibernateLibraryAliquotDao.class, new HibernateLibraryAliquotDao());
  }

  public DefaultLibraryAliquotService getLibraryAliquotService() {
    return getService(DefaultLibraryAliquotService.class);
  }

  public void setLibraryAliquotService(DefaultLibraryAliquotService libraryAliquotService) {
    setService(DefaultLibraryAliquotService.class, libraryAliquotService, false);
  }

  public void setDefaultLibraryAliquotService() {
    DefaultLibraryAliquotService svc = new DefaultLibraryAliquotService();
    svc.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    setService(DefaultLibraryAliquotService.class, svc);
  }

  public HibernateTargetedSequencingDao getTargetedSequencingDao() {
    return getService(HibernateTargetedSequencingDao.class);
  }

  public void setTargetedSequencingDao(HibernateTargetedSequencingDao targetedSequencingDao) {
    setService(HibernateTargetedSequencingDao.class, targetedSequencingDao, false);
  }

  public void setDefaultTargetedSequencingDao() {
    setService(HibernateTargetedSequencingDao.class, new HibernateTargetedSequencingDao());
  }

  public HibernatePoolDao getPoolDao() {
    return getService(HibernatePoolDao.class);
  }

  public void setPoolDao(HibernatePoolDao poolDao) {
    setService(HibernatePoolDao.class, poolDao, false);
  }

  public void setDefaultPoolDao() {
    setService(HibernatePoolDao.class, new HibernatePoolDao());
  }

  public DefaultPoolService getPoolService() {
    return getService(DefaultPoolService.class);
  }

  public void setPoolService(DefaultPoolService service) {
    setService(DefaultPoolService.class, service, false);
  }

  public void setDefaultPoolService() {
    DefaultPoolService service = new DefaultPoolService();
    service.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    setService(DefaultPoolService.class, service);
  }

  public HibernateExperimentDao getExperimentDao() {
    return getService(HibernateExperimentDao.class);
  }

  public void setExperimentDao(HibernateExperimentDao experimentDao) {
    setService(HibernateExperimentDao.class, experimentDao, false);
  }

  public void setDefaultExperimentDao() {
    setService(HibernateExperimentDao.class, new HibernateExperimentDao());
  }

  public HibernateKitDao getKitDao() {
    return getService(HibernateKitDao.class);
  }

  public void setKitDao(HibernateKitDao kitDao) {
    setService(HibernateKitDao.class, kitDao, false);
  }

  public void setDefaultKitDao() {
    setService(HibernateKitDao.class, new HibernateKitDao());
  }

  public HibernateInstrumentModelDao getPlatformDao() {
    return getService(HibernateInstrumentModelDao.class);
  }

  public void setInstrumentModelDao(HibernateInstrumentModelDao instrumentModelDao) {
    setService(HibernateInstrumentModelDao.class, instrumentModelDao, false);
  }

  public void setDefaultInstrumentModelDao() {
    setService(HibernateInstrumentModelDao.class, new HibernateInstrumentModelDao());
  }

  public HibernateStudyDao getStudyDao() {
    return getService(HibernateStudyDao.class);
  }

  public void setStudyDao(HibernateStudyDao studyDao) {
    setService(HibernateStudyDao.class, studyDao, false);
  }

  public void setDefaultStudyDao() {
    setService(HibernateStudyDao.class, new HibernateStudyDao());
  }

  public HibernateRunDao getRunDao() {
    return getService(HibernateRunDao.class);
  }

  public void setRunDao(HibernateRunDao runDao) {
    setService(HibernateRunDao.class, runDao, false);
  }

  public void setDefaultRunDao() {
    setService(HibernateRunDao.class, new HibernateRunDao());
  }

  public HibernateSequencerPartitionContainerDao getSequencerPartitionContainerDao() {
    return getService(HibernateSequencerPartitionContainerDao.class);
  }

  public void setSequencerPartitionContainerDao(HibernateSequencerPartitionContainerDao sequencerPartitionContainerDao) {
    setService(HibernateSequencerPartitionContainerDao.class, sequencerPartitionContainerDao, false);
  }

  public void setDefaultSequencerPartitionContainerDao() {
    setService(HibernateSequencerPartitionContainerDao.class, new HibernateSequencerPartitionContainerDao());
  }

  public HibernateInstrumentDao getInstrumentDao() {
    return getService(HibernateInstrumentDao.class);
  }

  public void setInstrumentDao(HibernateInstrumentDao instrumentDao) {
    setService(HibernateInstrumentDao.class, instrumentDao, false);
  }

  public void setDefaultInstrumentDao() {
    setService(HibernateInstrumentDao.class, new HibernateInstrumentDao());
  }

  public HibernateBoxDao getBoxDao() {
    return getService(HibernateBoxDao.class);
  }

  public void setBoxDao(HibernateBoxDao boxDao) {
    setService(HibernateBoxDao.class, boxDao, false);
  }

  public void setDefaultBoxDao() {
    setService(HibernateBoxDao.class, new HibernateBoxDao());
  }

  public DefaultLabService getLabService() {
    return getService(DefaultLabService.class);
  }

  public void setLabService(DefaultLabService labService) {
    setService(DefaultLabService.class, labService, false);
  }

  public void setDefaultLabService() {
    setService(DefaultLabService.class, new DefaultLabService());
  }

  public HibernateLabDao getLabDao() {
    return getService(HibernateLabDao.class);
  }

  public void setLabDao(HibernateLabDao labDao) {
    setService(HibernateLabDao.class, labDao, false);
  }

  public void setDefaultLabDao() {
    setService(HibernateLabDao.class, new HibernateLabDao());
  }

  public HibernateInstituteDao getInstituteDao() {
    return getService(HibernateInstituteDao.class);
  }

  public void setInstituteDao(HibernateInstituteDao instituteDao) {
    setService(HibernateInstituteDao.class, instituteDao, false);
  }

  public void setDefaultInstituteDao() {
    setService(HibernateInstituteDao.class, new HibernateInstituteDao());
  }

  public HibernateDetailedQcStatusDao getDetailedQcStatusDao() {
    return getService(HibernateDetailedQcStatusDao.class);
  }

  public void setDetailedQcStatusDao(HibernateDetailedQcStatusDao detailedQcStatus) {
    setService(HibernateDetailedQcStatusDao.class, detailedQcStatus, false);
  }

  public void setDefaultDetailedQcStatusDao() {
    setService(HibernateDetailedQcStatusDao.class, new HibernateDetailedQcStatusDao());
  }

  public HibernateSubprojectDao getSubprojectDao() {
    return getService(HibernateSubprojectDao.class);
  }

  public void setSubprojectDao(HibernateSubprojectDao subprojectDao) {
    setService(HibernateSubprojectDao.class, subprojectDao, false);
  }

  public void setDefaultSubprojectDao() {
    setService(HibernateSubprojectDao.class, new HibernateSubprojectDao());
  }

  public HibernateTissueOriginDao getTissueOriginDao() {
    return getService(HibernateTissueOriginDao.class);
  }

  public void setTissueOriginDao(HibernateTissueOriginDao tissueOriginDao) {
    setService(HibernateTissueOriginDao.class, tissueOriginDao, false);
  }

  public void setDefaultTissueOriginDao() {
    setService(HibernateTissueOriginDao.class, new HibernateTissueOriginDao());
  }

  public HibernateTissueTypeDao getTissueTypeDao() {
    return getService(HibernateTissueTypeDao.class);
  }

  public void setTissueTypeDao(HibernateTissueTypeDao tissueTypeDao) {
    setService(HibernateTissueTypeDao.class, tissueTypeDao, false);
  }

  public void setDefaultTissueTypeDao() {
    setService(HibernateTissueTypeDao.class, new HibernateTissueTypeDao());
  }

  public HibernateSamplePurposeDao getSamplePurposeDao() {
    return getService(HibernateSamplePurposeDao.class);
  }

  public void setSamplePurposeDao(HibernateSamplePurposeDao samplePurposeDao) {
    setService(HibernateSamplePurposeDao.class, samplePurposeDao, false);
  }

  public void setDefaultSamplePurposeDao() {
    setService(HibernateSamplePurposeDao.class, new HibernateSamplePurposeDao());
  }

  public HibernateTissueMaterialDao getTissueMaterialDao() {
    return getService(HibernateTissueMaterialDao.class);
  }

  public void setTissueMaterialDao(HibernateTissueMaterialDao tissueMaterialDao) {
    setService(HibernateTissueMaterialDao.class, tissueMaterialDao, false);
  }

  public void setDefaultTissueMaterialDao() {
    setService(HibernateTissueMaterialDao.class, new HibernateTissueMaterialDao());
  }

  public DefaultSampleNumberPerProjectService getSampleNumberPerProjectService() {
    return getService(DefaultSampleNumberPerProjectService.class);
  }

  public void setSampleNumberPerProjectService(DefaultSampleNumberPerProjectService sampleNumberPerProjectService) {
    setService(DefaultSampleNumberPerProjectService.class, sampleNumberPerProjectService, false);
  }

  public void setDefaultSampleNumberPerProjectService() {
    setService(DefaultSampleNumberPerProjectService.class, new DefaultSampleNumberPerProjectService());
  }

  public HibernateSampleNumberPerProjectDao getSampleNumberPerProjectDao() {
    return getService(HibernateSampleNumberPerProjectDao.class);
  }

  public void setSampleNumberPerProjectDao(HibernateSampleNumberPerProjectDao sampleNumberPerProjectDao) {
    setService(HibernateSampleNumberPerProjectDao.class, sampleNumberPerProjectDao, false);
  }

  public void setDefaultSampleNumberPerProjectDao() {
    setService(HibernateSampleNumberPerProjectDao.class, new HibernateSampleNumberPerProjectDao());
  }

  public DefaultSampleValidRelationshipService getSampleValidRelationshipService() {
    return getService(DefaultSampleValidRelationshipService.class);
  }

  public void setSampleValidRelationshipService(DefaultSampleValidRelationshipService sampleValidRelationshipService) {
    setService(DefaultSampleValidRelationshipService.class, sampleValidRelationshipService, false);
  }

  public void setDefaultSampleValidRelationshipService() {
    setService(DefaultSampleValidRelationshipService.class, new DefaultSampleValidRelationshipService());
  }

  public DefaultStudyService getStudyService() {
    return getService(DefaultStudyService.class);
  }

  public void setStudyService(DefaultStudyService studyService) {
    setService(DefaultStudyService.class, studyService, false);
  }

  public void setDefaultStudyService() {
    setService(DefaultStudyService.class, new DefaultStudyService());
  }

  public DefaultReferenceGenomeService getReferenceGenomeService() {
    return getService(DefaultReferenceGenomeService.class);
  }

  public void setReferenceGenomeService(DefaultReferenceGenomeService referenceGenomeService) {
    setService(DefaultReferenceGenomeService.class, referenceGenomeService, false);
  }

  public void setDefaultReferenceGenomeService() {
    setService(DefaultReferenceGenomeService.class, new DefaultReferenceGenomeService());
  }

  public HibernateReferenceGenomeDao getReferenceGenomeDao() {
    return getService(HibernateReferenceGenomeDao.class);
  }

  public void setReferenceGenomeDao(HibernateReferenceGenomeDao referenceGenomeDao) {
    setService(HibernateReferenceGenomeDao.class, referenceGenomeDao, false);
  }

  public void setDefaultReferenceGenomeDao() {
    setService(HibernateReferenceGenomeDao.class, new HibernateReferenceGenomeDao());
  }

  public HibernateSampleValidRelationshipDao getSampleValidRelationshipDao() {
    return getService(HibernateSampleValidRelationshipDao.class);
  }

  public void setSampleValidRelationshipDao(HibernateSampleValidRelationshipDao sampleValidRelationshipDao) {
    setService(HibernateSampleValidRelationshipDao.class, sampleValidRelationshipDao, false);
  }

  public void setDefaultSampleValidRelationshipDao() {
    setService(HibernateSampleValidRelationshipDao.class, new HibernateSampleValidRelationshipDao());
  }

  public HibernateLibraryDesignDao getLibraryDesignDao() {
    return getService(HibernateLibraryDesignDao.class);
  }

  public void setLibraryDesignDao(HibernateLibraryDesignDao libraryDesignDao) {
    setService(HibernateLibraryDesignDao.class, libraryDesignDao, false);
  }

  public void setDefaultLibraryDesignDao() {
    setService(HibernateLibraryDesignDao.class, new HibernateLibraryDesignDao());
  }

  public HibernateLibraryDesignCodeDao getLibraryDesignCodeDao() {
    return getService(HibernateLibraryDesignCodeDao.class);
  }

  public void setLibraryDesignCodeDao(HibernateLibraryDesignCodeDao libraryDesignCodeDao) {
    setService(HibernateLibraryDesignCodeDao.class, libraryDesignCodeDao, false);
  }

  public void setDefaultLibraryDesignCodeDao() {
    setService(HibernateLibraryDesignCodeDao.class, new HibernateLibraryDesignCodeDao());
  }

  public HibernateIndexDao getIndexDao() {
    return getService(HibernateIndexDao.class);
  }

  public void setIndexDao(HibernateIndexDao indexDao) {
    setService(HibernateIndexDao.class, indexDao, false);
  }

  public void setDefaultIndexDao() {
    setService(HibernateIndexDao.class, new HibernateIndexDao());
  }

  public HibernateSequencingParametersDao getSequencingParametersDao() {
    return getService(HibernateSequencingParametersDao.class);
  }

  public void setSequencingParametersDao(HibernateSequencingParametersDao sequencingParametersDao) {
    setService(HibernateSequencingParametersDao.class, sequencingParametersDao, false);
  }

  public void setDefaultSequencingParametersDao() {
    setService(HibernateSequencingParametersDao.class, new HibernateSequencingParametersDao());
  }

  public HibernatePoolableElementViewDao getPoolableElementViewDao() {
    return getService(HibernatePoolableElementViewDao.class);
  }

  public void setPoolableElementViewDao(HibernatePoolableElementViewDao dao) {
    setService(HibernatePoolableElementViewDao.class, dao, false);
  }

  public void setDefaultPoolableElementViewDao() {
    setService(HibernatePoolableElementViewDao.class, new HibernatePoolableElementViewDao());
  }

  public DefaultPoolableElementViewService getPoolableElementViewService() {
    return getService(DefaultPoolableElementViewService.class);
  }

  public void setPoolableElementViewService(DefaultPoolableElementViewService service) {
    setService(DefaultPoolableElementViewService.class, service, false);
  }

  public void setDefaultPoolableElementViewService() {
    setService(DefaultPoolableElementViewService.class, new DefaultPoolableElementViewService());
  }

  public DefaultRunService getRunService() {
    return getService(DefaultRunService.class);
  }

  public void setRunService(DefaultRunService service) {
    setService(DefaultRunService.class, service, false);
  }

  public void setDefaultRunService() {
    setService(DefaultRunService.class, new DefaultRunService());
  }

  public DefaultContainerService getContainerService() {
    return getService(DefaultContainerService.class);
  }

  public void setContainerService(DefaultContainerService service) {
    setService(DefaultContainerService.class, service, false);
  }

  public void setDefaultContainerService() {
    setService(DefaultContainerService.class, new DefaultContainerService());
  }

  public DefaultInstrumentModelService getInstrumentModelService() {
    return getService(DefaultInstrumentModelService.class);
  }

  public void setInstrumentModelService(DefaultInstrumentModelService service) {
    setService(DefaultInstrumentModelService.class, service, false);
  }

  public void setDefaultInstrumentModelService() {
    setService(DefaultInstrumentModelService.class, new DefaultInstrumentModelService());
  }

  public DefaultInstrumentService getInstrumentService() {
    return getService(DefaultInstrumentService.class);
  }

  public void setInstrumentService(DefaultInstrumentService service) {
    setService(DefaultInstrumentService.class, service, false);
  }

  public void setDefaultInstrumentService() {
    setService(DefaultInstrumentService.class, new DefaultInstrumentService());
  }

  public DefaultLibraryDesignService getLibraryDesignService() {
    return getService(DefaultLibraryDesignService.class);
  }

  public void setLibraryDesignService(DefaultLibraryDesignService service) {
    setService(DefaultLibraryDesignService.class, service, false);
  }

  public void setDefaultLibraryDesignService() {
    setService(DefaultLibraryDesignService.class, new DefaultLibraryDesignService());
  }

  public DefaultLibraryDesignCodeService getLibraryDesignCodeService() {
    return getService(DefaultLibraryDesignCodeService.class);
  }

  public void setLibraryDesignCodeService(DefaultLibraryDesignCodeService service) {
    setService(DefaultLibraryDesignCodeService.class, service, false);
  }

  public void setDefaultLibraryDesignCodeService() {
    setService(DefaultLibraryDesignCodeService.class, new DefaultLibraryDesignCodeService());
  }

  public DefaultIndexService getIndexService() {
    return getService(DefaultIndexService.class);
  }

  public void setIndexService(DefaultIndexService service) {
    setService(DefaultIndexService.class, service, false);
  }

  public void setDefaultIndexService() {
    setService(DefaultIndexService.class, new DefaultIndexService());
  }

  public DefaultKitDescriptorService getKitDescriptorService() {
    return getService(DefaultKitDescriptorService.class);
  }

  public void setKitDescriptorService(DefaultKitDescriptorService service) {
    setService(DefaultKitDescriptorService.class, service, false);
  }

  public void setDefaultKitDescriptorService() {
    setService(DefaultKitDescriptorService.class, new DefaultKitDescriptorService());
  }

  public DefaultKitService getKitService() {
    return getService(DefaultKitService.class);
  }

  public void setKitService(DefaultKitService service) {
    setService(DefaultKitService.class, service, false);
  }

  public void setDefaultKitService() {
    setService(DefaultKitService.class, new DefaultKitService());
  }

  public DefaultTargetedSequencingService getTargetedSequencingService() {
    return getService(DefaultTargetedSequencingService.class);
  }

  public void setTargetedSequencingService(DefaultTargetedSequencingService service) {
    setService(DefaultTargetedSequencingService.class, service, false);
  }

  public void setDefaultTargetedSequencingService() {
    setService(DefaultTargetedSequencingService.class, new DefaultTargetedSequencingService());
  }

  public DefaultChangeLogService getChangeLogService() {
    return getService(DefaultChangeLogService.class);
  }

  public void setChangeLogService(DefaultChangeLogService service) {
    setService(DefaultChangeLogService.class, service, false);
  }

  public void setDefaultChangeLogService() {
    setService(DefaultChangeLogService.class, new DefaultChangeLogService());
  }

  public DefaultSequencingParametersService getSequencingParametersService() {
    return getService(DefaultSequencingParametersService.class);
  }

  public void setSequencingParametersService(DefaultSequencingParametersService service) {
    setService(DefaultSequencingParametersService.class, service, false);
  }

  public void setDefaultSequencingParametersService() {
    setService(DefaultSequencingParametersService.class, new DefaultSequencingParametersService());
  }

  public DefaultSequencingOrderService getSequencingOrderService() {
    return getService(DefaultSequencingOrderService.class);
  }

  public void setSequencingOrderService(DefaultSequencingOrderService service) {
    setService(DefaultSequencingOrderService.class, service, false);
  }

  public void setDefaultSequencingOrderService() {
    setService(DefaultSequencingOrderService.class, new DefaultSequencingOrderService());
  }

  public HibernateSequencingOrderDao getSequencingOrderDao() {
    return getService(HibernateSequencingOrderDao.class);
  }

  public void setSequencingOrderDao(HibernateSequencingOrderDao dao) {
    setService(HibernateSequencingOrderDao.class, dao, false);
  }

  public void setDefaultSequencingOrderDao() {
    setService(HibernateSequencingOrderDao.class, new HibernateSequencingOrderDao());
  }

  public DefaultExperimentService getExperimentService() {
    return getService(DefaultExperimentService.class);
  }

  public void setExperimentService(DefaultExperimentService svc) {
    setService(DefaultExperimentService.class, svc, false);
  }

  public void setDefaultExperimentService() {
    setService(DefaultExperimentService.class, new DefaultExperimentService());
  }

  public DefaultBoxService getBoxService() {
    return getService(DefaultBoxService.class);
  }

  public void setBoxService(DefaultBoxService service) {
    setService(DefaultBoxService.class, service, false);
  }

  public void setDefaultBoxService() {
    DefaultBoxService service = new DefaultBoxService();
    service.setAutoGenerateIdBarcodes(autoGenerateIdBarcodes);
    setService(DefaultBoxService.class, service);
  }

  public DefaultQualityControlService getQualityControlService() {
    return getService(DefaultQualityControlService.class);
  }

  public void setQualityControlService(DefaultQualityControlService qualityControlService) {
    setService(DefaultQualityControlService.class, qualityControlService, false);
  }

  public void setDefaultQualityControlService() {
    setService(DefaultQualityControlService.class, new DefaultQualityControlService());
  }

  public HibernateQcTypeDao getQcTypeDao() {
    return getService(HibernateQcTypeDao.class);
  }

  public void setQcTypeDao(HibernateQcTypeDao qcTypeDao) {
    setService(HibernateQcTypeDao.class, qcTypeDao, false);
  }

  public void setDefaultQcTypeDao() {
    setService(HibernateQcTypeDao.class, new HibernateQcTypeDao());
  }

  public HibernateDeletionDao getDeletionStore() {
    return getService(HibernateDeletionDao.class);
  }

  public void setDeletionStore(HibernateDeletionDao dao) {
    setService(HibernateDeletionDao.class, dao, false);
  }

  public void setDefaultDeletionStore() {
    setService(HibernateDeletionDao.class, new HibernateDeletionDao());
  }

  public HibernateBoxUseDao getBoxUseDao() {
    return getService(HibernateBoxUseDao.class);
  }

  public void setBoxUseDao(HibernateBoxUseDao dao) {
    setService(HibernateBoxUseDao.class, dao, false);
  }

  public void setDefaultBoxUseDao() {
    setService(HibernateBoxUseDao.class, new HibernateBoxUseDao());
  }

  public DefaultBoxUseService getBoxUseService() {
    return getService(DefaultBoxUseService.class);
  }

  public void setBoxUseService(DefaultBoxUseService service) {
    setService(DefaultBoxUseService.class, service, false);
  }

  public void setDefaultBoxUseService() {
    setService(DefaultBoxUseService.class, new DefaultBoxUseService());
  }

  public HibernateBoxSizeDao getBoxSizeDao() {
    return getService(HibernateBoxSizeDao.class);
  }

  public void setBoxSizeDao(HibernateBoxSizeDao dao) {
    setService(HibernateBoxSizeDao.class, dao, false);
  }

  public void setDefaultBoxSizeDao() {
    setService(HibernateBoxSizeDao.class, new HibernateBoxSizeDao());
  }

  public DefaultBoxSizeService getBoxSizeService() {
    return getService(DefaultBoxSizeService.class);
  }

  public void setBoxSizeService(DefaultBoxSizeService service) {
    setService(DefaultBoxSizeService.class, service, false);
  }

  public void setDefaultBoxSizeService() {
    setService(DefaultBoxSizeService.class, new DefaultBoxSizeService());
  }

  public HibernateStudyTypeDao getStudyTypeDao() {
    return getService(HibernateStudyTypeDao.class);
  }

  public void setStudyTypeDao(HibernateStudyTypeDao dao) {
    setService(HibernateStudyTypeDao.class, dao, false);
  }

  public void setDefaultStudyTypeDao() {
    setService(HibernateStudyTypeDao.class, new HibernateStudyTypeDao());
  }

  public DefaultStudyTypeService getStudyTypeService() {
    return getService(DefaultStudyTypeService.class);
  }

  public void setStudyTypeService(DefaultStudyTypeService service) {
    setService(DefaultStudyTypeService.class, service, false);
  }

  public void setDefaultStudyTypeService() {
    setService(DefaultStudyTypeService.class, new DefaultStudyTypeService());
  }

  public HibernateLibrarySelectionDao getLibrarySelectionDao() {
    return getService(HibernateLibrarySelectionDao.class);
  }

  public void setLibrarySelectionDao(HibernateLibrarySelectionDao dao) {
    setService(HibernateLibrarySelectionDao.class, dao, false);
  }

  public void setDefaultLibrarySelectionDao() {
    setService(HibernateLibrarySelectionDao.class, new HibernateLibrarySelectionDao());
  }

  public DefaultLibrarySelectionService getLibrarySelectionService() {
    return getService(DefaultLibrarySelectionService.class);
  }

  public void setLibrarySelectionService(DefaultLibrarySelectionService service) {
    setService(DefaultLibrarySelectionService.class, service, false);
  }

  public void setDefaultLibrarySelectionService() {
    setService(DefaultLibrarySelectionService.class, new DefaultLibrarySelectionService());
  }

  public HibernateLibraryStrategyDao getLibraryStrategyDao() {
    return getService(HibernateLibraryStrategyDao.class);
  }

  public void setLibraryStrategyDao(HibernateLibraryStrategyDao dao) {
    setService(HibernateLibraryStrategyDao.class, dao, false);
  }

  public void setDefaultLibraryStrategyDao() {
    setService(HibernateLibraryStrategyDao.class, new HibernateLibraryStrategyDao());
  }

  public DefaultLibraryStrategyService getLibraryStrategyService() {
    return getService(DefaultLibraryStrategyService.class);
  }

  public void setLibraryStrategyService(DefaultLibraryStrategyService service) {
    setService(DefaultLibraryStrategyService.class, service, false);
  }

  public void setDefaultLibraryStrategyService() {
    setService(DefaultLibraryStrategyService.class, new DefaultLibraryStrategyService());
  }

}
