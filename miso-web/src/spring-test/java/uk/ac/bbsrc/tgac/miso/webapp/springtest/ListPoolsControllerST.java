package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.webapp.controller.view.ListPoolsController;
import uk.ac.bbsrc.tgac.miso.core.service.PoolService;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.beans.factory.annotation.Autowired;

public class ListPoolsControllerST extends AbstractST {

    @Autowired
    private PoolService poolService;

    @Autowired
    private ListPoolsController controller;


    @Test
    @WithMockUser(username = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testGetPoolsPage() throws Exception {
        PoolImpl pool = currentSession().get(PoolImpl.class, 1L);
        assertNotNull(pool);
        assertNotNull(pool.getPlatformType());

        getMockMvc()
                .perform(get("/pools"))
                .andExpect(status().isOk())
                .andExpect(view().name("/WEB-INF/pages/listTabbed.jsp"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attribute("title", "Pools"))
                .andExpect(model().attribute("tabs", hasKey(pool.getPlatformType().getKey())));
    }
}