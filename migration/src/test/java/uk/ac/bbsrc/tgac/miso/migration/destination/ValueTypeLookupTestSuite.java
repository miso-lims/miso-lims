package uk.ac.bbsrc.tgac.miso.migration.destination;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.IndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SamplePurpose;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Subproject;
import uk.ac.bbsrc.tgac.miso.core.data.TissueMaterial;
import uk.ac.bbsrc.tgac.miso.core.data.TissueOrigin;
import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstituteImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SamplePurposeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubprojectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueMaterialImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueOriginImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TissueTypeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.persistence.HibernateSampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateDetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateIndexDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLabDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignCodeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateLibraryDesignDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateSubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateTissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.impl.DefaultReferenceGenomeService;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLKitDescriptorDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLLibraryQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSampleQCDAO;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLSequencerReferenceDAO;

public class ValueTypeLookupTestSuite {

  private static final Long VALID_LONG = 1L;
  private static final String VALID_STRING = "real thing";
  private static final Long INVALID_LONG = 2L;
  private static final String INVALID_STRING = "doesn't exist";

  private static final Long UNSPECIFIED_LAB_ID = 3L;
  private static final Long UNSPECIFIED_LAB_INST_ID = 4L;
  private static final String UNSPECIFIED_LAB_INST_ALIAS = "unspecified";

  private ValueTypeLookup sut;

