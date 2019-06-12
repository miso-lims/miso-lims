package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LibrarySpikeIn")
public class LibrarySpikeIn implements Serializable, Aliasable, Deletable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

  @Id
  @Column(name = "spikeInId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id = UNSAVED_ID;

  private String alias;

  @Override
  public long getId() {
    return id;
  }

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  @Override
  public String getDeleteType() {
    return "Library Spike-In";
  }

  @Override
  public String getDeleteDescription() {
    return getAlias();
  }

}
