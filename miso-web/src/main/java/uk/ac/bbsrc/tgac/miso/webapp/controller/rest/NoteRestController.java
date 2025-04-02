package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

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

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.NoteService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;


@Controller
@RequestMapping("/rest/notes")
public class NoteRestController extends AbstractRestController {
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

  @Autowired
  private LibraryService libraryService;
  @Autowired
  private PoolService poolService;
  @Autowired
  private RunService runService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private RequisitionService requisitionService;
  @Autowired
  private WorksetService worksetService;

  private <T extends Identifiable> void addNote(NoteService<T> service, long entityId, NoteRequest request)
      throws IOException {
    T entity = service.get(entityId);
    Note note = new Note();

    note.setInternalOnly(request.isInternalOnly());
    note.setText(request.getText());
    note.setCreationDate(LocalDate.now(ZoneId.systemDefault()));
    service.addNote(entity, note);
  }

  @PostMapping(value = "/{entityType}/{entityId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void addNote(@PathVariable(name = "entityType") String entityType,
      @PathVariable(name = "entityId") long entityId,
      @RequestBody NoteRequest request) throws IOException {
    addNote(serviceForEntityType(entityType), entityId, request);
  }

  private <T extends Identifiable> void deleteNote(NoteService<T> service, long entityId, String entityType,
      long noteId)
      throws IOException {
    T entity = getEntity(entityId, entityType, service, Status.NOT_FOUND);
    service.deleteNote(entity, noteId);
  }

  private <T extends Identifiable> void deleteNotes(NoteService<T> service, BulkDeleteRequest request)
      throws IOException {
    T entity = getEntity(request.entityId(), request.entityType(), service, Status.BAD_REQUEST);
    service.deleteNotes(entity, request.noteIds());
  }

  private <T extends Identifiable> T getEntity(long entityId, String entityType, NoteService<T> service,
      Status notFoundErrorType)
      throws IOException {
    T entity = service.get(entityId);
    if (entity == null) {
      throw new RestException(entityType + " not found", notFoundErrorType);
    }
    return entity;
  }

  @DeleteMapping(value = "/{entityType}/{entityId}/{noteId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteNote(@PathVariable(name = "entityType") String entityType,
      @PathVariable(name = "entityId") long entityId,
      @PathVariable(name = "noteId") long noteId) throws IOException {
    deleteNote(serviceForEntityType(entityType), entityId, entityType, noteId);
  }

  public record BulkDeleteRequest(String entityType, long entityId, List<Long> noteIds) {
  }

  @PostMapping("/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDeleteNotes(@RequestBody BulkDeleteRequest request) throws IOException {
    NoteService<? extends Identifiable> service = serviceForEntityType(request.entityType());
    deleteNotes(service, request);
  }

  private NoteService<? extends Identifiable> serviceForEntityType(String entityType) {
    switch (entityType.toLowerCase()) {
      case "sample":
        return sampleService;
      case "library":
        return libraryService;
      case "pool":
        return poolService;
      case "run":
        return runService;
      case "requisition":
        return requisitionService;
      case "workset":
        return worksetService;
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

  public void setRequisitionService(RequisitionService requisitionService) {
    this.requisitionService = requisitionService;
  }

  public void setWorksetService(WorksetService worksetService) {
    this.worksetService = worksetService;
  }
}
