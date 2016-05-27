package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.eaglegenomics.simlims.core.User;
import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.core.data.Identity;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.IdentityImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleTissueImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleValidRelationshipImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.KitStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SecurityStore;
import uk.ac.bbsrc.tgac.miso.persistence.QcPassedDetailDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleClassDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleDao;
import uk.ac.bbsrc.tgac.miso.persistence.SampleGroupDao;
import uk.ac.bbsrc.tgac.miso.persistence.SamplePurposeDao;
import uk.ac.bbsrc.tgac.miso.persistence.SubprojectDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueMaterialDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueOriginDao;
import uk.ac.bbsrc.tgac.miso.persistence.TissueTypeDao;
import uk.ac.bbsrc.tgac.miso.service.IdentityService;
import uk.ac.bbsrc.tgac.miso.service.SampleAdditionalInfoService;
import uk.ac.bbsrc.tgac.miso.service.SampleAnalyteService;
import uk.ac.bbsrc.tgac.miso.service.SampleNumberPerProjectService;
import uk.ac.bbsrc.tgac.miso.service.SampleTissueService;
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
  private SampleAnalyteService sampleAnalyteService;

  @Mock
  private SampleAdditionalInfoService sampleAdditionalInfoService;

  @Mock
  private IdentityService identityService;

  @Mock
  private SampleValidRelationshipService sampleValidRelationshipService;

  @Mock
  private SampleNumberPerProjectService sampleNumberPerProjectService;

  @Mock
  private SampleTissueService sampleTissueService;
  
  @Mock
  private ProjectStore projectStore;
  
  @Mock
  private TissueOriginDao tissueOriginDao;
  
  @Mock
  private TissueTypeDao tissueTypeDao;
  
  @Mock
  private QcPassedDetailDao qcPassedDetailDao;
  
  @Mock
  private SubprojectDao subProjectDao;
  
  @Mock
  private KitStore kitStore;
  
  @Mock
  private SecurityStore securityStore;
  
  @Mock
  private SamplePurposeDao samplePurposeDao;
  
  @Mock
  private SampleGroupDao sampleGroupDao;
  
  @Mock
  private TissueMaterialDao tissueMaterialDao;
  
  @Mock
  private MisoNamingScheme<Sample> sampleNamingScheme;
  
  @Mock
  private MisoNamingScheme<Sample> namingScheme;
  
  @InjectMocks
  private DefaultSampleService sut;
  
  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    sut.setAutoGenerateIdBarcodes(false);
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
    assertFalse("A null sample object does not contain a temporary name so must return false.",
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
    Mockito.when(namingScheme.generateNameFor(Mockito.eq("name"), (Sample) Mockito.any())).thenReturn(expectedName);
    Mockito.when(sampleNamingScheme.generateNameFor(Mockito.eq("alias"), (Sample) Mockito.any())).thenReturn("bad");
    Mockito.when(sampleDao.getSample(Mockito.anyLong())).thenReturn(sample);
    sut.create(sample);
    
    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).addSample(createdCapture.capture());
    Sample created = createdCapture.getValue();
    assertEquals("shell project should be replaced by real project", expectedProject.getAlias(), created.getProject().getAlias());
    assertNotNull("modification details should be added", created.getLastModifier());
    assertEquals("modification details should be added", expectedLastModifier.getUserId(), created.getLastModifier().getUserId());
    assertNull("plain sample still should not contain SampleAdditionalInfo", created.getSampleAdditionalInfo());
    assertNull("plain sample still should not contain Identity", created.getIdentity());
    assertNull("plain sample still should not contain SampleTissue", created.getSampleTissue());
    assertNull("plain sample still should not contain SampleAnalyte", created.getSampleAnalyte());
    
    // name generators get called after initial save
    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).update(updatedCapture.capture());
    Sample updated = updatedCapture.getValue();
    assertEquals("name should be generated", expectedName, updated.getName());
    assertEquals("alias should not be generated", expectedAlias, updated.getAlias());
    assertNull("identificationBarcode should not be generated", updated.getIdentificationBarcode());
    assertNull("plain sample still should not contain SampleAdditionalInfo", updated.getSampleAdditionalInfo());
    assertNull("plain sample still should not contain Identity", updated.getIdentity());
    assertNull("plain sample still should not contain SampleTissue", updated.getSampleTissue());
    assertNull("plain sample still should not contain SampleAnalyte", updated.getSampleAnalyte());
  }
  
  @Test
  public void testPlainSampleAliasGeneration() throws Exception {
    Sample sample = new SampleImpl();
    String expectedAlias = "generated_alias";
    Mockito.when(sampleNamingScheme.hasGeneratorFor(Mockito.eq("alias"))).thenReturn(true);
    Mockito.when(sampleNamingScheme.generateNameFor(Mockito.eq("alias"), (Sample) Mockito.any())).thenReturn(expectedAlias);
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
    Sample parent = makeParentWithLookup();
    Sample child = makeChild();
    child.setIdentity(new IdentityImpl());
    child.getIdentity().setSampleId(parent.getId());
    
    Long newId = 89L;
    Sample postSave = makeChild();
    postSave.setId(newId);
    postSave.getSampleAdditionalInfo().setParent(parent);
    
    Mockito.when(sampleDao.addSample(Mockito.any(Sample.class))).thenReturn(newId);
    Mockito.when(sampleDao.getSample(newId)).thenReturn(postSave);
    mockValidRelationship(parent.getSampleAdditionalInfo().getSampleClass(), child.getSampleAdditionalInfo().getSampleClass());
    
    sut.create(child);
    
    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).addSample(createdCapture.capture());
    Sample created = createdCapture.getValue();
    assertNotNull("Detailed sample should have SampleAdditionalInfo", created.getSampleAdditionalInfo());
    assertNotNull("Detailed sample should have SampleTissue", created.getSampleTissue());
    assertNull("Child sample should not have Identity", created.getIdentity());
    assertNotNull("Child sample should have parent", created.getSampleAdditionalInfo().getParent());
    assertEquals("Unexpected parent ID", parent.getId(), created.getSampleAdditionalInfo().getParent().getId());
  }
  
  @Test
  public void testCreateDetailedSampleExistingParentByExternalName() throws Exception {
    Sample parent = makeParentWithLookup();
    Sample child = makeChild();
    child.setIdentity(new IdentityImpl());
    child.getIdentity().setExternalName(parent.getIdentity().getExternalName());
    
    Long newId = 89L;
    Sample postSave = makeChild();
    postSave.setId(newId);
    postSave.getSampleAdditionalInfo().setParent(parent);
    
    Mockito.when(sampleDao.addSample(Mockito.any(Sample.class))).thenReturn(newId);
    Mockito.when(sampleDao.getSample(newId)).thenReturn(postSave);
    mockValidRelationship(parent.getSampleAdditionalInfo().getSampleClass(), child.getSampleAdditionalInfo().getSampleClass());
    
    sut.create(child);
    
    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao).addSample(createdCapture.capture());
    Sample created = createdCapture.getValue();
    assertNotNull("Detailed sample should have SampleAdditionalInfo", created.getSampleAdditionalInfo());
    assertNotNull("Detailed sample should have SampleTissue", created.getSampleTissue());
    assertNull("Child sample should not have Identity", created.getIdentity());
    assertNotNull("Child sample should have parent", created.getSampleAdditionalInfo().getParent());
    assertEquals("Unexpected parent ID", parent.getId(), created.getSampleAdditionalInfo().getParent().getId());
  }
  
  @Test
  public void testCreateDetailedSampleMissingIdentity() throws Exception {
    Sample sample = new SampleImpl();
    sample.setSampleAdditionalInfo(new SampleAdditionalInfoImpl());
    sample.getSampleAdditionalInfo().setSampleClass(new SampleClassImpl());
    sample.getSampleAdditionalInfo().getSampleClass().setSampleCategory(SampleTissue.CATEGORY_NAME);
    exception.expect(IllegalArgumentException.class);
    sut.create(sample);
  }
  
  @Test
  public void testCreateDetailedSampleNoParent() throws Exception {
    Sample sample = makeChild();
    sample.setIdentity(new IdentityImpl());
    sample.getIdentity().setExternalName("non-existing");
    mockShellProjectWithRealLookup(sample);
    mockUser();
    
    // because of mocked dao, we can't actually continue with the same parent sample that should be created, but the partial 
    // parent sample that gets created is caught and examined below 
    Sample parent = makeParentWithLookup();
    Mockito.when(sampleDao.addSample(Mockito.any(Sample.class))).thenReturn(parent.getId());
    mockValidRelationship(parent.getSampleAdditionalInfo().getSampleClass(), sample.getSampleAdditionalInfo().getSampleClass());
    
    Long newId = 31L;
    Mockito.when(sampleDao.addSample(sample)).thenReturn(newId);
    Mockito.when(sampleDao.getSample(newId)).thenReturn(sample);
    
    sut.create(sample);
    
    ArgumentCaptor<Sample> createdCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao, Mockito.times(2)).addSample(createdCapture.capture());
    Sample partialParent = createdCapture.getAllValues().get(0);
    assertNotNull("Parent sample should have SampleAdditionalInfo", partialParent.getSampleAdditionalInfo());
    assertNotNull("Parent sample should have Identity", partialParent.getIdentity());
    
    Sample partialChild = createdCapture.getAllValues().get(1);
    assertNotNull("Detailed sample should have SampleAdditionalInfo", partialChild.getSampleAdditionalInfo());
    assertNotNull("Detailed sample should have SampleTissue", partialChild.getSampleTissue());
    assertNull("Child sample should not have Identity", partialChild.getIdentity());
    
    ArgumentCaptor<Sample> updatedCapture = ArgumentCaptor.forClass(Sample.class);
    Mockito.verify(sampleDao, Mockito.times(2)).update(updatedCapture.capture());
    // note: finalParent is not actually derived from partialParent because of mocked dao (above), but it should be the parent 
    // linked to finalChild
    Sample finalParent = updatedCapture.getAllValues().get(0); 
    Sample finalChild = updatedCapture.getAllValues().get(1);
    assertNotNull("Child sample should have parent", finalChild.getSampleAdditionalInfo().getParent());
    assertEquals("Unexpected parent ID", finalChild.getSampleAdditionalInfo().getParent().getId(), finalParent.getId());
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
  
  private Sample makePlainSample() {
    Sample sample = new SampleImpl();
    sample.setId(77L);
    sample.setAlias("alias");
    sample.setName("name");
    return sample;
  }
  
  private Sample makeParentWithLookup() throws IOException {
    Sample sample = makeParent();
    Mockito.when(sampleDao.getSample(sample.getId())).thenReturn(sample);
    Mockito.when(identityService.get(sample.getIdentity().getExternalName())).thenReturn(sample.getIdentity());
    Mockito.when(sampleClassDao.listByCategory(Mockito.eq(Identity.CATEGORY_NAME)))
        .thenReturn(Lists.newArrayList(sample.getSampleAdditionalInfo().getSampleClass()));
    return sample;
  }
  
  private Sample makeParent() {
    Sample sample = new SampleImpl();
    sample.setId(63L);
    sample.setSampleAdditionalInfo(new SampleAdditionalInfoImpl());
    sample.getSampleAdditionalInfo().setSampleClass(new SampleClassImpl());
    sample.getSampleAdditionalInfo().getSampleClass().setId(51L);
    sample.getSampleAdditionalInfo().getSampleClass().setAlias("parent");
    sample.getSampleAdditionalInfo().getSampleClass().setSampleCategory(Identity.CATEGORY_NAME);
    Identity identity = new IdentityImpl();
    identity.setSampleId(sample.getId());
    identity.setExternalName("external");
    sample.setIdentity(identity);
    return sample;
  }
  
  private Sample makeChild() {
    Sample sample = new SampleImpl();
    sample.setSampleType("type");
    sample.setScientificName("scientific");
    sample.setSampleAdditionalInfo(new SampleAdditionalInfoImpl());
    sample.getSampleAdditionalInfo().setSampleClass(new SampleClassImpl());
    sample.getSampleAdditionalInfo().getSampleClass().setId(10L);
    sample.getSampleAdditionalInfo().getSampleClass().setAlias("child");
    sample.getSampleAdditionalInfo().getSampleClass().setSampleCategory(SampleTissue.CATEGORY_NAME);
    sample.setSampleTissue(new SampleTissueImpl());
    Mockito.when(sampleClassDao.getSampleClass(sample.getSampleAdditionalInfo().getSampleClass().getId()))
        .thenReturn(sample.getSampleAdditionalInfo().getSampleClass());
    return sample;
  }
  
  private void mockValidRelationship(SampleClass parentClass, SampleClass childClass) throws IOException {
    Set<SampleValidRelationship> relationships = new HashSet<>();
    SampleValidRelationship rel = new SampleValidRelationshipImpl();
    rel.setParent(parentClass);
    rel.setChild(childClass);
    relationships.add(rel);
    Mockito.when(sampleValidRelationshipService.getAll()).thenReturn(relationships);
  }
  
  /**
   * Adds a shell project to the provided Sample, and adds the real project to the mocked projectStore. The 
   * projectStore should be queried to swap in the persisted object in place of the shell
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
