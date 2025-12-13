package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

public class ListPoolsControllerST extends AbstractST {

    @Test
    @WithMockUser(username = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testGetPoolsPage() throws Exception {
        getMockMvc()
                .perform(get("/pools"))
                .andExpect(status().isOk())
                .andExpect(view().name("/WEB-INF/pages/listTabbed.jsp"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("title", "Pools"));
    }

}