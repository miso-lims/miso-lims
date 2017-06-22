package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Institute;
import uk.ac.bbsrc.tgac.miso.core.data.Lab;

@Entity
@Table(name = "Lab")
public class LabImpl implements Lab {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "labId")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(targetEntity = InstituteImpl.class)
  @JoinColumn(name = "instituteId", nullable = false)
  private Institute institute;

  @Column(nullable = false)
  private String alias;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public Institute getInstitute() {
    return institute;
  }

  @Override
  public void setInstitute(Institute institute) {
    this.institute = institute;
  }

  @Override
  public User getCreatedBy() {
    return this.createdBy;
  }

  @Override
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public Date getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public User getUpdatedBy() {
    return updatedBy;
  }

  @Override
  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  @Override
  public Date getLastUpdated() {
    return lastUpdated;
  }

  @Override
  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  /**
   * Get custom label for dropdown options
   */
  public String getItemLabel() {
    String label = getAlias() + " (" + getInstitute().getAlias() + ")";
    return (label.length() < 51 ? label : label.substring(0, 49) + "\u2026");
  }

}
