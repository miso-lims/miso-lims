package uk.ac.bbsrc.tgac.miso.persistence.impl;

import static org.junit.Assert.*;

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
import uk.ac.bbsrc.tgac.miso.core.data.SampleAdditionalInfo;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.service.naming.DefaultSampleNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ChangeLogStore;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

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
  public void getSampleWithChildrenTest() throws Exception {
    Sample sample = sut.get(15L);
    assertTrue(LimsUtils.isDetailedSample(sample));
    SampleAdditionalInfo detailed = (SampleAdditionalInfo) sample;
    assertNotNull(detailed.getChildren());
    assertEquals(2, detailed.getChildren().size());
    for (@SuppressWarnings("unused") Sample child : detailed.getChildren()) {
      // will throw ClassCastException if children are not correctly loaded as Samples
    }
  }
  
  @Test
  public void getSampleWithParentTest() throws Exception {
    SampleImpl sample = (SampleImpl) sut.get(16L);
    assertNotNull(sample);
    assertTrue(LimsUtils.isDetailedSample(sample));
    SampleAdditionalInfo detailed = (SampleAdditionalInfo) sample;
    assertNotNull(detailed.getParent());
    assertEquals(15L, detailed.getParent().getId());
  }

}
