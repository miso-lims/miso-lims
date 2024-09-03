package uk.ac.bbsrc.tgac.miso.core.data.impl.view;

import java.util.List;

public class BarcodableReference {

  public static class TupleTransformer implements org.hibernate.query.TupleTransformer<BarcodableReference> {
    private final String entityType;

    public TupleTransformer(String entityType) {
      this.entityType = entityType;
    }

    @Override
    public BarcodableReference transformTuple(Object[] tuple, String[] aliases) {
      String secondaryLabel = tuple.length < 3 ? null : (String) tuple[2];
      return new BarcodableReference(entityType, (long) tuple[0], (String) tuple[1], secondaryLabel);
    }

  };

  public static class ResultListTransformer implements org.hibernate.query.ResultListTransformer<BarcodableReference> {
    @Override
    public List<BarcodableReference> transformList(List<BarcodableReference> collection) {
      return collection;
    }
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
