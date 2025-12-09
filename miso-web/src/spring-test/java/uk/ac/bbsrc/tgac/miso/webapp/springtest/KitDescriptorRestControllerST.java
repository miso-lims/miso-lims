package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import com.jayway.jsonpath.JsonPath;
import static org.hamcrest.Matchers.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.TargetedSequencing;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.KitDescriptorDto;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.KitDescriptorRestController.KitChangeTargetedSequencingRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.KitDescriptorRestController;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import java.util.Arrays;
import java.util.Set;


public class KitDescriptorRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/kitdescriptors";
  private static final Class<KitDescriptor> entityClass = KitDescriptor.class;

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2, 3, 4, 5));
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
    testListAll(CONTROLLER_BASE, Arrays.asList(1, 2, 3, 4, 5));
  }

  @Test
  public void testDatatableByType() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/type/CLUSTERING", Arrays.asList(3));

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testChangeTargetedSequencing() throws Exception {
    // must be admin to change targeted sequencing
    KitDescriptorRestController.KitChangeTargetedSequencingRequest req =
        new KitDescriptorRestController.KitChangeTargetedSequencingRequest();
    req.setRemove(Arrays.asList(1L, 2L));
    req.setAdd(Arrays.asList(3L));

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/targetedsequencing").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    KitDescriptor changed = currentSession().get(entityClass, 1);
    Set<TargetedSequencing> seq = changed.getTargetedSequencing();
    assertTrue(seq.size() == 1);
    assertTrue(seq.iterator().next().getId() == 3);
  }

  @Test
  public void testChangeTargetedSequencingUnauthorized() throws Exception {
    KitDescriptorRestController.KitChangeTargetedSequencingRequest req =
        new KitDescriptorRestController.KitChangeTargetedSequencingRequest();
    req.setRemove(Arrays.asList(1L, 2L));
    req.setAdd(Arrays.asList(3L));

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/targetedsequencing").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
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
    KitDescriptor newKitDesc = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 201);
    assertEquals("kit desc", newKitDesc.getName());
    assertEquals(false, newKitDesc.isArchived());
    assertEquals(KitType.LIBRARY, newKitDesc.getKitType());
    assertEquals("me", newKitDesc.getManufacturer());
    assertEquals("kit desc", newKitDesc.getName());
    assertEquals("1453", newKitDesc.getPartNumber());
    assertEquals(PlatformType.ILLUMINA, newKitDesc.getPlatformType());
    assertEquals(2, newKitDesc.getStockLevel().intValue());
  }

  @Test
  public void testCreateFail() throws Exception {
    // must be admin to create
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), entityClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be admin to update

    KitDescriptorDto kitdesc = Dtos.asDto(currentSession().get(entityClass, 1));
    kitdesc.setName("updated");
    KitDescriptor updated = baseTestUpdate(CONTROLLER_BASE, kitdesc, 1, entityClass);
    assertEquals("updated", updated.getName());
  }

  @Test
  public void testUpdateFail() throws Exception {
    // must be admin to update

    KitDescriptorDto kitdesc = Dtos.asDto(currentSession().get(entityClass, 1));
    kitdesc.setName("updated");
    testUpdateUnauthorized(CONTROLLER_BASE, kitdesc, 1, entityClass);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // must be admin to delete instrument
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }
}
