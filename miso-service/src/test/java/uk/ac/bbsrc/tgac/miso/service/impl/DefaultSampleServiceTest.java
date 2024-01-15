package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.hasTemporaryName;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleStock;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.ScientificName;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleIdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleStockImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.IdentityView;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.BoxService;
import uk.ac.bbsrc.tgac.miso.core.service.DetailedQcStatusService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleValidRelationshipService;
import uk.ac.bbsrc.tgac.miso.core.service.ScientificNameService;
import uk.ac.bbsrc.tgac.miso.core.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleStore;
import uk.ac.bbsrc.tgac.miso.persistence.SecurityStore;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.persistence.impl.HibernateProjectDao;

public class DefaultSampleServiceTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Mock
  private SampleStore sampleStore;
  @Mock
  private AuthorizationManager authorizationManager;
  @Mock
  private HibernateProjectDao projectDAO;
  @Mock
  private SampleClassService sampleClassService;
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
  private SubprojectService subProjectService;
  @Mock
  private SecurityStore securityStore;
  @Mock
  private SamplePurposeDao samplePurposeDao;
  @Mock
  private TissueMaterialDao tissueMaterialDao;
  @Mock
  private NamingSchemeHolder namingSchemeHolder;
  @Mock
  private NamingScheme namingScheme;
  @Mock
  private BoxService boxService;
  @Mock
  private ScientificNameService scientificNameService;
  @Mock
  private DetailedQcStatusService detailedQcStatusService;
  @Mock
  private RequisitionService requisitionService;

  @InjectMocks
  private DefaultSampleService sut;

  private Set<SampleValidRelationship> relationships;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    sut.setAutoGenerateIdBarcodes(false);
    relationships = new HashSet<>();
    Mockito.when(namingSchemeHolder.getPrimary()).thenReturn(namingScheme);
    Mockito.when(namingSchemeHolder.get(Mockito.anyBoolean())).thenReturn(namingScheme);
    Mockito.when(namingScheme.validateSampleAlias(ArgumentMatchers.any())).thenReturn(ValidationResult.success());
    Mockito.when(namingScheme.validateName(ArgumentMatchers.anyString())).thenReturn(ValidationResult.success());
    Mockito.when(namingScheme.generateNameFor(ArgumentMatchers.any(Sample.class))).thenReturn("SAM1");
  }

  @Test
  public void temporarySampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName(LimsUtils.generateTemporaryName());
    assertTrue("Temporary sample names must return true.", hasTemporaryName(sample));
  }

  @Test
  public void notTemporarySampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName("RealSampleName");
    assertFalse("Real sample names must return false.", hasTemporaryName(sample));
  }

  @Test
  public void nullSampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName(null);
    assertFalse("Non-temporary sample names must return false.", hasTemporaryName(sample));
  }

  @Test
  public void nullSampleObjectNameTest() throws Exception {
    Sample sample = null;
    assertFalse(
        "A null sample object does not contain a temporary name so must return false.",
        hasTemporaryName(sample));
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
    Mockito.when(sampleStore.getSample(Mockito.anyLong())).thenReturn(sample);
    sut.create(sample);

    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).create(createdCapture.capture());
    Sample created = createdCapture.getValue();
    assertEquals("shell project should be replaced by real project", expectedProject.getTitle(),
        created.getProject().getTitle());
    assertNotNull("modification details should be added", created.getLastModifier());
    assertEquals("modification details should be added", expectedLastModifier.getId(),
        created.getLastModifier().getId());
    assertTrue("expected a plain sample", LimsUtils.isPlainSample(created));

    // name generators get called after initial save
    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).update(updatedCapture.capture());
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
    Mockito.when(sampleStore.getSample(Mockito.anyLong())).thenReturn(sample);
    sut.create(sample);

    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).update(updatedCapture.capture());
    Sample updated = updatedCapture.getValue();
    assertEquals("alias should be generated", expectedAlias, updated.getAlias());
  }

  @Test
  public void testPlainSampleIdentificationBarcodeGeneration() throws Exception {
    Sample sample = new SampleImpl();
    sample.setAlias("alias");
    mockShellProjectWithRealLookup(sample);
    assertNull("identificationBarcode should be null before save for test", sample.getIdentificationBarcode());
    sut.setAutoGenerateIdBarcodes(true);
    Mockito.when(sampleStore.getSample(Mockito.anyLong())).thenReturn(sample);
    sut.create(sample);

    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).update(updatedCapture.capture());
    Sample updated = updatedCapture.getValue();
    assertNotNull("identificationBarcode should be generated", updated.getIdentificationBarcode());
  }

  @Test
  public void testCreateDetailedSampleExistingParentById() throws Exception {
    SampleIdentity parent = makeParentIdentityWithLookup();
    SampleTissue child = makeUnsavedChildTissue();
    child.setParent(new SampleIdentityImpl());
    child.getParent().setId(parent.getId());

    Long newId = 89L;
    DetailedSample postSave = makeUnsavedChildTissue();
    postSave.setId(newId);
    postSave.setParent(parent);

    Mockito.when(sampleStore.create(Mockito.any(Sample.class))).thenReturn(newId);
    Mockito.when(sampleStore.getSample(newId)).thenReturn(postSave);
    mockValidRelationship(parent.getSampleClass(), child.getSampleClass());

    sut.create(child);

    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).create(createdCapture.capture());
    Sample created = createdCapture.getValue();
    assertTrue("Expected a TissueSample", LimsUtils.isTissueSample(created));
    assertNotNull("Child sample should have parent", ((SampleTissue) created).getParent());
    assertEquals("Unexpected parent ID", parent.getId(), ((SampleTissue) created).getParent().getId());
  }

  @Test
  public void testCreateDetailedSampleExistingParentByExternalName() throws Exception {
    SampleIdentity parent = makeParentIdentityWithLookup();
    SampleTissue child = makeUnsavedChildTissue();

    SampleIdentity shellParent = new SampleIdentityImpl();
    shellParent.setExternalName(parent.getExternalName());
    Long shellParentId = 88L;
    shellParent.setId(shellParentId);
    child.setParent(shellParent);

    Long newId = 89L;
    SampleTissue postSave = makeUnsavedChildTissue();
    postSave.setId(newId);
    postSave.setParent(parent);

    Mockito.when(sampleStore.create(Mockito.any(Sample.class))).thenReturn(newId);
    Mockito.when(sampleStore.getSample(newId)).thenReturn(postSave);
    Mockito.when(sampleStore.getSample(shellParentId)).thenReturn(parent);
    mockValidRelationship(parent.getSampleClass(), child.getSampleClass());

    sut.create(child);

    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).create(createdCapture.capture());
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
    Mockito.when(sampleClassService.listByCategory(Mockito.eq(SampleIdentity.CATEGORY_NAME)))
        .thenReturn(Lists.newArrayList(sample.getSampleClass()));
    exception.expect(IllegalArgumentException.class);
    sut.create(sample);
  }

  @Test
  public void testCreateDetailedSampleNoParent() throws Exception {
    SampleTissue sample = makeUnsavedChildTissue();
    SampleIdentity fakeParent = new SampleIdentityImpl();
    fakeParent.setExternalName("non-existing");
    sample.setParent(fakeParent);
    mockShellProjectWithRealLookup(sample);
    mockUser();

    // because of mocked dao, we can't actually continue with the same parent sample that should be
    // created, but the partial
    // parent sample that gets created is caught and examined below
    SampleIdentity parent = makeParentIdentityWithLookup();
    Mockito.when(sampleStore.create(Mockito.any(Sample.class))).thenReturn(parent.getId());
    mockValidRelationship(parent.getSampleClass(), sample.getSampleClass());

    Long newId = 31L;
    Mockito.when(sampleStore.create(sample)).thenReturn(newId);
    Mockito.when(sampleStore.getSample(newId)).thenReturn(sample);

    sut.create(sample);

    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore, Mockito.times(2)).create(createdCapture.capture());
    Sample partialParent = createdCapture.getAllValues().get(0);
    assertTrue(LimsUtils.isIdentitySample(partialParent));

    Sample partialChild = createdCapture.getAllValues().get(1);
    assertTrue(LimsUtils.isTissueSample(partialChild));

    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore, Mockito.times(3)).update(updatedCapture.capture());
    // note: finalParent is not actually derived from partialParent because of mocked dao (above), but
    // it should be the parent
    // linked to finalChild
    Sample finalParent = updatedCapture.getAllValues().get(0);
    Sample finalChild = updatedCapture.getAllValues().get(1);
    assertNotNull("Child sample should have parent", ((SampleTissue) finalChild).getParent());
    assertEquals("Unexpected parent ID", ((SampleTissue) finalChild).getParent().getId(), finalParent.getId());
  }

  @Test
  public void testCreateDetailedSampleNoTissue() throws Exception {
    SampleIdentity identity = makeParentIdentityWithLookup();
    SampleTissue tissue = makeUnsavedParentTissue();

    SampleIdentity shellIdentity = new SampleIdentityImpl();
    shellIdentity.setExternalName(identity.getExternalName());
    shellIdentity.setId(identity.getId());
    tissue.setParent(shellIdentity);

    SampleStock analyte = makeUnsavedChildStock();
    analyte.setParent(tissue);

    Mockito.when(sampleStore.create(tissue)).thenReturn(94L);
    Mockito.when(sampleStore.getSample(94L)).thenReturn(tissue);
    Mockito.when(sampleStore.create(analyte)).thenReturn(12L);
    Mockito.when(sampleStore.getSample(12L)).thenReturn(analyte);

    mockValidRelationship(identity.getSampleClass(), tissue.getSampleClass());
    mockValidRelationship(tissue.getSampleClass(), analyte.getSampleClass());
    sut.create(analyte);
    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore, Mockito.times(4)).update(updatedCapture.capture());
    Sample createdTissue = updatedCapture.getAllValues().get(0);
    assertTrue(LimsUtils.isTissueSample(createdTissue));
    Sample createdAnalyte = updatedCapture.getAllValues().get(2);
    assertTrue(LimsUtils.isStockSample(createdAnalyte));
  }

  @Test
  public void testCreateDetailedSampleNoTissueOrIdentity() throws Exception {
    SampleIdentity identity = makeUnsavedParentIdentity();
    Mockito.when(sampleClassService.listByCategory(Mockito.eq(SampleIdentity.CATEGORY_NAME)))
        .thenReturn(Lists.newArrayList(identity.getSampleClass()));
    SampleTissue tissue = makeUnsavedParentTissue();

    SampleIdentity shellIdentity = new SampleIdentityImpl();
    shellIdentity.setExternalName(identity.getExternalName());
    tissue.setParent(shellIdentity);

    SampleStock analyte = makeUnsavedChildStock();
    analyte.setParent(tissue);

    SampleIdentity identityPostCreate = makeUnsavedParentIdentity();
    identityPostCreate.setId(39L);
    Mockito.when(sampleStore.create(Mockito.any(Sample.class))).thenReturn(identityPostCreate.getId());
    Mockito.when(sampleStore.getSample(39L)).thenReturn(identityPostCreate);
    Mockito.when(sampleStore.create(tissue)).thenReturn(94L);
    Mockito.when(sampleStore.getSample(94L)).thenReturn(tissue);
    Mockito.when(sampleStore.create(analyte)).thenReturn(12L);
    Mockito.when(sampleStore.getSample(12L)).thenReturn(analyte);

    mockValidRelationship(identity.getSampleClass(), tissue.getSampleClass());
    mockValidRelationship(tissue.getSampleClass(), analyte.getSampleClass());
    sut.create(analyte);
    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore, Mockito.times(5)).update(updatedCapture.capture());
    Sample createdIdentity = updatedCapture.getAllValues().get(0);
    assertTrue(LimsUtils.isIdentitySample(createdIdentity));
    Sample createdTissue = updatedCapture.getAllValues().get(1);
    assertTrue(LimsUtils.isTissueSample(createdTissue));
    Sample createdAnalyte = updatedCapture.getAllValues().get(3);
    assertTrue(LimsUtils.isStockSample(createdAnalyte));
  }

  @Test
  public void testUpdatePlainSample() throws Exception {
    Sample old = makePlainSample();
    Mockito.when(sampleStore.getSample(old.getId())).thenReturn(old);

    Sample updated = makePlainSample();
    // modifiable attributes
    updated.setAlias("newAlias");
    updated.setDescription("newDesc");
    updated.setSampleType("newType");
    updated.setDetailedQcStatus(mockDetailedQcStatus(5L, "Ready", false));
    updated.setQcUser(mockUser());
    updated.setScientificName(mockScientificName(2L, "newSciName"));
    updated.setTaxonIdentifier("newTaxonId");
    updated.setVolume(new BigDecimal("5.5"));
    updated.setVolumeUnits(VolumeUnit.MICROLITRES);

    // unmodifiable
    updated.setName("newName");
    mockShellProjectWithRealLookup(updated);

    sut.update(updated);

    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).update(updatedCapture.capture());
    Sample result = updatedCapture.getValue();
    assertEquals("Sample sampleType should be modifiable", updated.getSampleType(), result.getSampleType());
    assertEquals("Sample description should be modifiable", updated.getDescription(), result.getDescription());
    assertEquals("Sample sampleType should be modifiable", updated.getSampleType(), result.getSampleType());
    assertEquals("Sample detailedQcStatus should be modifiable", updated.getDetailedQcStatus(),
        result.getDetailedQcStatus());
    assertEquals("Sample scientificName should be modifiable", updated.getScientificName(), result.getScientificName());
    assertEquals("Sample taxonIdentifier should be modifiable", updated.getTaxonIdentifier(),
        result.getTaxonIdentifier());
    assertEquals("Sample volume should be modifiable", updated.getVolume(), result.getVolume());
    assertEquals("Sample project should be modifiable", updated.getProject().getId(), result.getProject().getId());

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
    IdentityView id1 = new IdentityView();
    id1.setExternalName("String1,String2");
    id1.setProjectId(project.getId());
    Mockito.when(sut.getIdentitiesByExternalNameOrAliasAndProject(ArgumentMatchers.anyString(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
        .thenReturn(Collections.singletonList(id1));
    Sample newSample = new SampleImpl();
    newSample.setProject(project);
    sut.confirmExternalNameUniqueForProjectIfRequired("String3", newSample);
  }

  @Test
  public void testNonUniqueExternalNamePerProjectFailTest() throws IOException {
    Project project = new ProjectImpl();
    project.setId(1L);
    project.setReferenceGenome(humanReferenceGenome());
    IdentityView id1 = new IdentityView();
    id1.setId(1L);
    id1.setExternalName("String1,String2");
    id1.setProjectId(project.getId());
    Sample newSample = new SampleImpl();
    newSample.setProject(project);
    Mockito.when(sut.getIdentitiesByExternalNameOrAliasAndProject(ArgumentMatchers.anyString(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
        .thenReturn(Collections.singletonList(id1));
    exception.expect(ValidationException.class);
    sut.confirmExternalNameUniqueForProjectIfRequired("String1,String3", newSample);
  }

  @Test
  public void testCanEditExternalNameTest() throws IOException {
    Project project = new ProjectImpl();
    project.setId(1L);
    project.setReferenceGenome(humanReferenceGenome());
    IdentityView id1 = new IdentityView();
    id1.setId(1L);
    id1.setExternalName("String1,String2");
    id1.setProjectId(project.getId());
    Mockito.when(sut.getIdentitiesByExternalNameOrAliasAndProject(ArgumentMatchers.anyString(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
        .thenReturn(Collections.singletonList(id1));
    Sample newSample = new SampleImpl();
    newSample.setProject(project);
    exception.expect(ValidationException.class);
    sut.confirmExternalNameUniqueForProjectIfRequired("String1", newSample);
  }

  @Test
  public void testNonUniqueExternalNamePerProjectPassTest() throws IOException {
    Project project = new ProjectImpl();
    project.setId(1L);
    project.setReferenceGenome(humanReferenceGenome());
    IdentityView id1 = new IdentityView();
    id1.setExternalName("String1,String2");
    id1.setProjectId(project.getId());
    sut.setUniqueExternalNameWithinProjectRequired(false);
    Mockito.when(sut.getIdentitiesByExternalNameOrAliasAndProject(ArgumentMatchers.anyString(),
        ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean()))
        .thenReturn(Collections.singletonList(id1));
    Sample newSample = new SampleImpl();
    newSample.setProject(project);
    sut.confirmExternalNameUniqueForProjectIfRequired("String1", newSample);
  }

  @Test
  public void testAddNote() throws Exception {
    Sample paramSample = new SampleImpl();
    paramSample.setId(1L);
    paramSample.setAlias("paramSample");

    Note note = new Note();

    Sample dbSample = new SampleImpl();
    dbSample.setId(paramSample.getId());
    dbSample.setAlias("persistedSample");
    Mockito.when(sampleStore.get(paramSample.getId())).thenReturn(dbSample);

    sut.addNote(paramSample, note);

    ArgumentCaptor<Sample> capture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).update(capture.capture());
    Sample savedSample = capture.getValue();
    assertEquals(1, savedSample.getNotes().size());
  }

  @Test
  public void testDeleteNote() throws Exception {
    Sample paramSample = new SampleImpl();
    paramSample.setId(1L);
    paramSample.setAlias("paramSample");

    Sample dbSample = new SampleImpl();
    dbSample.setId(paramSample.getId());
    dbSample.setAlias("persistedSample");
    Note note = new Note();
    note.setId(3L);
    User owner = new UserImpl();
    owner.setId(5L);
    note.setOwner(owner);
    dbSample.addNote(note);
    Mockito.when(sampleStore.get(paramSample.getId())).thenReturn(dbSample);

    sut.deleteNote(paramSample, note.getId());

    Mockito.verify(authorizationManager).throwIfNonAdminOrMatchingOwner(owner);
    ArgumentCaptor<Sample> capture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleStore).update(capture.capture());
    Sample savedSample = capture.getValue();
    assertTrue(savedSample.getNotes().isEmpty());
  }

  private Sample makePlainSample() {
    Sample sample = new SampleImpl();
    sample.setId(77L);
    sample.setAlias("alias");
    sample.setName("name");
    return sample;
  }

  private SampleIdentity makeParentIdentityWithLookup() throws IOException {
    SampleIdentity sample = makeUnsavedParentIdentity();
    Mockito.when(sampleStore.getSample(sample.getId())).thenReturn(sample);
    Mockito.when(sampleClassService.listByCategory(Mockito.eq(SampleIdentity.CATEGORY_NAME)))
        .thenReturn(Lists.newArrayList(sample.getSampleClass()));
    return sample;
  }

  private SampleIdentity makeUnsavedParentIdentity() throws IOException {
    SampleIdentity sample = new SampleIdentityImpl();
    sample.setId(63L);
    sample.setSampleClass(new SampleClassImpl());
    sample.getSampleClass().setId(51L);
    sample.getSampleClass().setAlias("identity");
    sample.getSampleClass().setSampleCategory(SampleIdentity.CATEGORY_NAME);
    sample.setExternalName("external");
    return sample;
  }

  private SampleTissue makeUnsavedChildTissue() throws IOException {
    SampleTissue sample = makeUnsavedParentTissue();
    sample.setSampleType("type");
    ScientificName sn = mockScientificName(1L, "scientific");
    sample.setScientificName(sn);
    mockShellProjectWithRealLookup(sample);
    return sample;
  }

  private SampleTissue makeUnsavedParentTissue() throws IOException {
    SampleTissue sample = new SampleTissueImpl();
    sample.setSampleClass(new SampleClassImpl());
    sample.getSampleClass().setId(10L);
    sample.getSampleClass().setAlias("tissue");
    sample.getSampleClass().setSampleCategory(SampleTissue.CATEGORY_NAME);
    Mockito.when(sampleClassService.get(sample.getSampleClass().getId())).thenReturn(sample.getSampleClass());
    return sample;
  }

  private SampleStock makeUnsavedChildStock() throws IOException {
    SampleStock sample = new SampleStockImpl();
    sample.setSampleType("type");
    sample.setScientificName(mockScientificName(1L, "scientific"));
    sample.setLastModifier(mockUser());
    sample.setSampleClass(new SampleClassImpl());
    sample.getSampleClass().setId(30L);
    sample.getSampleClass().setAlias("analyte");
    sample.getSampleClass().setSampleCategory(SampleStock.CATEGORY_NAME);
    Mockito.when(sampleClassService.get(sample.getSampleClass().getId())).thenReturn(sample.getSampleClass());
    mockShellProjectWithRealLookup(sample);
    return sample;
  }

  private void mockValidRelationship(SampleClass parentClass, SampleClass childClass) throws IOException {
    SampleValidRelationship rel = new SampleValidRelationshipImpl();
    rel.setParent(parentClass);
    rel.setChild(childClass);
    relationships.add(rel);
    Mockito.when(sampleValidRelationshipService.getAll()).thenReturn(relationships);
    Mockito.when(sampleValidRelationshipService.getByClasses(parentClass, childClass)).thenReturn(rel);
  }

  /**
   * Adds a shell project to the provided Sample, and adds the real project to the mocked
   * projectStore. The projectStore should be queried to swap in the persisted object in place of the
   * shell
   * 
   * @param sample the Sample to add shell project to
   * @return the "real" project that will be returned by the mock projectStore
   * @throws IOException
   */
  private Project mockShellProjectWithRealLookup(Sample sample) throws IOException {
    Project shell = new ProjectImpl();
    shell.setTitle("shell_project");
    shell.setId(32L);
    sample.setProject(shell);

    Project project = new ProjectImpl();
    project.setId(shell.getId());
    project.setTitle("real_project");
    project.setCode("PROJ");
    Mockito.when(projectStore.get(shell.getId())).thenReturn(project);
    return project;
  }

  private User mockUser() throws IOException {
    User user = new UserImpl();
    user.setId(15L);
    Mockito.when(authorizationManager.getCurrentUser()).thenReturn(user);
    Mockito.when(securityStore.getUserById(user.getId())).thenReturn(user);
    return user;
  }

  private ScientificName mockScientificName(long id, String alias) throws IOException {
    ScientificName sn = new ScientificName();
    sn.setId(id);
    sn.setAlias(alias);
    Mockito.when(scientificNameService.get(id)).thenReturn(sn);
    return sn;
  }

  private DetailedQcStatus mockDetailedQcStatus(long id, String description, boolean noteRequired) throws IOException {
    DetailedQcStatus status = new DetailedQcStatusImpl();
    status.setId(id);
    status.setDescription(description);
    status.setNoteRequired(noteRequired);
    Mockito.when(detailedQcStatusService.get(id)).thenReturn(status);
    return status;
  }

  @Test
  public void testValidateRelationshipForSimpleSample() throws Exception {
    Sample child = new SampleImpl(); // Simple sample has no DetailedSample attributes.
    Sample parent = null; // Simple sample has no parent.
    assertTrue(
        "Simple sample with a null parent and null DetailedSample is a valid relationship",
        sut.isValidRelationship(parent, child));
  }

}
