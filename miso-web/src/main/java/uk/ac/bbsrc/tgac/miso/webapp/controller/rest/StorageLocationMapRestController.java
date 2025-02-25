package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import uk.ac.bbsrc.tgac.miso.core.service.StorageLocationMapService;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.controller.AbstractRestController;

@Controller
@RequestMapping("/rest/locationmaps")
public class StorageLocationMapRestController extends AbstractRestController {

  @Autowired
  private StorageLocationMapService mapService;

  @PostMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void upload(@RequestParam("file") MultipartFile file,
      @RequestParam(name = "description", required = false) String description)
      throws IOException {
    String saveDesc = LimsUtils.isStringEmptyOrNull(description) ? null : description;
    mapService.create(file, saveDesc);
  }

  @PostMapping(value = "/bulk-delete")
  @ResponseBody
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void bulkDelete(@RequestBody(required = true) List<Long> ids) throws IOException {
    RestUtils.bulkDelete("Location Map", ids, mapService);
  }

}
