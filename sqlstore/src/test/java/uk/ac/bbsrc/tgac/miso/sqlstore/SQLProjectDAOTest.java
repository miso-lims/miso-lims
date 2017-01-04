/**
 * 
 */
package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.naming.validation.ValidationResult;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryStore;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.store.RunStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleStore;
import uk.ac.bbsrc.tgac.miso.core.store.Store;
import uk.ac.bbsrc.tgac.miso.core.store.StudyStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.ReferenceGenomeDao;

/**
 * @author Chris Salt
 *
 */
public class SQLProjectDAOTest extends AbstractDAOTest {

  // Auto-increment sequence doesn't roll back with transactions, so must be
  // tracked
  private static long nextAutoIncrementId = 4L;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Autowired
  @Spy
  private TgacDataObjectFactory dataObjectFactory;

  @Mock
  private SampleStore sampleDAO;

  @Mock
  private StudyStore studyDAO;

  @Mock
  private com.eaglegenomics.simlims.core.manager.SecurityManager securityManager;
  @Mock
  private Authentication authentication;
  @Mock
  private Store<SecurityProfile> securityProfileDAO;
  @Mock
  private RunStore runDAO;
  @Mock
  private NoteStore noteDAO;

  @Mock
  private NamingScheme namingScheme;

  /*
   * @Mock private CacheManager cacheManager;
   */

  @Mock
  private LibraryStore libraryDAO;

  @Mock
  private ReferenceGenomeDao referenceGenomeDao;

  @InjectMocks
  private SQLProjectDAO projectDAO;

  // shared rules
  private final Authentication mockAuthentication = mock(Authentication.class);
  private final SecurityContext mockContext = mock(SecurityContext.class);

  // a project to save
  private final Project project = new ProjectImpl();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    DataObjectFactory dataObjectFactory = new TgacDataObjectFactory();
    projectDAO.setDataObjectFactory(dataObjectFactory);

    when(mockContext.getAuthentication()).thenReturn(mockAuthentication);
    when(mockAuthentication.getName()).thenReturn("some name");
    SecurityContextHolder.setContext(mockContext);
    when(securityProfileDAO.save(any(SecurityProfile.class))).thenReturn(1L);
    
    projectDAO.setReferenceGenomeDao(referenceGenomeDao);

    when(namingScheme.generateNameFor(Matchers.any(Project.class))).thenReturn("EDI123");
    when(namingScheme.validateName(Matchers.anyString())).thenReturn(ValidationResult.success());
    when(namingScheme.validateProjectShortName(Matchers.anyString())).thenReturn(ValidationResult.success());

