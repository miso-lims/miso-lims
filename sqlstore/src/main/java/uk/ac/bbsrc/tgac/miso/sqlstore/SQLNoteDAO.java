/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.store.SecurityStore;

import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.store.NoteStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class SQLNoteDAO implements NoteStore {
  private static final String TABLE_NAME = "Note";

  public static final String NOTES_SELECT = "SELECT noteId, creationDate, internalOnly, text, owner_userId " + "FROM " + TABLE_NAME;

  public static final String NOTE_SELECT_BY_ID = NOTES_SELECT + " " + "WHERE noteId = ?";

  public static final String NOTES_BY_RELATED_PROJECT_OVERVIEW = "SELECT n.noteId, n.creationDate, n.internalOnly, n.text, n.owner_userId "
      + "FROM " + TABLE_NAME + " n, ProjectOverview_Note pon " + "WHERE n.noteId=pon.notes_noteId " + "AND pon.overview_overviewId=?";

  public static final String NOTES_BY_RELATED_SAMPLE = "SELECT n.noteId, n.creationDate, n.internalOnly, n.text, n.owner_userId " + "FROM "
      + TABLE_NAME + " n, Sample_Note sn " + "WHERE n.noteId=sn.notes_noteId " + "AND sn.sample_sampleId=?";

  public static final String NOTES_BY_RELATED_LIBRARY = "SELECT n.noteId, n.creationDate, n.internalOnly, n.text, n.owner_userId " + "FROM "
      + TABLE_NAME + " n, Library_Note ln " + "WHERE n.noteId=ln.notes_noteId " + "AND ln.library_libraryId=?";

  public static final String NOTES_BY_RELATED_KIT = "SELECT n.noteId, n.creationDate, n.internalOnly, n.text, n.owner_userId " + "FROM "
      + TABLE_NAME + " n, Kit_Note kn " + "WHERE n.noteId=kn.notes_noteId " + "AND kn.kit_kitId=?";

  public static final String NOTES_BY_RELATED_RUN = "SELECT n.noteId, n.creationDate, n.internalOnly, n.text, n.owner_userId " + "FROM "
      + TABLE_NAME + " n, Run_Note rn " + "WHERE n.noteId=rn.notes_noteId " + "AND rn.run_runId=?";

  public static final String NOTE_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE noteId=:noteId";

  protected static final Logger log = LoggerFactory.getLogger(SQLNoteDAO.class);
  private SecurityStore securityDAO;
  private JdbcTemplate template;

  @Autowired
  private DataObjectFactory dataObjectFactory;

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setSecurityDAO(SecurityStore securityDAO) {
    this.securityDAO = securityDAO;
  }

  public JdbcTemplate getJdbcTemplate() {
    return template;
  }

  public void setJdbcTemplate(JdbcTemplate template) {
    this.template = template;
  }

  @Override
  public long save(Note note) throws IOException {
    // make links clickable
    String newNoteText = LimsUtils.findHyperlinks(note.getText());

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("creationDate", note.getCreationDate());
    params.addValue("internalOnly", note.isInternalOnly());
    params.addValue("text", newNoteText);

    if (note.getOwner() == null) {
      log.warn("Note has no owner - check parent permissions.");
    } else {
      params.addValue("owner_userId", note.getOwner().getUserId());
    }

    if (note.getNoteId() == Note.UNSAVED_ID) {
      SimpleJdbcInsert insert = new SimpleJdbcInsert(template).withTableName(TABLE_NAME).usingGeneratedKeyColumns("noteId");
      Number newId = insert.executeAndReturnKey(params);
      note.setNoteId(newId.longValue());
    }
    return note.getNoteId();
  }

  @Override
  public long saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    long noteId = save(note);
    SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("ProjectOverview_Note");

    MapSqlParameterSource poParams = new MapSqlParameterSource();
    poParams.addValue("overview_overviewId", overview.getOverviewId());
    poParams.addValue("notes_noteId", noteId);

    try {
      pInsert.execute(poParams);
    } catch (DuplicateKeyException se) {
      // ignore
    }
    return note.getNoteId();
  }

  @Override
  public long saveKitNote(Kit kit, Note note) throws IOException {
    long noteId = save(note);
    SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("Kit_Note");

    MapSqlParameterSource poParams = new MapSqlParameterSource();
    poParams.addValue("kit_kitId", kit.getId());
    poParams.addValue("notes_noteId", noteId);
    try {
      pInsert.execute(poParams);
    } catch (DuplicateKeyException se) {
      // ignore
    }
    return note.getNoteId();
  }

  @Override
  public long saveSampleNote(Sample sample, Note note) throws IOException {
    long noteId = save(note);
    SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("Sample_Note");

    MapSqlParameterSource poParams = new MapSqlParameterSource();
    poParams.addValue("sample_sampleId", sample.getId());
    poParams.addValue("notes_noteId", noteId);

    try {
      pInsert.execute(poParams);
    } catch (DuplicateKeyException se) {
      // ignore
    }
    return note.getNoteId();
  }

  @Override
  public long saveLibraryNote(Library library, Note note) throws IOException {
    long noteId = save(note);
    SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("Library_Note");

    MapSqlParameterSource poParams = new MapSqlParameterSource();
    poParams.addValue("library_libraryId", library.getId());
    poParams.addValue("notes_noteId", noteId);

    try {
      pInsert.execute(poParams);
    } catch (DuplicateKeyException se) {
      // ignore
    }
    return note.getNoteId();
  }

  @Override
  public long saveRunNote(Run run, Note note) throws IOException {
    long noteId = save(note);
    SimpleJdbcInsert pInsert = new SimpleJdbcInsert(template).withTableName("Run_Note");

    MapSqlParameterSource poParams = new MapSqlParameterSource();
    poParams.addValue("run_runId", run.getId());
    poParams.addValue("notes_noteId", noteId);

    try {
      pInsert.execute(poParams);
    } catch (DuplicateKeyException se) {
      // ignore
    }
    return note.getNoteId();
  }

  @Override
  public boolean remove(Note note) throws IOException {
    NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
    return (namedTemplate.update(NOTE_DELETE, new MapSqlParameterSource().addValue("noteId", note.getNoteId())) == 1);
  }

  @Override
  public Note get(long noteId) throws IOException {
    List eResults = template.query(NOTE_SELECT_BY_ID, new Object[] { noteId }, new NoteMapper());
    Note e = eResults.size() > 0 ? (Note) eResults.get(0) : null;
    return e;
  }

  @Override
  public Note lazyGet(long id) throws IOException {
    return get(id);
  }

  @Override
  public Collection<Note> listAll() throws IOException {
    return template.query(NOTES_SELECT, new NoteMapper());
  }

  @Override
  public int count() throws IOException {
    return template.queryForInt("SELECT count(*) FROM " + TABLE_NAME);
  }

  @Override
  public List<Note> listByProjectOverview(Long overviewId) throws IOException {
    return template.query(NOTES_BY_RELATED_PROJECT_OVERVIEW, new Object[] { overviewId }, new NoteMapper());
  }

  @Override
  public List<Note> listByKit(Long kitId) throws IOException {
    return template.query(NOTES_BY_RELATED_KIT, new Object[] { kitId }, new NoteMapper());
  }

  @Override
  public List<Note> listBySample(Long sampleId) throws IOException {
    return template.query(NOTES_BY_RELATED_SAMPLE, new Object[] { sampleId }, new NoteMapper());
  }

  @Override
  public List<Note> listByLibrary(Long libraryId) throws IOException {
    return template.query(NOTES_BY_RELATED_LIBRARY, new Object[] { libraryId }, new NoteMapper());
  }

  @Override
  public List<Note> listByRun(Long runId) throws IOException {
    return template.query(NOTES_BY_RELATED_RUN, new Object[] { runId }, new NoteMapper());
  }

  public class NoteMapper implements RowMapper<Note> {
    @Override
    public Note mapRow(ResultSet rs, int rowNum) throws SQLException {
      Note note = new Note();
      note.setNoteId(rs.getLong("noteId"));
      note.setCreationDate(rs.getDate("creationDate"));
      note.setInternalOnly(rs.getBoolean("internalOnly"));
      note.setText(rs.getString("text"));

      try {
        note.setOwner(securityDAO.getUserById(rs.getLong("owner_userId")));
      } catch (IOException e) {
        log.error("note row mapper", e);
      }

      return note;
    }
  }
}
