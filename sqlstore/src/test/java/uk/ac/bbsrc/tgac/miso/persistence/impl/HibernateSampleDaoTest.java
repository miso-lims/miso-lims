package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleAdditionalInfoImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultSampleNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;

public class HibernateSampleDaoTest extends AbstractDAOTest {

  @Autowired
  private SessionFactory sessionFactory;
  
  @Mock
  private SecurityStore securityDAO;
  @Mock
  private Store<SecurityProfile> securityProfileDAO;
  @Mock
  private ChangeLogStore changeLogDAO;
  @Mock
  private ProjectStore projectStore;
  @Mock
  private LibraryStore libraryStore;
  @Mock
  private SampleQcStore sampleQCStore;
  @Mock
  private NoteStore noteStore;

  @InjectMocks
  private HibernateSampleDao sut;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    sut.setNamingScheme(new DefaultSampleNamingScheme());
    sut.setSessionFactory(sessionFactory);
  }

  @Test
  public void parentNameNotModifiedWhenParentNullTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setSampleAdditionalInfo(new SampleAdditionalInfoImpl());
    sut.updateParentSampleNameIfRequired(sample);
    assertNull("Null parent will remain null. No need to udate sample name.", sample.getParent());
  }

  @Test
  public void parentNameNotModifiedWhenParentNameNotTemporaryTest() throws Exception {
    Sample sample = new SampleImpl();
    Sample parent = new SampleImpl();
    sample.setSampleAdditionalInfo(new SampleAdditionalInfoImpl());
    sample.getSampleAdditionalInfo().setParent(parent);
    String nonTemporaryName = "RealSampleName";
    parent.setName(nonTemporaryName);
    sut.updateParentSampleNameIfRequired(sample);
    assertThat("Parent has a non-temporary name. No need to udate sample name.", sample.getParent().getName(), is(nonTemporaryName));
  }

  @Test
  public void parentNameNotModifiedWhenParentIdNotSetTest() throws Exception {
    Sample sample = new SampleImpl();
    Sample parent = new SampleImpl();
    sample.setSampleAdditionalInfo(new SampleAdditionalInfoImpl());
    sample.getSampleAdditionalInfo().setParent(parent);
    String temporaryName = HibernateSampleDao.generateTemporaryName();
    parent.setName(temporaryName);
    parent.setId(Sample.UNSAVED_ID);
    sut.updateParentSampleNameIfRequired(sample);
    assertThat("Parent has not been assigned an id. No need to udate sample name.", sample.getParent().getName(), is(temporaryName));
  }

  @Test
  public void parentNameModifiedTest() throws Exception {
    Sample sample = new SampleImpl();
    Sample parent = new SampleImpl();
    sample.setSampleAdditionalInfo(new SampleAdditionalInfoImpl());
    sample.getSampleAdditionalInfo().setParent(parent);
    String temporaryName = HibernateSampleDao.generateTemporaryName();
    parent.setName(temporaryName);
    parent.setId(42);
    sut.updateParentSampleNameIfRequired(sample);
    assertThat("Parent has temporary name and an id. Update sample name.", sample.getParent().getName(), is("SAM42"));
  }

  @Test
  public void temporarySampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName(HibernateSampleDao.generateTemporaryName());
    assertTrue("Temporary sample names must return true.", HibernateSampleDao.hasTemporaryName(sample));
  }

  @Test
  public void notTemporarySampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName("RealSampleName");
    assertFalse("Real sample names must return false.", HibernateSampleDao.hasTemporaryName(sample));
  }

  @Test
  public void nullSampleNameTest() throws Exception {
    Sample sample = new SampleImpl();
    sample.setName(null);
    assertFalse("Non-temporary sample names must return false.", HibernateSampleDao.hasTemporaryName(sample));
  }

  @Test
  public void nullSampleObjectNameTest() throws Exception {
    Sample sample = null;
    assertFalse("A null sample object does not contain a temporary name so must return false.",
        HibernateSampleDao.hasTemporaryName(sample));
  }
  
  @Test
  public void getSampleWithChildrenTest() throws Exception {
    Sample sample = sut.get(15L);
    assertNotNull(sample.getChildren());
    assertEquals(2,sample.getChildren().size());
    for (@SuppressWarnings("unused") Sample child : sample.getChildren()) {
      // will throw ClassCastException if children are not correctly loaded as Samples
    }
  }
  
  @Test
  public void getSampleWithParentTest() throws Exception {
    SampleImpl sample = (SampleImpl) sut.get(16L);
    assertNotNull(sample);
    assertNotNull(sample.getSampleAdditionalInfo());
    assertNotNull(sample.getParent());
    assertEquals(15L, sample.getParent().getId());
  }

}
