package uk.ac.bbsrc.tgac.miso.service.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingSchemeHolder;
import uk.ac.bbsrc.tgac.miso.persistence.ProjectStore;
import uk.ac.bbsrc.tgac.miso.persistence.StudyStore;
import uk.ac.bbsrc.tgac.miso.persistence.StudyTypeDao;

public class DefaultStudyServiceTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Mock
  private AuthorizationManager authorizationManager;
  @Mock
  private StudyStore studyStore;
  @Mock
  private StudyTypeDao studyTypeDao;
  @Mock
  private ProjectStore projectStore;
  @Mock
  private NamingSchemeHolder namingSchemeHolder;
  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private DefaultStudyService sut;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(namingSchemeHolder.getPrimary()).thenReturn(namingScheme);
  }

  @Test
  public void testSaveStudy() throws IOException {
    StudyType st = new StudyType();
    st.setId(2L);
    st.setName("Test");
    when(studyTypeDao.get(2L)).thenReturn(st);

    Study s = new StudyImpl();
    s.setId(1L);
    s.setName("a");
    s.setDescription("desc");
    s.setStudyType(st);

    Study db = new StudyImpl();
    db.setId(1L);
    db.setName("b");
    db.setDescription("mt");
    s.setStudyType(st);
    when(studyStore.get(1L)).thenReturn(db);
    when(studyStore.update(db)).thenReturn(1L);

    assertEquals(1L, sut.update(s));
    assertEquals(s.getDescription(), db.getDescription());
    assertNotEquals(s.getName(), db.getName());
  }

  @Test
  public void testGetStudyByIdThrows() throws IOException {
    Study db = new StudyImpl();
    db.setId(1L);
    db.setName("b");
    db.setDescription("mt");
    when(studyStore.get(1L)).thenReturn(db);

    assertEquals(db, sut.get(1L));
  }
}
