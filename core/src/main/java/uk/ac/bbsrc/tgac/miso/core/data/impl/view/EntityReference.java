package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.util.List;

import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.ResultTransformer;

public class EntityReference {

  public static final ResultTransformer RESULT_TRANSFORMER = new ResultTransformer() {

    private static final long serialVersionUID = 1L;

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
      return new EntityReference((long) tuple[0], (String) tuple[1]);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
      return collection;
    }

  };

  public static ProjectionList makeProjectionList(String idProperty, String labelProperty) {
    return Projections.projectionList()
        .add(Projections.property(idProperty))
        .add(Projections.property(labelProperty));
  }

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
