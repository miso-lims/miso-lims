package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.core.data.impl.workset.WorksetStage;

import java.util.Arrays;
import java.util.List;
import com.jayway.jsonpath.JsonPath;
import static org.junit.Assert.assertEquals;

public class WorkStageControllerST extends AbstractST {

    private static final String CONTROLLER_BASE = "/worksetstage";
    private static final Class<WorksetStage> entityClass = WorksetStage.class;

    private void assertDbVsModelObjects(String resultJson, List<Long> ids) {
        assertEquals(Integer.valueOf(ids.size()), JsonPath.read(resultJson, "$.length()"));

        for (int i = 0; i < ids.size(); i++) {
            WorksetStage db = currentSession().get(entityClass, ids.get(i));
            assertEquals(db.getId(), readLong(resultJson, "$[" + i + "].id"));
            assertEquals(db.getAlias(), JsonPath.read(resultJson, "$[" + i + "].alias"));
        }
    }

    @Test
    public void testList() throws Exception {
        List<Long> ids = Arrays.asList(4L, 1L, 2L, 3L);
        String resultJson = testStaticListPage(CONTROLLER_BASE + "/list", "data");
        System.out.println("------>"+resultJson);
        assertDbVsModelObjects(resultJson, ids);
    }

    @Test
    public void testBulkCreate() throws Exception {
        testBulkCreatePage(CONTROLLER_BASE + "/bulk/new", 3, "input");
    }

    @Test
    public void testBulkEdit() throws Exception {
        List<Long> ids = Arrays.asList(1L, 2L);
        String resultJson = testBulkEditPage(CONTROLLER_BASE + "/bulk/edit", ids, "input");
        assertDbVsModelObjects(resultJson, ids);
    }
}