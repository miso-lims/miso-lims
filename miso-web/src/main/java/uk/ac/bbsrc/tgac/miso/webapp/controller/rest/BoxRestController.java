package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView.BoxableId;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.BoxDto;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.service.BoxService;
import uk.ac.bbsrc.tgac.miso.service.LibraryDilutionService;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.spring.util.FormUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;

@Controller
@RequestMapping("/rest")
public class BoxRestController extends RestController {
  @Autowired
  private BoxService boxService;

  @Autowired
  private MisoFilesManager misoFileManager;

  @Autowired
  private SampleService sampleService;

  @Autowired
  private LibraryService libraryService;

  @Autowired
  private LibraryDilutionService libraryDilutionService;

  @Value("${miso.detailed.sample.enabled}")
  private Boolean detailedSampleEnabled;

  private final JQueryDataTableBackend<Box, BoxDto> jQueryBackend = new JQueryDataTableBackend<Box, BoxDto>() {
    @Override
    protected BoxDto asDto(Box model) {
      return Dtos.asDto(model, false);
    }

    @Override
    protected PaginatedDataSource<Box> getSource() throws IOException {
      return boxService;
    }
  };

  @RequestMapping(value = "/box/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<BoxDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/box/dt/use/{id}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<BoxDto> dataTableByUse(@PathVariable("id") Long id, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.boxUse(id));
  }

  @RequestMapping(value = "/boxes/rest/", method = RequestMethod.GET)
  public @ResponseBody Collection<Box> jsonRest() throws IOException {
    return boxService.list(0, 0, true, "id");
  }

  @RequestMapping(value = "/box/{boxId}/position/{position}", method = RequestMethod.PUT, consumes = { "application/json" },
      produces = { "application/json" })
  public @ResponseBody BoxDto setPosition(@PathVariable("boxId") Long boxId, @PathVariable("position") String position,
      @RequestParam("entity") String entity) throws IOException {
    BoxableId id = parseEntityIdentifier(entity);
    Box box = boxService.get(boxId);
    if (!box.isValidPosition(position)) {
      throw new RestException("Invalid position given: " + position, Status.BAD_REQUEST);
    }
    BoxableView boxable = boxService.getBoxableView(id);
    if (boxable == null) {
      throw new RestException(String.format("Item not found (type=%s, id=%s)", id.getTargetType(), id.getTargetId()),
          Status.BAD_REQUEST);
    } else if (boxable.isDiscarded()) {
      throw new RestException("Cannot add discarded item to box", Status.BAD_REQUEST);
    }
    // if the selected item is already in the box, remove it here and add it to the correct position in next step
    if (Long.valueOf(box.getId()).equals(boxable.getBoxId())) {
      box.removeBoxable(boxable.getBoxPosition());
    }
    // if an item already exists at this position, its location will be set to unknown.
    box.setBoxable(position, boxable);
    boxService.save(box);
    Box saved = boxService.get(boxId);
    return Dtos.asDto(saved, true);
  }

  @RequestMapping(value = "/boxes/search")
  public @ResponseBody List<BoxDto> search(@RequestParam("q") String search) {
    List<Box> results = boxService.getBySearch(search);
    return Dtos.asBoxDtos(results, true);
  }

  /**
   * Creates an Excel spreadsheet that contains the list of all Boxable items located in a particular position. Empty positions are not
   * listed.
   *
   * @param boxId ID of the Box
   * @return JSON object with "hashCode" field representing the hash code of the spreadsheet filename
   */
  @RequestMapping(value = "/box/{boxId}/spreadsheet", method = RequestMethod.GET)
  public @ResponseBody JSONObject createSpreadsheet(@PathVariable("boxId") Long boxId) {
    try {
      return exportBoxContentsForm(boxId);
    } catch (Exception e) {
      throw new RestException("Failed to get contents form", Status.BAD_REQUEST);
    }
  }

