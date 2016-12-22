package uk.ac.bbsrc.tgac.miso.sqlstore;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.AbstractDAOTest;
import uk.ac.bbsrc.tgac.miso.core.data.KitComponent;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitComponentImpl;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.factory.TgacDataObjectFactory;

public class SQLNoteDAOTest extends AbstractDAOTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @InjectMocks
  private SQLNoteDAO dao;

  @Mock
  private SQLSecurityDAO securityDAO;

  @Autowired
  @Spy
  private JdbcTemplate jdbcTemplate;

  @Before
  public void setup() throws IOException, MisoNamingException {
    MockitoAnnotations.initMocks(this);
    dao.setJdbcTemplate(jdbcTemplate);
    dao.setDataObjectFactory(new TgacDataObjectFactory());
  }

  @Test
  public void testSave() throws Exception {
    Note note = new Note();
    note.setCreationDate(new Date());
    note.setInternalOnly(true);
    note.setOwner(new UserImpl());
    note.setText("newnote");
    long save = dao.save(note);
    assertTrue(save > 0);
    Note retrievedNote = dao.get(save);
    assertEquals(note.getText(), retrievedNote.getText());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    assertEquals(df.print(note.getCreationDate(), Locale.CANADA), df.print(retrievedNote.getCreationDate(), Locale.CANADA));
  }

  @Test
  public void testSaveProjectOverviewNote() throws Exception {
    Note note = new Note();
    note.setText("projectOverviewNote");
    note.setCreationDate(new Date());
    note.setOwner(new UserImpl());
    note.setInternalOnly(false);
    ProjectOverview projectOverview = new ProjectOverview();
    long saveId = dao.saveProjectOverviewNote(projectOverview, note);

    assertTrue(saveId > 0);
    Note retrievedNote = dao.get(saveId);
    assertEquals(note.getText(), retrievedNote.getText());
    assertEquals(note.isInternalOnly(), retrievedNote.isInternalOnly());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    assertEquals(df.print(note.getCreationDate(), Locale.CANADA), df.print(retrievedNote.getCreationDate(), Locale.CANADA));
  }

  @Test
  public void testSaveKitNote() throws Exception {
    Note note = new Note();
    note.setText("kitenote");
    note.setCreationDate(new Date());
    note.setOwner(new UserImpl());
    note.setInternalOnly(true);
    KitComponent kit = new KitComponentImpl();
    long saveId = dao.saveKitNote(kit, note);

    assertTrue(saveId > 0);
    Note retrievedNote = dao.get(saveId);
    assertEquals(note.getText(), retrievedNote.getText());
    assertEquals(note.isInternalOnly(), retrievedNote.isInternalOnly());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    assertEquals(df.print(note.getCreationDate(), Locale.CANADA), df.print(retrievedNote.getCreationDate(), Locale.CANADA));
  }

  @Test
  public void testSaveSampleNote() throws Exception {
    Note note = new Note();
    note.setText("samplenote");
    note.setCreationDate(new Date());
    note.setOwner(new UserImpl());
    note.setInternalOnly(true);
    Sample sample = new SampleImpl();
    long saveId = dao.saveSampleNote(sample, note);

    assertTrue(saveId > 0);
    Note retrievedNote = dao.get(saveId);
    assertEquals(note.getText(), retrievedNote.getText());
    assertEquals(note.isInternalOnly(), retrievedNote.isInternalOnly());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    assertEquals(df.print(note.getCreationDate(), Locale.CANADA), df.print(retrievedNote.getCreationDate(), Locale.CANADA));
  }

  @Test
  public void testSaveLibraryNote() throws Exception {
    Note note = new Note();
    note.setText("librarynote");
    note.setCreationDate(new Date());
    note.setOwner(new UserImpl());
    note.setInternalOnly(false);
    Library library = new LibraryImpl();
    long saveId = dao.saveLibraryNote(library, note);

    assertTrue(saveId > 0);
    Note retrievedNote = dao.get(saveId);
    assertEquals(note.getText(), retrievedNote.getText());
    assertEquals(note.isInternalOnly(), retrievedNote.isInternalOnly());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    assertEquals(df.print(note.getCreationDate(), Locale.CANADA), df.print(retrievedNote.getCreationDate(), Locale.CANADA));

  }

  @Test
  public void testSaveRunNote() throws Exception {
    Note note = new Note();
    note.setText("runnote");
    note.setCreationDate(new Date());
    note.setOwner(new UserImpl());
    note.setInternalOnly(true);
    Run run = new RunImpl();
    long saveId = dao.saveRunNote(run, note);

    assertTrue(saveId > 0);
    Note retrievedNote = dao.get(saveId);
    assertEquals(note.getText(), retrievedNote.getText());
    assertEquals(note.isInternalOnly(), retrievedNote.isInternalOnly());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    assertEquals(df.print(note.getCreationDate(), Locale.CANADA), df.print(retrievedNote.getCreationDate(), Locale.CANADA));

  }

  @Test
  public void testSavePoolNote() throws Exception {
    Note note = new Note();
    note.setText("poolnote");
    note.setCreationDate(new Date());
    note.setOwner(new UserImpl());
    note.setInternalOnly(true);
    Pool pool = new PoolImpl();
    long saveId = dao.savePoolNote(pool, note);

    assertTrue(saveId > 0);
    Note retrievedNote = dao.get(saveId);
    assertEquals(note.getText(), retrievedNote.getText());
    assertEquals(note.isInternalOnly(), retrievedNote.isInternalOnly());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    assertEquals(df.print(note.getCreationDate(), Locale.CANADA), df.print(retrievedNote.getCreationDate(), Locale.CANADA));

  }

  @Test
  public void testRemove() throws Exception {
    Note note = dao.get(3);
    boolean remove = dao.remove(note);
    assertTrue(remove);
    assertNull(dao.get(3));
  }

  @Test
  public void testGet() throws Exception {
    Note note = dao.get(2);
    assertEquals(new Long(2),  note.getNoteId());
    assertEquals("second note",  note.getText());
    assertFalse(note.isInternalOnly());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    df.print(note.getCreationDate(), Locale.CANADA);
    assertEquals("2016-01-23", df.print(note.getCreationDate(), Locale.CANADA));
  }

  @Test
  public void testLazyGet() throws Exception {
    Note note = dao.get(3);
    assertEquals(new Long(3),  note.getNoteId());
    assertEquals("third note",  note.getText());
    assertFalse(note.isInternalOnly());
    DateFormatter df = new DateFormatter("yyyy-MM-dd");
    df.print(note.getCreationDate(), Locale.CANADA);
    assertEquals("2016-03-11", df.print(note.getCreationDate(), Locale.CANADA));
  }

  @Test
  public void testListAll() throws Exception {
    Collection<Note> notes = dao.listAll();
    assertEquals(3, notes.size());
  }

  @Test
  public void testCount() throws Exception {
    int count = dao.count();
    assertEquals(3, count);
  }

  @Test
  public void testListByProjectOverview() throws Exception {
    List<Note> notes = dao.listByProjectOverview(33l);
    assertEquals(1, notes.size());
    assertEquals(new Long(2), notes.iterator().next().getNoteId());
  }

  @Test
  public void testListByKit() throws Exception {
    List<Note> notes = dao.listByKit(33l);
    assertEquals(1, notes.size());
    assertEquals(new Long(1), notes.iterator().next().getNoteId());
  }

  @Test
  public void testListBySample() throws Exception {
    List<Note> notes = dao.listBySample(33l);
    assertEquals(1, notes.size());
    assertEquals(new Long(2), notes.iterator().next().getNoteId());
  }

  @Test
  public void testListByLibrary() throws Exception {
    List<Note> notes = dao.listByLibrary(33l);
    assertEquals(1, notes.size());
    assertEquals(new Long(3), notes.iterator().next().getNoteId());
  }

  @Test
  public void testListByRun() throws Exception {
    List<Note> notes = dao.listByRun(33l);
    assertEquals(1, notes.size());
    assertEquals(new Long(1), notes.iterator().next().getNoteId());
  }

  @Test
  public void testListByPool() throws Exception {
    List<Note> notes = dao.listByPool(33l);
    assertEquals(1, notes.size());
    assertEquals(new Long(2), notes.iterator().next().getNoteId());
  }
}
