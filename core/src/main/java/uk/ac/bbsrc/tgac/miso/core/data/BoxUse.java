package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BoxUse")
public class BoxUse implements Serializable {

  private static final long serialVersionUID = 1L;

  private String alias;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "boxUseId")
  private long id;

  public String getAlias() {
    return alias;
  }

  public long getId() {
    return id;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setId(long id) {
    this.id = id;
  }
}
