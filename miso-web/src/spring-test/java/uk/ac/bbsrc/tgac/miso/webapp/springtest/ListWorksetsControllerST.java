package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.hasKey;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.Workset;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetCategory;

public class ListWorksetsControllerST extends AbstractST {

    @Test
    public void testListWorksets() throws Exception {

        getMockMvc().perform(get("/worksets"))
                .andExpect(status().isOk())
                .andExpect(view().name("/WEB-INF/pages/listTabbed.jsp"))
                .andExpect(model().attribute("title", "Worksets"))
                .andExpect(model().attribute("targetType", "ListTarget.workset"))
                .andExpect(model().attribute("tabs", hasKey(Workset.ReservedWord.MINE.getText())))
                .andExpect(model().attribute("tabs", hasKey(Workset.ReservedWord.ALL.getText())))
                .andExpect(model().attribute("tabs", hasKey(Workset.ReservedWord.UNCATEGORIZED.getText())));


        WorksetCategory cat = currentSession().get(WorksetCategory.class, 1L);
        if(cat != null){
            getMockMvc().perform(get("/worksets"))
                    .andExpect(model().attribute("tabs", hasKey(cat.getAlias())));
        }
    }
}