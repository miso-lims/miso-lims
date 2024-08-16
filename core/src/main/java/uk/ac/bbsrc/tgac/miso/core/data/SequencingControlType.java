package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

@Entity
public class SequencingControlType implements Aliasable, Deletable, Serializable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long sequencingControlTypeId = UNSAVED_ID;

  private String alias;

  @Override
  public long getId() {
    return sequencingControlTypeId;
  }

  @Override
  public void setId(long id) {
    this.sequencingControlTypeId = id;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Sequencing Control Type";
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

  @Override
  public int hashCode() {
    return Objects.hash(sequencingControlTypeId, alias);
  }

  @Override
  public boolean equals(Object obj) {
    return LimsUtils.equals(this, obj,
        SequencingControlType::getId,
        SequencingControlType::getAlias);
  }

}