  private JSONObject exportBoxContentsForm(Long boxId) throws IOException  {
    Box box = boxService.get(boxId);

    List<List<String>> boxContents = getBoxContents(box);
    String name = box.getName();
    String alias = box.getAlias();

    File f = misoFileManager.getNewFile(Box.class, "forms", "BoxContentsForm-" + getCurrentDateAsString() + ".xlsx");
    if (detailedSampleEnabled) {
      FormUtils.createDetailedBoxSpreadsheet(f, name, alias, boxContents);
    } else {
      FormUtils.createPlainBoxSpreadsheet(f, name, alias, boxContents);
    }

    return JSONObject.fromObject("{hashCode: " + f.getName().hashCode() + "}");
  }

  /**
   * @param box
   * @return List of lists of strings, where each list of string represents a box
   */
  private List<List<String>> getBoxContents(Box box) throws IOException {
    List<List<String>> boxContents = new ArrayList<>();

    // Use TreeMap to iterate through the contents of the box in order of box position
    for (Map.Entry<String, BoxableView> entry : new TreeMap<>(box.getBoxables()).entrySet()) {
      String position = entry.getKey();
      BoxableView boxableView = entry.getValue();

      String name = boxableView.getName();
      String alias = boxableView.getAlias();
      String barcode = boxableView.getIdentificationBarcode();

      if (detailedSampleEnabled) {
        String externalName = findExternalName(boxableView);
        String numSlides = findNumSlides(boxableView);

        boxContents.add(Arrays.asList(position, name, alias, barcode, externalName, numSlides));
      } else {
        boxContents.add(Arrays.asList(position, name, alias, barcode));
      }
    }

    return boxContents;
  }

  private String findExternalName(BoxableView boxableView) throws IOException {
    DetailedSample detailedSample;

    switch(boxableView.getId().getTargetType()) {
    case SAMPLE:
      detailedSample = (DetailedSample) extractSample(boxableView);
      break;
    case LIBRARY:
      detailedSample = (DetailedSample) extractLibrary(boxableView).getSample();
      break;
    case DILUTION:
      detailedSample = (DetailedSample) extractDilution(boxableView).getLibrary().getSample();
      break;
    default: // Can't find external name for a Pool or otherwise
      return "n/a";
    }

    return extractSampleIdentity(detailedSample).getExternalName();
  }

  private SampleIdentity extractSampleIdentity(DetailedSample detailedSample) {
    while (!isIdentitySample(detailedSample)) {
      if (detailedSample == null) {
        throw new IllegalStateException("No identity found in hierarchy");
      }

      detailedSample = detailedSample.getParent();
    }

    // Load all fields from Hibernate object to enable cast to SampleIdentity
    return (SampleIdentity) deproxify(detailedSample);
  }

  private Sample extractSample(BoxableView boxableView) throws IOException {
    return sampleService.get(boxableView.getId().getTargetId());
  }

  private Library extractLibrary(BoxableView boxableView) throws IOException {
    return libraryService.get(boxableView.getId().getTargetId());
  }

  private LibraryDilution extractDilution(BoxableView boxableView) throws IOException {
    return libraryDilutionService.get(boxableView.getId().getTargetId());
  }

  private String findNumSlides(BoxableView boxableView) throws IOException {
    if (isSample(boxableView)) {
      DetailedSample sample = (DetailedSample) extractSample(boxableView);

      if (isSampleSlide(sample)) {
        return Integer.toString(((SampleSlide) sample).getSlides());
      }
    }

    return "n/a";
  }

  private boolean isSample(BoxableView boxableView) {
    return boxableView.getId().getTargetType() == EntityType.SAMPLE;
  }

  private static BoxableId parseEntityIdentifier(String identifier) {
    try {
      String[] pieces = identifier.split(":");
      EntityType et = EntityType.valueOf(pieces[0]);
      long id = Long.parseLong(pieces[1]);
      return new BoxableId(et, id);
    } catch (NullPointerException | IllegalArgumentException e) {
      throw new RestException("Invalid entity identifier: " + identifier, Status.BAD_REQUEST);
    }
  }

  public void setBoxService(BoxService boxService) {
    this.boxService = boxService;
  }

}
