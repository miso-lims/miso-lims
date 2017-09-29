package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.StudyDto;
import uk.ac.bbsrc.tgac.miso.service.ChangeLogService;
import uk.ac.bbsrc.tgac.miso.service.StudyService;

@Controller
@RequestMapping("/rest/study")
public class StudyController {
  @Autowired
  private ChangeLogService changeLogService;
  @Autowired
  private StudyService studyService;

  @RequestMapping(value = "/{studyId}", method = RequestMethod.GET)
  public @ResponseBody StudyDto get(@PathVariable Long studyId) throws IOException {
    return Dtos.asDto(studyService.get(studyId));
  }

  @RequestMapping(value = "/changes", method = RequestMethod.GET)
  public @ResponseBody Collection<ChangeLog> getChanges() throws IOException {
    return changeLogService.listAll("Study");
  }

}
