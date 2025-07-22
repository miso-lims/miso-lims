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

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.dto.AttachmentDto;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;


import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class AttachmentRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/attachments";

  @Test
  public void testLinkFile() throws Exception {
    Sample from = currentSession().get(SampleImpl.class, 1);
    assertEquals(1, from.getAttachments().size());
    FileAttachment file = from.getAttachments().get(0);
    assertEquals(1, file.getId());

    // assert that the attachment exists and is attached to Sample 1

    // this is linking an attachment from one entity to another (possibly different entity)
    getMockMvc().perform(post(CONTROLLER_BASE + "/sample/2")
        .param("fromEntityType", "sample")
        .param("fromEntityId", "1")
        .param("attachmentId", "1"))
        .andExpect(status().isNoContent());

    Sample saved = currentSession().get(SampleImpl.class, 2);
    assertTrue(saved.getAttachments().stream().anyMatch(x -> x.getId() == 1));
  }

  @Test
  public void testBulkLinkFile() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/sample/shared")
        .param("fromEntityType", "sample")
        .param("fromEntityId", "1")
        .param("attachmentId", "1")
        .param("entityIds", "2,3"))
        .andExpect(status().isNoContent());


    Sample savedtwo = currentSession().get(SampleImpl.class, 2);
    Sample savedthree = currentSession().get(SampleImpl.class, 3);


    assertTrue(savedtwo.getAttachments().stream().anyMatch(x -> x.getId() == 1));
    assertTrue(savedthree.getAttachments().stream().anyMatch(x -> x.getId() == 1));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteAttachment() throws Exception {

    getMockMvc().perform(delete(CONTROLLER_BASE + "/sample/1/1"))
        .andExpect(status().isNoContent());
    assertFalse(currentSession().get(SampleImpl.class, 1).getAttachments().stream().anyMatch(x -> x.getId() == 1));
  }

  @Test
  public void testDeleteAttachmentFail() throws Exception {
    getMockMvc().perform(delete(CONTROLLER_BASE + "/sample/1/1"))
        .andExpect(status().isUnauthorized());
  }
}