  @Before
  public void setup() throws IOException {
    MisoServiceManager mgr = Mockito.mock(MisoServiceManager.class);

    HibernateSampleClassDao scDao = Mockito.mock(HibernateSampleClassDao.class);
    List<SampleClass> scs = new ArrayList<>();
    scs.add(makeSampleClass(VALID_LONG, VALID_STRING));
    Mockito.when(scDao.getSampleClass()).thenReturn(scs);
    Mockito.when(mgr.getSampleClassDao()).thenReturn(scDao);

    HibernateTissueTypeDao ttDao = Mockito.mock(HibernateTissueTypeDao.class);
    List<TissueType> tts = new ArrayList<>();
    tts.add(makeTissueType(VALID_LONG, VALID_STRING));
    Mockito.when(ttDao.getTissueType()).thenReturn(tts);
    Mockito.when(mgr.getTissueTypeDao()).thenReturn(ttDao);

    HibernateTissueMaterialDao tmDao = Mockito.mock(HibernateTissueMaterialDao.class);
    List<TissueMaterial> tms = new ArrayList<>();
    tms.add(makeTissueMaterial(VALID_LONG, VALID_STRING));
    Mockito.when(tmDao.getTissueMaterial()).thenReturn(tms);
    Mockito.when(mgr.getTissueMaterialDao()).thenReturn(tmDao);

    SQLKitDescriptorDAO kitDao = Mockito.mock(SQLKitDescriptorDAO.class);
    List<KitDescriptor> kits = new ArrayList<>();
    kits.add(makeKit(VALID_LONG, VALID_STRING));
    Mockito.when(kitDao.listAll()).thenReturn(kits);
    Mockito.when(mgr.getKitDescriptorDao()).thenReturn(kitDao);

    HibernateSamplePurposeDao spDao = Mockito.mock(HibernateSamplePurposeDao.class);
    List<SamplePurpose> sps = new ArrayList<>();
    sps.add(makeSamplePurpose(VALID_LONG, VALID_STRING));
    Mockito.when(spDao.getSamplePurpose()).thenReturn(sps);
    Mockito.when(mgr.getSamplePurposeDao()).thenReturn(spDao);

    HibernateLabDao labDao = Mockito.mock(HibernateLabDao.class);
    List<Lab> labs = new ArrayList<>();
    labs.add(makeLab(VALID_LONG, VALID_STRING, VALID_LONG, VALID_STRING));
    labs.add(makeLab(UNSPECIFIED_LAB_ID, "Not Specified", UNSPECIFIED_LAB_INST_ID, UNSPECIFIED_LAB_INST_ALIAS));
    Mockito.when(labDao.getLabs()).thenReturn(labs);
    Mockito.when(mgr.getLabDao()).thenReturn(labDao);

    HibernateTissueOriginDao toDao = Mockito.mock(HibernateTissueOriginDao.class);
    List<TissueOrigin> tos = new ArrayList<>();
    tos.add(makeTissueOrigin(VALID_LONG, VALID_STRING, VALID_STRING));
    Mockito.when(toDao.getTissueOrigin()).thenReturn(tos);
    Mockito.when(mgr.getTissueOriginDao()).thenReturn(toDao);

    SQLLibraryDAO libDao = Mockito.mock(SQLLibraryDAO.class);
    List<LibrarySelectionType> lsts = new ArrayList<>();
    lsts.add(makeLibrarySelection(VALID_LONG, VALID_STRING));
    Mockito.when(libDao.listAllLibrarySelectionTypes()).thenReturn(lsts);
    Mockito.when(mgr.getLibraryDao()).thenReturn(libDao);

    List<LibraryStrategyType> lstrats = new ArrayList<>();
    lstrats.add(makeLibraryStrategy(VALID_LONG, VALID_STRING));
    Mockito.when(libDao.listAllLibraryStrategyTypes()).thenReturn(lstrats);
    Mockito.when(mgr.getLibraryDao()).thenReturn(libDao);

    List<LibraryType> lts = new ArrayList<>();
    lts.add(makeLibraryType(VALID_LONG, VALID_STRING, VALID_STRING));
    Mockito.when(libDao.listAllLibraryTypes()).thenReturn(lts);

    HibernateLibraryDesignDao ldDao = Mockito.mock(HibernateLibraryDesignDao.class);
    List<LibraryDesign> lds = new ArrayList<>();
    lds.add(makeLibraryDesign(VALID_LONG, VALID_STRING));
    Mockito.when(ldDao.getLibraryDesigns()).thenReturn(lds);
    Mockito.when(mgr.getLibraryDesignDao()).thenReturn(ldDao);

    HibernateLibraryDesignCodeDao ldcDao = Mockito.mock(HibernateLibraryDesignCodeDao.class);
    List<LibraryDesignCode> ldcs = new ArrayList<>();
    ldcs.add(makeLibraryDesignCode(VALID_LONG, VALID_STRING));
    Mockito.when(ldcDao.getLibraryDesignCodes()).thenReturn(ldcs);
    Mockito.when(mgr.getLibraryDesignCodeDao()).thenReturn(ldcDao);

    HibernateIndexDao iDao = Mockito.mock(HibernateIndexDao.class);
    List<Index> inds = new ArrayList<>();
    inds.add(makeIndex(VALID_LONG, VALID_STRING, VALID_STRING));
    Mockito.when(iDao.listAllIndices()).thenReturn(inds);
    Mockito.when(mgr.getIndexDao()).thenReturn(iDao);

    SQLSampleQCDAO sqcDao = Mockito.mock(SQLSampleQCDAO.class);
    List<QcType> sqcs = new ArrayList<>();
    sqcs.add(makeQcType(VALID_LONG, VALID_STRING));
    Mockito.when(sqcDao.listAllSampleQcTypes()).thenReturn(sqcs);
    Mockito.when(mgr.getSampleQcDao()).thenReturn(sqcDao);

    SQLLibraryQCDAO lqcDao = Mockito.mock(SQLLibraryQCDAO.class);
    List<QcType> lqcs = new ArrayList<>();
    lqcs.add(makeQcType(VALID_LONG, VALID_STRING));
    Mockito.when(lqcDao.listAllLibraryQcTypes()).thenReturn(lqcs);
    Mockito.when(mgr.getLibraryQcDao()).thenReturn(lqcDao);

    SQLSequencerReferenceDAO seqRefDao = Mockito.mock(SQLSequencerReferenceDAO.class);
    List<SequencerReference> seqRefs = new ArrayList<>();
    seqRefs.add(makeSequencer(VALID_LONG, VALID_STRING));
    Mockito.when(seqRefDao.listAll()).thenReturn(seqRefs);
    Mockito.when(mgr.getSequencerReferenceDao()).thenReturn(seqRefDao);
    
    HibernateDetailedQcStatusDao detQcStatusDao = Mockito.mock(HibernateDetailedQcStatusDao.class);
    List<DetailedQcStatus> qcStatuses = new ArrayList<>();
    qcStatuses.add(makeDetailedQcStatus(VALID_LONG, VALID_STRING));
    Mockito.when(detQcStatusDao.getDetailedQcStatus()).thenReturn(qcStatuses);
    Mockito.when(mgr.getDetailedQcStatusDao()).thenReturn(detQcStatusDao);
    
    HibernateSubprojectDao subProjDao = Mockito.mock(HibernateSubprojectDao.class);
    List<Subproject> subprojs = new ArrayList<>();
    subprojs.add(makeSubproject(VALID_LONG, VALID_STRING));
    Mockito.when(subProjDao.getSubproject()).thenReturn(subprojs);
    Mockito.when(mgr.getSubprojectDao()).thenReturn(subProjDao);
    
    DefaultReferenceGenomeService referenceGenomeService = Mockito.mock(DefaultReferenceGenomeService.class);
    List<ReferenceGenome> referenceGenomes = Lists.newArrayList();
    referenceGenomes.add(makeReferenceGenome(VALID_LONG, VALID_STRING));
    Mockito.when(referenceGenomeService.listAllReferenceGenomeTypes()).thenReturn(referenceGenomes);
    Mockito.when(mgr.getReferenceGenomeService()).thenReturn(referenceGenomeService);

    sut = new ValueTypeLookup(mgr);
  }

