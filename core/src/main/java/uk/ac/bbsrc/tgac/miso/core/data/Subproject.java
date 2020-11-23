package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

public interface Subproject extends Serializable, Aliasable, Deletable, Timestamped {

  public void setAlias(String alias);

  public String getDescription();

  public void setDescription(String description);

  public Project getParentProject();

  public void setParentProject(Project parentProject);

  public Boolean getPriority();

  public void setPriority(Boolean priority);

  public void setReferenceGenome(ReferenceGenome referenceGenome);

  public ReferenceGenome getReferenceGenome();

}