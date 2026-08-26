package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.NoteRestController.NoteRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.NoteRestController.BulkDeleteRequest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;


public class NoteRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/notes";

  @Test
  public void testAddNote() throws Exception {
    NoteRequest req = new NoteRequest();
    req.setInternalOnly(false);
    req.setText("new note");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/Sample/1").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    SampleImpl sam = currentSession().find(SampleImpl.class, 1);
    assertEquals(1, sam.getNotes().size());
    assertEquals(req.getText(), sam.getNotes().iterator().next().getText());

  }

  @Test
  public void testDeleteNote() throws Exception {
    LibraryImpl lib = currentSession().find(LibraryImpl.class, 110005);
    int sizeBefore = lib.getNotes().size();

    getMockMvc().perform(delete(CONTROLLER_BASE + "/Library/110005/1")).andExpect(status().isNoContent());

    lib = currentSession().find(LibraryImpl.class, 110005);
    int sizeAfter = lib.getNotes().size();
    assertEquals(sizeBefore - 1, sizeAfter);
    assertFalse(lib.getNotes().stream().anyMatch(x -> x.getId() == 1));

  }

  @Test
  public void testBulkDeleteNote() throws Exception {
    BulkDeleteRequest req = new BulkDeleteRequest("Library", 110005L, Arrays.asList(1L, 3L));

    LibraryImpl lib = currentSession().find(LibraryImpl.class, 110005);
    assertTrue(lib.getNotes().stream().anyMatch(x -> x.getId() == 1));
    assertTrue(lib.getNotes().stream().anyMatch(x -> x.getId() == 3));
    assertEquals(2, lib.getNotes().size());

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    lib = currentSession().find(LibraryImpl.class, 110005);
    assertTrue(lib.getNotes().isEmpty());
  }

}
