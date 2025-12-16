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
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

public class ListPoolsControllerST extends AbstractST {

    @Autowired
    private PoolService poolService;

    @Autowired
    private ListPoolsController controller;


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

    @Test
    @WithMockUser(username = "admin", roles = {"INTERNAL", "ADMIN"})
    public void testPoolsTabsContainsLoadedPools() throws Exception {
        Pool pool = poolService.getByAlias("POOL_1");
        assertNotNull(pool.getPlatformType());

        ModelMap model = new ModelMap();
        ModelAndView mav = controller.listPools(model);

        String platformName = pool.getPlatformType().name();
        String modelString = String.valueOf(mav.getModel());

        assertThat("Expected pools tab model to include platform type of a loaded pool",
                modelString,
                containsString(platformName));

    }

}