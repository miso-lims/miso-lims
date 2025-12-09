package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class AttachmentControllerST extends AbstractST {

        private static final String CONTROLLER_BASE = "/attachments";

        @Test
        public void testAcceptUpload() throws Exception {

                String entityType = "sample";
                long entityId = 1L;

                MockMultipartFile file = new MockMultipartFile(
                                "files",
                                "test-document.pdf",
                                "application/pdf",
                                "Test file content".getBytes());

                getMockMvc()
                                .perform(MockMvcRequestBuilders
                                                .multipart(CONTROLLER_BASE + "/" + entityType + "/" + entityId)
                                                .file(file))
                                .andExpect(status().isNoContent());
        }

        @Test
        public void testAcceptSharedUpload() throws Exception {

                String entityType = "sample";
                String entityIds = "1,2";

                MockMultipartFile file = new MockMultipartFile(
                                "files",
                                "shared-document.pdf",
                                "application/pdf",
                                "Shared file content".getBytes());

                getMockMvc()
                                .perform(MockMvcRequestBuilders
                                                .multipart(CONTROLLER_BASE + "/" + entityType + "/shared")
                                                .file(file)
                                                .param("entityIds", entityIds))
                                .andExpect(status().isNoContent());
        }

        @Test
        public void testDownloadAttachmentNotFound() throws Exception {

                String entityType = "sample";
                long entityId = 1L;
                long fileId = 999999L;

                getMockMvc()
                                .perform(get(CONTROLLER_BASE + "/" + entityType + "/" + entityId + "/" + fileId))
                                .andExpect(status().isNotFound());
        }
}
