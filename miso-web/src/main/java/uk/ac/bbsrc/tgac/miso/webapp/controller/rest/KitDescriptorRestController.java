package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.service.KitDescriptorService;
import uk.ac.bbsrc.tgac.miso.core.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.KitDescriptorDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.ConstantsController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;
import uk.ac.bbsrc.tgac.miso.webapp.controller.component.AdvancedSearchParser;

@Controller
@RequestMapping("/rest/kitdescriptors")
public class KitDescriptorRestController extends AbstractRestController {

  @Autowired
  private KitDescriptorService kitDescriptorService;
  @Autowired
  private TargetedSequencingService targetedSequencingService;

  @Autowired
  private ConstantsController constantsController;

  @Autowired
  private AdvancedSearchParser advancedSearchParser;

  private final JQueryDataTableBackend<KitDescriptor, KitDescriptorDto> jQueryBackend =
      new JQueryDataTableBackend<KitDescriptor, KitDescriptorDto>() {

        @Override
        protected KitDescriptorDto asDto(KitDescriptor model) {
          return Dtos.asDto(model);
        }

        @Override
        protected PaginatedDataSource<KitDescriptor> getSource() throws IOException {
          return kitDescriptorService;
        }
      };

  public static class KitChangeTargetedSequencingRequest {
    private List<Long> add;
    private List<Long> remove;

    public List<Long> getAdd() {
      return add;
    }

    public List<Long> getRemove() {
      return remove;
    }

    public void setAdd(List<Long> add) {
      this.add = add;
    }

    public void setRemove(List<Long> remove) {
      this.remove = remove;
    }
  }

  public void setKitService(KitDescriptorService kitService) {
    this.kitDescriptorService = kitService;
  }

  @GetMapping(value = "/{id}", produces = {"application/json"})
  @ResponseBody
  public KitDescriptorDto getKitDescriptor(@PathVariable long id) throws IOException {
    return RestUtils.getObject("Kit descriptor", id, kitDescriptorService, Dtos::asDto);
  }

  @GetMapping(produces = {"application/json"})
  @ResponseBody
  public Set<KitDescriptorDto> getKitDescriptors() throws IOException {
    Collection<KitDescriptor> kitDescriptors = kitDescriptorService.list();
    Set<KitDescriptorDto> dtos = Dtos.asKitDescriptorDtos(kitDescriptors);
    return dtos;
  }

  @PostMapping(headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.CREATED)
  public @ResponseBody KitDescriptorDto createKitDescriptor(@RequestBody KitDescriptorDto kitDescriptorDto)
      throws IOException {
    return RestUtils.createObject("Kit descriptor", kitDescriptorDto, Dtos::to, kitDescriptorService, kd -> {
      constantsController.refreshConstants();
      return Dtos.asDto(kd);
    });
  }

  @PutMapping(value = "/{id}", headers = {"Content-type=application/json"})
  @ResponseStatus(HttpStatus.OK)
  public @ResponseBody KitDescriptorDto updateKitDescriptor(@PathVariable long id,
      @RequestBody KitDescriptorDto kitDescriptorDto)
      throws IOException {
    return RestUtils.updateObject("Kit descriptor", id, kitDescriptorDto, Dtos::to, kitDescriptorService, kd -> {
      constantsController.refreshConstants();
      return Dtos.asDto(kd);
    });
  }

  @GetMapping(value = "/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<KitDescriptorDto> dataTable(HttpServletRequest request) throws IOException {
    return jQueryBackend.get(request, advancedSearchParser);
  }

  @GetMapping(value = "/dt/type/{type}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<KitDescriptorDto> dataTableByType(@PathVariable("type") String type,
      HttpServletRequest request)
      throws IOException {
    KitType kitType = KitType.valueOf(type);
    if (kitType == null) {
      throw new RestException("Invalid kit type.");
    }
    return jQueryBackend.get(request, advancedSearchParser, PaginationFilter.kitType(kitType));
  }

  @PutMapping(value = "/{id}/targetedsequencing", produces = "application/json")
  public @ResponseBody KitDescriptorDto changeTargetedSequencings(@PathVariable("id") Long id,
      @RequestBody KitChangeTargetedSequencingRequest request) throws IOException {
    KitDescriptor kitDescriptor = kitDescriptorService.get(id);
    // remove first
    for (Long idToRemove : request.remove) {
      TargetedSequencing toRemove = targetedSequencingService.get(idToRemove);
      kitDescriptor.removeTargetedSequencing(toRemove);
    }
    // then add
    for (Long idToAdd : request.add) {
      TargetedSequencing toAdd = targetedSequencingService.get(idToAdd);
      kitDescriptor.addTargetedSequencing(toAdd);
    }
    kitDescriptorService.saveTargetedSequencingRelationships(kitDescriptor);
    return Dtos.asDto(kitDescriptorService.get(id));
  }

  @GetMapping("/search")
  public @ResponseBody List<KitDescriptorDto> search(@RequestParam("q") String search,
      @RequestParam("kitType") String kitTypeString)
      throws IOException {
    KitType kitType = KitType.get(kitTypeString);
    if (kitType == null) {
      throw new RestException("Invalid kit type", Status.BAD_REQUEST);
    }
    return kitDescriptorService.search(kitType, search).stream().map(Dtos::asDto).collect(Collectors.toList());
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Kit Descriptor", ids, kitDescriptorService);
  }
}
