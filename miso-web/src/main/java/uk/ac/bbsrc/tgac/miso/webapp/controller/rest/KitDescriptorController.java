package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.KitDescriptorDto;

@Controller
@RequestMapping("/rest")
public class KitDescriptorController extends RestController {
  
  @Autowired
  private RequestManager requestManager;
  
  private static KitDescriptorDto writeUrls(KitDescriptorDto kitDescriptorDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    kitDescriptorDto.setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/kitdescriptor/{id}")
        .buildAndExpand(kitDescriptorDto.getId()).toUriString());
    return kitDescriptorDto;
  }
  
  @RequestMapping(value = "/kitdescriptor/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public KitDescriptorDto getKitDescriptor(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder) throws IOException {
    KitDescriptor kd = requestManager.getKitDescriptorById(id);
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
    Collection<KitDescriptor> kitDescriptors = requestManager.listAllKitDescriptors();
    if (kitDescriptors.isEmpty()) {
      throw new RestException("No kit descriptors found", Status.NOT_FOUND);
    } else {
      Set<KitDescriptorDto> dtos = Dtos.asKitDescriptorDtos(kitDescriptors);
      for (KitDescriptorDto dto : dtos) {
        writeUrls(dto, uriBuilder);
      }
      return dtos;
    }
  }
  
  @RequestMapping(value = "/kitdescriptor", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createKitDescriptor(@RequestBody KitDescriptorDto kitDescriptorDto, UriComponentsBuilder uriBuilder) throws IOException {
    KitDescriptor kd = Dtos.to(kitDescriptorDto);
    kd.setKitDescriptorId(KitDescriptor.UNSAVED_ID);
    requestManager.saveKitDescriptor(kd);
  }
  
  @RequestMapping(value = "/kitdescriptor/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  @ResponseStatus(HttpStatus.OK)
  public void updateKitDescriptor(@PathVariable("id") Long id, @RequestBody KitDescriptorDto kitDescriptorDto, 
      UriComponentsBuilder uriBuilder) throws IOException {
    KitDescriptor kd = Dtos.to(kitDescriptorDto);
    kd.setKitDescriptorId(id);
    requestManager.saveKitDescriptor(kd);
  }

}
