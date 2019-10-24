package uk.ac.bbsrc.tgac.miso.core.data;

public interface SampleValidRelationship extends Identifiable, Timestamped {

  SampleClass getParent();

  void setParent(SampleClass parent);

  SampleClass getChild();

  void setChild(SampleClass child);

  boolean isArchived();

  void setArchived(boolean archived);

}