package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;


import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.*;

public class AttachmentRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/attachments";

  @Test
  public void testLinkFile() throws Exception {
    Sample from = currentSession().find(SampleImpl.class, 1);
    assertEquals(1, from.getAttachments().size());
    FileAttachment file = from.getAttachments().get(0);
    assertEquals(1, file.getId());

    Sample to = currentSession().find(SampleImpl.class, 2);
    assertFalse(to.getAttachments().stream().anyMatch(x -> x.getId() == 1));


    // this is linking an attachment from one entity to another (possibly different entity)
    getMockMvc().perform(post(CONTROLLER_BASE + "/sample/2")
        .param("fromEntityType", "sample")
        .param("fromEntityId", "1")
        .param("attachmentId", "1"))
        .andExpect(status().isNoContent());

    Sample saved = currentSession().find(SampleImpl.class, 2); // need to refetch sample 2 to see the update
    assertTrue(saved.getAttachments().stream().anyMatch(x -> x.getId() == 1));
  }

  @Test
  public void testBulkLinkFile() throws Exception {
    Sample sam2 = currentSession().find(SampleImpl.class, 2);
    Sample sam3 = currentSession().find(SampleImpl.class, 3);


    assertFalse(sam2.getAttachments().stream().anyMatch(x -> x.getId() == 1));
    assertFalse(sam3.getAttachments().stream().anyMatch(x -> x.getId() == 1));


    getMockMvc().perform(post(CONTROLLER_BASE + "/sample/shared")
        .param("fromEntityType", "sample")
        .param("fromEntityId", "1")
        .param("attachmentId", "1")
        .param("entityIds", "2,3"))
        .andExpect(status().isNoContent());

    // need to refetch samples 2 and 3 to see the updates
    Sample savedtwo = currentSession().find(SampleImpl.class, 2);
    Sample savedthree = currentSession().find(SampleImpl.class, 3);


    assertTrue(savedtwo.getAttachments().stream().anyMatch(x -> x.getId() == 1));
    assertTrue(savedthree.getAttachments().stream().anyMatch(x -> x.getId() == 1));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteAttachment() throws Exception {
    assertTrue(currentSession().find(SampleImpl.class, 1).getAttachments().stream().anyMatch(x -> x.getId() == 1));
    getMockMvc().perform(delete(CONTROLLER_BASE + "/sample/1/1"))
        .andExpect(status().isNoContent());
    assertFalse(currentSession().find(SampleImpl.class, 1).getAttachments().stream().anyMatch(x -> x.getId() == 1));
  }

  @Test
  public void testDeleteAttachmentFail() throws Exception {
    getMockMvc().perform(delete(CONTROLLER_BASE + "/sample/1/1"))
        .andExpect(status().isUnauthorized());
  }
}
