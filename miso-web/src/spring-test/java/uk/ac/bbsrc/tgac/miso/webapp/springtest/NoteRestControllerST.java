package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import org.springframework.web.servlet.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import com.eaglegenomics.simlims.core.Note;
import uk.ac.bbsrc.tgac.miso.dto.NoteDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.MetricCategory;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import java.util.Collections;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.NoteRestController.NoteRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.NoteRestController.BulkDeleteRequest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class NoteRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/notes";
  private static final Class<Note> controllerClass = Note.class;

  @Test
  public void testAddNote() throws Exception {
    NoteRequest req = new NoteRequest();
    req.setInternalOnly(false);
    req.setText("new note");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/Sample/1").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    SampleImpl sam = currentSession().get(SampleImpl.class, 1);
    assertEquals(1, sam.getNotes().size());
    assertEquals(req.getText(), sam.getNotes().iterator().next().getText());

  }

  @Test
  public void testDeleteNote() throws Exception {
    LibraryImpl lib = currentSession().get(LibraryImpl.class, 110005);
    int sizeBefore = lib.getNotes().size();

    getMockMvc().perform(delete(CONTROLLER_BASE + "/Library/110005/1")).andExpect(status().isNoContent());

    lib = currentSession().get(LibraryImpl.class, 110005);
    int sizeAfter = lib.getNotes().size();
    assertEquals(sizeBefore - 1, sizeAfter);
    assertFalse(lib.getNotes().stream().anyMatch(x -> x.getId() == 1));

  }

  @Test
  public void testBulkDeleteNote() throws Exception {
    BulkDeleteRequest req = new BulkDeleteRequest("Library", 110005L, Arrays.asList(1L, 3L));

    LibraryImpl lib = currentSession().get(LibraryImpl.class, 110005);
    assertTrue(lib.getNotes().stream().anyMatch(x -> x.getId() == 1));
    assertTrue(lib.getNotes().stream().anyMatch(x -> x.getId() == 3));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    lib = currentSession().get(LibraryImpl.class, 110005);
    assertTrue(lib.getNotes().isEmpty());
  }

}
