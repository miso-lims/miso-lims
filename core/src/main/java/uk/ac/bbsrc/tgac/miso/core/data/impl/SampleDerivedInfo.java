package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

@Entity
@Immutable
@Subselect("SELECT sampleId, MAX(changeTime) as lastModified FROM SampleChangeLog GROUP BY sampleId")
@Synchronize("Sample")
public class SampleDerivedInfo {

  @Id
  private Long sampleId;
  private Date lastModified;

  public Long getId() {
    return sampleId;
  }

  public Date getLastModified() {
    return lastModified;
  }
}
