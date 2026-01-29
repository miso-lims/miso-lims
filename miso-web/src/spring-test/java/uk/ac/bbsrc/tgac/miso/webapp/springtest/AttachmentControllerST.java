package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;

public class AttachmentControllerST extends AbstractST {

        private static final String CONTROLLER_BASE = "/attachments";
        private static final long PROJECT_1 = 1L;
        private static final long PROJECT_2 = 2L;
        private static final long CATEGORY_SUBMISSION_FORMS = 1L;

        @Test
        public void testUploadSuccess() throws Exception {
                ProjectImpl before = currentSession().get(ProjectImpl.class, PROJECT_1);
                assertNotNull(before);
                int originalCount = before.getAttachments().size();

                MockMultipartFile file =
                                new MockMultipartFile("files", "test.txt", "text/plain", "test content".getBytes());

                getMockMvc()
                                .perform(multipart(CONTROLLER_BASE + "/project/" + PROJECT_1)
                                                .file(file)
                                                .param("categoryId", Long.toString(CATEGORY_SUBMISSION_FORMS)))
                                .andExpect(status().isNoContent());

                currentSession().clear();
                ProjectImpl after = currentSession().get(ProjectImpl.class, PROJECT_1);
                assertNotNull(after);
                assertEquals(originalCount + 1, after.getAttachments().size());

                FileAttachment attachment = after.getAttachments().stream()
                                .max((a, b) -> Long.compare(a.getId(), b.getId()))
                                .orElseThrow(() -> new AssertionError("Expected an attachment but found none"));

                assertNotNull(attachment.getId());
                assertEquals("test.txt", attachment.getFilename());
                assertNotNull(attachment.getCategory());
                assertEquals(CATEGORY_SUBMISSION_FORMS, attachment.getCategory().getId());

                // Download: assert behavior, not formatting
                MockHttpServletResponse response = getMockMvc()
                                .perform(get(CONTROLLER_BASE + "/project/" + PROJECT_1 + "/" + attachment.getId()))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Content-Disposition", containsString("test.txt")))
                                .andReturn()
                                .getResponse();

                assertTrue(response.getContentAsByteArray().length > 0);
        }

        @Test
        public void testSharedUploadSuccess() throws Exception {
                ProjectImpl before1 = currentSession().get(ProjectImpl.class, PROJECT_1);
                ProjectImpl before2 = currentSession().get(ProjectImpl.class, PROJECT_2);
                assertNotNull(before1);
                assertNotNull(before2);

                int count1 = before1.getAttachments().size();
                int count2 = before2.getAttachments().size();

                MockMultipartFile file =
                                new MockMultipartFile("files", "shared.txt", "text/plain", "shared".getBytes());

                getMockMvc()
                                .perform(multipart(CONTROLLER_BASE + "/project/shared")
                                                .file(file)
                                                .param("categoryId", Long.toString(CATEGORY_SUBMISSION_FORMS))
                                                .param("entityIds", PROJECT_1 + "," + PROJECT_2))
                                .andExpect(status().isNoContent());

                currentSession().clear();
                ProjectImpl after1 = currentSession().get(ProjectImpl.class, PROJECT_1);
                ProjectImpl after2 = currentSession().get(ProjectImpl.class, PROJECT_2);
                assertNotNull(after1);
                assertNotNull(after2);

                assertEquals(count1 + 1, after1.getAttachments().size());
                assertEquals(count2 + 1, after2.getAttachments().size());

                long sharedId = after1.getAttachments().stream()
                                .max((a, b) -> Long.compare(a.getId(), b.getId()))
                                .orElseThrow(() -> new AssertionError("Expected an attachment but found none"))
                                .getId();

                boolean linked =
                                after2.getAttachments().stream().anyMatch(a -> a.getId() == sharedId);
                assertTrue("Expected shared attachment to be linked to both projects", linked);
        }

        @Test
        public void testUploadMissingEntityReturnsNotFound() throws Exception {
                MockMultipartFile file =
                                new MockMultipartFile("files", "test.txt", "text/plain", "x".getBytes());

                getMockMvc()
                                .perform(multipart(CONTROLLER_BASE + "/project/999999")
                                                .file(file)
                                                .param("categoryId", Long.toString(CATEGORY_SUBMISSION_FORMS)))
                                .andExpect(status().isNotFound());
        }

        @Test
        public void testUploadInvalidCategoryReturnsBadRequest() throws Exception {
                MockMultipartFile file =
                                new MockMultipartFile("files", "test.txt", "text/plain", "x".getBytes());

                getMockMvc()
                                .perform(multipart(CONTROLLER_BASE + "/project/" + PROJECT_1)
                                                .file(file)
                                                .param("categoryId", "999999"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void testDownloadNonExistentAttachmentReturnsNotFound() throws Exception {
                getMockMvc()
                                .perform(get(CONTROLLER_BASE + "/project/" + PROJECT_1 + "/999999"))
                                .andExpect(status().isNotFound());
        }
}
