package uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ui;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.dto.SampleOptionsDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.InstituteController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.KitDescriptorController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.LabController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.QcPassedDetailController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.SampleClassController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.SampleGroupController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.SamplePurposeController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.SampleValidRelationshipController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.SubprojectController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.TissueMaterialController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.TissueOriginController;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.TissueTypeController;

@Controller
@RequestMapping("/rest/ui")
public class SampleOptionsController {

  protected static final Logger log = LoggerFactory.getLogger(SampleOptionsController.class);

  @Autowired
  private SubprojectController subprojectController;
  @Autowired
  private TissueOriginController tissueOriginController;
  @Autowired
  private TissueTypeController tissueTypeController;
  @Autowired
  private SampleClassController sampleClassController;
  @Autowired
  private SamplePurposeController samplePurposeController;
  @Autowired
  private SampleGroupController sampleGroupController;
  @Autowired
  private TissueMaterialController tissueMaterialController;
  @Autowired
  private QcPassedDetailController qcPassedDetailController;
  @Autowired
  private SampleValidRelationshipController sampleValidRelationshipController;
  @Autowired
  private LabController labController;
  @Autowired
  private InstituteController instituteController;
  @Autowired
  private KitDescriptorController kitDescriptorController;

  @RequestMapping(value = "/sampleoptions", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public SampleOptionsDto getSampleOptions(UriComponentsBuilder uriBuilder, HttpServletResponse response) throws IOException {
    SampleOptionsDto sampleOptionsDto = new SampleOptionsDto();
    sampleOptionsDto.setSubprojectsDtos(subprojectController.getSubprojects(uriBuilder, response));
    sampleOptionsDto.setTissueOriginsDtos(tissueOriginController.getTissueOrigins(uriBuilder, response));
    sampleOptionsDto.setTissueTypesDtos(tissueTypeController.getTissueTypes(uriBuilder, response));
    sampleOptionsDto.setSampleClassesDtos(sampleClassController.getSampleClasses(uriBuilder, response));
    sampleOptionsDto.setSamplePurposesDtos(samplePurposeController.getSamplePurposes(uriBuilder, response));
    sampleOptionsDto.setSampleGroupsDtos(sampleGroupController.getSampleGroups(uriBuilder, response));
    sampleOptionsDto.setTissueMaterialsDtos(tissueMaterialController.getTissueMaterials(uriBuilder, response));
    sampleOptionsDto.setQcPassedDetailsDtos(qcPassedDetailController.getQcPassedDetails(uriBuilder, response));
    sampleOptionsDto.setSampleValidRelationshipsDtos(sampleValidRelationshipController.getSampleValidRelationships(uriBuilder, response));
    sampleOptionsDto.setInstitutesDtos(instituteController.getInstitutes(uriBuilder));
    sampleOptionsDto.setLabsDtos(labController.getLabs(uriBuilder));
    sampleOptionsDto.setKitDescriptorsDtos(kitDescriptorController.getKitDescriptors(uriBuilder));

    return sampleOptionsDto;
  }
}
