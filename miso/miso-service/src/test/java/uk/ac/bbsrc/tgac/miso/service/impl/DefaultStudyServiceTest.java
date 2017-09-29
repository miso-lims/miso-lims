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
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.ProjectStore;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationManager;

public class DefaultStudyServiceTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  @Mock
  private AuthorizationManager authorizationManager;

  @Mock
  private StudyStore studyStore;

  @Mock
  private ProjectStore projectStore;

  @Mock
  private NamingScheme namingScheme;

  @InjectMocks
  private DefaultStudyService sut;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#saveStudy(uk.ac.bbsrc.tgac.miso.core.data.Study)}
   * .
   */
  @Test
  public void testSaveStudy() throws IOException {
    StudyType st = new StudyType();
    st.setId(2L);
    st.setName("Test");
    when(studyStore.getType(2L)).thenReturn(st);

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
    when(studyStore.save(db)).thenReturn(1L);

    assertEquals(1L, sut.save(s));
    Mockito.verify(authorizationManager).throwIfNotWritable(db);
    assertEquals(s.getDescription(), db.getDescription());
    assertNotEquals(s.getName(), db.getName());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.core.manager.UserAuthMisoRequestManager#getStudyById(long)} .
   */
  @Test
  public void testGetStudyByIdThrows() throws IOException {
    Study db = new StudyImpl();
    db.setId(1L);
    db.setName("b");
    db.setDescription("mt");
    when(studyStore.get(1L)).thenReturn(db);

    assertEquals(db, sut.get(1L));
    Mockito.verify(authorizationManager).throwIfNotReadable(db);
  }
}
