package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.util.List;

import org.hibernate.query.ResultListTransformer;
import org.hibernate.query.TupleTransformer;

public class EntityReference {

  public static final TupleTransformer<EntityReference> TUPLE_TRANSFORMER = new TupleTransformer<EntityReference>() {
    @Override
    public EntityReference transformTuple(Object[] tuple, String[] aliases) {
      return new EntityReference((long) tuple[0], (String) tuple[1]);
    }
  };

  public static final ResultListTransformer<EntityReference> RESULT_LIST_TRANSFORMER =
      new ResultListTransformer<EntityReference>() {
        @Override
        public List<EntityReference> transformList(List<EntityReference> collection) {
          return collection;
        }
      };

  private final long id;
  private final String label;

  public EntityReference(long id, String label) {
    this.id = id;
    this.label = label;
  }

  public long getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

}