    project.setProgress(ProgressType.ACTIVE);
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setId(1L);
    referenceGenome.setAlias("hg19");
    project.setReferenceGenome(referenceGenome);

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#save(uk.ac.bbsrc.tgac.miso.core.data.Project)} .
   */
  @Test
  public void testSave() throws Exception {
    long autoIncrementId = nextAutoIncrementId;
    mockAutoIncrement(autoIncrementId);
    final String testAlias = "test alias";
    project.setAlias(testAlias);

    long savedProjectId = projectDAO.save(project);
    nextAutoIncrementId += 1;

    Project savedProject = projectDAO.get(savedProjectId);
    assertEquals(testAlias, savedProject.getAlias());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#save(uk.ac.bbsrc.tgac.miso.core.data.Project)} .
   */

  @Test
  public void testSaveWithUnsavedSecurityProfile() throws Exception {
    long autoIncrementId = nextAutoIncrementId;
    mockAutoIncrement(autoIncrementId);
    project.getSecurityProfile().setProfileId(SecurityProfile.UNSAVED_ID);
    final String testAlias = "test alias";
    project.setAlias(testAlias);
    
    long savedProjectId = projectDAO.save(project);
    nextAutoIncrementId += 1;

    Project savedProject = projectDAO.get(savedProjectId);
    assertEquals(testAlias, savedProject.getAlias());
  }

  private void mockAutoIncrement(long value) {
    Map<String, Object> rs = new HashMap<>();
    rs.put("Auto_increment", value);
    Mockito.doReturn(rs).when(jdbcTemplate).queryForMap(Matchers.anyString());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#saveOverview(uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview)}
   * .
   * 
   * @throws IOException
   */
  @Ignore
  @Test
  public void testSaveOverview() throws IOException {
    // TODO: implement.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#listAll()}.
   */
  @Test
  public void testListAll() throws Exception {
    List<Project> projects = projectDAO.listAll();
    System.out.println(projects);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#listAllWithLimit(long)} .
   * 
   * @throws IOException
   */
  @Test
  public void testListAllWithLimit() throws IOException {
    List<Project> projects = projectDAO.listAllWithLimit(2L);
    assertEquals(2, projects.size());

  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#count()}.
   * 
   * @throws IOException
   */
  @Test
  public void testCount() throws IOException {
    int count = projectDAO.count();
    assertEquals(3, count);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#remove(uk.ac.bbsrc.tgac.miso.core.data.Project)} .
   * 
   * @throws IOException
   */
  @Ignore
  @Test
  public void testRemove() throws IOException {
    // TODO: Uses cache so ignoring test for now.
    List<Project> projects = projectDAO.listAll();
    assertEquals(3, projects.size());
    projectDAO.remove(projects.get(1));
    projects = projectDAO.listAll();
    assertEquals(2, projects.size());
  }

  /**
   * Test method for
   * {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#removeOverview(uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview)} .
   */
  @Ignore
  @Test
  public void testRemoveOverview() {
    // TODO: Uses cache so ignoring test for now.
    // TODO : Implement
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#get(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGet() throws IOException {
    Project p = projectDAO.get(1);
    assertNotNull(p);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#lazyGet(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testLazyGet() throws IOException {
    Project p = projectDAO.lazyGet(1);
    assertNotNull(p);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#listBySearch(java.lang.String)} .
   */
  @Ignore
  @Test
  public void testListBySearch() {
    // TODO: Delete this method.
    // It allows you to pass in hard mysql query string to
    // return a project. I have deprecated it.
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#getByAlias(java.lang.String)} .
   * 
   * @throws IOException
   */
  @Test
  public void testGetByAlias() throws IOException {
    String alias = projectDAO.listAll().get(1).getAlias();
    assertFalse(LimsUtils.isStringEmptyOrNull(alias));
    Project p = projectDAO.getByAlias(alias);
    assertNotNull(p);
    assertEquals(alias, p.getAlias());
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#getByStudyId(long)}.
   * 
   * @throws IOException
   */
  @Test
  public void testGetByStudyId() throws IOException {
    Project p = projectDAO.getByStudyId(1L);
    assertNotNull(p);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#getProjectOverviewById(long)} .
   * 
   * @throws IOException
   */
  @Test
  public void testGetProjectOverviewById() throws IOException {
    ProjectOverview po = projectDAO.getProjectOverviewById(1L);
    System.out.println(po);
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#lazyGetProjectOverviewById(long)} .
   */
  @Test
  public void testLazyGetProjectOverviewById() {
    // TODO : Implement
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#listOverviewsByProjectId(long)} .
   */
  @Test
  public void testListOverviewsByProjectId() {
    // TODO : Implement
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#listIssueKeysByProjectId(long)} .
   */
  @Test
  public void testListIssueKeysByProjectId() {
    // TODO : Implement
  }

  /**
   * Test method for {@link uk.ac.bbsrc.tgac.miso.sqlstore.SQLProjectDAO#getProjectColumnSizes()} .
   */
  @Test
  public void testGetProjectColumnSizes() {
    // TODO : Implement
  }

  @Test
  public void testNotes() throws Exception {
    long overviewId = 1L;
    String message = "test message";
    Note note = new Note();
    note.setText(message);

    ProjectOverview overview = projectDAO.getProjectOverviewById(overviewId);
    assertNotNull(overview);
    assertEquals(0, overview.getNotes().size());

    projectDAO.addNote(overview, note);
    ProjectOverview overviewWithNote = projectDAO.getProjectOverviewById(overviewId);
    assertEquals(1, overviewWithNote.getNotes().size());
    Note savedNote = overviewWithNote.getNotes().iterator().next();
    assertEquals(message, savedNote.getText());
    // TODO: uncomment after Hibernatized
    // Long noteId = savedNote.getNoteId();
    // assertNotNull(sessionFactory.getCurrentSession().get(Note.class, noteId));

    projectDAO.deleteNote(overviewWithNote, savedNote);
    ProjectOverview overviewNoteDeleted = projectDAO.getProjectOverviewById(overviewId);
    assertEquals(0, overviewNoteDeleted.getNotes().size());
    // assertNull(sessionFactory.getCurrentSession().get(Note.class, noteId));
  }

}
