package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.util.List;

import org.hibernate.query.ResultListTransformer;
import org.hibernate.query.TupleTransformer;

public class EntityReference {

  @SuppressWarnings("rawtypes")
  public static final TupleTransformer TUPLE_TRANSFORMER = new TupleTransformer() {
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
      return new EntityReference((long) tuple[0], (String) tuple[1]);
    }
  };

  @SuppressWarnings("rawtypes")
  public static final ResultListTransformer RESULT_LIST_TRANSFORMER = new ResultListTransformer() {
    @Override
    public List transformList(List collection) {
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
