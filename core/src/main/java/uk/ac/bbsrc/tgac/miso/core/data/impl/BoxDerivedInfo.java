package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;

@Entity
@Immutable
@Table(name = "BoxDerivedInfo")
@Synchronize("Box")
public class BoxDerivedInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long boxId;
  
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  public Long getId() {
    return boxId;
  }

  public Date getLastModified() {
    return lastModified;
  }
}
