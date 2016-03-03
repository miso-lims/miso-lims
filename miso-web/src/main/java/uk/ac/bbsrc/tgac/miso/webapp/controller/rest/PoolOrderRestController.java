package uk.ac.bbsrc.tgac.miso.webapp.controller.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import uk.ac.bbsrc.tgac.miso.core.data.PoolOrder;
import uk.ac.bbsrc.tgac.miso.core.data.SequencingParameters;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderDto;
import uk.ac.bbsrc.tgac.miso.dto.SequencingParametersDto;
import uk.ac.bbsrc.tgac.miso.service.PoolOrderService;
import uk.ac.bbsrc.tgac.miso.service.SequencingParametersService;

@Controller
@RequestMapping("/rest")
@SessionAttributes("poolorder")
public class PoolOrderRestController extends RestController {

  protected static final Logger log = LoggerFactory.getLogger(PoolOrderRestController.class);

  @Autowired
  private PoolOrderService poolOrderService;
  @Autowired
  private SequencingParametersService sequencingParametersService;

  @RequestMapping(value = "/pool/{id}/orders", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public Set<PoolOrderDto> getOrdersByPool(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    Set<PoolOrderDto> dtos = Dtos.asPoolOrderDtos(poolOrderService.getByPool(id));
    for (PoolOrderDto dto : dtos) {
      writeUrls(dto, uriBuilder);
    }
    return dtos;
  }

  @RequestMapping(value = "/poolorder/{id}", method = RequestMethod.GET, produces = { "application/json" })
  @ResponseBody
  public PoolOrderDto getPoolOrder(@PathVariable("id") Long id, UriComponentsBuilder uriBuilder, HttpServletResponse response)
      throws IOException {
    PoolOrder result = poolOrderService.get(id);
    if (result == null) {
      throw new RestException("No pool order found with ID: " + id, Status.NOT_FOUND);
    } else {
      return writeUrls(Dtos.asDto(result), uriBuilder);
    }
  }

  @RequestMapping(value = "/poolorder", method = RequestMethod.POST, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> createPoolOrder(@RequestBody PoolOrderDto poolOrderDto, UriComponentsBuilder b, HttpServletResponse response)
      throws IOException {
    Long id = poolOrderService.create(poolOrderDto);
    UriComponents uriComponents = b.path("/poolorder/{id}").buildAndExpand(id);
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uriComponents.toUri());
    return new ResponseEntity<>(headers, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/poolorder/{id}", method = RequestMethod.PUT, headers = { "Content-type=application/json" })
  @ResponseBody
  public ResponseEntity<?> updatePoolOrder(@PathVariable("id") Long id, @RequestBody PoolOrderDto poolOrderDto,
      HttpServletResponse response) throws IOException {
    PoolOrder poolOrder = poolOrderService.get(id);
    if (poolOrder == null) {
      throw new RestException("No pool order found with ID: " + id, Status.NOT_FOUND);
    }
    poolOrder.setPartitions(poolOrderDto.getPartitions());
    SequencingParameters parameters = sequencingParametersService.get(poolOrderDto.getParameters().getId());
    if (parameters == null) {
      throw new RestException("No sequencing parameters found with ID: " + poolOrderDto.getParameters(), Status.BAD_REQUEST);
    }
    poolOrder.setSequencingParameter(parameters);
    poolOrderService.update(poolOrder);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/poolorder/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public ResponseEntity<?> deletePoolOrder(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
    poolOrderService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  private static PoolOrderDto writeUrls(PoolOrderDto poolOrderDto, UriComponentsBuilder uriBuilder) {
    URI baseUri = uriBuilder.build().toUri();
    poolOrderDto
        .setUrl(UriComponentsBuilder.fromUri(baseUri).path("/rest/poolorder/{id}").buildAndExpand(poolOrderDto.getId()).toUriString());
    poolOrderDto.setCreatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(poolOrderDto.getCreatedById()).toUriString());
    poolOrderDto.setUpdatedByUrl(
        UriComponentsBuilder.fromUri(baseUri).path("/rest/user/{id}").buildAndExpand(poolOrderDto.getUpdatedById()).toUriString());
    return poolOrderDto;
  }

}
