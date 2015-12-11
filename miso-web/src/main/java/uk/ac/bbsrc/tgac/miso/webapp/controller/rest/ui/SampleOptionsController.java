package uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.dto.SampleOptionsDto;
import uk.ac.bbsrc.tgac.miso.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.service.SampleGroupService;
import uk.ac.bbsrc.tgac.miso.service.SamplePurposeService;
import uk.ac.bbsrc.tgac.miso.service.SubprojectService;
import uk.ac.bbsrc.tgac.miso.service.TissueMaterialService;
import uk.ac.bbsrc.tgac.miso.service.TissueOriginService;
import uk.ac.bbsrc.tgac.miso.service.TissueTypeService;

@Controller
@RequestMapping("/rest/ui")
public class SampleOptionsController {

  protected static final Logger log = LoggerFactory.getLogger(SampleOptionsController.class);
  
  @Autowired
  private SubprojectService subprojectService;
  @Autowired
  private TissueOriginService tissueOriginService;
  @Autowired
  private TissueTypeService tissueTypeService;
  @Autowired
  private SampleClassService sampleClassService;
  @Autowired
  private SamplePurposeService samplePurposeService;
  @Autowired
  private SampleGroupService sampleGroupService;
  @Autowired
  private TissueMaterialService tissueMaterialService;
  
  @RequestMapping(value = "/sampleoptions", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public ResponseEntity<SampleOptionsDto> getSampleOptions(UriComponentsBuilder uriBuilder) {
    SampleOptionsDto sampleOptionsDto = new SampleOptionsDto();
    sampleOptionsDto.setSubprojects(subprojectService.getAll());
    sampleOptionsDto.setTissueOrigins(tissueOriginService.getAll());
    sampleOptionsDto.setTissueTypes(tissueTypeService.getAll());
    sampleOptionsDto.setSampleClasses(sampleClassService.getAll());
    sampleOptionsDto.setSamplePurposes(samplePurposeService.getAll());
    sampleOptionsDto.setSampleGroups(sampleGroupService.getAll());
    sampleOptionsDto.setTissueMaterials(tissueMaterialService.getAll());
   
    return new ResponseEntity<>(sampleOptionsDto, HttpStatus.OK);
  }
}
