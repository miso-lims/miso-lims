package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import uk.ac.bbsrc.tgac.miso.dto.NoteDto;

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
  @Autowired
  private RequisitionService requisitionService;
  @Autowired
  private WorksetService worksetService;

  @GetMapping("/{entityType}/{entityId}/dt")
  @ResponseBody
  public Map<String, Object> getNotesForDataTable(
      @PathVariable String entityType,
      @PathVariable long entityId,
      @RequestParam(value = "draw", defaultValue = "1") int draw,
      @RequestParam(value = "start", defaultValue = "0") int start,
      @RequestParam(value = "length", defaultValue = "10") int length,
      @RequestParam(value = "includeRelated", defaultValue = "true") boolean includeRelated) throws IOException {

    List<NoteDto> notes = getNotesList(entityType, entityId, includeRelated);

    Map<String, Object> response = new HashMap<>();
    response.put("draw", draw);
    response.put("recordsTotal", notes.size());
    response.put("recordsFiltered", notes.size());

    if (length > 0 && start < notes.size()) {
      int end = Math.min(start + length, notes.size());
      response.put("data", notes.subList(start, end));
    } else {
      response.put("data", notes);
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

    return toDto(note, entity);
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
      noteDtos.add(toDto(note, entity));
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
      NoteDto dto = toDto(note, sample);
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

  private NoteDto toDto(Note note, Identifiable entity) {
    NoteDto dto = new NoteDto();
    dto.setId(Long.valueOf(note.getId()));
    dto.setText(note.getText());
    dto.setInternalOnly(note.isInternalOnly());
    dto.setCreationDate(note.getCreationDate().toString());
    dto.setOwnerName(note.getOwner() != null ? note.getOwner().getFullName() : "Unknown");
    dto.setEntityId(Long.valueOf(entity.getId()));
    dto.setEntityType(entity.getClass().getSimpleName());

    if (entity instanceof Sample) {
      Sample sample = (Sample) entity;
      dto.setEntityName(sample.getName());
      dto.setEntityAlias(sample.getAlias());
    } else if (entity instanceof Library) {
      Library library = (Library) entity;
      dto.setEntityName(library.getName());
      dto.setEntityAlias(library.getAlias());
    } else {
      dto.setEntityName("Unknown");
      dto.setEntityAlias("");
    }

    return dto;
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
