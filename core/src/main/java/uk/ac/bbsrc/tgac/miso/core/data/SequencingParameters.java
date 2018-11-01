package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Comparator;
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

public class SequencingParameters implements Serializable, Comparable<SequencingParameters>
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

  public static Comparator<SequencingParameters> nameComparator = (sp1, sp2) -> sp1.compareTo(sp2);

  @Override
  public int compareTo(SequencingParameters other) {
    // v4 2x126, v4, Rapid Run, 10X, Custom, v3, other
    if (!(other instanceof SequencingParameters)) return -1;
    if (name.startsWith("v4")) {
      if ("v4 2×126".equals(name)) return -1;
      if ("v4 2×126".equals(other.name)) return 1;
      if (other.name.startsWith("v4")) return other.name.compareTo(name);
      return -1; // v4 > everything else
    } else if (name.startsWith("Rapid Run")) {
      if (other.name.startsWith("v4")) return 1;
      if (other.name.startsWith("Rapid Run")) return other.name.compareTo(name);
      return -1; // Rapid Run > everything else
    } else if (name.startsWith("10X")) {
      if (other.name.startsWith("v4") || other.name.startsWith("Rapid Run")) return 1;
      if (other.name.startsWith("10X")) return other.name.compareTo(name);
      return -1; // 10X > everything else
    } else if (name.startsWith("v3")) {
      if (other.name.startsWith("v4") || other.name.startsWith("Rapid Run") || other.name.startsWith("10X")) return 1;
      if (other.name.startsWith("v3")) return other.name.compareTo(name);
      return -1; // v3 > everything else
    } else {
      return other.name.compareTo(name);
    }
  }

}
