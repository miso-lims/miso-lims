package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;

import com.eaglegenomics.simlims.core.User;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Immutable
@Table(name = "SampleDerivedInfo")
@Synchronize("Sample")
public class SampleDerivedInfo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  private Long sampleId;

  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "creator", nullable = false)
  @JsonBackReference
  private User creator;

  public Long getId() {
    return sampleId;
  }

  public Date getLastModified() {
    return lastModified;
  }

  public Date getCreated() {
    return created;
  }

  public User getCreator() {
    return creator;
  }

}
