package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayModelControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/arraymodel";
  private static final Class<ArrayModel> entityClass = ArrayModel.class;

  private List<HashMap<String, Object>> makeObjectMaps(List<Integer> ids) {
    List<HashMap<String, Object>> mappedProperties = new ArrayList<HashMap<String, Object>>();

    for (int i = 0; i < ids.size(); i++) {
      MapBuilder<String, Object> currMap = new MapBuilder<String, Object>();
      ArrayModel arrayModel = currentSession().get(entityClass, ids.get(i));
      currMap.put("id", ids.get(i)).put("alias", arrayModel.getAlias());
      mappedProperties.add((HashMap) currMap.build()); // safe cast since MapBuilder using HashMap
    }
    return mappedProperties;
  }

  @Test
  public void testList() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);

    testModelList(CONTROLLER_BASE + "/list", "data", makeObjectMaps(ids));
  }

  @Test
  public void testBulkCreate() throws Exception {
    testModelBulkCreate(CONTROLLER_BASE + "/bulk/new", 3, "input");
  }

  @Test
  public void testBulkEdit() throws Exception {
    List<Integer> ids = Arrays.asList(1, 2);


    testModelBulkEdit(CONTROLLER_BASE + "/bulk/edit", idListString(ids), "input", makeObjectMaps(ids));
  }
}
