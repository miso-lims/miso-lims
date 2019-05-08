package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.ReferenceGenomeDto;
import uk.ac.bbsrc.tgac.miso.service.ReferenceGenomeService;

@Controller
@RequestMapping("/rest/referencegenomes")
public class ReferenceGenomeRestController extends RestController {

  @Autowired
  private ReferenceGenomeService referenceGenomeService;

  @GetMapping(produces = { "application/json" })
  @ResponseBody
  public List<ReferenceGenomeDto> getReferenceGenomeOptions(UriComponentsBuilder uriComponentsBuilder, HttpServletResponse response)
      throws IOException {
    Collection<ReferenceGenome> referenceGenomes = referenceGenomeService.listAllReferenceGenomeTypes();
    return referenceGenomes.stream().map(Dtos::asDto).collect(Collectors.toList());
  }

}
