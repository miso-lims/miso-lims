package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;

import com.eaglegenomics.simlims.core.Note;

public interface NoteService<T> extends ProviderService<T> {

  public void addNote(T entity, Note note) throws IOException;

  public void deleteNote(T entity, Long noteId) throws IOException;
}