  @Test
  public void testResolveSampleClass() {
    assertNotNull(sut.resolve(makeSampleClass(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeSampleClass(null, VALID_STRING)));
    assertNull(sut.resolve((SampleClass) null));
    assertNull(sut.resolve(makeSampleClass(null, null)));
    assertNull(sut.resolve(makeSampleClass(INVALID_LONG, null)));
    assertNull(sut.resolve(makeSampleClass(null, INVALID_STRING)));
  }

  private SampleClass makeSampleClass(Long id, String alias) {
    SampleClass sc = new SampleClassImpl();
    sc.setId(id);
    sc.setAlias(alias);
    return sc;
  }

  @Test
  public void testResolveTissueType() {
    assertNotNull(sut.resolve(makeTissueType(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeTissueType(null, VALID_STRING)));
    assertNull(sut.resolve((TissueType) null));
    assertNull(sut.resolve(makeTissueType(null, null)));
    assertNull(sut.resolve(makeTissueType(INVALID_LONG, null)));
    assertNull(sut.resolve(makeTissueType(null, INVALID_STRING)));
  }

  private TissueType makeTissueType(Long id, String alias) {
    TissueType tt = new TissueTypeImpl();
    tt.setId(id);
    tt.setAlias(alias);
    return tt;
  }

  @Test
  public void testResolveTissueMaterial() {
    assertNotNull(sut.resolve(makeTissueMaterial(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeTissueMaterial(null, VALID_STRING)));
    assertNull(sut.resolve((TissueMaterial) null));
    assertNull(sut.resolve(makeTissueMaterial(null, null)));
    assertNull(sut.resolve(makeTissueMaterial(INVALID_LONG, null)));
    assertNull(sut.resolve(makeTissueMaterial(null, INVALID_STRING)));
  }

  private TissueMaterial makeTissueMaterial(Long id, String alias) {
    TissueMaterial tm = new TissueMaterialImpl();
    tm.setId(id);
    tm.setAlias(alias);
    return tm;
  }

  @Test
  public void testResolveKit() {
    assertNotNull(sut.resolve(makeKit(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeKit(null, VALID_STRING)));
    assertNull(sut.resolve((KitDescriptor) null));
    assertNull(sut.resolve(makeKit(null, null)));
    assertNull(sut.resolve(makeKit(INVALID_LONG, null)));
    assertNull(sut.resolve(makeKit(null, INVALID_STRING)));
  }

  private KitDescriptor makeKit(Long id, String name) {
    KitDescriptor kit = new KitDescriptor();
    kit.setId(id == null ? KitDescriptor.UNSAVED_ID : id);
    kit.setName(name);
    return kit;
  }

  @Test
  public void testResolveSamplePurpose() {
    assertNotNull(sut.resolve(makeSamplePurpose(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeSamplePurpose(null, VALID_STRING)));
    assertNull(sut.resolve((SamplePurpose) null));
    assertNull(sut.resolve(makeSamplePurpose(null, null)));
    assertNull(sut.resolve(makeSamplePurpose(INVALID_LONG, null)));
    assertNull(sut.resolve(makeSamplePurpose(null, INVALID_STRING)));
  }

  private SamplePurpose makeSamplePurpose(Long id, String alias) {
    SamplePurpose sp = new SamplePurposeImpl();
    sp.setId(id);
    sp.setAlias(alias);
    return sp;
  }

  @Test
  public void testResolveLab() {
    assertNotNull(sut.resolve(makeLab(VALID_LONG, null, null, null)));
    assertNotNull(sut.resolve(makeLab(null, VALID_STRING, VALID_LONG, null)));
    assertNotNull(sut.resolve(makeLab(null, VALID_STRING, null, VALID_STRING)));
    // assumed "Not Specified" lab alias
    assertNotNull(sut.resolve(makeLab(null, null, UNSPECIFIED_LAB_INST_ID, null)));
    assertNotNull(sut.resolve(makeLab(null, null, null, UNSPECIFIED_LAB_INST_ALIAS)));
    assertNull(sut.resolve((Lab) null));
    assertNull(sut.resolve(makeLab(null, null, null, null)));
    assertNull(sut.resolve(makeLab(INVALID_LONG, null, null, null)));
    assertNull(sut.resolve(makeLab(null, VALID_STRING, null)));
    assertNull(sut.resolve(makeLab(null, INVALID_STRING, VALID_LONG, null)));
    assertNull(sut.resolve(makeLab(null, VALID_STRING, INVALID_LONG, null)));
    assertNull(sut.resolve(makeLab(null, INVALID_STRING, null, VALID_STRING)));
    assertNull(sut.resolve(makeLab(null, VALID_STRING, null, INVALID_STRING)));
  }

  private Lab makeLab(Long labId, String labAlias, Long instId, String instAlias) {
    return makeLab(labId, labAlias, makeInstitute(instId, instAlias));
  }

  private Lab makeLab(Long labId, String labAlias, Institute institute) {
    Lab lab = new LabImpl();
    lab.setId(labId);
    lab.setAlias(labAlias);
    lab.setInstitute(institute);
    return lab;
  }

  private Institute makeInstitute(Long id, String alias) {
    Institute inst = new InstituteImpl();
    inst.setId(id);
    inst.setAlias(alias);
    return inst;
  }

  @Test
  public void testResolveTissueOrigin() {
    assertNotNull(sut.resolve(makeTissueOrigin(VALID_LONG, null, null)));
    assertNotNull(sut.resolve(makeTissueOrigin(null, VALID_STRING, null)));
    assertNotNull(sut.resolve(makeTissueOrigin(null, null, VALID_STRING)));
    assertNull(sut.resolve((TissueOrigin) null));
    assertNull(sut.resolve(makeTissueOrigin(null, null, null)));
    assertNull(sut.resolve(makeTissueOrigin(INVALID_LONG, null, null)));
    assertNull(sut.resolve(makeTissueOrigin(null, INVALID_STRING, null)));
    assertNull(sut.resolve(makeTissueOrigin(null, null, INVALID_STRING)));
  }

  private TissueOrigin makeTissueOrigin(Long id, String alias, String desc) {
    TissueOrigin to = new TissueOriginImpl();
    to.setId(id);
    to.setAlias(alias);
    to.setDescription(desc);
    return to;
  }

  @Test
  public void testResolveLibrarySelection() {
    assertNotNull(sut.resolve(makeLibrarySelection(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeLibrarySelection(null, VALID_STRING)));
    assertNull(sut.resolve((LibrarySelectionType) null));
    assertNull(sut.resolve(makeLibrarySelection(null, null)));
    assertNull(sut.resolve(makeLibrarySelection(INVALID_LONG, null)));
    assertNull(sut.resolve(makeLibrarySelection(null, INVALID_STRING)));
  }

  @Test
  public void testResolveLibraryStrategy() {
    assertNotNull(sut.resolve(makeLibraryStrategy(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeLibraryStrategy(null, VALID_STRING)));
    assertNull(sut.resolve((LibraryStrategyType) null));
    assertNull(sut.resolve(makeLibraryStrategy(null, null)));
    assertNull(sut.resolve(makeLibraryStrategy(INVALID_LONG, null)));
    assertNull(sut.resolve(makeLibraryStrategy(null, INVALID_STRING)));
  }

  private LibrarySelectionType makeLibrarySelection(Long id, String name) {
    LibrarySelectionType ls = new LibrarySelectionType();
    ls.setId(id == null ? LibrarySelectionType.UNSAVED_ID : id);
    ls.setName(name);
    return ls;
  }

  private LibraryStrategyType makeLibraryStrategy(Long id, String name) {
    LibraryStrategyType ls = new LibraryStrategyType();
    ls.setId(id == null ? LibraryStrategyType.UNSAVED_ID : id);
    ls.setName(name);
    return ls;
  }

  @Test
  public void testResolveLibraryType() {
    assertNotNull(sut.resolve(makeLibraryType(VALID_LONG, null, null)));
    assertNotNull(sut.resolve(makeLibraryType(null, VALID_STRING, VALID_STRING)));
    assertNull(sut.resolve((LibraryType) null));
    assertNull(sut.resolve(makeLibraryType(null, null, null)));
    assertNull(sut.resolve(makeLibraryType(INVALID_LONG, null, null)));
    assertNull(sut.resolve(makeLibraryType(null, INVALID_STRING, INVALID_STRING)));
    assertNull(sut.resolve(makeLibraryType(null, VALID_STRING, null)));
    assertNull(sut.resolve(makeLibraryType(null, null, VALID_STRING)));
  }

  private LibraryType makeLibraryType(Long id, String platform, String desc) {
    LibraryType lt = new LibraryType();
    lt.setId(id == null ? LibraryType.UNSAVED_ID : id);
    lt.setPlatformType(platform);
    lt.setDescription(desc);
    return lt;
  }

  @Test
  public void testResolveLibraryDesign() {
    assertNotNull(sut.resolve(makeLibraryDesign(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeLibraryDesign(null, VALID_STRING)));
    assertNull(sut.resolve((LibraryDesign) null));
    assertNull(sut.resolve(makeLibraryDesign(null, null)));
    assertNull(sut.resolve(makeLibraryDesign(INVALID_LONG, null)));
    assertNull(sut.resolve(makeLibraryDesign(null, INVALID_STRING)));
  }

  private LibraryDesign makeLibraryDesign(Long id, String name) {
    LibraryDesign ld = new LibraryDesign();
    ld.setId(id);
    ld.setName(name);
    ld.setSampleClass(makeSampleClass(VALID_LONG, VALID_STRING));
    return ld;
  }

  @Test
  public void testResolveLibraryDesignCode() {
    assertNotNull(sut.resolve(makeLibraryDesignCode(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeLibraryDesignCode(null, VALID_STRING)));
    assertNull(sut.resolve((LibraryDesignCode) null));
    assertNull(sut.resolve(makeLibraryDesignCode(null, null)));
    assertNull(sut.resolve(makeLibraryDesignCode(INVALID_LONG, null)));
    assertNull(sut.resolve(makeLibraryDesignCode(null, INVALID_STRING)));
  }

  private LibraryDesignCode makeLibraryDesignCode(Long id, String code) {
    LibraryDesignCode ldc = new LibraryDesignCode();
    ldc.setId(id);
    ldc.setCode(code);
    return ldc;
  }

  @Test
  public void testResolveIndex() {
    assertNotNull(sut.resolve(makeIndex(VALID_LONG, null, null)));
    assertNotNull(sut.resolve(makeIndex(null, VALID_STRING, VALID_STRING)));
    assertNull(sut.resolve((Index) null));
    assertNull(sut.resolve(makeIndex(null, null, null)));
    assertNull(sut.resolve(makeIndex(INVALID_LONG, null, null)));
    assertNull(sut.resolve(makeIndex(null, INVALID_STRING, INVALID_STRING)));
    assertNull(sut.resolve(makeIndex(null, VALID_STRING, null)));
    assertNull(sut.resolve(makeIndex(null, null, VALID_STRING)));
  }

  private Index makeIndex(Long id, String familyName, String sequence) {
    Index index = new Index();
    index.setId(id == null ? Index.UNSAVED_ID : id);
    index.setFamily(new IndexFamily());
    index.getFamily().setName(familyName);
    index.setSequence(sequence);
    return index;
  }

  @Test
  public void testResolveSampleQcType() {
    assertNotNull(sut.resolveForSample(makeQcType(VALID_LONG, null)));
    assertNotNull(sut.resolveForSample(makeQcType(null, VALID_STRING)));
    assertNull(sut.resolveForSample((QcType) null));
    assertNull(sut.resolveForSample(makeQcType(null, null)));
    assertNull(sut.resolveForSample(makeQcType(INVALID_LONG, null)));
    assertNull(sut.resolveForSample(makeQcType(null, INVALID_STRING)));
  }

  @Test
  public void testResolveLibraryQcType() {
    assertNotNull(sut.resolveForLibrary(makeQcType(VALID_LONG, null)));
    assertNotNull(sut.resolveForLibrary(makeQcType(null, VALID_STRING)));
    assertNull(sut.resolveForLibrary((QcType) null));
    assertNull(sut.resolveForLibrary(makeQcType(null, null)));
    assertNull(sut.resolveForLibrary(makeQcType(INVALID_LONG, null)));
    assertNull(sut.resolveForLibrary(makeQcType(null, INVALID_STRING)));
  }

  private QcType makeQcType(Long id, String name) {
    QcType qc = new QcType();
    qc.setQcTypeId(id == null ? QcType.UNSAVED_ID : id);
    qc.setName(name);
    return qc;
  }

  @Test
  public void testResolveSequencer() {
    assertNotNull(sut.resolve(makeSequencer(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeSequencer(null, VALID_STRING)));
    assertNull(sut.resolve((SequencerReference) null));
    assertNull(sut.resolve(makeSequencer(null, null)));
    assertNull(sut.resolve(makeSequencer(INVALID_LONG, null)));
    assertNull(sut.resolve(makeSequencer(null, INVALID_STRING)));
  }

  private SequencerReference makeSequencer(Long id, String name) {
    SequencerReference seq = new SequencerReferenceImpl(name, null, null);
    seq.setId(id == null ? AbstractSequencerReference.UNSAVED_ID : id);
    return seq;
  }
  
  @Test
  public void testResolveDetailedQcStatus() {
    assertNotNull(sut.resolve(makeDetailedQcStatus(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeDetailedQcStatus(null, VALID_STRING)));
    assertNull(sut.resolve((DetailedQcStatus) null));
    assertNull(sut.resolve(makeDetailedQcStatus(null, null)));
    assertNull(sut.resolve(makeDetailedQcStatus(INVALID_LONG, null)));
    assertNull(sut.resolve(makeDetailedQcStatus(null, INVALID_STRING)));
  }
  
  private DetailedQcStatus makeDetailedQcStatus(Long id, String description) {
    DetailedQcStatus qcDet = new DetailedQcStatusImpl();
    qcDet.setId(id);
    qcDet.setDescription(description);
    return qcDet;
  }
  
  @Test
  public void testResolveSubproject() {
    assertNotNull(sut.resolve(makeSubproject(VALID_LONG, null)));
    assertNotNull(sut.resolve(makeSubproject(null, VALID_STRING)));
    assertNull(sut.resolve((Subproject) null));
    assertNull(sut.resolve(makeSubproject(null, null)));
    assertNull(sut.resolve(makeSubproject(INVALID_LONG, null)));
    assertNull(sut.resolve(makeSubproject(null, INVALID_STRING)));
  }
  
  private Subproject makeSubproject(Long id, String alias) {
    Subproject sp = new SubprojectImpl();
    sp.setId(id);
    sp.setAlias(alias);
    return sp;
  }
  
  @Test
  public void testResolveReferenceGenome() throws Exception {
    assertThat("ref with valid alias", sut.resolve(makeReferenceGenome(VALID_LONG, VALID_STRING)), is(notNullValue()));
    assertThat("ref with valid alias", sut.resolve(makeReferenceGenome(null, VALID_STRING)), is(notNullValue()));
    assertThat("ref with valid alias", sut.resolve(makeReferenceGenome(INVALID_LONG, VALID_STRING)), is(notNullValue()));
    assertThat("ref with non-existent alias", sut.resolve(makeReferenceGenome(null, INVALID_STRING)), is(nullValue()));
    assertThat("ref with null alias", sut.resolve(makeReferenceGenome(null, null)), is(nullValue()));
  }

  private ReferenceGenome makeReferenceGenome(Long id, String alias) {
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setId(id);
    referenceGenome.setAlias(alias);
    return referenceGenome;
  }

}
