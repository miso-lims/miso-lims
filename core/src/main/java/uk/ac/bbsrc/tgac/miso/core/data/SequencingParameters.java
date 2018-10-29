package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
@Table(name = "SequencingParameters")

public class SequencingParameters implements Serializable
{

  private static final long serialVersionUID = 1L;

  @Enumerated(EnumType.STRING)
  private IlluminaChemistry chemistry;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "createdBy", nullable = false)
  private User createdBy;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date creationDate;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdated;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private boolean paired;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long parametersId;

  @ManyToOne(targetEntity = Platform.class)
  @JoinColumn(name = "platformId")
  private Platform platform;
  @Column(nullable = false)
  private int readLength;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  public IlluminaChemistry getChemistry() {
    return chemistry;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public Long getId() {
    return parametersId;
  }

  public Date getLastUpdated() {
    return lastUpdated;
  }

  public String getName() {
    return name;
  }

  public Platform getPlatform() {
    return platform;
  }


  public int getReadLength() {
    return readLength;
  }

  public User getUpdatedBy() {
    return updatedBy;
  }

  public boolean isPaired() {
    return paired;
  }

  public void setChemistry(IlluminaChemistry chemistry) {
    this.chemistry = chemistry;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreationDate(Date creation) {
    this.creationDate = creation;
  }

  public void setId(Long id) {
    this.parametersId = id;
  }

  public void setLastUpdated(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPaired(boolean paired) {
    this.paired = paired;
  }

  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  public void setReadLength(int readLength) {
    this.readLength = readLength;
  }

  public void setUpdatedBy(User updatedBy) {
    this.updatedBy = updatedBy;
  }

}
