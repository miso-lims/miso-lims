package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;

public interface NoteService<T extends Identifiable> extends ProviderService<T> {

  public void addNote(T entity, Note note) throws IOException;

  public void deleteNote(T entity, Long noteId) throws IOException;
}
