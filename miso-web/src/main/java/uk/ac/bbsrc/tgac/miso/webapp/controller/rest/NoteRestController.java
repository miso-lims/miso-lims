/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Date;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eaglegenomics.simlims.core.Note;

import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.NoteService;
import uk.ac.bbsrc.tgac.miso.service.PoolService;
import uk.ac.bbsrc.tgac.miso.service.RunService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;

@Controller
@RequestMapping("/rest/notes")
public class NoteRestController extends RestController {
  public static class NoteRequest {
    private boolean internalOnly;
    private String text;

    public String getText() {
      return text;
    }

    public boolean isInternalOnly() {
      return internalOnly;
    }

    public void setInternalOnly(boolean internalOnly) {
      this.internalOnly = internalOnly;
    }

    public void setText(String text) {
      this.text = text;
    }

  }

  protected static final Logger log = LoggerFactory.getLogger(NoteRestController.class);

  @Autowired
  private LibraryService libraryService;

  @Autowired
  private PoolService poolService;

  @Autowired
  private RunService runService;

  @Autowired
  private SampleService sampleService;

  private <T extends Identifiable> void addNote(NoteService<T> service, long entityId, NoteRequest request) throws IOException {
    T entity = service.get(entityId);
    Note note = new Note();

    note.setInternalOnly(request.isInternalOnly());
    note.setText(request.getText());
    note.setCreationDate(new Date());
    service.addNote(entity, note);
  }

  @PostMapping(value = "/{entityType}/{entityId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addNote(@PathVariable(name = "entityType") String entityType, @PathVariable(name = "entityId") long entityId,
      @RequestBody NoteRequest request) throws IOException {
    addNote(serviceForEntityType(entityType), entityId, request);
  }

  private <T extends Identifiable> void deleteNote(NoteService<T> service, long entityId, long noteId) throws IOException {
    T entity = service.get(entityId);
    service.deleteNote(entity, noteId);

  }

  @DeleteMapping(value = "/{entityType}/{entityId}/{noteId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteNote(@PathVariable(name = "entityType") String entityType, @PathVariable(name = "entityId") long entityId,
      @PathVariable(name = "noteId") long noteId) throws IOException {
    deleteNote(serviceForEntityType(entityType), entityId, noteId);
  }

  private NoteService<?> serviceForEntityType(String entityType) {
    switch (entityType) {
    case "sample":
      return sampleService;
    case "library":
      return libraryService;
    case "pool":
      return poolService;
    case "run":
      return runService;
    default:
      throw new RestException("Unknown entity type: " + entityType, Status.NOT_FOUND);
    }
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setPoolService(PoolService poolService) {
    this.poolService = poolService;
  }

  public void setRunService(RunService runService) {
    this.runService = runService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }
}
