package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.KitComponentStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.DetailedQcStatusDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleGroupDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO;

public class DefaultSampleServiceTestSuite {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  private SampleDao sampleDao;

  @Mock
  private AuthorizationManager authorizationManager;

  @Mock
  private SQLProjectDAO sqlProjectDAO;

  @Mock
  private SampleClassDao sampleClassDao;

  @Mock
  private SampleValidRelationshipService sampleValidRelationshipService;

  @Mock
  private SampleNumberPerProjectService sampleNumberPerProjectService;

  @Mock
  private ProjectStore projectStore;

  @Mock
  private TissueOriginDao tissueOriginDao;

  @Mock
  private TissueTypeDao tissueTypeDao;

  @Mock
  private DetailedQcStatusDao detailedQcStatusDao;

  @Mock
  private SubprojectDao subProjectDao;

  @Mock
  private KitComponentStore kitStore;

  @Mock
  private SecurityStore securityStore;

  @Mock
  private SamplePurposeDao samplePurposeDao;

  @Mock
  private SampleGroupDao sampleGroupDao;

  @Mock
  private TissueMaterialDao tissueMaterialDao;

  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private DefaultSampleService sut;

  private Set<SampleValidRelationship> relationships;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    sut.setAutoGenerateIdBarcodes(false);
    relationships = new HashSet<>();
    Mockito.when(namingScheme.validateSampleAlias(Matchers.anyString())).thenReturn(ValidationResult.success());
  }

  @Test
  public void temporarySampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName(DefaultSampleService.generateTemporaryName());
    assertTrue("Temporary sample names must return true.", DefaultSampleService.hasTemporaryName(sample));
  }

  @Test
  public void notTemporarySampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName("RealSampleName");
    assertFalse("Real sample names must return false.", DefaultSampleService.hasTemporaryName(sample));
  }

  @Test
  public void nullSampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName(null);
    assertFalse("Non-temporary sample names must return false.", DefaultSampleService.hasTemporaryName(sample));
  }

  @Test
  public void nullSampleObjectNameTest() throws Exception {
    Sample sample = null;
    assertFalse(
        "A null sample object does not contain a temporary name so must return false.",
        DefaultSampleService.hasTemporaryName(sample));
  }

  @Test
  public void testCreatePlainSample() throws Exception {
    Sample sample = new SampleImpl();
    String expectedAlias = "not_generated";
    sample.setAlias(expectedAlias);
    Project expectedProject = mockShellProjectWithRealLookup(sample);
    User expectedLastModifier = mockUser();
    String expectedName = "generated_name";
    Mockito.when(namingScheme.generateNameFor((Sample) Mockito.any())).thenReturn(expectedName);
    Mockito.when(namingScheme.generateSampleAlias((Sample) Mockito.any())).thenReturn("bad");
    Mockito.when(sampleDao.getSample(Mockito.anyLong())).thenReturn(sample);
    sut.create(sample);

    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).addSample(createdCapture.capture());
    Sample created = createdCapture.getValue();
    assertEquals("shell project should be replaced by real project", expectedProject.getAlias(), created.getProject().getAlias());
    assertNotNull("modification details should be added", created.getLastModifier());
    assertEquals("modification details should be added", expectedLastModifier.getUserId(), created.getLastModifier().getUserId());
    assertTrue("expected a plain sample", LimsUtils.isPlainSample(created));

    // name generators get called after initial save
    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).update(updatedCapture.capture());
    Sample updated = updatedCapture.getValue();
    assertEquals("name should be generated", expectedName, updated.getName());
    assertEquals("alias should not be generated", expectedAlias, updated.getAlias());
    assertNull("identificationBarcode should not be generated", updated.getIdentificationBarcode());
    assertTrue("expected a plain sample", LimsUtils.isPlainSample(updated));
  }

  @Test
  public void testPlainSampleAliasGeneration() throws Exception {
    Sample sample = new SampleImpl();
    mockShellProjectWithRealLookup(sample);
    String expectedAlias = "generated_alias";
    Mockito.when(namingScheme.hasSampleAliasGenerator()).thenReturn(true);
    Mockito.when(namingScheme.generateSampleAlias((Sample) Mockito.any())).thenReturn(expectedAlias);
    Mockito.when(sampleDao.getSample(Mockito.anyLong())).thenReturn(sample);
    sut.create(sample);

    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).update(updatedCapture.capture());
    Sample updated = updatedCapture.getValue();
    assertEquals("alias should be generated", expectedAlias, updated.getAlias());
  }

  @Test
  public void testPlainSampleIdentificationBarcodeGeneration() throws Exception {
    Sample sample = new SampleImpl();
    mockShellProjectWithRealLookup(sample);
    assertNull("identificationBarcode should be null before save for test", sample.getIdentificationBarcode());
    sut.setAutoGenerateIdBarcodes(true);
    Mockito.when(sampleDao.getSample(Mockito.anyLong())).thenReturn(sample);
    sut.create(sample);

    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).update(updatedCapture.capture());
    Sample updated = updatedCapture.getValue();
    assertNotNull("identificationBarcode should be generated", updated.getIdentificationBarcode());
  }

  @Test
  public void testCreateDetailedSampleExistingParentById() throws Exception {
    Identity parent = makeParentIdentityWithLookup();
    SampleTissue child = makeUnsavedChildTissue();
    child.setParent(new IdentityImpl());
    child.getParent().setId(parent.getId());

    Long newId = 89L;
    DetailedSample postSave = makeUnsavedChildTissue();
    postSave.setId(newId);
    postSave.setParent(parent);

    Mockito.when(sampleDao.addSample(Mockito.any(Sample.class))).thenReturn(newId);
    Mockito.when(sampleDao.getSample(newId)).thenReturn(postSave);
    mockValidRelationship(parent.getSampleClass(), child.getSampleClass());

    sut.create(child);

    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).addSample(createdCapture.capture());
    Sample created = createdCapture.getValue();
    assertTrue("Expected a TissueSample", LimsUtils.isTissueSample(created));
    assertNotNull("Child sample should have parent", ((SampleTissue) created).getParent());
    assertEquals("Unexpected parent ID", parent.getId(), ((SampleTissue) created).getParent().getId());
  }

  @Test
  public void testCreateDetailedSampleExistingParentByExternalName() throws Exception {
    Identity parent = makeParentIdentityWithLookup();
    SampleTissue child = makeUnsavedChildTissue();

    Identity shellParent = new IdentityImpl();
    shellParent.setExternalName(parent.getExternalName());
    shellParent.setSecurityProfile(parent.getSecurityProfile());
    shellParent.getSecurityProfile().setOwner(mockUser());
    Long shellParentId = 88L;
    shellParent.setId(shellParentId);
    child.setParent(shellParent);

    Long newId = 89L;
    SampleTissue postSave = makeUnsavedChildTissue();
    postSave.setId(newId);
    postSave.setParent(parent);

    Mockito.when(sampleDao.addSample(Mockito.any(Sample.class))).thenReturn(newId);
    Mockito.when(sampleDao.getSample(newId)).thenReturn(postSave);
    Mockito.when(sampleDao.getSample(shellParentId)).thenReturn(parent);
    mockValidRelationship(parent.getSampleClass(), child.getSampleClass());

    sut.create(child);

    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).addSample(createdCapture.capture());
    Sample created = createdCapture.getValue();
    assertTrue("Expected a TissueSample", LimsUtils.isTissueSample(created));
    assertNotNull("Child sample should have parent", ((SampleTissue) created).getParent());
    assertEquals("Unexpected parent ID", parent.getId(), ((SampleTissue) created).getParent().getId());
  }

  @Test
  public void testCreateDetailedSampleMissingIdentity() throws Exception {
    SampleTissue sample = new SampleTissueImpl();
    sample.setSampleClass(new SampleClassImpl());
    sample.getSampleClass().setSampleCategory(SampleTissue.CATEGORY_NAME);
    Mockito.when(sampleClassDao.listByCategory(Mockito.eq(Identity.CATEGORY_NAME))).thenReturn(Lists.newArrayList(sample.getSampleClass()));
    exception.expect(IllegalArgumentException.class);
    sut.create(sample);
  }

  @Test
  public void testCreateDetailedSampleNoParent() throws Exception {
    SampleTissue sample = makeUnsavedChildTissue();
    Identity fakeParent = new IdentityImpl();
    fakeParent.setExternalName("non-existing");
    fakeParent.setSecurityProfile(new SecurityProfile(mockUser()));
    fakeParent.getSecurityProfile().setOwner(mockUser());
    sample.setParent(fakeParent);
    mockShellProjectWithRealLookup(sample);
    mockUser();

    // because of mocked dao, we can't actually continue with the same parent sample that should be created, but the partial
    // parent sample that gets created is caught and examined below
    Identity parent = makeParentIdentityWithLookup();
    Mockito.when(sampleDao.addSample(Mockito.any(Sample.class))).thenReturn(parent.getId());
    mockValidRelationship(parent.getSampleClass(), sample.getSampleClass());

    Long newId = 31L;
    Mockito.when(sampleDao.addSample(sample)).thenReturn(newId);
    Mockito.when(sampleDao.getSample(newId)).thenReturn(sample);

    sut.create(sample);

    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao, Mockito.times(2)).addSample(createdCapture.capture());
    Sample partialParent = createdCapture.getAllValues().get(0);
    assertTrue(LimsUtils.isIdentitySample(partialParent));

    Sample partialChild = createdCapture.getAllValues().get(1);
    assertTrue(LimsUtils.isTissueSample(partialChild));

    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao, Mockito.times(2)).update(updatedCapture.capture());
    // note: finalParent is not actually derived from partialParent because of mocked dao (above), but it should be the parent
    // linked to finalChild
    Sample finalParent = updatedCapture.getAllValues().get(0);
    Sample finalChild = updatedCapture.getAllValues().get(1);
    assertNotNull("Child sample should have parent", ((SampleTissue) finalChild).getParent());
    assertEquals("Unexpected parent ID", ((SampleTissue) finalChild).getParent().getId(), finalParent.getId());
  }

  @Test
  public void testCreateDetailedSampleNoTissue() throws Exception {
    Identity identity = makeParentIdentityWithLookup();
    SampleTissue tissue = makeUnsavedParentTissue();

    Identity shellIdentity = new IdentityImpl();
    shellIdentity.setExternalName(identity.getExternalName());
    shellIdentity.setId(identity.getId());
    tissue.setParent(shellIdentity);

    SampleStock analyte = makeUnsavedChildStock();
    analyte.setParent(tissue);

    Mockito.when(sampleDao.addSample(tissue)).thenReturn(94L);
    Mockito.when(sampleDao.getSample(94L)).thenReturn(tissue);
    Mockito.when(sampleDao.addSample(analyte)).thenReturn(12L);
    Mockito.when(sampleDao.getSample(12L)).thenReturn(analyte);

    mockValidRelationship(identity.getSampleClass(), tissue.getSampleClass());
    mockValidRelationship(tissue.getSampleClass(), analyte.getSampleClass());
    sut.create(analyte);
    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao, Mockito.times(2)).update(updatedCapture.capture());
    Sample createdTissue = updatedCapture.getAllValues().get(0);
    assertTrue(LimsUtils.isTissueSample(createdTissue));
    Sample createdAnalyte = updatedCapture.getAllValues().get(1);
    assertTrue(LimsUtils.isStockSample(createdAnalyte));
  }

  @Test
  public void testCreateDetailedSampleNoTissueOrIdentity() throws Exception {
    Identity identity = makeUnsavedParentIdentity();
    Mockito.when(sampleClassDao.listByCategory(Mockito.eq(Identity.CATEGORY_NAME)))
        .thenReturn(Lists.newArrayList(identity.getSampleClass()));
    SampleTissue tissue = makeUnsavedParentTissue();

    Identity shellIdentity = new IdentityImpl();
    shellIdentity.setExternalName(identity.getExternalName());
    tissue.setParent(shellIdentity);

    SampleStock analyte = makeUnsavedChildStock();
    analyte.setParent(tissue);

    Identity identityPostCreate = makeUnsavedParentIdentity();
    identityPostCreate.setId(39L);
    Mockito.when(sampleDao.addSample(Mockito.any(Sample.class))).thenReturn(identityPostCreate.getId());
    Mockito.when(sampleDao.getSample(39L)).thenReturn(identityPostCreate);
    Mockito.when(sampleDao.addSample(tissue)).thenReturn(94L);
    Mockito.when(sampleDao.getSample(94L)).thenReturn(tissue);
    Mockito.when(sampleDao.addSample(analyte)).thenReturn(12L);
    Mockito.when(sampleDao.getSample(12L)).thenReturn(analyte);

    mockValidRelationship(identity.getSampleClass(), tissue.getSampleClass());
    mockValidRelationship(tissue.getSampleClass(), analyte.getSampleClass());
    sut.create(analyte);
    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao, Mockito.times(3)).update(updatedCapture.capture());
    Sample createdIdentity = updatedCapture.getAllValues().get(0);
    assertTrue(LimsUtils.isIdentitySample(createdIdentity));
    Sample createdTissue = updatedCapture.getAllValues().get(1);
    assertTrue(LimsUtils.isTissueSample(createdTissue));
    Sample createdAnalyte = updatedCapture.getAllValues().get(2);
    assertTrue(LimsUtils.isStockSample(createdAnalyte));
  }

  @Test
  public void testUpdatePlainSample() throws Exception {
    Sample old = makePlainSample();
    Mockito.when(sampleDao.getSample(old.getId())).thenReturn(old);

    Sample updated = makePlainSample();
    // modifiable attributes
    updated.setAlias("newAlias");
    updated.setDescription("newDesc");
    updated.setSampleType("newType");
    updated.setReceivedDate(new Date());
    updated.setQcPassed(true);
    updated.setScientificName("newSciName");
    updated.setTaxonIdentifier("newTaxonId");
    updated.setVolume(5.5D);

    // unmodifiable
    updated.setName("newName");
    mockShellProjectWithRealLookup(updated);

    sut.update(updated);

    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).update(updatedCapture.capture());
    Sample result = updatedCapture.getValue();
    assertEquals("Sample sampleType should be modifiable", updated.getSampleType(), result.getSampleType());
    assertEquals("Sample description should be modifiable", updated.getDescription(), result.getDescription());
    assertEquals("Sample sampleType should be modifiable", updated.getSampleType(), result.getSampleType());
    assertEquals("Sample receivedDate should be modifiable", updated.getReceivedDate(), result.getReceivedDate());
    assertEquals("Sample qcPassed should be modifiable", updated.getQcPassed(), result.getQcPassed());
    assertEquals("Sample scientificName should be modifiable", updated.getScientificName(), result.getScientificName());
    assertEquals("Sample taxonIdentifier should be modifiable", updated.getTaxonIdentifier(), result.getTaxonIdentifier());
    assertEquals("Sample volume should be modifiable", updated.getVolume(), result.getVolume());

    assertNull("Sample project should NOT be modifiable", result.getProject());
    assertEquals("Sample name should NOT be modifiable", old.getName(), result.getName());
  }

  private ReferenceGenome humanReferenceGenome() {
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setAlias("hg19");
    referenceGenome.setId(1L);
    return referenceGenome;
  }

  @Test
  public void testUniqueExternalNamePerProjectTest() throws IOException {
    Project project = new ProjectImpl();
    project.setId(1L);
    project.setReferenceGenome(humanReferenceGenome());
    Set<Identity> idList = new HashSet<>();
    Identity id1 = new IdentityImpl();
    id1.setExternalName("String1,String2");
    id1.setProject(project);
    idList.add(id1);
    Mockito.when(sut.getIdentitiesByExternalNameOrAlias(Matchers.anyString())).thenReturn(idList);
    Sample newSample = new SampleImpl();
    newSample.setProject(project);
    sut.confirmExternalNameUniqueForProjectIfRequired("String3", newSample);
  }

  @Test
  public void testNonUniqueExternalNamePerProjectFailTest() throws IOException {
    Project project = new ProjectImpl();
    project.setId(1L);
    project.setReferenceGenome(humanReferenceGenome());
    Set<Identity> idList = new HashSet<>();
    Identity id1 = new IdentityImpl();
    id1.setExternalName("String1,String2");
    id1.setProject(project);
    idList.add(id1);
    Mockito.when(sut.getIdentitiesByExternalNameOrAlias(Matchers.anyString())).thenReturn(idList);
    Sample newSample = new SampleImpl();
    newSample.setProject(project);
    exception.expect(ConstraintViolationException.class);
    sut.confirmExternalNameUniqueForProjectIfRequired("String1", newSample);
  }

  @Test
  public void testNonUniqueExternalNamePerProjectPassTest() throws IOException {
    Project project = new ProjectImpl();
    project.setId(1L);
    project.setReferenceGenome(humanReferenceGenome());
    Set<Identity> idList = new HashSet<>();
    Identity id1 = new IdentityImpl();
    id1.setExternalName("String1,String2");
    id1.setProject(project);
    idList.add(id1);
    sut.setUniqueExternalNameWithinProjectRequired(false);
    Mockito.when(sut.getIdentitiesByExternalNameOrAlias(Matchers.anyString())).thenReturn(idList);
    Sample newSample = new SampleImpl();
    newSample.setProject(project);
    sut.confirmExternalNameUniqueForProjectIfRequired("String1", newSample);
  }

  private Sample makePlainSample() {
    Sample sample = new SampleImpl();
    sample.setId(77L);
    sample.setAlias("alias");
    sample.setName("name");
    return sample;
  }

  private Identity makeParentIdentityWithLookup() throws IOException {
    Identity sample = makeUnsavedParentIdentity();
    Mockito.when(sampleDao.getSample(sample.getId())).thenReturn(sample);
    Mockito.when(sampleClassDao.listByCategory(Mockito.eq(Identity.CATEGORY_NAME))).thenReturn(Lists.newArrayList(sample.getSampleClass()));
    return sample;
  }

  private Identity makeUnsavedParentIdentity() throws IOException {
    Identity sample = new IdentityImpl();
    sample.setId(63L);
    sample.setSampleClass(new SampleClassImpl());
    sample.getSampleClass().setId(51L);
    sample.getSampleClass().setAlias("identity");
    sample.getSampleClass().setSampleCategory(Identity.CATEGORY_NAME);
    sample.setExternalName("external");
    sample.setSecurityProfile(new SecurityProfile(mockUser()));
    sample.getSecurityProfile().setOwner(mockUser());
    return sample;
  }

  private SampleTissue makeUnsavedChildTissue() {
    SampleTissue sample = makeUnsavedParentTissue();
    sample.setSampleType("type");
    sample.setScientificName("scientific");
    return sample;
  }

  private SampleTissue makeUnsavedParentTissue() {
    SampleTissue sample = new SampleTissueImpl();
    sample.setSampleClass(new SampleClassImpl());
    sample.getSampleClass().setId(10L);
    sample.getSampleClass().setAlias("tissue");
    sample.getSampleClass().setSampleCategory(SampleTissue.CATEGORY_NAME);
    Mockito.when(sampleClassDao.getSampleClass(sample.getSampleClass().getId())).thenReturn(sample.getSampleClass());
    return sample;
  }

  private SampleStock makeUnsavedChildStock() throws IOException {
    SampleStock sample = new SampleStockImpl();
    sample.setSampleType("type");
    sample.setScientificName("scientific");
    sample.setLastModifier(mockUser());
    sample.setSampleClass(new SampleClassImpl());
    sample.getSampleClass().setId(30L);
    sample.getSampleClass().setAlias("analyte");
    sample.getSampleClass().setSampleCategory(SampleStock.CATEGORY_NAME);
    Mockito.when(sampleClassDao.getSampleClass(sample.getSampleClass().getId())).thenReturn(sample.getSampleClass());
    mockShellProjectWithRealLookup(sample);
    return sample;
  }

  private void mockValidRelationship(SampleClass parentClass, SampleClass childClass) throws IOException {
    SampleValidRelationship rel = new SampleValidRelationshipImpl();
    rel.setParent(parentClass);
    rel.setChild(childClass);
    relationships.add(rel);
    Mockito.when(sampleValidRelationshipService.getAll()).thenReturn(relationships);
  }

  /**
   * Adds a shell project to the provided Sample, and adds the real project to the mocked projectStore. The projectStore should be queried
   * to swap in the persisted object in place of the shell
   *
   * @param sample the Sample to add shell project to
   * @return the "real" project that will be returned by the mock projectStore
   * @throws IOException
   */
  private Project mockShellProjectWithRealLookup(Sample sample) throws IOException {
    Project shell = new ProjectImpl();
    shell.setAlias("shell_project");
    shell.setId(32L);
    sample.setProject(shell);

    Project project = new ProjectImpl();
    project.setId(shell.getId());
    project.setAlias("real_project");
    project.setShortName("PROJ");
    project.setSecurityProfile(new SecurityProfile(mockUser()));
    project.getSecurityProfile().setOwner(mockUser());
    Mockito.when(projectStore.get(shell.getId())).thenReturn(project);
    Mockito.when(projectStore.lazyGet(shell.getId())).thenReturn(project);
    return project;
  }

  private User mockUser() throws IOException {
    User user = new UserImpl();
    user.setUserId(15L);
    Mockito.when(authorizationManager.getCurrentUser()).thenReturn(user);
    Mockito.when(securityStore.getUserById(user.getUserId())).thenReturn(user);
    return user;
  }

}
