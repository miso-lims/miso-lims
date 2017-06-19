package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;
import uk.ac.bbsrc.tgac.miso.core.util.PaginationFilter;
import uk.ac.bbsrc.tgac.miso.dto.DataTablesResponseDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.KitDescriptorDto;
import uk.ac.bbsrc.tgac.miso.service.KitService;

@Controller
@RequestMapping("/rest")
public class KitDescriptorRestController extends RestController {

  @Autowired
  private KitService kitService;

  @Autowired
  private SecurityManager securityManager;
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

  public void setKitService(KitService kitService) {
    this.kitService = kitService;
  }

  private static KitDescriptorDto writeUrls(KitDescriptorDto kitDescriptorDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    kitDescriptorDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/kitdescriptor/{id}")
        .buildAndExpand(kitDescriptorDto.getId()).toUriString());
    return kitDescriptorDto;
  }

  @RequestMapping(value = "/kitdescriptor/{id}", method = RequestMethod.GET, produces = { "application/json" })
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

  @RequestMapping(value = "/kitdescriptors", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<KitDescriptorDto> getKitDescriptors(UriComponentsBuilder uriBuilder) throws IOException {
    Collection<KitDescriptor> kitDescriptors = kitService.listKitDescriptors();
    Set<KitDescriptorDto> dtos = Dtos.asKitDescriptorDtos(kitDescriptors);
    for (KitDescriptorDto dto : dtos) {
      writeUrls(dto, uriBuilder);
    }
    return dtos;
  }

  @RequestMapping(value = "/kitdescriptor", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createKitDescriptor(@RequestBody KitDescriptorDto kitDescriptorDto, UriComponentsBuilder uriBuilder) throws IOException {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    KitDescriptor kd = Dtos.to(kitDescriptorDto);
    kd.setId(KitDescriptor.UNSAVED_ID);
    kd.setLastModifier(user);
    kitService.saveKitDescriptor(kd);
  }

  @RequestMapping(value = "/kitdescriptor/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public void updateKitDescriptor(@PathVariable("id") Long id, @RequestBody KitDescriptorDto kitDescriptorDto,
      UriComponentsBuilder uriBuilder) throws IOException {
    KitDescriptor kd = Dtos.to(kitDescriptorDto);
    kd.setId(id);
    kitService.saveKitDescriptor(kd);
  }

  @RequestMapping(value = "/kitdescriptor/dt", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public DataTablesResponseDto<KitDescriptorDto> dataTable(HttpServletRequest request, HttpServletResponse response,
      UriComponentsBuilder uriBuilder) throws IOException {
    return jQueryBackend.get(request, response, uriBuilder);
  }

  @RequestMapping(value = "/kitdescriptor/dt/type/{type}", method = RequestMethod.GET, produces = "application/json")
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

}
