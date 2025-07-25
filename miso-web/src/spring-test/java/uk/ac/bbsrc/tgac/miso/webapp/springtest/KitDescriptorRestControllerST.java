package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;


import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.dto.KitDescriptorDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.KitDescriptorRestController.KitChangeTargetedSequencingRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.KitDescriptorRestController;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;


import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;
import java.time.LocalDate;


public class KitDescriptorRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/kitdescriptors";
  private static final Class<KitDescriptor> controllerClass = KitDescriptor.class;

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1,2,3,4));
  }

  @Test
  public void testGetById() throws Exception {
    baseTestGetById(CONTROLLER_BASE, 1)
        .andExpect(jsonPath("$.manufacturer").value("TestCo"))
        .andExpect(jsonPath("$.stockLevel").value(0))
        .andExpect(jsonPath("$.kitType").value("Library"))
        .andExpect(jsonPath("$.platformType").value("Illumina"));
  }

  @Test
  public void testListAll() throws Exception {
    testListAll(CONTROLLER_BASE, Arrays.asList(1,2,3,4));
  }

  @Test
  public void testDatatableByType() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/type/CLUSTERING", Arrays.asList(3));

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testChangeTargetedSequencing() throws Exception {
    // must be admin to change targeted sequencing
    KitDescriptorRestController.KitChangeTargetedSequencingRequest req = new KitDescriptorRestController.KitChangeTargetedSequencingRequest();
    req.setRemove(Arrays.asList(1L,2L));
    req.setAdd(Arrays.asList(3L));

    getMockMvc().perform(put(CONTROLLER_BASE + "/1/targetedsequencing").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isOk());

    KitDescriptor changed = currentSession().get(controllerClass, 1);
    Set<TargetedSequencing> seq = changed.getTargetedSequencing();
    assertTrue(seq.size() == 1);
    assertTrue(seq.iterator().next().getId() == 3);
  }

  @Test
  public void testChangeTargetedSequencingUnauthorized() throws Exception {
    KitDescriptorRestController.KitChangeTargetedSequencingRequest req = new KitDescriptorRestController.KitChangeTargetedSequencingRequest();
    req.setRemove(Arrays.asList(1L,2L));
    req.setAdd(Arrays.asList(3L));

    getMockMvc().perform(put(CONTROLLER_BASE + "/1/targetedsequencing").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
    .andExpect(status().isUnauthorized());
  }


  @Test
  public void testSearch() throws Exception {
    MultiValueMap params = new LinkedMultiValueMap();
    params.add("q", "Test Kit Two");
    params.add("kitType", "Library");
    baseSearchByTerm(CONTROLLER_BASE + "/search", params, Arrays.asList(2));
  }

  private KitDescriptorDto makeCreateDto() throws Exception {
    KitDescriptorDto dto = new KitDescriptorDto();
    dto.setArchived(false);
    dto.setKitType("Library");
    dto.setManufacturer("me");
    dto.setName("kit desc");
    dto.setPartNumber("1453");
    dto.setPlatformType("Illumina");
    dto.setStockLevel(2);
    return dto;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be admin to create
    KitDescriptor newKitDesc = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), controllerClass, 201);
    assertEquals("kit desc", newKitDesc.getName());
  }

  @Test
  public void testCreateFail() throws Exception {
    // must be admin to create
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), controllerClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be admin to update

    KitDescriptor kitdesc = currentSession().get(controllerClass, 1);
    kitdesc.setName("updated");
    KitDescriptor updated = baseTestUpdate(CONTROLLER_BASE, Dtos.asDto(kitdesc), 1, controllerClass);
    assertEquals("updated", updated.getName());
  }

  @Test
  public void testUpdateFail() throws Exception {
    // must be admin to update

    KitDescriptor kitdesc = currentSession().get(controllerClass, 1);
    kitdesc.setName("updated");
    testUpdateUnauthorized(CONTROLLER_BASE, Dtos.asDto(kitdesc), 1, controllerClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // must be admin to delete instrument
    testBulkDelete(controllerClass, 4, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 4, CONTROLLER_BASE);
  }
}
