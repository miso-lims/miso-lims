package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.ws.rs.core.Response.Status;
import uk.ac.bbsrc.tgac.ApiKeyDto;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;
import uk.ac.bbsrc.tgac.miso.core.service.ApiKeyService;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.RestException;

@RestController
@RequestMapping("/rest/apikeys")
public class ApiKeyRestController extends AbstractRestController {

  @Autowired
  private ApiKeyService apiKeyService;

  public record CreateApiKeyRequest(String name) {
  }

  @PostMapping
  public ApiKeyDto create(@RequestBody CreateApiKeyRequest request) throws IOException {
    String keyString = apiKeyService.generateApiKey(request.name());
    ApiKey apiKey = apiKeyService.authenticate(keyString);
    if (apiKey == null) {
      throw new RestException("Failed to generate a valid API Key");
    }
    ApiKeyDto dto = ApiKeyDto.from(apiKey);
    dto.setKey(keyString);
    return dto;
  }

  @PostMapping("/bulk-delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    List<ApiKey> items = new ArrayList<>();
    for (Long id : ids) {
      if (id == null) {
        throw new RestException("API key id cannot be null", Status.BAD_REQUEST);
      }
      ApiKey item = RestUtils.retrieve("API key", id, apiKeyService, Status.BAD_REQUEST);
      items.add(item);
    }
    apiKeyService.bulkDelete(items);
  }

}
