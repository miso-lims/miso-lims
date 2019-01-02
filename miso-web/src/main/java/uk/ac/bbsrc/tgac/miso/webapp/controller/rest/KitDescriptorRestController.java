package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.KitDescriptorDto;
import uk.ac.bbsrc.tgac.miso.service.KitService;
import uk.ac.bbsrc.tgac.miso.service.TargetedSequencingService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.MenuController;

@Controller
@RequestMapping("/rest")
public class KitDescriptorRestController extends RestController {

  @Autowired
  private KitService kitService;
  @Autowired
  private TargetedSequencingService targetedSequencingService;
  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private MenuController menuController;

  private final JQueryDataTableBackend<KitDescriptor, KitDescriptorDto> jQueryBackend = new JQueryDataTableBackend<KitDescriptor, KitDescriptorDto>() {

    @Override
    protected KitDescriptorDto asDto(KitDescriptor model) {
      return Dtos.asDto(model);
    }

    @Override
    protected PaginatedDataSource<KitDescriptor> getSource() throws IOException {
      return kitService;
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

  public void setKitService(KitService kitService) {
    this.kitService = kitService;
  }

  private static KitDescriptorDto writeUrls(KitDescriptorDto kitDescriptorDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    kitDescriptorDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/kitdescriptor/{id}")
        .buildAndExpand(kitDescriptorDto.getId()).toUriString());
    return kitDescriptorDto;
  }

  @GetMapping(value = "/kitdescriptor/{id}", produces = { "application/json" })
  @ResponseBody
  public KitDescriptorDto getKitDescriptor(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) throws IOException {
    KitDescriptor kd = kitService.getKitDescriptorById(id);
    if (kd == null) {
      throw new RestException("No kit descriptor found with ID: " + id, Status.NOT_FOUND);
    } else {
      KitDescriptorDto dto = Dtos.asDto(kd);
      writeUrls(dto, uriBuilder);
      return dto;
    }
  }

  @GetMapping(value = "/kitdescriptors", produces = { "application/json" })
  @ResponseBody
  public Set<KitDescriptorDto> getKitDescriptors(UriComponentsBuilder uriBuilder) throws IOException {
    Collection<KitDescriptor> kitDescriptors = kitService.listKitDescriptors();
    Set<KitDescriptorDto> dtos = Dtos.asKitDescriptorDtos(kitDescriptors);
    for (KitDescriptorDto dto : dtos) {
      writeUrls(dto, uriBuilder);
    }
    return dtos;
  }

  @PostMapping(value = "/kitdescriptor", headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createKitDescriptor(@RequestBody KitDescriptorDto kitDescriptorDto) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    KitDescriptor kd = Dtos.to(kitDescriptorDto);
    kd.setId(KitDescriptor.UNSAVED_ID);
    kd.setLastModifier(user);
    kitService.saveKitDescriptor(kd);
    menuController.refreshConstants();
  }

  @PutMapping(value = "/kitdescriptor/{id}", headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public void updateKitDescriptor(@PathVariable("id") Long id, @RequestBody KitDescriptorDto kitDescriptorDto) throws IOException {
    KitDescriptor kd = Dtos.to(kitDescriptorDto);
    kd.setId(id);
    kitService.saveKitDescriptor(kd);
    menuController.refreshConstants();
  }

  @GetMapping(value = "/kitdescriptor/dt", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<KitDescriptorDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @GetMapping(value = "/kitdescriptor/dt/type/{type}", produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<KitDescriptorDto> dataTableByType(@PathVariable("type") String type, HttpServletRequest request,
      HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    KitType kitType = KitType.valueOf(type);
    if (kitType == null) {
      throw new RestException("Invalid kit type.");
    }
    return jQueryBackend.get(request, response, uriBuilder, PaginationFilter.kitType(kitType));
  }

  @PutMapping(value = "/kitdescriptor/{id}/targetedsequencing", produces = "application/json")
  public @ResponseBody KitDescriptorDto changeTargetedSequencings(@PathVariable("id") Long id,
      @RequestBody KitChangeTargetedSequencingRequest request) throws IOException {
    KitDescriptor kitDescriptor = kitService.getKitDescriptorById(id);
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
    kitService.saveKitDescriptor(kitDescriptor);
    return Dtos.asDto(kitService.getKitDescriptorById(id));
  }
}
