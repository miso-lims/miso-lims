package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import uk.ac.bbsrc.tgac.miso.core.data.Aliasable;
import uk.ac.bbsrc.tgac.miso.core.data.Deletable;

@Entity
public class Pipeline implements Aliasable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long pipelineId = UNSAVED_ID;

  private String alias;

  @Override
  public long getId() {
    return pipelineId;
  }

  @Override
  public void setId(long id) {
    this.pipelineId = id;
  }

  @Override
  public boolean isSaved() {
    return pipelineId != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Pipeline";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

}
