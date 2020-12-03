package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

import java.io.Serializable;
import java.util.List;

public interface QcNode extends Serializable {

  public Long getId();

  public default Long[] getIds() {
    return null;
  }

  public QcNodeType getEntityType();

  public String getTypeLabel();

  public String getName();

  public String getLabel();

  public Boolean getQcPassed();

  public Long getQcStatusId();

  public String getQcNote();

  public List<? extends QcNode> getChildren();

}
