package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.PartitionQCType;
import uk.ac.bbsrc.tgac.miso.dto.PartitionQCTypeDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class PartitionQcTypeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/partitionqctypes";
  private static final Class<PartitionQCType> controllerClass = PartitionQCType.class;

  private List<PartitionQCTypeDto> makeCreateDtos() {

    List<PartitionQCTypeDto> dtos = new ArrayList<PartitionQCTypeDto>();
    PartitionQCTypeDto one = new PartitionQCTypeDto();
    one.setAnalysisSkipped(false);
    one.setNoteRequired(false);
    one.setOrderFulfilled(false);
    one.setDescription("one");


    PartitionQCTypeDto two = new PartitionQCTypeDto();
    two.setDescription("two");
    two.setNoteRequired(false);
    two.setOrderFulfilled(false);
    two.setAnalysisSkipped(false);

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<PartitionQCType> qcTypes = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals(qcTypes.get(0).getDescription(), "one");
    assertEquals(qcTypes.get(1).getDescription(), "two");
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // PartitionQcType creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these PartitionQCTypes so only admin can update them
    PartitionQCTypeDto pqt1 = Dtos.asDto(currentSession().get(PartitionQCType.class, 1));
    PartitionQCTypeDto pqt2 = Dtos.asDto(currentSession().get(PartitionQCType.class, 2));

    pqt1.setDescription("pqt1");
    pqt2.setDescription("pqt2");

    List<PartitionQCTypeDto> dtos = new ArrayList<PartitionQCTypeDto>();
    dtos.add(pqt1);
    dtos.add(pqt2);

    List<PartitionQCType> partitionQcTypes =
        (List<PartitionQCType>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, Arrays.asList(1, 2));
    assertEquals("pqt1", partitionQcTypes.get(0).getDescription());
    assertEquals("pqt2", partitionQcTypes.get(1).getDescription());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // the admin user made these PartitionQcTypes so only admin can update them
    PartitionQCTypeDto pqt1 = Dtos.asDto(currentSession().get(PartitionQCType.class, 1));
    PartitionQCTypeDto pqt2 = Dtos.asDto(currentSession().get(PartitionQCType.class, 2));

    pqt1.setDescription("pqt1");
    pqt2.setDescription("pqt2");

    List<PartitionQCTypeDto> dtos = new ArrayList<PartitionQCTypeDto>();
    dtos.add(pqt1);
    dtos.add(pqt2);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeletePartitionQCType() throws Exception {
    testBulkDelete(controllerClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 4, CONTROLLER_BASE);
  }
}
