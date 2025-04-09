package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.eaglegenomics.simlims.core.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@Entity
@Table(name = "SequencingParameters")

public class SequencingParameters
    implements Deletable, Serializable, Comparable<SequencingParameters>, Timestamped {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0;

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
  @Column
  private String runType;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long parametersId = UNSAVED_ID;

  @ManyToOne(targetEntity = InstrumentModel.class)
  @JoinColumn(name = "instrumentModelId")
  private InstrumentModel instrumentModel;
  @Column(nullable = false)
  private int readLength;
  @Column(nullable = false)
  private int readLength2;

  @ManyToOne(targetEntity = UserImpl.class, fetch = FetchType.LAZY)
  @JoinColumn(name = "updatedBy", nullable = false)
  private User updatedBy;

  @Column
  private Integer movieTime;

  public IlluminaChemistry getChemistry() {
    return chemistry;
  }

  @Override
  public User getCreator() {
    return createdBy;
  }

  @Override
  public Date getCreationTime() {
    return creationDate;
  }

  @Override
  public long getId() {
    return parametersId;
  }

  @Override
  public Date getLastModified() {
    return lastUpdated;
  }

  public String getName() {
    return name;
  }

  public InstrumentModel getInstrumentModel() {
    return instrumentModel;
  }


  public int getReadLength() {
    return readLength;
  }

  @Override
  public User getLastModifier() {
    return updatedBy;
  }

  public void setChemistry(IlluminaChemistry chemistry) {
    this.chemistry = chemistry;
  }

  @Override
  public void setCreator(User createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public void setCreationTime(Date creation) {
    this.creationDate = creation;
  }

  @Override
  public void setId(long id) {
    this.parametersId = id;
  }

  @Override
  public void setLastModified(Date lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setInstrumentModel(InstrumentModel instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  public void setReadLength(int readLength) {
    this.readLength = readLength;
  }

  @Override
  public void setLastModifier(User updatedBy) {
    this.updatedBy = updatedBy;
  }

  private static final List<String> sortOrder = getSortOrder();

  private static List<String> getSortOrder() {
    List<String> order = new ArrayList<>();
    order.add("v4 2×126");
    order.add("v4");
    order.add("Rapid Run");
    order.add("10X");
    order.add("v3");
    order.add("High"); // NextSeq
    order.add("Mid"); // NextSeq
    order.add("Nano"); // MiSeq
    order.add("Micro"); // MiSeq
    order.add("Custom"); // All
    order.add("Sequencing"); // Oxford Nanopore
    order.add("Configuration"); // Oxford Nanopore
    order.add("Control"); // Oxford Nanopore
    order.add("Platform"); // Oxford Nanopore
    return Collections.unmodifiableList(order);
  }

  @Override
  public int compareTo(SequencingParameters other) {
    Integer thisSortKey = null;
    for (int i = 0; i < sortOrder.size(); i++) {
      if (name.startsWith(sortOrder.get(i))) {
        thisSortKey = i;
        break;
      }
    }
    if (thisSortKey == null)
      thisSortKey = 100;

    Integer otherSortKey = null;
    for (int i = 0; i < sortOrder.size(); i++) {
      if (other.name.startsWith(sortOrder.get(i))) {
        otherSortKey = i;
        break;
      }
    }
    if (otherSortKey == null)
      otherSortKey = 100;

    if (thisSortKey == otherSortKey) {
      return name.compareTo(other.name);
    } else {
      return thisSortKey - otherSortKey;
    }
  }

  @Override
  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }

  public String getRunType() {
    return runType;
  }

  public void setRunType(String runType) {
    this.runType = runType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((chemistry == null) ? 0 : chemistry.hashCode());
    result = prime * result + ((instrumentModel == null) ? 0 : instrumentModel.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + readLength;
    result = prime * result + readLength2;
    result = prime * result + ((runType == null) ? 0 : runType.hashCode());
    result = prime * result + ((movieTime == null) ? 0 : movieTime.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SequencingParameters other = (SequencingParameters) obj;
    if (chemistry != other.chemistry)
      return false;
    if(movieTime != other.movieTime)
      return false;
    if (instrumentModel == null) {
      if (other.instrumentModel != null)
        return false;
    } else if (!instrumentModel.equals(other.instrumentModel))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (readLength != other.readLength)
      return false;
    if (readLength2 != other.readLength2)
      return false;
    if (runType == null) {
      if (other.runType != null)
        return false;
    } else if (!runType.equals(other.runType))
      return false;

    return true;
  }

  public int getReadLength2() {
    return readLength2;
  }

  public void setReadLength2(int readLength2) {
    this.readLength2 = readLength2;
  }

  @Override
  public String getDeleteType() {
    return "Sequencing Parameters";
  }

  @Override
  public String getDeleteDescription() {
    return getName() + " (" + getInstrumentModel().getAlias() + ")";
  }

  public Integer getMovieTime() { return movieTime; }

  public void setMovieTime(Integer movieTime) {
    this.movieTime = movieTime;
  }
}
