package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.util.List;

import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

public class BarcodableReference {

  public static class ResultTransformer implements org.hibernate.transform.ResultTransformer {

    private static final long serialVersionUID = 1L;

    private final String entityType;

    public ResultTransformer(String entityType) {
      this.entityType = entityType;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
      String secondaryLabel = tuple.length < 3 ? null : (String) tuple[2];
      return new BarcodableReference(entityType, (long) tuple[0], (String) tuple[1], secondaryLabel);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List transformList(List collection) {
      return collection;
    }

  };

  public static ProjectionList makeProjectionList(String primaryLabelProperty, String secondaryLabelProperty) {
    ProjectionList projections = Projections.projectionList()
        .add(Projections.property("id"))
        .add(Projections.property(primaryLabelProperty));
    if (secondaryLabelProperty != null) {
      projections.add(Projections.property(secondaryLabelProperty));
    }
    return projections;
  }

  private final String entityType;
  private final long id;
  private final String primaryLabel;
  private final String secondaryLabel;

  private BarcodableReference(String entityType, long id, String primaryLabel, String secondaryLabel) {
    this.entityType = entityType;
    this.id = id;
    this.primaryLabel = primaryLabel;
    this.secondaryLabel = secondaryLabel;
  }

  public String getEntityType() {
    return entityType;
  }

  public long getId() {
    return id;
  }

  public String getPrimaryLabel() {
    return primaryLabel;
  }

  public String getSecondaryLabel() {
    return secondaryLabel;
  }

  public String getFullLabel() {
    if (secondaryLabel == null) {
      return primaryLabel;
    } else {
      return primaryLabel + " (" + secondaryLabel + ")";
    }
  }

}
