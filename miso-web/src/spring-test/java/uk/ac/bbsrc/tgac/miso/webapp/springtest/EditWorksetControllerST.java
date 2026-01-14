package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;

public class EditWorksetControllerST extends AbstractST {

    public static final String CONTROLLER_BASE = "/workset";

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testNewWorkset() throws Exception {
        baseTestNewModel(CONTROLLER_BASE + "/new", "New Workset");
    }

    @Test
    @WithMockUser(username = "user", roles = {"INTERNAL"})
    public void testSetupForm() throws Exception {
        Workset workset = currentSession().get(Workset.class, 1L);

        getMockMvc().perform(get(CONTROLLER_BASE + "/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("title", "Workset 1"))
                .andExpect(model().attributeExists("workset"))
                .andExpect(model().attributeExists("worksetJson"));
    }
}