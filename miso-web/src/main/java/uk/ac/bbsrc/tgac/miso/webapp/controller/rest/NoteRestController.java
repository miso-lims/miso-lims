package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.eaglegenomics.simlims.core.Note;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.HierarchyEntity;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.NoteService;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;
import uk.ac.bbsrc.tgac.miso.core.service.RequisitionService;
import uk.ac.bbsrc.tgac.miso.core.service.RunService;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;
import uk.ac.bbsrc.tgac.miso.core.service.WorksetService;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.NoteDto;
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

  protected static final Logger log = LoggerFactory.getLogger(NoteRestController.class);

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

  @GetMapping("/{entityType}/{entityId}/dt")
  @ResponseBody
  public DataTablesResponseDto<NoteDto> getNotesForDataTable(
      @PathVariable String entityType,
      @PathVariable long entityId,
      @RequestParam(value = "sEcho", defaultValue = "1") Long sEcho,
      @RequestParam(value = "iDisplayStart", defaultValue = "0") Integer iDisplayStart,
      @RequestParam(value = "iDisplayLength", defaultValue = "10") Integer iDisplayLength,
      @RequestParam(value = "includeRelated", defaultValue = "true") boolean includeRelated) throws IOException {

    List<NoteDto> notes = getNotesList(entityType, entityId, includeRelated);

    DataTablesResponseDto<NoteDto> response = new DataTablesResponseDto<>();
    response.setSEcho(sEcho);
    response.setITotalRecords((long) notes.size());
    response.setITotalDisplayRecords((long) notes.size());

    if (iDisplayLength > 0 && iDisplayStart < notes.size()) {
      int end = Math.min(iDisplayStart + iDisplayLength, notes.size());
      response.setAaData(notes.subList(iDisplayStart, end));
    } else {
      response.setAaData(notes);
    }

    return response;
  }

  @GetMapping("/{entityType}/{entityId}")
  @ResponseBody
  public List<NoteDto> getNotes(
      @PathVariable String entityType,
      @PathVariable long entityId,
      @RequestParam(value = "includeRelated", defaultValue = "false") boolean includeRelated) throws IOException {

    return getNotesList(entityType, entityId, includeRelated);
  }

  @PostMapping("/{entityType}/{entityId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public NoteDto createNote(
      @PathVariable String entityType,
      @PathVariable long entityId,
      @RequestBody NoteRequest request) throws IOException {

    NoteService<? extends Identifiable> service = serviceForEntityType(entityType);
    Identifiable entity = service.get(entityId);

    if (entity == null) {
      throw new RestException(entityType + " not found", Status.NOT_FOUND);
    }

    Note note = new Note();
    note.setText(request.getText());
    note.setInternalOnly(request.isInternalOnly());
    note.setCreationDate(LocalDate.now(ZoneId.systemDefault()));

    @SuppressWarnings("unchecked")
    NoteService<Identifiable> typedService = (NoteService<Identifiable>) service;
    typedService.addNote(entity, note);

    return NoteDto.from(note, (HierarchyEntity) entity);
  }

  @DeleteMapping("/{entityType}/{entityId}/{noteIds}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteNotes(
      @PathVariable String entityType,
      @PathVariable long entityId,
      @PathVariable String noteIds) throws IOException {

    NoteService<? extends Identifiable> service = serviceForEntityType(entityType);
    Identifiable entity = service.get(entityId);

    if (entity == null) {
      throw new RestException(entityType + " not found", Status.NOT_FOUND);
    }

    List<Long> noteIdList = Arrays.stream(noteIds.split(","))
        .map(Long::parseLong)
        .collect(Collectors.toList());

    @SuppressWarnings("unchecked")
    NoteService<Identifiable> typedService = (NoteService<Identifiable>) service;

    for (Long noteId : noteIdList) {
      typedService.deleteNote(entity, noteId);
    }
  }

  public static class BulkDeleteRequest {
    private String entityType;
    private long entityId;
    private List<Long> ids;

    public String getEntityType() {
      return entityType;
    }

    public void setEntityType(String entityType) {
      this.entityType = entityType;
    }

    public long getEntityId() {
      return entityId;
    }

    public void setEntityId(long entityId) {
      this.entityId = entityId;
    }

    public List<Long> getIds() {
      return ids;
    }

    public void setIds(List<Long> ids) {
      this.ids = ids;
    }
  }

  @PostMapping("/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDeleteNotes(@RequestBody BulkDeleteRequest request) throws IOException {
    NoteService<? extends Identifiable> service = serviceForEntityType(request.getEntityType());
    Identifiable entity = service.get(request.getEntityId());

    if (entity == null) {
      throw new RestException(request.getEntityType() + " not found", Status.NOT_FOUND);
    }

    @SuppressWarnings("unchecked")
    NoteService<Identifiable> typedService = (NoteService<Identifiable>) service;

    for (Long noteId : request.getIds()) {
      typedService.deleteNote(entity, noteId);
    }
  }


  private List<NoteDto> getNotesList(String entityType, long entityId, boolean includeRelated) throws IOException {
    NoteService<? extends Identifiable> service = serviceForEntityType(entityType);
    Identifiable entity = service.get(entityId);

    if (entity == null) {
      throw new RestException(entityType + " not found", Status.NOT_FOUND);
    }

    List<NoteDto> noteDtos = new ArrayList<>();

    @SuppressWarnings("unchecked")
    NoteService<Identifiable> typedService = (NoteService<Identifiable>) service;
    List<Note> notes = typedService.getNotes(entity);

    for (Note note : notes) {
      noteDtos.add(NoteDto.from(note, (HierarchyEntity) entity));
    }

    if (includeRelated) {
      if (entityType.equalsIgnoreCase("library") && entity instanceof Library) {
        Library library = (Library) entity;
        Sample sample = library.getSample();

        if (sample != null) {
          addSampleNotes(sample, noteDtos, "sample");

          if (sample instanceof DetailedSample) {
            addParentSampleNotes((DetailedSample) sample, noteDtos);
          }
        }
      } else if (entityType.equalsIgnoreCase("sample") && entity instanceof DetailedSample) {
        addParentSampleNotes((DetailedSample) entity, noteDtos);
      }
    }

    return noteDtos;
  }

  private void addSampleNotes(Sample sample, List<NoteDto> noteDtos, String source) throws IOException {
    List<Note> sampleNotes = sampleService.getNotes(sample);

    for (Note note : sampleNotes) {
      NoteDto dto = NoteDto.from(note, sample);
      dto.setSource(source);
      noteDtos.add(dto);
    }
  }

  private void addParentSampleNotes(DetailedSample sample, List<NoteDto> noteDtos) throws IOException {
    Sample currentParent = sample.getParent();

    while (currentParent != null) {
      addSampleNotes(currentParent, noteDtos, "parent");

      if (currentParent instanceof DetailedSample) {
        currentParent = ((DetailedSample) currentParent).getParent();
      } else {
        currentParent = null;
      }
    }
  }

  private NoteService<? extends Identifiable> serviceForEntityType(String entityType) {
    switch (entityType) {
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
